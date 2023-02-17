package global.genesis.alpha.fileHandler.endpoint

import com.google.common.collect.ImmutableSet
import com.google.inject.Inject
import global.genesis.alpha.fileHandler.exception.FileEndpointException
import global.genesis.alpha.fileHandler.helper.FileHelper
import global.genesis.commons.annotation.Module
import global.genesis.db.entity.TableEntity
import global.genesis.db.rx.entity.multi.RxEntityDb
import global.genesis.gen.dao.Counterparty
import global.genesis.router.server.web.http.extensions.RequestType
import global.genesis.router.server.web.http.extensions.WebEndpoint
import global.genesis.router.server.web.http.extensions.WebEndpointRegistry
import io.netty.channel.Channel
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.multipart.*
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct


@Module
class CounterpartyUploadEndpoint @Inject constructor(
    private val registry: WebEndpointRegistry,
    private val db: RxEntityDb,
    fileHelper: FileHelper
) : WebEndpoint {
    private val fileHelper: FileHelper

    init {
        this.fileHelper = fileHelper
    }

    //Register this under the high level endpoint <protocol><domain>/counterparty-handler/
    @PostConstruct
    fun init() {
        registry.registerEndpoint(FileHelper.COUNTERPARTY_PATH, this)
    }

    //Register this specific endpoint <protocol><domain>/counterparty-handler/upload/
    override fun name(): String {
        return "upload"
    }

    //Register the http method types which are allowed to his  this endpoint
    override fun allowedMethods(): kotlin.collections.Set<RequestType> {
        return ALLOWED_HTTP_METHODS
    }

    /**
     * Handle an upload request hitting this endpoint
     *
     *
     * Counterpartys will be validated for meta and stored against a given request for download later
     */
    override fun process(s: String, fullHttpRequest: FullHttpRequest, channel: Channel): Any {
        try {
            //Get user from session token
            val username: String = fileHelper.getValidatedUserName(fullHttpRequest.headers()["SESSION_AUTH_TOKEN"])
            //TODO validate user is in the correct group when we have rights set up

            //Get and validate request input
            val requestId: Int? = null
            val uploadedFiles: MutableList<MemoryFileUpload> = ArrayList()
            val dataFactory = DefaultHttpDataFactory(false)
            dataFactory.setMaxLimit(fileHelper.maxFileSize())
            try {
                val postDecoder = HttpPostRequestDecoder(dataFactory, fullHttpRequest)
                val postData = postDecoder.bodyHttpDatas

                //Parse out all the data we need
                for (data in postData) {
                    // General Post Content
                    if (data.httpDataType == InterfaceHttpData.HttpDataType.Attribute) {
                        val attribute = data as MemoryAttribute
                        LOG.info("CounterpartyUploadEndpoint INFO :: attribute {}", attribute)
                        LOG.info("CounterpartyUploadEndpoint INFO :: attribute.getName() {}", attribute.name)
                        LOG.info("CounterpartyUploadEndpoint INFO :: attribute.getValue() {}", attribute.value)
                    }
                    else if (data.httpDataType == InterfaceHttpData.HttpDataType.FileUpload) {
                        // File Uploaded
                        val fileUpload = data as MemoryFileUpload
                        uploadedFiles.add(fileUpload)
                        LOG.info("CounterpartyUploadEndpoint INFO :: File-{} file-name : {}", uploadedFiles.size, fileUpload.name)
                    }
                }
                if (uploadedFiles.size == 0) {
                    LOG.error("CounterpartyUploadEndpoint ERROR :: No file uploaded with request")
                    throw FileEndpointException("No file uploaded", HttpResponseStatus.BAD_REQUEST)
                }
                val uploadTime = DateTime()
                for (uploadedFile in uploadedFiles) {
                    LOG.info("CounterpartyUploadEndpoint INFO :: uploadedFile {}", uploadedFile)
                    val counterpartyRecord = getCounterparty(uploadTime, username)
                    updateDb(counterpartyRecord)
                }
            }
            catch (ex: HttpPostRequestDecoder.ErrorDataDecoderException) {
                LOG.error("CounterpartyUploadEndpoint ERROR :: Error decoding file", ex)
                throw FileEndpointException(ex.message, HttpResponseStatus.BAD_REQUEST)
            }
            catch (e: FileEndpointException) {
                throw e
            }
            catch (e: Exception) {
                LOG.error("CounterpartyUploadEndpoint ERROR :: File upload failed to process!", e)
                throw FileEndpointException("File upload failed", HttpResponseStatus.BAD_REQUEST)
            }
        }
        catch (e: FileEndpointException) {
            return fileHelper.errorResponse(e)
        }
        return fileHelper.successResponse("Request counterparty(s) added successfully")
    }

    private fun updateDb(Counterparty: Counterparty) {
        db.insert<TableEntity>(Counterparty).subscribe()
    }

    // Turn the received request meta into a dao DB record for writing
    private fun getCounterparty(uploadTime: DateTime, userName: String): Counterparty {
        //need a way to programmatically set these
        return Counterparty.builder()
            .setCounterpartyId("")
            .setCounterpartyName("")
            .build()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(CounterpartyUploadEndpoint::class.java)
        private val ALLOWED_HTTP_METHODS: kotlin.collections.Set<RequestType> = ImmutableSet.of(RequestType.POST)
    }
}
