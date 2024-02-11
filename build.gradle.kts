import org.checkerframework.gradle.plugin.CheckerFrameworkExtension
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.javamodularity.moduleplugin.extensions.ModularityExtension

//import org.javamodularity.moduleplugin.extensions.ModularityExtension

// Tip: Use `apply false` in the top-level build.gradle file to add a Gradle
// plugin as a build dependency but not apply it to the current (root) project.
// Don't use `apply false` in sub-projects.
//
// For more information, see the following on applying external plugins with
// same version to subprojects:
//
// - https://docs.gradle.org/current/userguide/dependency_management_basics.html
// - https://docs.gradle.org/current/samples/sample_building_java_applications_multi_project.html
// - https://docs.gradle.org/current/dsl/org.gradle.plugin.use.PluginDependenciesSpec.html#org.gradle.plugin.use.PluginDependenciesSpec:id(java.lang.String)
plugins {
    id("java")

    // Code Coverage
    id("jacoco")

    // Only see code converage on new/modified code
    // TODO: Add configuration
    id("io.github.surpsg.delta-coverage") version "latest.release"

    // Check styles
    id("checkstyle")

    // Because the project has a `sourceSet` is apply java/java-library plugin
    // as well, don't append `apply false`. If root project doesn't have
    // `sourceSet`, we need to append `apply false`.
    id("com.github.andygoossens.modernizer") version "latest.release"

    // Code Formatter
    id("com.diffplug.spotless") version "latest.release"

    // Enhanced type checking and verification
    id("org.checkerframework") version "latest.release"

    // AST-based plugin for static code analysis
    id("pmd")

    // AST-based plugin for bug finding
    id("net.ltgt.errorprone") version "latest.release"

    // Bytecode-based plugin for bug finding
    id("com.github.spotbugs") version "latest.release"

    // Support Java Platform Module System (JPMS) for Java 8 releases
    id("org.javamodularity.moduleplugin") version "latest.release"
}

group = "com.rainerhahnekamp.sneakythrow"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.toString()
    options.compilerArgs.addAll(listOf(
        "-Xlint:all",
        "-Xlint:-options",
        "-Werror"
    ))
}

dependencies {
    ////////////////////////////////////////////////////////////////////////////
    //                          Dependency Injection                          //
    ////////////////////////////////////////////////////////////////////////////
    // dependency injection
    val daggerVersion = "latest.release"
    implementation("com.google.dagger", "dagger", daggerVersion)
    annotationProcessor("com.google.dagger", "dagger-compiler", daggerVersion)
    compileOnly("jakarta.annotation", "jakarta.annotation-api", "latest.release")

    ////////////////////////////////////////////////////////////////////////////
    //                                Logging                                 //
    ////////////////////////////////////////////////////////////////////////////
    implementation(platform("org.apache.logging.log4j:log4j-bom:latest.release"))
    implementation("org.apache.logging.log4j", "log4j-api")
    runtimeOnly("org.apache.logging.log4j", "log4j-core")

    ////////////////////////////////////////////////////////////////////////////
    //                              Unit Testing                              //
    ////////////////////////////////////////////////////////////////////////////
    // Unit Test Framework
    //
    // Useful info:
    // - https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-display-names
    testImplementation(platform("org.junit:junit-bom:latest.release"))
    testImplementation("org.junit.jupiter", "junit-jupiter")

    // Unit Test Assertion library
    testImplementation("org.assertj", "assertj-core", "latest.release")

    // Unit Test Mocking Framework
    testImplementation(platform("org.mockito:mockito-bom:latest.release"))
    testImplementation("org.mockito", "mockito-core")
    testImplementation("org.mockito", "mockito-junit-jupiter")

    ////////////////////////////////////////////////////////////////////////////
    //                             Error Checking                             //
    ////////////////////////////////////////////////////////////////////////////
    // Checker framework annotations
    //
    // For late initialization similar to kotlin's `lateinit`, use
    // `@MonotonicNonNull`
    //
    // Use `org.jspecify.annotations.Nullable` and
    // `org.jspecify.annotations.NonNull` instead of checker framework's
    // equivalents
    compileOnly("org.checkerframework", "checker-qual", "latest.release")

    // Null checking annotation to use with checker framework
    //
    // Instead of checker framework's `Nullable`/`NonNull`,
    // use jspecify's `Nullable`/`NonNull`
    compileOnly("org.jspecify", "jspecify", "latest.release")

    // Google error-prone annotations to work with the AST compiler plugin
    //
    // Useful annotations:
    // - @Immutable
    // - @Var
    val errorproneVersion = "latest.release"
    errorprone("com.google.errorprone", "error_prone_core", errorproneVersion)
    compileOnly("com.google.errorprone", "error_prone_annotations", errorproneVersion)

    ////////////////////////////////////////////////////////////////////////////
    //                                 Other                                  //
    ////////////////////////////////////////////////////////////////////////////
    // Utility Libraries/Frameworks
    implementation("com.google.guava", "guava", "latest.release")
    implementation("org.apache.commons", "commons-lang3", "latest.release")
    implementation("io.vavr", "vavr", "1.0.0-alpha-4")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
}

