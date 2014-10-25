/*
 * Copyright (c) IntellectualCrafters - 2014. You are not allowed to distribute
 * and/or monetize any of our intellectual property. IntellectualCrafters is not
 * affiliated with Mojang AB. Minecraft is a trademark of Mojang AB.
 * 
 * >> File = Info.java >> Generated by: Citymonstret at 2014-08-09 01:41
 */

package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.plot.*;
import com.intellectualcrafters.plot.database.DBFunc;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Citymonstret
 */
public class Info extends SubCommand {

	public Info() {
		super(Command.INFO, "Display plot info", "info", CommandCategory.INFO, false);
	}

	@Override
	public boolean execute(Player player, String... args) {
		World world;
		Plot plot;
		if (player!=null) {
		    world = player.getWorld();
			if (!PlayerFunctions.isInPlot(player)) {
				PlayerFunctions.sendMessage(player, C.NOT_IN_PLOT);
				return false;
			}
			plot = PlayerFunctions.getCurrentPlot(player);
		}
		else {
			if (args.length<2) {
				PlayerFunctions.sendMessage(player, C.INFO_SYNTAX_CONSOLE);
				return false;
			}
			PlotWorld plotworld = PlotMain.getWorldSettings(args[0]);
			if (plotworld==null) {
				PlayerFunctions.sendMessage(player, C.NOT_VALID_WORLD);
				return false;
			}
			try {
				String[] split = args[1].split(";");
				PlotId id = new PlotId(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
				plot = PlotHelper.getPlot(Bukkit.getWorld(plotworld.worldname), id);
				if (plot==null) {
					PlayerFunctions.sendMessage(player, C.NOT_VALID_PLOT_ID);
					return false;
				}
				world = Bukkit.getWorld(args[0]);
				if (args.length==3) {
				    args = new String[] {args[2]};
				}
				else {
				    args = new String[0];
				}
			}
			catch (Exception e) {
				PlayerFunctions.sendMessage(player, C.INFO_SYNTAX_CONSOLE);
				return false;
			}
		}

		boolean hasOwner = plot.hasOwner();
		boolean containsEveryone;
		boolean trustedEveryone;

		// Wildcard player {added}
		{
			if (plot.helpers == null) {
				containsEveryone = false;
			}
			else {
				containsEveryone = plot.helpers.contains(DBFunc.everyone);
			}
			if (plot.trusted == null) {
				trustedEveryone = false;
			}
			else {
				trustedEveryone = plot.trusted.contains(DBFunc.everyone);
			}
		}

		// Unclaimed?
		if (!hasOwner && !containsEveryone && !trustedEveryone) {
			PlayerFunctions.sendMessage(player, C.PLOT_INFO_UNCLAIMED, (plot.id.x + ";" + plot.id.y));
			return true;
		}

		String owner = "none";
		if (plot.owner != null) {
			owner = Bukkit.getOfflinePlayer(plot.owner).getName();
		}
		if (owner == null) {
			owner = plot.owner.toString();
		}
		String info = C.PLOT_INFO.s();
		
		if (args.length==1) {
		    info = getCaption(args[0].toLowerCase());
		    if (info==null) {
		        PlayerFunctions.sendMessage(player, "&6Categories&7: &ahelpers&7, &aalias&7, &abiome&7, &adenied&7, &aflags&7, &aid&7, &asize&7, &atrusted&7, &aowner&7, &arating");
		        return false;
		    }
		}
		
		info = format(info, world, plot, player);

		PlayerFunctions.sendMessage(player, info);
		return true;
	}
	
	private String getCaption(String string) {
        switch (string) {
        case "helpers":
            return C.PLOT_INFO_HELPERS.s();
        case "alias":
            return C.PLOT_INFO_ALIAS.s();
        case "biome":
            return C.PLOT_INFO_BIOME.s();
        case "denied":
            return C.PLOT_INFO_DENIED.s();
        case "flags":
            return C.PLOT_INFO_FLAGS.s();
        case "id":
            return C.PLOT_INFO_ID.s();
        case "size":
            return C.PLOT_INFO_SIZE.s();
        case "trusted":
            return C.PLOT_INFO_TRUSTED.s();
        case "owner":
            return C.PLOT_INFO_OWNER.s();
        case "rating":
            return C.PLOT_INFO_RATING.s();
        default:
            return null;
        }
	}
	
	private String format(String info, World world, Plot plot, Player player) {
	    
	    PlotId id = plot.id;
        PlotId id2 = PlayerFunctions.getTopPlot(world, plot).id;
        int num = PlayerFunctions.getPlotSelectionIds(world, id, id2).size();
        String alias = plot.settings.getAlias().length() > 0 ? plot.settings.getAlias() : "none";
        String biome = getBiomeAt(plot).toString();
        String helpers = getPlayerList(plot.helpers);
        String trusted = getPlayerList(plot.trusted);
        String denied = getPlayerList(plot.denied);
        String rating = String.format("%.1f", DBFunc.getRatings(plot));
        String flags = "&3"+ (StringUtils.join(plot.settings.getFlags(), "").length() > 0 ? StringUtils.join(plot.settings.getFlags(), "&7, &3") : "none");
        boolean build = player==null ? true : plot.hasRights(player);
        
        String owner = "none";
        if (plot.owner != null) {
            owner = Bukkit.getOfflinePlayer(plot.owner).getName();
        }
        if (owner == null) {
            owner = plot.owner.toString();
        }
        
        info = info.replaceAll("%alias%", alias);
        info = info.replaceAll("%id%", id.toString());
        info = info.replaceAll("%id2%", id2.toString());
        info = info.replaceAll("%num%", num+"");
        info = info.replaceAll("%biome%", biome);
        info = info.replaceAll("%owner%", owner);
        info = info.replaceAll("%helpers%", helpers);
        info = info.replaceAll("%trusted%", trusted);
        info = info.replaceAll("%denied%", denied);
        info = info.replaceAll("%rating%", rating);
        info = info.replaceAll("%flags%", flags);
        info = info.replaceAll("%build%", build+"");
        info = info.replaceAll("%desc%", "No description set.");

        
        return info;
	}

	private String getPlayerList(ArrayList<UUID> l) {
		if ((l == null) || (l.size() < 1)) {
			return " none";
		}
		String c = C.PLOT_USER_LIST.s();
		StringBuilder list = new StringBuilder();
		for (int x = 0; x < l.size(); x++) {
			if ((x + 1) == l.size()) {
				list.append(c.replace("%user%", getPlayerName(l.get(x))).replace(",", ""));
			}
			else {
				list.append(c.replace("%user%", getPlayerName(l.get(x))));
			}
		}
		return list.toString();
	}

	private String getPlayerName(UUID uuid) {
		if (uuid == null) {
			return "unknown";
		}
		if (uuid.equals(DBFunc.everyone) || uuid.toString().equalsIgnoreCase(DBFunc.everyone.toString())) {
			return "everyone";
		}
		/*
		 * OfflinePlayer plr = Bukkit.getOfflinePlayer(uuid); if (plr.getName()
		 * == null) { return "unknown"; } return plr.getName();
		 */
		return UUIDHandler.getName(uuid);
	}

	private Biome getBiomeAt(Plot plot) {
		World w = Bukkit.getWorld(plot.world);
		Location bl = PlotHelper.getPlotTopLoc(w, plot.id);
		return bl.getBlock().getBiome();
	}
}
