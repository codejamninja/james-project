# Run James
#
# VERSION	1.0

FROM openjdk:8u222-jre

RUN wget -O /usr/local/bin/confd \
  https://github.com/kelseyhightower/confd/releases/download/v0.16.0/confd-0.16.0-linux-amd64 && \
  chmod +x /usr/local/bin/confd

# Ports that are used
#
# 25   SMTP without authentication
# 110  POP3
# 143  IMAP with startTLS enabled
# 465  SMTP with authentication and socketTLS enabled
# 587  SMTP with authentication and startTLS enabled
# 993  IMAP with socketTLS enabled
# 8000 Web Admin interface (unsecured: expose at your own risks)

EXPOSE 25 110 143 465 587 993 4000 8000

WORKDIR /root

# Get data we need to run James : build results and configuration
ADD destination/james-server-cassandra-guice.jar /root/james-server.jar
ADD destination/james-server-cassandra-guice.lib /root/james-server-cassandra-guice.lib
ADD destination/james-server-cli.jar /root/james-cli.jar
ADD destination/james-server-cli.lib /root/james-server-cli.lib
ADD destination/conf /root/conf
ADD destination/glowroot/plugins /root/glowroot/plugins
ADD destination/glowroot/glowroot.jar /root/glowroot/glowroot.jar
ADD destination/glowroot/lib/glowroot-embedded-collector.jar /root/glowroot/lib/glowroot-embedded-collector.jar
ADD destination/glowroot/lib/glowroot-logging-logstash.jar /root/glowroot/lib/glowroot-logging-logstash.jar
ADD destination/glowroot/admin.json /root/glowroot/admin.json
ADD destination/run_james.sh /root/run_james.sh
ADD destination/confd /etc/confd/conf.d
ADD destination/templates /etc/confd/templates

VOLUME /logs
VOLUME /root/conf
VOLUME /root/glowroot/plugins
VOLUME /root/glowroot/data

ENV CASSANDRA_NODES=cassandra \
  ELASTICSEARCH_MASTER_HOST=elasticsearch \
  ELASTICSEARCH_PASSWORD=secret \
  ELASTICSEARCH_USER=elasticsearch \
  GLOWROOT_ACTIVATED="false" \
  JVM_OPTIONS="" \
  PATH="$PATH:/root/glowroot/lib" \
  TIKA_ENABLED="true" \
  TIKA_HOST=tika

ENTRYPOINT ./run_james.sh
