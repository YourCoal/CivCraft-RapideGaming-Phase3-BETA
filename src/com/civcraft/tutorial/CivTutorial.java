package com.civcraft.tutorial;

import gpl.AttributeUtil;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.civcraft.config.ConfigMaterial;
import com.civcraft.config.ConfigMaterialCategory;
import com.civcraft.lorestorage.LoreCraftableMaterial;
import com.civcraft.lorestorage.LoreGuiItem;
import com.civcraft.lorestorage.LoreGuiItemListener;
import com.civcraft.lorestorage.LoreMaterial;
import com.civcraft.main.CivGlobal;
import com.civcraft.util.CivColor;
import com.civcraft.util.ItemManager;

public class CivTutorial {
	
	public static Inventory serverInfoInventory = null;
	public static Inventory tutorialInventory = null;
	public static Inventory craftingHelpInventory = null;
	public static Inventory guiInventory = null;
	public static final int MAX_CHEST_SIZE = 6;
	
	public static void showServerInfoInventory(Player player) {	
		if (serverInfoInventory == null) {
			serverInfoInventory = Bukkit.getServer().createInventory(player, 9*2, "Server Links");
			
			serverInfoInventory.setItem(0, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+"Website", ItemManager.getId(Material.PAPER), 0, 
				ChatColor.RESET+"http://rapidegaming.enjin.com/"
			));
			
			serverInfoInventory.setItem(2, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+"Voting", ItemManager.getId(Material.BEACON), 0, 
				ChatColor.RESET+"http://tinyurl.com/rgnvotetemp/"
			));
			
