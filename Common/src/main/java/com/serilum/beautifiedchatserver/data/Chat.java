package com.serilum.beautifiedchatserver.data;

import com.serilum.beautifiedchatserver.util.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class Chat {
	public static final ResourceKey<ChatType> FORMATTED_CHAT_TYPE = ResourceKey.create(Registries.CHAT_TYPE, Identifier.fromNamespaceAndPath(Reference.MOD_ID, "chat"));
}
