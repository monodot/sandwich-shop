package sandwichshop.kitchen;

import sandwichshop.OrdersService;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.logging.Logger;

/**
 * This class that receives an order, enriches it, fulfils it and
 * updates it.
 * */
@MessageDriven(name = "KitchenMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/orders.new"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})
public class KitchenMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(KitchenMDB.class.toString());

    @Override
    public void onMessage(Message message) {
        TextMessage msg = (TextMessage) message;

        try {
            LOGGER.info("Kitchen received an order! " + msg.getText());
            // TODO call the chef here
            // TODO place the result on the other queue

        } catch (JMSException e) {
            LOGGER.severe("Something went wrong: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
