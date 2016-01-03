/*
 * Created on 15/3/2006
 */
package fem.divider.figure.command;

/**
 * @author gefox
 */
public abstract class AbstractCommand {

	/**
	 * Constructor of command
	 * _divider has to be given to this constructor
	 */
	public AbstractCommand(fem.divider.figure.Figure _figure)
	{
		figure=_figure;
	}
	

	
	/**
	 * Thsi method is actually called to perform some action.
	 * @returns true if action was done, and command may be placed in stack,
	 *		 false if nothing was done and command should not be placed in stack 
	 */
	boolean execute()
	{
		if(done) return false; else return true;
	}
	
	abstract void undo();
	abstract void redo();
	
	/**
	 * Returns whether this command may be undone
	 */
	public boolean isUndoable()
	{
			return false;
	}
	
	abstract public String getName();
	//divider.Divider divider;
	protected fem.divider.figure.Figure figure;
	protected boolean done=false;
	protected boolean undone=false;
	protected boolean redone=false; 
}
