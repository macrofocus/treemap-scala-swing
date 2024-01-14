plugins {
    scala
    application
}

tasks.compileJava {
    options.release.set(11)
}

repositories {
    maven { url = uri("https://www.macrofocus.com/archiva/repository/public/") }
    maven { url = uri("https://www.macrofocus.com/archiva/repository/snapshots/") }
    mavenCentral()

    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev/")

    google()
}

val frameworkAttribute = Attribute.of("mkui", String::class.java)
configurations.all {
    afterEvaluate {
        attributes.attribute(frameworkAttribute, "swing")
    }
}

dependencies {
    implementation("org.scala-lang:scala3-library_3:3.3.1")
//    implementation("org.scala-lang:scala-library:2.13.12")

    val localDependencies: String? by project
    if (localDependencies != null && localDependencies.toBoolean()) {
        implementation(project(":macrofocus-common"))
        implementation(project(":molap"))
        implementation(project(":mkui"))
        implementation(project(":treemap"))
    } else {
        val macrofocusVersion: String by project
        implementation("org.macrofocus:macrofocus-common:$macrofocusVersion")
        implementation("org.molap:molap:$macrofocusVersion")
        implementation("org.mkui:mkui:$macrofocusVersion")
        implementation("com.treemap:treemap:$macrofocusVersion")
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
                exclude("build/**")
                exclude("yarn.lock")
            }

//            from("TreeMap API for Scala-Swing Developer Guide.pdf")
            from("../treemap/build/dokka/html/") {
                into("dokka")
            }
        }
    }
}