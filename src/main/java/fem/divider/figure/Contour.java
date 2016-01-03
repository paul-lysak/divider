/*
 * Contour.java
 *
 * Created on Saturday, 19, March 2005, 13:51
 */

package fem.divider.figure;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import fem.divider.*;
import fem.geometry.Dot;

/**
 *
 * @author  gefox
 * @version 
 */
public class Contour {

		/** Creates new Contour */
      public Contour(Figure figure_) {
         figure = figure_;
      }
      
		public void draw(Graphics2D graphics)
		{
				if(positive)
						graphics.setPaint(positiveColor);
				else
						graphics.setPaint(negativeColor);
						
				for(Node node : nodes)
				{
				   node.draw(graphics);
				}
		}
		
		public boolean edit()
		{
				return dialog.run(this);
		}

		public void setPositive(boolean positive_)
		{
				positive = positive_;
		}
		
		public boolean isPositive()
		{
				return positive;
		}
		
	
		public void addToEnd(Node node)
		{
				node.setContour(this);
				Node lastNode;
				Node firstNode;
				if(nodes.size()>0)//if we have something already in contour
				{
						lastNode = (Node)nodes.get(nodes.size() - 1);
						Segment lastSegment = lastNode.getNextSegment();
						lastSegment.setNewEnd(node);//new end on new node
						firstNode = (Node)nodes.get(0);
				}
				else //if there's nothing in contour yet
				{
						firstNode = node;
				}
				Segment segment = new Segment(node, firstNode);
				nodes.add(node);
		}
		
		/**
		 *Simply appends node to the nodes and sets their contour.
		 *Doesn't do any operations with segments.
		 *Intended to be used for opening data from file
		 *Futher setup seems to be done by DefaultSegment constructor
		 */
		public void rawAppend(Node node)
		{
				nodes.add(node);
				node.setContour(this);
		}
		
		/**
		 *When contour is just created and contains no nodes, this method
		 *can be used to add first node to contour.
		 *Also creates segment that begins and ends on this node.
		 *Thsi method intended to be used for interactive editing
		 *@param node --- node to be added
		 */
		public void addFirstNode(Node node)
		{
				Segment segment = new Segment(node, node);
				nodes.add(node);
				node.setContour(this);
		}
	
		/**
		 *Adds 'added' node after 'base' node
		 */
		public void addAfter(Node base, Node added)
		{
				int baseIndex = nodes.indexOf(base);
				Node next;
				if(baseIndex +1 < nodes.size())
						 next = (Node)nodes.get(baseIndex+1);
				else
						next = (Node)nodes.get(0);
				
				Segment newSegment = new Segment(added, next);
				Segment oldSegment = base.getNextSegment();
				oldSegment.setNewEnd(added);

				nodes.add(baseIndex+1, added);
				added.setContour(this);
		}
		
		/**
		 *Removes given node and folowing segment
		 *(node retains all the links it had)
		 *Spreads previous segment
		 */
		public void deleteNode(Node node)
		{
				int baseIndex = nodes.indexOf(node);
				if(nodes.size()==1) //if it's only one node in the contour
				{
						nodes.remove(baseIndex);
						figure.deleteContour(this);
						return;
				}
				//if there's more then one 
				//node.unsetNextSegment();
				Node prevNode;
				if(baseIndex>0)
						prevNode = (Node)nodes.get(baseIndex -1);//really previous
				else
						prevNode = (Node)nodes.get( nodes.size()-1 );//no previous, use the last
				
				Node nextNode;
				if(baseIndex<nodes.size()-1)
						nextNode = (Node)nodes.get(baseIndex+1);//really next
				else
						nextNode = (Node)nodes.get(0);//no next, use first
				
				//change end of previous segment
				prevNode.getNextSegment().setNewEnd(nextNode);
				//forget given node
				nodes.remove(node);
		}
		
		/**
		 * Inserts node in contour, using node.nextSegment.end to determine
		 * where to insert
		 * @param node
		 * @return true on success
		 */
		public boolean restoreNode(Node node)
		{
			if(nodes.size()==0) //if list is empty, add node and return
				{
					nodes.add(node);
					return true;
				}
			Node nextNode = node.getNextSegment().getEnd();
			int nextIndex = nodes.indexOf(nextNode);
			if( nextIndex==-1 ) return false; //next node not found
			int prevIndex;			
			if(nextIndex==0)
				prevIndex=nodes.size()-1;
			else
				prevIndex=nextIndex-1;
			Node prevNode =(Node) nodes.get(prevIndex);
				
			Segment oldSegment = prevNode.getNextSegment();
			oldSegment.setNewEnd(node);

			//node is placed to where next node was
			nodes.add(nextIndex, node);
			return true; 
		}
		
		public Node previous(Node node)
		{
				int i = nodes.indexOf(node);
				if(i==0) i=nodes.size()-1; else i--;
				return (Node)nodes.get(i);
		}

		public Node next(Node node)
		{
				int i = nodes.indexOf(node);
				if(i==nodes.size()-1) i=0; else i++;
				return (Node)nodes.get(i);
		}
		
		
		/**
		 *Add contous menu items to given popupMenu
		 */
		void joinToMenu(JPopupMenu popupMenu)
		{
				popupMenu.add(editContourAction);
		}
		
		public Segment findSegment(double x_, double y_, double handle_)
		{
				Node node;
				Segment segment;
				double distance;
				for(Iterator i=nodes.iterator(); i.hasNext(); )
				{
						node = ((Node)i.next());
						segment = node.getNextSegment();
						if(segment==null) continue;
						if(segment.distance(x_, y_) < handle_) return segment;
				}
				return null;
		}
		
