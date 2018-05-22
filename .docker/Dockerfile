# Dockerfile
FROM alpine:3.5

FROM alpine:3.5

MAINTAINER Author Name <acamu@email.com>

# Place the code version inside the directory
ARG PACKAGE_VERSION
RUN echo "${PACKAGE_VERSION}" >> /usr/local/version.txt

#COPY project.war /usr/local/tomcat/webapps/project.war
COPY docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]
#CMD ["catalina.sh", "run"]
