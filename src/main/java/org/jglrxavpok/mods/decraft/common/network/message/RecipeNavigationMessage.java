package org.jglrxavpok.mods.decraft.common.network.message;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jglrxavpok.mods.decraft.inventory.ContainerUncraftingTable;

public class RecipeNavigationMessage implements IMessage
{

	private int recipeIndex;

	public RecipeNavigationMessage()
	{
	}

	public RecipeNavigationMessage(int recipeIndex)
	{
		this.recipeIndex = recipeIndex;
	}

	@Override
	public void decode(PacketBuffer buf)
	{
		this.recipeIndex = buf.readVarInt();
	}

	@Override
	public void encode(PacketBuffer buf)
	{
		buf.writeVarInt(this.recipeIndex);
	}


	public static final class MessageHandler implements IMessageHandler<RecipeNavigationMessage, IMessage>
	{

		@Override
		public IMessage onMessage(RecipeNavigationMessage message, NetworkEvent.Context ctx)
		{
			final ServerPlayerEntity player = ctx.getSender();

			ServerWorld serverWorld = (ServerWorld)player.world;
			serverWorld.getServer().enqueue(new TickDelayedTask(0, () -> {
				Container container = player.openContainer;
				if (container instanceof ContainerUncraftingTable)
				{
					ContainerUncraftingTable uncraftingContainer = (ContainerUncraftingTable)container;
					uncraftingContainer.uncraftingResult.selectedCraftingGrid = message.recipeIndex;
					uncraftingContainer.switchRecipe();
				}
			}));
			return null;
		}
	}

}

