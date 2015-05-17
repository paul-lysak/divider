/*
 * Created on 25/6/2006
 */
package fem.divider.figure.command;

import fem.divider.Messages;
import fem.divider.figure.*;

/**
 * @author gefox
 */
public class DeleteNodeCommand extends AbstractCommand {

	/**
	 * @param _figure
	 */
	public DeleteNodeCommand(Figure _figure, Node _node) {
		super(_figure);
		node=_node;
	}

	boolean execute()
	{
		node.getContour().deleteNode(node);
		return true;
	}

	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#name()
	 */
	public String getName() {
		return Messages.getString("DeleteNodeCommand.delete_node_1"); //$NON-NLS-1$
	}

	public boolean isUndoable() {return true;}
	
	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#undo()
	 */
	void undo() {
		Contour contour=node.getContour();
		if( ! figure.haveContour(contour) )
			figure.addContour( contour );
		contour.restoreNode( node );
		figure.redraw();
	}

	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#redo()
	 */
	void redo() {
		node.getContour().deleteNode(node);
		figure.redraw();		
	}

	private Node node;

	//deleted node is stored here  
	//implicitly also next segment and next node are stored here 	
//	private Node node_stateBefore;
}
