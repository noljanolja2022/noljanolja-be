#!/bin/sh

readonly image_tag="$1"
readonly image_repository="$2"
readonly type="$3"
readonly service="$4"
readonly push_image="$5"

buildDir="./$type"
dockerfilePath="./docker/$type/Dockerfile"
if [ $type == "migrate" ]; then
  buildDir="./$service/src/main/resources/db/changelog"
  dockerfilePath="./docker/db/Dockerfile"
fi

docker build \
  --no-cache \
  --progress=plain \
  --tag "$image_repository":"$image_tag" \
  --file "$dockerfilePath" \
  --build-arg "SERVICE=$service" \
  "$buildDir"

if [ "$push_image" == "true" ]; then
  docker push "$image_repository":"$image_tag"
fi
