/** Some code, that provide generation of air frame  
 * Created on 10/5/2015
 * @author Nikolay Konovalow
 */
package fem.divider.figure.command;

import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fem.divider.AbstractEditingDialog;
import fem.divider.DividerUI;
import fem.divider.Messages;
import fem.divider.RectangleArea;
import fem.divider.figure.*;
import fem.geometry.DotMaterial;


/** 
* Provide functionality for generating air frame.
*/
public class CreateAirFrameCommand extends AbstractCommand {
   final DividerUI ui;
	public CreateAirFrameCommand(Figure _figure, final DividerUI _ui) {
		super(_figure);
		ui = _ui;
	}

	/**
	 * This method invoke dialog and generate air frame by your preferences.
	 * @returns true if action was done, and command may be placed in stack,
	 *		 false if nothing was done and command should not be placed in stack 
	 */
	boolean execute(){
	   CreateAirFrameDialog dialog = new CreateAirFrameDialog(figure, ui);
	   
	   if( dialog.getCountOfFrameDots() == 0 ){
	      ui.setStatusbarText("Can't generate air frame: programm have no dots for it");
	      return false;
	   }

	   // NOTICE: if you get 'open-ended' contour - verify 'onOk' method of 'CreateAirFrameDialog'
	   Contour contour = new Contour(figure);
	   for( Double[] dot : dialog.getFrameDots() ){
	      contour.addToEnd( new Node(dot[0], dot[1], DotMaterial.AIR) );
	   }
	   figure.addContour(contour);
	   figure.redraw();
	   contour_stateAfter=contour;
	   
      return done;
	}
   @Override
   void undo() {
      figure.deleteContour(contour_stateAfter);
      figure.redraw();
   }

   @Override
   void redo() {
      figure.addContour(contour_stateAfter);    
      figure.redraw();
   }

   @Override
   public String getName() {
      return "Crete air frame";
   }
   
   /** Special variable that provide one-step undo/redo */
   private Contour contour_stateAfter;
}

/** Provide customization of air frame generator 
 * Now is only one pattern for generator: 'boxLike' - user chose two points and program build rectangle on it
 * Pattern 'scaledBoundary' isn't done 
 */
class CreateAirFrameDialog extends AbstractEditingDialog { 
   private Figure figure;
   private DividerUI ui;
   /** Array of coordinates that describe air frame. 
    * Be precise for sequence order */
   private ArrayList<Double[]> dots;
   
   public CreateAirFrameDialog(Figure _figure, DividerUI _ui) {
      super("Create air frame");
      figure = _figure;
      ui = _ui;
      initUI();
      dots = null;
      setSize(250, 160);
      setVisible(true);
   }

   @Override
   protected String onOk() {
      // Box-like frame
      if( wayForGenerating.getSelectedIndex() == 0 ) {
         dots = new ArrayList<Double[]>(4);
         double x1, x2, y1, y2;
         
         // Try read coordinates of first button
         try {
            x1 = Double.parseDouble( firstX.getText() );
         } catch(NumberFormatException e) {
            return firstX.getText()+Messages.getString("Node._is_not_a_valid_floating-point_number_8");
         }
         try {
            y1 = Double.parseDouble( firstY.getText() );
         } catch(NumberFormatException e) {
            return firstY.getText()+Messages.getString("Node._is_not_a_valid_floating-point_number_9");
         }
         // Try read coordinates of second button
         try {
            x2 = Double.parseDouble( secondX.getText() );
         } catch(NumberFormatException e) {
            return secondX.getText()+Messages.getString("Node._is_not_a_valid_floating-point_number_8");
         }
         try {
            y2 = Double.parseDouble( secondY.getText() );
         } catch(NumberFormatException e) {
            return secondY.getText()+Messages.getString("Node._is_not_a_valid_floating-point_number_9");
         }
         
         dots.add( new Double[] {x1, y1} );
         dots.add( new Double[] {x1, y2} );  
         dots.add( new Double[] {x2, y2} );
         dots.add( new Double[] {x2, y1} );         
      } // Scaled-boundary frame
      else {
         
      }

      return null;
   }
   
   public ArrayList<Double[]> getFrameDots(){
      return dots;
   }
   public int getCountOfFrameDots(){
      return dots.size();
   }
   
   private void initUI(){
      wayForGenerating = new JTabbedPane();
      
      // First tab: generate frame as box (by to opposite points)   
      initFirstTab_cornerFields();
      Box b1 = Box.createHorizontalBox();
      b1.add(Box.createHorizontalGlue());      b1.add(new JLabel("x=")); b1.add(firstX);
      b1.add(new JLabel("y=")); b1.add(firstY);
      Box b2 = Box.createHorizontalBox();      b2.add(Box.createHorizontalGlue());
      b2.add(new JLabel("x=")); b2.add(secondX);
      b2.add(new JLabel("y=")); b2.add(secondY);
      boxLike = new JPanel();
      boxLike.setLayout( new BoxLayout(boxLike, BoxLayout.PAGE_AXIS) );
      boxLike.add(new JLabel("Frame's first corner:"));
      boxLike.add(b1);
      boxLike.add(new JLabel("Frame's second corner:"));
      boxLike.add(b2);
      
      // Second tab: generate frame as scaled copy of current boundaries 
      scaledBoundary = new JPanel();
      scaledBoundary.setLayout( new BoxLayout(scaledBoundary, BoxLayout.PAGE_AXIS) );
      scaledBoundary.add(new JLabel("It is incomplete yeut"));

      // Combine tabs
      wayForGenerating.addTab("Box-like", boxLike);
      wayForGenerating.addTab("Scaled boundary", scaledBoundary);
      wayForGenerating.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent e) {
             if( wayForGenerating.getSelectedIndex() == 0 )
                ui.setStatusbarText("Generate frame as box (by to opposite points)");
             else
                ui.setStatusbarText("Generate frame as scaled copy of current boundaries");
         }
      });
      contentPanel.setLayout( new BoxLayout(contentPanel, BoxLayout.Y_AXIS) );
      contentPanel.add(wayForGenerating);
   }
   
   private void initFirstTab_cornerFields(){
      firstX = new JTextField(15);  firstX.setMaximumSize(firstX.getPreferredSize());
      firstY = new JTextField(15);  firstY.setMaximumSize(firstY.getPreferredSize());
      secondX = new JTextField(15); secondX.setMaximumSize(secondX.getPreferredSize());
      secondY = new JTextField(15); secondY.setMaximumSize(secondY.getPreferredSize());
      
      // Default values for air frame
      RectangleArea area = figure.calculateBounds();
      firstX.setText( Double.toString( area.getLeft()  - 10.0 ));
      secondX.setText(Double.toString( area.getRight() + 10.0 ));
      firstY.setText( Double.toString( area.getBottom()- 10.0 ));
      secondY.setText(Double.toString( area.getTop()   + 10.0 ));
   }
   
   private static final long serialVersionUID = 1L;
   private JTabbedPane wayForGenerating;
   private JPanel boxLike;
   private JPanel scaledBoundary;
   private JTextField firstX; 
   private JTextField firstY; 
   private JTextField secondX;
   private JTextField secondY;
}
