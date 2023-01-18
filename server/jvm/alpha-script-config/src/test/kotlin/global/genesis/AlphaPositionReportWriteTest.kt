package global.genesis

import global.genesis.alpha.message.event.PositionReport
import global.genesis.commons.model.GenesisSet
import global.genesis.dictionary.GenesisDictionary
import global.genesis.message.core.event.EventReply
import global.genesis.testsupport.AbstractGenesisTestSupport
import global.genesis.testsupport.GenesisTestConfig
import kotlinx.coroutines.runBlocking
import org.junit.Test

class AlphaPositionReportWriteTest : AbstractGenesisTestSupport<GenesisSet>(
    GenesisTestConfig {
        addPackageName("global.genesis.eventhandler.pal")
        genesisHome = "/GenesisHome/"
        parser = { it }
        scriptFileName = "alpha-eventhandler.kts"
        initialDataFile = "data/TEST_DATA.csv"
        addAuthCacheOverride("ENTITY_VISIBILITY")
    }
) {
    override fun systemDefinition(): Map<String, Any> = mapOf(
        "IS_SCRIPT" to "true"
    )

    override fun createDictionary(): GenesisDictionary = prodDictionary()

    @Test
    fun `test write event`(): Unit = runBlocking {
        sendEvent(PositionReport()).assertedCast<EventReply.EventAck>()
    }
}