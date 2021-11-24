package sandwichshop;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.logging.Logger;

@MessageDriven(name = "OrdersMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/orders.updates"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})
public class OrdersMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(OrdersMDB.class.toString());

    @Inject
    OrdersService orders;

    @Override
    public void onMessage(Message message) {
        TextMessage msg = (TextMessage) message;

        try {
            LOGGER.info("OrdersMDB received a message! " + msg.getText());

            // TODO receive the update from the Kitchen and update the Order status
            //orders.getOrders().get("1").setStatus("Fulfilled");
        } catch (JMSException e) {
            LOGGER.severe("Something went wrong: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
