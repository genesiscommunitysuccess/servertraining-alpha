plugins {
    distribution
}

dependencies {
    implementation(project(":alpha-config"))
    implementation(project(":alpha-dictionary-cache"))
    implementation(project(":alpha-eventhandler"))
    implementation(project(":alpha-messages"))
    implementation(project(":alpha-script-config"))
}

description = "alpha-distribution"

distributions {
    main {
        contents {
            // Octal conversion for file permissions
            val libPermissions = "600".toInt(8)
            val scriptPermissions = "700".toInt(8)
            into("alpha/bin") {
                from(configurations.runtimeClasspath)
                exclude("alpha-config*.jar")
                exclude("alpha-script-config*.jar")
                exclude("alpha-distribution*.jar")
                include("alpha-*.jar")
            }
            into("alpha/lib") {
                from("${project.rootProject.buildDir}/dependencies")
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE

                exclude("genesis-*.jar")
                exclude("alpha-*.jar")
                exclude("*.zip")

                fileMode = libPermissions
            }
            into("alpha/cfg") {
                from("${project.rootProject.projectDir}/alpha-config/src/main/resources/cfg")
                from(project.layout.buildDirectory.dir("generated/product-details"))
                filter(
                    org.apache.tools.ant.filters.FixCrLfFilter::class,
                    "eol" to org.apache.tools.ant.filters.FixCrLfFilter.CrLf.newInstance("lf")
                )
            }
            into("alpha/scripts") {
                from("${project.rootProject.projectDir}/alpha-config/src/main/resources/scripts")
                from("${project.rootProject.projectDir}/alpha-script-config/src/main/resources/scripts")
                filter(
                    org.apache.tools.ant.filters.FixCrLfFilter::class,
                    "eol" to org.apache.tools.ant.filters.FixCrLfFilter.CrLf.newInstance("lf")
                )
                fileMode = scriptPermissions
            }
            // Removes intermediate folder called with the same name as the zip archive.
            into("/")
        }
    }
}

val distribution by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

// To give custom name to the distribution package
tasks {
    distZip {
        archiveBaseName.set("genesisproduct-alpha")
        archiveClassifier.set("bin")
        archiveExtension.set("zip")
    }
    copyDependencies {
        enabled = false
    }
}

artifacts {
    val distzip = tasks.distZip.get()
    add("distribution", distzip.archiveFile) {
        builtBy(distzip)
    }
}

publishing {
    publications {
        create<MavenPublication>("alphaServerDistribution") {
            artifact(tasks.distZip.get())
        }
    }
}

description = "alpha-distribution"