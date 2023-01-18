dependencies {
    implementation("global.genesis:genesis-messages")
    compileOnly(project(path = ":alpha-dictionary-cache", configuration = "codeGen"))
}

description = "alpha-messages"