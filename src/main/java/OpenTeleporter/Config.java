package OpenTeleporter;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by NEO on 01.02.2016.
 */
public class Config {
    public static Configuration configuration;

    public static int pow;
    public static boolean logging;
    public static int timeLimit;
    public static int entityTeleportationLimit;

    public static void init(File file){
        configuration = new Configuration(file);
        configuration.load();
        pow = configuration.get("MATH", "POW", 3, "Calculating the amount of energy : distance ^ POW.").getInt();
        logging = configuration.get("DEBUG", "loggingTeleportation", false, "If a player is teleported , it will be recorded if there is a true").getBoolean();
        timeLimit = configuration.get("GAME", "timeLimit", 120, "Validity UUID, which generates getEntitiesId.(seconds)").getInt();
        entityTeleportationLimit = configuration.get("GAME", "entityTeleportationLimit", 5, "Limit the number of things to teleport.").getInt();
        configuration.save();
    }
}
