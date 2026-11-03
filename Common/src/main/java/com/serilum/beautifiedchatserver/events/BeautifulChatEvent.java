package com.serilum.beautifiedchatserver.events;

import com.natamus.collective.functions.MessageFunctions;
import com.serilum.beautifiedchatserver.config.ConfigHandler;
import com.serilum.beautifiedchatserver.util.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.ChatVisiblity;

public class BeautifulChatEvent {
	public static boolean onChatMessageSend(ServerPlayer receiver, OutgoingChatMessage outgoingChatMessage, boolean filtered, ChatType.Bound chatType) {
		if (filtered || !Util.isPlayerChat(receiver, chatType)) {
			return false;
		}

		if (!(outgoingChatMessage instanceof OutgoingChatMessage.Player outgoingPlayerMessage)) {
			return false;
		}

		if (receiver.getChatVisibility() != ChatVisiblity.FULL) {
			return false;
		}

		PlayerChatMessage playerChatMessage = outgoingPlayerMessage.message();
		ServerPlayer sender = receiver.level().getServer().getPlayerList().getPlayer(playerChatMessage.sender());
		if (sender == null) {
			return false;
		}

		boolean keepOwnColours = !ConfigHandler.overwriteOtherModColours;
		MutableComponent chatMessage = Util.getColouredComponent(playerChatMessage.decoratedContent(), Util.getColour("chatmessage"), keepOwnColours);

		boolean isOwnMessage = receiver.getUUID().equals(sender.getUUID());
		MutableComponent formattedMessage = Util.getFormattedChatMessage(sender, chatMessage, Util.getTimestamp(), isOwnMessage);

		if (ConfigHandler.sendAsPlayerChat) {
			Util.sendAsPlayerChat(receiver, sender, playerChatMessage, formattedMessage);
		}
		else {
			MessageFunctions.sendMessage(receiver, formattedMessage);
		}
		return true;
	}
}
