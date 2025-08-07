def call(manifestRepo, manifestPath, appName, imageTag, repo, credsId, buildNo) {
    git credentialsId: credsId, url: manifestRepo, branch: 'main'
    sshagent(credentials: [credsId]) {
        sh """
            cd ${manifestPath}
            sed -i "s|image: ${repo}/${appName}:.*|image: ${repo}/${appName}:${imageTag}|" deployment.yaml
            git config user.name "ci-bot"
            git config user.email "ci-bot@ci.local"
            if git diff --quiet; then
                echo "No changes detected"
            else
                git add deployment.yaml
                git commit -m "Update ${appName} image to ${imageTag} [Build: ${buildNo}]"
                git push origin main
            fi
        """
    }
}

