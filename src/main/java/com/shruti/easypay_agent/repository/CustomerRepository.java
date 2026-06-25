package com.shruti.easypay_agent.repository;

import com.shruti.easypay_agent.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerRepository {

    public Customer findById(String customerId) {
        return switch (customerId) {
            case "A" -> new Customer("A", "Sarah Mitchell", 8, 45, 0, 400.00, "Dyson Vacuum");
            case "B" -> new Customer("B", "John Doe", 0, 3, 2, 1200.00, "MacBook Pro");
            case "C" -> new Customer("C", "Maria Garcia", 2, 10, 1, 250.00, "KitchenAid Mixer");
            case "D" -> new Customer("D", "Robert Chen", 15, 120, 0, 800.00, "Samsung TV");
            case "E" -> new Customer("E", "Ashley Brown", 0, 2, 1, 1500.00, "Diamond Bracelet");
            default -> throw new IllegalArgumentException("Unknown customer: " + customerId);
        };
    }
}