streams {
    stream("TRADE_AUDIT_STREAM", TRADE_AUDIT.BY_TIMESTAMP){
        where { trades->
            trades.quantity!! > 100
        }
        fields{
            TRADE.TRADE_ID

        }
    }
}
