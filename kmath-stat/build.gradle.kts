plugins {
    id("ru.mipt.npm.gradle.mpp")
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-coroutines"))
        }
    }

    jvmMain {
        dependencies {
            api("org.apache.commons:commons-rng-sampling:1.3")
            api("org.apache.commons:commons-rng-simple:1.3")
        }
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.EXPERIMENTAL
}