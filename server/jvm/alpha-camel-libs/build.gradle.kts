dependencies {
    implementation("global.genesis:genesis-pal-execution")
    implementation("global.genesis:genesis-pal-camel")
    implementation("global.genesis:genesis-eventhandler")
    implementation(project(":alpha-messages"))
    api("global.genesis:genesis-db")
    api("org.apache.camel:camel-stream:3.19.0")
    api("org.apache.camel:camel-http:3.19.0")
    api("org.apache.camel:camel-management:3.19.0")
    api("org.apache.camel:camel-console:3.19.0")
    api("org.apache.camel:camel-cli-connector:3.19.0")
    api("org.apache.camel:camel-timer:3.19.0")
    api("org.apache.camel:camel-ftp:3.19.0")
    compileOnly(project(":alpha-config"))
    compileOnly(project(path = ":alpha-dictionary-cache", configuration = "codeGen"))
    testImplementation("global.genesis:genesis-dbtest")
    testImplementation("global.genesis:genesis-testsupport")
    testImplementation(project(path = ":alpha-dictionary-cache", configuration = "codeGen"))
}

description = "alpha-camel-libs"
