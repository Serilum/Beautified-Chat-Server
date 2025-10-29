package com.natamus.beautifiedchatserver.cmd;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.natamus.beautifiedchatserver.util.Reference;
import com.natamus.beautifiedchatserver.util.Util;
import com.natamus.collective.functions.MessageFunctions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Arrays;

public class CommandBCS {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		for (String commandPrefix : Arrays.asList(Reference.MOD_ID, "bcs")) {
			dispatcher.register(Commands.literal(commandPrefix).requires((iCommandSender) -> { return iCommandSender.hasPermission(2); })
				.then(Commands.literal("rank")
				.then(Commands.literal("create")
				.then(Commands.argument("rank-name", StringArgumentType.string())
				.then(Commands.argument("color", StringArgumentType.string())
				.suggests(Util::suggestsColors)
				.executes((command) -> {
					String rankName = StringArgumentType.getString(command, "rank-name");
					String colorStr = StringArgumentType.getString(command, "color");

					ChatFormatting color = Util.parseColor(colorStr);
					if (color == null) {
						command.getSource().sendFailure(Util.prefix()
							.append(Util.text("Unknown color: ", ChatFormatting.RED))
							.append(Util.text(colorStr, ChatFormatting.WHITE)));
						return 0;
					}

					boolean changed = Util.addOrUpdateRank(rankName, color);
					if (changed) {
						MessageFunctions.sendMessage(command.getSource(),
							Util.prefix()
								.append(Util.text("Rank ", ChatFormatting.GRAY))
								.append(Util.text(Util.norm(rankName), ChatFormatting.AQUA))
								.append(Util.text(" set to color ", ChatFormatting.GRAY))
								.append(Util.text(color.getName(), color)), true);
					}
					else {
						MessageFunctions.sendMessage(command.getSource(),
							Util.prefix()
								.append(Util.text("Rank ", ChatFormatting.GRAY))
								.append(Util.text(Util.norm(rankName), ChatFormatting.AQUA))
								.append(Util.text(" already had color ", ChatFormatting.GRAY))
								.append(Util.text(color.getName(), color)), true);
					}
					return 1;
				}))))

				.then(Commands.literal("remove")
				.then(Commands.argument("rank-name", StringArgumentType.string())
				.suggests(Util::suggestRanks)
				.executes((command) -> {
					String rankName = StringArgumentType.getString(command, "rank-name");

					boolean removed = Util.removeRank(rankName);
					if (removed) {
						MessageFunctions.sendMessage(command.getSource(),
							Util.prefix()
								.append(Util.text("Removed rank ", ChatFormatting.GRAY))
								.append(Util.text(Util.norm(rankName), ChatFormatting.AQUA)), true);
						return 1;
					}
					else {
						command.getSource().sendFailure(Util.prefix()
							.append(Util.text("Rank ", ChatFormatting.RED))
							.append(Util.text(Util.norm(rankName), ChatFormatting.AQUA))
							.append(Util.text(" does not exist.", ChatFormatting.RED)));
						return 0;
					}
				})))

				.then(Commands.literal("list")
				.executes((command) -> {
					JsonObject ranks = Util.readJsonObjectSafe(Util.ranksPathString());
					if (ranks.size() == 0) {
						MessageFunctions.sendMessage(command.getSource(),
							Util.prefix().append(Util.text("No ranks defined.", ChatFormatting.GRAY)), true);
						return 1;
					}

					MutableComponent out = Util.prefix().append(Util.text("Ranks:", ChatFormatting.GOLD));
					for (String key : Util.sortedKeys(ranks)) {
						String colorName = Util.asString(ranks.get(key));
						ChatFormatting cf = colorName != null ? Util.parseColor(colorName) : null;

						out.append(Component.literal("\n"))
							.append(Util.text("- ", ChatFormatting.DARK_GRAY))
							.append(Util.text(key, ChatFormatting.AQUA))
							.append(Util.text(" = ", ChatFormatting.GRAY))
							.append(Util.text(colorName == null ? "UNKNOWN" : colorName, cf != null ? cf : ChatFormatting.WHITE));
					}
					MessageFunctions.sendMessage(command.getSource(), out, true);
					return 1;
				}))
				)

				.then(Commands.literal("player")
				.then(Commands.literal("add")
				.then(Commands.argument("player-name", StringArgumentType.string())
				.then(Commands.argument("rank-name", StringArgumentType.string())
				.suggests(Util::suggestRanks)
				.executes((command) -> {
					String playerName = StringArgumentType.getString(command, "player-name");
					String rankName = StringArgumentType.getString(command, "rank-name");

					String current = Util.getRankOfPlayer(playerName).orElse(null);
					if (Util.rankEquals(current, rankName)) {
						MessageFunctions.sendMessage(command.getSource(),
							Util.prefix()
								.append(Util.text("Player ", ChatFormatting.GRAY))
								.append(Util.text(playerName, ChatFormatting.GREEN))
								.append(Util.text(" is already in rank ", ChatFormatting.GRAY))
								.append(Util.text(Util.norm(current), ChatFormatting.AQUA)), true);
						return 1;
					}

					String outcome = Util.togglePlayerRank(playerName, rankName);
					if ("removed".equals(outcome)) {
						Util.togglePlayerRank(playerName, rankName);
					}

					MessageFunctions.sendMessage(command.getSource(),
						Util.prefix()
							.append(Util.text("Player ", ChatFormatting.GRAY))
							.append(Util.text(playerName, ChatFormatting.GREEN))
							.append(Util.text(" added to rank ", ChatFormatting.GRAY))
							.append(Util.text(Util.norm(rankName), ChatFormatting.AQUA)), true);
					return 1;
				}))))

				.then(Commands.literal("remove")
				.then(Commands.argument("player-name", StringArgumentType.string())
				.then(Commands.argument("rank-name", StringArgumentType.string())
				.suggests(Util::suggestRanks)
				.executes((command) -> {
					String playerName = StringArgumentType.getString(command, "player-name");
					String rankName = StringArgumentType.getString(command, "rank-name");

					String current = Util.getRankOfPlayer(playerName).orElse(null);
					if (!Util.rankEquals(current, rankName)) {
						command.getSource().sendFailure(Util.prefix()
							.append(Util.text("Player ", ChatFormatting.RED))
							.append(Util.text(playerName, ChatFormatting.GREEN))
							.append(Util.text(" is not in rank ", ChatFormatting.RED))
							.append(Util.text(Util.norm(rankName), ChatFormatting.AQUA))
							.append(Util.text(".", ChatFormatting.RED)));
						return 0;
					}

					Util.togglePlayerRank(playerName, rankName);
					MessageFunctions.sendMessage(command.getSource(),
						Util.prefix()
							.append(Util.text("Removed player ", ChatFormatting.GRAY))
							.append(Util.text(playerName, ChatFormatting.GREEN))
							.append(Util.text(" from rank ", ChatFormatting.GRAY))
							.append(Util.text(Util.norm(rankName), ChatFormatting.AQUA)), true);
					return 1;
				}))))

				.then(Commands.literal("list")
				.executes((command) -> {
					JsonObject players = Util.readJsonObjectSafe(Util.playersPathString());
					if (players.size() == 0) {
						MessageFunctions.sendMessage(command.getSource(),
							Util.prefix().append(Util.text("No players assigned to ranks.", ChatFormatting.GRAY)), true);
						return 1;
					}

					MutableComponent out = Util.prefix().append(Util.text("Players with ranks:", ChatFormatting.GOLD));
					for (String player : Util.sortedKeys(players)) {
						String rank = Util.asString(players.get(player));
						ChatFormatting cf = rank != null ? Util.parseColor(Util.getRankColor(rank).map(ChatFormatting::getName).orElse(rank)) : null;

						out.append(Component.literal("\n"))
							.append(Util.text("- ", ChatFormatting.DARK_GRAY))
							.append(Util.text(player, ChatFormatting.GREEN))
							.append(Util.text(" -> ", ChatFormatting.GRAY))
							.append(Util.text(rank == null ? "UNKNOWN" : Util.norm(rank), ChatFormatting.AQUA));
					}
					MessageFunctions.sendMessage(command.getSource(), out, true);
					return 1;
				}))
				)
			);
		}
	}
}
