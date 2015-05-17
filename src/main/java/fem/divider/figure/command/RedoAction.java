/*
 * Created on 29/6/2006
 */
package fem.divider.figure.command;

import java.awt.event.*;
import javax.swing.*;

import fem.divider.Messages;

/**
 * Action that undoes last command on figure.
 * CommandStack should provide information for this class
 * @author Paul
 */
public class RedoAction extends AbstractAction {

	public RedoAction()
	{
		super(Messages.getString("RedoAction.Redo_1"),  //$NON-NLS-1$
			new ImageIcon(fem.divider.Divider.class.getResource("resources/images/stock_redo.png")) //$NON-NLS-1$
			);
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("RedoAction.Redo_3")); //$NON-NLS-1$
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if(commandStack==null) return;
		commandStack.redo();
	}

	public CommandStack getCommandStack() {
		return commandStack;
	}


	public void setCommandStack(CommandStack stack) {
		if(commandStack!=null)//remove references from old stack
			{commandStack.setRedoAction(null);}
		commandStack = stack;
		commandStack.setRedoAction(this);
		update();
	}

	/**
	 *Should be called by commandStack when something changes
	 */
	public void update()
	{
		if(commandStack==null) return;
		
		if(!commandStack.canRedo())
			{setEnabled(false); return;}
			else
			{
				setEnabled(true);
				putValue(Action.SHORT_DESCRIPTION, 
						Messages.getString("RedoAction.Redo_1")+" ("+commandStack.getRedoName()+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				putValue(Action.NAME, 
						Messages.getString("RedoAction.Redo_1")+" ("+commandStack.getRedoName()+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
	}
	
	private CommandStack commandStack=null;
}//end of class UndoAction
