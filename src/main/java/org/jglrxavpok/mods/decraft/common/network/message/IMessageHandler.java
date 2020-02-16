package org.jglrxavpok.mods.decraft.common.network.message;

import net.minecraftforge.fml.network.NetworkEvent;

public interface IMessageHandler<T extends IMessage, R extends IMessage> {

    R onMessage(final T message, NetworkEvent.Context ctx);
}
