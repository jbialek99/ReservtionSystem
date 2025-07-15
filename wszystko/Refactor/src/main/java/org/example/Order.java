package org.example;

public class Order {
    private String orderNumber;
    private String customerName;
    private boolean isSmartCustomer;
    private OrderItem item;
    private Shipping shipping;
    private Payment payment;

    private static final double SMART_THRESHOLD = 45.0;

    public Order(String orderNumber, String customerName, boolean isSmartCustomer,
                 OrderItem item, Shipping shipping, Payment payment) {
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.isSmartCustomer = isSmartCustomer;
        this.item = item;
        this.shipping = shipping;
        this.payment = payment;
    }

    public double calculateTotalCost() {
        double itemsCost = item.getTotalCost();
        double shippingCost = isSmartCustomer && itemsCost >= SMART_THRESHOLD ? 0 : shipping.getCost();
        double total = itemsCost + shippingCost;
        return payment.applyDiscount(total);
    }

    // Gettery
    public String getOrderNumber() { return orderNumber; }
    public String getCustomerName() { return customerName; }
    public boolean isSmartCustomer() { return isSmartCustomer; }
}
