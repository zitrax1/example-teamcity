package Netology.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object ExampleTeamcity_Build : BuildType({
    id = AbsoluteId("ExampleTeamcity_Build")
    name = "Build"

    artifactRules = "target/*.jar"

    vcs {
        root(AbsoluteId("ExampleTeamcity_HttpsGithubComAragastmatbExampleTeamcityGitRefsHeadsMaster"))
    }

    steps {
        maven {
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
        }
    }
})