		public RectangleArea calculateBounds()
		{
				RectangleArea area = ((Node)nodes.get(0)).getNextSegment().calculateBounds();
				Iterator i=nodes.iterator();
				i.next();
				for(; i.hasNext(); )
				{
						area.include( ((Node)i.next()).getNextSegment().calculateBounds() );
				}
				
				return area;
		}
		
		/**
		 *Determines, whether dot is inside the contour or not
		 *TODO: inreliable algorithm. If node is ouside but close 
		 *enough to the contour, it will tell that dot is inside
		 */
		public boolean isInside(Dot dot)
		{
				Node node;
				Segment segment;
				int nIntersections=0;
				for(int i=0; i<nodes.size(); i++)
				{
						node = (Node)nodes.get(i);
						segment = node.getNextSegment();
						nIntersections+=segment.countRightXRayIntersections(dot);
//						System.out.println("#"+i+"="+segment.countRightXRayIntersections(dot));
				}
//				System.out.println(nIntersections+"%2="+nIntersections%2);
				if(nIntersections%2==0)
						return false;
				else
						return true;
		}
		
		/**
		 *@return  Are nodes go in contour in clockwise direction?
		 */
		public boolean isClockwise()
		{
			Node c, s1, s2;
			if(nodes.size()<3) return false; //not enougn nodes to talk about direction, so we say contour is "ccw"
			int i, n;
			double angles_sum=0.0, angle;
			n=nodes.size();
			
			s1=(Node)nodes.get(n-2);
			c=(Node)nodes.get(n-1);
			s2=(Node)nodes.get(0);
			angles_sum+=c.signedAngle(s1, s2); //angle between n-2'th, n-1'th and 1'st nodes
			s1=c; c=s2;
			s2=(Node)nodes.get(1);
			angles_sum+=c.signedAngle(s1, s2); //angle between n-1'th, 1'st and 2'nd nodes
			for(i=2;i<n;i++)
			{
				s1=c; c=s2;
				s2=(Node)nodes.get(i);
				angle=c.signedAngle(s1, s2);
				angles_sum+=angle; 
			}
			if(angles_sum>0) 
					return true;
				else
					return false;
		}//end of isClockwise

		/**
		 * Dots are contour nodes and begin/end of czones when offset/length are
		 * given by value
		 * @return list of contour nodes and contact zone marks 
		 */
		public List<Dot> getDots()
		{
			List<Dot> list = new ArrayList<Dot>(10);
			List<CZone> czones;
			Node node; Segment segment; CZone czone;
			int ni, nc, czi, czn;
			double x, y;
			for(ni=0, nc=nodes.size(); ni<nc; ni++) //for nodes of contour
			{
				//get node and its segment
				node=(Node)nodes.get(ni);
				segment=node.getNextSegment();
				segment.appendDotsToList(list);
			} //end for nodes of contour
			return list;
		}


		public List<Node>    getNodes()                      { return nodes; }
		public void          setNodes(List<Node> nodes)      { this.nodes = nodes; }
		public RectangleArea getBounds()                     { return bounds;  }
		public void          setBounds(RectangleArea bounds) { this.bounds = bounds; }
		public void          setFigure(Figure figure)        { this.figure = figure; }
		public Contour       getThisContour()                { return this; }
		public Figure        getFigure()                     { return figure;}
		public boolean       isEmpty()                       { return nodes.isEmpty(); }
		public int           getNodesAmount()                { return nodes.size(); }
		
		
		static ContourEditDialog dialog = new ContourEditDialog();
		
		boolean positive = true;
		public List<Node> nodes = new ArrayList(5);//Do not add items manually to this! use methods of Contour instead
		RectangleArea bounds;
		Figure figure;

		
		


		Action editContourAction = new AbstractAction(Messages.getString("Contour.Edit_Contour_1"), //$NON-NLS-1$
			new ImageIcon(Divider.class.getResource("resources/images/editContour.png")) //$NON-NLS-1$
		)
		{
				public void actionPerformed(ActionEvent event)
				{
//						System.out.println("edit contour");
//						if(edit())
					if(figure.getCommandStack().doNewCommand(
							new fem.divider.figure.command.EditContourCommand(figure, getThisContour() )))
						{
								figure.panel.redraw();
						}
				}
		};

		public static Color positiveColor = new Color(0,0x77,0); //dark green
		public static Color negativeColor = new Color(0xDD, 0, 0);//red
		public static Color influenceColor = new Color(0, 0, 0);//black
//	public static Color negativeColor = Color.RED;
		public static final int HANDLE_WIDTH = 3;
		public static final double ANGLE_ACCURACY = 1e-30;

}





class ContourEditDialog extends fem.divider.AbstractEditingDialog
{
	ContourEditDialog()
	{
		super( Messages.getString("Contour.Edit_Contour_3")); //$NON-NLS-1$

		positiveCheckBox = new JCheckBox(
				Messages.getString("Contour.Is_the_contour_positive_(adds_area_to_figure)__4"), true); //$NON-NLS-1$
		contentPanel.add(positiveCheckBox);
		setSize(340, 100);
	}
	
	protected String onOk()
	{
			contour.setPositive(positiveCheckBox.isSelected());
			return null;
	}
	
	boolean run(Contour contour_)
	{
			contour = contour_;
			positiveCheckBox.setSelected(contour.isPositive());
			show();
			return result;
	}
	
	JCheckBox positiveCheckBox;
	Contour contour;
}
