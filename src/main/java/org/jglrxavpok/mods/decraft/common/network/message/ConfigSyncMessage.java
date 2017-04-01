package org.jglrxavpok.mods.decraft.common.network.message;

import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ConfigSyncMessage implements IMessage
{

	private int standardLevel = ModConfiguration.standardLevel;
	private int maxUsedLevel = ModConfiguration.maxUsedLevel;
	private int enchantmentCost = ModConfiguration.enchantmentCost;
	private int uncraftMethod = ModConfiguration.uncraftMethod;
	private String[] excludedItems = ModConfiguration.excludedItems;
	private boolean useNuggets = ModConfiguration.useNuggets;
	private boolean registerNuggets = ModConfiguration.registerNuggets;
	private boolean useRabbitHide = ModConfiguration.useRabbitHide;
	private boolean ensureReturn = ModConfiguration.ensureReturn;


	public ConfigSyncMessage()
	{
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		standardLevel = ByteBufUtils.readVarShort(buf);
		maxUsedLevel = ByteBufUtils.readVarShort(buf);
		enchantmentCost = ByteBufUtils.readVarShort(buf);
		uncraftMethod = ByteBufUtils.readVarShort(buf);
		excludedItems = ByteBufUtils.readUTF8String(buf).split("\\|");
		useNuggets = (ByteBufUtils.readVarShort(buf) == 1);
		registerNuggets = (ByteBufUtils.readVarShort(buf) == 1);
		useRabbitHide = (ByteBufUtils.readVarShort(buf) == 1);
		ensureReturn = (ByteBufUtils.readVarShort(buf) == 1);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarShort(buf, standardLevel);
		ByteBufUtils.writeVarShort(buf, maxUsedLevel);
		ByteBufUtils.writeVarShort(buf, enchantmentCost);
		ByteBufUtils.writeVarShort(buf, uncraftMethod);
		ByteBufUtils.writeUTF8String(buf, String.join("|", excludedItems));
		ByteBufUtils.writeVarShort(buf, (useNuggets ? 1 : 0));
		ByteBufUtils.writeVarShort(buf, (registerNuggets ? 1 : 0));
		ByteBufUtils.writeVarShort(buf, (useRabbitHide ? 1 : 0));
		ByteBufUtils.writeVarShort(buf, (ensureReturn ? 1 : 0));
	}


	public static final class MessageHandler implements IMessageHandler<ConfigSyncMessage, IMessage>
	{
		@Override
		public IMessage onMessage(final ConfigSyncMessage message, MessageContext ctx)
		{
			Minecraft minecraft = Minecraft.getMinecraft();
			minecraft.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					ModConfiguration.maxUsedLevel = message.maxUsedLevel;
					ModConfiguration.standardLevel = message.standardLevel;
					ModConfiguration.enchantmentCost = message.enchantmentCost;
					ModConfiguration.uncraftMethod = message.uncraftMethod;
					ModConfiguration.excludedItems = message.excludedItems;
					ModConfiguration.useNuggets = message.useNuggets;
					ModConfiguration.registerNuggets = message.registerNuggets;
					ModConfiguration.useRabbitHide = message.useRabbitHide;
					ModConfiguration.ensureReturn = message.ensureReturn;
				}
			});

			return null;
		}
	}

}
