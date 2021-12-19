package sandwichshop.kitchen;

import sandwichshop.Order;

import javax.inject.Inject;
import javax.jms.JMSContext;

public class KitchenService {

    @Inject
    JMSContext jmsContext;

    /**
     * "Cooks" the customer's order (makes the sandwich, spends a random amount of time
     * in preparation.)
     * TODO - maybe this shouldn't be a static method??
     */
    public static Order cookOrder(Order order) throws InterruptedException {
        order.setStatus("prepared");
        // TODO: Add some characteristics: Sandwich Quality, Chef's Name, Weight, etc.
        // TODO: Cook the order here.

        return order;
    }
}
