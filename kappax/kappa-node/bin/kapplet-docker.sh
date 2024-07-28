#!/bin/bash

export KUBE_HOST=192.168.0.19

docker run -d --name kapplet \
--add-host=nevada:$KUBE_HOST \
--add-host=artifactory:$KUBE_HOST \
-p 30701:7701 \
-p 30703:7703 \
-e M2_HOME=/opt/3.5.4 \
-e KAPPLET_PROPERTIES=kapplet-test-container.yml \
-v /home/super/var/shared/m2:/opt/m2 \
-v /home/super/var/kapplet/.kappa:/opt/.kappa \
localhost:32000/kappa:latest
