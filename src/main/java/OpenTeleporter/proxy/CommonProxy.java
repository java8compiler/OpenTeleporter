package OpenTeleporter.proxy;

import OpenTeleporter.blocks.BlockTeleporter;
import OpenTeleporter.packet.PacketPlayerPosition;
import OpenTeleporter.packet.PacketTeleporter;
import OpenTeleporter.tileentities.TileEntityTeleporter;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {
    public static SimpleNetworkWrapper wrapper;

	public void preInit(FMLPreInitializationEvent e) {
    }

    public void init(FMLInitializationEvent e) {
        wrapper = NetworkRegistry.INSTANCE.newSimpleChannel("OpenTeleporter");
        GameRegistry.registerBlock(new BlockTeleporter(), "Teleporter");
        GameRegistry.registerTileEntity(TileEntityTeleporter.class, "TileEntityTeleporter");

        wrapper.registerMessage(PacketPlayerPosition.Handler.class, PacketPlayerPosition.class, 0, Side.CLIENT);
        wrapper.registerMessage(PacketTeleporter.Handler.class, PacketTeleporter.class, 1, Side.CLIENT);
    }

    public void postInit(FMLPostInitializationEvent e) {
    }
}
