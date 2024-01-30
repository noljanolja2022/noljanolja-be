
# Prerequisite

- Redis
- Mysql
- Python
- Docker (recommended)
- IntelliJ (recommended)

# Setup Redis image

1. Pull redis image from Docker hub
2. Run the image with Host port to your choice (eg: 6379)
   ```dbn-psql
   REDIS_HOST
   REDIS_PORT
   ```
# Start Db service

Either use docker or local instance. Prefer docker for lightweight

1. Pull [Mysql image](https://hub.docker.com/_/mysql) from Docker hub
2. Run the image with these environment variable. These can be found in `migration/cli.py`
   ```agsl
   MYSQL_ROOT_PASSWORD
   MYSQL_DATABASE
   MYSQL_USER
   MYSQL_PASSWORD
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
    python3 cli.py revision -m <revision name>
    ```
   - Window
   ```agsl
   py cli.py migrations/revision -m <revision name>
   ```
   Populate the revision file, then run step 3 again

5. To reset a database

   - Mac
    ```agsl
    python3 cli.py downgrade
    ```
   - Window
   ```agsl
   py cli.py downgrade
   ```
   
# Start local server

1. Setup these environment variable for `consumer` server
   ```
   AUTH_BASE_URL
   CORE_BASE_URL
   ```

2. Setup these env for `auth` server
   ```agsl
   FIREBASE_CREDENTIALS
   ```

3. Setup these environment variable for `core` server
   ```agsl
   DB_HOST
   DB_PORT
   DB_NAME
   DB_USER
   DB_PASS
   ```

4. Start `mysql` and `redis` local instance

5. Run server with `boothRun` or use default launch option from IDEA

# Debug local db

1. Run
   ```agsl
   docker run -p 3306:3306 --name nolja-mysql -e MYSQL_ROOT_PASSWORD=password -d mysql:8 --default-authentication-plugin=mysql_native_password
   ```

# Debug dev or prod db

1. Start with port forwarding
```agsl
kubectl port-forward $(kubectl get pod --selector="app=mysql" --output jsonpath='{.items[0].metadata.name}') 8020:3306
```

# Config hostname

Go to cafe link
Config A record

# Deployment
Check README in docker