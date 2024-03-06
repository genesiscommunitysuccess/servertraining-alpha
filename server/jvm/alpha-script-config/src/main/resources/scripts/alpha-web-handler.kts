import global.genesis.commons.standards.GenesisPaths
import global.genesis.message.core.HttpStatusCode
import java.nio.file.*

webHandlers("BASE-PATH"){
    multipartEndpoint("FILE_UPLOAD",status=HttpStatusCode.Forbidden){
        val fileUploadFolder = "traning/files"
        config {
            multiPart {
                maxFileSize - 10_000_000
                useDisk = true
                baseDir = fileUploadFolder
            }
        }
        val savedFolder = Paths.get(GenesisPaths.runtime() + "/" + fileUploadFolder)
        handleRequest {
            body.fileUploads.forEach{
                it.copyTo(savedFolder.resolve(it.fileName))
            }
        }
    }
    endpoint<Trade,Trade>(GET,"TEST"){
        handleRequest{
            val trade = db.insert(body).record
            triggerEvent("TRADE_AUDIT_STREAM",trade)
            trade
        }
    }
}

