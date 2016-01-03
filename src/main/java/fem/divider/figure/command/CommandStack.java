/*
 * Created on 16/3/2006
 */
package fem.divider.figure.command;
import java.util.*;
import javax.swing.JOptionPane;

import fem.divider.Messages;


/**
 * Stack of commands, performed by program.
 * Is used for undo/redo capability
 * @author gefox
 */
public class CommandStack {

public CommandStack()
{
}

/**
 * execute() method of cmd is called. If it returns "true", method is added to stack
 * @return true if cmd.execute() returned true
 */
public boolean doNewCommand(AbstractCommand cmd)
{
	if( !cmd.isUndoable() )//if command is NOT undoable
		{int sel=
			JOptionPane.showConfirmDialog(null,
			Messages.getString("CommandStack.Command__1")+cmd.getName()+Messages.getString("CommandStack._is_not_undoable._Really_continue__2"), //$NON-NLS-1$ //$NON-NLS-2$
			Messages.getString("CommandStack.Confirm_command_3"), //$NON-NLS-1$
			JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.WARNING_MESSAGE
			);
			if(sel==JOptionPane.CANCEL_OPTION) return false;
			if(!cmd.execute()) return false;
			commands.clear();
		}
		else//when command IS undoable
		{
			if(!cmd.execute()) return false;
			//need to remove commands from last undone to end of stack
			while( lastUndone.hasNext() )
					{lastUndone.next(); lastUndone.remove();}
			//if there's too much commands, need to remove least recent
			if( commands.size()>=maxCommands ) 
				{commands.removeFirst();}
			commands.add(cmd);
		}
	lastUndone=commands.listIterator(commands.size());
	
	if(undoAction!=null) undoAction.update();
	if(redoAction!=null) redoAction.update();
	return true;
}


void undo()
{
	if(!canUndo()) return;
	AbstractCommand cmd = (AbstractCommand)lastUndone.previous();
	cmd.undo();
	undoAction.update();
	redoAction.update();
}

void redo()
{
	if(!canRedo()) return;
	AbstractCommand cmd = (AbstractCommand)lastUndone.next();
	cmd.redo();
	undoAction.update();
	redoAction.update();
}


public boolean canUndo()
{
	return lastUndone.hasPrevious();
}


public boolean canRedo()
{
	return lastUndone.hasNext();
}

public String getUndoName()
{
	//get command that can be undone
	AbstractCommand command = (AbstractCommand)lastUndone.previous();
	//return iterator to initial state
	lastUndone.next();
	return command.getName();
}

public String getRedoName()
{
	//get command that can be redone
	AbstractCommand command = (AbstractCommand)lastUndone.next();
	//return iterator to initial state
	lastUndone.previous();
	return command.getName();
}


public RedoAction getRedoAction() {
	return redoAction;
}

public UndoAction getUndoAction() {
	return undoAction;
}

public void setRedoAction(RedoAction action) {
	redoAction = action;
}

public void setUndoAction(UndoAction action) {
	undoAction = action;
}

//list of commands, that were performed
private LinkedList  commands=new LinkedList();
private int maxCommands=256; //maximal number of commands
//lastUndone.gefNext returns last undone command or null if there's no undone commands
private ListIterator lastUndone=commands.listIterator();

private UndoAction undoAction=null;
private RedoAction redoAction=null;




}
