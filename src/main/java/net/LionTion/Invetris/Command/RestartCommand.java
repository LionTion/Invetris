package net.LionTion.Invetris.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.LionTion.Invetris.Mod;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public class RestartCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("invetris").then(CommandManager.literal("restart").executes(RestartCommand::run)));
    }

    private static int run(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {
        Mod.Tetris.restart();
        if (Mod.Enabled) {
            serverCommandSourceCommandContext.getSource().sendMessage(Text.literal("§e> §bInvetris restarted!"));
        } else {
            serverCommandSourceCommandContext.getSource().sendMessage(Text.literal("§e> §fInvetris is currently disabled, but the game was restarted anyway."));
        }
        return 1;
    }
}
