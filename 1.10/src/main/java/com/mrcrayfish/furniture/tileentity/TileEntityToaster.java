/**
 * MrCrayfish's Furniture Mod
 * Copyright (C) 2016  MrCrayfish (http://www.mrcrayfish.com/)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mrcrayfish.furniture.tileentity;

import com.mrcrayfish.furniture.api.RecipeAPI;
import com.mrcrayfish.furniture.api.RecipeData;
import com.mrcrayfish.furniture.gui.inventory.ISimpleInventory;
import com.mrcrayfish.furniture.init.FurnitureSounds;
import com.mrcrayfish.furniture.util.TileEntityUtil;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;

public class TileEntityToaster extends TileEntity implements ITickable, ISimpleInventory
{
	public ItemStack[] slots = new ItemStack[2];

	private int toastingTime = 0;
	private boolean toasting = false;

	public boolean addSlice(ItemStack item)
	{
		for (int i = 0; i < slots.length; i++)
		{
			if (slots[i] == null)
			{
				slots[i] = item.copy();
				return true;
			}
		}
		return false;
	}

	public void removeSlice()
	{
		for (int i = 0; i < slots.length; i++)
		{
			if (slots[i] != null)
			{
				if (!worldObj.isRemote)
				{
					EntityItem entityItem = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, slots[i]);
					worldObj.spawnEntityInWorld(entityItem);
				}
				slots[i] = null;
				TileEntityUtil.markBlockForUpdate(worldObj, pos);
				return;
			}
		}
	}

	public void startToasting()
	{
		this.toasting = true;
		TileEntityUtil.markBlockForUpdate(worldObj, pos);
	}

	public boolean isToasting()
	{
		return toasting;
	}

	public ItemStack getSlice(int slot)
	{
		return slots[slot];
	}

	@Override
	public void update()
	{
		if (toasting)
		{
			if (toastingTime == 200)
			{
				for (int i = 0; i < slots.length; i++)
				{
					if (slots[i] != null)
					{
						if (!worldObj.isRemote)
						{
							RecipeData data = RecipeAPI.getToasterRecipeFromInput(slots[i]);
							EntityItem entityItem = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, data.getOutput().copy());
							worldObj.spawnEntityInWorld(entityItem);
						}
						slots[i] = null;
					}
				}
				if (!worldObj.isRemote)
				{
					worldObj.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, FurnitureSounds.toaster_down, SoundCategory.BLOCKS, 0.75F, 1.0F, true);
					
				}
				toastingTime = 0;
				toasting = false;
				TileEntityUtil.markBlockForUpdate(worldObj, pos);
				worldObj.updateComparatorOutputLevel(pos, blockType);
			}
			else
			{
				toastingTime++;
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		NBTTagList nbttaglist = (NBTTagList) tagCompound.getTag("Items");
		this.slots = new ItemStack[2];

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < this.slots.length)
			{
				this.slots[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}

		this.toastingTime = tagCompound.getInteger("ToastTime");
		this.toasting = tagCompound.getBoolean("Toasting");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.slots.length; ++i)
		{
			if (this.slots[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.slots[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		tagCompound.setTag("Items", nbttaglist);
		tagCompound.setInteger("ToastTime", this.toastingTime);
		tagCompound.setBoolean("Toasting", toasting);
		return tagCompound;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		NBTTagCompound tagCom = pkt.getNbtCompound();
		this.readFromNBT(tagCom);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		NBTTagCompound tagCom = new NBTTagCompound();
		this.writeToNBT(tagCom);
		return new SPacketUpdateTileEntity(pos, getBlockMetadata(), tagCom);
	}
	
	@Override
	public NBTTagCompound getUpdateTag() 
	{
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public int getSize()
	{
		return 2;
	}

	@Override
	public ItemStack getItem(int i)
	{
		return slots[i];
	}

	@Override
	public void clear()
	{
		for (int i = 0; i < slots.length; i++)
		{
			slots[i] = null;
		}

	}
}
