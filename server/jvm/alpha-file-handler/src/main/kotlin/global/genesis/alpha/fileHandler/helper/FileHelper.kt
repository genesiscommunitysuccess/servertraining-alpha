package global.genesis.alpha.fileHandler.helper

import com.google.inject.Inject
import com.google.inject.name.Named
import global.genesis.alpha.fileHandler.exception.FileEndpointException
import global.genesis.db.rx.entity.multi.RxEntityDb
import global.genesis.gen.dao.UserSession
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@global.genesis.commons.annotation.Module
class FileHelper @Inject constructor(
    @Named(SYS_DEF_FILE_MAX_SIZE_IN_BITS) sysdefPayloadMaxSize: Long,
    private val db: RxEntityDb,
) {
    var maxFileSize: Long = 0

    init {
        setupMaxPayloadSize(sysdefPayloadMaxSize)
    }

    @Throws(FileEndpointException::class)
    fun getValidatedUserName(sessionAuthToken: String): String {
        val foundUserSession = db.get(UserSession.BySessionAuthToken(sessionAuthToken)).blockingGet()
        return foundUserSession?.userName
            ?: throw FileEndpointException("No session found for auth token",
                HttpResponseStatus.BAD_REQUEST)
    }

    fun errorResponse(e: FileEndpointException): DefaultFullHttpResponse {
        val responseJson = ("{\"ERROR\": \"" + e.message + "\"}").toByteArray()
        val responseBuffer = Unpooled.wrappedBuffer(responseJson)
        val response = DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            e.getHttpResponseStatus(),
            responseBuffer
        )
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        HttpUtil.setContentLength(response, responseJson.size.toLong())
        return response
    }

    fun errorResponse(errorMsg: String, errorResponseStatus: HttpResponseStatus?): DefaultFullHttpResponse {
        val responseJson = "{\"ERROR\": \"$errorMsg\"}".toByteArray()
        val responseBuffer = Unpooled.wrappedBuffer(responseJson)
        val response = DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            errorResponseStatus,
            responseBuffer
        )
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        HttpUtil.setContentLength(response, responseJson.size.toLong())
        return response
    }

    fun successResponse(responseText: String): DefaultFullHttpResponse {
        val responseJson = "{\"SUCCESS\": \"$responseText\"}".toByteArray()
        val responseBuffer = Unpooled.wrappedBuffer(responseJson)
        val response = DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.OK,
            responseBuffer
        )
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        HttpUtil.setContentLength(response, responseJson.size.toLong())
        return response
    }

    private fun setupMaxPayloadSize(sysdefPayloadMaxSize: Long?) {
        maxFileSize = if (sysdefPayloadMaxSize == null) {
            LOG.warn("System Definition Item {} not defined. Using default value 1Mb!",
                SYS_DEF_FILE_MAX_SIZE_IN_BITS
            )
            1000000L // Default is 1 Mb
        } else {
            sysdefPayloadMaxSize
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(FileHelper::class.java)
        const val ATTACHMENT_PATH = "file-handler"
        private const val SYS_DEF_FILE_MAX_SIZE_IN_BITS = "SYS_DEF_FILE_MAX_SIZE_IN_BITS"
    }
}
