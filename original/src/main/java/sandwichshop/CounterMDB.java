package sandwichshop;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.logging.Logger;

@MessageDriven(name = "CounterMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/sandwichshop.counter"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})
public class CounterMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(CounterMDB.class.toString());

    @Inject
    OrdersRepository orders;

    /**
     * Receive an order update message (usually from the kitchen) and update
     * the Order's status in the repository.
     */
    @Override
    public void onMessage(Message message) {
        TextMessage msg = (TextMessage) message;

        try {
            LOGGER.info("CounterMDB received a message! " + msg.getText());

            // Unmarshal the message
            JsonObject orderUpdateMessage = null;
            try (JsonReader jsonReader = Json.createReader(new StringReader(msg.getText()))) {
                orderUpdateMessage = jsonReader.readObject();
            }

            // Find the Order and update its status
            Order order = orders.findById(orderUpdateMessage.getString("orderId"));
            order.setStatus(orderUpdateMessage.getString("status"));
            orders.save(order);

        } catch (JMSException e) {
            LOGGER.severe("Something went wrong: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
