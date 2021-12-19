package sandwichshop;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.*;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@JMSDestinationDefinitions(
        value = {
                @JMSDestinationDefinition(
                        name = "java:/queue/sandwichshop.counter",
                        interfaceName = "javax.jms.Queue",
                        destinationName = "sandwichshop.counter"
                ),
                @JMSDestinationDefinition(
                        name = "java:/queue/sandwichshop.kitchen",
                        interfaceName = "javax.jms.Queue",
                        destinationName = "sandwichshop.kitchen"
                )
        }
)
@Singleton
@Named
public class OrdersRepository {

    private static final Logger LOGGER = Logger.getLogger(OrdersRepository.class.toString());
    private AtomicInteger counter = new AtomicInteger();

    private Map<String, Order> orders;

    private static final String STATUS_NEW = "New";

    @Inject
    JMSContext jmsContext;

    @Resource(lookup = "java:/queue/sandwichshop.kitchen")
    private Queue kitchenQueue;

    /**
     * Initialises the order store and adds a demonstration order to the repository.
     * Note the {@link PostConstruct} annotation which identifies code which
     * will run after this Singleton instance has been created.
     */
    @PostConstruct
    public void init() {
        LOGGER.info("Initialising orders map and adding demo order.");

        orders = new HashMap<String, Order>();
        this.create("eggs");
    }

    /**
     * Returns the list of all sandwich orders.
     */
    public Map<String, Order> getOrders() {
        return orders;
    }

    /**
     * Returns an HTML table representation of all sandwich orders in the repository.
     */
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
                    "  <td>" + o.getValue().getStatus() + "</td>" +
                    "</tr>";
        }

        response += "            </tbody>\n" +
                "        </table>";
        return response;
    }

    /**
     * Creates a new order, adds it to the orders repository and sends the
     * new order to the Kitchen.
     */
    public Order create(String sandwich) {
        LOGGER.info("Creating new order: " + sandwich);

        // Create the order and add it into the repository
        String orderId = String.valueOf(counter.incrementAndGet());
        Order order = new Order();
        order.setId(orderId);
        order.setSandwich(sandwich);
        order.setStatus(STATUS_NEW);
        orders.put(orderId, order);

        // Create a new order JSON message (in a faffy way)
        // TODO improve this, maybe Jackson instead
        JsonObject json = Json.createObjectBuilder()
                .add("orderId", order.getId())
                .add("status", order.getStatus())
                .build();

        StringWriter stringWriter = new StringWriter();
        try (JsonWriter writer = Json.createWriter(stringWriter)) {
            writer.write(json);
        }

        // Put the order in the new orders queue (send to the kitchen)
        jmsContext.createProducer().send(kitchenQueue, stringWriter.toString());

        return order;
    }

    /**
     * Find an order in the repository by its ID
     */
    public Order findById(String id) {
        LOGGER.info("Finding order " + id);
        return orders.get(id);
    }

    /**
     * Save an order into the repository.
     */
    public void save(Order order) {
        LOGGER.info("Saving order");
        orders.put(order.getId(), order);
    }

}
