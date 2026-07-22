# Контекст сборки — КОРЕНЬ репозитория (см. compose.yaml: context: .),
# потому что notebook зависит от библиотеки synccore, лежащей рядом.
FROM eclipse-temurin:26-jdk AS build
WORKDIR /app

# 1. Сначала библиотека: её jar кладётся в .m2 внутри образа
COPY synccore/mvnw synccore/mvnw
COPY synccore/.mvn synccore/.mvn
COPY synccore/pom.xml synccore/pom.xml
RUN cd synccore && ./mvnw dependency:go-offline -B
COPY synccore/src synccore/src
RUN cd synccore && ./mvnw clean install -DskipTests -B

# 2. Затем сервис: найдёт synccore в локальном .m2 образа
COPY notebook/mvnw notebook/mvnw
COPY notebook/.mvn notebook/.mvn
COPY notebook/pom.xml notebook/pom.xml
RUN cd notebook && ./mvnw dependency:go-offline -B
COPY notebook/src notebook/src
RUN cd notebook && ./mvnw clean package -DskipTests -B

FROM eclipse-temurin:26-jre AS run
WORKDIR /app

COPY --from=build /app/notebook/target/notebook-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
