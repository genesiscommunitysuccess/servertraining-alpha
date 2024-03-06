package scripts

notify {
    gateways {
        email(id = "email") {
            smtpHost = systemDefinition["EMAIL_SMTP_HOST"].get()
            smtpPort = systemDefinition["EMAIL_SMTP_PORT"].get().toInt()
            smtpUser = systemDefinition["EMAIL_SMTP_USER"].get()
            smtpPw = systemDefinition["EMAIL_SMTP_PW"].get()
            smtpProtocol = TransportStrategy.SMTP_TLS
            systemDefaultUserName = systemDefinition["SYSTEM_DEFAULT_USER_NAME"].get()
            systemDefaultEmail = systemDefinition["SYSTEM_DEFAULT_EMAIL"].get()
        }
    }
}