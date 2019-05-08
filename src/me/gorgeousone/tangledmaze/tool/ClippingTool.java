package me.gorgeousone.tangledmaze.tool;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.core.Renderer;
import me.gorgeousone.tangledmaze.shape.Shape;
import me.gorgeousone.tangledmaze.util.MazePoint;
import me.gorgeousone.tangledmaze.util.Utils;

public class ClippingTool extends Tool {
	
	private Shape shape;
	
	private Clip clip;
	private ArrayList<MazePoint> vertices;
	
	private boolean isComplete, isResizing;
	private int indexOfResizedVertex;
	
	public ClippingTool(World world, Shape type) {
		super(null);
		
		clip = new Clip(world);
		shape = type;
		vertices = new ArrayList<>();
	}
	
	public ClippingTool(Player builder, Shape type) {
		super(builder);
		
		clip = new Clip(builder.getWorld()); 
		shape = type;
		vertices = new ArrayList<>();
	}
	
	@Override
	public String getName() {
		return shape.getClass().getSimpleName().toLowerCase();
	}
	
	public World getWorld() {
		return clip.getWorld();
	}
	
	public Shape getType() {
		return shape;
	}

	public boolean isStarted() {
		return !vertices.isEmpty();
	}

	public boolean isComplete() {
		return isComplete;
	}
	
	public Clip getClip() {
		return clip;
	}
	
	public void setType(Shape shape) {
		
		Renderer.hideClipboard(this, true);
		this.shape = shape;

		if(isComplete) {

			vertices.remove(3);
			vertices.remove(1);
			calculateShape();
		}
		
		Renderer.showClipboard(this);
	}
	
	@Override
	public void interact(Block clicked, Action interaction) {
		
		if(clicked.getWorld() != getWorld()) {
			reset();
		}
		
		if(vertices.size() < shape.getVertexCount()-1) {
			vertices.add(Utils.nearestSurface(clicked.getLocation()));
			
		}else if(vertices.size() == shape.getVertexCount()-1) {

			vertices.add(Utils.nearestSurface(clicked.getLocation()));
			calculateShape();
			
		}else {

			if(isResizing) {
				resizeShape(clicked);
			
			}else if(isVertex(clicked)) {

				indexOfResizedVertex = indexOfVertex(clicked);
				isResizing = true;
				return;
				
			}else {

				Renderer.hideClipboard(this, true);
				reset();
				vertices.add(Utils.nearestSurface(clicked.getLocation()));
			}
		}
		
		Renderer.showClipboard(this);
	}
	
	private void calculateShape() {
		
		clip = shape.createClip(vertices);
		isComplete = true;
		isResizing = false;
	}
	
	private void resizeShape(Block b) {
		
		Renderer.hideClipboard(this, true);
		MazePoint oppositeVertex = vertices.get((indexOfResizedVertex+2) % 4);
		
		vertices.clear();
		vertices.add(oppositeVertex);
		vertices.add(Utils.nearestSurface(b.getLocation()));
		
		calculateShape();
	}
	
	public void reset() {
		
		Renderer.hideClipboard(this, true);
		
		clip = new Clip(getPlayer() != null ? getPlayer().getWorld() : getWorld());
		vertices.clear();
		isComplete = false;
		isResizing = false;
	}
	
	public ArrayList<MazePoint> getVertices() {
		return vertices;
	}
	
	public boolean isVertex(Block block) {
		
		for(MazePoint vertex : vertices) {
			
			if(vertex.equals(block.getLocation())) {
				return true;
			}
		}
		
		return false;
	}
	
	public int indexOfVertex(Block block) {
		
		if(!isComplete() || !block.getWorld().equals(getWorld()))
			return -1;
		
		for(Location vertex : vertices) {
			if(block.getX() == vertex.getX() &&
			   block.getZ() == vertex.getZ())
				return vertices.indexOf(vertex);
		}
		
		return -1;
	}

	public boolean isBorderBlock(Block block) {
		
		if(!isComplete() ||!block.getWorld().equals(getWorld()))
			return false;
		
		MazePoint point = new MazePoint(block.getLocation());
		
		if(!getClip().borderContains(point))
			return false;
		
		for(MazePoint borderPoint : getClip().getBorder()) {
			if(borderPoint.equals(point) && borderPoint.getBlockY() == point.getBlockY())
				return true;
		}
		
		return false;
	}
	
	public Block updateHeight(Block block) {
		
		MazePoint updatedPoint = Utils.nearestSurface(block.getLocation());
		
		if(!getClip().removeFilling(updatedPoint))
			return block;
		
		getClip().addFilling(updatedPoint);
		
		if(getClip().removeBorder(updatedPoint))
			getClip().addBorder(updatedPoint);

		return updatedPoint.getBlock();
	}
}