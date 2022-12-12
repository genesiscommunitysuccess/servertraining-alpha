/**
 * System              : Genesis Business Library
 * Sub-System          : multi-pro-code-test Configuration
 * Version             : 1.0
 * Copyright           : (c) Genesis
 * Date                : 2022-03-18
 * Function : Provide view config for multi-pro-code-test.
 *
 * Modification History
 */
views {

    view("TRADE_VIEW", TRADE) {

        joins {
            joining(COUNTERPARTY) {
                on(TRADE.COUNTERPARTY_ID to COUNTERPARTY { COUNTERPARTY_ID })
            }
            joining(INSTRUMENT) {
                on(TRADE.INSTRUMENT_ID to INSTRUMENT { INSTRUMENT_ID })
            }
        }

        fields {
            TRADE.allFields()

            COUNTERPARTY.COUNTERPARTY_NAME
            INSTRUMENT.INSTRUMENT_NAME
            INSTRUMENT.MARKET_ID withPrefix INSTRUMENT
            INSTRUMENT.CURRENCY_ID withAlias "CURRENCY"

            derivedField("CONSIDERATION", DOUBLE) {
                withInput(TRADE.QUANTITY, TRADE.PRICE) { QUANTITY, PRICE ->
                    QUANTITY * PRICE
                }
            }

            derivedField("TRADE_ASSET_CLASS", STRING) {
                withInput(INSTRUMENT.ASSET_CLASS) {ASSET_CLASS ->
                    if (ASSET_CLASS == null) {
                        "UNKOWN"
                    }
                    ASSET_CLASS
                }
            }
        }
    }
}
