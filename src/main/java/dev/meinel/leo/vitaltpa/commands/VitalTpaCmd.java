/*
 * VitalTpa is a Spigot Plugin that gives players the ability to ask players to teleport to them.
 * Copyright © 2022 Leopold Meinel & contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see https://github.com/LeoMeinel/VitalTpa/blob/main/LICENSE
 */

package dev.meinel.leo.vitaltpa.commands;

import dev.meinel.leo.vitaltpa.utils.commands.Cmd;
import dev.meinel.leo.vitaltpa.utils.commands.CmdSpec;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VitalTpaCmd
		implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
	                         @NotNull String[] args) {
		if (Cmd.isArgsLengthNotEqualTo(sender, args, 1)) {
			return false;
		}
		doTpa(sender, args);
		return true;
	}

	public void doTpa(@NotNull CommandSender sender, @NotNull String[] args) {
		Player player = Bukkit.getPlayer(args[0]);
		if (Cmd.isInvalidSender(sender)) {
			return;
		}
		if (CmdSpec.isInvalidCmd(sender, player, "vitaltpa.tpa", false)) {
			return;
		}
		assert player != null;
		CmdSpec.addToMap(sender, player, "tpa-received", "tpa-sent", "tpa");
	}
}