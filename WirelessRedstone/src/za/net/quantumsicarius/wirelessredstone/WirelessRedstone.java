package za.net.quantumsicarius.wirelessredstone;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
	ArrayList<SpoutBlock> spout_Transmitter_block_on = new ArrayList<SpoutBlock>();
	
	HashMap<SpoutBlock, Integer> block_channel = new HashMap<SpoutBlock, Integer>();
	HashMap<SpoutPlayer, GUI> player_GUI = new HashMap<SpoutPlayer, GUI>();
	HashMap<SpoutPlayer, SpoutBlock> player_block_clicked = new HashMap<SpoutPlayer, SpoutBlock>();
	
	// Channel and list
	HashMap<Integer, ArrayList<SpoutBlock>> tansmitter_blocks_on_channel = new HashMap<Integer, ArrayList<SpoutBlock>>();
	HashMap<Integer, ArrayList<SpoutBlock>> reciever_blocks_on_channel = new HashMap<Integer, ArrayList<SpoutBlock>>();
	
	GUI gui;
	
	@Override
	public void onDisable() {
		System.out.println("Disabled!");
		
		//this.getConfig().set("Reciever_Block", spout_Reciever_block);
		//this.saveConfig();
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
				System.out.println("Add button pressed");
				
				int block_channel_increment = block_channel.get(player_block_clicked.get(event.getPlayer())) + 1;
				block_channel.put((SpoutBlock) player_block_clicked.get(event.getPlayer()), block_channel_increment);
				
				System.out.println("Channel: " + block_channel.get((SpoutBlock) player_block_clicked.get(event.getPlayer())));
				player_GUI.get(event.getPlayer()).updateGUI(event.getPlayer(), block_channel.get((SpoutBlock) player_block_clicked.get(event.getPlayer())));
			}
			else if (player_GUI.get(event.getPlayer()).isSubtractButton(event.getButton())) {
				System.out.println("Subtract button pressed");
				
				int block_channel_decrement = block_channel.get(player_block_clicked.get(event.getPlayer())) - 1;
				block_channel.put((SpoutBlock) player_block_clicked.get(event.getPlayer()), block_channel_decrement);
				
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
		
		System.out.println("Block Place event!");
		SpoutBlock block = (SpoutBlock) event.getBlock();
		
		if (block.getCustomBlock() == Reciever_Block_off) {
			spout_Reciever_block.add(block);
			block_channel.put(block, 0);
		}
		
		else if (block.getCustomBlock() == Transmitter_Block_off) {
			block_channel.put(block, 0);
		}
	}
	
	@EventHandler
	public void BlockBreak(BlockBreakEvent event) {
		
		SpoutBlock block = (SpoutBlock) event.getBlock();
		
		if (block.getCustomBlock() == Reciever_Block_off | block.getCustomBlock() == Reciever_Block_on) {
			spout_Reciever_block.remove(block);
		}
		else if (block.getCustomBlock() == Transmitter_Block_off | block.getCustomBlock() == Transmitter_Block_on) {
			spout_Transmitter_block_on.remove(block);
			// Add the ArrayList to the HashMap (List of all transmitters on a certain channel)
			tansmitter_blocks_on_channel.put(block_channel.get(block), spout_Transmitter_block_on);
		}
	}
	
	@EventHandler
	public void redStoneEvent(BlockRedstoneEvent event) {
		
		SpoutBlock b = (SpoutBlock) event.getBlock();
		
		//System.out.println("The block is: " + b.getName());
			
		SpoutBlock variant_block = side_block(b, Transmitter_Block_off, null);
		
		if (variant_block.getCustomBlock() == Transmitter_Block_off) {
			if (!variant_block.isBlockPowered() || !variant_block.isBlockIndirectlyPowered()) {
				
			//System.out.println("The Transmitter block is powered!!!!!");
			SpoutManager.getMaterialManager().overrideBlock(variant_block , Transmitter_Block_on);
					
				spout_Transmitter_block_on.add(variant_block);
				// Add the ArrayList to the HashMap (List of all transmitters on a certain channel)
				tansmitter_blocks_on_channel.put(block_channel.get(b), spout_Transmitter_block_on);
					
				for (int i = 0; i < spout_Reciever_block.size(); i++) {
					if (block_channel.get(spout_Reciever_block.get(i)) == block_channel.get(variant_block)) {
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
		}
		else {
			variant_block = side_block(b, Transmitter_Block_on, null);
			
			if (variant_block.getCustomBlock() == Transmitter_Block_on) {
				if (variant_block.isBlockPowered() || variant_block.isBlockIndirectlyPowered()){
				//System.out.println("The block is un-powered!!!!!");
					
					SpoutManager.getMaterialManager().overrideBlock(variant_block, Transmitter_Block_off);
						
					spout_Transmitter_block_on.remove(variant_block);
					// Add the ArrayList to the HashMap (List of all transmitters on a certain channel)
					tansmitter_blocks_on_channel.put(block_channel.get(variant_block), spout_Transmitter_block_on);
					
					//System.out.println("Reciever Blocks in world: " + spout_Reciever_block.size());
					//System.out.println("Transmitter Blocks in world: " + spout_Transmitter_block_on.size());
						
					for (int i = 0; i < spout_Reciever_block.size(); i++) {
						// If the reciever block is on the same channel as the transmitter
						if (block_channel.get(spout_Reciever_block.get(i)) == block_channel.get(variant_block)) {
							if (tansmitter_blocks_on_channel.get(block_channel.get(variant_block)).size() == 0) {
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
				System.out.println("Block Right is the transmitter");
				return block_right;
			}
			else if (block_left.getCustomBlock() == custom_block) {
				System.out.println("Block left is the transmitter");
				return block_left;
			}
			else if (block_front.getCustomBlock() == custom_block) {
				System.out.println("Block front is the transmitter");
				return block_front;
			}
			else if (block_back.getCustomBlock() == custom_block) {
				System.out.println("Block back is the transmitter");
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
