// Runs the whole suite and publishes JUnit and Allure results.
// Jenkins setup is described in README.md, section "Jenkins".
pipeline {
  agent any

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }

  environment {
    // Config reads TEST_USER_EMAIL / TEST_USER_PASSWORD; Jenkins masks them in the log.
    TEST_USER_EMAIL = credentials('test-user-email')
    TEST_USER_PASSWORD = credentials('test-user-password')
    // Config key "remote": a Jenkins service has no desktop, so Chrome must run headless.
    REMOTE = 'true'
  }

  stages {
    stage('Test') {
      steps {
        script {
          // Failed tests mark the build UNSTABLE (via junit below) instead of stopping the pipeline.
          def mvn = 'clean test -B -Dmaven.test.failure.ignore=true'
          if (isUnix()) {
            sh "./mvnw ${mvn}"
          } else {
            bat "mvnw.cmd ${mvn}"
          }
        }
      }
    }
  }

  post {
    always {
      junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
      allure results: [[path: 'target/allure-results']]
      archiveArtifacts artifacts: 'target/screenshots/**', allowEmptyArchive: true
    }
  }
}
