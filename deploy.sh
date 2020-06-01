#!/bin/bash

export KUBE_NAMESPACE=${ENVIRONMENT}
export KUBE_SERVER=${KUBE_SERVER}

if [[ -z ${VERSION} ]] ; then
    export VERSION=${IMAGE_VERSION}
fi

if [[ ${ENVIRONMENT} == "cs-prod" ]] ; then
    echo "deploy ${VERSION} to PROD namespace, using HOCS_NOTIFY_CS_PROD drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_CS_PROD}
    export REPLICAS="1"
elif [[ ${ENVIRONMENT} == "wcs-prod" ]] ; then
    echo "deploy ${VERSION} to PROD namespace, using HOCS_NOTIFY_WCS_PROD drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_WCS_PROD}
    export REPLICAS="1"
elif [[ ${ENVIRONMENT} == "cs-qa" ]] ; then
    echo "deploy ${VERSION} to QA namespace, using HOCS_NOTIFY_CS_QA drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_CS_QA}
    export REPLICAS="1"
elif [[ ${ENVIRONMENT} == "wcs-qa" ]] ; then
    echo "deploy ${VERSION} to QA namespace, using HOCS_NOTIFY_WCS_QA drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_WCS_QA}
    export REPLICAS="1"
elif [[ ${ENVIRONMENT} == "cs-demo" ]] ; then
    echo "deploy ${VERSION} to DEMO namespace, using HOCS_NOTIFY_CS_DEMO drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_CS_DEMO}
    export REPLICAS="1"
elif [[ ${ENVIRONMENT} == "wcs-demo" ]] ; then
    echo "deploy ${VERSION} to DEMO namespace, using HOCS_NOTIFY_WCS_DEMO drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_WCS_DEMO}
    export REPLICAS="1"
elif [[ ${ENVIRONMENT} == "cs-dev" ]] ; then
    echo "deploy ${VERSION} to DEV namespace, using HOCS_NOTIFY_CS_DEV drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_CS_DEV}
    export REPLICAS="1"
elif [[ ${ENVIRONMENT} == "wcs-dev" ]] ; then
    echo "deploy ${VERSION} to DEV namespace, using HOCS_NOTIFY_WCS_DEV drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_WCS_DEV}
    export REPLICAS="1"
else
    echo "Unable to find environment: ${ENVIRONMENT}"
fi

if [[ -z ${KUBE_TOKEN} ]] ; then
    echo "Failed to find a value for KUBE_TOKEN - exiting"
    exit -1
fi

cd kd

kd --insecure-skip-tls-verify \
   --timeout 10m \
    -f deployment.yaml \
    -f service.yaml \
    -f autoscale.yaml
