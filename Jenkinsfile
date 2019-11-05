#!/usr/bin/env groovy

@Library('SeaStreetPipelineLibrary@24592-use-only-one-node-for-execution') _
test.PipelineBuild.buildPipelineBuilder(this)
                  .buildStratOSPipeline()
                  .withStratOSPackage('helloworldObjectivePackage', true)
                  .build()
                  .execute()
