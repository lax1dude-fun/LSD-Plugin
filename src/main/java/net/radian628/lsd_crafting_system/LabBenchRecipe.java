package net.radian628.lsd_crafting_system;

import org.bukkit.inventory.ItemStack;

public class LabBenchRecipe {
	ItemStack[] ingredients;
	ItemStack product;
	ItemStack[] byproducts;
	String name;
	
	public LabBenchRecipe(ItemStack[] ing, ItemStack prod, ItemStack[] byprod, String recipeName) {
		ingredients = ing;
		product = prod;
		byproducts = byprod;
		name = recipeName;
	}
	
	public boolean singleIngredientMatches(ItemStack ingredientToTest, ItemStack requiredIngredient) {
		if (!(ingredientToTest == null ^ requiredIngredient == null)) {
			if (ingredientToTest == null && requiredIngredient == null) {
				return true;
			} else if (ingredientToTest.getType() == requiredIngredient.getType()) {
				if (!requiredIngredient.getItemMeta().hasCustomModelData() && !ingredientToTest.getItemMeta().hasCustomModelData()) {
					return true;
				} else if (requiredIngredient.getItemMeta().hasCustomModelData() && ingredientToTest.getItemMeta().hasCustomModelData()) {
					if (requiredIngredient.getItemMeta().getCustomModelData() == ingredientToTest.getItemMeta().getCustomModelData()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean matches(ItemStack[] ingredientsToTest) {
		for (int i = 0; 9 > i; i++) {
			boolean ingredientMatches = singleIngredientMatches(ingredientsToTest[i], ingredients[i]);
			/*if (!(ingredientsToTest[i] == null ^ ingredients[i] == null)) {
				if (ingredientsToTest[i] == null && ingredients[i] == null) {
					ingredientMatches = true;
				} else if (ingredientsToTest[i].getType() == ingredients[i].getType()) {
					if (!ingredients[i].getItemMeta().hasCustomModelData() && !ingredientsToTest[i].getItemMeta().hasCustomModelData()) {
						ingredientMatches = true;
					} else if (ingredients[i].getItemMeta().hasCustomModelData() && ingredientsToTest[i].getItemMeta().hasCustomModelData()) {
						if (ingredients[i].getItemMeta().hasCustomModelData() == ingredientsToTest[i].getItemMeta().hasCustomModelData()) {
							ingredientMatches = true;
						}
					}
				}
			}*/
			
			if (!ingredientMatches) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isCraftableWithAmounts(ItemStack[] ingredientsToTest) {
		for (int i = 0; 9 > i; i++) {
			if (ingredients[i] != null) {
				if (ingredientsToTest[i] == null || ingredients[i].getAmount() > ingredientsToTest[i].getAmount()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void consume(ItemStack[] ingredientsToConsume, LabBench labBench) {
		for (int i = 0; 9 > i; i++) {
			if (ingredients[i] != null) {
				ingredientsToConsume[i].setAmount(ingredientsToConsume[i].getAmount() - ingredients[i].getAmount());
				labBench.labInventory.setItem(labBench.getEditableSlotIndex(i), ingredientsToConsume[i]);
			}
		}
	}
	
}
