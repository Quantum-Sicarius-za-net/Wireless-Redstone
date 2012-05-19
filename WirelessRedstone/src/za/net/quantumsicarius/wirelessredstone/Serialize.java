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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.block.SpoutBlock;

public class Serialize {
	
	int counter = 1;
	YamlConfiguration yml = new YamlConfiguration();
	File fileblock_channel = new File("plugins" + File.separator + "WirelessRedstone" + File.separator + "block_channel.yml");
	File file_recievers_list = new File("plugins" + File.separator + "WirelessRedstone" + File.separator + "receivers.yml");
	
	public void SerializeBlock(SpoutBlock block, int channel) {
		String world = block.getWorld().getName();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		
		HashMap<String, Object> serialize = new HashMap<String, Object>();
		
		serialize.put("world", world);
		serialize.put("channel", channel);
		serialize.put("x", x);
		serialize.put("y", y);
		serialize.put("z", z);

		yml.set("Amount", counter);
		yml.createSection(Integer.toString(counter -1), serialize);
		
		try {
			yml.save(fileblock_channel);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		counter++;
	}
	
	int channel;
	
	public HashMap<SpoutBlock, Integer> deserialize() {
		HashMap<SpoutBlock, Integer> deserialized = new HashMap<SpoutBlock, Integer>();
		
		// Load the YML file
		try {
			yml.load(fileblock_channel);
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			//e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		System.out.println("YML says amount = " + yml.getInt("Amount"));
		
		for (int i = 0; i < yml.getInt("Amount"); i++) {
			deserialized.put(deserializeBlock(i), channel);
		}
		
		System.out.println("Deserialized size: " + deserialized.size());
		fileblock_channel.delete();
		return deserialized;
	}
	
	public SpoutBlock deserializeBlock(int counter) {

		String world = yml.getString(Integer.toString(counter) + ".world");;
		channel = yml.getInt(Integer.toString(counter) + ".channel");;
		int x = yml.getInt(Integer.toString(counter) + ".x");;
		int y = yml.getInt(Integer.toString(counter) + ".y");;
		int z = yml.getInt(Integer.toString(counter) + ".z");;
		
		System.out.println("Found block at: X: " + x + " Y: " + y+ " Z: " + z +" In world: " + world);
		
		return (SpoutBlock) Bukkit.getWorld(world).getBlockAt(x, y, z);
	}
	// Saves Reciever List (SERIALIZE)
	public void SaveBlockList(ArrayList<SpoutBlock> list) {
		
		int counter = 1;
		YamlConfiguration yml = new YamlConfiguration();
		
		for (int i = 0; i < list.size(); i++) {
			
			String world = list.get(i).getWorld().getName();
			int x = list.get(i).getX();
			int y = list.get(i).getY();
			int z = list.get(i).getZ();
			
			HashMap<String, Object> serialize = new HashMap<String, Object>();
			
			serialize.put("world", world);
			serialize.put("x", x);
			serialize.put("y", y);
			serialize.put("z", z);
			
			yml.set("Amount", counter);
			yml.createSection(Integer.toString(i), serialize);
			
			counter ++;
		}
		
		try {
			yml.save(file_recievers_list);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Returns a List of all the receiver blocks (DESERIALIZE)
	public ArrayList<SpoutBlock> deserializeList () {
		
		YamlConfiguration yml = new YamlConfiguration();
		
		try {
			yml.load(file_recievers_list);
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		ArrayList<SpoutBlock> deserializedblocks = new ArrayList<SpoutBlock>();
		
		System.out.println("YML says amount = " + yml.getInt("Amount"));
		
		for (int i = 0; i < yml.getInt("Amount"); i++) {
			String world = yml.getString(Integer.toString(i) + ".world");
			int x = yml.getInt(Integer.toString(i) + ".x");
			int y = yml.getInt(Integer.toString(i) + ".y");
			int z = yml.getInt(Integer.toString(i) + ".z");
			
			System.out.println("Found block at: X: " + x + " Y: " + y+ " Z: " + z +" In world: " + world);
			SpoutBlock block = (SpoutBlock) Bukkit.getWorld(world).getBlockAt(x, y, z);
			
			deserializedblocks.add(block);
		}
		
		file_recievers_list.delete();
		return deserializedblocks;
	}
}
