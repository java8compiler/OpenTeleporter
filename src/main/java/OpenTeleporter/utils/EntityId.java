package OpenTeleporter.utils;

import OpenTeleporter.Config;
import net.minecraft.entity.Entity;

import java.util.List;

/**
 * Created by NEO on 21.01.2016.
 */
public class EntityId {
    public List<Entity> entity;
    public int x, y, z, time;
    public boolean live;

    public EntityId(List<Entity> entity, int x, int y, int z) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
        live = true;
    }

    public void update(){
        time++;
        if(Config.timeLimit <= time){
            live = false;
        }
    }
}
