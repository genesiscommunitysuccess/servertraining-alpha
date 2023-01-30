import global.genesis.gen.config.tables.COUNTERPARTY
import global.genesis.gen.config.tables.COUNTERPARTY.COUNTERPARTY_NAME
import global.genesis.gen.config.tables.TRADE.COUNTERPARTY_LEI

pipelines {
  csvSource("cp-pipeline") {
    location = "file:/home/alpha/run/runtime/fileIngress?fileName=cp-pipeline.csv"

    map("mapper-name", COUNTERPARTY) {
      COUNTERPARTY_NAME {
        property = "counterpartyName"
      }
      COUNTERPARTY_LEI {
        property = "counterpartyLei"
      }
    }
  }
}