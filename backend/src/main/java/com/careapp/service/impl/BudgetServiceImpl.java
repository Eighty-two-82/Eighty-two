package com.careapp.service.impl;

import com.careapp.domain.Budget;
import com.careapp.domain.BudgetCategory;
import com.careapp.domain.BudgetSubElement;
import com.careapp.repository.BudgetRepository;
import com.careapp.service.BudgetCalculationService;
import com.careapp.service.BudgetService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
        return budgetRepository.save(budget);
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
}
