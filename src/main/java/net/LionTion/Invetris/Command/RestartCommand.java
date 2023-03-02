package net.LionTion.Invetris.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.LionTion.Invetris.Mod;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class RestartCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("invetris").then(literal("restart").executes(RestartCommand::run)));
    }

    private static int run(CommandContext<FabricClientCommandSource> ctx) {
        Mod.Tetris.restart();
        if (Mod.Enabled) {
            ctx.getSource().sendFeedback(Text.literal("§e> §bInvetris restarted!"));
        } else {
            ctx.getSource().sendFeedback(Text.literal("§e> §fInvetris is currently disabled, but the game was restarted anyway."));
        }
        return 1;
    }
}
