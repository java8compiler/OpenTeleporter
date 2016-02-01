package OpenTeleporter;

import OpenTeleporter.proxy.CommonProxy;
import OpenTeleporter.tileentities.TileEntityTeleporter;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Mod(modid=OpenTeleporter.MODID, name=OpenTeleporter.MODID, version=OpenTeleporter.VERSION)
public class OpenTeleporter {
	public final static String MODID = "OpenTeleporter";
	public final static String VERSION = "0.0.2a";
	public static Configuration configuration;
	public static int pow;
	public static boolean logging;
	public static int timeLimit;
	public static int timePeriod;
	public static int entityTeleportationLimit;

	public static Logger logger;

	@SidedProxy(clientSide="OpenTeleporter.proxy.ClientProxy", serverSide="OpenTeleporter.proxy.ServerProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		logger = e.getModLog();
		configuration = new Configuration(e.getSuggestedConfigurationFile());
		configuration.load();
		pow = configuration.get("MATH", "POW", 3).getInt();
		logging = configuration.get("DEBUG", "loggingTeleportation", false).getBoolean();
		timeLimit = configuration.get("GAME", "timeLimit", 120).getInt();
		timePeriod = configuration.get("GAME", "timePeriod", 1000).getInt();
		entityTeleportationLimit = configuration.get("GAME", "timePeriod", 5).getInt();
		configuration.save();
	    proxy.preInit(e);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
	    proxy.init(e);
		TileEntityTeleporter.timer.schedule(TileEntityTeleporter.task, timePeriod, timePeriod);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
	    proxy.postInit(e);
	}
}
