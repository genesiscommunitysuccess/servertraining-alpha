streamerClients {
    streamerClient(clientName = "TRADE_AUDIT_RESPONSE") {
        dataSource(processName = "ALPHA_STREAMER", sourceName = "TRADE_AUDIT_STREAM")
        onMessage {
            send("GENESIS_COMPACT_PROCESS", "EVENT_TRADE_AUDIT_STREAM")
        }
    }
}