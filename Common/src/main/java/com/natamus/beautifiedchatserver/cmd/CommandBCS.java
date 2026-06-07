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
						command.getSource().sendFailure(Util.prefix().append(Component.translatable("collective.beautifiedchatserver.message.unknowncolor", Util.text(colorStr, ChatFormatting.WHITE)).withStyle(ChatFormatting.RED)));
						return 0;
					}

					boolean changed = Util.addOrUpdateRank(rankName, color);
					if (changed) {
						MessageFunctions.sendMessage(command.getSource(), Util.prefix().append(Component.translatable("collective.beautifiedchatserver.message.ranksetcolor", Util.text(Util.norm(rankName), ChatFormatting.AQUA), Util.text(color.getName(), color)).withStyle(ChatFormatting.GRAY)), true);
					}
					else {
						MessageFunctions.sendMessage(command.getSource(), Util.prefix().append(Component.translatable("collective.beautifiedchatserver.message.rankalreadycolor", Util.text(Util.norm(rankName), ChatFormatting.AQUA), Util.text(color.getName(), color)).withStyle(ChatFormatting.GRAY)), true);
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
						MessageFunctions.sendMessage(command.getSource(), Util.prefix().append(Component.translatable("collective.beautifiedchatserver.message.removedrank", Util.text(Util.norm(rankName), ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY)), true);
						return 1;
					}
					else {
						command.getSource().sendFailure(Util.prefix().append(Component.translatable("collective.beautifiedchatserver.message.rankdoesnotexist", Util.text(Util.norm(rankName), ChatFormatting.AQUA)).withStyle(ChatFormatting.RED)));
						return 0;
					}
				})))

				.then(Commands.literal("list")
				.executes((command) -> {
					JsonObject ranks = Util.readJsonObjectSafe(Util.ranksPathString());
					if (ranks.isEmpty()) {
						MessageFunctions.sendMessage(command.getSource(), Util.prefix().append(Component.translatable("collective.beautifiedchatserver.message.noranksdefined").withStyle(ChatFormatting.GRAY)), true);
						return 1;
					}

					MutableComponent out = Util.prefix().append(Component.translatable("collective.beautifiedchatserver.message.ranksheader").withStyle(ChatFormatting.GOLD));
					for (String key : Util.sortedKeys(ranks)) {
						String colorName = Util.asString(ranks.get(key));
						ChatFormatting cf = colorName != null ? Util.parseColor(colorName) : null;

						out.append(Component.literal("\n")).append(Component.translatable("collective.beautifiedchatserver.message.listitem", Util.text(key, ChatFormatting.AQUA), Util.text(colorName == null ? "UNKNOWN" : colorName, cf != null ? cf : ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY));
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
						MessageFunctions.sendMessage(command.getSource(), Util.prefix().append(Component.translatable("collective.beautifiedchatserver.message.playeralreadyinrank", Util.text(playerName, ChatFormatting.GREEN), Util.text(Util.norm(current), ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY)), true);
						return 1;
					}

					String outcome = Util.togglePlayerRank(playerName, rankName);
					if ("removed".equals(outcome)) {
						Util.togglePlayerRank(playerName, rankName);
					}

					MessageFunctions.sendMessage(command.getSource(), Util.prefix().append(Component.translatable("collective.beautifiedchatserver.message.playeraddedtorank", Util.text(playerName, ChatFormatting.GREEN), Util.text(Util.norm(rankName), ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY)), true);
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
						command.getSource().sendFailure(Util.prefix().append(Component.translatable("collective.beautifiedchatserver.message.playernotinrank", Util.text(playerName, ChatFormatting.GREEN), Util.text(Util.norm(rankName), ChatFormatting.AQUA)).withStyle(ChatFormatting.RED)));
						return 0;
					}

					Util.togglePlayerRank(playerName, rankName);
					MessageFunctions.sendMessage(command.getSource(), Util.prefix().append(Component.translatable("collective.beautifiedchatserver.message.removedplayerfromrank", Util.text(playerName, ChatFormatting.GREEN), Util.text(Util.norm(rankName), ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY)), true);
					return 1;
				}))))

				.then(Commands.literal("list")
				.executes((command) -> {
					JsonObject players = Util.readJsonObjectSafe(Util.playersPathString());
					if (players.isEmpty()) {
						MessageFunctions.sendMessage(command.getSource(), Util.prefix().append(Component.translatable("collective.beautifiedchatserver.message.noplayersassigned").withStyle(ChatFormatting.GRAY)), true);
						return 1;
					}

					MutableComponent out = Util.prefix().append(Component.translatable("collective.beautifiedchatserver.message.playerswithranks").withStyle(ChatFormatting.GOLD));
					for (String player : Util.sortedKeys(players)) {
						String rank = Util.asString(players.get(player));
						ChatFormatting cf = rank != null ? Util.parseColor(Util.getRankColor(rank).map(ChatFormatting::getName).orElse(rank)) : null;

						out.append(Component.literal("\n")).append(Component.translatable("collective.beautifiedchatserver.message.listitem", Util.text(player, ChatFormatting.GREEN), Util.text(rank == null ? "UNKNOWN" : Util.norm(rank), ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
					}
					MessageFunctions.sendMessage(command.getSource(), out, true);
					return 1;
				}))
				)
			);
		}
	}
}
