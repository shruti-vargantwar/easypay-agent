package com.shruti.easypay_agent.model;

import java.util.Map;

public class EasyPayReport {

    private Customer customer;
    private String toolCalled;
    private Map<String, Object> toolParameters;
    private String generatedMessage;

    public EasyPayReport(Customer customer, String toolCalled,
                         Map<String, Object> toolParameters, String generatedMessage) {
        this.customer = customer;
        this.toolCalled = toolCalled;
        this.toolParameters = toolParameters;
        this.generatedMessage = generatedMessage;
    }

    public Customer getCustomer() { return customer; }
    public String getToolCalled() { return toolCalled; }
    public Map<String, Object> getToolParameters() { return toolParameters; }
    public String getGeneratedMessage() { return generatedMessage; }

    public String getFormattedToolParameters() {
        if (toolParameters == null) return "";
        StringBuilder sb = new StringBuilder();
        toolParameters.forEach((key, value) ->
                sb.append(key).append(": ").append(value).append("\n")
        );
        return sb.toString();
    }
}
