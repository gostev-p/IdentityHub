plugins {
    `java-library`
}

dependencies {
    implementation(libs.edc.spi.core)

    implementation(libs.identity.hub.spi)
    implementation(libs.participant.context.spi)
    implementation(libs.identityhub.api.authentication)

    testImplementation(libs.edc.junit)
}
