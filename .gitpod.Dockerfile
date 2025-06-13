FROM gitpod/workspace-full

# Cài OpenJDK 21
USER root
RUN apt-get update \
    && apt-get install -y openjdk-21-jdk \
    && update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-21-openjdk-amd64/bin/java 1 \
    && update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/java-21-openjdk-amd64/bin/javac 1 \
    && update-alternatives --set java /usr/lib/jvm/java-21-openjdk-amd64/bin/java \
    && update-alternatives --set javac /usr/lib/jvm/java-21-openjdk-amd64/bin/javac
USER gitpod