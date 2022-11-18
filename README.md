### To Get Started
1. Create a `.env` file at the root of the project, with the following info
    ```properties
    JDBC_URL="..."
    JDBC_USER="..."
    JDBC_PASS="..."
    ```
2. Start the MySQL emr db snapshot
    ```shell
    emr-db-snapshot-start mysql
    ```
3. Run the sandbox
    ```
   ./gradlew bootRun
   ```