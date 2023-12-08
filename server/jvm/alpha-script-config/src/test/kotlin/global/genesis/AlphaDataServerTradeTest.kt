package global.genesis

import com.google.inject.Inject
import global.genesis.db.entity.*
import global.genesis.gen.dao.Trade
import global.genesis.gen.dao.description.TradeDescription
import global.genesis.gen.dao.repository.TradeAsyncRepository
import global.genesis.testsupport.dataserver.DataServerMsg
import global.genesis.testsupport.dataserver.DataServerTest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.awaitility.kotlin.await
import org.awaitility.kotlin.has
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.Test
import java.util.concurrent.CopyOnWriteArrayList

class AlphaDataServerTradeTest : DataServerTest<Trade>(
    genesisHome = "/GenesisHome/",
    scriptFileName = "alpha-dataserver.kts",
    initialDataFile = "data/TEST_DATA.csv",
    mapperBuilder = TradeDescription::buildRowMapper,
    authCacheOverride = "ENTITY_VISIBILITY"
) {

    @Inject
    private lateinit var tableRepo: TradeAsyncRepository

    @Test
    fun `test ALL_PRICES trades`() = runBlocking(coroutineContext) {
        val updates = CopyOnWriteArrayList<DataServerMsg.QueryUpdate<Trade>>()
        val updateJob = launch {
            dataLogon("ALL_PRICES", 5)
                .filterIsInstance<DataServerMsg.QueryUpdate<Trade>>()
                .collect { updates.add(it) }
        }

        val trades = tableRepo.getBulk().take(4).toList()

        await untilCallTo { updates } has { size == 1 }

        val rows: List<Trade> = updates.first().rows

        assert(rows.map { it.price }.sorted() == trades.map { it.price }.sorted())

        updateJob.cancel()
    }
}

