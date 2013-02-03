/*
 * Created on 25/6/2006
 */
package divider.figure.command;

import divider.figure.*;
import divider.Messages;

/**
 * @author gefox
 */
public class EditNodeCommand extends AbstractCommand {

	/**
	 * @param _figure
	 */
	public EditNodeCommand(Figure _figure, Node _node) {
		super(_figure);
		node=_node;
	}

	boolean execute()
	{
		if(!super.execute()) return false;
		x_stateBefore=node.getX();
		y_stateBefore=node.getY();
		if(node.edit())
				{done=true; 
					x_stateAfter=node.getX();
					y_stateAfter=node.getY();
					return true;} 
			else
				return false;	}
	
	private Node node;

	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#name()
	 */
	public String getName() {
		return Messages.getString("EditNodeCommand.edit_node_1"); //$NON-NLS-1$
	}

	public boolean isUndoable() {return true;}
	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#undo()
	 */
	void undo() {
		node.setX(x_stateBefore);		
		node.setY(y_stateBefore);
		figure.redraw();		
	}

	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#redo()
	 */
	void redo() {
		node.setX(x_stateAfter);		
		node.setY(y_stateAfter);
		figure.redraw();		
	}
	
	private double x_stateBefore;
	private double y_stateBefore;
	private double x_stateAfter;
	private double y_stateAfter;
}
