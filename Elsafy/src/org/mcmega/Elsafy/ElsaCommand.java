package org.mcmega.Elsafy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mcmega.Elsafy.Objects.Elsa;

public class ElsaCommand implements CommandExecutor {
	
	Elsafy plugin = Elsafy.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		
		if (!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Hahaha, consoles can't be Elsa!");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!player.hasPermission("elsafy.becomeelsa")){
			player.sendMessage(ChatColor.RED + "Just let it go, you can never be Elsa.");
			return true;
		}
		
		if (args.length > 0){
			if (args[0].equalsIgnoreCase("bridge")){
				if (plugin.isElsa(player.getName())){
					Elsa elsa = plugin.getElsaObject(player.getName());
					if (elsa.isBridgeActive){
						player.sendMessage(ChatColor.BLUE + "Ice Bridge Disabled!");
						elsa.disableIceBridge();
						return true;
					}else{
						player.sendMessage(ChatColor.BLUE + "Ice Bridge Activated!");
						elsa.enableIceBridge();
						return true;
					}
				}
			}else{
				player.sendMessage(ChatColor.RED + "Use /elsafy first!");
				return true;
			}
		}
		
		if (plugin.isElsa(player.getName())){
			plugin.removeElsa(player.getName());
			player.setAllowFlight(false);
			player.sendMessage(ChatColor.BLUE + "You have been thawed, Elsa's magic is no longer swirling through you!");
			return true;
		}else{
			plugin.addElsa(player.getName());
			player.setAllowFlight(true);
			player.sendMessage(ChatColor.DARK_AQUA + "By the magic of the Trolls, you have now become Elsa! Beware the Frozen Heart!");
			return true;
		}
	}

}
