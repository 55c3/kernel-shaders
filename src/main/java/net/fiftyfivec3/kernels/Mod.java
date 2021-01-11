package net.fiftyfivec3.kernels;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fiftyfivec3.kernels.shaders.KernelShader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Mod implements ModInitializer {
	public static Logger LOGGER = LogManager.getLogger("Kernel Mod");

	public static int NONE = 0, EMBOSS = 1, OUTLINE = 2, SOBEL = 3, SHARPEN = 4;

	@Override
	public void onInitialize() {
		LOGGER.info("Mod loaded");

		CommandRegistrationCallback.EVENT.register(((commandDispatcher, b) -> commandDispatcher.register(
				literal("kernel").then(literal("brightness").then(
						argument("brightness", IntegerArgumentType.integer(0,255)).executes(context -> {
							KernelShader.brightness = IntegerArgumentType.getInteger(context, "brightness");
							return 1;
						}))).then(literal("contrast").then(
						argument("contrast", IntegerArgumentType.integer(0,255)).executes(context -> {
							KernelShader.contrast = IntegerArgumentType.getInteger(context, "contrast");
							return 1;
						}))).then(
						literal("set").then(
							literal("none").executes(context -> {KernelShader.kernel = NONE;return 1;})
						).then(
							literal("emboss").executes(context -> {KernelShader.kernel = EMBOSS;return 1;})
						).then(
							literal("emboss").executes(context -> {KernelShader.kernel = EMBOSS;return 1;})
						).then(
							literal("sharpen").executes(context -> {KernelShader.kernel = SHARPEN;return 1;})
						).then(
							literal("sobel").executes(context -> {KernelShader.kernel = SOBEL;return 1;})
						).then(
							literal("outline").executes(context -> {KernelShader.kernel = OUTLINE;return 1;})
						)
				))
		));
	}
}
