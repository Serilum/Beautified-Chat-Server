package com.serilum.beautifiedchatserver.forge.events;

import com.serilum.beautifiedchatserver.cmd.CommandBCS;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeRegisterCommandsEvent {
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent e) {
		CommandBCS.register(e.getDispatcher());
	}
}
