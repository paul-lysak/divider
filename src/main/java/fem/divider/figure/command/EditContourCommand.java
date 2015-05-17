/*
 * Created on 25/6/2006
 */
package fem.divider.figure.command;

import fem.divider.Messages;
import fem.divider.figure.*;

/**
 * @author gefox
 */
public class EditContourCommand extends AbstractCommand {

	/**
	 * @param _figure --- figure of _contour ,  _contour --- one to be edited
	 */
	public EditContourCommand(Figure _figure, Contour _contour) {
		super(_figure);
		contour=_contour;
	}

	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#execute()
	 */
	boolean execute() {
		positive_stateBefore=contour.isPositive();
		if(!super.execute()) return false;
		if(contour.edit())
				{done=true;
					positive_stateAfter=contour.isPositive();					 
					return true;} 
			else
				return false;
	}

	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#name()
	 */
	public String getName() {
		return Messages.getString("EditContourCommand.edit_contour_1"); //$NON-NLS-1$
	}

	public boolean isUndoable() {return true;}

	void undo() {
		contour.setPositive(positive_stateBefore);
		figure.redraw();
	}
	void redo() {
		contour.setPositive(positive_stateAfter);		
		figure.redraw();
	}
	
	private Contour contour;



	
	private boolean positive_stateBefore;
	private boolean positive_stateAfter;
}
