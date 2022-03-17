import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2021.2"

project {

    buildType(Build)
    buildType(Sample)

    params {
        text("name", "Alexey", readOnly = true, allowEmpty = true)
        param("system.sample", "123")
    }
}

object Build : BuildType({
    name = "Build"

    artifactRules = "+:target/*.jar"

    params {
        param("env.name2", "%name%")
        password("env.out", "credentialsJSON:12f2c387-f043-4357-8d37-8e00390454d0")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "run clean test"
            executionMode = BuildStep.ExecutionMode.ALWAYS

            conditions {
                doesNotContain("teamcity.build.branch", "master")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        maven {
            name = "make distrib"
            executionMode = BuildStep.ExecutionMode.ALWAYS

            conditions {
                contains("teamcity.build.branch", "master")
            }
            goals = "clean deploy"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "netology"
        }
    }

    triggers {
        vcs {
        }
    }

    requirements {
        exists("env.JAVA_HOME")
    }
})

object Sample : BuildType({
    name = "Sample"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "run clean test"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }
})
