package com.serilum.beautifiedchatserver.cmd;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.natamus.collective.functions.MessageFunctions;
import com.serilum.beautifiedchatserver.util.Reference;
import com.serilum.beautifiedchatserver.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class CommandBCS {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		for (String commandName : Arrays.asList(Reference.MOD_ID, "bcs")) {
			dispatcher.register(Commands.literal(commandName).requires((iCommandSender) -> { return iCommandSender.hasPermission(2); })
				.executes((command) -> {
					showUsage(command.getSource(), commandName);
					return 1;
				})
				.then(Commands.literal("usage")
				.executes((command) -> {
					showUsage(command.getSource(), commandName);
					return 1;
				}))
				.then(Commands.literal("rank")
				.then(Commands.literal("create")
				.then(Commands.argument("rank-name", StringArgumentType.string())
				.then(Commands.argument("color", StringArgumentType.string())
				.suggests(Util::suggestsColors)
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
					String rankName = Util.norm(StringArgumentType.getString(command, "rank-name"));
					String colourInput = StringArgumentType.getString(command, "color");

					ChatFormatting colour = Util.parseColor(colourInput);
					if (colour == null) {
						MessageFunctions.sendTranslatableMessage(source, "collective.beautifiedchatserver.message.unknowncolor", true, ChatFormatting.RED, colourInput);
						return 0;
					}

					String colourName = colour.getName();
					if (!Util.addOrUpdateRank(rankName, colour)) {
						MessageFunctions.sendTranslatableMessage(source, "collective.beautifiedchatserver.message.rankalreadycolor", true, ChatFormatting.YELLOW, rankName, colourName);
						return 1;
					}

					MessageFunctions.sendTranslatableMessage(source, "collective.beautifiedchatserver.message.ranksetcolor", true, ChatFormatting.DARK_GREEN, rankName, colourName);
					return 1;
				}))))
				.then(Commands.literal("remove")
				.then(Commands.argument("rank-name", StringArgumentType.string())
				.suggests(Util::suggestRanks)
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
					String rankName = Util.norm(StringArgumentType.getString(command, "rank-name"));

					if (!Util.removeRank(rankName)) {
						MessageFunctions.sendTranslatableMessage(source, "collective.beautifiedchatserver.message.rankdoesnotexist", true, ChatFormatting.RED, rankName);
						return 0;
					}

					MessageFunctions.sendTranslatableMessage(source, "collective.beautifiedchatserver.message.removedrank", true, ChatFormatting.DARK_GREEN, rankName);
					return 1;
				})))
				.then(Commands.literal("list")
				.executes((command) -> {
					showRanks(command.getSource());
					return 1;
				})))
				.then(Commands.literal("player")
				.then(Commands.literal("add")
				.then(Commands.argument("player-name", StringArgumentType.string())
				.then(Commands.argument("rank-name", StringArgumentType.string())
				.suggests(Util::suggestRanks)
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
					String playerName = StringArgumentType.getString(command, "player-name");
					String rankName = Util.norm(StringArgumentType.getString(command, "rank-name"));

					Optional<String> optionalCurrentRank = Util.getRankOfPlayer(playerName);
					if (optionalCurrentRank.isPresent() && Util.rankEquals(optionalCurrentRank.get(), rankName)) {
						MessageFunctions.sendTranslatableMessage(source, "collective.beautifiedchatserver.message.playeralreadyinrank", true, ChatFormatting.YELLOW, playerName, rankName);
						return 1;
					}

					Util.togglePlayerRank(playerName, rankName);
					MessageFunctions.sendTranslatableMessage(source, "collective.beautifiedchatserver.message.playeraddedtorank", true, ChatFormatting.DARK_GREEN, playerName, rankName);
					return 1;
				}))))
				.then(Commands.literal("remove")
				.then(Commands.argument("player-name", StringArgumentType.string())
				.then(Commands.argument("rank-name", StringArgumentType.string())
				.suggests(Util::suggestRanks)
				.executes((command) -> {
					CommandSourceStack source = command.getSource();
					String playerName = StringArgumentType.getString(command, "player-name");
					String rankName = Util.norm(StringArgumentType.getString(command, "rank-name"));

					Optional<String> optionalCurrentRank = Util.getRankOfPlayer(playerName);
					if (optionalCurrentRank.isEmpty() || !Util.rankEquals(optionalCurrentRank.get(), rankName)) {
						MessageFunctions.sendTranslatableMessage(source, "collective.beautifiedchatserver.message.playernotinrank", true, ChatFormatting.RED, playerName, rankName);
						return 0;
					}

					Util.togglePlayerRank(playerName, rankName);
					MessageFunctions.sendTranslatableMessage(source, "collective.beautifiedchatserver.message.removedplayerfromrank", true, ChatFormatting.DARK_GREEN, playerName, rankName);
					return 1;
				}))))
				.then(Commands.literal("list")
				.executes((command) -> {
					showPlayerRanks(command.getSource());
					return 1;
				})))
			);
		}
	}

	private static void showRanks(CommandSourceStack source) {
		MessageFunctions.sendTranslatableMessage(source, "collective.beautifiedchatserver.message.ranksheader", true, ChatFormatting.DARK_GREEN);

		JsonObject ranks = Util.readJsonObjectSafe(Util.ranksPathString());
		if (ranks.isEmpty()) {
			MessageFunctions.sendTranslatableMessage(source, " ", "collective.beautifiedchatserver.message.noranksdefined", ChatFormatting.RED);
			return;
		}

		for (String rankName : Util.sortedKeys(ranks)) {
			String colourName = Util.asString(ranks.get(rankName));
			if (colourName == null) {
				colourName = "?";
			}

			MessageFunctions.sendTranslatableMessage(source, " ", "collective.beautifiedchatserver.message.listitem", ChatFormatting.GRAY, rankName, colourName.toLowerCase(Locale.ROOT));
		}
	}

	private static void showPlayerRanks(CommandSourceStack source) {
		MessageFunctions.sendTranslatableMessage(source, "collective.beautifiedchatserver.message.playerswithranks", true, ChatFormatting.DARK_GREEN);

		JsonObject players = Util.readJsonObjectSafe(Util.playersPathString());
		if (players.isEmpty()) {
			MessageFunctions.sendTranslatableMessage(source, " ", "collective.beautifiedchatserver.message.noplayersassigned", ChatFormatting.RED);
			return;
		}

		for (String playerName : Util.sortedKeys(players)) {
			String rankName = Util.asString(players.get(playerName));
			if (rankName == null) {
				rankName = "?";
			}

			MessageFunctions.sendTranslatableMessage(source, " ", "collective.beautifiedchatserver.message.listitem", ChatFormatting.GRAY, playerName, rankName);
		}
	}

	private static void showUsage(CommandSourceStack source, String commandName) {
		MessageFunctions.sendTranslatableMessage(source, "collective.shared.message.usage", true, ChatFormatting.DARK_GREEN, Reference.NAME);
		MessageFunctions.sendMessage(source, " /" + commandName + " usage", ChatFormatting.DARK_GREEN);
		MessageFunctions.sendTranslatableMessage(source, "  ", "collective.shared.message.showmessage", ChatFormatting.GRAY);
		MessageFunctions.sendMessage(source, " /" + commandName + " rank create <rank-name> <color>", ChatFormatting.DARK_GREEN);
		MessageFunctions.sendTranslatableMessage(source, "  ", "collective.beautifiedchatserver.message.createsrank", ChatFormatting.GRAY);
		MessageFunctions.sendMessage(source, " /" + commandName + " rank remove <rank-name>", ChatFormatting.DARK_GREEN);
		MessageFunctions.sendTranslatableMessage(source, "  ", "collective.beautifiedchatserver.message.removesrank", ChatFormatting.GRAY);
		MessageFunctions.sendMessage(source, " /" + commandName + " rank list", ChatFormatting.DARK_GREEN);
		MessageFunctions.sendTranslatableMessage(source, "  ", "collective.beautifiedchatserver.message.listsranks", ChatFormatting.GRAY);
		MessageFunctions.sendMessage(source, " /" + commandName + " player add <player-name> <rank-name>", ChatFormatting.DARK_GREEN);
		MessageFunctions.sendTranslatableMessage(source, "  ", "collective.beautifiedchatserver.message.addsplayertorank", ChatFormatting.GRAY);
		MessageFunctions.sendMessage(source, " /" + commandName + " player remove <player-name> <rank-name>", ChatFormatting.DARK_GREEN);
		MessageFunctions.sendTranslatableMessage(source, "  ", "collective.beautifiedchatserver.message.removesplayerfromrank", ChatFormatting.GRAY);
		MessageFunctions.sendMessage(source, " /" + commandName + " player list", ChatFormatting.DARK_GREEN);
		MessageFunctions.sendTranslatableMessage(source, "  ", "collective.beautifiedchatserver.message.listsplayerranks", ChatFormatting.GRAY);
	}
}
