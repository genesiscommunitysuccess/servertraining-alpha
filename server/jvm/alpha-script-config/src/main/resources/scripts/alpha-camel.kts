import java.io.File
import global.genesis.commons.standards.GenesisPaths
import org.apache.camel.support.processor.idempotent.FileIdempotentRepository

camel {
    val pathFromSftp = "${GenesisPaths.runtime()}/inbound/"
    val pathToSftp = "${GenesisPaths.runtime()}/outbound/"
    LOG.info("#### About to create or make sure the dirs are there: '$pathFromSftp' and '$pathToSftp' ####")
    try {
        File(pathFromSftp).mkdirs()
        File(pathToSftp).mkdirs()
        LOG.info("#### Dirs are created as defined: '$pathFromSftp' and '$pathToSftp' ####")
    } 
    catch (e: Exception) {
        LOG.error("Error on create folders - ${e.message}", e)
    }

	val endPointPath = systemDefinition.getItem("SFTP_PATH")
	val userName = systemDefinition.getItem("SFTP_USERNAME")
	val password = systemDefinition.getItem("SFTP_PASSWORD")

    routeHandler {
		val fileNameFrom = systemDefinition.getItem("SFTP_FILE_FROM")
		val pathStrInbound = "${GenesisPaths.genesisHome()}/runtime/inbound"
        val consumerRepo = "${pathStrInbound}/IDEMPOTENT_CONSUMER.DATA"

		LOG.info("Reading from an SFTP server :: '${fileNameFrom}' to '${pathStrInbound}/alpha'")
        from("sftp:${endPointPath}?username=${userName}&password=${password}&include=${fileNameFrom}" +
                "&delay=1000&sortBy=file:modified&delete=false&bridgeErrorHandler=true" +
                "&knownHostsFile=/home/alpha/.ssh/known_hosts&throwExceptionOnConnectFailed=true&stepwise=false")
            .idempotentConsumer(header("CamelFileName"),
                FileIdempotentRepository.fileIdempotentRepository(File(consumerRepo), 300000, 15000000))
            .process { exchange ->
                LOG.info("SFTP copy CamelFileName = ${exchange.`in`.getHeader("CamelFileNameOnly").toString()}")
            }
            .log("ALPHA file transfer: \${headers.CamelFileName}")
            .to("file:${pathStrInbound}/alpha")
    }

    routeHandler {
		//val fileNameTo = systemDefinition.getItem("SFTP_FILE_TO")
		val fileNameTo = "/home/alpha/run/runtime/outbound/to.txt" 
		
		LOG.info("Writing to an SFTP server :: '${fileNameTo}' to 'sftp:${endPointPath}?username=${userName}&password=${password}'")
        from("file:${fileNameTo}")
            .to("sftp:${endPointPath}?username=${userName}&password=${password}")
    }
}