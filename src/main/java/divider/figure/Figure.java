/*
 * Figure.java
 *
 * Created on Saturday, 19, March 2005, 12:34
 */

package divider.figure;

import java.awt.*;
import java.util.*;
import divider.figure.command.*;

/**
 *
 * @author  gefox
 * @version 
 */
public class Figure {

		/** Creates new Figure */
    public Figure() {
				meshSettings = new divider.mesh.MeshSettings();
    }

		public void draw(Graphics2D graphics)
		{
				for(Iterator i=contours.iterator(); i.hasNext(); )
				{
						((Contour)i.next()).draw(graphics);
				}
		}
		
		public void setPanel(FigurePanel panel_)
		{
				panel = panel_;
		}
		
		public Segment findSegment(double x_, double y_, double handle_)
		{
				Segment segment;
				for(Iterator i=contours.iterator(); i.hasNext(); )
				{
						segment = ((Contour)i.next()).findSegment(x_, y_, handle_);
						if(segment!=null) return segment;
				}
				return null;
		}
		
		public void deleteContour(Contour contour)
		{
				contours.remove(contour);
		}
		
		public divider.RectangleArea calculateBounds()
		{
				if(contours.size()==0) return null; //if there's no contours yet
				divider.RectangleArea area = ((Contour)contours.get(0)).calculateBounds();
				for(int i=1;i<contours.size();i++)
				{
						area.include( ((Contour)contours.get(i)).calculateBounds() );
				}
				return area;
		}
		
		public divider.mesh.MeshSettings getMeshSettings()
		{
				return meshSettings;
		}
		
		public void setMeshSettings(divider.mesh.MeshSettings settings)
		{
				meshSettings = settings;
		}
		
		/**Redraw figure on panel
		 */
		public void redraw()
		{
				panel.redraw();
		}

		/**Redraw mesh that coresponds to this figure
		 */
		public void redrawMesh()
		{
			if(mesh!=null)
				{mesh.redraw();}
		}
		
		
		public void setMesh(divider.mesh.Mesh _mesh)
		{
			mesh=_mesh;
		}
		
		public divider.mesh.Mesh getMesh()
		{
			return mesh;
		}
		
		/**
		 *Get CZones, that belong to groupName group, no more than maxNum 
		 * if maxNum=0, quantiry not limited
		 * if groupName=null, group name not checked
		 * so, if groupName=null and maxNum=1, method will return one CZone (the first one it finds)
		 */
		public ArrayList getCZones(String groupName, int maxNum)
		{
			int cont_i, segm_i, cz_i, cont_n, segm_n, cz_n,  num;
			Contour cont;
			Segment segm;
			Node node;
			ArrayList czones;
			ArrayList result = new ArrayList(1);
			CZone czone;
			
			cont_n= contours.size();
			num=0;
			contours_loop: for(cont_i=0; cont_i<cont_n; cont_i++) //for each contour
			{
				cont=(Contour)contours.get(cont_i);
				
				segm_n= cont.nodes.size();
				for(segm_i=0; segm_i<segm_n; segm_i++) //for each segment of contour;
				{
					//System.out.println("Segmtnt #"+segm_i);
					node = (Node)(cont.nodes.get(segm_i));
					segm = node.getNextSegment();
					czones= segm.getCZones(); 
					if( czones.isEmpty() ) continue; //if empty, move to next segment
					
					if(groupName==null&&maxNum==0) //if no conditions, add alll
					{
						result.addAll(czones);
					}
					else //there are some conditions, we need to choose what to add
					{
						cz_n = czones.size();
						for(cz_i=0; cz_i<cz_n; cz_i++) //for each CZone of segment
						{
							czone = (CZone)czones.get(cz_i);
							if( groupName==null||czone.getGroupName().equals(groupName) )
							{
								num++; //advance added czones counter								
							}
							if( num==maxNum) break contours_loop;
						}//end of CZones loop
					}
				}//end of segments loop
			}//end of contours loop
			return result;
		}//end of method getCZones
		
		public CommandStack getCommandStack() {
			return commandStack;
		}

		public boolean isEditable() {
			return editable;
		}
	
	
		public void setEditable(boolean b) {
			editable = b;
		}
			
		private divider.mesh.Mesh mesh;
		public FigurePanel panel;
		public ArrayList contours = new ArrayList(5);

		//here are stored all commands of figure editing 
		CommandStack commandStack = new CommandStack(); 
		
		private divider.mesh.MeshSettings meshSettings;
		private boolean editable=true;



}
