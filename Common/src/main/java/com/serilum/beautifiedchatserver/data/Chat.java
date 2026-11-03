package com.serilum.beautifiedchatserver.data;

import com.serilum.beautifiedchatserver.util.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class Chat {
	public static final ResourceKey<ChatType> FORMATTED_CHAT_TYPE = ResourceKey.create(Registries.CHAT_TYPE, new ResourceLocation(Reference.MOD_ID, "chat"));
}
