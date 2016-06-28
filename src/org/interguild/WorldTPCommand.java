package org.interguild;

import java.io.*;
import java.util.List;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class WorldTPCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			// process command arguments
			if (args.length != 1)
				return false;
			WorldType to = getCommand(args[0]);
			if (to == WorldType.INVALID)
				return false; // show usage message
			WorldType from = WorldTP.getWorldType(player.getWorld());
			if (from == WorldType.INVALID)
				return true; // don't show usage
			else if (from == to) {
				if (from == WorldType.SURVIVAL)
					sender.sendMessage("You are already in Survival.");
				else
					sender.sendMessage("You are already in Creative.");
				return true;
			}

			// process teleportation
			player.teleport(getNewPosition(player, to));
		}
		return true;
	}

	private WorldType getCommand(String arg) {
		arg = arg.toLowerCase();
		if (arg.equals("creative") || arg.equals("c")) {
			return WorldType.CREATIVE;
		} else if (arg.equals("survival") || arg.equals("s")) {
			return WorldType.SURVIVAL;
		} else {
			return WorldType.INVALID;
		}
	}

	private Location getNewPosition(Player player, WorldType to) {
		File f = new File(WorldTP.getMe().getDataFolder().getAbsolutePath() + File.separator + WorldTP.getFolderName(to) + File.separator + player.getUniqueId());
		if (f.exists()) {
			double[] data;
			try {
				ObjectInput in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
				data = (double[]) in.readObject();
				in.close();
			} catch (IOException e) {
				WorldTP.getMe().getLogger().severe(e.getMessage());
				return getWorldSpawn(to);
			} catch (ClassNotFoundException e) {
				WorldTP.getMe().getLogger().severe(e.getMessage());
				return getWorldSpawn(to);
			}
			int index = (int) data[6];
			List<World> wl = WorldTP.getWorldList(to);
			if (index < 0 || index >= wl.size()) {
				WorldTP.getMe().getLogger().severe("Invalid " + WorldTP.getFolderName(to) + " world position saved for player '" + player.getName() + "'. Did a world get removed from config.yml?");
				index = 0;
			}
			Location loc = player.getLocation().clone();
			loc.setWorld(wl.get(index));
			loc.setX(data[0]);
			loc.setY(data[1]);
			loc.setZ(data[2]);
			loc.setDirection(new Vector(data[3], data[4], data[5]));
			return loc;
		} else {
			return getWorldSpawn(to);
		}
	}

	private Location getWorldSpawn(WorldType to) {
		return WorldTP.getWorldList(to).get(0).getSpawnLocation();
	}
}
