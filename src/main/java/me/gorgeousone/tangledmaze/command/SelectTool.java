package me.gorgeousone.tangledmaze.command;

import me.gorgeousone.tangledmaze.clip.ClipShape;
import me.gorgeousone.tangledmaze.handler.ClipToolHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.command.framework.argument.ArgType;
import me.gorgeousone.tangledmaze.command.framework.argument.ArgValue;
import me.gorgeousone.tangledmaze.command.framework.argument.Argument;
import me.gorgeousone.tangledmaze.command.framework.command.ArgCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.tool.*;
import me.gorgeousone.tangledmaze.util.PlaceHolder;

public class SelectTool extends ArgCommand {

	private ClipToolHandler clipHandler;

	public SelectTool(ClipToolHandler clipHandler, MazeCommand parent) {

		super("select", null, true, parent);
		addArg(new Argument("tool", ArgType.STRING, "rect", "circle", "brush", "exit"));

		this.clipHandler = clipHandler;
	}
	
	@Override
	protected boolean onCommand(CommandSender sender, ArgValue[] arguments) {

		Player player = (Player) sender;
		String toolType = arguments[0].getString();

		switch (toolType.toLowerCase()) {

			case "rect":
			case "rectangle":
			case "square":

				if(!clipHandler.setClipShape(player, ClipShape.RECTANGLE))
					return true;
				break;

			case "circle":
			case "ellipse":

				if(!clipHandler.setClipShape(player, ClipShape.ELLIPSE))
					return true;
				break;

			case "brush":

				if(!switchToMazeTool(player, new BrushTool(player)))
					return true;
				break;

			case "exit":
			case "entrance":

				if(!switchToMazeTool(player, new ExitSettingTool(player)))
					return true;
				break;

			default:
				player.sendMessage("/tangledmaze help 6");
				return false;
		}

		Messages.MESSAGE_TOOL_SWITCHED.sendTo(player, new PlaceHolder("tool", ToolHandler.getTool(player).getName()));
		return true;
	}

	private boolean switchToMazeTool(Player player, Tool type) {

		if(ToolHandler.getTool(player).getClass().equals(type.getClass()))
			return false;
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(!maze.isStarted()) {
			Messages.MESSAGE_TOOL_FOR_MAZE_ONLY.sendTo(player);
			player.sendMessage("/tangledmaze start");
			return false;
		}
		
		if(maze.isConstructed()) {
			Messages.ERROR_MAZE_ALREADY_BUILT.sendTo(player);
			return false;
		}
		
		if(ToolHandler.hasClipboard(player)) {
			//TODO make cliphandler handle rendering of cliptool
			ClipTool clipboard = ToolHandler.getClipboard(player);
			Renderer.hideClipboard(clipboard, true);
			clipHandler.removeClipTool(player);
		}
		
		ToolHandler.setTool(player, type);
		return true;
	}
}