/*
 * AbstractPanel.java
 *
 * Created on Saturday, 19, March 2005, 12:58
 */

package fem.divider;

import java.awt.*;
import java.awt.event.*;
//import java.awt.geom.*;
import javax.swing.*;

/**
 *
 * @author  gefox
 */
public abstract class AbstractPanel extends javax.swing.JPanel 
		implements AreaListener, MouseListener
{
		
		/** Creates new form AbstractPanel */
    public AbstractPanel(RectangleArea world_) {
//        initComponents();
				drawingPanel = new JPanel()
				{
						public void paintComponent(Graphics g)
						{
								super.paintComponent(g);
								Graphics2D g2 =(Graphics2D)g;
								//Possibly, we need to draw axes
								if(xAxis||yAxis) 
								{
										Stroke defaultStroke = g2.getStroke();
										float[] dashPattern = {5, 10};
										g2.setStroke(
												new BasicStroke(1.0F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0F, dashPattern, 0F)
										);
										if(xAxis)
												g.drawLine(0, ysi(0), drawingPanel.getWidth(), ysi(0));
										if(yAxis)
												g.drawLine(xsi(0), 0, xsi(0), drawingPanel.getHeight());
										g2.setStroke(defaultStroke);
								};
								//Call polymorphic render method
								draw( g2 );
						}
				};
				drawingPanel.setPreferredSize( 
								new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT)
								);
				drawingPanel.addMouseListener(this);
				drawingPanel.setBackground(new Color(0xFF, 0xFF, 0xFF)); //background is white
				scrollPane = new JScrollPane(drawingPanel);
				setLayout(new BorderLayout());
				add(scrollPane, BorderLayout.CENTER);
				setWorld( world_);
//				System.out.println(world.bottom+":"+world.top);
//				System.out.println(drawingPanel.getWidth()+":"+drawingPanel.getHeight());
    }
		
		public void setXAxis(boolean on)
		{
				xAxis = on;
		}
		
		public void setYAxis(boolean on)
		{
				yAxis = on;
		}
		
		/**
		 * @return relation of panel width to world width
		 */
		private double kx()
		{
				int width = drawingPanel.getWidth();
				return (double)width/world.getWidth();
		}

		/**
		 * @return relation of panel height to world height
		 */		
		private double ky()
		{
				int height = drawingPanel.getHeight();
				return (double)height/world.getHeight();
		}
		
		private double min_k()
		{
				return Math.min(kx(), ky());
		}
		
		private double max_k()
		{
				return Math.max(kx(), ky());
		}
		//world x-> screen x
		public double xs(double xw)
		{
//				return kx()*(xw - world.getLeft());
				return min_k()*(xw - world.getLeft());
		}

		public int xsi(double xw)
		{
				return (int)(xs(xw));
		}
		
		//world y-> screen y
		public double ys(double yw)
		{
//				return ky()*( world.getTop() - yw );
				return min_k()*( world.getTop() - yw );
		}
		
		public int ysi(double yw)
		{
				return (int)(ys(yw));
		}
		
		//world width -> screen width
		public double ws(double ww)
		{
//				int width = drawingPanel.getWidth();
//				double kx = world.getWidth()/(double)width;
				
//				return ww/kx();
				return ww*min_k();
		}

		public int wsi(double ww)
		{
				return (int)(ws(ww));
		}
		
		//world height -> screen height
		public double hs(double hw)
		{
//				int height = drawingPanel.getHeight();
//				double ky = world.getHeight()/(double)height;
				
//				return hw/ky();
				return hw*min_k();
		}
		
		public int hsi(double hw)
		{
				return(int)(hs(hw));
		}
		
		
		//screen x -> world x
		public double xw(int xsi)
		{
				return xw((double)xsi);
		}
		
		public double xw(double xs)
		{
//				int width = drawingPanel.getWidth();
//				double kx = world.getWidth()/(double)width;
				
//				return xs/kx()+world.getLeft();
				return xs/min_k()+world.getLeft();
		}
		
		//screen y -> world y
		public double yw(int ysi)
		{
				return yw((double)ysi);
		}
		
		public double yw(double ys)
		{
//				int height = drawingPanel.getHeight();
//				double ky = world.getHeight()/(double)height;
				
//				return world.getTop()-ys/ky();
				return world.getTop()-ys/min_k();
		}
		
		//screen width -> world width
		public double ww(int ws)
		{
				return ww((double)ws);
		}
		
		public double ww(double ws)
		{
//				int width = drawingPanel.getWidth();
//				double kx = world.getWidth()/(double)width;
				
//				return ws/kx();
				return ws/min_k();
		}
		
		//screen height -> world height
		public double hw(int hs)
		{
				return hw((double)hs);
		}
		
		public double hw(double hs)
		{
//				int height = drawingPanel.getHeight();
//				double ky = world.getHeight()/(double)height;
				
//				return hw/ky();
				return hs/min_k();
		}
			
		public RectangleArea getWorld()
		{
				return world;
		}
		
		public void setWorld(RectangleArea world_)
		{
				if(world!=null) world.removeListener(this);
				world = world_;
				world.addListener(this);
				onAreaChanged(world_);
		}		
		
		public void onAreaChanged(AbstractArea area_)
		{
				//we have to fix dimensions of drawingPanel
				//to maintain world aspect ratio
				Dimension dim = drawingPanel.getPreferredSize();
				double screen_width = dim.getWidth();
				double world_width = world.getRight() - world.getLeft();
				double world_height = world.getTop() - world.getBottom();
				double k = world_height / world_width;
//				System.out.println("width="+world_width);
//				System.out.println("height="+world_height);
				dim.setSize(screen_width, screen_width*k);
				drawingPanel.setPreferredSize(dim);
				redraw();
		}
		
		abstract public void draw(Graphics2D g2);

		public void redraw(RectangleArea area_)
		{
				drawingPanel.repaint( 
						xsi(area_.getLeft()), ysi(area_.getTop()),
						wsi(area_.getRight()-area_.getLeft()), hsi(area_.getTop()-area_.getBottom())
						);
		}
		
		/**Redraw figure on panel
		 */
		public void redraw()
		{
				drawingPanel.repaint();
		}
		
		
		public void zoomIn()
		{
				Dimension d = drawingPanel.getPreferredSize();
				d.setSize( d.getWidth()*1.2, d.getHeight()*1.2);
				drawingPanel.setPreferredSize(d);
				scrollPane.getViewport().revalidate();
		}

		public void zoomOut()
		{
				Dimension d = drawingPanel.getPreferredSize();		
				d.setSize( d.getWidth()/1.2, d.getHeight()/1.2);
				drawingPanel.setPreferredSize(d);
				scrollPane.getViewport().revalidate();
		}
		
		
    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {}

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {}

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {}
		
    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {}
		
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
		private void initComponents() {//GEN-BEGIN:initComponents
				
				setLayout(new java.awt.BorderLayout());
				
		}//GEN-END:initComponents


		// Variables declaration - do not modify//GEN-BEGIN:variables
		// End of variables declaration//GEN-END:variables

		//World dimensions
		protected RectangleArea world=null;
		
		protected JPanel drawingPanel;
		protected JScrollPane scrollPane;

		private boolean xAxis = true;
		private boolean yAxis = true;
		
		public static final int DEFAULT_WIDTH = 400;
		public static final int DEFAULT_HEIGHT = 300;

		public static final int HANDLE_WIDTH = 3;
}
