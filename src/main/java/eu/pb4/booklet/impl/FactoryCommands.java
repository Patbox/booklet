package eu.pb4.booklet.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class FactoryCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(FactoryCommands::createCommands);
    }

    private static void createCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(literal("booklet")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                //.executes(FactoryCommands::about)
                .then(literal("open")
                        .then(argument("id", IdentifierArgument.id()).executes(FactoryCommands::bookletPage)))

        );
    }

    private static int bookletPage(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return BookletImplUtil.openPage(ctx.getSource().getPlayerOrException(), IdentifierArgument.getId(ctx, "id"), BookletOpenState.DEFAULT) ? 1 : 0;
    }

    private static int about(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Booklet by Patbox"));
        return 0;
    }
}
