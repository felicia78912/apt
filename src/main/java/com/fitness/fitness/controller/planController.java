package com.fitness.fitness.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fitness.fitness.model.Benefit;
import com.fitness.fitness.model.PaymentTransaction;
import com.fitness.fitness.model.Plan;
import com.fitness.fitness.model.PlanDurationPrice;
import com.fitness.fitness.repository.PaymentTransactionRepo;
import com.fitness.fitness.service.PlanService;








@Controller
public class planController {

    @Autowired
    private PlanService planService;
    @Autowired
    private PaymentTransactionRepo paymentTransactionRepo;
    @GetMapping("/browse_plans")
    public String browsePlans(Model model) {
        Map<String, Double> startingPrices = planService.getStartingPricesForAllPlanTypes();

        LinkedHashMap<String, Double> sortedStartingPrices = startingPrices.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(
                Map.Entry::getKey, 
                Map.Entry::getValue, 
                (oldValue, newValue) -> oldValue, // This merge function will keep the old value in case of a key collision
                LinkedHashMap::new));
                

        List<String> sortedPlanTypes = new ArrayList<>(sortedStartingPrices.keySet());
        Map<String, List<Benefit>> planBenefits = new HashMap<>();
        sortedPlanTypes.forEach(planType -> {
            List<Benefit> benefits = planService.findBenefitsByPlanType(planType).get(planType);
            planBenefits.put(planType, benefits);
        });

        model.addAttribute("planTypes", sortedPlanTypes);
        model.addAttribute("startingPrices", sortedStartingPrices);
        model.addAttribute("planBenefits", planBenefits);
        
        return "plans";

    }

    @GetMapping("/browsePlan/{planType}")
    public String browsePlan(@PathVariable("planType") String planType, Model model) {
        List<Plan> plans = planService.findByPlanType(planType);
        
        // Assuming each planType only has one set of details but multiple duration prices
        Plan planDetails = plans.stream().findFirst().orElseThrow(() -> new RuntimeException("Plan type not found"));
    
        List<Benefit> benefits = new ArrayList<>(planDetails.getBenefits()); // Assuming findBenefitsByPlanType method adjustment
    
        // If you want to include various prices for different durations directly:
        Set<PlanDurationPrice> durationPrices = planDetails.getPlanDurationPrices();
    
        model.addAttribute("planType", planType);
        model.addAttribute("planDetails", planDetails.getPlanDetails());
        model.addAttribute("benefits", benefits);
        model.addAttribute("durationPrices", durationPrices);
        return "planDetails";
    }

    @GetMapping("/browsePlan/{planType}/purchaseForm")
    public String showPurchaseForm(@PathVariable("planType") String planType,
                                   @RequestParam("duration") String duration, 
                                   @RequestParam("price") Double price, Model model) {
        model.addAttribute("planType", planType);
        model.addAttribute("selectedDuration", duration);
        model.addAttribute("price", price);
        return "purchaseForm";
    }

    @GetMapping("/confirm_purchase")
    public String showPurchaseConfirmationPage(Model model) {
        return "purchaseConfirmation";
    }

    @PostMapping("/finalizePurchase")
    public String finalizePurchase(@RequestParam("userAgreement") boolean userAgreement, 
                                    @RequestParam("paymentMethod") String paymentMethod,
                                    @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                    @RequestParam(value = "cardholderName", required = false) String cardholderName,
                                    @RequestParam(value = "cardNumber", required = false) String cardNumber,
                                    @RequestParam(value = "cvv", required = false) String cvv,
                                    @RequestParam(value = "expiryDate", required = false) String expiryDate,
                                    Model model) {
        if (userAgreement) {
            String transactionId = UUID.randomUUID().toString();

            PaymentTransaction paymentTransaction = new PaymentTransaction();
            paymentTransaction.setTransactionId(transactionId);
            paymentTransaction.setPaymentMethod(paymentMethod);
            paymentTransaction.setAccountNumber(phoneNumber);
            paymentTransaction.setCardholderName(cardholderName);
            paymentTransaction.setCardNumber(cardNumber);
            paymentTransaction.setCvv(cvv);
            paymentTransaction.setExpiryDate(expiryDate);
        
            
            // Save the payment transaction to the database
            paymentTransactionRepo.save(paymentTransaction);
            
            return "redirect:/confirm_purchase"; 
        } else {
            model.addAttribute("error", "You must agree to the user agreement to proceed!");
            return "purchaseForm"; 
        }
    }

       
}
