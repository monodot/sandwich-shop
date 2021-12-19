package sandwichshop;

public class Order {

    private String id;
    private String sandwich;
    private String status;

    public String getSandwich() {
        return sandwich;
    }

    public void setSandwich(String sandwich) {
        this.sandwich = sandwich;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", sandwich='" + sandwich + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
