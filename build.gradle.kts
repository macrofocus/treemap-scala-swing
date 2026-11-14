plugins {
    scala
    application
}

tasks.compileJava {
    options.release.set(17)
}

// Gradle otherwise passes the version of the running JDK to -java-output-version, which Scala may not know yet
tasks.withType<ScalaCompile>().configureEach {
    targetCompatibility = "17"
}

repositories {
    maven { url = uri("https://maven.macrofocus.com/releases") }
    mavenCentral()

    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev/")

    google()
}

val frameworkAttribute = Attribute.of("mkui", String::class.java)
configurations.all {
    attributes.attribute(frameworkAttribute, "swing")
}

dependencies {
    implementation("org.scala-lang:scala3-library_3:3.10.1")
//    implementation("org.scala-lang:scala-library:2.13.12")

    val localDependencies = project.findProperty("localDependencies") as String?
    if(localDependencies != null && localDependencies.toBoolean()) {
        implementation(project(":macrofocus-common"))
        implementation(project(":molap"))
        implementation(project(":mkui"))
        implementation(project(":treemap"))
    } else {
        val macrofocusVersion = project.property("macrofocusVersion") as String
        implementation("org.macrofocus:macrofocus-common:$macrofocusVersion")
        implementation("org.molap:molap:$macrofocusVersion")
        implementation("org.mkui:mkui-swing:$macrofocusVersion")
        implementation("com.treemap:treemap-swing:$macrofocusVersion")
    }
}

application {
    mainClass.set("Demo")
}

distributions {
    main {
//        distributionBaseName.set("someName")
        contents {
            from(".") {
                exclude(".gradle/**")
                exclude("build/**")
                exclude("yarn.lock")
            }

//            from("TreeMap API for Scala-Swing Developer Guide.pdf")
//            from("../treemap/build/dokka/html/") {
//                into("dokka")
//            }
        }
    }
}