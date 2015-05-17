/*
 * Node.java
 *
 * Created on Saturday, 19, March 2005, 13:53
 */

package fem.divider.figure;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import fem.divider.*;
import fem.geometry.Dot;
import fem.geometry.DotMaterial;


/**
 *
 * @author gefox
 * @author Nikolay Konovalow (add _material_ to dots)
 * @version 
 */
public class Node extends Dot// implements ContourComponent
{
   private Color cFigure = new Color(0, 119, 0);
   private Color cAir = new Color(43, 127, 176);
   
	/** Creates new Node 
	 *DON'T FORGET TO setContour AFTER NODE WAS CREATED!
	 */
	public Node(double x_, double y_) {
		super(x_, y_);
		popupMenu = new JPopupMenu();
	}
	public Node(double x_, double y_, DotMaterial m_) {
	   super(x_, y_, m_);
	   popupMenu = new JPopupMenu();
	}
				
	
	public void setContour(Contour contour_)
	{				
			contour = contour_;
			fillPopupMenu();
			if(nextSegment!=null) nextSegment.updateContour();
	}

	public Contour getContour() {
		return contour;
	}
	
	private void fillPopupMenu()
	{
			popupMenu.removeAll();
			popupMenu.add(editNodeAction);
			popupMenu.add(deleteNodeAction);
			popupMenu.addSeparator();
			if(nextSegment!=null) nextSegment.joinToMenu(popupMenu);
			popupMenu.addSeparator();
			if(contour!=null) contour.joinToMenu(popupMenu);
	}
	
	/**
	 *Create a node and open properties editing dialog
	 *@returns true on "ok" button, false on "cancel"
	 */
	static public Node interactiveCreate()
	{
			Node node = new Node(0,0);
			if(!node.edit())	return null;
			return node;
	}
	
	public void draw(Graphics2D graphics)
	{
		if( this.material == DotMaterial.AIR )
			graphics.setPaint(cAir);
		else
		   graphics.setPaint(cFigure);
		
		FigurePanel panel = contour.figure.panel;
		graphics.drawOval(panel.xsi(x)-2, panel.ysi(y)-2, 4, 4);
		if(nextSegment!=null)
				nextSegment.draw(graphics);
	}

	public boolean edit()
	{
			return dialog.run(this);
	}
	
	public void setNextSegment(Segment s)
	{
			nextSegment = s;
			nextSegment.updateContour();
			fillPopupMenu();
	}

	public void unsetNextSegment()
	{
			nextSegment = null;
	}
	
	public Segment getNextSegment()
	{
			return nextSegment;
	}
	
	public Node getNextNode()
	{
		return nextSegment.getEnd();
	}

	public Segment getPrevSegment()
	{
			return prevSegment;
	}

	public void setPrevSegment(Segment s)
	{
			prevSegment = s;
	}

	public void unsetPrevSegment()
	{
			prevSegment = null;
	}
	
	public double angle()
	{
			Node n1, n2;
			n1=contour.previous(this);
			n2=contour.next(this);
			return angle(n1, n2);
	}
	
	public Node getThisNode() {
		return this;
	}
	
	static NodeEditDialog dialog = new NodeEditDialog();		
	
	private Segment prevSegment = null;
	private Segment nextSegment = null;
	Contour contour;
	JPopupMenu popupMenu;
	Node thisNode = this;

	Action editNodeAction = new AbstractAction(Messages.getString("Node.Edit_Node_1"), //$NON-NLS-1$
	new ImageIcon(Divider.class.getResource("resources/images/editNode.png")) //$NON-NLS-1$
	)
	{
			public void actionPerformed(ActionEvent event)
			{
//						if(edit())
					if(contour.figure.getCommandStack().doNewCommand(
							new fem.divider.figure.command.EditNodeCommand(contour.figure, getThisNode() )))
						{				
								contour.figure.panel.redraw();
						}
				}
		};

		Action deleteNodeAction = new AbstractAction(Messages.getString("Node.Delete_Node_3"), //$NON-NLS-1$
		new ImageIcon(Divider.class.getResource("resources/images/deleteNode.png")) //$NON-NLS-1$
		)
		{
				public void actionPerformed(ActionEvent event)
				{
//						contour.deleteNode(thisNode);
					if(contour.figure.getCommandStack().doNewCommand(
						new fem.divider.figure.command.DeleteNodeCommand(contour.figure, getThisNode())) )
							contour.figure.panel.redraw();
				}
		};
}




class NodeEditDialog extends AbstractEditingDialog
{
   JTextField xField;
	JTextField yField;
	JComboBox<String> materialField;
	Node node;
	static final String[] material_items = {
         "Air", 
         "Figure"
   };
   
	NodeEditDialog() {
		super( Messages.getString("Node.Edit_Node_5")); //$NON-NLS-1$
		JLabel l1 = new JLabel("x="); //$NON-NLS-1$
		JLabel l2 = new JLabel("y="); //$NON-NLS-1$
		JLabel l3 = new JLabel("Material: "); 

		xField = new JTextField(15);
		yField = new JTextField(15);
		materialField = new JComboBox<String>(material_items);
		
		xField.setMaximumSize( xField.getPreferredSize() );
		yField.setMaximumSize( yField.getPreferredSize() );
		materialField.setMaximumSize( materialField.getPreferredSize() );
		
		contentPanel.setLayout( new BoxLayout(contentPanel, BoxLayout.Y_AXIS) );
	//	Box b0 = Box.createHorizontalBox();
		Box b1 = Box.createHorizontalBox();
		Box b2 = Box.createHorizontalBox();
		Box b3 = Box.createHorizontalBox();
		
		b1.add(l1); b1.add(xField);
		b2.add(l2); b2.add(yField);
		b3.add(l3); b3.add(materialField);
		
		contentPanel.add(b1);
		contentPanel.add(b2);
		contentPanel.add(b3);
		contentPanel.add( Box.createGlue() );
		
		setSize(250, 160);
	}
		
	protected String onOk()	{
//		contour.setPositive(positiveCheckBox.isSelected());
		double x;
		double y;
		DotMaterial material;
		
		try {
			x = Double.parseDouble( xField.getText() );
		} catch(NumberFormatException e) {
			return xField.getText()+Messages.getString("Node._is_not_a_valid_floating-point_number_8"); //$NON-NLS-1$
		}

		try {
			y = Double.parseDouble( yField.getText() );
		} catch(NumberFormatException e) {
			return yField.getText()+Messages.getString("Node._is_not_a_valid_floating-point_number_9"); //$NON-NLS-1$
		}
		
		try {
			String selectedMaterial = (String)materialField.getSelectedItem();
			material = DotMaterial.valueOf(DotMaterial.class, selectedMaterial.toUpperCase() );  // Enum's values is in upper case: AIR, FUGURE, ...
		} catch( IllegalArgumentException iae ) {
			return iae.getMessage() + " " + Messages.getString("Node._is_not_a_valid_material_10");
		}
/*
if( !nod.panel.getWorld().isInside(x,y) )
		{
				return "("+x+";"+y+") is outside of world limits.";
		}
*/
		node.setCoordinates(x, y);
		node.setMaterial(material);
		
		return null;
	}
	 
	boolean run(Node node_)
	{
		node = node_;
		xField.setText( Double.toString(node.x) );
		yField.setText( Double.toString(node.y) );
		materialField.setSelectedItem( material_items[node.material.getValue()] ); 
		setVisible(true);
		return result;
	}
}
