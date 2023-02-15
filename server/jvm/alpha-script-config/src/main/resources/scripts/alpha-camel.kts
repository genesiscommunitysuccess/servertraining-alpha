import global.genesis.commons.standards.GenesisPaths
import org.apache.camel.support.processor.idempotent.FileIdempotentRepository
import java.io.File

camel {
    routeHandler {
        val endPointPath = systemDefinition.getItem("SFTP_PATH")
        val userName = systemDefinition.getItem("SFTP_USERNAME")
        val password = systemDefinition.getItem("SFTP_PASSWORD")
        val fileName = systemDefinition.getItem("SFTP_FILE_FROM")
        val pathStr = "${GenesisPaths.genesisHome()}/runtime/inbound"
        val consumerRepo = "${pathStr}/IDEMPOTENT_CONSUMER.DATA"

        LOG.info("About to start the 'Reading from an SFTP server' process")
        from("sftp://${userName}:${password}@${endPointPath}&include=$${fileName}" +
                "&delay=1000&sortBy=file:modified&delete=false&bridgeErrorHandler=true" +
                "&throwExceptionOnConnectFailed=true&stepwise=false")
            .idempotentConsumer(header("CamelFileName"),
                FileIdempotentRepository.fileIdempotentRepository(File(consumerRepo), 300000, 15000000))
            .process { exchange ->
                LOG.debug("SFTP copy CamelFileName = ${exchange.`in`.getHeader("CamelFileNameOnly").toString()}")
            }
            .log("file transfer: \${headers.CamelFileName}")
            .to("file:${pathStr}")
    }
}