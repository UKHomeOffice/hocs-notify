#!/bin/bash

export KUBE_NAMESPACE=${KUBE_NAMESPACE}
export KUBE_SERVER=${KUBE_SERVER}

if [[ -z ${VERSION} ]] ; then
    export VERSION=${IMAGE_VERSION}
fi

if [[ ${ENVIRONMENT} == "prod" ]] ; then
    echo "deploy ${VERSION} to PROD namespace, using HOCS_NOTIFY_PROD drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_PROD}
    export REPLICAS="1"
else
    if [[ ${ENVIRONMENT} == "qa" ]] ; then
        echo "deploy ${VERSION} to QA namespace, using HOCS_NOTIFY_QA drone secret"
        export KUBE_TOKEN=${HOCS_NOTIFY_QA}
        export REPLICAS="1"
    elif [[ ${ENVIRONMENT} == "demo" ]] ; then
        echo "deploy ${VERSION} to DEMO namespace, using HOCS_NOTIFY_DEMO drone secret"
        export KUBE_TOKEN=${HOCS_NOTIFY_DEMO}
        export REPLICAS="1"
    elif [[ ${ENVIRONMENT} == "dev" ]] ; then
        echo "deploy ${VERSION} to DEV namespace, using HOCS_NOTIFY_DEV drone secret"
        export KUBE_TOKEN=${HOCS_NOTIFY_DEV}
        export REPLICAS="1"        
    else
        echo "Unable to find environment: ${ENVIRONMENT}"
    fi
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
