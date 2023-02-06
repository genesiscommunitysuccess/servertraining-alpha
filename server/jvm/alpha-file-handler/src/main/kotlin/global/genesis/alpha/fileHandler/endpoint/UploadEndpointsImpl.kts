package global.genesis.alpha.fileHandler.endpoint

import com.google.inject.Inject
import global.genesis.alpha.fileHandler.helper.FileHelper
import global.genesis.alpha.fileHandler.csvMapper.impl.CounterpartyMapper
import global.genesis.alpha.fileHandler.csvMapper.impl.InstrumentMapper
import global.genesis.db.rx.entity.multi.AsyncEntityDb
import global.genesis.router.server.web.http.extensions.WebEndpointRegistry

@global.genesis.commons.annotation.Module
class CounterpartyUploadEndpoint @Inject constructor(
    registry: WebEndpointRegistry,
    attachmentCommon: FileHelper,
    entityDb: AsyncEntityDb
) : GenericUploadEndpoint<CounterpartyMapper>(registry, attachmentCommon, "upload-counterparty", entityDb)

@global.genesis.commons.annotation.Module
class InstrumentUploadEndpoint @Inject constructor(
    registry: WebEndpointRegistry, attachmentCommon: FileHelper,
    entityDb: AsyncEntityDb
) : GenericUploadEndpoint<InstrumentMapper>(registry, attachmentCommon, "upload-instrument", entityDb)
