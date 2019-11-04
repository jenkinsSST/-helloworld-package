#!/usr/bin/env groovy

@Library('SeaStreetPipelineLibraryx') _
test.PipelineBuild.buildPipelineBuilder(this)
                  .buildStratOSPipeline()
                  .withStratOSPackage('helloworldObjectivePackage', true)
                  .build()
                  .execute()
