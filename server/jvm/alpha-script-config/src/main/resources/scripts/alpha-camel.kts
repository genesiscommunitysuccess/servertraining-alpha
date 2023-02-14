import global.genesis.commons.standards.GenesisPaths
import org.apache.camel.Exchange
import org.apache.camel.support.processor.idempotent.FileIdempotentRepository
import java.io.File

camel {
    // read from sftp
    routeHandler {
        val pathFromSftp = "${GenesisPaths.runtime()}/inbound/"
        val pathToSftp = "${GenesisPaths.runtime()}/outbound/"
        LOG.info("#### About to create or make sure the dirs are there: '$pathFromSftp' and '$pathToSftp' ####")
        try {
            File(pathFromSftp).mkdirs()
            File(pathToSftp).mkdirs()
        } catch (e: Exception) {
            LOG.error("Error on create folders - ${e.message}", e)
        }

        val sftpServer = systemDefinition.get("SFTP_SERVER").get()
        val sftpPort = systemDefinition.get("SFTP_PORT").get()
        val sftpUserName = systemDefinition.get("SFTP_USERNAME").get()
        val sftpPassword = systemDefinition.get("SFTP_PASSWORD").get()
        val fromSftpFileName = systemDefinition.get("SFTP_FILE_FROM").get()
        val toSftpFileName = systemDefinition.get("SFTP_FILE_TO").get()

        //Handles sFTP file transfer
        val fromSftpIdempotentRepo = "${GenesisPaths.runtime()}/inbound/FROM_SFTP_IDEMPOTENT_CONSUMER.DATA"

        LOG.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
        LOG.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
        LOG.info("fromSftpFileName = $fromSftpFileName")
        LOG.info("fromSftpIdempotentRepo = $fromSftpIdempotentRepo")

        from(
            "sftp:" + sftpServer + ":" + sftpPort + "?username=" + sftpUserName + "&password=" + sftpPassword + "&include=" + fromSftpFileName +
                    "&delay=1000&sortBy=file:modified&delete=false&bridgeErrorHandler=true" +
                    "&throwExceptionOnConnectFailed=true&stepwise=false" // &knownHostsFile=/home/<APP_USER>/.ssh/known_hosts
        )
            .idempotentConsumer(
                header("CamelFileName"),
                FileIdempotentRepository.fileIdempotentRepository(File(fromSftpIdempotentRepo), 300000, 15000000)
            )
            .process { exchange ->
                LOG.info("sFTP file = {}", exchange.getIn().getHeader(Exchange.FILE_NAME_ONLY).toString())
            }
            .log("Fetched file from sFTP: \${headers.CamelFileName}")
            .to("file:$pathFromSftp")

        val toSftpIdempotentRepo = "${GenesisPaths.runtime()}/outbound/TO_SFTP_IDEMPOTENT_CONSUMER.DATA"
        LOG.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
        LOG.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
        LOG.info("toSftpIdempotentRepo = $toSftpIdempotentRepo")

        from(
            "file:" + pathToSftp + "&include=" + toSftpFileName +
                    "&delay=1000&sortBy=file:modified&delete=false&bridgeErrorHandler=true" +
                    "&throwExceptionOnConnectFailed=true&stepwise=false"
        )
            .idempotentConsumer(
                header("CamelFileName"),
                FileIdempotentRepository.fileIdempotentRepository(File(toSftpIdempotentRepo), 300000, 15000000)
            )
            .process { exchange ->
                LOG.info("local file = {}", exchange.getIn().getHeader(Exchange.FILE_NAME_ONLY).toString())
            }
            .log("Fetched file from sFTP: \${headers.CamelFileName}")
            .to("sftp:$sftpServer:$sftpPort?username=$sftpUserName&password=$sftpPassword")
    }
}