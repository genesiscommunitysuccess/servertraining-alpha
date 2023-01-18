dependencies {
    implementation("global.genesis:genesis-pal-execution")
    compileOnly("global.genesis:genesis-dictionary")
    api("global.genesis:genesis-pal-dataserver")
    api("global.genesis:genesis-pal-requestserver")
    api("global.genesis:genesis-pal-consolidator")
    api("global.genesis:genesis-pal-streamer")
    api("global.genesis:genesis-pal-streamerclient")
    api("global.genesis:genesis-pal-eventhandler")
    api("global.genesis:genesis-dataserver2")
    api(project(":alpha-messages"))
    api(project(":alpha-eventhandler"))
    compileOnly(project(path = ":alpha-dictionary-cache", configuration = "codeGen"))
    testCompileOnly(project(":alpha-config"))
    testImplementation("global.genesis:genesis-dbtest")
    testImplementation("global.genesis:genesis-testsupport")
    testImplementation("global.genesis:genesis-dataserver2")
    testImplementation("global.genesis:genesis-pal-dataserver")
    testImplementation(project(path = ":alpha-dictionary-cache", configuration = "codeGen"))
}

description = "alpha-script-config"
