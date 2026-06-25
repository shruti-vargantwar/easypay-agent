package com.shruti.easypay_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shruti.easypay_agent.client.AnthropicClient;
import com.shruti.easypay_agent.model.Customer;
import com.shruti.easypay_agent.model.EasyPayReport;
import com.shruti.easypay_agent.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class EasyPayAgentService {

    @Autowired
    private AnthropicClient anthropicClient;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public EasyPayReport evaluate(String customerId) throws Exception {

        Customer customer = customerRepository.findById(customerId);

        // Load prompt
        Resource promptResource = resourceLoader.getResource("classpath:prompts/easypay-system.txt");
        String systemPrompt = new String(promptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        // Load tools
        Resource toolsResource = resourceLoader.getResource("classpath:tools/easypay-tools.json");
        String toolsJson = new String(toolsResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        List<Map<String, Object>> tools = objectMapper.readValue(toolsJson, List.class);

        // Build user prompt
        String userPrompt = buildUserPrompt(customer);

        // First API call -- Claude decides which tool to call
        String firstResponse = anthropicClient.call(systemPrompt, userPrompt, tools);
        JsonNode firstNode = objectMapper.readTree(firstResponse);

        // Extract tool_use block
        JsonNode toolUseBlock = null;
        for (JsonNode block : firstNode.get("content")) {
            if ("tool_use".equals(block.get("type").asText())) {
                toolUseBlock = block;
                break;
            }
        }

        String toolUseId = toolUseBlock.get("id").asText();
        String toolName = toolUseBlock.get("name").asText();
        Map<String, Object> toolInput = objectMapper.convertValue(
                toolUseBlock.get("input"), Map.class
        );

        // Execute the tool locally
        String toolResult = executeTool(toolName, toolInput, customer);

        // Second API call -- Claude generates final message
        String secondResponse = anthropicClient.callWithToolResult(
                systemPrompt, userPrompt, tools, toolUseId, toolName, toolInput, toolResult
        );
        JsonNode secondNode = objectMapper.readTree(secondResponse);
        String generatedMessage = secondNode.get("content").get(0).get("text").asText();

        return new EasyPayReport(customer, toolName, toolInput, generatedMessage);
    }

    private String buildUserPrompt(Customer customer) {
        return String.format("""
            Failed Easy Pay Installment Alert:
            Customer ID: %s
            Customer Name: %s
            Membership Years: %d
            Total Purchases: %d
            Prior Declines: %d
            Failed Amount: $%.2f
            Product: %s
            """,
                customer.getId(),
                customer.getName(),
                customer.getMembershipYears(),
                customer.getTotalPurchases(),
                customer.getPriorDeclines(),
                customer.getFailedAmount(),
                customer.getProductName()
        );
    }

    private String executeTool(String toolName, Map<String, Object> toolInput, Customer customer) {
        if ("extendGracePeriod".equals(toolName)) {
            return "Grace period extended by " + toolInput.get("days")
                    + " days for customer " + customer.getName() + ". Retry date updated in system.";
        } else {
            return "Account frozen for customer " + customer.getName()
                    + ". Pending shipments paused. Routed to fraud team.";
        }
    }
}