plugins {
    `java-library`
}

dependencies {
    // das hast du schon im Catalog
    implementation(libs.edc.spi.core)

    // die drei hier kennt dein Catalog noch nicht, drum direkt per String:
    implementation(libs.identity.hub.spi)
    implementation(libs.participant.context.spi)
    implementation(libs.identityhub.api.authentication)

    testImplementation(libs.edc.junit)
}
