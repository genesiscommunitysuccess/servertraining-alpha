package global.genesis

import com.google.inject.Inject
import global.genesis.commons.model.GenesisSet
import global.genesis.db.DbRecord
import global.genesis.db.entity.*
import global.genesis.db.query.api.reading.DataTypeConverter
import global.genesis.db.query.api.reading.DbRecordReader
import global.genesis.db.query.api.reading.GenesisSetReader
import global.genesis.db.query.api.reading.ValueReader
import global.genesis.db.query.api.rowmapper.RowMapper
import global.genesis.dictionary.pal.TableField
import global.genesis.gen.config.tables.TRADE
import global.genesis.gen.dao.Trade
import global.genesis.gen.dao.description.TradeDescription
import global.genesis.gen.dao.enums.Direction
import global.genesis.gen.dao.enums.TradeStatus
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
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.Test
import java.io.Serializable
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

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

