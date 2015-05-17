/*
 * Created on 26/6/2006
 */
package fem.divider.figure.command;

import fem.divider.Messages;
import fem.divider.figure.*;

/**
 * @author gefox
 */
public class SplitSegmentCommand extends AbstractCommand {

	/**
	 * @param _figure
	 */
	public SplitSegmentCommand(Figure _figure, Segment _segment) {
		super(_figure);
		segment=_segment;
	}

	boolean execute()
	{
		Node node = Node.interactiveCreate();
		if(node==null) return false;
		segment.getBegin().getContour().addAfter(segment.getBegin(), node);
		newNode_stateAfter = node;
		return true;
	}
	
	private Segment segment;

	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#name()
	 */
	public String getName() {
		return Messages.getString("SplitSegmentCommand.split_segment_1"); //$NON-NLS-1$
	}

	public boolean isUndoable() {return true;}
	
	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#undo()
	 */
	void undo() {
		Contour contour = newNode_stateAfter.getContour();
		contour.deleteNode( newNode_stateAfter );
		figure.redraw();
	}

	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#redo()
	 */
	void redo() {
		Contour contour = newNode_stateAfter.getContour();
		contour.restoreNode( newNode_stateAfter );
		figure.redraw();
	}
	
	private Node newNode_stateAfter;
}
