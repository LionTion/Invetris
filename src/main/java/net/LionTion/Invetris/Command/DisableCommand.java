package net.LionTion.Invetris.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.LionTion.Invetris.Mod;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DisableCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("invetris").then(literal("disable").executes(DisableCommand::run)));
    }

    private static int run(CommandContext<FabricClientCommandSource> ctx) {
        if (Mod.Enabled) {
            Mod.Enabled = false;
            ctx.getSource().sendFeedback(Text.literal("§e> §cInvetris disabled!"));
            return 1;
        }
        ctx.getSource().sendFeedback(Text.literal("§e> §fInvetris is already disabled!"));
        return 0;
    }
}
