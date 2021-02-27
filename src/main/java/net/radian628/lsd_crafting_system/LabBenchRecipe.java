package net.radian628.lsd_crafting_system;

import org.bukkit.inventory.ItemStack;

public class LabBenchRecipe {
	ItemStack[] ingredients;
	ItemStack product;
	ItemStack[] byproducts;
	
	public LabBenchRecipe(ItemStack[] ing, ItemStack prod, ItemStack[] byprod) {
		ingredients = ing;
		product = prod;
		byproducts = byprod;
	}
	
	public boolean isCraftableWith(ItemStack[] ingredientsToTest) {
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