			serverInfoInventory.setItem(4, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+"Dynmap", ItemManager.getId(Material.MAP), 0, 
				ChatColor.RESET+"http://mc.rapidegaming.com:8123/"
			));
			
			serverInfoInventory.setItem(6, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+"Wiki", ItemManager.getId(Material.ENDER_PORTAL_FRAME), 0, 
				ChatColor.RESET+"https://github.com/YourCoal/Wiki/wiki"
			));
			
			serverInfoInventory.setItem(8, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+"YourCoal's YouTube", ItemManager.getId(Material.REDSTONE_BLOCK), 0, 
				ChatColor.RESET+"https://www.youtube.com/user/cpcole556"
			));
			LoreGuiItemListener.guiInventories.put(serverInfoInventory.getName(), serverInfoInventory);
		}
		
		if (player != null && player.isOnline() && player.isValid()) {
			player.openInventory(serverInfoInventory);	
		}
	}
	
	public static void showTutorialInventory(Player player) {	
		if (tutorialInventory == null) {
			tutorialInventory = Bukkit.getServer().createInventory(player, 9*2, "Custom CivCraft Tutorial");
			
			tutorialInventory.setItem(0, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+"What is CivCraft?", ItemManager.getId(Material.WORKBENCH), 0, 
				ChatColor.RESET+"CivCraft requires those of many skills; the",
				ChatColor.RESET+"skills of gathering, fighting, and most of all,",
				ChatColor.RESET+"knowledge. You must remember the basics for you",
				ChatColor.RESET+"to actually have success. You will need to also",
				ChatColor.RESET+"gether allies, create friendships, and prepare to",
				ChatColor.RESET+"do things you don't like. "+CivColor.LightGreen+"Are you ready?"
			));
			
			tutorialInventory.setItem(3, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+"Explore!", ItemManager.getId(Material.COMPASS), 0, 
				ChatColor.RESET+"You will need to venture out of spawn, into the",
				ChatColor.RESET+"wildnerness in order to find the resources you",
				ChatColor.RESET+"will need. These resources include tradeposts,",
				ChatColor.RESET+"ruins, and, the most common, other player's",
				ChatColor.RESET+"civilizations. Each biome will bring you different",
				ChatColor.RESET+"options. Find the ones that suit you."
			));
			
			tutorialInventory.setItem(4, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+"Resources and Materials", ItemManager.getId(Material.DIAMOND_ORE), 0, 
				ChatColor.RESET+"There are many different custom items you can craft.",
				ChatColor.RESET+"These items are crafted like regular items in a",
				ChatColor.RESET+"workbench, and can be used to make other items.",
				ChatColor.RESET+"When caving, ingots can be sold at a "+CivColor.Yellow+"Bank.",
				ChatColor.RESET+"Some items can be bought/sold at the "+CivColor.Yellow+"Market."
			));
			
			if (CivGlobal.isCasualMode()) {
				tutorialInventory.setItem(7, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+"Casual War!", ItemManager.getId(Material.FIREWORK), 0, 
					ChatColor.RESET+"War allows civilizations to settle their differences.",
					ChatColor.RESET+"In casual mode, Civs have to the option to request war from",
					ChatColor.RESET+"each other. The winner of a war is awarded a trophy which can be",
					ChatColor.RESET+"displayed in an item frame for bragging rights.",
					ChatColor.RESET+"After a civilization is defeated in war, war must be requested again."
					));
			} else {
				tutorialInventory.setItem(7, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+"War!", ItemManager.getId(Material.IRON_SWORD), 0, 
					ChatColor.RESET+"War allows civilizations to settle their differences.",
					ChatColor.RESET+"Normally, all structures inside a civilization are protected",
					ChatColor.RESET+"from damage. However civs have to the option to declare war on",
					ChatColor.RESET+"each other and do damage to each other's structures, and even capture",
					ChatColor.RESET+"towns from each other. Each weekend, WarTime is enabled for two hours",
					ChatColor.RESET+"during which players at war must defend their civ and conquer their enemies."
				));
			}
			
			tutorialInventory.setItem(9, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+"More Info?", ItemManager.getId(Material.BOOK_AND_QUILL), 0, 
				ChatColor.RESET+"There is much more information you will require for your",
				ChatColor.RESET+"journey into CivCraft. Please visit the wiki at ",
				ChatColor.RESET+CivColor.LightGreen+ChatColor.BOLD+"http://civcraft.net/wiki",
				ChatColor.RESET+"For more detailed information about CivCraft and it's features."
			));
			
			tutorialInventory.setItem(13, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+"QUEST: Build a Camp", ItemManager.getId(Material.BOOK_AND_QUILL), 0, 
				ChatColor.RESET+"First things first, in order to start your journey",
				ChatColor.RESET+"you must first build a camp. Camps allow you to store",
				ChatColor.RESET+"your materials safely, and allow you to obtain leadership",
				ChatColor.RESET+"tokens which can be crafted into a civ. The recipe for a camp is below."
				));
			
			tutorialInventory.setItem(14, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+"QUEST: Found a Civ", ItemManager.getId(Material.BOOK_AND_QUILL), 0, 
				ChatColor.RESET+"Next, you'll want to start a civilization.",
				ChatColor.RESET+"To do this, you must first obtain leadership tokens",
				ChatColor.RESET+"by feeding bread to your camp's longhouse.",
				ChatColor.RESET+"Once you have enough leadership tokens.",
				ChatColor.RESET+"You can craft the founding flag item below."
			));
			
			tutorialInventory.setItem(17, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+"Need to know a recipe?", ItemManager.getId(Material.WORKBENCH), 0, 
				ChatColor.RESET+"Type /res book to obtain the tutorial book",
				ChatColor.RESET+"and then click on 'Crafting Recipies'",
				ChatColor.RESET+"Every new item in CivCraft is listed here",
				ChatColor.RESET+"along with how to craft them.",
				ChatColor.RESET+"Good luck!"
			));
			LoreGuiItemListener.guiInventories.put(tutorialInventory.getName(), tutorialInventory);
		}
		
		if (player != null && player.isOnline() && player.isValid()) {
			player.openInventory(tutorialInventory);	
		}
	}
	
	public static ItemStack getInfoBookForItem(String matID) {
		LoreCraftableMaterial loreMat = LoreCraftableMaterial.getCraftMaterialFromId(matID);
		ItemStack stack = LoreMaterial.spawn(loreMat);			
		if (!loreMat.isCraftable()) {
			return null;
		}
		
		AttributeUtil attrs = new AttributeUtil(stack);
		attrs.removeAll(); /* Remove all attribute modifiers to prevent them from displaying */
		LinkedList<String> lore = new LinkedList<String>();
		lore.add(""+ChatColor.RESET+ChatColor.BOLD+ChatColor.GOLD+"Click For Recipe");
		attrs.setLore(lore);				
		stack = attrs.getStack();
		return stack;
	}
	
	public static void showCraftingHelp(Player player) {
		if (craftingHelpInventory == null) {
			craftingHelpInventory = Bukkit.getServer().createInventory(player, MAX_CHEST_SIZE*9, "CivCraft Custom Item Recipes");
			
			/* Build the Category Inventory. */
			for (ConfigMaterialCategory cat : ConfigMaterialCategory.getCategories()) {
				if (cat.craftableCount == 0) {
					continue;
				}
				
				int identifier;
				if (cat.name.contains("Element")) {
					identifier = ItemManager.getId(Material.EXP_BOTTLE);
				} else if (cat.name.contains("Gear Tier 0")) {
					identifier = ItemManager.getId(Material.STONE_SWORD);
				} else if (cat.name.contains("Gear Tier 1")) {
					identifier = ItemManager.getId(Material.IRON_HELMET);
				} else if (cat.name.contains("Gear Tier 2")) {
					identifier = ItemManager.getId(Material.GOLD_CHESTPLATE);
				} else if (cat.name.contains("Gear Tier 3")) {
					identifier = ItemManager.getId(Material.CHAINMAIL_LEGGINGS);
				} else if (cat.name.contains("Gear Tier 4")) {
					identifier = ItemManager.getId(Material.DIAMOND_BOOTS);
				} else if (cat.name.contains("Special")) {
					identifier = ItemManager.getId(Material.BEACON);
				} else if (cat.name.contains("Tier 1 Material")) {
					identifier = ItemManager.getId(Material.IRON_BLOCK);
				} else if (cat.name.contains("Tier 2 Material")) {
					identifier = ItemManager.getId(Material.GOLD_BLOCK);
				} else if (cat.name.contains("Tier 3 Material")) {
					identifier = ItemManager.getId(Material.DIAMOND_BLOCK);
				} else if (cat.name.contains("Tier 4 Material")) {
					identifier = ItemManager.getId(Material.EMERALD_BLOCK);
				} else if (cat.name.contains("Upgrader")) {
					identifier = ItemManager.getId(Material.NETHER_STAR);
				} else {
					identifier = ItemManager.getId(Material.WRITTEN_BOOK);
				}
				
				ItemStack infoRec = LoreGuiItem.build(cat.name, identifier, 0,
					CivColor.LightBlue+cat.materials.size()+" Items",
					CivColor.Gold+"<Click To Open>");
					infoRec = LoreGuiItem.setAction(infoRec, "OpenInventory");
					infoRec = LoreGuiItem.setActionData(infoRec, "invType", "showGuiInv");
					infoRec = LoreGuiItem.setActionData(infoRec, "invName", cat.name+" Recipes");
					craftingHelpInventory.addItem(infoRec);
					
				Inventory inv = Bukkit.createInventory(player, LoreGuiItem.MAX_INV_SIZE, cat.name+" Recipes");
				for (ConfigMaterial mat : cat.materials.values()) {
					ItemStack stack = getInfoBookForItem(mat.id);
					if (stack != null) {
						stack = LoreGuiItem.setAction(stack, "ShowRecipe");
						inv.addItem(LoreGuiItem.asGuiItem(stack));
					}
				}
				
				/* Add back buttons. */
				ItemStack backButton = LoreGuiItem.build("Back", ItemManager.getId(Material.MAP), 0, "Back to Categories");
				backButton = LoreGuiItem.setAction(backButton, "OpenInventory");
				backButton = LoreGuiItem.setActionData(backButton, "invType", "showCraftingHelp");
				inv.setItem(LoreGuiItem.MAX_INV_SIZE-1, backButton);
				LoreGuiItemListener.guiInventories.put(inv.getName(), inv);
			}
			LoreGuiItemListener.guiInventories.put(craftingHelpInventory.getName(), craftingHelpInventory);
		}
		player.openInventory(craftingHelpInventory);
	}
	
	public static void spawnGuiBook(Player player) {
		if (guiInventory == null) {
			guiInventory = Bukkit.getServer().createInventory(player, 1*9, "CivCraft Information");

			ItemStack infoRec = LoreGuiItem.build("CivCraft Information", ItemManager.getId(Material.FEATHER), 
					0, CivColor.Gold+"<Click To View>");
			infoRec = LoreGuiItem.setAction(infoRec, "OpenInventory");
			infoRec = LoreGuiItem.setActionData(infoRec, "invType", "showTutorialInventory");
			guiInventory.addItem(infoRec);
			
			ItemStack serverLinks = LoreGuiItem.build("Server Links", ItemManager.getId(Material.STAINED_CLAY), 
					9, CivColor.Gold+"<Click To View>");
			serverLinks = LoreGuiItem.setAction(serverLinks, "OpenInventory");
			serverLinks = LoreGuiItem.setActionData(serverLinks, "invType", "showServerInfoInventory");
			guiInventory.addItem(serverLinks);
			
			//XXX Filler
			ItemStack filler = LoreGuiItem.build("", ItemManager.getId(Material.WOOD_BUTTON), 
					0, CivColor.Gold+"");
			guiInventory.addItem(filler);
			LoreGuiItemListener.guiInventories.put(guiInventory.getName(), guiInventory);
			
			ItemStack craftRec = LoreGuiItem.build("Crafting Recipes", ItemManager.getId(Material.WORKBENCH), 
					0, CivColor.Gold+"<Click To View>");
			craftRec = LoreGuiItem.setAction(craftRec, "OpenInventory");
			craftRec = LoreGuiItem.setActionData(craftRec, "invType", "showCraftingHelp");
			guiInventory.addItem(craftRec);
			LoreGuiItemListener.guiInventories.put(guiInventory.getName(), guiInventory);
			
			//XXX Filler
			ItemStack filler1 = LoreGuiItem.build("", ItemManager.getId(Material.WOOD_BUTTON),
					0, CivColor.Gold+" ");
			guiInventory.addItem(filler1);
			LoreGuiItemListener.guiInventories.put(guiInventory.getName(), guiInventory);
			//XXX Filler
			ItemStack filler2 = LoreGuiItem.build("", ItemManager.getId(Material.WOOD_BUTTON), 
					0, CivColor.Gold+"  ");
			guiInventory.addItem(filler2);
			LoreGuiItemListener.guiInventories.put(guiInventory.getName(), guiInventory);
			//XXX Filler
			ItemStack filler3 = LoreGuiItem.build("", ItemManager.getId(Material.WOOD_BUTTON),
					0, CivColor.Gold+"   ");
			guiInventory.addItem(filler3);
			LoreGuiItemListener.guiInventories.put(guiInventory.getName(), guiInventory);
			//XXX Filler
			ItemStack filler4 = LoreGuiItem.build("", ItemManager.getId(Material.WOOD_BUTTON),
					0, CivColor.Gold+"    ");
			guiInventory.addItem(filler4);
			LoreGuiItemListener.guiInventories.put(guiInventory.getName(), guiInventory);
			
			ItemStack dynmapRec = LoreGuiItem.build("Collecting Elements", ItemManager.getId(Material.EXP_BOTTLE), 
					0, CivColor.Gold+"<Click To View>");
			dynmapRec = LoreGuiItem.setAction(dynmapRec, "OpenInventory");
			dynmapRec = LoreGuiItem.setActionData(dynmapRec, "invType", "showElementInfo");
			guiInventory.addItem(dynmapRec);
			LoreGuiItemListener.guiInventories.put(guiInventory.getName(), guiInventory);
		}
		player.openInventory(guiInventory);
	}
}