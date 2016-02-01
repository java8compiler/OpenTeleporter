package OpenTeleporter.blocks;

import OpenTeleporter.OpenTeleporter;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * Created by NEO on 21.01.2016.
 */
public class BlockLinkedTeleporter extends Block implements ITileEntityProvider {
    private IIcon side;

    public BlockLinkedTeleporter() {
        super(Material.iron);
        super.setBlockName("LinkedTeleporter");
        super.setHardness(10);
    }

    @Override
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        side = p_149651_1_.registerIcon(OpenTeleporter.MODID+":linkedteleporterside");
    }

    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return side;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return null;
    }
}
