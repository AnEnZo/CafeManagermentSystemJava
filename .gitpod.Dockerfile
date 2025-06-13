FROM gitpod/workspace-full
USER root

# Cài các công cụ cần thiết
RUN apt-get update \
  && apt-get install -y wget tar zip unzip \
  && mkdir -p /usr/lib/jvm

# Tải và bung JDK21 từ Temurin
RUN wget -qO /tmp/temurin-21.tar.gz \
     https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21+35/OpenJDK21U-jdk_x64_linux_hotspot_21_35.tar.gz \
  && tar -xzf /tmp/temurin-21.tar.gz -C /usr/lib/jvm \
  && mv /usr/lib/jvm/jdk-21* /usr/lib/jvm/java-21-temurin \
  && rm /tmp/temurin-21.tar.gz

# Cấu hình alternatives
RUN update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-21-temurin/bin/java 1 \
  && update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/java-21-temurin/bin/javac 1 \
  && update-alternatives --set java /usr/lib/jvm/java-21-temurin/bin/java \
  && update-alternatives --set javac /usr/lib/jvm/java-21-temurin/bin/javac

USER gitpod
