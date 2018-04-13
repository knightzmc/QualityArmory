package me.zombie_striker.qg.miscitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.ArmoryBaseObject;
import me.zombie_striker.qg.MaterialStorage;

public class MeleeItems implements ArmoryBaseObject{
	MaterialStorage ms;
	String displayname;
	int cost;
	String name;

	List<UUID> medkitHeartUsage = new ArrayList<>();
	HashMap<UUID, Long> lastTimeHealed = new HashMap<>();
	HashMap<UUID, Double> PercentTimeHealed = new HashMap<>();

	ItemStack[] ing = null;

	int damage = 1;
	
	public MeleeItems(MaterialStorage ms, String name, String displayname, ItemStack[] ings, int cost, int damage) {
		this.ms = ms;
		this.displayname = displayname;
		this.cost = cost;
		this.name = name;
		this.ing = ings;
		this.damage = damage;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ItemStack[] getIngredients() {
		return ing;
	}

	@Override
	public int getCraftingReturn() {
		return 1;
	}

	@Override
	public MaterialStorage getItemData() {
		return ms;
	}

	@Override
	public List<String> getCustomLore() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return displayname;
	}

	@Override
	public double cost() {
		return cost;
	}
	public int getDamage() {
		return damage;
	}
}
