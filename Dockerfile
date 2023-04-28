# 第一阶段，构建二进制镜像
FROM ghcr.io/graalvm/native-image:ol9-java17 as builder

RUN curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo \
	&& mv sbt-rpm.repo /etc/yum.repos.d/ \
	&& microdnf -y install sbt-1.8.0

WORKDIR /app

COPY . /app

RUN sbt "graalvm-native-image:packageBin"

# 第二阶段，构建运行镜像，使用最基础的slim镜像
FROM oraclelinux:9-slim

EXPOSE 9080

COPY --from=builder /app/target/graalvm-native-image/junilant-scala ./app/
CMD ./app/apply-at-vdb
