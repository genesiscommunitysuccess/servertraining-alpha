package global.genesis.alpha.fileHandler.exception

import io.netty.handler.codec.http.HttpResponseStatus

class FileEndpointException(s: String, internalServerError: HttpResponseStatus) :
    Throwable() {
    private val internalServerError: HttpResponseStatus
    override val message: String

    init {
        message = s
        this.internalServerError = internalServerError
    }

    fun getHttpResponseStatus(): HttpResponseStatus {
        return internalServerError
    }
}
