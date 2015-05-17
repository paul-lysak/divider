/*
 * Created on 26/6/2006
 */
package fem.divider.figure.command;

import java.util.*;

import fem.divider.Messages;
import fem.divider.figure.*;

/**
 * @author gefox
 */
public class EditContactZonesCommand extends AbstractCommand {

	/**
	 * @param _figure
	 */
	public EditContactZonesCommand(Figure _figure, Segment _segment) {
		super(_figure);
		segment=_segment;
	}

	boolean execute()
	{
		czones_stateBefore = segment.copyCZones();
		if( segment.editCZones() )
			{
				czones_stateAfter = segment.copyCZones();
				return true;
			} 
			else
			return false;
	}
	
	private Segment segment;

	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#name()
	 */
	public String getName() {
		return Messages.getString("EditContactZonesCommand.edit_contact_zones_1"); //$NON-NLS-1$
	}

	public boolean isUndoable() {return true;}
	
	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#undo()
	 */
	void undo() {
		segment.setCZones( czones_stateBefore );
		figure.redraw();
	}

	/* (non-Javadoc)
	 * @see divider.figure.command.AbstractCommand#redo()
	 */
	void redo() {
		segment.setCZones( czones_stateAfter );
		figure.redraw();
	}
	
	private ArrayList czones_stateBefore;
	private ArrayList czones_stateAfter;
}
