package servlets;

public class AccountBean {
    private String name;
    private String billingCity;
    private String phone;

    // Constructor
    public AccountBean(String name, String billingCity, String phone) {
        this.name = name;
        this.billingCity = billingCity;
        this.phone = phone;
    }

    // Getters (required for Jasper)
    public String getName() { return name; }
    public String getBillingCity() { return billingCity; }
    public String getPhone() { return phone; }
}
