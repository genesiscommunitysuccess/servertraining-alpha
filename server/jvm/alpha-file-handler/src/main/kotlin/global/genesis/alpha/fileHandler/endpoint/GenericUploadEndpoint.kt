package global.genesis.alpha.fileHandler.endpoint

import com.google.gson.Gson
import com.opencsv.CSVReader
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.bean.HeaderColumnNameMappingStrategy
import global.genesis.alpha.fileHandler.helper.FileHelper
import global.genesis.db.rx.entity.multi.AsyncMultiEntityReadWriteGenericSupport
import global.genesis.router.server.web.http.extensions.RequestType
import global.genesis.router.server.web.http.extensions.WebEndpoint
import global.genesis.router.server.web.http.extensions.WebEndpointRegistry
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.*
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException
import io.netty.handler.codec.http.multipart.MemoryFileUpload
import org.slf4j.LoggerFactory
import java.io.StringReader
import java.lang.reflect.ParameterizedType
import javax.annotation.PostConstruct

abstract class GenericUploadEndpoint<T : Any> constructor(
    private val registry: WebEndpointRegistry,
    private val attachmentCommon: FileHelper,
    private val endpointName: String,
    private val entityDb: AsyncMultiEntityReadWriteGenericSupport
) : WebEndpoint {

    @PostConstruct
    fun init() {
        registry.registerEndpoint(FileHelper.ATTACHMENT_PATH, this)
        LOG.info("FILE_HANDLER: Registered endpoint for $endpointName")
    }

    override fun process(method: String, request: FullHttpRequest, conn: io.netty.channel.Channel): Any {

        LOG.info("FILE_HANDLER: Hit {}/{} endpoint", FileHelper.ATTACHMENT_PATH, name())

        var uploadedFile: MemoryFileUpload

        val dataFactory = DefaultHttpDataFactory(false)
        dataFactory.setMaxLimit(attachmentCommon.maxFileSize)

        try {

            val postDecoder = HttpPostRequestDecoder(dataFactory, request)
            val postData = postDecoder.bodyHttpDatas

            for (data in postData) {

                uploadedFile = data as MemoryFileUpload
                LOG.info("FILE_HANDLER: file-name : {} , user {}", uploadedFile.name, "username")

                val genericClass: Class<T> = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>
                LOG.info("genericClass is $genericClass")

                val ms: HeaderColumnNameMappingStrategy<*> = HeaderColumnNameMappingStrategy<Any?>()
                ms.type = genericClass

                LOG.info("uploadedFile.string equals to '{}'", uploadedFile.string)

                val csvRows = CsvToBeanBuilder<Any?>(
                    CSVReader(
                        StringReader(uploadedFile.string)
                    )
                )
                .withType(genericClass)
                .withMappingStrategy(ms)
                .build()
                .parse() as List<T>
                LOG.info("csvRows equals to '{}'", csvRows)

                for (csvRow in csvRows) {
                    LOG.info("current csvRow is '{}'", csvRow)
                }
            }
        }
        catch (ex: ErrorDataDecoderException) {
            val errMsg = "ErrorDataDecoderException: $ex"
            LOG.error(errMsg)
            return attachmentCommon.errorResponse(errMsg, HttpResponseStatus.INTERNAL_SERVER_ERROR)
        }
        catch (ex: Exception) {
            val errMsg = "Exception: $ex"
            LOG.error(errMsg)
            return attachmentCommon.errorResponse(errMsg, HttpResponseStatus.INTERNAL_SERVER_ERROR)
        }

        return attachmentCommon.successResponse("File handled successfully for $endpointName")
    }

    override fun allowedMethods(): Set<RequestType> {
        return ALLOWED_HTTP_METHODS
    }

    override fun name(): String {
        return endpointName
    }

    override fun requiresAuth(): Boolean {
        return false
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(GenericUploadEndpoint::class.java)
        private val ALLOWED_HTTP_METHODS: Set<RequestType> = setOf(RequestType.POST)
    }
}
