package com.natamus.beautifiedchatserver.neoforge.events;

import com.natamus.beautifiedchatserver.cmd.CommandBCS;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class NeoForgeRegisterCommandsEvent {
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent e) {
		CommandBCS.register(e.getDispatcher());
	}
}
