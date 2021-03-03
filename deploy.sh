#!/bin/bash

export KUBE_NAMESPACE=${ENVIRONMENT}
export KUBE_SERVER=${KUBE_SERVER}

if [[ -z ${VERSION} ]] ; then
    export VERSION=${IMAGE_VERSION}
fi

if [[ ${KUBE_NAMESPACE} == *prod ]]
then
    export MIN_REPLICAS="2"
    export MAX_REPLICAS="6"
else
    export MIN_REPLICAS="1"
    export MAX_REPLICAS="2"
fi

if [[ ${ENVIRONMENT} == "cs-prod" ]] ; then
    echo "deploy ${VERSION} to PROD namespace, using HOCS_NOTIFY_CS_PROD drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_CS_PROD}
elif [[ ${ENVIRONMENT} == "wcs-prod" ]] ; then
    echo "deploy ${VERSION} to PROD namespace, using HOCS_NOTIFY_WCS_PROD drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_WCS_PROD}
elif [[ ${ENVIRONMENT} == "cs-qa" ]] ; then
    echo "deploy ${VERSION} to QA namespace, using HOCS_NOTIFY_CS_QA drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_CS_QA}
elif [[ ${ENVIRONMENT} == "wcs-qa" ]] ; then
    echo "deploy ${VERSION} to QA namespace, using HOCS_NOTIFY_WCS_QA drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_WCS_QA}
elif [[ ${ENVIRONMENT} == "cs-demo" ]] ; then
    echo "deploy ${VERSION} to DEMO namespace, using HOCS_NOTIFY_CS_DEMO drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_CS_DEMO}
elif [[ ${ENVIRONMENT} == "wcs-demo" ]] ; then
    echo "deploy ${VERSION} to DEMO namespace, using HOCS_NOTIFY_WCS_DEMO drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_WCS_DEMO}
elif [[ ${ENVIRONMENT} == "cs-dev" ]] ; then
    echo "deploy ${VERSION} to DEV namespace, using HOCS_NOTIFY_CS_DEV drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_CS_DEV}
elif [[ ${ENVIRONMENT} == "wcs-dev" ]] ; then
    echo "deploy ${VERSION} to DEV namespace, using HOCS_NOTIFY_WCS_DEV drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_WCS_DEV}
elif [[ ${ENVIRONMENT} == "hocs-qax" ]] ; then
    echo "deploy ${VERSION} to qax namespace, using HOCS_NOTIFY_QAX drone secret"
    export KUBE_TOKEN=${HOCS_NOTIFY_QAX}
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
