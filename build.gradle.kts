plugins {
    //id("ru.mipt.npm.publish") apply false
    id("ru.mipt.npm.project")
}

val kmathVersion by extra("0.2.0-dev-1")
val bintrayRepo by extra("kscience")
val githubProject by extra("kmath")

allprojects {
    repositories {
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlinx")
        maven("https://dl.bintray.com/hotkeytlt/maven")
    }

    group = "kscience.kmath"
    version = kmathVersion
}

subprojects {
    if (name.startsWith("kmath")) apply<ru.mipt.npm.gradle.KSciencePublishPlugin>()
}

readme{
    readmeTemplate = file("docs/template/README-TEMPLATE.md")
}