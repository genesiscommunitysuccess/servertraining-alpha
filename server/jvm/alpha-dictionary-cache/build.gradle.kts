
// Add your genesis config dependencies here
dependencies {
    implementation(project(":alpha-dictionary-cache:alpha-generated-dao"))
    implementation(project(":alpha-dictionary-cache:alpha-generated-fields"))
    implementation(project(":alpha-dictionary-cache:alpha-generated-hft"))
    implementation(project(":alpha-dictionary-cache:alpha-generated-sysdef"))
    implementation(project(":alpha-dictionary-cache:alpha-generated-view"))

    implementation("global.genesis:auth-config:${properties["authVersion"]}")
}

description = "alpha-dictionary-cache"