spotless {
    // optional: limit format enforcement to just the files changed by this
    // feature branch
    // ratchetFrom("origin/main")

    format("misc") {
        // define the files to apply `misc` to
        target("*.gradle.kts", ".gitattributes", ".gitignore")

        // define the steps to apply to those files
        trimTrailingWhitespace()
        indentWithSpaces(4)
        endWithNewline()
    }
    java {
        // don't need to set target, it is inferred from java

        // Automatic code refactoring with Cleanthat. In the future, may replace or
        // also include eclipse clean up refactoring tool. See:
        //
        // - https://github.com/solven-eu/cleanthat/blob/master/MUTATORS.generated.MD
        // - https://github.com/solven-eu/cleanthat/blob/master/MUTATORS_BY_TAG.generated.MD
        cleanthat()
            // Java 17 is the latest defined version. See:
            //
            // Tracking:  https://github.com/solven-eu/cleanthat/blob/master/refactorer/src/main/java/eu/solven/cleanthat/engine/java/IJdkVersionConstants.java#L60
            // Permalink: https://github.com/solven-eu/cleanthat/blob/c97ded164de19cc1c90bd3a9162ed794dae22ee9/refactorer/src/main/java/eu/solven/cleanthat/engine/java/IJdkVersionConstants.java#L60
            .sourceCompatibility("17")
            .includeDraft(true)
            .addMutator("SafeAndConsensual")
            .addMutator("SafeButControversial")
            .addMutator("SafeButNotConsensual")
            .addMutator("CheckStyleMutators")
            .addMutator("ErrorProneMutators")
            .addMutator("Guava")
            .addMutator("JSparrowMutators")
            .addMutator("PMDMutators")
            .addMutator("SonarMutators")
            .addMutator("SpotBugsMutators")
            .addMutator("Stream")
            .excludeMutator("AvoidInlineConditionals")
            .excludeMutator("CreateTempFilesUsingNio")
            .excludeMutator("LiteralsFirstInComparisons")
            .excludeMutator("RemoveExplicitCallToSuper")
            .excludeMutator("SimplifyBooleanExpression")
            .excludeMutator("SimplifyStartsWith")

        // Run eclipse code formatter profile
        eclipse("4.30")
            .configFile(".config/eclipse-code-formatter-profile.xml")

        removeUnusedImports()

        // import order file as exported from eclipse. See:
        //
        // https://github.com/diffplug/spotless/blob/main/ECLIPSE_SCREENSHOTS.md#creating-spotlessimportorder
        importOrderFile(".config/eclipse-import-order.importorder")

        trimTrailingWhitespace()
        endWithNewline()
    }
}

configure<CheckerFrameworkExtension> {
    checkers = listOf(
        "org.checkerframework.checker.nullness.NullnessChecker"
    )
    extraJavacArgs = listOf(
        "--module-path",
        configurations.compileClasspath.get().asPath,
    )
}

//configureigure<ModularityExtension> {
configure<ModularityExtension> {
    mixedJavaRelease(8)
}

tasks.jacocoTestReport {
    // tests are required to run before generating the report
    dependsOn(tasks.test)

    reports {
        xml.required = false
        csv.required = false
    }
}