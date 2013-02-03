/*
 * Created on 29/6/2006
 */
package divider.figure.command;

import java.awt.event.*;
import javax.swing.*;
import divider.Messages;

/**
 * Action that undoes last command on figure.
 * CommandStack should provide information for this class
 * @author Paul
 */
public class UndoAction extends AbstractAction {

	public UndoAction()
	{
		super(Messages.getString("UndoAction.Undo_1"),  //$NON-NLS-1$
			new ImageIcon(divider.Divider.class.getResource("resources/images/stock_undo.png")) //$NON-NLS-1$
			);
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("UndoAction.Undo_3")); //$NON-NLS-1$
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if(commandStack==null) return;
		commandStack.undo();
	}

	public CommandStack getCommandStack() {
		return commandStack;
	}


	public void setCommandStack(CommandStack stack) {
		if(commandStack!=null)//remove references from old stack
			{commandStack.setUndoAction(null);}
		commandStack = stack;
		commandStack.setUndoAction(this);
		update();
	}

	/**
	 *Should be called by commandStack when something changes
	 */
	public void update()
	{
		if(commandStack==null) return;
		
		if(!commandStack.canUndo())
			{setEnabled(false); return;}
			else
			{
				setEnabled(true);
				putValue(Action.SHORT_DESCRIPTION, 
						Messages.getString("UndoAction.Undo_1")+" ("+commandStack.getUndoName()+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				putValue(Action.NAME, 
						Messages.getString("UndoAction.Undo_1")+" ("+commandStack.getUndoName()+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
	}
	
	private CommandStack commandStack=null;
}//end of class UndoAction
