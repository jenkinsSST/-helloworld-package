#!/usr/bin/env groovy

@Library('devops-shared-lib-for-testing') _
test.PipelineBuild.buildPipelineBuilder(this)
                  .buildStratOSPipeline()
                  .withStratOSPackage('helloworldObjectivePackage', true)
                  .build()
                  .execute()
