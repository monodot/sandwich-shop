# The Sandwich Shoppe

A demo Java EE application, for ordering delicious sandwiches. Designed to run on Wildfly application server.

- Displays a sandwich order form and shows all the recent orders and their status.
- A bean _processes_ each order, enriching it with some information
- JAX-RS provides the endpoints to the application (create a new order; view orders list)

## To deploy

Download and extract the Wildfly distribution. Then go into the installation directory and run Wildfly with the "full" profile (enables messaging and other features):

```
cd $WILDFLY_HOME/bin
./standalone.sh -c standalone-full.xml 
```

In another terminal, compile and deploy this application to your local Wildfly server:

```
mvn clean package wildfly:deploy
```

Go to <http://localhost:8080/sandwich-shop/> in your web browser to view the application and order your lunch!

