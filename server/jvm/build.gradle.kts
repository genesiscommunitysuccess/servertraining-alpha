ext.set("localDaogenVersion", "ALPHA")

plugins {
    kotlin("jvm") version "1.9.0"
    `maven-publish`
    id("global.genesis.build")
}

subprojects  {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.gradle.maven-publish")


    dependencies {
        implementation(platform("global.genesis:genesis-bom:${properties["genesisVersion"]}"))
        implementation("org.agrona:agrona:1.10.0!!")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.7.10")
        constraints {
            // define versions of your dependencies here so that submodules do not have to define versions
            testImplementation("junit:junit:4.13.2")
        }
    }
    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
            }
        }
        val java = "17"

        compileKotlin {
            kotlinOptions { jvmTarget = java }
        }

        //testing should use H2 mem db
        test {
            systemProperty("DbLayer", "SQL")
            systemProperty("DbHost", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
            systemProperty("DbQuotedIdentifiers", "true")
            useJUnitPlatform()

            // Add exports and opens so ChronicleQueue can continue working in JDK 17.
            // More info in: https://chronicle.software/chronicle-support-java-17/
            jvmArgs = jvmArgs!! + listOf(
                "--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED",
                "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
                "--add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED",
                "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
                "--add-opens=jdk.compiler/com.sun.tools.javac=ALL-UNNAMED",
                "--add-opens=java.base/java.lang=ALL-UNNAMED",
                "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
                "--add-opens=java.base/java.io=ALL-UNNAMED",
                "--add-opens=java.base/java.util=ALL-UNNAMED",
                "--add-opens=java.base/java.nio=ALL-UNNAMED" // this one is opened for LMDB
            )

        }

        afterEvaluate {
            val copyDependencies = tasks.findByName("copyDependencies") ?: return@afterEvaluate

            tasks.withType<Jar> {
                dependsOn(copyDependencies)
            }
        }
    }
}

tasks {
    assemble {
        for(subproject in subprojects){
            dependsOn(subproject.tasks.named("assemble"))
        }
        finalizedBy("copyUserNpmrc")
    }
    build {
        for(subproject in subprojects){
            dependsOn(subproject.tasks.named("build"))
        }
    }
    clean {
        for(subproject in subprojects){
            dependsOn(subproject.tasks.named("clean"))
        }
    }
    task("copyUserNpmrc") {
        copy {
            file(project.gradle.gradleUserHomeDir.parent).listFiles()
                ?.let { from(it.filter { it.name.equals(".npmrc") }).into("$projectDir/../../client") }
        }
    }
}

allprojects {

    group = "global.genesis"
    version = "1.0.0-SNAPSHOT"


    kotlin {
        jvmToolchain {
            (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
    tasks.withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
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
        maven {
            url = uri(properties["genesisArtifactoryPath"].toString())
            credentials {
                username = properties["genesisArtifactoryUser"].toString()
                password = properties["genesisArtifactoryPassword"].toString()
            }
        }
    }

    publishing {
        publications.create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
// testing
