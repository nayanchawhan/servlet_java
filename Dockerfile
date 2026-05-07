FROM tomcat:9.0
RUN rm -rf /usr/local/tomcat/webapps/ROOT/*
COPY servlet/ /usr/local/tomcat/webapps/ROOT/