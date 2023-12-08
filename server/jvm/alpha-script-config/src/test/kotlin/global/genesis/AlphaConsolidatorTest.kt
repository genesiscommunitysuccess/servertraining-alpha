package global.genesis

import global.genesis.commons.model.GenesisSet
import global.genesis.gen.dao.Position
import global.genesis.gen.dao.Trade
import global.genesis.gen.dao.enums.Direction
import global.genesis.gen.dao.enums.TradeStatus
import global.genesis.testsupport.AbstractGenesisTestSupport
import global.genesis.testsupport.GenesisTestConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AlphaConsolidatorTest : AbstractGenesisTestSupport<GenesisSet>(
  GenesisTestConfig {
    addPackageName("global.genesis.pal.consolidator")
    genesisHome = "/GenesisHome/"
    scriptFileName = "alpha-consolidator.kts"
    parser = { it }
  }) {
  override fun systemDefinition(): Map<String, Any> = mapOf("IS_SCRIPT" to "true")

  @Test
  fun `position is consolidated when trades are inserted with a trade status of NEW`() = runBlocking {
    val testCase = TestCase(
      numberOfBuys = 5L,
      numberOfSells = 2L,
      tradeQuantity = 5,
      tradePrice = 100.00,
      tradeStatus = TradeStatus.NEW,
      instrumentId = "1"
    )
    insertTrades(testCase)
    delay(3000)

    val actualPosition = entityDb.getBulk<Position>().toList()[0]

    assertEquals("1", actualPosition.instrumentId)
    assertEquals(15, actualPosition.quantity)
    assertEquals(1500.0, actualPosition.notional)
    assertEquals(1500.0, actualPosition.value)
    //assertEquals(25, actualPosition.buyQuantity)
    //assertEquals(10, actualPosition.sellQuantity)
  }

  @Test
  fun `position is consolidated when trades are inserted with a trade status of ALLOCATED`() = runBlocking {
    val testCase = TestCase(
      numberOfBuys = 5L,
      numberOfSells = 2L,
      tradeQuantity = 5,
      tradePrice = 100.00,
      tradeStatus = TradeStatus.ALLOCATED,
      instrumentId = "1"
    )
    insertTrades(testCase)
    delay(3000)

    val actualPosition = entityDb.getBulk<Position>().toList()[0]

    assertEquals("1", actualPosition.instrumentId)
    assertEquals(15, actualPosition.quantity)
    assertEquals(1500.0, actualPosition.notional)
    assertEquals(1500.0, actualPosition.value)
    //assertEquals(25, actualPosition.buyQuantity)
    //assertEquals(10, actualPosition.sellQuantity)
  }

  @Test
  fun `position is consolidated when a trade is CANCELLED with a side of BUY`() = runBlocking {
    val testCase = TestCase(
      numberOfBuys = 2L,
      numberOfSells = 0L,
      tradeQuantity = 5,
      tradePrice = 100.00,
      tradeStatus = TradeStatus.ALLOCATED,
      instrumentId = "1",
      numberOfBuyCancels = 1
    )
    insertTrades(testCase)
    cancelTrades(testCase)
    delay(3000)

    val actualPosition = entityDb.getBulk<Position>().toList()[0]

    assertEquals("1", actualPosition.instrumentId)
    assertEquals(5, actualPosition.quantity)
    assertEquals(500.0, actualPosition.notional, String.format("expected %s, actualPosition.notional %s", "500.0", actualPosition.notional))
    assertEquals(1000.0, actualPosition.value, String.format("expected %s, actualPosition.value %s", "500.0", actualPosition.value))
    assertEquals(500.0, actualPosition.pnl)
  }

  @Test
  fun `position is consolidated when a trade is CANCELLED with a side of SELL`() = runBlocking {
    val testCase = TestCase(
      numberOfBuys = 0L,
      numberOfSells = 2L,
      tradeQuantity = 5,
      tradePrice = 100.00,
      tradeStatus = TradeStatus.ALLOCATED,
      instrumentId = "1",
      numberOfSellCancels = 1
    )
    insertTrades(testCase)
    cancelTrades(testCase)
    delay(3000)

    val actualPosition = entityDb.getBulk<Position>().toList()[0]

    assertEquals("1", actualPosition.instrumentId)
    assertEquals(-5, actualPosition.quantity)
    assertEquals(-500.0, actualPosition.notional, String.format("expected %s, actualPosition.notional %s", "-1000.0", actualPosition.notional))
    assertEquals(-1000.0, actualPosition.value, String.format("expected %s, actualPosition.value %s", "-1000.0", actualPosition.value))
    //assertEquals(0, actualPosition.buyQuantity)
    //assertEquals(5, actualPosition.sellQuantity)
  }

  @Test
  fun `positions are grouped by instrumentId`() = runBlocking {
    insertTrades(TestCase(1L, 0L, 10, 50.00, TradeStatus.NEW, "1"))
    insertTrades(TestCase(1L, 0L, 10, 50.00, TradeStatus.NEW, "2"))
    delay(3000)
    val positions = entityDb.getBulk<Position>().toList()
    assertEquals(2, positions.size)
    //assertEquals("1", positions[1].instrumentId)
  }

  private fun insertTrades(testCase: TestCase) = runBlocking {
    val trades = arrayListOf<Trade>()
    val numberOfBuys = testCase.numberOfBuys
    val qtyOfBuyAndSellsCombined = testCase.numberOfBuys.plus(testCase.numberOfSells)
    for (i in 1..qtyOfBuyAndSellsCombined) {
        val tradeDirection = if (i <= numberOfBuys) Direction.BUY else Direction.SELL
        val trade = Trade {
          instrumentId = testCase.instrumentId
          counterpartyId = "1"
          price = testCase.tradePrice
          quantity = testCase.tradeQuantity
          direction = tradeDirection
          tradeStatus = testCase.tradeStatus
        }
        trades.add(trade)
    }
    entityDb.insertAll(trades)
  }

  private fun cancelTrades(testCase: TestCase) = runBlocking {
    val tradesMap = entityDb.getBulk<Trade>().toList().groupBy { it.direction }
    for ((key, value) in tradesMap) {
      val numOfCurrentSideCancels = if (key == Direction.BUY) testCase.numberOfBuyCancels else testCase.numberOfSellCancels
      for (i in 0..value.size) {
        if (i < numOfCurrentSideCancels)
          entityDb.updateBy(Trade.byId(value[i].tradeId)) { tradeStatus = TradeStatus.CANCELLED }
      }
    }
  }

  private data class TestCase(
    val numberOfBuys: Long,
    val numberOfSells: Long,
    val tradeQuantity: Int,
    val tradePrice: Double,
    val tradeStatus: TradeStatus,
    val instrumentId: String,
    val numberOfBuyCancels: Long = 0L,
    val numberOfSellCancels: Long = 0L
  )
}