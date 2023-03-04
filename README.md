# Prerequisite

- Redis
- Mysql
- Python
- Docker (recommended)

# Start Db service

Either use docker or local instance. Prefer docker for lightweight

1. Pull [Mysql image](https://hub.docker.com/_/mysql) from Docker hub
2. Run the image with these environment variable. These can be found in `migration/cli.py` 
   ```agsl
   MYSQL_ROOT_PASSWORD=
   MYSQL_DATABASE=
   MYSQL_USER=
   MYSQL_PASSWORD=
   ```
   And expose port `3306`

# Create migration/populate Database
1. Navigate to `migrations` 
   
   ```agsl
   cd migrations
   ```

2. Run this to install dependencies
    ```
    pip3 install -r ./requirements.txt
    ```
3. To populate database

   - Mac
    ```agsl
    python3 cli.py upgrade
    ```
   - Window
   ```agsl
   py cli.py upgrade
   ```

4. To create a new migration/table

   - Mac
    ```agsl
    python3 cli.py revision -m "revision name"
    ```
   - Window
   ```agsl
   py cli.py migrations/revision -m "revision name"
   ```
   Populate the revision file, then run step 3 again

4. To reset a database

   - Mac
    ```agsl
    python3 cli.py downgrade
    ```
   - Window
   ```agsl
   py cli.py downgrade
   ```
   
# Start local server

# Debug local db