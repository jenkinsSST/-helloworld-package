#!/usr/bin/env groovy

@Library('SeaStreetPipelineLibrary@24592-use-only-one-node-for-execution') _

//@Library('devops-shared-lib-for-testing@mhyde-test-alterate-script-method') _

dtest.PipelineBuild.buildPipelineBuilder(this)
                  .buildStratOSPipeline()
                  .withStratOSPackage('helloworldObjectivePackage', true)
                  .build()
                  .execute()
