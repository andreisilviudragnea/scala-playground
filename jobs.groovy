// The complete DSL API reference is available in your Jenkins installation at
// https://your.jenkins.installation/plugin/job-dsl/api-viewer/index.html

def multiBranchJob(String name, String owner, String repo, String path) {
    multibranchPipelineJob(name) {
        branchSources {
            branchSource {
                buildStrategies {
                    skipInitialBuildOnFirstBranchIndexing()
                }
                source {
                    github {
                        id("$name-23232323") // IMPORTANT: use a constant and unique identifier
                        credentialsId('<CREDENTIALS_ID>')
                        repoOwner(owner)
                        repository(repo)
                        apiUri('<GITHUB_URL>/api/v3')
                        configuredByUrl(true)
                        repositoryUrl("<GITHUB_URL>/$owner/$repo")
                        traits {
                            gitHubBranchDiscovery {
                                strategyId(3)
                            }
                            cleanAfterCheckoutTrait {
                                extension {
                                    deleteUntrackedNestedRepositories(true)
                                }
                            }
                            cleanBeforeCheckoutTrait {
                                extension {
                                    deleteUntrackedNestedRepositories(true)
                                }
                            }
                            pruneStaleBranchTrait()
                        }
                    }
                }
            }
        }
        factory {
            workflowBranchProjectFactory {
                scriptPath(path)
            }
        }
        triggers {
            periodicFolderTrigger {
                interval('1d')
            }
        }
        orphanedItemStrategy {
            discardOldItems()
        }
    }
}

def pipeline(String name, String uri, String branchName, String path, Closure closure = {}) {
    pipelineJob(name) {
        definition {
            cpsScm {
                lightweight(true)
                scm {
                    git {
                        branch(branchName)
                        remote {
                            credentials('<CREDENTIALS_ID>')
                            url(uri)
                        }
                    }
                }
                scriptPath(path)
            }
        }
        closure.delegate = it
        closure()
    }
}

Closure configureTriggers(String periodSpec) {
    { it ->
        properties {
            pipelineTriggers {
                triggers {
                    cron {
                        spec(periodSpec)
                    }
                    githubPush()
                }
            }
        }
    }
}

folder('scala-playground')

multiBranchJob('jobs/ci', 'dragnea', 'scala-playground', '<PATH_TO_JENKINSFILE>')
pipeline(
        'jobs/pipeline-example',
        'https://github.com/andreisilviudragnea/scala-playground.git',
        '*/main',
        '<PATH_TO_JENKINSFILE>',
        configureTriggers('@daily')
)
