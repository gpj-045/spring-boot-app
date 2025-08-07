// vars/dockerBuildAndPush.groovy
def call(appName, imageTag, registry, repo, credsId) {
    withCredentials([
        usernamePassword(
            credentialsId: credsId,
            usernameVariable: 'DOCKER_USER',
            passwordVariable: 'DOCKER_PASS'
        )
    ]) {
        try {
            sh """
                docker build -t ${registry}/${repo}/${appName}:${imageTag} .
                docker tag ${registry}/${repo}/${appName}:${imageTag} ${registry}/${repo}/${appName}:latest
                echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                docker push ${registry}/${repo}/${appName}:${imageTag}
                docker push ${registry}/${repo}/${appName}:latest
            """
        } finally {
            sh 'docker logout || true'
        }
    }
}

