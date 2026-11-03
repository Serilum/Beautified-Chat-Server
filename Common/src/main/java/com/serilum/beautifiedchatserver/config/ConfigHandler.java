package com.serilum.beautifiedchatserver.config;

import com.natamus.collective.config.DuskConfig;
import com.serilum.beautifiedchatserver.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<String, List<String>>();

	@Entry public static boolean showRankTitles = true;
	@Entry public static boolean useRankColours = true;
	@Entry public static String chatMessageFormat = "%timestamp% | %username%: %chatmessage%";
	@Entry public static String timestampFormat = "HH:mm";
	@Entry public static String timestampTimeZone = "";
	@Entry public static String rankTitleFormat = "[%rank] ";
	@Entry(min = 0, max = 15) public static int chatTimestampColour = 8;
	@Entry(min = 0, max = 15) public static int chatUsernameColour = 2;
	@Entry(min = 0, max = 15) public static int chatOtherUsernameColour = 3;
	@Entry(min = 0, max = 15) public static int chatMessageColour = 15;
	@Entry(min = 0, max = 15) public static int chatOtherSymbolsColour = 7;
	@Entry public static boolean overwriteOtherModColours = false;
	@Entry public static boolean sendAsPlayerChat = true;
	@Entry public static boolean formatCommandFeedback = true;
	@Entry(min = 0, max = 15) public static int commandFeedbackColour = 2;

	public static void initConfig() {
		configMetaData.put("showRankTitles", Arrays.asList(
			"Whether the rank titles, created in /bcs rank, should be shown before the username."
		));
		configMetaData.put("useRankColours", Arrays.asList(
			"Whether rank colours, set in /bcs rank, should be used as the priority username colour."
		));

		configMetaData.put("chatMessageFormat", Arrays.asList(
			"Variables: %timestamp% = timestamp set in timestampFormat. %username% = the username of the player who sent the message. %chatmessage% = the message itself."
		));
		configMetaData.put("timestampFormat", Arrays.asList(
			"Example time in formats: 5 seconds past 9 o' clock in the evening. *=Default. *(HH:mm) = 21:00, (HH:mm:ss) = 21:00:05, (hh:mm a) = 9:00 PM"
		));
		configMetaData.put("timestampTimeZone", Arrays.asList(
			"The time zone of the timestamp, like 'Europe/London' or 'GMT+0'. Leave empty to use the server's time zone."
		));
		configMetaData.put("rankTitleFormat", Arrays.asList(
			"The rank format prefixed before %username% when showRankTitles is enabled."
		));
		configMetaData.put("chatTimestampColour", Arrays.asList(
			"The colour of the timestamp in the chat message. The possible values are; 0: black, 1: dark_blue, 2: dark_green, 3: dark_aqua, 4: dark_red, 5: dark_purple, 6: gold, 7: gray, 8: dark_gray, 9: blue, 10: green, 11: aqua, 12: red, 13: light_purple, 14: yellow, 15: white."
		));
		configMetaData.put("chatUsernameColour", Arrays.asList(
			"The colour of your own username in the chat message. The possible values are; 0: black, 1: dark_blue, 2: dark_green, 3: dark_aqua, 4: dark_red, 5: dark_purple, 6: gold, 7: gray, 8: dark_gray, 9: blue, 10: green, 11: aqua, 12: red, 13: light_purple, 14: yellow, 15: white."
		));
		configMetaData.put("chatOtherUsernameColour", Arrays.asList(
			"The colour of other players' usernames in the chat message. chatUsernameColour is used for your own. The possible values are; 0: black, 1: dark_blue, 2: dark_green, 3: dark_aqua, 4: dark_red, 5: dark_purple, 6: gold, 7: gray, 8: dark_gray, 9: blue, 10: green, 11: aqua, 12: red, 13: light_purple, 14: yellow, 15: white."
		));
		configMetaData.put("chatMessageColour", Arrays.asList(
			"The colour of the chat message. The possible values are; 0: black, 1: dark_blue, 2: dark_green, 3: dark_aqua, 4: dark_red, 5: dark_purple, 6: gold, 7: gray, 8: dark_gray, 9: blue, 10: green, 11: aqua, 12: red, 13: light_purple, 14: yellow, 15: white."
		));
		configMetaData.put("chatOtherSymbolsColour", Arrays.asList(
			"The colour of the other symbols from chatMessageFormat. So everything except the variables. The possible values are; 0: black, 1: dark_blue, 2: dark_green, 3: dark_aqua, 4: dark_red, 5: dark_purple, 6: gold, 7: gray, 8: dark_gray, 9: blue, 10: green, 11: aqua, 12: red, 13: light_purple, 14: yellow, 15: white."
		));
		configMetaData.put("overwriteOtherModColours", Arrays.asList(
			"Whether colours that other mods or teams give the username and message are replaced by the colours set here."
		));
		configMetaData.put("sendAsPlayerChat", Arrays.asList(
			"Whether messages are sent as player chat instead of system messages, so mods like Chat Heads can see who sent them."
		));
		configMetaData.put("formatCommandFeedback", Arrays.asList(
			"Whether the replies of commands get the colour set in commandFeedbackColour."
		));
		configMetaData.put("commandFeedbackColour", Arrays.asList(
			"The colour of command replies. Parts that already have a colour keep it. The possible values are; 0: black, 1: dark_blue, 2: dark_green, 3: dark_aqua, 4: dark_red, 5: dark_purple, 6: gold, 7: gray, 8: dark_gray, 9: blue, 10: green, 11: aqua, 12: red, 13: light_purple, 14: yellow, 15: white."
		));

		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}