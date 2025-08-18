# Use OpenJDK 17 slim image for smaller size
FROM openjdk:17-jdk-slim

# Switch to root for installation
USER root

# Update package lists and install essential tools
RUN apt-get update && apt-get install -y \
    # Essential tools
    curl \
    wget \
    git \
    openssh-client \
    # Docker CLI dependencies
    ca-certificates \
    gnupg \
    lsb-release \
    && rm -rf /var/lib/apt/lists/*

# Install Maven (specific version)
ENV MAVEN_VERSION=3.9.4
ENV MAVEN_HOME=/opt/maven
ENV PATH=$PATH:$MAVEN_HOME/bin

RUN wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /opt && \
    mv /opt/apache-maven-${MAVEN_VERSION} $MAVEN_HOME && \
    rm apache-maven-${MAVEN_VERSION}-bin.tar.gz

# Install Docker CLI
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg && \
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian $(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list && \
    apt-get update && \
    apt-get install -y docker-ce-cli && \
    rm -rf /var/lib/apt/lists/*

# Set Git global configuration
RUN git config --system user.name "Jenkins Agent" && \
    git config --system user.email "jenkins@localhost" && \
    git config --system init.defaultBranch main && \
    git config --system pull.rebase false

# Add GitHub SSH keys (include all key types - RSA, ECDSA, ED25519)
RUN mkdir -p ~/.ssh && \
    chmod 700 ~/.ssh && \
    ssh-keyscan  -t rsa,ecdsa,ed25519 github.com >>  ~/.ssh/known_hosts && \
    ssh-keyscan  -t rsa,ecdsa,ed25519 ssh.github.com >>  ~/.ssh/known_hosts && \
    chmod 644 ~/.ssh/known_hosts

# Create jenkins user with docker group
RUN useradd -u 111 -m jenkins && \
    groupadd -g 988 docker || true && \
    usermod -aG docker jenkins

# Setup SSH for jenkins user
RUN mkdir -p /home/jenkins/.ssh && \
    cp ~/.ssh/known_hosts /home/jenkins/.ssh/known_hosts && \
    chown -R jenkins:jenkins /home/jenkins/.ssh && \
    chmod 700 /home/jenkins/.ssh && \
    chmod 600 /home/jenkins/.ssh/known_hosts

# Create Maven local repository
RUN mkdir -p /home/jenkins/.m2 && \
    chown -R jenkins:jenkins /home/jenkins/.m2

# Create Docker config directory for jenkins user
RUN mkdir -p /home/jenkins/.docker && \
    chown -R jenkins:jenkins /home/jenkins/.docker && \
    chmod 755 /home/jenkins/.docker

# Set HOME environment variable to ensure Docker uses correct config path
ENV HOME=/home/jenkins
ENV DOCKER_CONFIG=/home/jenkins/.docker

# Verify installations
RUN java -version && mvn -version && git --version && docker --version

# Add metadata
LABEL maintainer="gpj-045" \
      description="Java-based Jenkins Agent with Maven, Docker, Git" \
      java.version="17" \
      maven.version="3.9.4" \
      base="openjdk:17-jdk-slim"

# Default entrypoint (Jenkins-compatible)
USER jenkins
CMD ["/bin/bash"]

