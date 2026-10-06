package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ServerDataSource
import com.example.model.ConnectionStatus
import com.example.model.ServerRegion
import com.example.model.VpnProtocol
import com.example.vpn.VpnController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ShieldVPN", appName)
    }

    @Test
    fun `verify free global servers data source`() {
        val servers = ServerDataSource.initialServers
        assertTrue("Should have global servers", servers.isNotEmpty())
        assertTrue("All initial servers should be free tier", servers.all { it.isFree })
        assertTrue("Should contain European servers", servers.any { it.region == ServerRegion.EUROPE })
        assertTrue("Should contain American servers", servers.any { it.region == ServerRegion.AMERICAS })
        assertTrue("Should contain Asia-Pacific servers", servers.any { it.region == ServerRegion.ASIA_PACIFIC })
    }

    @Test
    fun `verify vpn controller initial state`() {
        assertEquals(ConnectionStatus.DISCONNECTED, VpnController.status.value)
        assertNotNull(VpnController.selectedServer.value)
        assertEquals(VpnProtocol.WIREGUARD, VpnController.selectedServer.value.protocol)
    }
}
