package OpenTeleporter.tileentities;

import OpenTeleporter.Config;
import OpenTeleporter.OpenTeleporter;
import OpenTeleporter.packet.PacketPlayerPosition;
import OpenTeleporter.packet.PacketTeleporter;
import OpenTeleporter.proxy.CommonProxy;
import OpenTeleporter.utils.EntityId;
import OpenTeleporter.utils.UuidList;
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

import java.util.List;
import java.util.UUID;

public class TileEntityTeleporter extends TileEntityEnvironment implements SimpleComponent, Analyzable, SidedEnvironment{


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

	@Callback(doc="eleports all the entities on the teleporter to another teleporter with the given name.")
	public Object[] teleport(Context context, Arguments arguments) throws Exception{
		if(!arguments.checkString(0).isEmpty()){
			String address = arguments.checkString(0);
			Iterable<Node> nodes = node.reachableNodes();
			boolean teleport = false;
			for(Node n : nodes){
				World world = worldObj;
				if(address.equals(n.address())){
					Connector connector = (Connector) node;
					double energy = Math.pow(distance(node, n), 3);

					if(!connector.tryChangeBuffer(-energy)){
						throw new Exception("not enough energy");
					}
					teleport = true;
					TileEntityTeleporter teleporter = (TileEntityTeleporter) n.host();
					if(world.isAirBlock(teleporter.xCoord, teleporter.yCoord+1, teleporter.zCoord) && world.isAirBlock(teleporter.xCoord, teleporter.yCoord+2, teleporter.zCoord)){
						List<Entity> entities = getEntitiesInBound(Entity.class, worldObj, xCoord, yCoord, zCoord, xCoord+1, yCoord+2, zCoord+1);
						if(entities.size() == 0  || entities.size() > Config.entityTeleportationLimit){
							throw new Exception("entity limit.");
						}
						for (Entity currentEntity : entities) {
							double dx, dy, dz;
							dx = currentEntity.posX - xCoord;
							dy = currentEntity.posY - yCoord;
							dz = currentEntity.posZ - zCoord;
							if (currentEntity instanceof EntityPlayerMP) {
								EntityPlayerMP playerMP = (EntityPlayerMP) currentEntity;
								double px, py, pz;
								px = teleporter.xCoord + dx;
								py = teleporter.yCoord + dy;
								pz = teleporter.zCoord + dz;
								playerMP.setPosition(px, py, pz);
								CommonProxy.wrapper.sendTo(new PacketPlayerPosition(px, py, pz), playerMP);
								if (Config.logging) {
									OpenTeleporter.logger.info(playerMP.getDisplayName() + "| teleportated of |" + xCoord + " " + yCoord + " " + zCoord + "| at |" + teleporter.zCoord + " " + teleporter.zCoord + " " + teleporter.zCoord);
								}
							} else {
								currentEntity.setPosition(teleporter.xCoord + dx, teleporter.yCoord + dy, teleporter.zCoord + dz);
							}
						}
					}
					CommonProxy.wrapper.sendToAll(new PacketTeleporter(xCoord, yCoord, zCoord));
					CommonProxy.wrapper.sendToAll(new PacketTeleporter(teleporter.xCoord, teleporter.yCoord, teleporter.zCoord));
					}
					if(teleport){
						return new Object[]{true};
					}else{
						throw new Exception("teleportation failed, teleporter not found.");
					}
				}
			}
		return null;
	}

