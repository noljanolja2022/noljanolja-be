# Docker Images
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

## Build server image to deploy server:
1. Build module first (e.g: core, consumer, etc.)

- service_name: the module name of service which we want to deploy, ie: core
    ```agsl
    ./gradlew [sevice_name]:assemble
    ```

2. Build image and push image:

    ```bash=
    bash ./scripts/dockerize.sh latest asia-northeast3-docker.pkg.dev/noljanolja2023/noljanolja-be server [service_name] true
    ```
- service_name: the module name of service which we want to deploy, ie: core

3. Deploy image:

    ```agsl
    kubectl rollout restart deployment/[service_name]
    ```
   
## Set environment variable on GCP

1. Creating configmap (storing configs)
    ```agsl
    kubectl create configmap <config_map_name> --from-literal=<key>=<value>
    ```
   
2. Creating secret (storing key, password, etc.)
    ```agsl
   kubectl create secret generic <secret_name> --from-literal=<key>>=<secret>
   ```
3. To use them, checkout yaml file in Workloads and Secret/config maps tabs on GCP and follow