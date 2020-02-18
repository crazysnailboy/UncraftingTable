package org.jglrxavpok.mods.decraft.common.network.message;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;

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
	public void decode(PacketBuffer buf)
	{
		standardLevel = buf.readVarInt();
		maxUsedLevel = buf.readVarInt();
		enchantmentCost = buf.readVarInt();
		uncraftMethod = buf.readVarInt();
		excludedItems = buf.readString().split("\\|");
		useNuggets = (buf.readVarInt() == 1);
		registerNuggets = (buf.readVarInt() == 1);
		useRabbitHide = (buf.readVarInt() == 1);
		ensureReturn = (buf.readVarInt() == 1);
	}

	@Override
	public void encode(PacketBuffer buf)
	{
		buf.writeVarInt(standardLevel);
		buf.writeVarInt(maxUsedLevel);
		buf.writeVarInt(enchantmentCost);
		buf.writeVarInt(uncraftMethod);
		buf.writeString(String.join("|", excludedItems));
		buf.writeVarInt((useNuggets ? 1 : 0));
		buf.writeVarInt((registerNuggets ? 1 : 0));
		buf.writeVarInt((useRabbitHide ? 1 : 0));
		buf.writeVarInt((ensureReturn ? 1 : 0));
	}

	public static final class MessageHandler implements IMessageHandler<ConfigSyncMessage, IMessage>
	{

		@Override
		public IMessage onMessage(ConfigSyncMessage message, NetworkEvent.Context ctx)
		{
			ctx.enqueueWork(() -> {
				ModConfiguration.maxUsedLevel = message.maxUsedLevel;
				ModConfiguration.standardLevel = message.standardLevel;
				ModConfiguration.enchantmentCost = message.enchantmentCost;
				ModConfiguration.uncraftMethod = message.uncraftMethod;
				ModConfiguration.excludedItems = message.excludedItems;
				ModConfiguration.useNuggets = message.useNuggets;
				ModConfiguration.registerNuggets = message.registerNuggets;
				ModConfiguration.useRabbitHide = message.useRabbitHide;
				ModConfiguration.ensureReturn = message.ensureReturn;
			});
			return null;
		}
	}

}
