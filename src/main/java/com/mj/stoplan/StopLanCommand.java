package com.mj.stoplan;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import java.lang.reflect.Field;

public class StopLanCommand extends CommandBase {

    @Override
    public String getName() {
        return "stoplan";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/stoplan - Stops the SP LAN server.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        // Player OP check
        if (!sender.canUseCommand(4, getName())) {
            sender.sendMessage(new TextComponentString("You cannot use this command."));
            return;
        }

        // Player world host check
        if (!hasOwnerPerms(sender)) {
            sender.sendMessage(new TextComponentString("You cannot use this command."));
            return;
        }


        if (server.isSinglePlayer()) {
            IntegratedServer integratedServer = Minecraft.getMinecraft().getIntegratedServer();
            if (integratedServer != null && integratedServer.getPublic()) {

                // 1. Disconnect all players except the host
                PlayerList playerList = integratedServer.getPlayerList();
                for (EntityPlayerMP player : playerList.getPlayers()) {
                    if (!player.getName().equals(sender.getName())) {
                        player.connection.disconnect(new TextComponentString("The LAN server has stopped!"));
                    }
                }


                // 2. Stop LAN broadcast
                integratedServer.getNetworkSystem().terminateEndpoints();


                // 3. Using reflection to stop LAN ping thread
                try {
                    Field lanServerPingField = IntegratedServer.class.getDeclaredField("lanServerPing");
                    lanServerPingField.setAccessible(true);
                    Object lanServerPing = lanServerPingField.get(integratedServer);

                    if (lanServerPing != null) {
                        lanServerPing.getClass().getMethod("interrupt").invoke(lanServerPing);
                        lanServerPingField.set(integratedServer, null);
                    }
                } catch (Exception e) {
                    sender.sendMessage(new TextComponentString("Warning: Failed to refresh server list."));
                }


                // 4. Reset LAN status
                integratedServer.setCanSpawnNPCs(false);
                integratedServer.setCanSpawnAnimals(false);

                try {
                    Field isPublicField = IntegratedServer.class.getDeclaredField("isPublic");
                    isPublicField.setAccessible(true);
                    isPublicField.set(integratedServer, false);
                } catch (Exception e) {
                    sender.sendMessage(new TextComponentString("Warning: Could not fully reset LAN status."));
                }


                // 5. Send message to host
                sender.sendMessage(new TextComponentString("SP LAN server has been closed!"));
            } else {
                sender.sendMessage(new TextComponentString("LAN Server is not enabled."));
            }
        } else {
            sender.sendMessage(new TextComponentString("This command can only be used in a single-player world with LAN open."));
        }
    }

    // Command perms
    private boolean hasOwnerPerms(ICommandSender sender) {
        boolean isWorldHost = sender.getName().equals(Minecraft.getMinecraft().getIntegratedServer().getServerOwner());
        return isWorldHost;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4; // This ensures only OP players can attempt the command
    }


}

