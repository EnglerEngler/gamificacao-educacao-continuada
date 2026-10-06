pipeline {
    agent any
    options { timestamps(); disableConcurrentBuilds(); timeout(time: 20, unit: 'MINUTES') }
    parameters {
        booleanParam(name: 'BUILD_IMAGES', defaultValue: false, description: 'Empacotar imagens no agente com Docker')
        booleanParam(name: 'DEPLOY_LAB', defaultValue: false, description: 'Implantar os artefatos testados no laboratório do agente')
    }
    stages {
        stage('Checkout') { steps { checkout scm } }
        stage('Core: regras, adapters e arquitetura') { steps { sh 'mvn -B verify' } }
        stage('Python: contratos e idempotência') {
            steps {
                sh '''set -eu
                    uv venv --clear .venv
                    uv pip install --python .venv/bin/python -r requirements.lock
                    mkdir -p .runtime
                    .venv/bin/python -m pytest -q tests --junitxml=.runtime/python-tests.xml
                '''
            }
        }
        stage('Frontend AC1') {
            steps { sh 'npm --prefix frontend ci && npm --prefix frontend run build' }
        }
        stage('Configurações e rastreabilidade') {
            steps { sh '.venv/bin/python scripts/validate-delivery.py && .venv/bin/python scripts/architecture-evidence.py' }
        }
        stage('Imagens versionadas') {
            when { expression { params.BUILD_IMAGES || params.DEPLOY_LAB } }
            steps {
                sh '''set -eu
                    docker build -t b1-core:$BUILD_NUMBER .
                    docker build -f services/Dockerfile -t b1-python:$BUILD_NUMBER .
                    docker image inspect b1-core:$BUILD_NUMBER b1-python:$BUILD_NUMBER > .runtime/images.json
                '''
            }
        }
        stage('Deploy laboratório') {
            when { expression { params.DEPLOY_LAB } }
            steps {
                sh '''set -eu
                    B1_IMAGE_TAG=$BUILD_NUMBER docker compose -f docker-compose.yml -f docker-compose.b1.yml -f ops/compose.release.yml up -d --no-build
                    .venv/bin/python scripts/smoke.py
                '''
            }
        }
    }
    post {
        always {
            junit allowEmptyResults: false, testResults: 'target/surefire-reports/*.xml'
            junit allowEmptyResults: true, testResults: '.runtime/python-tests.xml'
            archiveArtifacts allowEmptyArchive: true, artifacts: 'target/site/jacoco/**, .runtime/*.xml, .runtime/images.json, docs/evidencias/b1/**'
        }
    }
}
