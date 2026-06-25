package com.shruti.easypay_agent.controller;

import com.shruti.easypay_agent.model.EasyPayReport;
import com.shruti.easypay_agent.service.EasyPayAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/easypay-agent")
public class EasyPayAgentController {

    @Autowired
    private EasyPayAgentService service;

    @GetMapping
    public String showForm() {
        return "easypay-form";
    }

    @PostMapping
    public String evaluate(@RequestParam String customerId, Model model) throws Exception {
        EasyPayReport report = service.evaluate(customerId);
        model.addAttribute("report", report);
        return "easypay-report";
    }
}