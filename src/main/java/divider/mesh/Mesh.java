/*
 * Mesh.java
 *
 * Created on Saturday, 19, March 2005, 12:35
 */

package divider.mesh;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import divider.Dot;
import divider.figure.CZone;
import divider.figure.Contour;
import divider.figure.Figure;

/**
 *
 * @author  gefox
 * @version 
 */
public class Mesh {

	/** Creates new Mesh */
    Mesh() {
    }

		static public Mesh factory(Figure fig, MeshPanel panel)
		{
			
				Mesh mesh = MethodDefault.getInstance().meshdown(fig);
				if(mesh!=null)
					{
						mesh.setPanel(panel); 
						panel.setMesh(mesh);
					}
				return mesh;
		}
		
		public void draw(Graphics2D g)
		{
				g.setPaint(meshColor);
				Node node;
				for(Iterator i=nodes.iterator(); i.hasNext(); )
				{
						node = (Node)i.next();
						node.draw(g);
				}
				Element el;
				for(Iterator i=elements.iterator(); i.hasNext(); )
				{
						el = (Element)i.next();
						el.draw(g);
				}
		}
		
		/**Redraw mesh on panel
		 */
		public void redraw()
		{
				panel.redraw();
		}
	
		/**Redraw figure that coresponds to this mesh
		 */
		public void redrawFigure()
		{
			if(figure!=null)
				figure.redraw();				
		}
			
		public void setPanel(MeshPanel panel_)
		{
				panel = panel_;
		}
		
		public MeshPanel getPanel()
		{
				return panel;
		}
		
		public Figure getFigure()
		{
			return figure;
		}
		
		/**
		 *Remove node from nodes list
		 */
		public void forget(Node node)
		{
				nodes.remove(node);;
		}
		
		/**
		 *Remove element from elements list
		 */
		public boolean forget(Element element)
		{
				return elements.remove(element);
		}
		


		/**
		 *If needed, turn diagonal so that n1 and n2 would belong to one element
		 *@returns was the fixing necessary
		 */
		boolean fixEdge(Node n1, Node n2)
		{
				Element el1, el2;
				for(int n1e_i =0; n1e_i<n1.elements.size(); n1e_i++)
				{
						el1 = (Element)n1.elements.get(n1e_i);
						if(el1.hasNode(n2)) //if el1 contains both n1 and n2
						{								return false;						}
						el2 = el1.oppositeOf(n1);
						if(el2==null) continue;
//						System.out.println("el2="+elements.indexOf(el2));
						if(!el2.hasNode(n2)) continue;
						//here we have neibours el1 and el2, that contains n1 and n2 respectively
						if( el1.swapDiagonalWith(el2) )
						{
								return true;
						}
				}
				return false;
		}
		
		/**
		 *Remove elements that are not inside of positive contour 
		 *or are inside negative contour
		 */
		void cleanElements(ArrayList contours)
		{
				Contour contour;
				Element el;
				boolean in;
				for(int i=elements.size()-1; i>=0; i--)//walk through elements
				{
//						System.out.println("i= "+i);
						el = (Element)elements.get(i);
						in=false;
						for(int j=0; j<contours.size(); j++)//walk through contours
						{
								contour = (Contour)contours.get(j);
								if(contour.isInside( el.centralDot() ))
								{//Element inside the contour
										if(contour.isPositive())
										{
												in=true;
										}
										else
										{
												in=false;
												break;
										}
								}
						}//end walk through contours
						if(!in)
								el.delete();
				}//end of walk through elements
		}
		
		
		public Element findElementThatCovers(Dot dot)
		{
				Element el;
				for(Iterator i=elements.iterator(); i.hasNext(); )
				{
						el=(Element)i.next();
						if(el.isInside(dot)) 
								return el;
				}
				return null;
		}
		
		public void upgrade()
		{

		}
		
		/**
		 *Update index of each node so tha t index of node equals its place in list
		 */
		public void updateNodesIndexes()
		{
				int s = nodes.size();
				Node n;
				for(int i=0; i<s; i++)
				{
						n=(Node)nodes.get(i);
						n.index=i;
				}
		}

		public void updateElementsIndexes()
		{
			int s = elements.size();
			Element el;
			for(int i=0; i<s; i++)
			{
					el=(Element)elements.get(i);
					el.setIndex(i);
			}
		}

