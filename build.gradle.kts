plugins {
    id("java")
    id("maven-publish")
}

group = "com.github.sniconmc.gandalf"
version = "0.1-dev"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("net.minestom:minestom-snapshots:65f75bb059") // Minestom
    implementation("com.github.SniconMC:Utils:0.1.7")
    implementation("com.github.SniconMC:Momentum:0.1.7")
    implementation("com.github.SniconMC:Sidebar:0.1.3")
    implementation("com.github.SniconMC:Container:0.1.6")
    implementation("ch.qos.logback:logback-classic:1.5.7") // Logback
    implementation("net.kyori:adventure-text-minimessage:4.17.0") // MiniMessage
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
    options.encoding = "UTF-8"
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom{
                name = "Gandalf"
                description = project.description
                url = "https://github.com/SniconMC/Gandalf"
                licenses {
                    license {
                        name = "The GNU Affero General Public License Version 3"
                        url = "https://www.gnu.org/licenses/agpl-3.0.txt"
                    }
                }
            }
        }
    }
}