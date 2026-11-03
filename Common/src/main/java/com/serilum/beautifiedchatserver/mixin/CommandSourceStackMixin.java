package com.serilum.beautifiedchatserver.mixin;

import com.serilum.beautifiedchatserver.util.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Supplier;

@Mixin(value = CommandSourceStack.class, priority = 1001)
public class CommandSourceStackMixin {
	@ModifyVariable(method = "sendSuccess(Ljava/util/function/Supplier;Z)V", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
	public Supplier<Component> sendSuccess(Supplier<Component> messageSupplier) {
		return Util.getFormattedCommandFeedback(messageSupplier);
	}
}
