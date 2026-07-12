package onlasdan.gallery.encryption.domain

/**
 * Interface for backfilling vault_id after unlock.
 */
interface VaultIdBackfillHook {
	suspend fun backfillVaultId(vaultId: String)
}
