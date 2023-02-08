import java.io.File
import global.genesis.commons.standards.GenesisPaths
import org.apache.camel.support.processor.idempotent.FileIdempotentRepository

camel {
    routeHandler {
        val bbgEndPointPath = systemDefinition.getItem("BBG_SERVER_SFTP")
        val bbgUserName = systemDefinition.getItem("BBG_SERVER_USERNAME")
        val bbgPassword = systemDefinition.getItem("BBG_SERVER_PASSWORD")
        val bbgFileName = systemDefinition.getItem("BBG_SERVER_FILENAME")
        val pathStr = "${GenesisPaths.genesisHome()}/runtime/inbound"
        val bbgConsumerRepo = "${pathStr}/IDEMPOTENT_CONSUMER.DATA"

        from("sftp:${bbgEndPointPath}?username=${bbgUserName}&password=${bbgPassword}&include=$${bbgFileName}" +
                "&delay=1000&sortBy=file:modified&delete=false&bridgeErrorHandler=true" +
                "&knownHostsFile=/home/priss/.ssh/known_hosts&throwExceptionOnConnectFailed=true&stepwise=false")
            .idempotentConsumer(header("CamelFileName"),
                FileIdempotentRepository.fileIdempotentRepository(File(bbgConsumerRepo), 300000, 15000000))
            .process { exchange ->
                LOG.debug("SFTP copy CamelFileName = ${exchange.`in`.getHeader("CamelFileNameOnly").toString()}")
            }
            .log("BBG file transfer: \${headers.CamelFileName}")
            .to("file:${pathStr}/bbg")
    }

    routeHandler {
        from("file:/directory/to/watch")
            .to("sftp://remote-host:22/remote-path?username=user&password=pass")
    }
}