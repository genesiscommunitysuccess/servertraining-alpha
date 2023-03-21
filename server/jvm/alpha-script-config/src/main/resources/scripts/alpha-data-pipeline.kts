import global.genesis.gen.config.tables.COUNTERPARTY
import global.genesis.gen.config.tables.COUNTERPARTY.COUNTERPARTY_NAME
import global.genesis.gen.config.tables.COUNTERPARTY.COUNTERPARTY_LEI

pipelines {
  csvSource("cp-pipeline") {
    location = "file:/home/alpha/run/runtime/fileIngress?fileName=cp-pipeline.csv"

    map("mapper-name", COUNTERPARTY) {
      COUNTERPARTY_NAME {
        property = "COUNTERPARTY_NAME"
      }
      COUNTERPARTY_LEI {
        property = "COUNTERPARTY_LEI"
      }
    }
  }
}