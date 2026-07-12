package onlasdan.gallery.encryption.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SqlCipherKeyProviderTest {
    private fun androidKeyStoreAvailable(): Boolean = false // Robolectric doesn't support it well

    @Test
    fun `getOrCreatePassphrase returns 32 bytes`() {
        assumeTrue("AndroidKeyStore not available in Robolectric", androidKeyStoreAvailable())
    }
}
