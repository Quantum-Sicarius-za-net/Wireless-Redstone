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

package za.net.quantumsicarius.wirelessredstone.Blocks;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.material.block.GenericCubeCustomBlock;

public class TransmitterBlock extends GenericCubeCustomBlock {
	public TransmitterBlock(Plugin plugin, Texture texture, String s) {
		super(plugin, s, true, new GenericCubeBlockDesign(plugin, texture, 0));
	}
}
