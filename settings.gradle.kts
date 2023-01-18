rootProject.name = "alpha"

// servers
includeBuild("server/jvm") {
    name = "genesisproduct-alpha"
}

// clients
includeBuild("client")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri(extra.properties["genesisArtifactoryPath"].toString())
            credentials {
                username = extra.properties["genesisArtifactoryUser"].toString()
                password = extra.properties["genesisArtifactoryPassword"].toString()
            }
        }
    }
}
