FROM tomcat:9.0
COPY servlet/WEB-INF /usr/local/tomcat/webapps/ROOT/WEB-INF
COPY servlet/classes /usr/local/tomcat/webapps/ROOT/WEB-INF/classes