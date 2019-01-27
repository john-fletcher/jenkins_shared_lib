#!/usr/bin/env groovy


def call(body){
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.deligate = config
  body()

  node{
    //clean workspace befoe doing anything
    deleteDir()

    try{
      stage('clone'){
        checkout scm
      }
      stage('Build'){
        def shell(command) {
            return bat(returnStdout: true, script: "sh -x -c \"${command}\"").trim()
        }
        sh "echo 'building ${config.projectName} ...'"
      }
      stage('Tests'){
        parall 'static': {
          sh "echo 'shell scripts to run static tests ...'"
        },
        'unit': {
          sh "echo 'shell scripts to run unit tests ...'"
        },
        'integration': {
          sh "echo 'shell scripts to run integration tests ...'"
        }
      }
      stage('Deploy'){
        sh "echo 'deploying to server ${config.serverDomain}...'"
      }
    } catch(err){
      currentBuild.result = 'FAILED'
      throw err
    }

  }
}
