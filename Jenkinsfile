// Compiles, runs API and UI tests as separate stages, then publishes JUnit and Allure results.
// Jenkins setup is described in README.md, section "Jenkins".

// Runs the Maven Wrapper on Linux and Windows agents.
def mvnw(String args) {
  if (isUnix()) {
    sh "./mvnw -B ${args}"
  } else {
    bat "mvnw.cmd -B ${args}"
  }
}

// Failed tests must not stop the pipeline: the junit step marks the stage and the build UNSTABLE.
def runTests(String filter) {
  mvnw("test -Dtest=\"${filter}\" -Dmaven.test.failure.ignore=true")
}

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
    stage('Compile') {
      steps {
        mvnw('clean test-compile')
      }
    }

    stage('API tests') {
      steps {
        runTests('com.qa.automation.api.**')
      }
      post {
        always {
          junit testResults: 'target/surefire-reports/TEST-com.qa.automation.api.*.xml',
              allowEmptyResults: true
        }
      }
    }

    stage('UI tests') {
      steps {
        runTests('com.qa.automation.webui.**')
      }
      post {
        always {
          junit testResults: 'target/surefire-reports/TEST-com.qa.automation.webui.*.xml',
              allowEmptyResults: true
        }
      }
    }
  }

  post {
    always {
      // One Allure report for both stages: results accumulate in target/allure-results.
      allure results: [[path: 'target/allure-results']]
      archiveArtifacts artifacts: 'target/screenshots/**', allowEmptyArchive: true
    }
  }
}
