package com.natamus.beautifiedchatserver.forge.events;

import com.natamus.beautifiedchatserver.cmd.CommandBCS;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeRegisterCommandsEvent {
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent e) {
		CommandBCS.register(e.getDispatcher());
	}
}
