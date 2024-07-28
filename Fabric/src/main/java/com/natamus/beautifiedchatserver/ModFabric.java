package com.natamus.beautifiedchatserver;

import com.natamus.beautifiedchatserver.events.BeautifulChatEvent;
import com.natamus.beautifiedchatserver.util.Reference;
import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.collective.fabric.callbacks.CollectiveChatEvents;
import net.fabricmc.api.ModInitializer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class ModFabric implements ModInitializer {
	
	@Override
	public void onInitialize() {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		setGlobalConstants();
		ModCommon.init();

		loadEvents();

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadEvents() {
		CollectiveChatEvents.SERVER_CHAT_RECEIVED.register((ServerPlayer serverPlayer, Component message, UUID uuid) -> {
			return BeautifulChatEvent.onServerChat(serverPlayer, message, uuid);
		});
	}

	private static void setGlobalConstants() {

	}
}
