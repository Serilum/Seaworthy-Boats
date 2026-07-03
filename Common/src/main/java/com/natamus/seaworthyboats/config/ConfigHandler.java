package com.natamus.seaworthyboats.config;

import com.natamus.collective.config.DuskConfig;
import com.natamus.seaworthyboats.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<String, List<String>>();

	@Entry public static boolean disablePlayerBoatDamage = true;

	@Entry public static boolean showBoatHealthBar = true;
	@Entry(min = 1, max = 1000) public static int boatMaxHealth = 20;
	@Entry(min = 1, max = 100) public static int healthPerPlank = 2;

	@Entry(min = 1, max = 16) public static int shipyardRange = 4;
	@Entry(min = 0, max = 1000) public static int shipyardInfoYOffset = 8;

	@Entry public static boolean sequentialUpgrades = true;
	@Entry(min = 1, max = 64) public static int materialsPerUpgrade = 8;
	@Entry(min = 0, max = 1.0) public static double copperArmour = 0.07;
	@Entry(min = 0, max = 1.0) public static double goldArmour = 0.10;
	@Entry(min = 0, max = 1.0) public static double ironArmour = 0.25;
	@Entry(min = 0, max = 1.0) public static double diamondArmour = 0.40;
	@Entry(min = 0, max = 1.0) public static double netheriteArmour = 0.80;

	@Entry(min = 0.1, max = 20.0) public static double copperSpeed = 1.10;
	@Entry(min = 0.1, max = 20.0) public static double goldSpeed = 1.20;
	@Entry(min = 0.1, max = 20.0) public static double ironSpeed = 1.35;
	@Entry(min = 0.1, max = 20.0) public static double diamondSpeed = 1.55;
	@Entry(min = 0.1, max = 20.0) public static double netheriteSpeed = 1.80;

	public static void initConfig() {
		configMetaData.put("disablePlayerBoatDamage", Arrays.asList(
			"Whether a player's own hits deal no damage to boats. When on, hitting a boat tells you to sneak and right-click empty-handed to pick it up instead. Mobs and other sources still damage boats."
		));

		configMetaData.put("showBoatHealthBar", Arrays.asList(
			"Whether to show the boat's health as a row of hearts on the HUD while riding it, like a horse's health. Hides the hunger bar while in a boat, the same way vanilla mounts do."
		));
		configMetaData.put("boatMaxHealth", Arrays.asList(
			"The amount of health a boat has before it breaks. Each point of incoming damage lowers it by one (a trident hit deals around 8). Vanilla boats break after about 4 damage."
		));
		configMetaData.put("healthPerPlank", Arrays.asList(
			"How much boat health a single plank restores when repairing at a shipyard in repair mode."
		));

		configMetaData.put("shipyardRange", Arrays.asList(
			"How close a boat must be to a shipyard block for it to be repaired or upgraded there, in blocks."
		));
		configMetaData.put("shipyardInfoYOffset", Arrays.asList(
			"How far down from the top of the screen, in pixels, the shipyard info panel is drawn when you look at a shipyard. Increase it to move the panel below other mods' block tooltips, such as Jade or WAILA."
		));

		configMetaData.put("sequentialUpgrades", Arrays.asList(
			"Whether a boat must be reinforced one tier at a time, in order: copper, gold, iron, diamond, netherite. When off, a boat can be upgraded straight to any higher tier with the matching material."
		));
		configMetaData.put("materialsPerUpgrade", Arrays.asList(
			"How many of a material it costs to reinforce a boat to its tier. Reinforce a boat at a shipyard set to upgrade mode. Netherite reinforcement costs half this amount."
		));
		configMetaData.put("copperArmour", Arrays.asList(
			"How much incoming damage a copper-reinforced boat ignores. 0.0 is no protection, 1.0 is full immunity."
		));
		configMetaData.put("goldArmour", Arrays.asList(
			"How much incoming damage a gold-reinforced boat ignores. 0.0 is no protection, 1.0 is full immunity."
		));
		configMetaData.put("ironArmour", Arrays.asList(
			"How much incoming damage an iron-reinforced boat ignores. 0.0 is no protection, 1.0 is full immunity."
		));
		configMetaData.put("diamondArmour", Arrays.asList(
			"How much incoming damage a diamond-reinforced boat ignores. 0.0 is no protection, 1.0 is full immunity."
		));
		configMetaData.put("netheriteArmour", Arrays.asList(
			"How much incoming damage a netherite-reinforced boat ignores. 0.0 is no protection, 1.0 is full immunity."
		));

		configMetaData.put("copperSpeed", Arrays.asList(
			"Forward speed multiplier for a copper-reinforced boat. 1.0 is vanilla boat speed, 1.5 is fifty percent faster."
		));
		configMetaData.put("goldSpeed", Arrays.asList(
			"Forward speed multiplier for a gold-reinforced boat. 1.0 is vanilla boat speed, 1.5 is fifty percent faster."
		));
		configMetaData.put("ironSpeed", Arrays.asList(
			"Forward speed multiplier for an iron-reinforced boat. 1.0 is vanilla boat speed, 1.5 is fifty percent faster."
		));
		configMetaData.put("diamondSpeed", Arrays.asList(
			"Forward speed multiplier for a diamond-reinforced boat. 1.0 is vanilla boat speed, 1.5 is fifty percent faster."
		));
		configMetaData.put("netheriteSpeed", Arrays.asList(
			"Forward speed multiplier for a netherite-reinforced boat. 1.0 is vanilla boat speed, 1.5 is fifty percent faster."
		));


		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}