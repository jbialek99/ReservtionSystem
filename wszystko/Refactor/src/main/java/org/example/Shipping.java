package org.example;

public class Shipping {
    private String address;
    private String method;
    private double cost;

    public Shipping(String address, String method) {
        this.address = address;
        this.method = method;
        calculateShippingCost();
    }

    private void calculateShippingCost() {
        switch (method) {
            case "DHL" -> cost = 12;
            case "InPost" -> cost = 9;
            default -> throw new RuntimeException("Error: Unknown shipping method.");
        }
    }

    public double getCost() {
        return cost;
    }

    // Gettery
    public String getAddress() { return address; }
    public String getMethod() { return method; }
}
