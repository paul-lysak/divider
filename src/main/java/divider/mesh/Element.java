/*
 * Element.java
 *
 * Created on Monday, 28, March 2005, 18:26
 */

package divider.mesh;

import divider.Line;
import divider.Dot;
import java.awt.*;
import java.util.*;

/**
 *
 * @author  gefox
 * @version 
 */
public class Element {

		/** Creates new Element
		 * and adds it to mesh of given node
		 */
    public Element(Node A_, Node B_, Node C_) {
				mesh = A_.mesh;
				nodes[0]=A_; nodes[1]=B_; nodes[2]=C_;
				A_.add(this);
				B_.add(this);
				C_.add(this);
				mesh.elements.add(this);
    }

		public void draw(Graphics2D g)
		{
				Node A = nodes[0];
				Node B = nodes[1];
				Node C = nodes[2];
				MeshPanel p = mesh.panel;
				g.drawLine( p.xsi(A.x), p.ysi(A.y), p.xsi(B.x), p.ysi(B.y));
				g.drawLine( p.xsi(B.x), p.ysi(B.y), p.xsi(C.x), p.ysi(C.y));
				g.drawLine( p.xsi(C.x), p.ysi(C.y), p.xsi(A.x), p.ysi(A.y));

//				g.drawString(mesh.elements.indexOf(this)+"" , p.xsi( centralDot().x ), p.ysi( centralDot().y  ));
				if(divider.Divider.getDivider().getPreferences().isShowMeshElementNumbers())
					{
						Color col = g.getColor();
						g.setPaint(elementNumberColor);
						g.drawString((index+1)+"" , p.xsi( centralDot().x ), p.ysi( centralDot().y  )); //$NON-NLS-1$
						g.setPaint(col);
					}

/*
 				g.drawLine( p.xsi(centralDot().x)-5, p.ysi(centralDot().y)-5, 
						p.xsi(centralDot().x)+5, p.ysi(centralDot().y)+5);
				g.drawLine( p.xsi(centralDot().x)-5, p.ysi(centralDot().y)+5, 
						p.xsi(centralDot().x)+5, p.ysi(centralDot().y)-5);
*/
//				drawFignu(g);
		}

		/**
		 *Removes all known links to this element
		 */
		public void delete()
		{
				mesh.forget(this);
//				A.forget(this);
//				B.forget(this);
//				C.forget(this);
				nodes[0].forget(this);
				nodes[1].forget(this);
				nodes[2].forget(this);
				
				valid = false;
		}

		/**
		 *Removes all known links to this element, 
		 *except those that are in given node
		 */
		public void deleteBy(Node node)
		{
				mesh.forget(this);
				if(node!=nodes[0])
						nodes[0].forget(this);
				if(node!=nodes[1])
						nodes[1].forget(this);
				if(node!=nodes[2])
						nodes[2].forget(this);
				
				valid = false;
		}
		
//		public void replace

		public boolean hasNode(Node node)
		{
				for(int i=0;i<3;i++)
				{
						if(node==nodes[i]) return true;
				}
				return false;
		}
		
		/*
		 * @return is dot inside this element --- no matter are nodes of element clockwise or counter-clockwise
		 */
		public boolean isInside(divider.Dot dot)
		{
				Line l1 = new Line(nodes[0], nodes[1]);
				Line l2 = new Line(nodes[1], nodes[2]);
				Line l3 = new Line(nodes[2], nodes[0]);
				double d1 = l1.signedDistance(dot);
				double d2 = l2.signedDistance(dot);
				double d3 = l3.signedDistance(dot);
				
				if(d1>-divider.Divider.GENERAL_ACCURACY&&
						d2>-divider.Divider.GENERAL_ACCURACY&&
						d3>-divider.Divider.GENERAL_ACCURACY)
						return true;
				else
						return false; 				
		}
		
