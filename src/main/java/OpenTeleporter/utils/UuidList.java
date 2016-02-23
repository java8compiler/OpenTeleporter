package OpenTeleporter.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by NEO on 16.02.2016.
 */
public class UuidList {
    private static HashMap<String, EntityId> uuids = new HashMap<String, EntityId>();
    public static Timer timer = new Timer();
    public static TimerTask task = new TimerTask() {
        @Override
        public void run() {
            for(Map.Entry<String, EntityId> entries : uuids.entrySet()){
                if(!entries.getValue().live)
                    uuids.remove(entries.getKey());
                entries.getValue().update();
            }
        }
    };

    public static boolean isUuid(String uuid){
        return uuids.containsKey(uuid);
    }

    public static void addUuid(String uuid, EntityId entityId){
        uuids.put(uuid, entityId);
    }

    public static EntityId removeUuid(String uuid){
        if(isUuid(uuid)){
            return uuids.remove(uuid);
        }
        return null;
    }

    public static EntityId getEntityId(String uuid){
        if(isUuid(uuid)){
            return uuids.get(uuid);
        }
        return null;
    }
}
