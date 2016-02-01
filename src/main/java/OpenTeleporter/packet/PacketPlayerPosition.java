package OpenTeleporter.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;

/**
 * Created by NEO on 19.01.2016.
 */
public class PacketPlayerPosition implements IMessage {
    public double x, y, z;

    public PacketPlayerPosition() {
    }

    public PacketPlayerPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    public static class Handler implements IMessageHandler<PacketPlayerPosition, IMessage>{

        @Override
        public IMessage onMessage(PacketPlayerPosition message, MessageContext ctx) {
            if(ctx.side == Side.CLIENT){
                EntityClientPlayerMP thePlayer = Minecraft.getMinecraft().thePlayer;
                thePlayer.setPosition(message.x, message.y, message.z);
            }
            return null;
        }
    }
}
