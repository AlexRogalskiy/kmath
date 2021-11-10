plugins {
    id("ru.mipt.npm.gradle.jvm")
}

description = "Google tensorflow connector"

dependencies {
    api(project(":kmath-tensors"))
    api("org.tensorflow:tensorflow-core-api:0.3.3")
    testImplementation("org.tensorflow:tensorflow-core-platform:0.3.3")
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}