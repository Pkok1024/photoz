package onlasdan.gallery.encryption.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides the SQLCipher passphrase for [onlasdan.gallery.model.database.PhotoZDatabase].
 * Uses a hardware-backed wrapping key (Android Keystore) to protect a random DB passphrase.
 */
@Singleton
class SqlCipherKeyProvider
	@Inject
	constructor(
		@ApplicationContext private val context: Context,
	) {
		private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
		private val encryptedKeyFile = File(context.filesDir, "sqlcipher_key.enc")

		fun getOrCreatePassphrase(): ByteArray {
			if (encryptedKeyFile.exists()) {
				return decryptPassphrase()
			}

			val passphrase = ByteArray(32).also { SecureRandom().nextBytes(it) }
			encryptAndSavePassphrase(passphrase)
			return passphrase
		}

		private fun encryptAndSavePassphrase(passphrase: ByteArray) {
			val wrappingKey = getOrCreateWrappingKey()
			val cipher = Cipher.getInstance(WRAPPING_ALGORITHM)
			cipher.init(Cipher.ENCRYPT_MODE, wrappingKey)
			val encrypted = cipher.doFinal(passphrase)
			val iv = cipher.iv

			// Store IV (12 bytes for GCM) + Encrypted data
			encryptedKeyFile.writeBytes(iv + encrypted)
		}

		private fun decryptPassphrase(): ByteArray {
			val wrappingKey = getOrCreateWrappingKey()
			val blob = encryptedKeyFile.readBytes()
			val iv = blob.sliceArray(0 until 12)
			val encrypted = blob.sliceArray(12 until blob.size)

			val cipher = Cipher.getInstance(WRAPPING_ALGORITHM)
			cipher.init(Cipher.DECRYPT_MODE, wrappingKey, GCMParameterSpec(128, iv))
			return cipher.doFinal(encrypted)
		}

		private fun getOrCreateWrappingKey(): SecretKey {
			if (keyStore.containsAlias(ALIAS)) {
				return (keyStore.getEntry(ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
			}

			val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
			val builder = KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
				.setKeySize(256)
				.setBlockModes(KeyProperties.BLOCK_MODE_GCM)
				.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
				.setUserAuthenticationRequired(false)

			try {
				builder.setIsStrongBoxBacked(true)
				keyGenerator.init(builder.build())
				return keyGenerator.generateKey()
			} catch (e: Exception) {
				Timber.w(e, "StrongBox unavailable, falling back to TEE")
				val fallbackBuilder = KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
					.setKeySize(256)
					.setBlockModes(KeyProperties.BLOCK_MODE_GCM)
					.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
					.setUserAuthenticationRequired(false)
				keyGenerator.init(fallbackBuilder.build())
				return keyGenerator.generateKey()
			}
		}

		fun deleteKey() {
			if (keyStore.containsAlias(ALIAS)) {
				keyStore.deleteEntry(ALIAS)
			}
			if (encryptedKeyFile.exists()) {
				encryptedKeyFile.delete()
			}
		}

		companion object {
			private const val ANDROID_KEYSTORE = "AndroidKeyStore"
			private const val ALIAS = "photoz-sqlcipher-wrapper-v1"
			private const val WRAPPING_ALGORITHM = "AES/GCM/NoPadding"
		}
	}
