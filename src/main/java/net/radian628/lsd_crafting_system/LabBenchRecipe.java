package net.radian628.lsd_crafting_system;

import org.bukkit.inventory.ItemStack;

public class LabBenchRecipe {
	public LabBenchRecipe(ItemStack[] ing, ItemStack prod) {
		ingredients = ing;
		product = prod;
	}
	
	public boolean isCraftableWith(ItemStack[] ingredientsToTest) {
		for (int i = 0; 9 > i; i++) {
			if (!ingredients[i].isSimilar(ingredientsToTest[i]) || ingredients[i].getAmount() > ingredientsToTest[i].getAmount()) {
				return false;
			}
		}
		
		return true;
	}
	
	public void consume(ItemStack[] ingredientsToConsume, LabBench labBench) {
		for (int i = 0; 9 > i; i++) {
			ingredientsToConsume[i].setAmount(ingredientsToConsume[i].getAmount() - ingredients[i].getAmount());
			labBench.labInventory.setItem(labBench.getEditableSlotIndex(i), ingredientsToConsume[i]);
		}
	}
	
	ItemStack[] ingredients;
	ItemStack product;
}
