package com.natamus.beautifiedchatserver.util;

import com.google.gson.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.natamus.beautifiedchatserver.config.ConfigHandler;
import com.natamus.collective.functions.DataFunctions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Util {

	private static final Path BASE_DIR = Paths.get(DataFunctions.getConfigDirectory(), "beautifiedchatserver");
	private static final Path RANKS_FILE = BASE_DIR.resolve("ranks.json");
	private static final Path PLAYERS_FILE = BASE_DIR.resolve("players.json");

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


	//  Colour Helpers

	public static ChatFormatting getColour(String word, String playerName) {
		ChatFormatting colour = ChatFormatting.getById(ConfigHandler.chatOtherSymbolsColour);

		if (word.equalsIgnoreCase("timestamp")) {
			colour = ChatFormatting.getById(ConfigHandler.chatTimestampColour);
		}
		else if (word.equalsIgnoreCase("username")) {
			if (ConfigHandler.useRankColours) {
				Optional<String> optionalPlayerRank = getRankOfPlayer(playerName);
				if (optionalPlayerRank.isPresent()) {
					String playerRank = optionalPlayerRank.get();

					Optional<ChatFormatting> optionalRankColour = getRankColor(playerRank);
					if (optionalRankColour.isPresent()) {
						return optionalRankColour.get();
					}
				}
			}
			colour = ChatFormatting.getById(ConfigHandler.chatUsernameColour);
		}
		else if (word.equalsIgnoreCase("chatmessage")) {
			colour = ChatFormatting.getById(ConfigHandler.chatMessageColour);
		}

		return colour;
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

	public static MutableComponent prefix() {
		return Component.literal("[").withStyle(ChatFormatting.DARK_GRAY).append(Component.literal("BCS").withStyle(ChatFormatting.GOLD)).append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY));
	}

	public static MutableComponent text(String s, ChatFormatting fmt) {
		return Component.literal(s).withStyle(fmt);
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