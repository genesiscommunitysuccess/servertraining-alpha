package global.genesis.alpha.fileHandler.csvMapper.impl

import global.genesis.alpha.fileHandler.csvMapper.GenericMapper

class CounterpartyMapper : GenericMapper() {
    lateinit var COUNTERPARTY_ID : String
    lateinit var COUNTERPARTY_NAME : String
    lateinit var ENABLED : String
    lateinit var COUNTERPARTY_LEI : String
}