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
1. Change dir to root project.
2. Build image:
```bash=
bash ./scripts/dockerize.sh [tag] [repo] server [service_name] false
```
- tag is the image tag, ie: 1.0.0
- repo is the repo we will push to image to, ie: dockerhub, ...
- service_name must be the module name of service which we want to deploy, ie: core
- 