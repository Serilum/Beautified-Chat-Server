package com.natamus.beautifiedchatserver.events;

import com.mojang.datafixers.util.Pair;
import com.natamus.beautifiedchatserver.config.ConfigHandler;
import com.natamus.beautifiedchatserver.data.Chat;
import com.natamus.beautifiedchatserver.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class BeautifulChatEvent {
	public static Pair<Boolean, Component> onServerChat(ServerPlayer serverPlayer, Component messageComponent, UUID uuid) {
		String timestamp = new SimpleDateFormat(ConfigHandler.timestampFormat).format(new Date());
		
		String user = serverPlayer.getName().getString();
		String message = messageComponent.getString();
		if (!message.contains(user)) {
			return null;
		}
		
		if (message.contains("> ")) {
			message = message.substring(message.split("> ")[0].length() + 2);
		}
		
		MutableComponent output = Component.literal("");
		String raw_outputstring = ConfigHandler.chatMessageFormat;
		for (String word : raw_outputstring.split("%")) {
			ChatFormatting colour = Util.getColour(word);
			String toappend = word;
			
			if (word.equalsIgnoreCase("timestamp")) {
				toappend = timestamp;
			}
			else if (word.equalsIgnoreCase("username")) {
				toappend = user;
			}
			else if (word.equalsIgnoreCase("chatmessage")) {
				toappend = message;
			}
			
			MutableComponent wordcomponent = Component.literal(toappend);
			wordcomponent.withStyle(colour);
			output.append(wordcomponent);
		}

		Chat.logger.info(output.getString());
		return new Pair<Boolean, Component>(true, output);
	}
}
