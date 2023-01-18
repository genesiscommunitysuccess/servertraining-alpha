import global.genesis.gen.config.fields.Fields

/**
 * System              : Genesis Business Library
 * Sub-System          : multi-pro-code-test Configuration
 * Version             : 1.0
 * Copyright           : (c) Genesis
 * Date                : 2022-03-18
 * Function : Provide dataserver config for multi-pro-code-test.
 *
 * Modification History
 */
dataServer {
    query("ALL_TRADES", TRADE_VIEW) {
        permissioning {
            auth(mapName = "ENTITY_VISIBILITY") {
                TRADE_VIEW.COUNTERPARTY_ID
            }
        }
    }
    query("ALL_PRICES", TRADE) {
        fields {
            TRADE_ID
            INSTRUMENT_ID
            PRICE
            SYMBOL
        }
        where { trade ->
            trade.price!! > 0.0
        }
    }
    query("ALL_COUNTERPARTIES", COUNTERPARTY_VIEW)
    query("ALL_INSTRUMENTS", INSTRUMENT)
    query("ALL_POSITIONS", POSITION)
}