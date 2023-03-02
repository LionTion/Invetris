package net.LionTion.Invetris;

import net.LionTion.Invetris.Command.DisableCommand;
import net.LionTion.Invetris.Command.EnableCommand;
import net.LionTion.Invetris.Command.RestartCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.report.ReporterEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mod implements ModInitializer {

	public static boolean Enabled = true;
	public static Game Tetris = new Game();
	public static boolean Open = false;
	public static final Logger LOGGER = LoggerFactory.getLogger("invetris");

	@Override
	public void onInitialize() {

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (Enabled && screen instanceof InventoryScreen && !(screen instanceof GameScreen)) {
				GameScreen newScreen = new GameScreen(client.player);
				client.setScreenAndRender(newScreen);
				ScreenKeyboardEvents.beforeKeyPress(newScreen).register((gamescreen, key, scancode, modifiers) -> {
					String button = Character.toString(key);
					if (button.equalsIgnoreCase("a")) {
						Tetris.moveBlocks(-1, 0);
					} else if (button.equalsIgnoreCase("d")) {
						Tetris.moveBlocks(1, 0);
					} else if (button.equalsIgnoreCase("s")) {
						Tetris.moveBlocks(0, 1);
					} else if (button.equalsIgnoreCase("w")) {
						Tetris.rotateCurrent();
					} else if (button.equalsIgnoreCase("c")) {
						Tetris.spawnHeld();
					}
				});
				/*ScreenKeyboardEvents.beforeKeyRelease(newScreen).register((gamescreen, key, scancode, modifiers) -> {
					String button =  Character.toString(key);
					if (button.equalsIgnoreCase("a")) {
						pressed[0] = true;
					} else if (button.equalsIgnoreCase("d")) {
						pressed[1] = true;
					} else if (button.equalsIgnoreCase("s")) {
						pressed[2] = true;
					}
				});*/
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register((player) -> {
			if (Enabled && Open) {
				Tetris.tick();
			}
		});

		ClientCommandRegistrationCallback.EVENT.register(RestartCommand::register);
		ClientCommandRegistrationCallback.EVENT.register(DisableCommand::register);
		ClientCommandRegistrationCallback.EVENT.register(EnableCommand::register);

		Mod.LOGGER.debug("Invetris loaded!");
	}
}