		public boolean isReallyInside(divider.Dot dot)
		{
				if(!isInside(dot)) return false;
				if( nodes[0].angle(dot, nodes[1]) < INSIDE_ANGLE) return false;
				if( nodes[1].angle(dot, nodes[2]) < INSIDE_ANGLE) return false;
				if( nodes[2].angle(dot, nodes[0]) < INSIDE_ANGLE) return false;
				return true;
		}
		
		
		/**
		 *@returns on success element that borders with this and is opposite to node,
		 *on failure null
		 */
		public Element oppositeOf(Node node)
		{
				Node node1;
				Node node2;
				if(nodes[0]!=node)
				{
						node1=nodes[0];
						if(nodes[1]!=node)
								node2=nodes[1];
						else
								node2=nodes[2];//if nodes[1]==node
				}
				else //nodes[0]==node
				{
						node1=nodes[1];
						node2=nodes[2];
				}
				
				Element el;
				for(Iterator i=node1.elements.iterator(); i.hasNext(); )
				{
						el = (Element)i.next();
						if(el==this) continue;
//						if(el.hasNode(node2)&&el.valid)
						if(el.hasNode(node2))
								return el;
				}
				return null;
		}
				
		/**
		 *    /_\   =>  /|\
		 *   \ /           \|/
		 *@returns is it allowed 
		 *  /\
		 *  \/
		 *  or
		 *  //
		 *  \\
		 */
		public boolean swapDiagonalWith(Element el)
		{
				Node a,b,//common nodes
				c1, c2;//individual nodes
				if(!el.hasNode(nodes[0]))
				{
						c1 = nodes[0];
						a = nodes[1];
						b = nodes[2];
				}
				else
						if(!el.hasNode(nodes[1]))
						{
								c1 = nodes[1];
								a = nodes[2];
								b = nodes[0];
						}
						else
						{
								c1 = nodes[2];
								a = nodes[0];
								b = nodes[1];
						}
				if(!hasNode(el.nodes[0]))
						c2 = el.nodes[0];
				else
						if(!hasNode(el.nodes[1]))
								c2 = el.nodes[1];
						else
								c2 = el.nodes[2];
						
				double angle = 0;
				//c2 a c1 b
				angle+=a.angle(c1, c2);
				angle+=c1.angle(a, b);
				angle+=b.angle(c1, c2);
				angle+=c2.angle(b,a);
				if(angle<2*Math.PI-divider.Divider.GENERAL_ACCURACY)
				{
						return false;
				}
				//replace nodes
				nodes[0]=c1;
				nodes[1]=a;
				nodes[2]=c2;
				
				el.nodes[0]=c2;
				el.nodes[1]=b;
				el.nodes[2]=c1;
				
				//update element lists in nodes
				//ex-this
				c1.add(el);
				a.forget(el);
				b.forget(this);
				//ex-el
				c2.add(this);
				return true;
		}
		
		private void drawFignu(Graphics2D g)
		{
				MeshPanel p = mesh.panel;
				double A1, A2, B1, B2, C1, C2;
				//line 1 coefficients
				A1=nodes[1].x-nodes[0].x;
				B1=nodes[1].y-nodes[0].y;
				C1=A1*(nodes[1].x+nodes[0].x)/2+B1*(nodes[1].y+nodes[0].y)/2;
				//line 2 coefficients
				A2=nodes[2].x-nodes[0].x;
				B2=nodes[2].y-nodes[0].y;
				C2=A2*(nodes[2].x+nodes[0].x)/2+B2*(nodes[2].y+nodes[0].y)/2;
				
				//determinants
				double d = A1*B2-B1*A2;
				double dx = C1*B2-B1*C2;
				double dy = A1*C2-C1*A2;
				
				double x = dx/d;
				double y = dy/d;
				
				double x1, y1, x2, y2;
				x1=-100;
				y1 = (-A1*x1+C1)/B1;
				x2=100;
				y2 = (-A1*x2+C1)/B1;
				Paint paint = g.getPaint();
				g.setPaint(Color.yellow);
				g.drawLine( p.xsi(x1), p.ysi(y1), p.xsi(x2), p.ysi(y2));
				x1=-100;
				y1 = (-A1*x1+C1)/B1;
				x2=100;
				y2 = (-A1*x2+C1)/B1;
				g.drawLine( p.xsi(x1), p.ysi(y1), p.xsi(x2), p.ysi(y2));
				g.setPaint(paint);
		}
		
		
		/**
		 * Returns center of circle, drawn around this element
		 */
		public Dot circleCenter()
		{		
				double A1, A2, B1, B2, C1, C2;
				//line 1 coefficients
				A1=nodes[1].x-nodes[0].x;
				B1=nodes[1].y-nodes[0].y;
				C1=A1*(nodes[1].x+nodes[0].x)/2+B1*(nodes[1].y+nodes[0].y)/2;
				//line 2 coefficients
				A2=nodes[2].x-nodes[0].x;
				B2=nodes[2].y-nodes[0].y;
				C2=A2*(nodes[2].x+nodes[0].x)/2+B2*(nodes[2].y+nodes[0].y)/2;
				
				//determinants
				double d = A1*B2-B1*A2;
				double dx = C1*B2-B1*C2;
				double dy = A1*C2-C1*A2;
				
				double x = dx/d;
				double y = dy/d;
				
				return new Dot(x,y);
		}
		
