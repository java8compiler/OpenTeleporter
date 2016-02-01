package OpenTeleporter;

import OpenTeleporter.proxy.CommonProxy;
import OpenTeleporter.tileentities.TileEntityTeleporter;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid=OpenTeleporter.MODID, name=OpenTeleporter.MODID, version=OpenTeleporter.VERSION)
public class OpenTeleporter {
	public final static String MODID = "OpenTeleporter";
	public final static String VERSION = "0.0.2a";

	public static Logger logger;

	@SidedProxy(clientSide="OpenTeleporter.proxy.ClientProxy", serverSide="OpenTeleporter.proxy.ServerProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		logger = e.getModLog();
		Config.init(e.getSuggestedConfigurationFile());
	    proxy.preInit(e);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
	    proxy.init(e);
		TileEntityTeleporter.timer.schedule(TileEntityTeleporter.task, 1000, 1000);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
	    proxy.postInit(e);
	}
}
