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

// Turns the TEST_IDS parameter ("TC-02, api-07") into a JUnit tag filter ("TC-02,API-07").
// ALL (any case) or an empty value means all tests and returns ''.
// Only letters, digits, hyphens and commas are allowed, so the value is safe in a shell.
def testIdsFilter() {
  String ids = (params.TEST_IDS ?: '').replaceAll(/\s/, '').toUpperCase()
  if (!(ids ==~ /[A-Z0-9,-]*/)) {
    error("TEST_IDS: use ALL or test IDs separated by commas, e.g. TC-02,API-07. Got: ${params.TEST_IDS}")
  }
  return (ids ==~ /(.*,)?ALL(,.*)?/) ? '' : ids
}

// Failed tests must not stop the pipeline: the junit step marks the stage and the build UNSTABLE.
// With TEST_IDS set, a stage that has none of the selected tests runs nothing and stays green.
// With REPORT_PORTAL checked, results also go to ReportPortal as one launch per stage, tagged
// with the build number and the stage name. The agent reads the key from RP_API_KEY.
def runTests(String filter, String stageName) {
  String ids = testIdsFilter()
  String groups = ids ? " -Dgroups=${ids}" : ''
  String args = "test -Dtest=\"${filter}\"${groups} -Dmaven.test.failure.ignore=true"
  if (params.REPORT_PORTAL) {
    withCredentials([string(credentialsId: 'rp-api-key', variable: 'RP_API_KEY')]) {
      String attributes = "build:${env.BUILD_NUMBER};stage:${stageName}"
      mvnw("${args} -Drp.enable=true \"-Drp.attributes=${attributes}\"")
    }
  } else {
    mvnw(args)
  }
}

pipeline {
  agent any

  parameters {
    string(
        name: 'TEST_IDS',
        defaultValue: 'ALL',
        trim: true,
        description: 'ALL runs every test. To run selected tests, enter their IDs separated by '
            + 'commas, e.g. TC-02,TC-04,API-07. IDs are listed in README.md.')
    booleanParam(
        name: 'REPORT_PORTAL',
        defaultValue: false,
        description: 'Also send results to ReportPortal (http://localhost:8090). '
            + 'Needs the Secret text credential rp-api-key.')
  }

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
        runTests('com.qa.automation.api.**', 'api')
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
        runTests('com.qa.automation.webui.**', 'ui')
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
