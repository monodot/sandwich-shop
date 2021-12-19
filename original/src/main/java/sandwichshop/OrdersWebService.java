package sandwichshop;

import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/")
public class OrdersWebService {

    @Inject
    OrdersRepository orders;

    @GET
    @Path("/list")
    @Produces("text/html")
    public String getOrders() {
        return orders.getOrdersAsHTML();
    }

    @POST
    @Path("/new")
    public String placeOrder(@FormParam("sandwich") String sandwich) {
        // TODO: Generate an Order ID
        // TODO: Set the Order status to Created, and add it to the OrdersService
        Order order = orders.create(sandwich);
        return "<p>" +
                "Last order result: " +
                "Order " + order.getId() + " has been created." +
                "</p>";
    }

}
