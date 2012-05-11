package za.net.quantumsicarius.wirelessredstone.gui;

import org.bukkit.plugin.Plugin;
import org.getspout.commons.ChatColor;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.player.SpoutPlayer;

public class GUI {
	
	GenericPopup popup;
	GenericContainer box;
	GenericLabel title;
	GenericLabel frequancy;
	GenericButton add;
	GenericButton subtract;
	
	Plugin plugin;
	String title_head;

	public GUI(String title_string, int frequancy_string, Plugin wirelessredstone_plugin, SpoutPlayer player) {
		plugin = wirelessredstone_plugin;
		
		title_head = title_string;
		
		title = new GenericLabel(ChatColor.GREEN + title_string);
		frequancy = new GenericLabel(ChatColor.AQUA + "Channel: " + ChatColor.WHITE + Integer.toString(frequancy_string));
		
		add = new GenericButton("Channel Up");
		subtract = new GenericButton("Channel Down");
		
		box = new GenericContainer();
		box.addChildren(title, frequancy, add, subtract);
		box.setX(100);
		box.setY(80);
		box.setHeight(100);
		box.setWidth(150);
		
		popup = new GenericPopup();
		popup.attachWidget(plugin, box);
		
		player.getMainScreen().attachPopupScreen(popup);
	}
	
	public void updateGUI(SpoutPlayer player, int frequancy_num) {
		frequancy.setText(ChatColor.AQUA + "Channel: " + ChatColor.WHITE + Integer.toString(frequancy_num));
		player.getMainScreen().getActivePopup().updateWidget(frequancy);
	}
	
	public String title() {
		return title_head;
	}
	
	public boolean isAddButton(Button button) {
		if (button.getText().equals("Channel Up") && button.getPlugin() == plugin) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isSubtractButton(Button button) {
		if (button.getText().equals("Channel Down") && button.getPlugin() == plugin) {
			return true;
		}
		else {
			return false;
		}
	}
	
}
