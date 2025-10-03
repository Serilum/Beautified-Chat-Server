package com.natamus.beautifiedchatserver.forge.events;

import com.mojang.datafixers.util.Pair;
import com.natamus.beautifiedchatserver.data.Chat;
import com.natamus.beautifiedchatserver.events.BeautifulChatEvent;
import com.natamus.collective.functions.MessageFunctions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

public class ForgeBeautifulChatEvent {
	public static void registerEventsInBus() {
		// BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeBeautifulChatEvent.class);

		ServerChatEvent.BUS.addListener(ForgeBeautifulChatEvent::onServerChat);
	}

	@SubscribeEvent
	public static boolean onServerChat(ServerChatEvent e) {
		ServerPlayer serverPlayer = e.getPlayer();
		Component originalMessage = e.getMessage();
		MutableComponent fullMessage = Component.literal("<" + serverPlayer.getName().getString() + "> ").append(originalMessage);

		Pair<Boolean, Component> pair = BeautifulChatEvent.onServerChat(serverPlayer, fullMessage, serverPlayer.getUUID());
		if (pair != null) {
			if (pair.getFirst()) {
				MutableComponent newMessage = pair.getSecond().copy();
				if (fullMessage != newMessage) {
					serverPlayer.level().getServer().execute(() -> {
						Chat.logger.info(newMessage.getString());
						MessageFunctions.broadcastMessage(serverPlayer.level(), newMessage);
					});

					return true;
				}
			}
		}
		return false;
	}
}
