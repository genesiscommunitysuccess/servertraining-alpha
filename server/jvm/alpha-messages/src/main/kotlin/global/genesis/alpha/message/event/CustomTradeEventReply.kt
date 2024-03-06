package global.genesis.alpha.message.event

import global.genesis.message.core.Outbound

sealed class CustomTradeEventReply : Outbound() {

    class ValidationTradeAck() : CustomTradeEventReply()

    data class TradeAck(val AckMessage : String) : CustomTradeEventReply()
    data class TradeNack(val NackMessage : String) : CustomTradeEventReply()

}