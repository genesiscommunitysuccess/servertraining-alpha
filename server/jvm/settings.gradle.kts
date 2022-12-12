rootProject.name = "genesisproduct-alpha"

pluginManagement {
    pluginManagement {
        val genesisVersion: String by settings
        val deployPluginVersion: String by settings
        plugins {
            id("global.genesis.build") version genesisVersion
            id("global.genesis.deploy") version deployPluginVersion
        }
    }
    repositories {
        mavenLocal {
            // VERY IMPORTANT!!! EXCLUDE AGRONA AS IT IS A POM DEPENDENCY AND DOES NOT PLAY NICELY WITH MAVEN LOCAL!
            content {
                excludeGroup("org.agrona")
            }
        }
        mavenCentral()
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



include("alpha-config")
include("alpha-messages")
include("alpha-eventhandler")
include("alpha-script-config")
include("alpha-distribution")
include("alpha-dictionary-cache")
include("alpha-dictionary-cache:alpha-generated-sysdef")
include("alpha-dictionary-cache:alpha-generated-fields")
include("alpha-dictionary-cache:alpha-generated-dao")
include("alpha-dictionary-cache:alpha-generated-hft")
include("alpha-dictionary-cache:alpha-generated-view")
include("alpha-deploy")
include("alpha-site-specific")

includeBuild("../../client") {
    dependencySubstitution {
        substitute(module("client:web"))
            .using(project(":web"))
    }
}
