import java.io.File
import global.genesis.commons.standards.GenesisPaths
import org.apache.camel.support.processor.idempotent.FileIdempotentRepository

camel {
    val pathFromSftp = "${GenesisPaths.runtime()}inbound/"
    LOG.info("#### About to create or make sure the dir is there: '$pathFromSftp' ####")
    try {
        File(pathFromSftp).mkdirs()
        LOG.info("#### Dir is created as defined: '$pathFromSftp' ####")
    } 
    catch (e: Exception) {
        LOG.error("Error on create folder - ${e.message}", e)
    }

    routeHandler {
        val endPointPath = systemDefinition.getItem("SFTP_PATH")
        val userName = systemDefinition.getItem("SFTP_USERNAME")
        val password = systemDefinition.getItem("SFTP_PASSWORD")
        val dirName = systemDefinition.getItem("SFTP_DIRECTORY")
		val fileName = systemDefinition.getItem("SFTP_FILE")
		val pathStrInbound = "${GenesisPaths.genesisHome()}runtime/inbound"
        val consumerRepo = "${pathStrInbound}/IDEMPOTENT_CONSUMER.DATA"

		LOG.info("Reading from an SFTP server :: '${dirName}/${fileName}' to '${pathStrInbound}/alpha'")
        from("sftp:${endPointPath}/${dirName}?username=${userName}&password=${password}&include=${fileName}" +
                "&delay=1000&sortBy=file:modified&delete=false&bridgeErrorHandler=true" +
                "&useUserKnownHostsFile=false&throwExceptionOnConnectFailed=true&stepwise=false")
            .idempotentConsumer(header("CamelFileName"),
                FileIdempotentRepository.fileIdempotentRepository(File(consumerRepo), 300000, 15000000))
            .process { exchange ->
                LOG.info("SFTP copy CamelFileName = ${exchange.`in`.getHeader("CamelFileNameOnly").toString()}")
            }
            .log("ALPHA file transfer: \${headers.CamelFileName}")
            .to("file:${pathStrInbound}")
        LOG.info("Done - Reading from an SFTP server :: '${dirName}/${fileName}' to '${pathStrInbound}/alpha'")
    }
}