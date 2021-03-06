package me.gorgeousone.cmdframework.handlers;

import me.gorgeousone.cmdframework.command.BasicCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class CommandHandler implements CommandExecutor {
	
	private JavaPlugin plugin;
	private Set<BasicCommand> commands;
	private CommandCompleter cmdCompleter;
	
	public CommandHandler(JavaPlugin plugin) {
		this.plugin = plugin;
		this.commands = new HashSet<>();
		this.cmdCompleter = new CommandCompleter(this);
	}
	
	public void registerCommand(BasicCommand command) {
		commands.add(command);
		plugin.getCommand(command.getName()).setExecutor(this);
		plugin.getCommand(command.getName()).setTabCompleter(cmdCompleter);
	}
	
	public Set<BasicCommand> getCommands() {
		return commands;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		String cmdName = cmd.getName();
		
		for (BasicCommand command : commands) {
			
			if (command.matches(cmdName)) {
				command.execute(sender, args);
				return true;
			}
		}
		return false;
	}
}