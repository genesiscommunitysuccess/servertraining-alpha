/**
 * System              : Genesis Business Library
 * Sub-System          : multi-pro-code-test Configuration
 * Version             : 1.0
 * Copyright           : (c) Genesis
 * Date                : 2022-03-18
 * Function : Provide system definition config for multi-pro-code-test.
 *
 * Modification History
 */
systemDefinition {
    global {
        item(name = "ADMIN_PERMISSION_ENTITY_TABLE", value = "COUNTERPARTY")
        item(name = "ADMIN_PERMISSION_ENTITY_FIELD", value = "COUNTERPARTY_ID")
        item(name = "NULLABILITY_FOR_TRADE_FIELDS", value = false)

        item(name = "SYSTEM_DEFAULT_USER_NAME", value = "" )
        item(name = "SYSTEM_DEFAULT_EMAIL", value = "notifications@freesmtpservers.com" )
        item(name = "EMAIL_SMTP_HOST", value = "smtp.freesmtpservers.com" )
        item(name = "EMAIL_SMTP_PORT", value = "25" )
        item(name = "EMAIL_SMTP_USER", value = "" )
        item(name = "EMAIL_SMTP_PW", value = "" )
        item(name = "EMAIL_SMTP_PROTOCOL", value = "SMTP")

        item(name = "SYS_DEF_FILE_MAX_SIZE_IN_BITS", value = "32000000")

        // SFTP
        item(name = "SFTP_PATH", value = "sftp:22/")
        item(name = "SFTP_USERNAME", value = "JohnDoe")
        item(name = "SFTP_PASSWORD", value = "Password11")
        item(name = "SFTP_DIRECTORY", value = "folder-inside-sftp")
        item(name = "SFTP_FILE", value = "from.txt")
    }

    systems {

    }

}