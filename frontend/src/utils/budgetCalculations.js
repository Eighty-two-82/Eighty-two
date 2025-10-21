// src/utils/budgetCalculations.js
// Shared budget calculation utilities to ensure consistency across components

/**
 * Calculate total used amount from budget data
 * @param {Object} budgetData - Budget data object with categories
 * @returns {number} Total used amount
 */
export function calculateTotalUsed(budgetData) {
  if (!budgetData || !budgetData.categories) {
    return 0
  }
  
  return budgetData.categories.reduce((total, category) => {
    return total + (category.subElements || []).reduce((categoryTotal, subElement) => {
      return categoryTotal + (subElement.totalUtilised || 0)
    }, 0)
  }, 0)
}

/**
 * Calculate total balance (remaining budget)
 * @param {Object} budgetData - Budget data object
 * @returns {number} Total balance
 */
export function calculateTotalBalance(budgetData) {
  if (!budgetData) {
    return 0
  }
  
  const totalBudget = budgetData.totalBudget || 0
  const totalUsed = calculateTotalUsed(budgetData)
  return totalBudget - totalUsed
}

/**
 * Calculate total usage percentage
 * @param {Object} budgetData - Budget data object
 * @returns {number} Usage percentage (0-100)
 */
export function calculateUsagePercentage(budgetData) {
  if (!budgetData) {
    return 0
  }
  
  const totalBudget = budgetData.totalBudget || 0
  if (totalBudget === 0) {
    return 0
  }
  
  const totalUsed = calculateTotalUsed(budgetData)
  return Math.round((totalUsed / totalBudget) * 100)
}

/**
 * Get usage color based on percentage
 * @param {number} percentage - Usage percentage
 * @returns {string} Color class
 */
export function getUsageColor(percentage) {
  if (percentage >= 100) return 'red'
  if (percentage >= 80) return 'orange'
  return 'green'
}

/**
 * Calculate category usage data
 * @param {Array} categories - Array of budget categories
 * @returns {Array} Array of category usage data
 */
export function calculateCategoryUsage(categories) {
  if (!categories) {
    return []
  }
  
  return categories.map(category => {
    const categoryUsed = (category.subElements || []).reduce((sum, subElement) => sum + (subElement.totalUtilised || 0), 0)
    const categoryBudget = category.categoryBudget || 0
    const percentage = categoryBudget > 0 ? Math.round((categoryUsed / categoryBudget) * 100) : 0
    
    return {
      ...category,
      totalUtilised: categoryUsed,
      usagePercentage: percentage,
      balance: categoryBudget - categoryUsed,
      usageColor: getUsageColor(percentage)
    }
  })
}

/**
 * Get top categories by usage percentage
 * @param {Array} categories - Array of budget categories
 * @param {number} limit - Number of top categories to return
 * @returns {Array} Top categories sorted by usage percentage
 */
export function getTopCategoriesByUsage(categories, limit = 3) {
  if (!categories) {
    return []
  }
  
  const categoryUsage = categories.map(category => {
    const categoryUsed = (category.subElements || []).reduce((sum, subElement) => sum + (subElement.totalUtilised || 0), 0)
    const categoryBudget = category.categoryBudget || 0
    const percentage = categoryBudget > 0 ? Math.round((categoryUsed / categoryBudget) * 100) : 0
    
    return {
      category: category.name,
      used: categoryUsed,
      budget: categoryBudget,
      pct: percentage
    }
  })
  
  return categoryUsage
    .sort((a, b) => b.pct - a.pct)
    .slice(0, limit)
}

/**
 * Get budget warnings
 * @param {Object} budgetData - Budget data object
 * @returns {Array} Array of warning objects
 */
export function getBudgetWarnings(budgetData) {
  if (!budgetData || !budgetData.categories) {
    return []
  }
  
  const warnings = []
  budgetData.categories.forEach(category => {
    (category.subElements || []).forEach(subElement => {
      const percentage = subElement.subElementBudget > 0 ? (subElement.totalUtilised / subElement.subElementBudget) * 100 : 0
      if (percentage >= 80) {
        let level = 'warning'
        if (percentage >= 100) {
          level = 'critical'
        }
        
        warnings.push({
          category: category.name,
          subElement: subElement.name,
          percentage: Math.round(percentage),
          utilised: subElement.totalUtilised,
          budget: subElement.subElementBudget,
          balance: subElement.balance,
          level: level
        })
      }
    })
  })
  return warnings
}
