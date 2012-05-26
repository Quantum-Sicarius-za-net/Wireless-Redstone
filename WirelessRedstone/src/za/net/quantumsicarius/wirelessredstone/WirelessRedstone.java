/*
This file is part of WirelessRedstone.

WirelessRedstone is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

WirelessRedstone is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with WirelessRedstone.  If not, see http://www.gnu.org/licenses/.
*/

package za.net.quantumsicarius.wirelessredstone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.player.SpoutPlayer;

import za.net.quantumsicarius.wirelessredstone.Blocks.RecieverBlock;
import za.net.quantumsicarius.wirelessredstone.Blocks.TransmitterBlock;
import za.net.quantumsicarius.wirelessredstone.gui.GUI;

public class WirelessRedstone extends JavaPlugin implements Listener{
	
	//SaveAndLoad saveLoad;
	Serialize serial = new Serialize();
	
	private static Texture Transmitter_Texture_on;
	private static CustomBlock Transmitter_Block_on;
	
	private static Texture Transmitter_Texture_off;
	private static CustomBlock Transmitter_Block_off;
	
	private static Texture Reciever_Texture_on;
	private static CustomBlock Reciever_Block_on;
	
	private static Texture Reciever_Texture_off;
	private static CustomBlock Reciever_Block_off;
	
	HashMap<SpoutPlayer, GUI> player_GUI = new HashMap<SpoutPlayer, GUI>();
	HashMap<SpoutPlayer, SpoutBlock> player_block_clicked = new HashMap<SpoutPlayer, SpoutBlock>();
	
	// This stores the channel to a specific block
	HashMap<SpoutBlock, Integer> block_channel;
	// This is the list off all receiver blocks
	ArrayList<SpoutBlock> spout_Reciever_block;
	// This stores the Channel to the list of active transmitters on that specified channel
	HashMap<Integer, ArrayList<SpoutBlock>> transmitter_blocks_on_channel;
	
	GUI gui;
	
