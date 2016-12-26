package org.jglrxavpok.mods.decraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jglrxavpok.mods.decraft.ContainerUncraftingTable;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.UncraftingManager;
import org.jglrxavpok.mods.decraft.event.ItemUncraftedEvent;

public class UncraftingRequest implements IMessage {

    private ItemStack toUncraft;
    private ItemStack book;

    public UncraftingRequest() {}

    public UncraftingRequest(ItemStack toUncraft, ItemStack book) {
        this.toUncraft = toUncraft;
        this.book = book;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        toUncraft = ByteBufUtils.readItemStack(buf);
        book = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, toUncraft);
        ByteBufUtils.writeItemStack(buf, book);
    }

    public static final class Handler implements IMessageHandler<UncraftingRequest, UncraftingResult> {

        public Handler() {}

        @Override
        public UncraftingResult onMessage(UncraftingRequest message, MessageContext ctx) {
            ItemStack toUncraft = message.toUncraft;
            ItemStack book = message.book;
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            UncraftingResult result = UncraftingManager.uncraft(toUncraft, book, player);
            if(result.getResultType() == UncraftingResult.ResultType.VALID) {
                Container container = player.openContainer;
                if (container instanceof ContainerUncraftingTable) {
                    ContainerUncraftingTable table = ((ContainerUncraftingTable) container);
                    return handleResult(table, player, result);
                } else {
                    ModUncrafting.instance.getLogger().error("Received UncraftingResult packet while *not* using an uncrafting table");
                }
            }
            return result;
        }

        private UncraftingResult handleResult(ContainerUncraftingTable table, EntityPlayer player, UncraftingResult result) {
            ItemStack stack = table.getUncraftSlot().getStack();

            // check one last time if everyone agrees to uncraft this item
            ItemUncraftedEvent uncraftEvent = new ItemUncraftedEvent(player, stack, result.getOutput(), result.getRequired());
            if (MinecraftForge.EVENT_BUS.post(uncraftEvent)) {
                return UncraftingManager.INVALID_ITEM;
            }
            player.addStat(ModUncrafting.instance.uncraftedItemsStat, result.getRequired());

            int remaining = stack.func_190916_E() - result.getRequired();
            if(remaining > 0) {
                stack.func_190920_e(remaining);
            } else {
                stack = ItemStack.field_190927_a;
            }
            table.getUncraftSlot().putStack(stack);
            table.setOutput(result.getOutput());

            if(!player.capabilities.isCreativeMode)
                player.removeExperienceLevel(result.getRequiredExpLevels());
            return result;
        }
    }
}
