package com.careapp.controller;

import com.careapp.domain.Budget;
import com.careapp.domain.BudgetCategory;
import com.careapp.domain.BudgetSubElement;
import com.careapp.service.BudgetCalculationService;
import com.careapp.service.BudgetService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Budget operations
 * Provides API endpoints for budget management
 */
@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Resource
    private BudgetService budgetService;
    
    @Resource
    private BudgetCalculationService budgetCalculationService;


    // Basic CRUD operations

    /**
     * Create a new budget
     * POST /api/budget
     */
    @PostMapping
    public Result<Budget> createBudget(@RequestBody Budget budget) {
        try {
            Budget createdBudget = budgetService.createBudget(budget);
            return Result.success(createdBudget, "Budget created successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to create budget: " + e.getMessage());
        }
    }

    /**
     * Get budget by patient ID
     * GET /api/budget/patient/{patientId}
     */
    @GetMapping("/patient/{patientId}")
    public Result<Budget> getBudgetByPatient(@PathVariable String patientId) {
        try {
            Budget budget = budgetService.getBudgetByPatientId(patientId);
            return Result.success(budget, "Budget retrieved successfully!");
        } catch (Exception e) {
            return Result.error("404", "Budget not found: " + e.getMessage());
        }
    }

    /**
     * Update existing budget
     * PUT /api/budget
     */
    @PutMapping
    public Result<Budget> updateBudget(@RequestBody Budget budget) {
        try {
            Budget updatedBudget = budgetService.updateBudget(budget);
            return Result.success(updatedBudget, "Budget updated successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to update budget: " + e.getMessage());
        }
    }

    /**
     * Delete budget by patient ID
     * DELETE /api/budget/patient/{patientId}
     */
    @DeleteMapping("/patient/{patientId}")
    public Result<String> deleteBudget(@PathVariable String patientId) {
        try {
            boolean deleted = budgetService.deleteBudget(patientId);
            if (deleted) {
                return Result.success("Budget deleted", "Budget deleted successfully!");
            } else {
                return Result.error("404", "Budget not found");
            }
        } catch (Exception e) {
            return Result.error("400", "Failed to delete budget: " + e.getMessage());
        }
    }

    // Budget adjustment operations

    /**
     * Adjust total budget amount
     * POST /api/budget/adjust-total
     */
    @PostMapping("/adjust-total")
    public Result<Budget> adjustTotalBudget(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            Double newTotalBudget = Double.valueOf(request.get("newTotalBudget").toString());
            String reason = (String) request.get("reason");

            Budget updatedBudget = budgetService.adjustTotalBudget(patientId, newTotalBudget, reason);
            return Result.success(updatedBudget, "Total budget adjusted successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to adjust total budget: " + e.getMessage());
        }
    }

    /**
     * Reallocate budget between two categories
     * POST /api/budget/reallocate-category
     */
    @PostMapping("/reallocate-category")
    public Result<Budget> reallocateBetweenCategories(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            String fromCategoryId = (String) request.get("fromCategoryId");
            String toCategoryId = (String) request.get("toCategoryId");
            Double amount = Double.valueOf(request.get("amount").toString());
            String reason = (String) request.get("reason");

            Budget updated = budgetService.reallocateBetweenCategories(patientId, fromCategoryId, toCategoryId, amount, reason);
            return Result.success(updated, "Category reallocation completed!");
        } catch (Exception e) {
            return Result.error("400", "Failed to reallocate category budget: " + e.getMessage());
        }
    }

    /**
     * Reallocate budget between two sub-elements within a category
     * POST /api/budget/reallocate-subelement
     */
    @PostMapping("/reallocate-subelement")
    public Result<Budget> reallocateBetweenSubElements(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            String categoryId = (String) request.get("categoryId");
            String fromSubElementId = (String) request.get("fromSubElementId");
            String toSubElementId = (String) request.get("toSubElementId");
            Double amount = Double.valueOf(request.get("amount").toString());
            String reason = (String) request.get("reason");

            Budget updated = budgetService.reallocateBetweenSubElements(patientId, categoryId, fromSubElementId, toSubElementId, amount, reason);
            return Result.success(updated, "Sub-element reallocation completed!");
        } catch (Exception e) {
            return Result.error("400", "Failed to reallocate sub-element budget: " + e.getMessage());
        }
    }

    /**
     * Process a refund for a sub-element (decrease utilised, increase balance)
     * POST /api/budget/refund
     */
    @PostMapping("/refund")
    public Result<Budget> refundSubElement(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            String categoryId = (String) request.get("categoryId");
            String subElementId = (String) request.get("subElementId");
            Double amount = Double.valueOf(request.get("amount").toString());
            String reason = (String) request.get("reason");

            Budget updated = budgetService.refundSubElement(patientId, categoryId, subElementId, amount, reason);
            return Result.success(updated, "Refund processed successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to process refund: " + e.getMessage());
        }
    }

    /**
     * Add new category to budget
     * POST /api/budget/category
     */
    @PostMapping("/category")
    public Result<Budget> addCategory(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            
            // Create BudgetCategory from request
            BudgetCategory category = new BudgetCategory();
            category.setName((String) request.get("name"));
            category.setDescription((String) request.get("description"));
            category.setCategoryBudget(Double.valueOf(request.get("categoryBudget").toString()));

            Budget updatedBudget = budgetService.addCategory(patientId, category);
            return Result.success(updatedBudget, "Category added successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to add category: " + e.getMessage());
        }
    }

    /**
     * Add new sub-element to a category
     * POST /api/budget/sub-element
     */
    @PostMapping("/sub-element")
    public Result<Budget> addSubElement(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            String categoryId = (String) request.get("categoryId");
            
            // Create BudgetSubElement from request
            BudgetSubElement subElement = new BudgetSubElement();
            subElement.setName((String) request.get("name"));
            subElement.setDescription((String) request.get("description"));
            subElement.setSubElementBudget(Double.valueOf(request.get("subElementBudget").toString()));

            Budget updatedBudget = budgetService.addSubElement(patientId, categoryId, subElement);
            return Result.success(updatedBudget, "Sub-element added successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to add sub-element: " + e.getMessage());
        }
    }

    /**
     * Update monthly usage for a sub-element
     * POST /api/budget/monthly-usage
     */
    @PostMapping("/monthly-usage")
    public Result<Budget> updateMonthlyUsage(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            String categoryId = (String) request.get("categoryId");
            String subElementId = (String) request.get("subElementId");
            Integer month = Integer.valueOf(request.get("month").toString());
            Double amount = Double.valueOf(request.get("amount").toString());

            Budget updatedBudget = budgetService.updateMonthlyUsage(patientId, categoryId, subElementId, month, amount);
            return Result.success(updatedBudget, "Monthly usage updated successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to update monthly usage: " + e.getMessage());
        }
    }

    /**
     * Bulk update monthly usage for a sub-element (12 months array)
     * POST /api/budget/monthly-usage/bulk
     */
    @PostMapping("/monthly-usage/bulk")
    public Result<Budget> updateMonthlyUsageBulk(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            String categoryId = (String) request.get("categoryId");
            String subElementId = (String) request.get("subElementId");
            @SuppressWarnings("unchecked")
            List<Object> raw = (List<Object>) request.get("monthlyUsage");
            if (raw == null) {
                return Result.error("400", "monthlyUsage is required and must be an array of 12 numbers");
            }
            java.util.ArrayList<Double> monthly = new java.util.ArrayList<>();
            for (Object v : raw) {
                if (v == null) {
                    monthly.add(0.0);
                } else if (v instanceof Number) {
                    monthly.add(((Number) v).doubleValue());
                } else {
                    monthly.add(Double.valueOf(v.toString()));
                }
            }
            Budget updated = budgetService.updateMonthlyUsageBulk(patientId, categoryId, subElementId, monthly);
            return Result.success(updated, "Monthly usage updated successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to update monthly usage: " + e.getMessage());
        }
    }

    // Query operations


    // Budget analysis and warnings

    /**
     * Get budget warnings for a patient
     * GET /api/budget/warnings/{patientId}
     */
    @GetMapping("/warnings/{patientId}")
    public Result<List<String>> getBudgetWarnings(@PathVariable String patientId) {
        try {
            Budget budget = budgetService.getBudgetByPatientId(patientId);
            List<String> warnings = budgetCalculationService.getBudgetWarnings(budget);
            return Result.success(warnings, "Budget warnings retrieved successfully!");
        } catch (Exception e) {
            return Result.error("404", "Failed to retrieve budget warnings: " + e.getMessage());
        }
    }

    /**
     * Get budget statistics for a patient
     * GET /api/budget/statistics/{patientId}
     */
    @GetMapping("/statistics/{patientId}")
    public Result<Map<String, Object>> getBudgetStatistics(@PathVariable String patientId) {
        try {
            Budget budget = budgetService.getBudgetByPatientId(patientId);
            
            Map<String, Object> statistics = Map.of(
                "totalBudget", budget.getTotalBudget(),
                "totalUsed", budgetCalculationService.calculateTotalUsed(budget),
                "totalBalance", budgetCalculationService.calculateTotalBalance(budget),
                "usagePercentage", budgetCalculationService.calculateUsagePercentage(budget),
                "isOverBudget", budgetCalculationService.isOverBudget(budget),
                "warnings", budgetCalculationService.getBudgetWarnings(budget)
            );
            
            return Result.success(statistics, "Budget statistics retrieved successfully!");
        } catch (Exception e) {
            return Result.error("404", "Failed to retrieve budget statistics: " + e.getMessage());
        }
    }

    /**
     * Validate budget data
     * POST /api/budget/validate
     */
    @PostMapping("/validate")
    public Result<List<String>> validateBudget(@RequestBody Budget budget) {
        try {
            List<String> validationErrors = budgetCalculationService.validateBudget(budget);
            if (validationErrors.isEmpty()) {
                return Result.success(validationErrors, "Budget validation passed!");
            } else {
                return Result.success(validationErrors, "Budget validation failed with " + validationErrors.size() + " errors");
            }
        } catch (Exception e) {
            return Result.error("400", "Failed to validate budget: " + e.getMessage());
        }
    }

}
