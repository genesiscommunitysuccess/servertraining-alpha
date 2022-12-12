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
            url = uri("https://genesisglobal.jfrog.io/genesisglobal/libs-release-client")
            credentials {
                username = extra.properties["genesisArtifactoryUser"].toString()
                password = extra.properties["genesisArtifactoryPassword"].toString()
            }
        }
    }
}
