package com.serilum.beautifiedchatserver.mixin;

import com.serilum.beautifiedchatserver.events.BeautifulChatEvent;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayer.class, priority = 1001)
public class ServerPlayerMixin {
	@Inject(method = "sendChatMessage(Lnet/minecraft/network/chat/OutgoingChatMessage;ZLnet/minecraft/network/chat/ChatType$Bound;)V", at = @At(value = "HEAD"), cancellable = true)
	public void sendChatMessage(OutgoingChatMessage message, boolean filtered, ChatType.Bound chatType, CallbackInfo ci) {
		if (BeautifulChatEvent.onChatMessageSend((ServerPlayer)(Object)this, message, filtered, chatType)) {
			ci.cancel();
		}
	}
}
