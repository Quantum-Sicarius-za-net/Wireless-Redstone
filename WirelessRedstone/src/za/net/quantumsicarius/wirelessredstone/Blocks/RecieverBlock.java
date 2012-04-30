package za.net.quantumsicarius.wirelessredstone.Blocks;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.material.block.GenericCubeCustomBlock;

public class RecieverBlock  extends GenericCubeCustomBlock{
	public RecieverBlock(Plugin plugin, Texture texture, String s) {
		super(plugin, s, true, new GenericCubeBlockDesign(plugin, texture, 0));
	}
}
