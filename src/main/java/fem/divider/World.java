/*
 * World.java
 *
 * Created on Sunday, 27, March 2005, 14:56
 */

package fem.divider;

import javax.swing.*;


/**
 *Adds editing capabilities to RectangleArea.
 *Dialog window can be called through edit method to edit World
 * @author  gefox
 * @version 
 */
public class World extends RectangleArea{

		/** Creates new World */
    public World(double left_, double right_, double bottom_, double top_) 
		{
				super(left_, right_, bottom_, top_);
    }

		public World(RectangleArea area_)
		{
				super(area_);
		}

		public boolean edit()
		{
				boolean result =  dialog.run(this);
				if(result)
						tellChanged();
				return result;
		}
		
		protected static WorldEditingDialog dialog =
				new WorldEditingDialog();

}


class WorldEditingDialog extends AbstractEditingDialog
{

		public WorldEditingDialog()
		{
				super(Messages.getString("World.Edit_world_boundaries_1")); //$NON-NLS-1$
				leftField = new JTextField(15);
				rightField = new JTextField(15);
				topField = new JTextField(15);
				bottomField = new JTextField(15);

				leftField.setMaximumSize( leftField.getPreferredSize() );
				rightField.setMaximumSize( rightField.getPreferredSize() );
				bottomField.setMaximumSize( bottomField.getPreferredSize() );
				topField.setMaximumSize( topField.getPreferredSize() );
				
				contentPanel.setLayout( new BoxLayout(contentPanel, BoxLayout.Y_AXIS) );
				Box tb = Box.createHorizontalBox();
				Box xb = Box.createHorizontalBox();
				Box bb = Box.createHorizontalBox();
				
				tb.add(new JLabel(Messages.getString("World.Top__2"))); //$NON-NLS-1$
				tb.add(topField);
				
				xb.add(new JLabel(Messages.getString("World.Left__3"))); //$NON-NLS-1$
				xb.add(leftField); 
						xb.add(new JLabel(Messages.getString("World.Right__4"))); //$NON-NLS-1$
						xb.add(rightField);
				
				bb.add(new JLabel(Messages.getString("World.Bottom__5"))); //$NON-NLS-1$
				bb.add(bottomField);
				
				contentPanel.add(tb);
				contentPanel.add(xb);
				contentPanel.add(bb);
				contentPanel.add( Box.createGlue());
				
				setSize(340, 130);
		}

		/**
		 * Action, that is performed on "ok"
		 * @returns null, if data, entered by user is good or error message otherwise
		 */
		protected String onOk() {
				result = true;
				double left;
				double right;
				double bottom;
				double top;
				
				try
				{
						left = Double.parseDouble( leftField.getText() );
				}
				catch(NumberFormatException e)
				{
						return leftField.getText()+Messages.getString("World._is_not_a_valid_floating-point_number_6"); //$NON-NLS-1$
				}
				try
				{
						right = Double.parseDouble( rightField.getText() );
				}
				catch(NumberFormatException e)
				{
						return rightField.getText()+Messages.getString("World._is_not_a_valid_floating-point_number_6"); //$NON-NLS-1$
				}
				try
				{
						bottom = Double.parseDouble( bottomField.getText() );
				}
				catch(NumberFormatException e)
				{
						return bottomField.getText()+Messages.getString("World._is_not_a_valid_floating-point_number_6"); //$NON-NLS-1$
				}
				try
				{
						top = Double.parseDouble( topField.getText() );
				}
				catch(NumberFormatException e)
				{
						return topField.getText()+Messages.getString("World._is_not_a_valid_floating-point_number_6"); //$NON-NLS-1$
				}

				world.resize(left, right, bottom, top);
				return null;
		}
		
		public boolean run(World world_)
		{
				result = false;
				world = world_;

				leftField.setText( Double.toString(world.getLeft()));
				rightField.setText( Double.toString(world.getRight()));
				bottomField.setText( Double.toString(world.getBottom()));
				topField.setText( Double.toString(world.getTop()));
				
				show();
				return result;
		}
		
		RectangleArea world;
		boolean result;
		
		JTextField leftField;
		JTextField rightField;
		JTextField bottomField;
		JTextField topField;
}