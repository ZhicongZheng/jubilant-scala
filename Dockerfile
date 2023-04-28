# 第一阶段，构建二进制镜像
FROM ghcr.io/graalvm/native-image:ol9-java11-22.3.0 as builder

RUN curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo \
	&& mv sbt-rpm.repo /etc/yum.repos.d/ \
	&& microdnf -y install sbt-1.8.0

WORKDIR /app

COPY . /app

RUN sbt "graalvm-native-image:packageBin"

RUN ls -l /app/target/graalvm-native-image/

# 第二阶段，构建运行镜像，使用最基础的slim镜像
FROM oraclelinux:9-slim

EXPOSE 9080

ENV DATABASE_URL="" \
    DATABASE_USER="root" \
    DATABASE_PWD="" \
    OSS_ACCESS_KEY_ID="" \
    OSS_ACCESS_KEY_SECRET="" \
    OSS_BUCKET_NAME=""

COPY --from=builder /app/target/graalvm-native-image/jubilant-scala ./app/
CMD ./app/jubilant-scala
