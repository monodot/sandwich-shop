package sandwichshop.kitchen;

import sandwichshop.Order;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.*;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This class that receives an order, enriches it, fulfils it and
 * updates it.
 *
 * Setting the `maxSession` property to 1 effectively creates a singleton
 * bean (a single chef in the kitchen, making all the sandwiches!)
 *
 * References:
 * https://docs.wildfly.org/15/Developer_Guide.html
 * http://www.mastertheboss.com/java-ee/ejb-3/how-to-create-a-mdb-30-singleton/
 * */
@MessageDriven(name = "KitchenMDB", description = "Processes incoming sandwich orders.", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/sandwichshop.kitchen"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
//        , @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1")
})
public class KitchenMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(KitchenMDB.class.toString());

    @Inject
    JMSContext jmsContext;

    @Resource(lookup = "java:/queue/sandwichshop.counter")
    private Queue counterQueue;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;

        try {
            LOGGER.info("Kitchen received an order! " + textMessage.getText());

            // Unmarshal the message
            JsonObject incoming = null;
            try (JsonReader jsonReader = Json.createReader(new StringReader(textMessage.getText()))) {
                incoming = jsonReader.readObject();
            }

            // TODO make this single-consumer only (no concurrency!)

            // Create an order object from the JSON message (probably could do this a bit nicer)
            Order order = new Order();
            order.setId(incoming.getString("orderId"));

            // Sleep for a short while. It takes time to make quality sandwiches!
            Thread.sleep(2000L);

            // Cook the order
            order = KitchenService.cookOrder(order);

            // Create an order status object (in a faffy way)
            // TODO improve this, maybe Jackson instead
            JsonObject json = Json.createObjectBuilder()
                    .add("orderId", order.getId())
                    .add("status", order.getStatus())
                    .build();

            StringWriter stringWriter = new StringWriter();
            try (JsonWriter writer = Json.createWriter(stringWriter)) {
                writer.write(json);
            }

            // Notify that the order has been processed (send to the counter queue)
            jmsContext.createProducer().send(counterQueue, stringWriter.toString());

            LOGGER.info("Kitchen sent order update: " + stringWriter.toString());

        } catch (Exception e) {
            LOGGER.severe("Something went wrong: " + e.getMessage());
            throw new RuntimeException(e); // Not ideal coding.
        }
    }

}
