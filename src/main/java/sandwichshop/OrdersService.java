package sandwichshop;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@JMSDestinationDefinitions(
        value = {
                @JMSDestinationDefinition(
                        name = "java:/queue/orders.updates",
                        interfaceName = "javax.jms.Queue",
                        destinationName = "orders.updates"
                ),
                @JMSDestinationDefinition(
                        name = "java:/queue/orders.new",
                        interfaceName = "javax.jms.Queue",
                        destinationName = "orders.new"
                )
        }
)
@Singleton
@Named
public class OrdersService {

    private static final Logger LOGGER = Logger.getLogger(OrdersService.class.toString());
    private AtomicInteger counter = new AtomicInteger();

    private Map<String, Order> orders;

    @Inject
    JMSContext jmsContext;

    @Resource(lookup = "java:/queue/orders.new")
    private Queue newOrdersQueue;

    /**
     * Initialises the order store and adds a demonstration order.
     */
    @PostConstruct
    public void init() {
        LOGGER.info("Initialising orders map and adding demo order.");
        orders = new HashMap<String, Order>();
        createOrder("eggs");
    }

    /**
     * Returns the list of all sandwich orders.
     */
    public Map<String, Order> getOrders() {
        return orders;
    }

    public String getOrdersAsHTML() {
        String response = "<table>\n" +
                "    <thead>\n" +
                "        <tr>\n" +
                "            <td>Order ID</td>\n" +
                "            <td>Sandwich type</td>\n" +
                "            <td>Status</td>\n" +
                "        </tr>\n" +
                "    </thead>\n" +
                "    <tbody>";

        for (Map.Entry<String, Order> o : orders.entrySet()) {
            response += "        <tr>" +
                    "  <td>" + o.getValue().getId() + "</td>" +
                    "  <td>" + o.getValue().getSandwich() + "</td>" +
                    "</tr>";
        }

        response += "            </tbody>\n" +
                "        </table>";
        return response;
    }

    /**
     * Creates a new order and adds it to the order store.
     */
    public Order createOrder(String sandwich) {
        LOGGER.info("Creating new order: " + sandwich);
        String orderId = String.valueOf(counter.incrementAndGet());
        Order order = new Order();
        order.setId(orderId);
        order.setSandwich(sandwich);
        orders.put(orderId, order);

        String request = "New order: " + orderId;
        jmsContext.createProducer().send(newOrdersQueue, request);

        return order;
    }

    //    @Inject
//    private JMSContext jmsContext;
//
//    @Resource(lookup = "java:/queue/sandwichshop.orders")
//    private Queue queue;


}
