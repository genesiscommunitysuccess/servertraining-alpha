import global.genesis.commons.standards.GenesisPaths
import global.genesis.data.ingest.source.SinkOperations
import global.genesis.data.ingest.transform.SinkOperation
import global.genesis.gen.config.tables.COUNTERPARTY
import global.genesis.gen.config.tables.COUNTERPARTY.COUNTERPARTY_ID
import global.genesis.gen.config.tables.COUNTERPARTY.COUNTERPARTY_NAME
import global.genesis.gen.config.tables.COUNTERPARTY.COUNTERPARTY_LEI

pipelines{
    csvSource("cp-pipeline"){
        location = "file:${GenesisPaths.runtime()}/fileIngress?fileName=cp-pipeline.csv&delete=true"
        errorStrategy = AllOrNothing()

        map("mapper-name", COUNTERPARTY){
            COUNTERPARTY_ID{
                property = "Id"
            }
            COUNTERPARTY_NAME{
                property = "Counterparty Name"
            }
            COUNTERPARTY_LEI{
                property = "Counterparty Lei"
            }
        }
    }
}
