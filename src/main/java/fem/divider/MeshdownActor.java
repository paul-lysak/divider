/*
 * Created on 9/7/2006
 */
package fem.divider;

import fem.divider.mesh.MethodAbstract;
import fem.divider.mesh.MethodDefault;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class can start thread, in which meshdown runs
 * @author gefox
 */
public class MeshdownActor implements Runnable {

	/**
	 * 
	 */
	public MeshdownActor(Divider divider_) 
	{
		divider = divider_;
		progressDialog = new ProgressDialog( this );
		methods=new MethodAbstract[1];
		methods[0] = MethodDefault.getInstance();
	}

	private void onStart()
	{
		running = true;
		progressDialog.show();
		progressDialog.setProgress(0.0);
		progressTimer.start();
	}
	
	private void onStop()
	{
		progressTimer.stop();
		progressDialog.setVisible( false );
		running = false;
	}
	
	/**
	 * Runs meshdown method. Should be called by start() methods
	 */
	public void run()
	{
		onStart();		
		//do meshdown
		divider.lastMesh = methods[methodIndex].meshdown(divider.figure);
		
		onStop();
						
		if(divider.lastMesh!=null)
			{
				divider.figure.setMesh(divider.lastMesh);
				divider.lastMesh.setPanel( divider.dividerUI.meshPanel );
				divider.dividerUI.meshPanel.setMesh( divider.lastMesh );
				divider.lastMesh.redraw();
				divider.dividerUI.setStatusbarText(Messages.getString("MeshdownActor.Meshdown_complete___1")+ //$NON-NLS-1$
						divider.lastMesh.nElements()+Messages.getString("MeshdownActor._elements_and__2")+ //$NON-NLS-1$
						divider.lastMesh.nNodes()+Messages.getString("MeshdownActor._nodes_3")); //$NON-NLS-1$
			}
			else
				divider.dividerUI.setStatusbarText(Messages.getString("MeshdownActor.Meshdown_failed__empty_figure_4")); //$NON-NLS-1$
	}//end run()
	
	/**
	 * Should be called to start meshdown.
	 */
	public void start()
	{
		String message =methods[methodIndex].test(divider.getFigure());
		if(message!=null)
		{
			JOptionPane.showMessageDialog(null, message,	
				Messages.getString("MeshdownActor.Failed_to_meshdown_1"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			return;
		} 
		runner = new Thread(this);
		runner.start();
	}
	
	/**
	 * Tell about progress
	 *
	 */
	public void notifyProgress()
	{
		progressDialog.setProgress( methods[methodIndex].getProgress() );
	}
	
	public void cancel()
	{
		//System.out.println("cancel");
		if(running) 
		{
			runner.stop();
			onStop();
			divider.dividerUI.switchToFigure();
		} 
	}


	public MethodAbstract getMethod() {
		return methods[methodIndex];
	}

	public boolean isRunning() {
		return running;
	}


	public MethodAbstract[] getMethods() {
		return methods;
	}

	public void setMethods(MethodAbstract[] abstracts) {
		methods = abstracts;
	}

	public int getMethodIndex() {
		return methodIndex;
	}

	public void setMethodIndex(int i) {
		if(i<methods.length) methodIndex = i;
			else methodIndex=methods.length-1;
	}
	

	private MethodAbstract[] methods=null;
	private Divider divider;
	private boolean running=false;
	private int methodIndex=0;
	//private MethodAbstract method=null;
	private ProgressDialog progressDialog;
	private Thread runner;
	
	//wakes up to tell actor about progress
	private Timer progressTimer = new Timer(500, new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {notifyProgress();}
		}
	);


}//end MeshdownActor





/**
 * displays progressbar and cancel button
 * @author gefox
 */
class ProgressDialog extends JFrame
{
	ProgressDialog(MeshdownActor actor_)
	{
		actor = actor_;
		
		progressBar = new JProgressBar(0, 1000);
		progressBar.setValue(0);
		cancelButton = new JButton(Messages.getString("MeshdownActor.Cancel")); //$NON-NLS-1$
		cancelButton.addActionListener(
			new AbstractAction() {
				public void actionPerformed(ActionEvent e) {actor.cancel();}
				}
			);//end cancelButton.setAction
			

		
		this.setTitle(Messages.getString("MeshdownActor.Meshdown_Progress_6")); //$NON-NLS-1$
		Container content = getContentPane();
		content.setLayout( new BorderLayout() );
		
		JPanel buttonPanel = new JPanel();
		content.add( buttonPanel, BorderLayout.SOUTH );
		buttonPanel.add( cancelButton );
		
		JPanel mainPanel = new JPanel();
		Box mainBox =  Box.createVerticalBox();
		content.add( mainPanel, BorderLayout.CENTER );
		mainPanel.add( mainBox );
		mainBox.add( new JLabel(Messages.getString("MeshdownActor.Meshdown_Progress__7")) ); //$NON-NLS-1$
		mainBox.add( progressBar );

//		cancelButton.setText("Hi all!");

		this.setSize(300, 140);
		
/*		content.add(new JLabel("Meshdown Progress:"), BorderLayout.NORTH);
		content.add( progressBar, BorderLayout.CENTER );
		content.add( cancelButton, BorderLayout.SOUTH );
		*/
	}//end ProgressDialog(MeshdownActor actor_)
	
	/**
	 * Set progress
	 * @param p --- progress (0.0 ... 1.0)
	 */
	public void setProgress(double p)
	{
		progressBar.setString( (int)(p*100)+"%" ); //$NON-NLS-1$
		progressBar.setValue( (int)(p*1000) );
	}
	
	private ProgressDialog thisProgressDialog = this;
	private MeshdownActor actor;
	private JProgressBar progressBar;
	private JButton cancelButton;
}