		/**
		 * Determines, whether is dot inside the circle, drawn around the element, or not
		 */
		public boolean isInsideCircle(Dot dot)
		{
				Dot center = circleCenter();
				if( center.distance(dot) < center.distance(nodes[0]) )
						return true;
				else
						return false;
		}
		
		public Dot centralDot()
		{
				return new Dot( (nodes[0].x+nodes[1].x+nodes[2].x)/3,
												(nodes[0].y+nodes[1].y+nodes[2].y)/3);
		}
		
		public double area()
		{
				double s = -0.5*(
				nodes[0].x*(nodes[1].y-nodes[2].y)+
				nodes[1].x*(nodes[2].y-nodes[0].y)+
				nodes[2].x*(nodes[0].y-nodes[1].y)
				);
				return Math.abs(s);
//				return s;
		}
		
		public double angle(int i)
		{
				if(i<0||i>2) return 0;
				int s1, s2, c;
				c =i;
				if(i==0) s1=2; else s1=i-1;
				if(i==2) s2=0; else s2=i+1;
				return nodes[c].angle(nodes[s1], nodes[s2]);
		}
		
		/**
		 * Returns number of minimal angle
		 */
		public int minAngle()
		{
				if( angle(0) < angle(1) && angle(0) < angle(2) )
						return 0;
				if( angle(1) < angle(0) && angle(1) < angle(2) )
						return 1;
				return 2;
		}

		/**
		 * Returns number of maximal angle
		 */
		public int maxAngle()
		{
				if( angle(0) > angle(1) && angle(0) > angle(2) )
						return 0;
				if( angle(1) > angle(0) && angle(1) > angle(2) )
						return 1;
				return 2;
		}
		
		private boolean isGood()
		{
				double s = area();
				if(s>mesh.settings.maxArea) return false; //too big
				if(s<mesh.settings.minArea) return true; //too small to care about :-)
				double a = Math.min( Math.min(angle(0), angle(1)), angle(2));
				if(a<mesh.settings.getMinAngle()) return false; //has too small angle
				return true; // nothing specital
		}
		
		private int mostDistantNode(Dot dot)
		{
				double d0 = dot.distance(nodes[0]);
				double d1 = dot.distance(nodes[1]);
				double d2 = dot.distance(nodes[2]);
				
				if(d0>d1&&d0>d2) return 0;
				if(d1>d0&&d1>d2) return 1;
				return 2;
		}
		
		private int other1(int i)
		{
				if(i==0) return 1;
				if(i==1) return 2;
				return 0; //i==2
		}
		
		private int other2(int i)
		{
				if(i==0) return 2;
				if(i==1) return 0;
				return 1; //i==1
		}
		
		private Node thirdNode(Node n1, Node n2)
		{
				for(int i=0; i<3; i++)
				{
						if(nodes[i]!=n1&&nodes[i]!=n2)
								return nodes[i];
				}
				return null;
		}
		
