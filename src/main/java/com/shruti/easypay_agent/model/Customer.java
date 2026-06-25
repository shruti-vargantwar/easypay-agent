package com.shruti.easypay_agent.model;
public class Customer {

    private String id;
    private String name;
    private int membershipYears;
    private int totalPurchases;
    private int priorDeclines;
    private double failedAmount;
    private String productName;

    public Customer(String id, String name, int membershipYears, int totalPurchases,
                    int priorDeclines, double failedAmount, String productName) {
        this.id = id;
        this.name = name;
        this.membershipYears = membershipYears;
        this.totalPurchases = totalPurchases;
        this.priorDeclines = priorDeclines;
        this.failedAmount = failedAmount;
        this.productName = productName;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getMembershipYears() { return membershipYears; }
    public int getTotalPurchases() { return totalPurchases; }
    public int getPriorDeclines() { return priorDeclines; }
    public double getFailedAmount() { return failedAmount; }
    public String getProductName() { return productName; }
}