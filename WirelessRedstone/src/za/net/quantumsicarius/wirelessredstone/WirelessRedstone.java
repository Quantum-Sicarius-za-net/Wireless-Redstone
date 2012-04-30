package za.net.quantumsicarius.wirelessredstone;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.MaterialData;

import za.net.quantumsicarius.wirelessredstone.Blocks.RecieverBlock;
import za.net.quantumsicarius.wirelessredstone.Blocks.TransmitterBlock;

public class WirelessRedstone extends JavaPlugin implements Listener {
	
	private static Texture Transmitter_Texture_on;
	private static CustomBlock Transmitter_Block_on;
	
	private static Texture Transmitter_Texture_off;
	private static CustomBlock Transmitter_Block_off;
	
	private static Texture Reciever_Texture_on;
	private static CustomBlock Reciever_Block_on;
	
	private static Texture Reciever_Texture_off;
	private static CustomBlock Reciever_Block_off;
	
	ArrayList<SpoutBlock> spout_Reciever_block = new ArrayList<SpoutBlock>();
	ArrayList<SpoutBlock> spout_Transmitter_block = new ArrayList<SpoutBlock>();
	
	@Override
	public void onDisable() {
		System.out.println("Disabled!");
		
		this.getConfig().set("Reciever_Block", spout_Reciever_block);
		this.saveConfig();
	}

	@Override
	public void onEnable() {
		System.out.println("Enabled!");
		
		loadItems();
		loadRecipes();
		
		// Register event listeners
		getServer().getPluginManager().registerEvents(this, this);
		
		//spout_Reciever_block = (ArrayList<SpoutBlock>) this.getConfig().get("Reciever_Block");
		
		//if (spout_Reciever_block == null) {
		//	spout_Reciever_block = new ArrayList<SpoutBlock>();
		//}
	}
	
	private void loadItems() {
    	Transmitter_Texture_on = new Texture(this, "http://s13.postimage.org/u2noy7pab/tx_On.png", 32, 32, 32);
    	Transmitter_Block_on = new TransmitterBlock(this, Transmitter_Texture_on, "Wireless Transmitter On");
    	
    	Transmitter_Texture_off = new Texture(this, "http://s17.postimage.org/xp94lmdln/tx_Off.png", 32, 32, 32);
    	Transmitter_Block_off = new TransmitterBlock(this, Transmitter_Texture_off, "Wireless Transmitter Off");
    	
       	Reciever_Texture_on = new Texture(this, "http://s7.postimage.org/owy3ah9vb/rx_On.png", 32, 32, 32);
    	Reciever_Block_on = new RecieverBlock(this, Reciever_Texture_on, "Wireless Reciever On");
    	
       	Reciever_Texture_off = new Texture(this, "http://s16.postimage.org/q6fezbnht/rx_Off.png", 32, 32, 32);
    	Reciever_Block_off = new RecieverBlock(this, Reciever_Texture_off, "Wireless Reciever off");
	}
	
	private void loadRecipes() {
		ItemStack result = new SpoutItemStack(Transmitter_Block_on);
		SpoutShapedRecipe recipe = new SpoutShapedRecipe(result);
		recipe.shape("ABA", "BCB", "ABA");
		recipe.setIngredient('A', MaterialData.ironIngot);
		recipe.setIngredient('B', MaterialData.redstone);
		recipe.setIngredient('C', MaterialData.redstoneTorchOn);
		SpoutManager.getMaterialManager().registerSpoutRecipe(recipe);
		
		ItemStack result2 = new SpoutItemStack(Reciever_Block_on);
		SpoutShapedRecipe recipe2 = new SpoutShapedRecipe(result2);
		recipe2.shape("ABA", "BCB", "ABA");
		recipe2.setIngredient('A', MaterialData.ironIngot);
		recipe2.setIngredient('B', MaterialData.redstone);
		recipe2.setIngredient('C', MaterialData.lever);
		SpoutManager.getMaterialManager().registerSpoutRecipe(recipe2);
	}
	
	@EventHandler
	public void BlockPlace(BlockPlaceEvent event) {
		
		SpoutBlock block = (SpoutBlock) event.getBlock();
		
		if (block.getCustomBlock() == Reciever_Block_off) {
			spout_Reciever_block.add(block);
		}
	}
	
