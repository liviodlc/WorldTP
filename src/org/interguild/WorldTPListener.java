package org.interguild;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class WorldTPListener implements Listener {

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent evt){
		WorldType from, to;
		from = WorldTP.getWorldType(evt.getFrom().getWorld());
		to = WorldTP.getWorldType(evt.getTo().getWorld());
		if(from != to)
			savePrevPosition(evt.getPlayer(), from);
	}
	
	private void savePrevPosition(Player player, WorldType from) {
		Location loc = player.getLocation();
		Vector dir = loc.getDirection();
		double[] toWrite = new double[7];
		toWrite[0] = loc.getX();
		toWrite[1] = loc.getY();
		toWrite[2] = loc.getZ();
		toWrite[3] = dir.getX();
		toWrite[4] = dir.getY();
		toWrite[5] = dir.getZ();
		toWrite[6] = WorldTP.getWorldList(from).indexOf(player.getWorld());

		try {
			ObjectOutput o = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(WorldTP.getMe().getDataFolder().getAbsolutePath() + File.separator + WorldTP.getFolderName(from) + File.separator + player.getUniqueId())));
			o.writeObject(toWrite);
			o.flush();
			o.close();
		} catch (IOException e) {
			WorldTP.getMe().getLogger().severe(e.getMessage());
		}
	}
}
