/*
 * Copyright (c) IntellectualCrafters - 2014.
 * You are not allowed to distribute and/or monetize any of our intellectual property.
 * IntellectualCrafters is not affiliated with Mojang AB. Minecraft is a trademark of Mojang AB.
 *
 * >> File = Delete.java
 * >> Generated by: Citymonstret at 2014-08-09 01:41
 */

package com.intellectualcrafters.plot.commands;

import org.bukkit.entity.Player;

import com.intellectualcrafters.plot.C;
import com.intellectualcrafters.plot.PlayerFunctions;
import com.intellectualcrafters.plot.Plot;
import com.intellectualcrafters.plot.PlotHelper;
import com.intellectualcrafters.plot.PlotMain;
import com.intellectualcrafters.plot.database.DBFunc;

/**
 * Created by Citymonstret on 2014-08-01.
 */
public class Delete extends SubCommand {

    public Delete() {
        super(Command.DELETE, "Delete a plot", "delete", CommandCategory.ACTIONS);
    }

    @Override
    public boolean execute(Player plr, String... args) {
        if (!PlayerFunctions.isInPlot(plr)) {
            PlayerFunctions.sendMessage(plr, "You're not in a plot.");
            return false;
        }
        Plot plot = PlayerFunctions.getCurrentPlot(plr);
        if (!PlayerFunctions.getTopPlot(plr.getWorld(), plot).equals(PlayerFunctions.getBottomPlot(plr.getWorld(), plot))) {
            PlayerFunctions.sendMessage(plr, C.UNLINK_REQUIRED);
            return false;
        }
        if ((plot==null || !plot.hasOwner() || !plot.getOwner().equals(plr.getUniqueId())) && !plr.hasPermission("plots.admin")) {
            PlayerFunctions.sendMessage(plr, C.NO_PLOT_PERMS);
            return false;
        }
        boolean result = PlotMain.removePlot(plr.getWorld().getName(), plot.id, true);
        if (result) {
            PlotHelper.removeSign(plr, plot);
            plot.clear(plr);
            DBFunc.delete(plr.getWorld().getName(), plot);
        } else {
            PlayerFunctions.sendMessage(plr, "Plot clearing has been denied.");
        }
        return true;
    }
}