package com.careapp.service.impl;

import com.careapp.domain.Budget;
import com.careapp.domain.BudgetCategory;
import com.careapp.domain.BudgetSubElement;
import com.careapp.domain.Patient;
import com.careapp.domain.User;
import com.careapp.repository.BudgetRepository;
import com.careapp.repository.PatientRepository;
import com.careapp.repository.UserRepository;
import com.careapp.service.BudgetCalculationService;
import com.careapp.service.BudgetService;
import com.careapp.service.impl.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of BudgetService
 * Contains all business logic for budget management
 */
@Service
public class BudgetServiceImpl implements BudgetService {

    @Resource
    private BudgetRepository budgetRepository;
    
    @Resource
    private BudgetCalculationService budgetCalculationService;
    
    @Resource
    private EmailService emailService;
    
    @Resource
    private PatientRepository patientRepository;
    
    @Resource
    private UserRepository userRepository;

    @Override
    public Budget createBudget(Budget budget) {
        // Validate budget data
        List<String> validationErrors = budgetCalculationService.validateBudget(budget);
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Budget validation failed: " + String.join(", ", validationErrors));
        }
        
        // Check if budget already exists for this patient
        if (budgetRepository.existsByPatientId(budget.getPatientId())) {
            throw new IllegalArgumentException("Budget already exists for this patient");
        }
        
        // Set timestamps
        budget.setCreatedAt(LocalDateTime.now());
        budget.setUpdatedAt(LocalDateTime.now());
        
        // Generate IDs for categories and sub-elements
        generateIds(budget);
        
        // Calculate all budget metrics
        budget = budgetCalculationService.calculateBudgetMetrics(budget);
        
