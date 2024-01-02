plugins {
    id("java")
    id("dev.architectury.loom") version("1.1-SNAPSHOT")
    id("architectury-plugin") version("3.4-SNAPSHOT")
    kotlin("jvm") version ("1.8.20")
    kotlin("plugin.serialization") version "1.8.20"
}

group = property("maven_group")!!
version = property("mod_version")!!

architectury {
    platformSetupLoomIde()
    fabric()
}

repositories {
    mavenCentral()
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
    maven {
        url = uri("https://maven.saps.dev/releases")
        content {
            includeGroup("dev.latvian.mods")
            includeGroup("dev.ftb.mods")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    // Fabric API
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")

    // Fabric Kotlin
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

    // Architectury
    modImplementation("dev.architectury:architectury-fabric:${property("architectury_version")}")

    // Cobblemon
    modImplementation("com.cobblemon:fabric:${property("cobblemon_version")}")

    modApi("dev.ftb.mods:ftb-library-fabric:${property("ftb_library_version")}") {
        isTransitive = false
    }
    modApi("dev.ftb.mods:ftb-teams-fabric:${property("ftb_teams_version")}")
    modApi("dev.ftb.mods:ftb-quests-fabric:${property("ftb_quests_version")}")
    modApi("dev.latvian.mods:item-filters-fabric:${property("itemfilters_version")}")

    include(implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")!!)

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")

    compileOnly("net.luckperms:api:5.4")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()

    maxHeapSize = "1G"

    testLogging {
        events("passed")
    }
}