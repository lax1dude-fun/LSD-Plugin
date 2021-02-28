package net.radian628.lsd_crafting_system;

import org.bukkit.inventory.ItemStack;

public class ShapelessLabBenchRecipe extends LabBenchRecipe {
	public ShapelessLabBenchRecipe(ItemStack[] ing, ItemStack prod, ItemStack[] byprod, String recipeName) {
		super(ing, prod, byprod, recipeName);
	}
	
	@Override
	public boolean matches(ItemStack[] ingredientsToTest) {
		for (int i = 0; 9 > i; i++) {
			
			ItemStack ingredient = ingredients[i];
			
			boolean ingredientMatches = false;
			
			for (int ii = 0; 9 > ii; ii++) {
				ItemStack ingredientToTest = ingredientsToTest[ii];
				if (singleIngredientMatches(ingredientToTest, ingredient)) {
					ingredientMatches = true;
					break;
				}
			}
			
			if (!ingredientMatches) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isCraftableWithAmounts(ItemStack[] ingredientsToTest) {
		return matches(ingredientsToTest);
	}
	
	public void consume(ItemStack[] ingredientsToConsume, LabBench labBench) {
		for (int i = 0; 9 > i; i++) {
			ItemStack ingredient = ingredients[i];
			if (ingredient != null) {
				
				int consumeIndex = -1;
				
				for (int ii = 0; 9 > ii; ii++) {
					if (singleIngredientMatches(ingredientsToConsume[ii], ingredient)) {
						consumeIndex = ii;
						break;
					}
				}
				
				ItemStack ingredientToConsume = ingredientsToConsume[consumeIndex];
	
				ingredientToConsume.setAmount(ingredientToConsume.getAmount() - ingredient.getAmount());
				labBench.labInventory.setItem(labBench.getEditableSlotIndex(consumeIndex), ingredientToConsume);
			}
		}
	}
	
}