        // Save to database
        return budgetRepository.save(budget);
    }

    @Override
    public Budget getBudgetByPatientId(String patientId) {
        Optional<Budget> budget = budgetRepository.findByPatientId(patientId);
        if (budget.isPresent()) {
            return budget.get();
        }
        throw new IllegalArgumentException("Budget not found for patient: " + patientId);
    }

    @Override
    public Budget updateBudget(Budget budget) {
        // Validate input
        if (budget.getId() == null || budget.getId().isEmpty()) {
            throw new IllegalArgumentException("Budget ID is required for update");
        }
        
        // Check if budget exists
        if (!budgetRepository.existsById(budget.getId())) {
            throw new IllegalArgumentException("Budget not found with ID: " + budget.getId());
        }
        
        // Update timestamp
        budget.setUpdatedAt(LocalDateTime.now());
        
        // Generate IDs for new categories and sub-elements
        generateIds(budget);
        
        // Save to database
        return budgetRepository.save(budget);
    }

    @Override
    public boolean deleteBudget(String patientId) {
        Optional<Budget> budget = budgetRepository.findByPatientId(patientId);
        if (budget.isPresent()) {
            budgetRepository.delete(budget.get());
            return true;
        }
        return false;
    }

    @Override
    public Budget adjustTotalBudget(String patientId, double newTotalBudget, String reason) {
        Budget budget = getBudgetByPatientId(patientId);
        
        // Validate new budget amount
        if (newTotalBudget < 0) {
            throw new IllegalArgumentException("Budget amount cannot be negative");
        }
        
        // Update total budget
        budget.setTotalBudget(newTotalBudget);
        budget.setUpdatedAt(LocalDateTime.now());
        
        // Save to database
        return budgetRepository.save(budget);
    }

    @Override
    public Budget addCategory(String patientId, BudgetCategory category) {
        Budget budget = getBudgetByPatientId(patientId);
        
        // Generate ID for new category
        if (category.getId() == null || category.getId().isEmpty()) {
            category.setId(UUID.randomUUID().toString());
        }
        
        // Generate IDs for sub-elements
        if (category.getSubElements() != null) {
            for (BudgetSubElement subElement : category.getSubElements()) {
                if (subElement.getId() == null || subElement.getId().isEmpty()) {
                    subElement.setId(UUID.randomUUID().toString());
                }
            }
        }
        
        // Add category to budget
        budget.getCategories().add(category);
        budget.setUpdatedAt(LocalDateTime.now());
        
        // Save to database
        return budgetRepository.save(budget);
    }

    @Override
    public Budget addSubElement(String patientId, String categoryId, BudgetSubElement subElement) {
        Budget budget = getBudgetByPatientId(patientId);
        
        // Find the category
        BudgetCategory category = budget.getCategories().stream()
            .filter(c -> c.getId().equals(categoryId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
        
        // Initialize sub-elements list if null
        if (category.getSubElements() == null) {
            category.setSubElements(new ArrayList<>());
        }
        
        // Generate ID for new sub-element
        if (subElement.getId() == null || subElement.getId().isEmpty()) {
            subElement.setId(UUID.randomUUID().toString());
        }
        
        // Add sub-element to category
        category.getSubElements().add(subElement);
        budget.setUpdatedAt(LocalDateTime.now());
        
        // Save to database
        return budgetRepository.save(budget);
    }

    @Override
    public Budget updateMonthlyUsage(String patientId, String categoryId, String subElementId, 
                                   int month, double amount) {
        // Validate input
        if (month < 0 || month > 11) {
            throw new IllegalArgumentException("Month must be between 0 and 11");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        
        Budget budget = getBudgetByPatientId(patientId);
        
        // Find the category
        BudgetCategory category = budget.getCategories().stream()
            .filter(c -> c.getId().equals(categoryId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
        
        // Find the sub-element
        BudgetSubElement subElement = category.getSubElements().stream()
            .filter(s -> s.getId().equals(subElementId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Sub-element not found: " + subElementId));
        
        // Update monthly usage
        subElement.updateMonthlyUsage(month, amount);
        budget.setUpdatedAt(LocalDateTime.now());
        
        // Recalculate all budget metrics
        budget = budgetCalculationService.calculateBudgetMetrics(budget);
        
        // Save to database
        budget = budgetRepository.save(budget);
        
        // Check for budget alerts and send emails
        checkAndSendBudgetAlerts(budget, patientId, subElement);
        
        return budget;
    }

    @Override
    public Budget reallocateBetweenCategories(String patientId, String fromCategoryId, String toCategoryId, double amount, String reason) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        Budget budget = getBudgetByPatientId(patientId);
        BudgetCategory from = budget.getCategories().stream().filter(c -> c.getId().equals(fromCategoryId))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("From category not found: " + fromCategoryId));
        BudgetCategory to = budget.getCategories().stream().filter(c -> c.getId().equals(toCategoryId))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("To category not found: " + toCategoryId));
        if (from.getCategoryBudget() - amount < 0) {
            throw new IllegalArgumentException("From category budget cannot go negative");
        }
        from.setCategoryBudget(from.getCategoryBudget() - amount);
        to.setCategoryBudget(to.getCategoryBudget() + amount);
        budget.setUpdatedAt(LocalDateTime.now());
        budget = budgetCalculationService.calculateBudgetMetrics(budget);
        return budgetRepository.save(budget);
    }

    @Override
    public Budget reallocateBetweenSubElements(String patientId, String categoryId, String fromSubElementId, String toSubElementId, double amount, String reason) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        Budget budget = getBudgetByPatientId(patientId);
        BudgetCategory category = budget.getCategories().stream()
            .filter(c -> c.getId().equals(categoryId))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
        BudgetSubElement from = category.getSubElements().stream().filter(s -> s.getId().equals(fromSubElementId))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("From sub-element not found: " + fromSubElementId));
        BudgetSubElement to = category.getSubElements().stream().filter(s -> s.getId().equals(toSubElementId))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("To sub-element not found: " + toSubElementId));
        if (from.getSubElementBudget() - amount < 0) {
            throw new IllegalArgumentException("From sub-element budget cannot go negative");
        }
        from.setSubElementBudget(from.getSubElementBudget() - amount);
        to.setSubElementBudget(to.getSubElementBudget() + amount);
        budget.setUpdatedAt(LocalDateTime.now());
        budget = budgetCalculationService.calculateBudgetMetrics(budget);
        return budgetRepository.save(budget);
    }

    @Override
    public Budget refundSubElement(String patientId, String categoryId, String subElementId, double amount, String reason) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        Budget budget = getBudgetByPatientId(patientId);
        BudgetCategory category = budget.getCategories().stream()
            .filter(c -> c.getId().equals(categoryId))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
        BudgetSubElement sub = category.getSubElements().stream().filter(s -> s.getId().equals(subElementId))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Sub-element not found: " + subElementId));
        
        System.out.println("🔍 Backend - refundSubElement called:");
        System.out.println("  Sub-element: " + sub.getName());
        System.out.println("  Original budget: " + sub.getSubElementBudget());
        System.out.println("  Original used: " + sub.getTotalUtilised());
        System.out.println("  Original balance: " + sub.getBalance());
        System.out.println("  Refund amount: " + amount);
        
        // Refund means decrease utilised and increase balance
        double newTotal = Math.max(0, sub.getTotalUtilised() - amount);
        sub.setTotalUtilised(newTotal);
        sub.calculateBalance();
        sub.calculateWarningLevel();
        
        System.out.println("  After refund - used: " + sub.getTotalUtilised());
        System.out.println("  After refund - balance: " + sub.getBalance());
        // Optionally append reason in comments
        String comments = sub.getComments();
        String delta = "Refund: " + amount + (reason != null && !reason.isEmpty() ? (" (" + reason + ")") : "");
        sub.setComments(comments == null || comments.isEmpty() ? delta : comments + "; " + delta);
        budget.setUpdatedAt(LocalDateTime.now());
        
        // Don't call calculateBudgetMetrics here as it will recalculate total  from monthlyUsage
        // which doesn't reflect the refund changes
        return budgetRepository.save(budget);
    }

    @Override
    public Budget updateMonthlyUsageBulk(String patientId, String categoryId, String subElementId, java.util.List<Double> monthlyAmounts) {
        if (monthlyAmounts == null || monthlyAmounts.size() != 12) {
            throw new IllegalArgumentException("monthlyAmounts must be 12-length list");
        }
        Budget budget = getBudgetByPatientId(patientId);
        BudgetCategory category = budget.getCategories().stream()
            .filter(c -> c.getId().equals(categoryId))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
        BudgetSubElement sub = category.getSubElements().stream().filter(s -> s.getId().equals(subElementId))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Sub-element not found: " + subElementId));
        sub.setMonthlyUsage(monthlyAmounts);
        budget.setUpdatedAt(LocalDateTime.now());
        budget = budgetCalculationService.calculateBudgetMetrics(budget);
        budget = budgetRepository.save(budget);
        
        // Check for budget alerts and send emails
        checkAndSendBudgetAlerts(budget, patientId, sub);
        
        return budget;
    }


    /**
     * Generate unique IDs for categories and sub-elements that don't have IDs
     * @param budget Budget object to process
     */
    private void generateIds(Budget budget) {
        if (budget.getCategories() != null) {
            for (BudgetCategory category : budget.getCategories()) {
                if (category.getId() == null || category.getId().isEmpty()) {
                    category.setId(UUID.randomUUID().toString());
                }
                
                if (category.getSubElements() != null) {
                    for (BudgetSubElement subElement : category.getSubElements()) {
                        if (subElement.getId() == null || subElement.getId().isEmpty()) {
                            subElement.setId(UUID.randomUUID().toString());
                        }
                    }
                }
            }
        }
    }

    /**
     * Check budget usage and send alert emails if necessary
     * @param budget The budget to check
     * @param patientId The patient ID
     * @param updatedSubElement The sub-element that was updated
     */
    private void checkAndSendBudgetAlerts(Budget budget, String patientId, BudgetSubElement updatedSubElement) {
        try {
            // Get patient information
            Patient patient = patientRepository.findById(patientId).orElse(null);
            if (patient == null || !StringUtils.hasText(patient.getPoaId())) {
                return; // No patient or no POA, skip email
            }
            
            // Get POA user information
            User poaUser = userRepository.findById(patient.getPoaId()).orElse(null);
            if (poaUser == null || !StringUtils.hasText(poaUser.getEmail())) {
                return; // No POA user or no email, skip email
            }
            
            // Check if the updated sub-element exceeds warning thresholds
            double monthlyUsage = updatedSubElement.getMonthlyUsage().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
            double budgetAmount = updatedSubElement.getSubElementBudget();
            double usagePercentage = (monthlyUsage / budgetAmount) * 100;
            
            String alertLevel = null;
            String subject = null;
            String emailContent = null;
            
            // Determine alert level
            if (usagePercentage >= 100) {
                alertLevel = "CRITICAL";
                subject = "🚨 CareTrack Budget Alert - CRITICAL: Budget Exceeded";
                emailContent = String.format(
                    "Dear %s,\n\n" +
                    "🚨 CRITICAL BUDGET ALERT for %s\n\n" +
                    "The budget for '%s' has been EXCEEDED!\n\n" +
                    "Budget Details:\n" +
                    "• Category: %s\n" +
                    "• Sub-element: %s\n" +
                    "• Budget Amount: $%.2f\n" +
                    "• Current Usage: $%.2f\n" +
                    "• Usage Percentage: %.1f%%\n\n" +
                    "Please review the budget immediately and take necessary actions.\n\n" +
                    "Best regards,\n" +
                    "CareTrack Team",
                    poaUser.getFirstName() != null ? poaUser.getFirstName() : "POA",
                    patient.getFirstName() != null ? patient.getFirstName() : "Patient",
                    updatedSubElement.getName(),
                    budget.getCategories().stream()
                        .filter(cat -> cat.getSubElements().contains(updatedSubElement))
                        .findFirst().map(BudgetCategory::getName).orElse("Unknown"),
                    updatedSubElement.getName(),
                    budgetAmount,
                    monthlyUsage,
                    usagePercentage
                );
            } else if (usagePercentage >= 80) {
                alertLevel = "WARNING";
                subject = "⚠️ CareTrack Budget Alert - WARNING: Budget Nearly Exceeded";
                emailContent = String.format(
                    "Dear %s,\n\n" +
                    "⚠️ BUDGET WARNING for %s\n\n" +
                    "The budget for '%s' is approaching its limit.\n\n" +
                    "Budget Details:\n" +
                    "• Category: %s\n" +
                    "• Sub-element: %s\n" +
                    "• Budget Amount: $%.2f\n" +
                    "• Current Usage: $%.2f\n" +
                    "• Usage Percentage: %.1f%%\n\n" +
                    "Please monitor the budget closely to avoid exceeding the limit.\n\n" +
                    "Best regards,\n" +
                    "CareTrack Team",
                    poaUser.getFirstName() != null ? poaUser.getFirstName() : "POA",
                    patient.getFirstName() != null ? patient.getFirstName() : "Patient",
                    updatedSubElement.getName(),
                    budget.getCategories().stream()
                        .filter(cat -> cat.getSubElements().contains(updatedSubElement))
                        .findFirst().map(BudgetCategory::getName).orElse("Unknown"),
                    updatedSubElement.getName(),
                    budgetAmount,
                    monthlyUsage,
                    usagePercentage
                );
            }
            
            // Send email if alert is needed
            if (alertLevel != null) {
                emailService.sendText(poaUser.getEmail(), subject, emailContent);
                System.out.println("Budget alert email sent to " + poaUser.getEmail() + " for " + alertLevel + " level alert");
            }
            
        } catch (Exception e) {
            // Log error but don't fail the budget update
            System.err.println("Failed to send budget alert email: " + e.getMessage());
        }
    }
}
