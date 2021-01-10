package net.fiftyfivec3.kernels;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Mod implements ModInitializer {
	public static Logger LOGGER = LogManager.getLogger("Kernel Mod");

	public static int contrast = 0, brightness = 0;
	public static Options kernel = Options.NONE;

	@Override
	public void onInitialize() {
		LOGGER.info("Mod loaded");

		CommandRegistrationCallback.EVENT.register(((commandDispatcher, b) -> commandDispatcher.register(
				literal("kernel").then(literal("brightness").then(
						argument("brightness", IntegerArgumentType.integer(0,255)).executes(context -> {
							brightness = IntegerArgumentType.getInteger(context, "brightness");
							return 1;
						}))).then(literal("contrast").then(
						argument("contrast", IntegerArgumentType.integer(0,255)).executes(context -> {
							brightness = IntegerArgumentType.getInteger(context, "contrast");
							return 1;
						}))).then(
						literal("set").then(
							literal("none").executes(context -> {kernel = Options.NONE;return 1;})
						).then(
							literal("emboss").executes(context -> {kernel = Options.EMBOSS;return 1;})
						).then(
							literal("emboss").executes(context -> {kernel = Options.EMBOSS;return 1;})
						).then(
							literal("sharpen").executes(context -> {kernel = Options.SHARPEN;return 1;})
						).then(
							literal("sobel").executes(context -> {kernel = Options.SOBEL;return 1;})
						).then(
							literal("outline").executes(context -> {kernel = Options.OUTLINE;return 1;})
						)
				))
		));
	}
}
