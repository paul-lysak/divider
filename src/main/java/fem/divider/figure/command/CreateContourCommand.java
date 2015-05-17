/*
 * Created on 25/6/2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package fem.divider.figure.command;

import fem.divider.Messages;
import fem.divider.figure.*;

/**
 * @author gefox
 */
public class CreateContourCommand extends AbstractCommand {

	/**
	 * @param _figure
	 */
	public CreateContourCommand(Figure _figure) {
		super(_figure);
	}

	/* (non-Javadoc)
	 * @see divider.figure.command.Command#execute()
	 */
	boolean execute() {
		if(!super.execute()) return false;
		Contour contour = new Contour(figure);
//		editContour = new EditContourCommand(figure, contour);
//		if(!editContour.execute()) return false;//canceled
		if(!contour.edit()) return false;//canceled
		Node node = Node.interactiveCreate();
		if(node==null) return false;
		contour.addFirstNode(node);
		figure.contours.add(contour);
		done=true;
		contour_stateAfter=contour;
		return true;
	}

	/* (non-Javadoc)
	 * @see divider.figure.command.Command#isUndoable()
	 */
	public boolean isUndoable() {return true;}

	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#name()
	 */
	public String getName() {
		return Messages.getString("CreateContourCommand.create_contour_1"); //$NON-NLS-1$
	}
	 
	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#undo()
	 */
	void undo() {
		figure.contours.remove(contour_stateAfter);
		figure.redraw();
	}

	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#redo()
	 */
	void redo() {
		figure.contours.add(contour_stateAfter);		
		figure.redraw();
	}

	private Contour contour_stateAfter;
}
