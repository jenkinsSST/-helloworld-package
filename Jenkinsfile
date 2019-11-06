#!/usr/bin/env groovy

// @Library('SeaStreetPipelineLibrary') _

@Library('devops-shared-lib-for-testing@mhyde-test-alterate-script-method') _

test.PipelineBuild.buildPipelineBuilder(this)
                  .buildStratOSPipeline()
                  .withStratOSPackage('helloworldObjectivePackage', true)
                  .build()
                  .execute()
