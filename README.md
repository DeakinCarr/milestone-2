# Week 07 Project Workshop Starter Code
Starter code for Week 07 Project Workshop for Javalin and Thymeleaf.

Folders:
```bash
├── /src/main                    - Location of all files as required by MAVEN build
│         ├── java               - Java Source location
│         │    └── app           - package location for all Java files
│         └── resources          - Web resources (html templates / style sheets)
│               ├── css          - CSS Style-sheets
│               └── images       - Image files
│ 
├── /target                      - build directory (DO NOT MODIFY)
├── /database                    - The folder to store sqlite database files (*.db files)
│         └── scripts            - The folder to store any external SQL scripts
├── pom.xml                      - Configure Build (DO NOT MODIFY)
└── README.md                    - This file ;)
```

Current Libraries
* org.xerial.sqlite-jdbc         - SQLite JDBC library
* javalin (lightweight java webserver)
* thymeleaf (HTML template) - https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html

Libraries required as dependencies
* By javalin/thymeleaf
   * slf4j-simple (lightweight logging)
* By xerial/jdbc
   * sqlite-jdbc
* By jackson/fasterxml
   * jackson-databind

# Building & Running the code
1. Open this project within VSCode
2. Allow VSCode to read the Maven pom.xml file
 - Allow the popups to run and "say yes" to VSCode configuring Maven
 - Allow VSCode to download the required Java libraries
3. To Build & Run
 - Select the "Launch App" launcher profile from the VSCode Run/Debugger
 - Alternatively, open the src/main/java/app/App.java source file, and select "Run" from the pop-up above the main function
4. Go to: http://localhost:7000

# Authors
* Deakin Carr, RMIT University.
* Max Cantwell, RMIT University.

Copyright RMIT University (c) 2021
