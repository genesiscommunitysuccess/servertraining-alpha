package global.genesis.alpha.fileHandler.exception

import io.netty.handler.codec.http.HttpResponseStatus

class FileEndpointException(message: String?, val httpResponseStatus: HttpResponseStatus) :
    Exception(message)