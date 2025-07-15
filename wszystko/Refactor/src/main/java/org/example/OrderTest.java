package org.example;

public class OrderTest {
    public static void main(String[] args) {
        OrderItem item = new OrderItem("Mój produkt", 10.0, 30);
        Shipping shipping = new Shipping("Mój adres", "InPost");
        Payment payment = new Payment("Allegro Pay");

        Order order = new Order("123", "Kowalski", true, item, shipping, payment);

        System.out.println("The total cost of the Order is " + order.calculateTotalCost());
    }
}
