package com.mrcrayfish.furniture.blocks;

import com.mrcrayfish.furniture.util.CollisionHelper;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWreath extends BlockFurniture {

	private static final AxisAlignedBB BOUNDING_BOX_NORTH = CollisionHelper.getBlockBounds(EnumFacing.NORTH, 0.8125, 0, 0, 1, 1, 1);
	private static final AxisAlignedBB BOUNDING_BOX_EAST = CollisionHelper.getBlockBounds(EnumFacing.EAST, 0.8125, 0, 0, 1, 1, 1);
	private static final AxisAlignedBB BOUNDING_BOX_SOUTH = CollisionHelper.getBlockBounds(EnumFacing.SOUTH, 0.8125, 0, 0, 1, 1, 1);
	private static final AxisAlignedBB BOUNDING_BOX_WEST = CollisionHelper.getBlockBounds(EnumFacing.WEST, 0.8125, 0, 0, 1, 1, 1);
	private static final AxisAlignedBB[] BOUNDING_BOX = { BOUNDING_BOX_SOUTH, BOUNDING_BOX_WEST, BOUNDING_BOX_NORTH, BOUNDING_BOX_EAST };
	
	public BlockWreath(Material materialIn) 
	{
		super(materialIn);
		this.setHardness(0.5F);
		this.setStepSound(SoundType.GROUND);
	}
	
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return ColorizerFoliage.getFoliageColorPine();
	}

	@SideOnly(Side.CLIENT)
	public int getRenderColor(IBlockState state)
	{
		return ColorizerFoliage.getFoliageColorPine();
	}

	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
	{
		return ColorizerFoliage.getFoliageColorPine();
	}
	
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		if (this.canPlaceCheck(world, pos, state))
		{
			EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);

			if (!world.getBlockState(pos.offset(enumfacing)).getBlock().isNormalCube(state))
			{
				this.breakBlock(world, pos, state);
				this.dropBlockAsItem(world, pos, state, 0);
				world.setBlockToAir(pos);
			}
		}
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
	{
		if (side.getHorizontalIndex() == -1)
		{
			return false;
		}
		return true;
	}
	
	
	private boolean canPlaceCheck(World world, BlockPos pos, IBlockState state)
	{
		EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);
		if (!this.canPlaceBlockOnSide(world, pos, enumfacing))
		{
			this.dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty(FACING, facing.getOpposite());
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) 
	{
		EnumFacing facing = state.getValue(FACING);
		return BOUNDING_BOX[facing.getHorizontalIndex()];
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState worldIn, World pos, BlockPos state) 
	{
		return NULL_AABB;
	}

	@Override
	public BlockRenderLayer getBlockLayer() 
	{
		return BlockRenderLayer.CUTOUT;
	}
}
