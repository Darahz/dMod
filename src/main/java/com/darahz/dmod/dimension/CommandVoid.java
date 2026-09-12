package com.darahz.dmod.dimension;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

/** Operator shortcut; normal gameplay uses the craftable Void Key. */
public class CommandVoid extends CommandBase {
    @Override public String getCommandName() { return "dvoid"; }
    @Override public String getCommandUsage(ICommandSender sender) { return "/dvoid"; }
    @Override public int getRequiredPermissionLevel() { return 2; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 0) {
            throw new CommandException("Usage: /dvoid");
        }
        VoidTravel.travel(getCommandSenderAsPlayer(sender));
    }
}
