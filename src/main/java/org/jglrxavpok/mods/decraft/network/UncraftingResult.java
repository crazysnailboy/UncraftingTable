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
    private int requiredExpLevels;
    private int required;
    private int consumedBooks;
    private NonNullList<ItemStack> output;

    public UncraftingResult() {
        output = NonNullList.func_191196_a();
    }

    public UncraftingResult(ResultType resultType) {
        this(resultType, 0);
    }

    public UncraftingResult(ResultType resultType, int required) {
        this(resultType, required, 0, 0, NonNullList.<ItemStack>func_191196_a());
    }

    public UncraftingResult(ResultType resultType, int required, int requiredExpLevels) {
        this(resultType, required, requiredExpLevels, 0, NonNullList.<ItemStack>func_191196_a());
    }

    public UncraftingResult(ResultType resultType, int required, int requiredExpLevels, int consumedBooks, NonNullList<ItemStack> output) {
        this.resultType = resultType;
        this.required = required;
        this.requiredExpLevels = requiredExpLevels;
        this.consumedBooks = consumedBooks;
        this.output = output;
    }

    public int getConsumedBooks() {
        return consumedBooks;
    }

    public int getRequiredExpLevels() {
        return requiredExpLevels;
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
        requiredExpLevels = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(resultType.ordinal());
        buf.writeInt(output.size());
        for (ItemStack stack : output) {
            ByteBufUtils.writeItemStack(buf, stack);
        }
        buf.writeInt(required);
        buf.writeInt(requiredExpLevels);
    }

    public enum ResultType {
        INVALID, NOT_ENOUGH_ITEMS, NOT_ENOUGH_EXPERIENCE, VALID
    }

    public static class Handler implements IMessageHandler<UncraftingResult, IMessage> {
        public Handler() {}

        @Override
        public IMessage onMessage(UncraftingResult message, MessageContext ctx) {
            return null;
        }
    }
}
