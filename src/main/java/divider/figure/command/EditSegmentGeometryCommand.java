/*
 * Created on 26/6/2006
 */
package divider.figure.command;

import java.util.*;
import divider.figure.*;
import divider.Messages;

/**
 * @author gefox
 */
public class EditSegmentGeometryCommand extends AbstractCommand {

	/**
	 * @param _figure
	 */
	public EditSegmentGeometryCommand(Figure _figure, Segment _segment) {
		super(_figure);
		segment = _segment;
	}

	boolean execute() {
		geometryBefore = segment.getGeometry();
		if (segment.editGeometry()) {
			geometryAfter = segment.getGeometry();
			return true;
		} else {
			return false;
		}
	}

	private Segment segment;

	/*
	 * (non-Javadoc)
	 * 
	 * @see divider.figure.command.AbstractCommand#name()
	 */
	public String getName() {
		return Messages
				.getString("EditSegmentGeometryCommand.edit_segment_geometry"); //$NON-NLS-1$
	}

	public boolean isUndoable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see divider.figure.command.AbstractCommand#undo()
	 */
	void undo() {
		// segment.setCZones( czones_stateBefore );
		segment.setGeometry(geometryBefore);
		figure.redraw();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see divider.figure.command.AbstractCommand#redo()
	 */
	void redo() {
		// segment.setCZones( czones_stateAfter );
		segment.setGeometry(geometryAfter);
		figure.redraw();
	}

	private AbstractSegmentGeometry geometryBefore;
	private AbstractSegmentGeometry geometryAfter;
	
	// private ArrayList czones_stateBefore;
	// private ArrayList czones_stateAfter;
}
