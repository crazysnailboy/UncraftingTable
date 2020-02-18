package org.jglrxavpok.mods.decraft.common.network.message;

import net.minecraft.network.PacketBuffer;

/**
 * Messages used to communicate between server and clients
 */
public interface IMessage
{

    /**
     * Encodes this message to the given buffer
     * @param buffer
     */
    void encode(PacketBuffer buffer);

    /**
     * Decodes this message from the given buffer
     * @param buffer
     */
    void decode(PacketBuffer buffer);
}
