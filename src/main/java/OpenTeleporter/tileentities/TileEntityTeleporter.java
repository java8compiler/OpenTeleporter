package OpenTeleporter.tileentities;

import OpenTeleporter.EntityId;
import OpenTeleporter.OpenTeleporter;
import OpenTeleporter.packet.PacketPlayerPosition;
import OpenTeleporter.packet.PacketTeleporter;
import OpenTeleporter.proxy.CommonProxy;
import li.cil.oc.api.API;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.*;
import li.cil.oc.api.prefab.TileEntityEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

public class TileEntityTeleporter extends TileEntityEnvironment implements SimpleComponent, Analyzable, SidedEnvironment{
	public static HashMap<String, EntityId> uuids = new HashMap<String, EntityId>();
	public static Timer timer = new Timer();
	public static TimerTask task = new TimerTask() {
		@Override
		public void run() {
			System.out.println(uuids.size());
			for(Map.Entry<String, EntityId> entries : uuids.entrySet()){
				if(!entries.getValue().live)
					uuids.remove(entries.getKey());
				entries.getValue().update();
			}
		}
	};


	public TileEntityTeleporter() {
		node = li.cil.oc.api.Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(1000000).create();
	}

	@Override
	public Node[] onAnalyze(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return new Node[]{node};
	}
	@Override
	public String getComponentName() {
		return "open_teleporter";
	}

