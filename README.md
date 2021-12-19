# The Sandwich Shop

This is a demo of applying the _Break-Up Monolith_ pattern!

- A standalone JBoss/Wildfly instance, running a classic monolithic style application

- An external Red Hat AMQ broker

- A containerised JBoss/Wildfly instance, running on OpenShift

## Deployment

### Run a Red Hat AMQ (ActiveMQ Artemis) broker

You'll need to run an instance of ActiveMQ Artemis, on a server which can be reached from your OpenShift cluster.

```
./bin/artemis create mybroker
# set a username and password when prompted

./mybroker/bin/artemis run
```

#### Create a tunnel to ActiveMQ (optional)

If you are running the broker locally, and you don't mind exposing it to the internet, you can make it available on a public address, using the free public tunnelling service _ngrok_.

First register for a free account with Ngrok and download the CLI. Then:

```
ngrok authtoken <YOUR_AUTH_TOKEN>
ngrok tcp 61616
```

This will start a tunnel to port **61616** (the ActiveMQ port). Be sure to note down the `tcp://...` address which _ngrok_ gives you -- this is the public address of your broker and you'll need it for the next steps.

### Run the main application

**Now let's run the "main" sandwich-shop application.**

Download and extract the Wildfly (or JBoss EAP) distribution. Then go into the installation directory and start up Wildfly with the "full" profile (this enables messaging and other features):

```
cd $WILDFLY_HOME/bin
./standalone.sh -c standalone-full.xml 
```

In another terminal, compile and deploy the main application to your local Wildfly server:

```
cd original
mvn clean package wildfly:deploy
```

Go to <http://localhost:8080/sandwich-shop/> in your web browser to view the application and order your lunch!

### Deploy the 'broken-off' microservice

Now we'll deploy the broken-off microservice to OpenShift.

Install the EAP 7.4 image streams into your OpenShift project (there is a copy of this same file in _contrib/_ if needed):

```
oc apply -f https://raw.githubusercontent.com/jboss-container-images/jboss-eap-openshift-templates/eap74/eap74-openjdk8-image-stream.json
```

Edit the file _microservice/application.yml_:

- Update `HENLO_AMQ_TCP_SERVICE_HOST` to your broker hostname (or public hostname provided by the _ngrok_ tunnel)

- Update `HENLO_AMQ_TCP_SERVICE_PORT` to your broker's port (or port provided by the _ngrok_ tunnel)

- Update `MYBROKER_USERNAME` to the username to authenticate to AMQ

- Update `MYBROKER_PASSWORD` to the password to authenticate to AMQ

Now deploy the microservice into your cluster:

```
oc apply -f microservice/application.yml
```

This will build the application from this repository (GitHub). If you want to build from another location, fork and set the Git URL in _microservice/application.yml_.
