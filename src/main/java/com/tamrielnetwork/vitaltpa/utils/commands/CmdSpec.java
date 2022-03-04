/*
 * VitalTpa is a Spigot Plugin that gives players the ability to ask players to teleport to them.
 * Copyright © 2022 Leopold Meinel
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
 * along with this program. If not, see https://github.com/TamrielNetwork/VitalTpa/blob/main/LICENSE
 */

package com.tamrielnetwork.vitaltpa.utils.commands;

import com.google.common.collect.ImmutableMap;
import com.tamrielnetwork.vitaltpa.VitalTpa;
import com.tamrielnetwork.vitaltpa.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CmdSpec {

	private static final HashMap<UUID, UUID> tpPlayerMap = new HashMap<>();
	private static final HashMap<HashMap<UUID, UUID>, String> tpMap = new HashMap<>();
	private static final VitalTpa main = JavaPlugin.getPlugin(VitalTpa.class);
	
	public static void doDelay(Player senderPlayer, Player player) {

		if (!player.hasPermission("vitaltpa.delay.bypass")) {
			String timeRemaining = String.valueOf(main.getConfig().getLong("delay.time"));
			Chat.sendMessage(player, ImmutableMap.of("%countdown%", timeRemaining), "countdown");
			new BukkitRunnable() {

				@Override
				public void run() {
					if (Cmd.isInvalidPlayer(player) || Cmd.isInvalidPlayer(senderPlayer)) {
						return;
					}

					doTpa(senderPlayer, player);
					doUnmap(senderPlayer);
				}
			}.runTaskLater(main, (main.getConfig().getLong("delay.time") * 20L));
		} else {
			doTpa(senderPlayer, player);
			doUnmap(senderPlayer);
		}
	}

	public static void addToMap(@NotNull CommandSender sender, @NotNull Player player, @NotNull String playerMessage, @NotNull String senderMessage, @NotNull String type) {

		Player senderPlayer = (Player) sender;

		if (tpPlayerMap.containsKey(senderPlayer.getUniqueId())) {
			Chat.sendMessage(sender, "active-tpa");
			return;
		}
		tpPlayerMap.put(senderPlayer.getUniqueId(), player.getUniqueId());
		tpMap.put(tpPlayerMap, type);
		Chat.sendMessage(player, ImmutableMap.of("%player%", sender.getName()), playerMessage);
		Chat.sendMessage(sender, ImmutableMap.of("%player%", player.getName()), senderMessage);
		doTiming(sender);
	}

	public static void doUnmap(@NotNull Player senderPlayer) {

		for (Map.Entry<UUID, UUID> uuidEntry : tpPlayerMap.entrySet()) {
			if (uuidEntry.getValue().equals(senderPlayer.getUniqueId())) {
				clearMaps(Objects.requireNonNull(Bukkit.getPlayer(uuidEntry.getKey())));
				break;
			}
		}
	}

	public static void clearMaps(@NotNull CommandSender sender) {

		Player senderPlayer = (Player) sender;
		tpMap.remove(tpPlayerMap);
		tpPlayerMap.remove(senderPlayer.getUniqueId());
	}

	private static void doTiming(@NotNull CommandSender sender) {

		new BukkitRunnable() {

			@Override
			public void run() {

				clearMaps(sender);
			}
		}.runTaskLaterAsynchronously(main, (main.getConfig().getLong("request-expiry") * 20L));
	}

	private static void doTpa(@NotNull Player senderPlayer, @NotNull Player player) {

		for (Map.Entry<HashMap<UUID, UUID>, String> tpEntry : tpMap.entrySet()) {
			if (tpEntry.getValue().equals("tpa")) {
				player.teleport(senderPlayer.getLocation());
				return;
			}
			if (tpEntry.getValue().equals("tpahere")) {
				senderPlayer.teleport(player.getLocation());
				return;
			}
			break;
		}
	}

	public static boolean isInvalidCmd(@NotNull CommandSender sender, Player player, @NotNull String perm, boolean isConfirmation) {

		Player senderPlayer = (Player) sender;

		if (!getTpPlayerMap().containsValue(senderPlayer.getUniqueId()) && isConfirmation) {
			Chat.sendMessage(senderPlayer, "no-request");
			return true;
		}

		if (Cmd.isNotPermitted(sender, perm)) {
			return true;
		}
		return Cmd.isInvalidPlayer(sender, player);
	}

	public static Player getMappedPlayer(@NotNull Player senderPlayer) {

		for (Map.Entry<UUID, UUID> uuidEntry : tpPlayerMap.entrySet()) {
			if (uuidEntry.getValue().equals(senderPlayer.getUniqueId())) {
				return Bukkit.getPlayer(uuidEntry.getKey());
			}
		}
		return null;
	}

	public static HashMap<UUID, UUID> getTpPlayerMap() {

		return tpPlayerMap;
	}

}
