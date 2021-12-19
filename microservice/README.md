# microservice

This is an example of a microservice which has been spun off from its parent application.

This is designed to show how you could extract some functionality from an application and deploy it separately onto OpenShift.

## To deploy on OpenShift

Install the EAP 7.4 image streams into your OpenShift project (there is a copy of this same file in _contrib/_ if needed):

```
oc apply -f https://raw.githubusercontent.com/jboss-container-images/jboss-eap-openshift-templates/eap74/eap74-openjdk8-image-stream.json
```

Now deploy the microservice into your cluster:

```
oc apply -f application.yml
```

