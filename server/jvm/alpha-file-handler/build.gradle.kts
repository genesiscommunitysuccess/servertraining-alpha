dependencies {
    implementation("global.genesis:genesis-pal-execution")
    implementation("com.opencsv:opencsv:5.6")
    implementation("com.google.code.gson:gson:2.9.1")
    api("global.genesis:genesis-router")
    api("global.genesis:genesis-process")
    api("global.genesis:genesis-db")
    api(project(":alpha-eventhandler"))
    compileOnly(project(":alpha-config"))
    compileOnly(project(path = ":alpha-dictionary-cache", configuration = "codeGen"))
    testCompileOnly(project(":alpha-config"))
    testImplementation(project(":alpha-config"))
    testImplementation("global.genesis:genesis-dbtest")
    testImplementation("global.genesis:genesis-testsupport")
    testImplementation(project(path = ":alpha-dictionary-cache", configuration = "codeGen"))
}

description = "alpha-file-handler"