	@EventHandler
	public void BlockBreak(BlockBreakEvent event) {
		
		SpoutBlock block = (SpoutBlock) event.getBlock();
		
		if (block.getCustomBlock() == Reciever_Block_off | block.getCustomBlock() == Reciever_Block_on) {
			spout_Reciever_block.remove(block);
		}
		else if (block.getCustomBlock() == Transmitter_Block_off | block.getCustomBlock() == Transmitter_Block_on) {
			spout_Transmitter_block.remove(block);
		}
	}
	
	@EventHandler
	public void redStoneEvent(BlockRedstoneEvent event) {
		
		SpoutBlock b = (SpoutBlock) event.getBlock();
		
		if (b.isBlockPowered()) {
			
			SpoutBlock variant_block = side_block(b, Transmitter_Block_off, null);
			
			if (variant_block.getCustomBlock() == Transmitter_Block_off) {
				System.out.println("The Transmitter block is powered!!!!!");
				SpoutManager.getMaterialManager().overrideBlock(variant_block , Transmitter_Block_on);
				
				spout_Transmitter_block.add(variant_block);
				
				for (int i = 0; i < spout_Reciever_block.size(); i++) {
					SpoutManager.getMaterialManager().overrideBlock(spout_Reciever_block.get(i), Reciever_Block_on);
					spout_Reciever_block.get(i).setBlockPowered(true);
					
					for (int a = 0; a < 4; a++) {
						SpoutBlock red_stone_wire = side_block(spout_Reciever_block.get(i), null, Material.REDSTONE_WIRE);
						
						if (red_stone_wire.getType() == Material.REDSTONE_WIRE) {
							red_stone_wire.setType(Material.REDSTONE_TORCH_ON);
						}
					}
				}
			}
		}
		else {
			System.out.println("The block is un-powered!!!!!");
			
			SpoutBlock variant_block = side_block(b, Transmitter_Block_on, null);
			
			if (variant_block.getCustomBlock() == Transmitter_Block_on) {
				SpoutManager.getMaterialManager().overrideBlock(variant_block , Transmitter_Block_off);
				
				spout_Transmitter_block.remove(variant_block);
				
				for (int i = 0; i < spout_Reciever_block.size(); i++) {
				
					if (spout_Transmitter_block.size() == 0) {
						
						SpoutManager.getMaterialManager().overrideBlock(spout_Reciever_block.get(i), Reciever_Block_off);
						spout_Reciever_block.get(i).setBlockPowered(false);
						
						for (int a = 0; a < 4; a++) {
							
							SpoutBlock red_stone_wire = side_block(spout_Reciever_block.get(i), null, Material.REDSTONE_TORCH_ON);
							
							if (red_stone_wire.getType() == Material.REDSTONE_TORCH_ON) {
								red_stone_wire.setType(Material.REDSTONE_WIRE);
							}
						}
					}
				}
			}
		}
	}
	
	public SpoutBlock side_block(SpoutBlock block, CustomBlock custom_block, Material normal_block) {
		
		World w = block.getWorld();
		
	    // Z+
		SpoutBlock block_right = (SpoutBlock) w.getBlockAt(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ() +1);
	    // Z-
		SpoutBlock block_left = (SpoutBlock) w.getBlockAt(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ() -1);
	    // X+
	    SpoutBlock block_front = (SpoutBlock) w.getBlockAt(block.getLocation().getBlockX() +1, block.getLocation().getBlockY(), block.getLocation().getBlockZ());
	    // X-
	    SpoutBlock block_back = (SpoutBlock) w.getBlockAt(block.getLocation().getBlockX() -1, block.getLocation().getBlockY(), block.getLocation().getBlockZ());
		
	    if (normal_block == null) {
			if (block_right.getCustomBlock() == custom_block) {
				return block_right;
			}
			else if (block_left.getCustomBlock() == custom_block) {
				return block_left;
			}
			else if (block_front == custom_block) {
				return block_front;
			}
			else if (block_back == custom_block) {
				return block_back;
			}
			else {
				return block;
			}
	    }
	    else {
			if (block_right.getType() == normal_block) {
				return block_right;
			}
			else if (block_left.getType() == normal_block) {
				return block_left;
			}
			else if (block_front.getType() == normal_block) {
				return block_front;
			}
			else if (block_back.getType() == normal_block) {
				return block_back;
			}
			else {
				return block;
			}
	    }

	}

}
