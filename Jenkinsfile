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
// extraArgs come only from choice and boolean parameters, so they are safe in a shell.
def runTests(String filter, String stageName, String extraArgs = '') {
  String ids = testIdsFilter()
  String groups = ids ? " -Dgroups=${ids}" : ''
  String args = "test -Dtest=\"${filter}\"${groups} -Dmaven.test.failure.ignore=true${extraArgs}"
  if (params.REPORT_PORTAL) {
    withCredentials([string(credentialsId: 'rp-api-key', variable: 'RP_API_KEY')]) {
      String attributes = "build:${env.BUILD_NUMBER};stage:${stageName}"
      if (stageName == 'ui') {
        attributes += ";browser:${browser()}"
      }
      mvnw("${args} -Drp.enable=true \"-Drp.attributes=${attributes}\"")
    }
  } else {
    mvnw(args)
  }
}

// Parameters are empty in the first build after they are added, so each one has a default here.
def suite() {
  return params.SUITE ?: 'ALL'
}

def browser() {
  return params.BROWSER ?: 'chrome'
}

// Selenium Grid from docker/selenium-grid, on the same machine as Jenkins.
def uiArgs() {
  String args = " -Dbrowser=${browser()}"
  return params.GRID ? "${args} -Dgrid.url=http://localhost:4444" : args
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
    choice(
        name: 'SUITE',
        choices: ['ALL', 'API', 'UI'],
        description: 'Which test stages run. TEST_IDS still filters the tests inside them.')
    choice(
        name: 'BROWSER',
        choices: ['chrome', 'edge', 'firefox'],
        description: 'Browser for the UI tests.')
    booleanParam(
        name: 'GRID',
        defaultValue: false,
        description: 'Run the UI tests on Selenium Grid (docker/selenium-grid) instead of a '
            + 'headless browser on the Jenkins machine. Watch them at http://localhost:7900 '
            + '(Chrome), 7901 (Edge) or 7902 (Firefox).')
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
    // Config key "remote": a Jenkins service has no desktop, so a local browser must run
    // headless. Grid browsers ignore it: they have their own screen.
    REMOTE = 'true'
  }

  stages {
    stage('Compile') {
      steps {
        script {
          // Shown next to the build number in the build history.
          String where = params.GRID ? ' on Grid' : ''
          String ids = params.TEST_IDS ?: 'ALL'
          currentBuild.description = "${suite()} | ${browser()}${where} | ${ids}"
        }
        mvnw('clean test-compile')
      }
    }

    stage('API tests') {
      when {
        expression { suite() in ['ALL', 'API'] }
      }
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
      when {
        expression { suite() in ['ALL', 'UI'] }
      }
      steps {
        runTests('com.qa.automation.webui.**', 'ui', uiArgs())
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