	@Callback(doc="teleports entities with the given UUID.")
	public Object[] teleportById(Context context, Arguments arguments) throws Exception{
		if(!arguments.checkString(0).isEmpty()){
			Object[] a = new Object[1];
			String uuid = arguments.checkString(0);
			if(UuidList.isUuid(uuid)){
				EntityId entityId = UuidList.removeUuid(uuid);
				if(entityId == null)
					throw new Exception("uuid not found.");
				TileEntity tileEntity = worldObj.getTileEntity(entityId.x, entityId.y, entityId.z);
				if(tileEntity instanceof TileEntityTeleporter && verifyEntities(entityId.entity, entityId.x, entityId.y, entityId.z)){
					Connector connector = (Connector) node;
					double energy = Math.pow(distance(node, ((TileEntityTeleporter) tileEntity).node), Config.pow);
					if(!connector.tryChangeBuffer(-energy)){
						throw new Exception("We need more energy.");
					}
					List<Entity> entities = entityId.entity;
					if(entities.size() == 0 || entities.size() > Config.entityTeleportationLimit)
						throw new Exception("entity limit.");

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
							if (Config.logging) {
								OpenTeleporter.logger.info(playerMP.getDisplayName() + "| teleportated of |" + xCoord + " " + yCoord + " " + zCoord + "| at |" + zCoord + " " + zCoord + " " + zCoord);
							}
						} else {
							entity.setPosition(xCoord + dx, yCoord + dy, zCoord + dz);
						}
					}
				}
			}else{
				throw new Exception("uuid not found.");
			}
		}
		return null;
	}

	@Callback(doc="returns entities on the teleporter and their UUIDs.")
	public Object[] getEntitiesId(Context context, Arguments arguments) throws Exception{
		try{
			List<Entity> entities = getEntitiesInBound(Entity.class, worldObj, xCoord, yCoord, zCoord, xCoord+1, yCoord+2, zCoord+1);
			if(entities.size() == 0 || entities.size() > Config.entityTeleportationLimit)
				throw new Exception("entity limit.");
				if(entities != null && entities.size() > 0){
				for(int z = 0; z < 30; z++){
					String uuid = UUID.randomUUID().toString();
					if(!UuidList.isUuid(uuid)){
						EntityId entityId = new EntityId(entities, xCoord, yCoord, zCoord);
						UuidList.addUuid(uuid, entityId);
						return new Object[]{uuid};
					}
				}
			}else{
					throw new Exception("entities not found.");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Callback(doc="returns the amount of energy for teleporting.")
	public Object[] getEnergyToTeleport(Context context, Arguments arguments) throws Exception{
		try{
			if(!arguments.checkString(0).isEmpty()){
				String address = arguments.checkString(0);
				Iterable<Node> nodes = node.reachableNodes();
				for(Node n : nodes){
					if(!n.address().isEmpty() && address.equals(n.address())){
						return new Object[]{Math.pow(distance(node, n), Config.pow)};
					}
				}
				throw new Exception("teleporter not found.");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Callback(doc="returns the distance between teleporters.")
	public Object[] getDistance(Context context, Arguments arguments) throws Exception{
		if(!arguments.checkString(0).isEmpty()){
			String address = arguments.checkString(0);
			Iterable<Node> nodes = node.reachableNodes();
			for(Node n : nodes){
				if(!n.address().isEmpty() && address.equals(n.address())){
					return new Object[]{distance(node, n)};
				}
			}
		}
		throw new Exception("teleporter not found.");
	}

	@Callback(doc="returns the distance using uuid")
	public Object[] getDistanceByUuid(Context context, Arguments arguments) throws Exception{
		if(!arguments.checkString(0).isEmpty()){
			String uuid = arguments.checkString(0);
			if(UuidList.isUuid(uuid)){
				EntityId entityId = UuidList.getEntityId(uuid);
				return new Object[]{distance(xCoord, yCoord, zCoord, entityId.x, entityId.y, entityId.z)};
			}
		}
		throw new IllegalArgumentException("Illegal arguments");
	}

	public boolean verifyEntities(List<Entity> entities, int x, int y, int z){
		List<Entity> entities1 = getEntitiesInBound(Entity.class, worldObj, x, y, z, x+1, y+2, z+1);
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

	protected double distance(Node n1, Node n2){
		TileEntity te1 = (TileEntity) n1.host();
		TileEntity te2 = (TileEntity) n2.host();
		if(te1 != null && te2 != null){
			double dx = Math.pow(te2.xCoord-te1.xCoord, 2);
			double dy = Math.pow(te2.yCoord-te1.yCoord, 2);
			double dz = Math.pow(te2.zCoord-te1.zCoord, 2);
			double res = dx+dy+dz;
			return Math.abs(Math.sqrt(res));
		}
		return 0;
	}

	protected double distance(double x1,double y1,double z1, double x2,double y2,double z2){
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

	protected static List<Entity> getEntitiesInBound(Class c, World world, int minx, int miny, int minz, int maxx, int maxy, int maxz){
		return world.getEntitiesWithinAABB(c, AxisAlignedBB.getBoundingBox(minx, miny, minz, maxx, maxy, maxz));
	}
}
