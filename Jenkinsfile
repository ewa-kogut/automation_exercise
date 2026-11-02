// Compiles, runs API and UI tests as separate stages, then publishes JUnit and Allure results
// and Plot charts.
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
// THREADS and extraArgs come only from choice and boolean parameters, so they are safe in a shell.
def runTests(String filter, String stageName, String extraArgs = '') {
  long start = System.currentTimeMillis()
  String ids = testIdsFilter()
  String groups = ids ? " -Dgroups=${ids}" : ''
  String threads = " -Dthreads=${params.THREADS ?: '1'}"
  String args =
      "test -Dtest=\"${filter}\"${groups}${threads} -Dmaven.test.failure.ignore=true${extraArgs}"
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
  long seconds = (System.currentTimeMillis() - start) / 1000
  writePlotCsv("${stageName}-duration", stageName.toUpperCase(), "${seconds}")
}

// Publishes the stage's JUnit results and saves its test counts for the Plot charts.
def recordResults(String stageName, String reports) {
  def summary = junit testResults: reports, allowEmptyResults: true
  // Plot colours series by their order (red, green, blue), so Failed comes first.
  writePlotCsv(
      "${stageName}-tests",
      'Failed,Passed,Skipped',
      "${summary.failCount},${summary.passCount},${summary.skipCount}")
}

// Plot plugin input: the first line holds the series names, the second this build's values.
// Each build adds one point per series. Files go to target/plot, which Compile cleans.
def writePlotCsv(String name, String header, String values) {
  writeFile file: "target/plot/${name}.csv", text: "${header}\n${values}\n"
}

def csvSeries(String name) {
  return [
      file: "target/plot/${name}.csv",
      url: '',
      inclusionFlag: 'OFF',
      exclusionValues: '',
      displayTableFlag: false]
}

// Charts on the job page (Plots). Runs only for full runs (TEST_IDS = ALL), so every point
// counts the same tests. A series whose stage was skipped (SUITE) gets no point.
def publishPlots() {
  plot group: 'Test results', title: 'API tests', style: 'line', yaxis: 'tests',
      numBuilds: '30', csvFileName: 'plot-api-test-counts.csv', csvSeries: [csvSeries('api-tests')]
  plot group: 'Test results', title: 'UI tests', style: 'line', yaxis: 'tests',
      numBuilds: '30', csvFileName: 'plot-ui-test-counts.csv', csvSeries: [csvSeries('ui-tests')]
  plot group: 'Test results', title: 'Stage duration', style: 'line', yaxis: 'seconds',
      numBuilds: '30', csvFileName: 'plot-duration.csv',
      csvSeries: [csvSeries('api-duration'), csvSeries('ui-duration')]
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
        name: 'THREADS',
        choices: ['1', '2', '3', '4'],
        description: 'How many tests run at the same time, each in its own browser. '
            + 'On Grid each browser node takes up to 3.')
    choice(
        name: 'BROWSER',
        choices: ['chrome', 'edge', 'firefox', 'chrome-mobile'],
        description: 'Browser for the UI tests. chrome-mobile: Chrome emulating a phone screen '
            + '(Pixel 7), to check the mobile layout.')
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
          String threads = params.THREADS ?: '1'
          currentBuild.description = "${suite()} | ${browser()}${where} | ${threads}x | ${ids}"
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
          recordResults('api', 'target/surefire-reports/TEST-com.qa.automation.api.*.xml')
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
          recordResults('ui', 'target/surefire-reports/TEST-com.qa.automation.webui.*.xml')
        }
      }
    }
  }

  post {
    always {
      // One Allure report for both stages: results accumulate in target/allure-results.
      allure results: [[path: 'target/allure-results']]
      archiveArtifacts artifacts: 'target/screenshots/**', allowEmptyArchive: true
      script {
        if (!testIdsFilter()) {
          publishPlots()
        }
      }
    }
  }
}