		static int i =0;
		/**
		 * Split element info smaller elements if needed
		 * @returns two-dimension array: result[0] has removed elements, 
		 * result[1] has added elements 
		 * If these was no upgrade returns zero-size array
		 */
		public boolean upgrade()
		{
//				System.out.println("shrink= "+areaShrink());
				//is upgrade needed?
				if(valid && ((area() < mesh.settings.maxArea*areaShrink() && 
//				if((area() < mesh.settings.maxArea && 
//						angle(minAngle()) > mesh.settings.maxAngle
						angle(minAngle()) > mesh.settings.getMinAngle()
						) ||
						area() < mesh.settings.minArea) )
						return false; //area OK
	
	
/*				if( nodes[0].distance(circleCenter()) < mesh.settings.maxRadius)
						return false;
*/				
				if(!isInside(circleCenter())||
						angle(minAngle()) < OUTER_UPGRADE_ANGLE
				)
						return outerUpgrade();
				else
						return innerUpgrade();
				//return true;
		}
		
		/**
		 * New node inside the element
		 */
		private boolean innerUpgrade()
		{
				Dot cdot = centralDot();
				Node cnode = new Node(mesh, cdot);
				
				Element el1 = new Element(nodes[0], nodes[1], cnode);
				Element el2 = new Element(nodes[1], nodes[2], cnode);
				Element el3 = new Element(nodes[2], nodes[0], cnode);
				this.delete();
				
				cnode.lawson();
				
				return true;
		}
		
		/**
		 * New node on element border
		 */
		private boolean outerUpgrade()
		{
				int man = maxAngle(); //maxAngle node
				Node maxN = nodes[man];
				
				int n1 = other1(man);
				int n2 = other2(man);
				Node N1 = nodes[n1];
				Node N2 = nodes[n2];

				//opposite element
				Element op4 = this.oppositeOf(nodes[man]);
				if(op4!=null)
				{
					int op4max = op4.maxAngle(); Node op4Nmax = op4.nodes[op4max];
					int op4_1 =  op4.other1(op4max); Node op4N1 = op4.nodes[op4_1];
					int op4_2 = op4.other2(op4max); Node op4N2 = op4.nodes[op4_2];
					//check if we won't make the opposite element worse
					if( op4Nmax.angle(op4N1, op4N2)> (3*Math.PI/4) && 
						(op4Nmax == N1 || op4Nmax == N2) ) return false; //not upgraded 
				}
				
				Node newNode = new Node(nodes[n1], nodes[n2], 0.5,  op4==null?true:false ); 
				
				Element el1 = new Element(newNode, nodes[n2], nodes[man]);
				Element el2 = new Element(newNode, nodes[man], nodes[n1]);

				this.delete();
				
				if(op4!=null) //we have opposite element, let's split it too
				{
						Node op4node = op4.thirdNode(nodes[n1], nodes[n2]);

						Element op4el1 = new Element(op4node, nodes[n2], newNode);
						Element op4el2 = new Element(newNode, nodes[n1], op4node);

						op4.delete();
//						op4node.lawson();

				}
				
//				maxN.lawson();
				
				newNode.lawson();
				return true;
		}

		/**
		 *Returns coeficient (0...1] , that tells maximal allowed size
		 *of this element (relatively to mesh.settings.maxArea)
		 */
		private double areaShrink()
		{
				double leastOriginalAngle=Math.PI; //180 degres
				for(int i=0; i<2; i++)
				{
						if(nodes[i].original)
						{
								if(nodes[i].originalAngle < leastOriginalAngle)
										leastOriginalAngle=nodes[i].originalAngle;
						}
				}
				
				return leastOriginalAngle/Math.PI;
		}
		
		public  Node[] getNodes()
		{
			return nodes;
		}

		public int getIndex() {
			return index;
		}
	
		void setIndex(int i) {
			index = i;
		}

		
		Node nodes[] = new Node[3];
		
		int index; //index in array of elements in mesh
		Mesh mesh;
		boolean edge = false;
		boolean valid = true;
		public static double INSIDE_ANGLE = Math.toRadians(1);
		public static double OUTER_UPGRADE_ANGLE = Math.toRadians(20);

		public static Color elementNumberColor=new Color(0, 100, 0);
}
