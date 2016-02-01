package OpenTeleporter.blocks;

import OpenTeleporter.OpenTeleporter;
import OpenTeleporter.tileentities.TileEntityTeleporter;
import li.cil.oc.api.CreativeTab;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockTeleporter extends Block implements ITileEntityProvider{
	private IIcon side, top, bottom;

	public BlockTeleporter() {
		super(Material.iron);
		super.setCreativeTab(CreativeTab.instance);
		super.setHardness(10);
		super.setBlockName("Teleporter");
	}

	@Override
	public void registerBlockIcons(IIconRegister p_149651_1_) {
		top = p_149651_1_.registerIcon(OpenTeleporter.MODID+":teleportertop");
		side = p_149651_1_.registerIcon(OpenTeleporter.MODID+":teleporterside");
		bottom = p_149651_1_.registerIcon(OpenTeleporter.MODID+":teleporterbottom");
	}

	@Override
	public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
		if(p_149691_1_ == 1){
			return top;
		}else if(p_149691_1_ == 0){
			return bottom;
		}else{
			return side;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityTeleporter();
	}

}
