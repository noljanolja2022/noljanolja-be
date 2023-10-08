#!/bin/sh

if [[ $1 == 'prod' ]];
then
    CLUSTER_NAME="production-cluster"
    PROJECT_NAME="nolgobulja-2023"
    ARTIFACT_FOLDER="nolgobulja-be"
else
    CLUSTER_NAME="development-cluster"
    PROJECT_NAME="noljanolja2023"
    ARTIFACT_FOLDER="noljanolja-be"
fi

REGION=asia-northeast3
# Choose appropriate project
gcloud config set project $PROJECT_NAME

# Ensure that you are connected to your GKE cluster.
gcloud container clusters get-credentials $CLUSTER_NAME --region $REGION

readonly service="$2"
IMG_NAME="$REGION-docker.pkg.dev/$PROJECT_NAME/$ARTIFACT_FOLDER/$service:latest"

docker build \
  --no-cache \
  --progress=plain \
  --tag "$IMG_NAME" \
  --file "./docker/server/Dockerfile" \
  --build-arg "SERVICE=$service" \
  .

docker push "$IMG_NAME"
kubectl rollout restart "deployment/$service"