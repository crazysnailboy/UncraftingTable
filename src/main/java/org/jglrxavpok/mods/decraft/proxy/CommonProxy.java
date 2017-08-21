package org.jglrxavpok.mods.decraft.proxy;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.common.config.ModJsonConfiguration;
import org.jglrxavpok.mods.decraft.common.network.ModGuiHandler;
import org.jglrxavpok.mods.decraft.common.network.message.ConfigSyncMessage;
import org.jglrxavpok.mods.decraft.common.network.message.RecipeNavigationMessage;
import org.jglrxavpok.mods.decraft.init.ModItems;
import org.jglrxavpok.mods.decraft.integration.ModIntegrations;
import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingManager;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers;
import org.jglrxavpok.mods.decraft.stats.ModAchievementList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.ShapedOreRecipe;


public class CommonProxy
{

	public void preInit()
	{
		this.initializeConfig();
		this.registerNetworkMessages();
		this.registerIntegrations();
	}

	public void init()
	{
		this.registerAchievements();
		this.registerGuiHandler();
		this.registerOreDictionaryEntries();
		this.registerUncraftingRecipes();
	}

	public void postInit()
	{
		this.initializeRecipeHandlers();
	}


	private void initializeConfig()
	{
		ModConfiguration.initializeConfiguration();
		ModJsonConfiguration.loadItemMappings();
	}

	private void initializeRecipeHandlers()
	{
		RecipeHandlers.postInit();
	}


	private void registerAchievements()
	{
		ModAchievementList.registerAchievementPage();
	}

	private void registerGuiHandler()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(ModUncrafting.instance, new ModGuiHandler());
	}

	private void registerIntegrations()
	{
		ModIntegrations.registerIntegrations();
	}

	private void registerOreDictionaryEntries()
	{
		ModItems.registerOreDictionaryEntries();
	}

	private void registerNetworkMessages()
	{
		ModUncrafting.NETWORK.registerMessage(RecipeNavigationMessage.MessageHandler.class, RecipeNavigationMessage.class, 0, Side.SERVER);
		ModUncrafting.NETWORK.registerMessage(ConfigSyncMessage.MessageHandler.class, ConfigSyncMessage.class, 1, Side.CLIENT);
		ModUncrafting.NETWORK.registerMessage(ConfigSyncMessage.MessageHandler.class, ConfigSyncMessage.class, 2, Side.SERVER);
	}

	private void registerUncraftingRecipes()
	{
		// damaged anvil recipes
		UncraftingManager.addUncraftingRecipe(new ShapedOreRecipe(new ResourceLocation(ModUncrafting.MODID, "damaged_anvil_1"), new ItemStack(Blocks.ANVIL, 1, 1), new Object[] { "B B", " I ", "I I", 'B', "blockIron", 'I', "ingotIron" }));
		UncraftingManager.addUncraftingRecipe(new ShapedOreRecipe(new ResourceLocation(ModUncrafting.MODID, "damaged_anvil_2"), new ItemStack(Blocks.ANVIL, 1, 2), new Object[] { "B  ", " I ", "  I", 'B', "blockIron", 'I', "ingotIron" }));

		if (!Loader.isModLoaded("craftablehorsearmour"))
		{
			// horse armor recipes
			UncraftingManager.addUncraftingRecipe(new ShapedOreRecipe(new ResourceLocation(ModUncrafting.MODID, "iron_horse_armor"), Items.IRON_HORSE_ARMOR, new Object[] { "  I", "IWI", "III", 'I', "ingotIron", 'W', Item.getItemFromBlock(Blocks.WOOL) }));
			UncraftingManager.addUncraftingRecipe(new ShapedOreRecipe(new ResourceLocation(ModUncrafting.MODID, "golden_horse_armor"), Items.GOLDEN_HORSE_ARMOR, new Object[] { "  G", "GWG", "GGG", 'G', "ingotGold", 'W', new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 4) }));
			UncraftingManager.addUncraftingRecipe(new ShapedOreRecipe(new ResourceLocation(ModUncrafting.MODID, "diamond_horse_armor"), Items.DIAMOND_HORSE_ARMOR, new Object[] { "  D", "DWD", "DDD", 'D', "gemDiamond", 'W', new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 3) }));
			// saddle recipe
			UncraftingManager.addUncraftingRecipe(new ShapedOreRecipe(new ResourceLocation(ModUncrafting.MODID, "saddle"), Items.SADDLE, new Object[] { "LLL", "S S", "I I", 'L', "leather", 'S', Items.STRING, 'I', "ingotIron" }));
		}
	}

}