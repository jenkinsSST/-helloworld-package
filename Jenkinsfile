#!/usr/bin/env groovy

@Library('SeaStreetPipelineLibrary') _
test.PipelineBuild.buildPipelineBuilder(this)
                  .buildStratOSPipeline()
                  .withStratOSPackage('helloworldObjectivePackage', true)
                  .build()
                  .execute()
