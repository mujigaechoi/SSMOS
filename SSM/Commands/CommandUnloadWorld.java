package SSM.Commands;

import SSM.GameManagers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnloadWorld implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if(!commandSender.isOp()) {
            return true;
        }
        if (args.length < 1) {
            commandSender.sendMessage("Must specify a world to load.");
            return true;
        }
        if (!unloadWorld(args[0])) {
            commandSender.sendMessage("Could not find world specified.");
            return true;
        }
        commandSender.sendMessage("World Saved!");
        return true;
    }

    public static boolean unloadWorld(String name) {
        World world = Bukkit.getWorld(name);
        if (world == null) {
            return false;
        }
        for (Player player : world.getPlayers()) {
            player.teleport(GameManager.lobby_world.getSpawnLocation());
        }
        Bukkit.unloadWorld(world, true);
        return true;
    }

}
