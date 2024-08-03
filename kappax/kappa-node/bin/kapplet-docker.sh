#!/bin/bash

export KUBE_HOST=192.168.0.19

#
# Single node cluster
#

export ATOMIX_PROFILE=Consensus
export ATOMIX_BOOTSTRAP=SINGLE:localhost:5501
export ATOMIX_NODE_PORT=5501
export ATOMIX_NODE_ID=SINGLE

export KAPPLET_SERVER_PORT=7701
export KAPPLET_WEB_PORT=7703


docker run -d --name kapplet \
--add-host=nevada:$KUBE_HOST \
--add-host=artifactory:$KUBE_HOST \
-e ATOMIX_PROFILE=${ATOMIX_PROFILE} \
-e ATOMIX_BOOTSTRAP=${ATOMIX_BOOTSTRAP} \
-e ATOMIX_NODE_PORT=${ATOMIX_NODE_PORT} \
-e ATOMIX_NODE_ID=${ATOMIX_NODE_ID} \
-e KAPPLET_SERVER_PORT=${KAPPLET_SERVER_PORT} \
-e KAPPLET_WEB_PORT=${KAPPLET_WEB_PORT} \
-e KAPPLET_PROPERTIES=kapplet.yml \
-p 30701:${KAPPLET_SERVER_PORT} \
-p 30703:${KAPPLET_WEB_PORT} \
-v /home/super/var/shared/m2:/opt/m2 \
-v /home/super/var/kapplet/.kappa:/opt/.kappa \
localhost:32000/kappa:latest
