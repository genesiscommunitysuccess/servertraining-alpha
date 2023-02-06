package global.genesis.alpha.fileHandler.csvMapper.impl

import global.genesis.alpha.fileHandler.csvMapper.GenericMapper

class InstrumentMapper : GenericMapper() {
    lateinit var INSTRUMENT_ID : String
    lateinit var INSTRUMENT_NAME : String
    lateinit var MARKET_ID : String
    lateinit var COUNTRY_CODE : String
    lateinit var CURRENCY_ID : String
    lateinit var ASSET_CLASS : String
}