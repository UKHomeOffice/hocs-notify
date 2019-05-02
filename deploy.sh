#!/bin/bash

export KUBE_NAMESPACE=${KUBE_NAMESPACE}
export KUBE_SERVER=${KUBE_SERVER}

if [[ -z ${VERSION} ]] ; then
    export VERSION=${IMAGE_VERSION}
fi

if [[ ${ENVIRONMENT} == "prod" ]] ; then
    echo "deploy ${VERSION} to PROD namespace, using HOCS_AUDIT_PROD drone secret"
    export KUBE_TOKEN=${HOCS_AUDIT_PROD}
    export REPLICAS="2"
else
    if [[ ${ENVIRONMENT} == "qa" ]] ; then
        echo "deploy ${VERSION} to QA namespace, using HOCS_AUDIT_QA drone secret"
        export KUBE_TOKEN=${HOCS_AUDIT_QA}
        export REPLICAS="2"
    elif [[ ${ENVIRONMENT} == "demo" ]] ; then
        echo "deploy ${VERSION} to DEMO namespace, using HOCS_AUDIT_DEMO drone secret"
        export KUBE_TOKEN=${HOCS_AUDIT_DEMO}
        export REPLICAS="1"
    elif [[ ${ENVIRONMENT} == "dev" ]] ; then
        echo "deploy ${VERSION} to DEV namespace, using HOCS_AUDIT_DEV drone secret"
        export KUBE_TOKEN=${HOCS_AUDIT_DEV}
        export REPLICAS="1"        
    else
        echo "Unable to find environment: ${ENVIRONMENT}"
    fi
fi

if [[ -z ${KUBE_TOKEN} ]] ; then
    echo "Failed to find a value for KUBE_TOKEN - exiting"
    exit -1
fi

if [ "${ENVIRONMENT}" == "prod" ] ; then
    export DNS_PREFIX=export.cs
    export KC_REALM=https://sso-dev.notprod.homeoffice.gov.uk/auth/realms/hocs-notprod
else
    export DNS_PREFIX=${ENVIRONMENT}.export.cs-notprod
    export KC_REALM=https://sso-dev.notprod.homeoffice.gov.uk/auth/realms/hocs-notprod
fi

export DOMAIN_NAME=${DNS_PREFIX}.homeoffice.gov.uk

echo
echo "Deploying hocs-frontend to ${ENVIRONMENT}"
echo "Keycloak realm: ${KC_REALM}"
echo "Keycloak domain: ${KC_DOMAIN}"
echo "domain name: ${DOMAIN_NAME}"
echo

cd kd

kd --insecure-skip-tls-verify \
   --timeout 10m \
    -f deployment.yaml \
    -f service.yaml
    -f ingress.yaml
