package org.jglrxavpok.mods.decraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
            System.out.println("Received request to uncraft: "+message.toUncraft);
            return null;
        }
    }
}
