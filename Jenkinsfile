#!/usr/bin/env groovy

@Library('devops-shared-lib-for-testing') _
test.PipelineBuild.buildPipelineBuilder(this)
                  .buildStratOSPipeline()
                  .withStratOSPackage('helloworldObjectivePackage', true)
                  .withStratOSTests('helloworldObjectiveTests', 'JiraRealGearTestSuite', true)
                  .withStratOSServer('2.5', '10.70.0.251', 'admin', 'admin')
                  .djv_build()
                  .execute()
