package org.example;

public class Payment {
    private String method;
    private static final double ALLEGRO_THRESHOLD = 100.0;
    private static final double ALLEGRO_DISCOUNT = 0.02;

    public Payment(String method) {
        this.method = method;
    }

    public double applyDiscount(double totalCost) {
        if (method.equals("Allegro Pay") && totalCost >= ALLEGRO_THRESHOLD) {
            return totalCost * (1 - ALLEGRO_DISCOUNT);
        }
        return totalCost;
    }

    public String getMethod() {
        return method;
    }
}
