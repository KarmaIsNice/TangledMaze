package me.gorgeousone.tangledmaze.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.Constants;

public class SetPathWidth {

	public void execute(Player p, String arg0) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		int pathWidth = 0;
		
		try {
			pathWidth = Integer.parseInt(arg0);
		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "\"" + arg0 + "\" is not an integer.");
			return;
		}
		
		if(pathWidth < 1) {
			p.sendMessage(ChatColor.RED + "A path cannot be thinner than 1 block.");
			return;
		}
		
		if(pathWidth > Constants.MAX_PATH_WIDTH) {
			p.sendMessage(Constants.prefix
					+ "Grandma still wants to cross the path on her own. "
					+ "There will not always be a handsome person like you around to help her. "
					+ "The path width is limited to " + Constants.MAX_PATH_WIDTH + " blocks.");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		
		if(maze.getPathWidth() != pathWidth) {
			maze.setPathWidth(pathWidth);
			p.sendMessage(Constants.prefix + "Set path width to " + pathWidth + ".");
		}
	}
}