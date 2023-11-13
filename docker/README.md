# Prerequisite

- Gcloud CLI
- Kubectl (optional, since it bundled with Gcloud CLI)

# Setup gcloud CLI

1. Download and install [gcloud CLI](https://cloud.google.com/sdk/docs/install)
2. Login
```agsl
gcloud auth login
```

3. Setup `kubectl` (if it was not installed):
```agsl
gcloud components install kubectl
```

4. Install plugin to use gcloud kubectl
```agsl
gcloud components install gke-gcloud-auth-plugin
```

# Build server image to deploy server:

1. Build module first (e.g: core, consumer, etc.)

- service_name: the module name of service which we want to deploy, ie: core
    ```agsl
    ./gradlew [sevice_name]:assemble
    ```

2. Deploy:

    ```agsl
    scripts/deploy.sh <env> <service-name>
    ```
- env: `prod` or `dev`
- service-name: the module name of service which we want to deploy, ie: `core`
   
# Set environment variable on GCP

1. Creating configmap (storing configs)
    ```agsl
    kubectl create configmap <config_map_name> --from-literal=<key>=<value>
    ```
   
2. Creating secret (storing key, password, etc.)
    ```agsl
   kubectl create secret generic <secret_name> --from-literal=<key>>=<secret>
   ```
   
3. Check/edit existing secret value
    ```agsl
    kubectl edit secret <secret-name>
    ```
- The value shown was base 64 encoded. To retrieve the actual value, decode it first.
- Save change to this file will update the config. 
- Remember, we must encode the value to base64 first before input it the file

## Build Liquibase image to migrate database:
1. Change dir to root project.
2. Build image:
```bash=
bash ./scripts/dockerize.sh liquibase-mysql liquibase migrate [service_name] false
```
- service_name must be the module name of service where we store the migration, ie: core
3. Run liquibase update from built image:
```bash=
docker run \
    -e DB_HOST=[db_host] -e DB_PORT=[db_port] -e DB_NAME=[db_name] \
    -e DB_USER=[db_user] -e DB_PASS=[db_pass] \
    -e MIGRATE_CONTEXT=[context] \
    --net=host \
    liquibase:liquibase-mysql
```
Beware that DB and Liquibase container might not be in the same network. Notice the `--net=host`
part, this assumes DB is on the physical host, or is forwarded to the physical host.