	@Override
	public void onDisable() {
		System.out.println("Disabled!");
		
		try {
			// Save the block channels	
			for (Entry<SpoutBlock, Integer> entry : block_channel.entrySet())
			{
				serial.SerializeBlock(entry.getKey(), entry.getValue());
			}
			
			// Save the Receiver List
			serial.SaveBlockList(spout_Reciever_block);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onEnable() {
		System.out.println("Enabled!");
		
		/// Load Block Channels
		block_channel = serial.deserialize();
		
		// Load Receiver blocks
		spout_Reciever_block = serial.deserializeList();
		
		if (block_channel == null) {
			block_channel = new HashMap<SpoutBlock, Integer>();
		}
		if (transmitter_blocks_on_channel == null) {
			transmitter_blocks_on_channel  = new HashMap<Integer, ArrayList<SpoutBlock>>();
		}
		if (spout_Reciever_block == null) {
			spout_Reciever_block  = new ArrayList<SpoutBlock>();
		}
		
		loadItems();
		loadRecipes();
		
		// Register event listeners
		getServer().getPluginManager().registerEvents(this, this);
		
		// Load preLogin cache
		//SpoutManager.getFileManager().addToPreLoginCache(this, "http://s13.postimage.org/u2noy7pab/tx_On.png");
		//SpoutManager.getFileManager().addToPreLoginCache(this, "http://s17.postimage.org/xp94lmdln/tx_Off.png");
		//SpoutManager.getFileManager().addToPreLoginCache(this, "http://s7.postimage.org/owy3ah9vb/rx_On.png");
		//SpoutManager.getFileManager().addToPreLoginCache(this, "ttp://s16.postimage.org/q6fezbnht/rx_Off.png");
	}
	
	private void loadItems() {
		
    	Transmitter_Texture_on = new Texture(this, "http://s14.postimage.org/6morwcyn1/Transmitter_On.png", 64, 32, 32);
    	Transmitter_Block_on = new TransmitterBlock(this, Transmitter_Texture_on, "Wireless Transmitter On");
    	
    	Transmitter_Texture_off = new Texture(this, "http://s9.postimage.org/jutkuqhjv/Transmitter_Off.png", 64, 32, 32);
    	Transmitter_Block_off = new TransmitterBlock(this, Transmitter_Texture_off, "Wireless Transmitter Off");
    	
       	Reciever_Texture_on = new Texture(this, "http://s13.postimage.org/4y344bdmb/Reciever_On.png", 64, 32, 32);
    	Reciever_Block_on = new RecieverBlock(this, Reciever_Texture_on, "Wireless Reciever On");
    	
       	Reciever_Texture_off = new Texture(this, "http://s14.postimage.org/hwhhrb3od/Receiver_Off.png", 64, 32, 32);
    	Reciever_Block_off = new RecieverBlock(this, Reciever_Texture_off, "Wireless Reciever off");
	}
	
	private void loadRecipes() {
		ItemStack result = new SpoutItemStack(Transmitter_Block_off);
		SpoutShapedRecipe recipe = new SpoutShapedRecipe(result);
		recipe.shape("ABA", "BCB", "ABA");
		recipe.setIngredient('A', MaterialData.ironIngot);
		recipe.setIngredient('B', MaterialData.redstone);
		recipe.setIngredient('C', MaterialData.redstoneTorchOn);
		SpoutManager.getMaterialManager().registerSpoutRecipe(recipe);
		
		ItemStack result2 = new SpoutItemStack(Reciever_Block_off);
		SpoutShapedRecipe recipe2 = new SpoutShapedRecipe(result2);
		recipe2.shape("ABA", "BCB", "ABA");
		recipe2.setIngredient('A', MaterialData.ironIngot);
		recipe2.setIngredient('B', MaterialData.redstone);
		recipe2.setIngredient('C', MaterialData.lever);
		SpoutManager.getMaterialManager().registerSpoutRecipe(recipe2);
	}
	
	// This should free memory in theory
	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		if (player_GUI.containsKey(event.getPlayer())) {
			player_GUI.remove(event.getPlayer());
		}
		if (player_block_clicked.containsKey(event.getPlayer())) {
			player_block_clicked.remove(event.getPlayer());
		}
	}
	
	@EventHandler
	public void buttonPress(ButtonClickEvent event) {
		if (player_GUI.containsKey(event.getPlayer())) {
			
			if (player_GUI.get(event.getPlayer()).isAddButton(event.getButton())) {
				//System.out.println("Add button pressed");
				
				SpoutBlock the_block = player_block_clicked.get(event.getPlayer());
				
				if (player_GUI.get(event.getPlayer()).title() == "Transmitter") {
					// Remove the block from the old channel
					transmitter_blocks_on_channel.get(block_channel.get(the_block)).remove(the_block);
					//System.out.println("Removing the block from the list, there are now: " + transmitter_blocks_on_channel.get(block_channel.get(the_block)).size());
					
					// Update the receiver
					ReceiverUpdate(the_block);
					
					// Update to new channel
					int block_channel_decrement = block_channel.get(the_block) + 1;
					block_channel.put(the_block, block_channel_decrement);
					
					// Add the new channel
					if (!transmitter_blocks_on_channel.containsKey(block_channel.get(the_block))) {
						//System.out.println("The channel doesn't exsist creating! Channel: " + (block_channel.get(the_block)));
						transmitter_blocks_on_channel.put(block_channel.get(the_block), new ArrayList<SpoutBlock>());
					}
					
					// If the block is powered add him!
					if (the_block.isBlockPowered() || the_block.isBlockFacePowered(BlockFace.NORTH) || the_block.isBlockFacePowered(BlockFace.SOUTH) || the_block.isBlockFacePowered(BlockFace.WEST) || the_block.isBlockFacePowered(BlockFace.EAST)) {
						transmitter_blocks_on_channel.get(block_channel.get(the_block)).add(the_block);
						//System.out.println("Adding the block to the list, there are now: " + transmitter_blocks_on_channel.get(block_channel.get(the_block)).size());
					}
				}
				else {
					// Update to new channel
					int block_channel_decrement = block_channel.get(the_block) + 1;
					block_channel.put(player_block_clicked.get(event.getPlayer()), block_channel_decrement);
				}
				
				// Update the receiver
				ReceiverUpdate(the_block);
				
				System.out.println("Channel: " + block_channel.get((SpoutBlock) player_block_clicked.get(event.getPlayer())));
				player_GUI.get(event.getPlayer()).updateGUI(event.getPlayer(), block_channel.get((SpoutBlock) player_block_clicked.get(event.getPlayer())));
			}
			else if (player_GUI.get(event.getPlayer()).isSubtractButton(event.getButton())) {
				System.out.println("Subtract button pressed");
				
				SpoutBlock the_block = player_block_clicked.get(event.getPlayer());
				
				if (player_GUI.get(event.getPlayer()).title() == "Transmitter") {
					// Remove the block from the old channel
					transmitter_blocks_on_channel.get(block_channel.get(the_block)).remove(the_block);
					//System.out.println("Removing the block from the list, there are now: " + transmitter_blocks_on_channel.get(block_channel.get(the_block)).size());
					
					// Update the receiver
					ReceiverUpdate(the_block);
					
					// Update to new channel
					int block_channel_decrement = block_channel.get(the_block) - 1;
					block_channel.put(the_block, block_channel_decrement);
					
					// Add the new channel
					if (!transmitter_blocks_on_channel.containsKey(block_channel.get(the_block))) {
						//System.out.println("The channel doesn't exsist creating! Channel: " + (block_channel.get(the_block)));
						transmitter_blocks_on_channel.put(block_channel.get(the_block), new ArrayList<SpoutBlock>());
					}
					// If the block is powered add him!
					if (the_block.isBlockPowered() || the_block.isBlockFacePowered(BlockFace.NORTH) || the_block.isBlockFacePowered(BlockFace.SOUTH) || the_block.isBlockFacePowered(BlockFace.WEST) || the_block.isBlockFacePowered(BlockFace.EAST)) {
						transmitter_blocks_on_channel.get(block_channel.get(the_block)).add(the_block);
						//System.out.println("Adding the block to the list, there are now: " + transmitter_blocks_on_channel.get(block_channel.get(the_block)).size());
					}
				}
				else {
					// Update to new channel
					int block_channel_decrement = block_channel.get(the_block) - 1;
					block_channel.put(the_block, block_channel_decrement);
				}
				
				// Update the receiver
				ReceiverUpdate(the_block);
				
				
				System.out.println("Channel: " + block_channel.get((SpoutBlock) player_block_clicked.get(event.getPlayer())));
				
				player_GUI.get(event.getPlayer()).updateGUI(event.getPlayer(), block_channel.get((SpoutBlock) player_block_clicked.get(event.getPlayer())));
			}
		}
	}
	
	@EventHandler
	public void blockRightClick(PlayerInteractEvent event) {
		SpoutBlock block = (SpoutBlock) event.getClickedBlock();
		SpoutPlayer player = (SpoutPlayer) event.getPlayer();
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (player.isSpoutCraftEnabled()) {
				if (block.getCustomBlock() == Reciever_Block_off | block.getCustomBlock() == Reciever_Block_on) {
					if (block_channel.containsKey(block)) {
						player_block_clicked.put(player, block);
						gui = new GUI("Reciever",block_channel.get((SpoutBlock) player_block_clicked.get(event.getPlayer())), this, (SpoutPlayer) player);
						player_GUI.put(player, gui);
						System.out.println("Channel on Right CLick: " + block_channel.get(block));
					}
				}
				else if ((block.getCustomBlock() == Transmitter_Block_off | block.getCustomBlock() == Transmitter_Block_on)) {
					if (block_channel.containsKey(block)) {
						gui = new GUI("Transmitter",block_channel.get(block), this, (SpoutPlayer) player);
						player_GUI.put(player, gui);
						player_block_clicked.put(player, block);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void BlockPlace(BlockPlaceEvent event) {
		
		SpoutBlock block = (SpoutBlock) event.getBlock();
		
		if (block.getCustomBlock() == Reciever_Block_off) {
			spout_Reciever_block.add(block);
			block_channel.put(block, 0);
			
			// Update Receiver
			ReceiverUpdate(block);
		}
		
		else if (block.getCustomBlock() == Transmitter_Block_off) {
			block_channel.put(block, 0);
			
			if (!transmitter_blocks_on_channel.containsKey(block_channel.get(block))) {
				transmitter_blocks_on_channel.put(block_channel.get(block), new ArrayList<SpoutBlock>());
			}
			
			if (block.isBlockPowered() || block.isBlockFacePowered(BlockFace.NORTH) || block.isBlockFacePowered(BlockFace.SOUTH) || block.isBlockFacePowered(BlockFace.WEST) || block.isBlockFacePowered(BlockFace.EAST)) {
				PowerChange(block, true);
			}
		}
	}
	
	@EventHandler
	public void BlockBreak(BlockBreakEvent event) {
		
		SpoutBlock block = (SpoutBlock) event.getBlock();
		
		if (block.getCustomBlock() == Reciever_Block_off | block.getCustomBlock() == Reciever_Block_on) {
			// Remove block from the HashMap
			if (block_channel.containsKey(block)) {
				block_channel.remove(block);
			}
			
			// Remove from List
			spout_Reciever_block.remove(block);
			
			if (block.getCustomBlock() == Reciever_Block_on) {
				
				block.setBlockPowered(false);
				for (int i = 0; i < 4; i++) {
					side_block(block, i).setBlockPowered(false);
				}
			}
		}
		else if (block.getCustomBlock() == Transmitter_Block_off | block.getCustomBlock() == Transmitter_Block_on) {
			// Remove block from the HashMap
			if (block_channel.containsKey(block)) {
				// Remove block from the List
				if (transmitter_blocks_on_channel.containsKey(block_channel.get(block))) {
					transmitter_blocks_on_channel.get(block_channel.get(block)).remove(block);
				}
				
				// Remove the block
				ReceiverUpdate(block);
				block_channel.remove(block);
			}
			
			block.setType(Material.AIR);
		}
	}
	
	// Checks whether the chunk that unloads is a chunk with a receiver on and if it is, cancel the event.
	@EventHandler
	public void chunkUnload(ChunkUnloadEvent event) {
		for (int i = 0; i < spout_Reciever_block.size(); i++) {
			if (event.getChunk() == spout_Reciever_block.get(i).getChunk()) {
				System.out.println("Stopped chunk from unloading!");
				event.setCancelled(true);
				event.getChunk().load(true);
			}
		}
	}
	
	@EventHandler
	public void redStoneEvent(BlockPhysicsEvent event) {
		
		SpoutBlock b = (SpoutBlock) event.getBlock();
		
		SpoutBlock variant_block = b;
		
		for (int i = 0; i < 4; i++) {
			if (side_block(b, i).getCustomBlock() == Transmitter_Block_off) {
				variant_block = side_block(b, i);
				break;
			}
		}
		// A check to see if we are working with the custom blocks, so that we do not repeat to many for loops.
		if (variant_block.getCustomBlock() == Transmitter_Block_on | variant_block.getCustomBlock() == Transmitter_Block_off) {
			if (variant_block.getCustomBlock() == Transmitter_Block_off) {
				if (variant_block.isBlockPowered() 
						|| variant_block.isBlockFaceIndirectlyPowered(BlockFace.NORTH)
						|| variant_block.isBlockFaceIndirectlyPowered(BlockFace.SOUTH)
						|| variant_block.isBlockFaceIndirectlyPowered(BlockFace.EAST)
						|| variant_block.isBlockFaceIndirectlyPowered(BlockFace.WEST) 
						|| variant_block.isBlockFaceIndirectlyPowered(BlockFace.DOWN)) {
					PowerChange(variant_block, true);
				}
			}
			else {
				
				for (int i = 0; i < 4; i++) {
					if (side_block(b, i).getCustomBlock() == Transmitter_Block_on) {
						variant_block = side_block(b, i);
						break;
					}
				}
				
				if (variant_block.getCustomBlock() == Transmitter_Block_on) {
					if (!variant_block.isBlockPowered() 
						|| !variant_block.isBlockFaceIndirectlyPowered(BlockFace.NORTH) 
						|| !variant_block.isBlockFaceIndirectlyPowered(BlockFace.SOUTH) 
						|| !variant_block.isBlockFaceIndirectlyPowered(BlockFace.WEST) 
						|| !variant_block.isBlockFaceIndirectlyPowered(BlockFace.EAST)
						|| !variant_block.isBlockFaceIndirectlyPowered(BlockFace.DOWN)){
						PowerChange(variant_block, false);
					}
				}
			}
		}
	}
		
	
	public SpoutBlock side_block(SpoutBlock block,int side) {
		
		World w = block.getWorld();
		
	    // Z+
		SpoutBlock block_right = (SpoutBlock) w.getBlockAt(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ() +1);
	    // Z-
		SpoutBlock block_left = (SpoutBlock) w.getBlockAt(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ() -1);
	    // X+
	    SpoutBlock block_front = (SpoutBlock) w.getBlockAt(block.getLocation().getBlockX() +1, block.getLocation().getBlockY(), block.getLocation().getBlockZ());
	    // X-
	    SpoutBlock block_back = (SpoutBlock) w.getBlockAt(block.getLocation().getBlockX() -1, block.getLocation().getBlockY(), block.getLocation().getBlockZ());
	    
		if (side == 0) {
			return block_right;
		}
		else if (side == 1) {
			return block_left;
		}
		else if (side == 2) {
			return block_front;
		}
		else if (side == 3) {
			return block_back;
		}
		return block;

	}
	
	public void PowerChange(SpoutBlock block, boolean on_off) {

		// On
		if (on_off) {
			
			// Create a new ArrayList if there isn't one for this channel
			if (!transmitter_blocks_on_channel.containsKey(block_channel.get(block))) {
				System.out.println("The channel doesn't exsist creating!");
				transmitter_blocks_on_channel.put(block_channel.get(block), new ArrayList<SpoutBlock>());
			}
			
			transmitter_blocks_on_channel.get(block_channel.get(block)).add(block);
			
			//System.out.println("There are: " + transmitter_blocks_on_channel.get(block_channel.get(block)).size() + " on channel: " + block_channel.get(block));
			
			//System.out.println("The Transmitter block is powered!!!!!");
			SpoutManager.getMaterialManager().overrideBlock(block , Transmitter_Block_on);
			
			ReceiverUpdate(block);
		}
		// Off
		else {
			
			// Create a new ArrayList if there isn't one for this channel
			if (!transmitter_blocks_on_channel.containsKey(block_channel.get(block))) {
				transmitter_blocks_on_channel.put(block_channel.get(block), new ArrayList<SpoutBlock>());
			}
			
			transmitter_blocks_on_channel.get(block_channel.get(block)).remove(block);		
			
			SpoutManager.getMaterialManager().overrideBlock(block, Transmitter_Block_off);
			
			ReceiverUpdate(block);
		}
		
	}
	public void ReceiverUpdate(SpoutBlock block) {
		//System.out.println("Receiver update!!!");
		for (int i = 0; i < spout_Reciever_block.size(); i++) {
			// If the receiver block is on the same channel as the transmitter
			if (block_channel.get(spout_Reciever_block.get(i)) == block_channel.get(block)) {
				if (transmitter_blocks_on_channel.containsKey(block_channel.get(block))) {
					if (transmitter_blocks_on_channel.get(block_channel.get(block)).size() == 0) {
						SpoutManager.getMaterialManager().overrideBlock(spout_Reciever_block.get(i), Reciever_Block_off);
						
						block.setBlockPowered(false);
						
						// Disable power on all four faces
						for (int a = 0; a < 4; a++) {	
							SpoutBlock adjacent_block = side_block(spout_Reciever_block.get(i), a);
							adjacent_block.setBlockPowered(false);
						}
					}
					else {
						SpoutManager.getMaterialManager().overrideBlock(spout_Reciever_block.get(i), Reciever_Block_on);
						
						block.setBlockPowered(true);
						
						// Enable power on all four faces
						for (int a = 0; a < 4; a++) {
							SpoutBlock adjacent_block = side_block(spout_Reciever_block.get(i), a);
							adjacent_block.setBlockPowered(true);
						}
					}
				}
			}
		}
	}
}
