plugins {
    `java-library`
}

dependencies {
    implementation(libs.edc.spi.core)

    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    implementation(libs.identity.hub.spi)
    implementation(libs.participant.context.spi)
    implementation(libs.identityhub.api.authentication)

    testImplementation(libs.edc.junit)
}
