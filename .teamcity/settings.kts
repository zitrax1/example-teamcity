import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.python
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

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

version = "2020.2"

project {

    vcsRoot(HttpsGithubComAragastmatbExampleTeamcityGitRefsHeadsMaster)

    buildType(Build)
    buildType(MyNewBuild)

    template(id123)

    params {
        text("name", "", allowEmpty = false)
        text("env.name", "%name%", display = ParameterDisplay.HIDDEN, allowEmpty = false)
    }
}

object Build : BuildType({
    name = "Build"

    artifactRules = "target/*.jar => target"
    publishArtifacts = PublishMode.SUCCESSFUL

    vcs {
        root(HttpsGithubComAragastmatbExampleTeamcityGitRefsHeadsMaster)
    }

    steps {
        maven {
            name = "Package"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE

            conditions {
                contains("teamcity.build.branch", "master")
            }
            goals = "package"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        maven {
            name = "Build & Test"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE

            conditions {
                doesNotContain("teamcity.build.branch", "master")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
        }
    }
})

object MyNewBuild : BuildType({
    name = "My New Build"

    params {
        text("env.some", "one", display = ParameterDisplay.HIDDEN, readOnly = true, allowEmpty = true)
    }

    steps {
        python {
            name = "Get version"
            command = custom {
                arguments = "-V"
            }
        }
        script {
            name = "Get Environment"
            scriptContent = """
                echo ${'$'}name
                echo ${'$'}some
            """.trimIndent()
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "MAcos", "RQ_1")
    }
    
    disableSettings("RQ_1")
})

object id123 : Template({
    id("123")
    name = "123"

    artifactRules = "target/*.jar => target"
    publishArtifacts = PublishMode.SUCCESSFUL

    vcs {
        root(HttpsGithubComAragastmatbExampleTeamcityGitRefsHeadsMaster)
    }

    steps {
        maven {
            name = "Package"
            id = "RUNNER_3"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE

            conditions {
                contains("teamcity.build.branch", "master")
            }
            goals = "package"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        maven {
            name = "Build & Test"
            id = "RUNNER_4"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE

            conditions {
                doesNotContain("teamcity.build.branch", "master")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
            id = "vcsTrigger"
        }
    }
})

object HttpsGithubComAragastmatbExampleTeamcityGitRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/aragastmatb/example-teamcity.git#refs/heads/master"
    url = "https://github.com/aragastmatb/example-teamcity.git"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "aragastmatb"
        password = "credentialsJSON:324ac48d-364a-448d-9ba5-b42efb076fe6"
    }
})