		/**
		 *For each node determine it it belongs to some CZone and to which exactly
		 *Skips nodes, that have non-null czone property
		 *For correct work of this function edge nodes of mesh must have their segment
		 *set to correct value
		 */
		public void determineCZones(divider.figure.Figure figure)
		{this.determineCZones(figure, false);}
		/**
		 *Same as determineCzones, but allows to overwrite or not overwrite nodes, that already have czone property set 
		 */
		public void determineCZones(divider.figure.Figure figure, boolean overwrite)
		{
			List<CZone> czones = new ArrayList<CZone>(3);
			
			//Collect CZones:
			//for all contours
			 for(Iterator<divider.figure.Contour> ic=figure.contours.iterator();ic.hasNext();)
			 {
			 	divider.figure.Contour contour = ic.next();
			 	//for all nodes(segments) of contour
			 	for(Iterator in=contour.nodes.iterator();in.hasNext();)
			 	{
			 		divider.figure.Node fnode = (divider.figure.Node)in.next();
			 		divider.figure.Segment seg = fnode.getNextSegment();
			 		//add all CZones from this segment to our list
			 		czones.addAll(seg.getCZones());
			 	}
			 }//end public void determineCZones(divider.figure.Figure figure)
			 
			//Check nodes of CZones:
			//for all Nodes
			for(Iterator ni=nodes.iterator(); ni.hasNext(); )
			{
				Node node = (Node)ni.next();
				if( node.czone!=null&&!overwrite ) continue;
				 //for all CZones
				 for(Iterator icz=czones.iterator(); icz.hasNext(); )
				 {
				 	CZone czone = (CZone)icz.next();
			 		//Check CZone
			 		node.checkCZone(czone);
			 	}
			 }
		}//end determineCZones(..)

		/**
		 * Sort nodes to optimize mesh
		 * (doesn't affect index property of nodes, so updateNodesindexes has to be called
		 * after this)
		 */
		public void sortNodes()
		{
			boolean x_wider; //true if mesh wider by x than by y
			Node left_node, right_node, bottom_node, top_node, node;
			int ni, nn;
			nn=nodes.size();
			if(nn==0) return;
			node=(Node)nodes.get(0);
			left_node=right_node=bottom_node=top_node=node;
			for(ni=1;ni<nn;ni++)
			{
				node=(Node)nodes.get(ni);
				if(node.getX()<left_node.getX()) left_node=node;
					else
					if(node.getX()>right_node.getX()) right_node=node; 
				if(node.getY()<bottom_node.getY()) bottom_node=node;
					else
					if(node.getY()>top_node.getY()) top_node=node; 
			}
			
			Comparator comparator;
			if( right_node.getX()-left_node.getX() > top_node.getY()-bottom_node.getY() )
				{
					x_wider=true; //mesh is wider by X
					comparator = new Comparator()
					{
						public int compare(Object a, Object b)
						{
							Node na, nb;
							na=(Node)a; nb=(Node)b;
							if( na.getX()<nb.getX() ) return -1;
							if( na.getX()>nb.getX() ) return 1;
							//x equals, compare y
							if( na.getY()<nb.getY() ) return -1;
							if( na.getY()>nb.getY() ) return 1;
							return 0;
						}
					};
				} 
				else 
				{x_wider=false; //mesh is wider by Y
					comparator = new Comparator()
					{
						public int compare(Object a, Object b)
						{
							Node na, nb;
							na=(Node)a; nb=(Node)b;
							if( na.getY()<nb.getY() ) return -1;
							if( na.getY()>nb.getY() ) return 1;
							//x equals, compare y
							if( na.getX()<nb.getX() ) return -1;
							if( na.getX()>nb.getX() ) return 1;
							return 0;
						}
					};
				}//end of checking widest direction
				
				Collections.sort(nodes, comparator);				
		}//end sortNodes()
		
		public List<Node> getNodes()
		{
			return nodes;
		}
		
		public int nNodes()
		{
				return nodes.size();
		}
		
		public List<Element> getElements()
		{
			return elements;
		}
		
		public int nElements()
		{
				return elements.size();
		}

		public static Color meshColor = new Color(0x0,0x0,0x0); //black
		
		Figure figure=null;
		MeshPanel panel;
		MeshSettings settings;
	  	List<Node> nodes = new ArrayList(30);
		List<Element> elements = new ArrayList(60);
}
