package org.jglrxavpok.mods.decraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UncraftingResult implements IMessage {

    private ResultType resultType;
    private int required;
    private NonNullList<ItemStack> output;

    public UncraftingResult() {
        output = NonNullList.func_191196_a();
    }

    public UncraftingResult(ResultType resultType) {
        this(resultType, 0);
    }

    public UncraftingResult(ResultType resultType, int required) {
        this(resultType, required, NonNullList.<ItemStack>func_191196_a());
    }

    public UncraftingResult(ResultType resultType, int required, NonNullList<ItemStack> output) {
        this.resultType = resultType;
        this.required = required;
        this.output = output;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public int getRequired() {
        return required;
    }

    public NonNullList<ItemStack> getOutput() {
        return output;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        output.clear();
        resultType = ResultType.values()[buf.readInt()];
        int outputSize = buf.readInt();
        for (int i = 0; i < outputSize; i++) {
            ItemStack stack = ByteBufUtils.readItemStack(buf);
            output.add(stack);
        }
        required = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(resultType.ordinal());
        buf.writeInt(output.size());
        for (ItemStack stack : output) {
            ByteBufUtils.writeItemStack(buf, stack);
        }
        buf.writeInt(required);
    }

    public enum ResultType {
        INVALID, NOT_ENOUGH_ITEMS, VALID
    }

    public static class Handler implements IMessageHandler<UncraftingResult, IMessage> {
        public Handler() {}

        @Override
        public IMessage onMessage(UncraftingResult message, MessageContext ctx) {
            return null;
        }
    }
}
