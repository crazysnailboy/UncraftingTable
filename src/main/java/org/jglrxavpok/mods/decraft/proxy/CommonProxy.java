package org.jglrxavpok.mods.decraft.proxy;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.common.config.ModJsonConfiguration;
import org.jglrxavpok.mods.decraft.common.network.ModGuiHandler;
import org.jglrxavpok.mods.decraft.common.network.message.ConfigSyncMessage;
import org.jglrxavpok.mods.decraft.common.network.message.RecipeNavigationMessage;
import org.jglrxavpok.mods.decraft.init.ModBlocks;
import org.jglrxavpok.mods.decraft.init.ModItems;
import org.jglrxavpok.mods.decraft.integration.ModIntegrations;
import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingRegistry;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers;
import org.jglrxavpok.mods.decraft.stats.ModAchievementList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.ShapedOreRecipe;


public class CommonProxy
{

	public void preInit()
	{
		this.initializeConfig();
		this.registerBlocksAndItems();
		this.registerNetworkMessages();
		this.registerIntegrations();
	}

	public void init()
	{
		this.registerOreDictionaryEntries();
		this.registerCraftingRecipes();
		this.registerUncraftingRecipes();
		this.registerGuiHandler();
		this.registerAchievements();
	}

	public void postInit()
	{
		this.initializeRecipeHandlers();
	}


	private void initializeConfig()
	{
		ModConfiguration.initializeConfiguration();
		ModJsonConfiguration.initializeItemMapppings();
	}

	private void initializeRecipeHandlers()
	{
		RecipeHandlers.postInit();
	}


	private void registerAchievements()
	{
		ModAchievementList.registerAchievementPage();
	}

	private void registerBlocksAndItems()
	{
		ModBlocks.registerBlocks();
		ModItems.registerItems();
	}

	private void registerCraftingRecipes()
	{
		ModBlocks.registerCraftingRecipes();
		ModItems.registerCraftingRecipes();
	}

	private void registerGuiHandler()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(ModUncrafting.instance, new ModGuiHandler());
	}

	private void registerIntegrations()
	{
		ModIntegrations.registerIntegrations();
	}

	private void registerNetworkMessages()
	{
		ModUncrafting.instance.getNetwork().registerMessage(RecipeNavigationMessage.MessageHandler.class, RecipeNavigationMessage.class, 0, Side.SERVER);
		ModUncrafting.instance.getNetwork().registerMessage(ConfigSyncMessage.MessageHandler.class, ConfigSyncMessage.class, 1, Side.CLIENT);
		ModUncrafting.instance.getNetwork().registerMessage(ConfigSyncMessage.MessageHandler.class, ConfigSyncMessage.class, 2, Side.SERVER);
	}

	private void registerOreDictionaryEntries()
	{
		ModItems.registerOreDictionaryEntries();
	}

	private void registerUncraftingRecipes()
	{
		// damaged anvil recipes
		UncraftingRegistry.getInstance().addUncraftingRecipe(new ShapedOreRecipe(new ItemStack(Blocks.ANVIL, 1, 1), new Object[] { "B B", " I ", "I I", 'B', "blockIron", 'I', "ingotIron" }));
		UncraftingRegistry.getInstance().addUncraftingRecipe(new ShapedOreRecipe(new ItemStack(Blocks.ANVIL, 1, 2), new Object[] { "B  ", " I ", "  I", 'B', "blockIron", 'I', "ingotIron" }));

		if (!Loader.isModLoaded("craftablehorsearmour"))
		{
			// horse armor recipes
			UncraftingRegistry.getInstance().addUncraftingRecipe(new ShapedOreRecipe(Items.IRON_HORSE_ARMOR, new Object[] { "  I", "IWI", "III", 'I', "ingotIron", 'W', Item.getItemFromBlock(Blocks.WOOL) }));
			UncraftingRegistry.getInstance().addUncraftingRecipe(new ShapedOreRecipe(Items.GOLDEN_HORSE_ARMOR, new Object[] { "  G", "GWG", "GGG", 'G', "ingotGold", 'W', new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 4) }));
			UncraftingRegistry.getInstance().addUncraftingRecipe(new ShapedOreRecipe(Items.DIAMOND_HORSE_ARMOR, new Object[] { "  D", "DWD", "DDD", 'D', "gemDiamond", 'W', new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 3) }));
			// saddle recipe
			UncraftingRegistry.getInstance().addUncraftingRecipe(new ShapedOreRecipe(Items.SADDLE, new Object[] { "LLL", "S S", "I I", 'L', "leather", 'S', Items.STRING, 'I', "ingotIron" }));

		}
	}

}
