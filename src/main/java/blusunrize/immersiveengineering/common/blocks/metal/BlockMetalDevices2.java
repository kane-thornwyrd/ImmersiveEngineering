package blusunrize.immersiveengineering.common.blocks.metal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import blusunrize.immersiveengineering.api.AdvancedAABB;
import blusunrize.immersiveengineering.api.fluid.PipeConnection;
import blusunrize.immersiveengineering.client.render.BlockRenderMetalDevices2;
import blusunrize.immersiveengineering.common.blocks.BlockIEBase;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.ICustomBoundingboxes;
import blusunrize.immersiveengineering.common.util.Lib;
import blusunrize.immersiveengineering.common.util.Utils;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Optional.Interface(iface = "blusunrize.aquatweaks.api.IAquaConnectable", modid = "AquaTweaks")
public class BlockMetalDevices2 extends BlockIEBase implements ICustomBoundingboxes, blusunrize.aquatweaks.api.IAquaConnectable
{
	public static final int META_breakerSwitch=0;
	public static final int META_skycrateDispenser=1;
	public static final int META_energyMeter=2;
	public static final int META_electricLantern=3;
	public static final int META_floodLight=4;
	public static final int META_fluidPipe=5;
	public static final int META_fluidPump=6;

	public BlockMetalDevices2()
	{
		super("metalDevice2", Material.iron, 1, ItemBlockMetalDevices2.class,
				"breakerSwitch","skycrateDispenser","energyMeter","electricLantern","floodlight","fluidPipe", "fluidPump");
		setHardness(3.0F);
		setResistance(15.0F);
	}


