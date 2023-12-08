package global.genesis.alpha.fileHandler.helper

import com.google.inject.Inject
import com.google.inject.name.Named
import global.genesis.db.rx.entity.multi.RxEntityDb
import global.genesis.gen.dao.UserSession
import global.genesis.alpha.fileHandler.exception.FileEndpointException
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.*
import io.netty.handler.codec.http.HttpVersion.HTTP_1_1

@global.genesis.commons.annotation.Module
class FileHelper {
    var maxFileSize: Long = 0
    private var db: RxEntityDb? = null

    @Inject
    fun AttachmentCommon(@Named(SYS_DEF_ATTACHMENT_MAX_SIZE_IN_BITS) sysdefPayloadMaxSize: Long, db: RxEntityDb?) {
        this.db = db
        setupMaxPayloadSize(sysdefPayloadMaxSize)
    }

    @Throws(FileEndpointException::class)
    fun getValidatedUserName(sessionAuthToken: String): String? {
        val foundUserSession: UserSession? = db!!.get(UserSession.BySessionAuthToken(sessionAuthToken)).blockingGet()
        return foundUserSession?.userName ?: null
    }

    fun errorResponse(e: FileEndpointException): DefaultFullHttpResponse {
        val responseJson: ByteArray = ("{\"ERROR\": \"" + e.message + "\"}").toByteArray()
        val responseBuffer = Unpooled.wrappedBuffer(responseJson)
        val response = DefaultFullHttpResponse(
            HTTP_1_1,
            e.httpResponseStatus,
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
            HTTP_1_1,
            HttpResponseStatus.OK,
            responseBuffer
        )
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        HttpUtil.setContentLength(response, responseJson.size.toLong())
        return response
    }

    fun maxFileSize() : Long {
        return maxFileSize
    }

    private fun setupMaxPayloadSize(sysdefPayloadMaxSize: Long) {
        if (sysdefPayloadMaxSize == null) {
            maxFileSize = 1000000L // Default is 1 Mb
        }
        else {
            maxFileSize = sysdefPayloadMaxSize
        }
    }

    companion object {
        const val INSTRUMENT_PATH = "instrument-handler"
        const val COUNTERPARTY_PATH = "counterparty-handler"
        private const val SYS_DEF_ATTACHMENT_MAX_SIZE_IN_BITS = "SYS_DEF_FILE_MAX_SIZE_IN_BITS"
    }
}
