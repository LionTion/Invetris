package net.LionTion.Invetris.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.LionTion.Invetris.Mod;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class DisableCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("invetris").then(CommandManager.literal("disable").executes(DisableCommand::run)));
    }

    private static int run(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {
        if (Mod.Enabled) {
            Mod.Enabled = false;
            serverCommandSourceCommandContext.getSource().sendMessage(Text.literal("§e> §cInvetris disabled!"));
            return 1;
        }
        serverCommandSourceCommandContext.getSource().sendMessage(Text.literal("§e> §fInvetris is already disabled!"));
        return 0;
    }
}
