FROM gitpod/workspace-full

# Cài OpenJDK 21
RUN sudo apt-get update \
    && sudo apt-get install -y openjdk-21-jdk \
    && sudo update-alternatives --set java /usr/lib/jvm/java-21-openjdk-amd64/bin/java \
    && sudo update-alternatives --set javac /usr/lib/jvm/java-21-openjdk-amd64/bin/javac