	@Override
	public boolean allowHammerHarvest(int meta)
	{
		return true;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
	{
		return super.getPickBlock(target, world, x, y, z, player);
	}
	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player)
	{
	}
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> ret = super.getDrops(world, x, y, z, metadata, fortune);
		return ret;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 3));
		list.add(new ItemStack(item, 1, 4));
		list.add(new ItemStack(item, 1, 5));
		list.add(new ItemStack(item, 1, 6));
		//		for(int i=0; i<subNames.length; i++)
		//		{
		//			list.add(new ItemStack(item, 1, i));
		//		}
	}

	IIcon[] pumpIcons = new IIcon[7];

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		for(int i=0; i<this.subNames.length; i++)
			if(i!=META_fluidPump)
				this.icons[i][0] = iconRegister.registerIcon("immersiveEngineering:metal2_"+this.subNames[i]);

		pumpIcons[0] = iconRegister.registerIcon("immersiveEngineering:metal2_pump_side_none");
		pumpIcons[1] = iconRegister.registerIcon("immersiveEngineering:metal2_pump_side_in");
		pumpIcons[2] = iconRegister.registerIcon("immersiveEngineering:metal2_pump_side_out");
		pumpIcons[3] = iconRegister.registerIcon("immersiveEngineering:metal2_pump_bottom_none");
		pumpIcons[4] = iconRegister.registerIcon("immersiveEngineering:metal2_pump_bottom_in");
		pumpIcons[5] = iconRegister.registerIcon("immersiveEngineering:metal2_pump_bottom_out");
		pumpIcons[6] = iconRegister.registerIcon("immersiveEngineering:metal2_pump_top");
	}
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		return super.getIcon(world, x, y, z, side);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if(meta==META_fluidPump)
			return pumpIcons[Math.min(side+1,6)];
		return super.getIcon(side, meta);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	@Override
	public int getRenderType()
	{
		return BlockRenderMetalDevices2.renderID;
	}


	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if(!player.isSneaking() && world.getTileEntity(x, y, z) instanceof TileEntityBreakerSwitch)
		{
			if(!world.isRemote)
			{
				if(Utils.isHammer(player.getCurrentEquippedItem()))
				{
					((TileEntityBreakerSwitch)world.getTileEntity(x, y, z)).inverted = !((TileEntityBreakerSwitch)world.getTileEntity(x, y, z)).inverted;
					player.addChatMessage(new ChatComponentTranslation(Lib.CHAT_INFO+ (((TileEntityBreakerSwitch)world.getTileEntity(x, y, z)).inverted?"invertedOn":"invertedOff")));
					world.getTileEntity(x, y, z).markDirty();
				}
				else
				{
					((TileEntityBreakerSwitch)world.getTileEntity(x, y, z)).toggle();
					world.getTileEntity(x, y, z).markDirty();
				}
				world.notifyBlocksOfNeighborChange(x, y, z, this);
				for(ForgeDirection fd:  ForgeDirection.VALID_DIRECTIONS)
					world.notifyBlocksOfNeighborChange(x+fd.offsetX, y+fd.offsetY, z+fd.offsetZ, this);

				world.func_147451_t(x, y, z);
			}
			return true;
		}
		else if(world.getTileEntity(x, y, z) instanceof TileEntityFluidPipe_old)
		{
			if(!world.isRemote) {
				TileEntityFluidPipe_old fluidPipe = ((TileEntityFluidPipe_old) world.getTileEntity(x, y, z));
				if(Utils.isHammer(player.getCurrentEquippedItem()))
				{
					//					if (++fluidPipe.mode > 2) {
					//						fluidPipe.mode = 0;
					//					}
					fluidPipe.markDirty();
					world.markBlockForUpdate(x, y, z);
					return true;
				}
				else
					player.addChatMessage(new ChatComponentText("Server Connections: " + fluidPipe.connections.size()));
			}
		}
		else if(Utils.isHammer(player.getCurrentEquippedItem()) && world.getTileEntity(x, y, z) instanceof TileEntityFloodLight)
		{
			if(player.isSneaking())
			{
				((TileEntityFloodLight)world.getTileEntity(x, y, z)).rotX+=10;
				((TileEntityFloodLight)world.getTileEntity(x, y, z)).rotX%=360;
				world.getTileEntity(x, y, z).markDirty();
				world.markBlockForUpdate(x, y, z);
			}
			else
			{
				((TileEntityFloodLight)world.getTileEntity(x, y, z)).rotY+=10;
				((TileEntityFloodLight)world.getTileEntity(x, y, z)).rotY%=360;
				world.getTileEntity(x, y, z).markDirty();
				world.markBlockForUpdate(x, y, z);
			}
		}
		else if(Utils.isHammer(player.getCurrentEquippedItem()) && world.getTileEntity(x, y, z) instanceof TileEntityFluidPump)
		{
			TileEntityFluidPump pump = (TileEntityFluidPump) world.getTileEntity(x, y, z);
			if (!pump.dummy)
			{
				if (!world.isRemote)
				{
					pump.toggleSide(side);
					world.markBlockForUpdate(x, y, z);
				}
				return true;
			}
		}
		else if(world.getTileEntity(x, y, z) instanceof TileEntityFluidPipe)
		{
			if(!world.isRemote && Utils.isHammer(player.getCurrentEquippedItem()))
			{
				TileEntityFluidPipe tile = ((TileEntityFluidPipe)world.getTileEntity(x, y, z));
				ForgeDirection fd =ForgeDirection.UNKNOWN;

				ArrayList<AxisAlignedBB> boxes = addCustomSelectionBoxesToList(world, x, y, z);
				for(AxisAlignedBB box : boxes)
				{
					if(box.expand(.002,.002,.002).isVecInside(Vec3.createVectorHelper(hitX, hitY, hitZ)))
						if(box instanceof AdvancedAABB)
							fd = ((AdvancedAABB)box).fd;
				}
				if(fd!=null && fd!=ForgeDirection.UNKNOWN)
				{
					tile.toggleSide(fd.ordinal());
					world.markBlockForUpdate(x, y, z);
					if(world.getTileEntity(x+fd.offsetX, y+fd.offsetY, z+fd.offsetZ) instanceof TileEntityFluidPipe)
					{
						((TileEntityFluidPipe)world.getTileEntity(x+fd.offsetX, y+fd.offsetY, z+fd.offsetZ)).toggleSide(fd.getOpposite().ordinal());
						world.markBlockForUpdate(x+fd.offsetX, y+fd.offsetY, z+fd.offsetZ);
					}
					TileEntityFluidPipe.indirectConnections.clear();
					return true;
				}
				//				List<DirectionalFluidOutput> list = TileEntityFluidPipe.getConnectedFluidHandlers(new ChunkCoordinates(x,y,z), world);
				//				System.out.println("list size: "+list.size());
			}
		}
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		if(world.getTileEntity(x, y, z) instanceof TileEntityBreakerSwitch)
		{
			int f = ((TileEntityBreakerSwitch)world.getTileEntity(x, y, z)).facing;
			int side = ((TileEntityBreakerSwitch)world.getTileEntity(x, y, z)).sideAttached;
			if(side==0)
				this.setBlockBounds(f<4?.25f:f==5?.75f:0, .1875f, f>3?.25f:f==3?.75f:0, f<4?.75f:f==4?.25f:1,.8125f,f>3?.75f:f==2?.25f:1);
			else if(side==1)
				this.setBlockBounds(f>=4?.1875f:.25f,0,f<=3?.1875f:.25f, f>=4?.8125f:.75f,.25f,f<=3?.8125f:.75f);
			else
				this.setBlockBounds(f>=4?.1875f:.25f,.75f,f<=3?.1875f:.25f, f>=4?.8125f:.75f,1,f<=3?.8125f:.75f);
		}
		else if (world.getTileEntity(x, y, z) instanceof TileEntityFluidPipe_old)
		{
			TileEntityFluidPipe_old pipe = ((TileEntityFluidPipe_old) world.getTileEntity(x, y, z));
			if(pipe.connections.size()==0)
				this.setBlockBounds(.125f,0,.125f, .875f,1,.875f);
			else
			{
				byte connections = 0;
				for(PipeConnection connection : pipe.connections)
					if(connection.direction != ForgeDirection.UNKNOWN)
						connections = (byte) (connections | 1 << connection.direction.ordinal());
				if(pipe.connections.size()==1)
				{
					byte tempCon = connections;
					for(int i = 0; i < 6; i++)
						if((tempCon & (1 << i)) >> i == 1)
							connections = (byte) (connections | 1 << ForgeDirection.OPPOSITES[i]);
				}

				if(connections == 48) // 110000
					this.setBlockBounds(0,.125f,.125f, 1,.875f,.875f);
				else if(connections == 12) // 001100
					this.setBlockBounds(.125f,.125f,0, .875f,.875f,1);
				else if(connections == 3) // 000011
					this.setBlockBounds(.125f,0,.125f, .875f,1,.875f);
				else
					this.setBlockBounds(0,0,0, 1,1,1);
			}
		}
		else if (world.getTileEntity(x, y, z) instanceof TileEntityFluidPipe)
			this.setBlockBounds(0,0,0,1,1,1);
		else if (world.getTileEntity(x, y, z) instanceof TileEntityFluidPump)
		{
			if (((TileEntityFluidPump) world.getTileEntity(x, y, z)).dummy)
				this.setBlockBounds(0.1875f, 0, 0.1875f, 0.8125f, 1, 0.8125f);
			else
				this.setBlockBounds(0, 0, 0, 1, 1, 1);
		}
		else if(world.getBlockMetadata(x, y, z)==META_electricLantern)
			this.setBlockBounds(.1875f,0,.1875f, .8125f,1,.8125f);
		else
			this.setBlockBounds(0,0,0,1,1,1);
	}


	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity ent)
	{
		if(world.getTileEntity(x, y, z) instanceof TileEntityFluidPipe)
		{
			TileEntityFluidPipe tile = (TileEntityFluidPipe)world.getTileEntity(x, y, z);
			byte connections = tile.getConnectionByte();
			if(/*connections==16||connections==32||*/connections==48)
			{
				this.setBlockBounds(0,.25f,.25f, 1,.75f,.75f);
				addCollisionBox(world, x, y, z, aabb, list, ent);
				if((connections&16) == 0)
				{
					this.setBlockBounds(0,.125f,.125f, .125f,.875f,.875f);
					addCollisionBox(world, x, y, z, aabb, list, ent);
				}
				if((connections&32) == 0)
				{
					this.setBlockBounds(.875f,.125f,.125f, 1,.875f,.875f);
					addCollisionBox(world, x, y, z, aabb, list, ent);
				}
			}
			else if(/*connections==4||connections==8||*/connections==12)
			{
				this.setBlockBounds(.25f,.25f,0, .75f,.75f,1);
				addCollisionBox(world, x, y, z, aabb, list, ent);
				if((connections&4) == 0)
				{
					this.setBlockBounds(.125f,.125f,0, .875f,.875f,.125f);
					addCollisionBox(world, x, y, z, aabb, list, ent);
				}
				if((connections&8) == 0)
				{
					this.setBlockBounds(.125f,.125f,.875f, .875f,.875f,1);
					addCollisionBox(world, x, y, z, aabb, list, ent);
				}
			}
			else if(connections==0 || /*connections==1||connections==2||*/connections==3)
			{
				this.setBlockBounds(.25f,0,.25f, .75f,1,.75f);
				addCollisionBox(world, x, y, z, aabb, list, ent);
				if((connections&1) == 0)
				{
					this.setBlockBounds(.125f,0,.125f, .875f,.125f,.875f);
					addCollisionBox(world, x, y, z, aabb, list, ent);
				}
				if((connections&2) == 0)
				{
					this.setBlockBounds(.125f,.875f,.125f, .875f,1,.875f);
					addCollisionBox(world, x, y, z, aabb, list, ent);
				}
			}
			else
			{
				this.setBlockBounds(.25f,.25f,.25f, .75f,.75f,.75f);
				addCollisionBox(world, x, y, z, aabb, list, ent);
				for(int i=0; i<6; i++)
				{
					if((connections & 0x1)==1)
					{
						this.setBlockBounds(i==4?0:i==5?.875f:.125f, i==0?0:i==1?.875f:.125f, i==2?0:i==3?.875f:.125f,  i==4?.125f:i==5?1:.875f, i==0?.125f:i==1?1:.875f, i==2?.125f:i==3?1:.875f);
						addCollisionBox(world, x, y, z, aabb, list, ent);
					}
					connections >>= 1;
				}
			}
		}
		else
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, ent);
	}

	@Override
	public ArrayList<AxisAlignedBB> addCustomSelectionBoxesToList(World world, int x, int y, int z)
	{
		ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		if(world.getTileEntity(x, y, z) instanceof TileEntityFluidPipe)
		{
			TileEntityFluidPipe tile = (TileEntityFluidPipe)world.getTileEntity(x, y, z);
			byte connections = tile.getAvailableConnectionByte();
			byte availableConnections = tile.getConnectionByte();
			double[] baseAABB = {.25,.75, .25,.75, .25,.75}; 
			for(int i=0; i<6; i++)
			{
				double depth = tile.getConnectionStyle(i)==0?.25:.125;
				double size = tile.getConnectionStyle(i)==0?.25:.125;
				if((connections & 0x1)==1)
					list.add(new AdvancedAABB(i==4?0:i==5?1-depth:size, i==0?0:i==1?1-depth:size, i==2?0:i==3?1-depth:size,  i==4?depth:i==5?1:1-size, i==0?depth:i==1?1:1-size, i==2?depth:i==3?1:1-size, ForgeDirection.getOrientation(i)));
				if((availableConnections & 0x1)==1)
					baseAABB[i] += i%2==1?.125: -.125;
				availableConnections = (byte)(availableConnections>>1);
				connections = (byte)(connections>>1);
			}
			list.add(new AdvancedAABB(baseAABB[4],baseAABB[0],baseAABB[2], baseAABB[5],baseAABB[1],baseAABB[3], ForgeDirection.UNKNOWN));
		}
		return list;
	}
	@Override
	public boolean addSpecifiedSubBox(World world, int x, int y, int z, EntityPlayer player, AxisAlignedBB box, Vec3 hitVec, ArrayList<AxisAlignedBB> list)
	{
		if(world.getTileEntity(x, y, z) instanceof TileEntityFluidPipe && box instanceof AdvancedAABB && ((AdvancedAABB)box).fd!=null)
		{
			ForgeDirection fd = ((AdvancedAABB)box).fd;
			//			AxisAlignedBB newBox = box.expand(fd.offsetX!=0?0:.03125, fd.offsetY!=0?0:.03125, fd.offsetZ!=0?0:.03125);
			//			if(newBox.isVecInside(hitVec.addVector(-x,-y,-z)))
			//				return newBox;
			if(box.expand(.002,.002,.002).isVecInside(hitVec.addVector(-x,-y,-z)))
			{
				list.add(fd==ForgeDirection.UNKNOWN?box: box.expand(fd.offsetX!=0?0:.03125, fd.offsetY!=0?0:.03125, fd.offsetZ!=0?0:.03125));
				return fd==ForgeDirection.UNKNOWN&&list.isEmpty();	
			}
		}
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		this.setBlockBoundsBasedOnState(world,x,y,z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		this.setBlockBoundsBasedOnState(world,x,y,z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}


	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		//		int meta = world.getBlockMetadata(x, y, z);
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		switch(meta)
		{
		case META_breakerSwitch:
			return new TileEntityBreakerSwitch();
		case META_skycrateDispenser:
			return new TileEntitySkycrateDispenser();
		case META_energyMeter:
			return new TileEntityEnergyMeter();
		case META_electricLantern:
			return new TileEntityElectricLantern();
		case META_floodLight:
			return new TileEntityFloodLight();
		case META_fluidPipe:
			return new TileEntityFluidPipe();
		case META_fluidPump:
			return new TileEntityFluidPump();
		}
		return null;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6)
	{
		if (world.getTileEntity(x, y, z) instanceof TileEntityFluidPump)
		{
			if (((TileEntityFluidPump) world.getTileEntity(x, y, z)).dummy)
				world.setBlockToAir(x, y - 1, z);
			else
				world.setBlockToAir(x, y + 1, z);
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block nbid)
	{
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		if(world.getTileEntity(x, y, z) instanceof TileEntityFluidPipe)
			TileEntityFluidPipe.indirectConnections.clear();
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ)
	{
		if(world.getTileEntity(tileX, tileY, tileZ) instanceof IFluidHandler)
			TileEntityFluidPipe.indirectConnections.clear();
	}

	//	@Override
	//	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ)
	//	{
	//		TileEntity tileEntity = world.getTileEntity(x, y, z);
	//		if (tileEntity instanceof TileEntityFluidPipe_old && !(world.getTileEntity(tileX, tileY, tileZ) instanceof TileEntityFluidPipe_old))
	//		{
	//			TileEntityFluidPipe_old fluidPipe = (TileEntityFluidPipe_old) tileEntity;
	//			boolean connected = false;
	//			for (PipeConnection connection : fluidPipe.connections)
	//			{
	//				connected = connection.to.equals(new ChunkCoordinates(tileX, tileY, tileZ));
	//				if(connected)
	//					break;
	//			}
	//			TileEntity neighbouringTileEntity = world.getTileEntity(tileX, tileY, tileZ);
	//
	//			if (PipeConnection.isTank(neighbouringTileEntity, PipeConnection.toDirection(Utils.toCC(neighbouringTileEntity), Utils.toCC(fluidPipe)).getOpposite()))
	//			{
	//				if (!connected)
	//				{
	//					fluidPipe.connections.add(new PipeConnection(Utils.toCC(fluidPipe), Utils.toCC(neighbouringTileEntity), PipeConnection.Type.TANK));
	//					fluidPipe.markDirty();
	//					if (world instanceof World)
	//						((World) world).markBlockForUpdate(x, y, z);
	//				}
	//			}
	//			else if (connected)
	//			{
	//				for (PipeConnection connection : fluidPipe.connections)
	//					if (connection.to.equals(new ChunkCoordinates(tileX, tileY, tileZ)))
	//					{
	//						fluidPipe.connections.remove(connection);
	//						break;
	//					}
	//				fluidPipe.markDirty();
	//				if (world instanceof World)
	//					((World) world).markBlockForUpdate(x, y, z);
	//			}
	//		}
	//	}
	//
	//	@Override
	//	public void onBlockAdded(World world, int x, int y, int z)
	//	{
	//		TileEntity tileEntity = world.getTileEntity(x, y, z);
	//		if (tileEntity instanceof TileEntityFluidPipe_old)
	//		{
	//			TileEntityFluidPipe_old fluidPipe = (TileEntityFluidPipe_old) tileEntity;
	//			for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
	//			{
	//				TileEntity neighbouringTileEntity = world.getTileEntity(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
	//
	//				if (neighbouringTileEntity instanceof TileEntityFluidPipe_old)
	//				{
	//					((TileEntityFluidPipe_old) neighbouringTileEntity).connections.add(new PipeConnection(Utils.toCC(neighbouringTileEntity), Utils.toCC(fluidPipe), direction.getOpposite(), PipeConnection.Type.PIPE));
	//					fluidPipe.connections.add(new PipeConnection(Utils.toCC(fluidPipe), Utils.toCC(neighbouringTileEntity), direction, PipeConnection.Type.PIPE));
	//					fluidPipe.markDirty();
	//					neighbouringTileEntity.markDirty();
	//					world.markBlockForUpdate(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
	//				}
	//				else if(neighbouringTileEntity instanceof IFluidPipe)
	//				{
	//					((IFluidPipe) neighbouringTileEntity).addConnection(new PipeConnection(Utils.toCC(neighbouringTileEntity), Utils.toCC(fluidPipe), direction.getOpposite(), PipeConnection.Type.PIPE));
	//					fluidPipe.connections.add(new PipeConnection(Utils.toCC(fluidPipe), Utils.toCC(neighbouringTileEntity), direction, PipeConnection.Type.PIPE));
	//					fluidPipe.markDirty();
	//					neighbouringTileEntity.markDirty();
	//					world.markBlockForUpdate(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
	//				}
	//				else
	//				{
	//					boolean isTank = neighbouringTileEntity instanceof IFluidHandler;
	//					if(isTank)
	//					{
	//						IFluidHandler fluidHandler = (IFluidHandler) neighbouringTileEntity;
	//						FluidTankInfo[] tankInfo = fluidHandler.getTankInfo(direction.getOpposite());
	//						isTank = tankInfo!=null&&tankInfo.length > 0;
	//					}
	//					if(isTank)
	//					{
	//						fluidPipe.connections.add(new PipeConnection(Utils.toCC(fluidPipe), Utils.toCC(neighbouringTileEntity), direction, PipeConnection.Type.TANK));
	//						fluidPipe.markDirty();
	//					}
	//				}
	//			}
	//			world.markBlockForUpdate(x, y, z);
	//			//			world.markBlockRangeForRenderUpdate(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
	//		}
	//	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
	{
		if(world.getTileEntity(x, y, z) instanceof TileEntityBreakerSwitch)
		{
			TileEntityBreakerSwitch breaker = ((TileEntityBreakerSwitch)world.getTileEntity(x, y, z));
			boolean power = (breaker.active&&!breaker.inverted) || (!breaker.active&&breaker.inverted);
			return power?15:0;
		}
		return 0;
	}
	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side)
	{
		if(world.getTileEntity(x, y, z) instanceof TileEntityBreakerSwitch)
		{
			TileEntityBreakerSwitch breaker = ((TileEntityBreakerSwitch)world.getTileEntity(x, y, z));
			int powerSide = breaker.sideAttached>0?breaker.sideAttached-1:breaker.facing;
			boolean power = (breaker.active&&!breaker.inverted) || (!breaker.active&&breaker.inverted);
			return power&&ForgeDirection.OPPOSITES[side]==powerSide?15:0;
		}
		return 0;
	}
	@Override
	public boolean canProvidePower()
	{
		return true;
	}
	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
	{
		if(world.getBlockMetadata(x, y, z)==META_breakerSwitch)
			return super.canConnectRedstone(world, x, y, z, side);
		return false;
	}


	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z)
	{
		if(world.getTileEntity(x, y, z) instanceof TileEntityElectricLantern)
			return ((TileEntityElectricLantern)world.getTileEntity(x, y, z)).active?15:0;
		return 0;
	}

	@Optional.Method(modid = "AquaTweaks")
	public boolean shouldRenderFluid(IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		return meta==META_breakerSwitch||meta==META_electricLantern;
	}
	@Optional.Method(modid = "AquaTweaks")
	public boolean canConnectTo(IBlockAccess world, int x, int y, int z, int side)
	{
		int meta = world.getBlockMetadata(x, y, z);
		return meta==META_breakerSwitch||meta==META_electricLantern;
	}
}