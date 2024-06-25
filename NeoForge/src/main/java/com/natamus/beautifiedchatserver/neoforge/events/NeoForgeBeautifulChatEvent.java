package com.natamus.beautifiedchatserver.neoforge.events;

import com.mojang.datafixers.util.Pair;
import com.natamus.beautifiedchatserver.data.Chat;
import com.natamus.beautifiedchatserver.events.BeautifulChatEvent;
import com.natamus.collective.functions.MessageFunctions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class NeoForgeBeautifulChatEvent {
	@SubscribeEvent
	public static void onServerChat(ServerChatEvent e) {
		ServerPlayer serverPlayer = e.getPlayer();
		Component originalMessage = e.getMessage();
		MutableComponent fullMessage = Component.literal("<" + serverPlayer.getName().getString() + "> ").append(originalMessage);

		Pair<Boolean, Component> pair = BeautifulChatEvent.onServerChat(serverPlayer, fullMessage, serverPlayer.getUUID());
		if (pair != null) {
			if (pair.getFirst()) {
				MutableComponent newMessage = pair.getSecond().copy();
				if (fullMessage != newMessage) {
					e.setCanceled(true);

					serverPlayer.server.execute(() -> {
						Chat.logger.info(newMessage.getString());
						MessageFunctions.broadcastMessage(serverPlayer.level(), newMessage);
					});
				}
			}
		}
	}
}
