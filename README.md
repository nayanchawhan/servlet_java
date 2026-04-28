# Intrusion Detection System - Run Instructions

Follow these commands to deploy the database, launch the local GUI application, and view the web logs. Ensure you run these commands from the root directory: `d:\intrusion-detection-system`.

## 1. Start the Server & Database
This command will spin up PostgreSQL (`intrusion_db`) and Tomcat (`intrusion_tomcat`) via Docker. Make sure Docker is running on your machine.
```powershell
docker-compose up -d
```

## 2. Run the Java Swing App
Navigate into the `swing-app` directory, compile the application (ensuring the PostgreSQL driver is included in the classpath), and launch the GUI.

```powershell
# Navigate into the UI directory
cd swing-app

# Compile the Swing Application
javac -cp "..\lib\postgresql.jar;." IntrusionDetectionApp.java

# Run the Swing Application
java -cp "..\lib\postgresql.jar;." IntrusionDetectionApp
```
*(When the app opens, you can use the buttons to add intrusion logs, which will be saved directly to the running PostgreSQL database).*

## 3. View the Action Logs via the Web Browser
Once Tomcat is running, the Web Servlet automatically binds to port 8081. Open the following URL in your browser to view all of the inserted database logs through the Servlet:
```
http://localhost:8081/viewLogs
```

*(Note: If you ever make edits to `ViewLogsServlet.java` inside the `servlet/src` folder, you must recompile it into the `WEB-INF/classes` directory and restart the Tomcat container using these commands):*
```powershell
# Recompile Servlet
javac -cp "d:\intrusion-detection-system\lib\servlet-api.jar;d:\intrusion-detection-system\lib\postgresql.jar" -d d:\intrusion-detection-system\servlet\WEB-INF\classes d:\intrusion-detection-system\servlet\src\ViewLogsServlet.java

# Restart Tomcat to pick up new classes
docker restart intrusion_tomcat
```
