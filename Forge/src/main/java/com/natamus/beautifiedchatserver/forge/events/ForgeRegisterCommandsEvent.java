package com.natamus.beautifiedchatserver.forge.events;

import com.natamus.beautifiedchatserver.cmd.CommandBCS;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

public class ForgeRegisterCommandsEvent {
	public static void registerEventsInBus() {
		// BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeRegisterCommandsEvent.class);

		RegisterCommandsEvent.BUS.addListener(ForgeRegisterCommandsEvent::registerCommands);
	}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent e) {
		CommandBCS.register(e.getDispatcher());
	}
}
