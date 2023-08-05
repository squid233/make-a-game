plugins {
    id("java")
}

val overrunglVersion = "0.1.0-SNAPSHOT"
val jomlVersion = "1.10.5"

val overrunglNatives = Pair(
    System.getProperty("os.name")!!,
    System.getProperty("os.arch")!!
).let { (name, arch) ->
    when {
        arrayOf("Linux", "FreeBSD", "SunOS", "Unit").any { name.startsWith(it) } ->
            if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
                "natives-linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
            else "natives-linux"
        arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) } ->
            "natives-macos${if (arch.startsWith("aarch64")) "-arm64" else ""}"
        arrayOf("Windows").any { name.startsWith(it) } ->
            if (arch.contains("64"))
                "natives-windows${if (arch.startsWith("aarch64")) "-arm64" else ""}"
            else throw Error("Unrecognized or unsupported architecture. Please set \"overrunglNatives\" manually")
        else -> throw Error("Unrecognized or unsupported platform. Please set \"overrunglNatives\" manually")
    }
}

group = "io.github.squid233"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
}

dependencies {
    implementation(platform("io.github.over-run:utilities:0.+"))
    implementation("io.github.over-run:bin-packing")
    implementation("io.github.over-run:timer")

    implementation(platform("io.github.over-run:overrungl-bom:$overrunglVersion"))
    implementation("io.github.over-run:overrungl")
    implementation("io.github.over-run:overrungl-glfw")
    runtimeOnly("io.github.over-run:overrungl-glfw::$overrunglNatives")
    implementation("io.github.over-run:overrungl-opengl")
    implementation("io.github.over-run:overrungl-stb")
    runtimeOnly("io.github.over-run:overrungl-stb::$overrunglNatives")
    implementation("io.github.over-run:overrungl-joml")
    implementation("org.joml:joml:$jomlVersion")
}

// Configure JDK
val targetJavaVersion = 21
val enablePreview = true

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    if (enablePreview) options.compilerArgs.add("--enable-preview")
    options.release.set(targetJavaVersion)
}

extensions.configure<JavaPluginExtension>("java") {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}
