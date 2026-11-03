package com.serilum.beautifiedchatserver.util;

import com.google.gson.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.natamus.collective.functions.DataFunctions;
import com.natamus.collective.functions.StringFunctions;
import com.serilum.beautifiedchatserver.config.ConfigHandler;
import com.serilum.beautifiedchatserver.data.Chat;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class Util {

	private static final Path BASE_DIR = Paths.get(DataFunctions.getConfigDirectory(), "beautifiedchatserver");
	private static final Path RANKS_FILE = BASE_DIR.resolve("ranks.json");
	private static final Path PLAYERS_FILE = BASE_DIR.resolve("players.json");

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


	//  Colour Helpers

	public static ChatFormatting getColour(String word) {
		ChatFormatting colour = ChatFormatting.getById(ConfigHandler.chatOtherSymbolsColour);

		if (word.equalsIgnoreCase("timestamp")) {
			colour = ChatFormatting.getById(ConfigHandler.chatTimestampColour);
		}
		else if (word.equalsIgnoreCase("chatmessage")) {
			colour = ChatFormatting.getById(ConfigHandler.chatMessageColour);
		}

		return colour;
	}

	public static ChatFormatting getUsernameColour(String playerName, boolean isOwnMessage) {
		Optional<ChatFormatting> optionalRankColour = getRankColourOfPlayer(playerName);
		if (optionalRankColour.isPresent()) {
			return optionalRankColour.get();
		}

		if (isOwnMessage) {
			return ChatFormatting.getById(ConfigHandler.chatUsernameColour);
		}
		return ChatFormatting.getById(ConfigHandler.chatOtherUsernameColour);
	}

	public static Optional<ChatFormatting> getRankColourOfPlayer(String playerName) {
		if (!ConfigHandler.useRankColours) {
			return Optional.empty();
		}

		Optional<String> optionalPlayerRank = getRankOfPlayer(playerName);
		if (optionalPlayerRank.isEmpty()) {
			return Optional.empty();
		}
		return getRankColor(optionalPlayerRank.get());
	}

	public static MutableComponent getColouredComponent(Component component, ChatFormatting baseColour, boolean keepOwnColours) {
		MutableComponent colouredComponent = Component.empty().withStyle(baseColour);
		if (keepOwnColours) {
			return colouredComponent.append(component.copy());
		}

		component.visit((style, text) -> {
			colouredComponent.append(Component.literal(text).setStyle(style.withColor((TextColor)null)));
			return Optional.empty();
		}, Style.EMPTY);

		return colouredComponent;
	}

	public static MutableComponent getFormattedChatMessage(ServerPlayer sender, MutableComponent chatMessage, String timestamp, boolean isOwnMessage) {
		MutableComponent formattedMessage = Component.empty();
		for (String word : ConfigHandler.chatMessageFormat.split("%")) {
			MutableComponent wordComponent;
			if (word.equalsIgnoreCase("timestamp")) {
				wordComponent = Component.literal(timestamp).withStyle(getColour(word));
			}
			else if (word.equalsIgnoreCase("username")) {
				wordComponent = getUsernameComponent(sender, isOwnMessage);
			}
			else if (word.equalsIgnoreCase("chatmessage")) {
				wordComponent = chatMessage.copy();
			}
			else {
				wordComponent = Component.literal(word).withStyle(getColour(word));
			}

			formattedMessage.append(wordComponent);
		}

		return formattedMessage;
	}

	public static MutableComponent getUsernameComponent(ServerPlayer serverPlayer, boolean isOwnMessage) {
		String playerName = serverPlayer.getName().getString();
		ChatFormatting colour = getUsernameColour(playerName, isOwnMessage);

		boolean hasRankColour = getRankColourOfPlayer(playerName).isPresent();
		boolean keepOwnColours = !ConfigHandler.overwriteOtherModColours && !hasRankColour;
		MutableComponent username = getColouredComponent(serverPlayer.getDisplayName(), colour, keepOwnColours);

		String rankTitle = getRankTitle(playerName);
		if (rankTitle.isEmpty()) {
			return username;
		}
		return Component.literal(rankTitle).withStyle(colour).append(username);
	}

	public static String getRankTitle(String playerName) {
		if (!ConfigHandler.showRankTitles) {
			return "";
		}

		Optional<String> optionalPlayerRank = getRankOfPlayer(playerName);
		if (optionalPlayerRank.isEmpty()) {
			return "";
		}
		return ConfigHandler.rankTitleFormat.replace("%rank", StringFunctions.capitalizeEveryWord(optionalPlayerRank.get()));
	}

	public static boolean isPlayerChat(ChatType.Bound chatType) {
		return chatType.chatType().is(ChatType.CHAT);
	}

	public static void sendAsPlayerChat(ServerPlayer receiver, ServerPlayer sender, PlayerChatMessage playerChatMessage, MutableComponent formattedMessage) {
		OutgoingChatMessage formattedChatMessage = OutgoingChatMessage.create(playerChatMessage.withUnsignedContent(formattedMessage));
		formattedChatMessage.sendToPlayer(receiver, false, ChatType.bind(Chat.FORMATTED_CHAT_TYPE, sender));
	}

	public static Supplier<Component> getFormattedCommandFeedback(Supplier<Component> messageSupplier) {
		if (!ConfigHandler.formatCommandFeedback) {
			return messageSupplier;
		}

		ChatFormatting feedbackColour = ChatFormatting.getById(ConfigHandler.commandFeedbackColour);
		return () -> getColouredComponent(messageSupplier.get(), feedbackColour, true);
	}

	public static String getTimestamp() {
		SimpleDateFormat timestampFormat = new SimpleDateFormat(ConfigHandler.timestampFormat);

		String timeZoneId = ConfigHandler.timestampTimeZone.strip();
		if (!timeZoneId.isEmpty()) {
			try {
				timestampFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of(timeZoneId, ZoneId.SHORT_IDS)));
			}
			catch (DateTimeException ignored) {}
		}

		return timestampFormat.format(new Date());
	}

	public static ChatFormatting parseColor(String s) {
		if (s == null) {
			return null;
		}
		try {
			return ChatFormatting.valueOf(s.toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException ignored) {}

		for (ChatFormatting cf : ChatFormatting.values()) {
			if (cf.isColor() && cf.getName().equalsIgnoreCase(s)) {
				return cf;
			}
		}
		return null;
	}

	public static CompletableFuture<Suggestions> suggestsColors(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
		for (ChatFormatting cf : ChatFormatting.values()) {
			if (cf.isColor()) {
				builder.suggest(cf.getName());
			}
		}
		return builder.buildFuture();
	}


	//  Rank Handling

	public static boolean addOrUpdateRank(String rankName, ChatFormatting color) {
		ensureDirs();

		String r = norm(rankName);
		JsonObject ranks = readJsonObject(RANKS_FILE);
		String newColor = color.getName().toUpperCase(Locale.ROOT);

		if (ranks.has(r) && newColor.equals(asString(ranks.get(r)))) {
			return false;
		}

		ranks.addProperty(r, newColor);
		writeJsonObject(RANKS_FILE, ranks);
		return true;
	}

	public static boolean removeRank(String rankName) {
		ensureDirs();

		String r = norm(rankName);
		JsonObject ranks = readJsonObject(RANKS_FILE);
		if (!ranks.has(r)) {
			return false;
		}

		ranks.remove(r);
		writeJsonObject(RANKS_FILE, ranks);
		return true;
	}

	public static Optional<ChatFormatting> getRankColor(String rankName) {
		ensureDirs();

		String r = norm(rankName);
		JsonObject ranks = readJsonObject(RANKS_FILE);
		if (!ranks.has(r)) {
			return Optional.empty();
		}

		String colorName = asString(ranks.get(r));
		if (colorName == null) {
			return Optional.empty();
		}

		try {
			return Optional.of(ChatFormatting.valueOf(colorName.toUpperCase(Locale.ROOT)));
		}
		catch (IllegalArgumentException ex) {
			for (ChatFormatting cf : ChatFormatting.values()) {
				if (cf.getName().equalsIgnoreCase(colorName)) {
					return Optional.of(cf);
				}
			}
			return Optional.empty();
		}
	}


	//  Player Handling

	public static String togglePlayerRank(String playerName, String rankName) {
		ensureDirs();

		String p = norm(playerName);
		String r = norm(rankName);

		JsonObject players = readJsonObject(PLAYERS_FILE);
		String current = players.has(p) ? asString(players.get(p)) : null;

		if (r.equals(current)) {
			players.remove(p);
			writeJsonObject(PLAYERS_FILE, players);
			return "removed";
		}

		players.addProperty(p, r);
		writeJsonObject(PLAYERS_FILE, players);
		return "added";
	}

	public static Optional<String> getRankOfPlayer(String playerName) {
		ensureDirs();

		String p = norm(playerName);
		JsonObject players = readJsonObject(PLAYERS_FILE);
		if (!players.has(p)) {
			return Optional.empty();
		}
		return Optional.ofNullable(asString(players.get(p)));
	}


	// File Handling

	private static void ensureDirs() {
		try {
			Files.createDirectories(BASE_DIR);
			ensureFile(RANKS_FILE);
			ensureFile(PLAYERS_FILE);
		} catch (IOException e) {
			throw new RuntimeException("Failed to create config directories/files", e);
		}
	}

	private static void ensureFile(Path file) throws IOException {
		if (Files.notExists(file)) {
			writeJsonObject(file, new JsonObject());
		}
	}

	private static JsonObject readJsonObject(Path file) {
		try {
			if (Files.notExists(file)) {
				return new JsonObject();
			}
			String text = Files.readString(file, StandardCharsets.UTF_8).trim();
			if (text.isEmpty()) {
				return new JsonObject();
			}
			JsonElement el = JsonParser.parseString(text);
			if (el.isJsonObject()) {
				return el.getAsJsonObject();
			}
			return new JsonObject();
		} catch (Exception e) {
			return new JsonObject();
		}
	}

	private static void writeJsonObject(Path file, JsonObject obj) {
		JsonObject sorted = sortByKey(obj);
		try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
			GSON.toJson(sorted, bw);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write " + file, e);
		}
	}

	private static JsonObject sortByKey(JsonObject obj) {
		List<String> keys = new ArrayList<>(obj.keySet());
		Collections.sort(keys);
		JsonObject out = new JsonObject();
		for (String k : keys) {
			out.add(k, obj.get(k));
		}
		return out;
	}


	// Text & Command Helpers

	public static String asString(JsonElement el) {
		return (el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) ? el.getAsString() : null;
	}

	public static String norm(String s) {
		return s.toLowerCase(Locale.ROOT).trim();
	}

	public static boolean rankEquals(String a, String b) {
		if (a == null || b == null) {
			return false;
		}
		return norm(a).equals(norm(b));
	}

	public static JsonObject readJsonObjectSafe(String stringPath) {
		try {
			Path path = Paths.get(stringPath);
			if (!Files.exists(path)) {
				return new JsonObject();
			}
			String text = Files.readString(path, StandardCharsets.UTF_8).trim();
			if (text.isEmpty()) {
				return new JsonObject();
			}
			JsonElement el = JsonParser.parseString(text);
			return el.isJsonObject() ? el.getAsJsonObject() : new JsonObject();
		}
		catch (Exception ignored) {
			return new JsonObject();
		}
	}

	public static CompletableFuture<Suggestions> suggestRanks(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
		JsonObject ranks = Util.readJsonObjectSafe(ranksPathString());
		for (String key : sortedKeys(ranks)) {
			builder.suggest(key);
		}
		return builder.buildFuture();
	}

	public static String ranksPathString() {
		return RANKS_FILE.toString();
	}

	public static String playersPathString() {
		return PLAYERS_FILE.toString();
	}

	public static Iterable<String> sortedKeys(JsonObject obj) {
		List<String> keys = new ArrayList<>(obj.keySet());
		Collections.sort(keys);
		return keys;
	}
}