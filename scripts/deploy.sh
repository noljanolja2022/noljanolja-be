#!/bin/sh

readonly service="$1"
img_name="asia-northeast3-docker.pkg.dev/noljanolja2023/noljanolja-be/$service:latest"

docker build \
  --no-cache \
  --progress=plain \
  --tag "$img_name" \
  --file "./docker/server/Dockerfile" \
  --build-arg "SERVICE=$service" \
  .

docker push "$img_name"
kubectl rollout restart "deployment/$service"