	@Callback
	public Object[] teleport(Context context, Arguments arguments) throws Exception{
		if(!arguments.checkString(0).isEmpty()){
			String address = arguments.checkString(0);
			Iterable<Node> nodes = node.reachableNodes();
			boolean teleport = false;
			for(Node n : nodes){
				World world = worldObj;
				if(address.equals(n.address())){
					if(API.isPowerEnabled){
						double energy = Math.pow(distance(node, n), 3);
							Connector connector = (Connector) node;
							if(connector.globalBuffer() >= energy){
								double out = connector.changeBuffer(-energy);
								if(out > 0){
									Object[] a = new Object[1];
									a[0] = "We need more energy.";
									return a;
								}
							}else{
								Object[] a = new Object[1];
								a[0] = "We need more energy.";
								return a;
							}
						}
						teleport = true;
						TileEntityTeleporter teleporter = (TileEntityTeleporter) n.host();
						if(world.isAirBlock(teleporter.xCoord, teleporter.yCoord+1, teleporter.zCoord) && world.isAirBlock(teleporter.xCoord, teleporter.yCoord+2, teleporter.zCoord)){
							List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+2, zCoord+1));
							for(int i = 0; i < entities.size(); i++){
								double dx, dy, dz;
								Entity entity = entities.get(i);
								dx = entity.posX - xCoord;
								dy = entity.posY - yCoord;
								dz = entity.posZ - zCoord;
								if(entity instanceof EntityPlayerMP){
									EntityPlayerMP playerMP = (EntityPlayerMP) entity;
									double px, py, pz;
									px = teleporter.xCoord + dx;
									py = teleporter.yCoord + dy;
									pz = teleporter.zCoord + dz;
									playerMP.setPosition(px, py, pz);
									CommonProxy.wrapper.sendTo(new PacketPlayerPosition(px, py, pz), playerMP);
									if(OpenTeleporter.logging){
										OpenTeleporter.logger.info(playerMP.getDisplayName()+"| teleportated of |"+xCoord+" "+yCoord+" "+zCoord+"| at |"+teleporter.zCoord+" "+teleporter.zCoord+" "+teleporter.zCoord);
									}
								}else{
									entity.setPosition(teleporter.xCoord + dx, teleporter.yCoord + dy, teleporter.zCoord + dz);
								}
							}
						}
						CommonProxy.wrapper.sendToAll(new PacketTeleporter(xCoord, yCoord, zCoord));
					CommonProxy.wrapper.sendToAll(new PacketTeleporter(teleporter.xCoord, teleporter.yCoord, teleporter.zCoord));
					}
					if(teleport){
						Object[] a = new Object[1];
						a[0] = "teleportation successful";
					}else{
						Object[] a = new Object[1];
						a[0] = "teleportation failed, teleporter not found.";
					}
				}
			}
		return null;
	}

	@Callback
	public Object[] teleportById(Context context, Arguments arguments){
		if(!arguments.checkString(0).isEmpty()){
			Object[] a = new Object[1];
			String uuid = arguments.checkString(0);
			if(uuids.containsKey(uuid)){
				EntityId entityId = uuids.get(uuid);
				uuids.remove(uuid);
				TileEntity tileEntity = worldObj.getTileEntity(entityId.x, entityId.y, entityId.z);
				if(tileEntity instanceof TileEntityTeleporter && verifyEntities(entityId.entity, entityId.x, entityId.y, entityId.z)){
					if(API.isPowerEnabled){
						double energy = Math.pow(distance(entityId.x, entityId.y, entityId.z, xCoord, yCoord, zCoord), 3);
						Connector connector = (Connector) node;
						if(connector.globalBuffer() >= energy){
							double out = connector.changeBuffer(-energy);
							if(out > 0){
								Object[] aa = new Object[1];
								a[0] = "We need more energy.";
								return aa;
							}
						}else{
							Object[] aa = new Object[1];
							a[0] = "We need more energy.";
							return aa;
						}
					}
					List<Entity> entities = entityId.entity;
					if(entities.size() == 0 || entities.size() > OpenTeleporter.entityTeleportationLimit)
						return null;
					for(int i = 0; i < entities.size(); i++) {
						double dx, dy, dz;
						Entity entity = entities.get(i);
						dx = entity.posX - entityId.x;
						dy = entity.posY - entityId.y;
						dz = entity.posZ - entityId.z;
						if (entity instanceof EntityPlayerMP) {
							EntityPlayerMP playerMP = (EntityPlayerMP) entity;
							double px, py, pz;
							px = xCoord + dx;
							py = yCoord + dy;
							pz = zCoord + dz;
							playerMP.setPosition(px, py, pz);
							CommonProxy.wrapper.sendTo(new PacketPlayerPosition(px, py, pz), playerMP);
							if (OpenTeleporter.logging) {
								OpenTeleporter.logger.info(playerMP.getDisplayName() + "| teleportated of |" + xCoord + " " + yCoord + " " + zCoord + "| at |" + zCoord + " " + zCoord + " " + zCoord);
							}
						} else {
							entity.setPosition(xCoord + dx, yCoord + dy, zCoord + dz);
						}
					}
				}
			}else{
				a[0] = "uuid not found.";
			}
		}
		return null;
	}

	public boolean verifyEntities(List<Entity> entities, int x, int y, int z){
		World world = worldObj;
		List<Entity> entities1 = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+2, z+1));
		if(entities.size() == entities1.size()){
			for(int i = 0; i < entities.size(); i++){
				boolean tmp = false;
				for(int q = 0; q < entities1.size(); q++){
					if(entities.get(i) == entities1.get(q)){
						tmp = true;
						break;
					}
				}
				if(!tmp){
					return false;
				}
			}
			return true;
		}else{
			return false;
		}
	}

	@Callback
	public Object[] getEntitiesId(Context context, Arguments arguments){
		try{
			World world = worldObj;
			Object[] a = null;
			List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+2, zCoord+1));
			if(entities != null && entities.size() > 0){
				a = new Object[1];
				for(int z = 0; z < 30; z++){
					String uuid = UUID.randomUUID().toString();
					if(!uuids.containsKey(uuid)){
						EntityId entityId = new EntityId(entities, xCoord, yCoord, zCoord);
						uuids.put(uuid, entityId);
						a[0] = uuid;
						break;
					}
				}
			}else{
				a = new Object[1];
				a[0] = "Entities not found.";
			}
			return a;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Callback
	public Object[] getEnergyToTeleport(Context context, Arguments arguments) throws Exception{
		try{
			if(!arguments.checkString(0).isEmpty()){
				String address = arguments.checkString(0);
				Iterable<Node> nodes = node.reachableNodes();
				for(Node n : nodes){
					if(!n.address().isEmpty() && address.equals(n.address())){
						double energy = Math.pow(distance(node, n), OpenTeleporter.pow);
						Object[] a = new Object[1];
						a[0] = energy;
						return a;
					}
				}
				return new Object[]{"teleporter not found."};
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Callback
	public Object[] getDistance(Context context, Arguments arguments) throws Exception{
		try{
			if(!arguments.checkString(0).isEmpty()){
				String address = arguments.checkString(0);
				Iterable<Node> nodes = node.reachableNodes();
				for(Node n : nodes){
					if(!n.address().isEmpty() && address.equals(n.address())){
						double distance = distance(node, n);
						Object[] a = new Object[1];
						a[0] = distance;
						return a;
					}
				}
				return new Object[]{"teleporter not found."};
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public double distance(Node n1, Node n2){
		TileEntity te1 = (TileEntity) n1.host();
		TileEntity te2 = (TileEntity) n2.host();
		if(te1 != null && te2 != null){
			double dx = Math.pow(te2.xCoord-te1.xCoord, 2);
			double dy = Math.pow(te2.yCoord-te1.yCoord, 2);
			double dz = Math.pow(te2.zCoord-te1.zCoord, 2);
			double res = dx+dy+dz;
			if(res > 0){
				return Math.abs(Math.sqrt(res));
			}else{
				return 0;
			}
		}
		return 0;
	}

	public double distance(double x1,double y1,double z1, double x2,double y2,double z2){
		double dx = Math.pow(x2-x1, 2);
		double dy = Math.pow(y2-y1, 2);
		double dz = Math.pow(z2-z1, 2);
		double res = dx+dy+dz;
		return Math.abs(Math.sqrt(res));
	}

	@Override
	public Node sidedNode(ForgeDirection side) {
		if(side != ForgeDirection.UP){
			return node;
		}else{
			return null;
		}
	}

	@Override
	public boolean canConnect(ForgeDirection side) {
		if(side != ForgeDirection.UP){
			return true;
		}else{
			return false;
		}
	}
}
