/*
 * Created on 8/10/2006
 */
package fem.divider.mesh.measure;
import fem.divider.mesh.Element;
import fem.geometry.Dot;
import fem.geometry.Triangle;

import java.util.*;

/**
 * MeasureLine --- represents data selection line for measurement system
 * @author gefox
 */
public class MeasureLine {

	/**
	 * 
	 */
	public MeasureLine(Measure _measure, String _name, Dot _begin, Dot _end) 
	{
		measure=_measure;
		name = _name;
		begin = new Dot(_begin);
		end = new Dot(_end);
	}

	/**
	 * Draw on mesh panel
	 */
	public void draw(java.awt.Graphics2D g2) 
	{
		//System.out.println("draw line:"+begin.toString()+"; "+end.toString());
		fem.divider.mesh.MeshPanel panel = measure.getMeshPanel();
		g2.setPaint(Measure.measureColor);
		g2.drawLine(panel.xsi(begin.getX()), panel.ysi(begin.getY()),
				panel.xsi(end.getX()), panel.ysi(end.getY()));
		
		//get measure line direction		
		double direction[]=new double[2];
		double length=Math.sqrt(  (end.getX()-begin.getX())*(end.getX()-begin.getX())+
									(end.getY()-begin.getY())*(end.getY()-begin.getY())  );
		direction[0]=(end.getX()-begin.getX())/length;
		direction[1]=	(end.getY()-begin.getY())/length;
		
		double direction1[]=new double[2];
		direction1[0]=direction[0];
		direction1[1]=direction[1];
		Dot.turnVector(direction1, 20*Math.PI/180);
		int arrow1rel_x=(int)(10.0*direction1[0]);
		int arrow1rel_y=-(int)(10.0*direction1[1]);

		direction1[0]=direction[0];
		direction1[1]=direction[1];
		Dot.turnVector(direction1, -20*Math.PI/180);
		int arrow2rel_x=(int)(10.0*direction1[0]);
		int arrow2rel_y=-(int)(10.0*direction1[1]);
		
		//draw an arrow on end
		g2.drawLine(panel.xsi(end.getX()), panel.ysi(end.getY()),
				panel.xsi(end.getX())-arrow1rel_x, panel.ysi(end.getY())-arrow1rel_y);
		g2.drawLine(panel.xsi(end.getX()), panel.ysi(end.getY()),
				panel.xsi(end.getX())-arrow2rel_x, panel.ysi(end.getY())-arrow2rel_y);
		//draw an arrow on begin
		g2.drawLine(panel.xsi(begin.getX()), panel.ysi(begin.getY()),
				panel.xsi(begin.getX())-arrow1rel_x, panel.ysi(begin.getY())-arrow1rel_y);
		g2.drawLine(panel.xsi(begin.getX()), panel.ysi(begin.getY()),
				panel.xsi(begin.getX())-arrow2rel_x, panel.ysi(begin.getY())-arrow2rel_y);
	}
	
	
	/**
	 * Found intersections of this line with given element
	 * 
	 */
	public Dot[] intersections(Element element)
	{
		Dot intersects[]=new Dot[3];
		Dot inter1; 
		int i=0;
		
		Dot elNodes[] = element.getNodes();
		//System.out.println("Search intersections");
		inter1=Dot.linesIntersection(begin, end, elNodes[0], elNodes[1]);
		if(inter1!=null && //lines not parallel
				Dot.isInsideRectangle(inter1, elNodes[0], elNodes[1])&&
				Dot.isInsideRectangle(inter1, begin, end))//intersection inside measure line and on element side   
				{intersects[i]=inter1; i++;
					//System.out.println("Intersection: "+inter1);
				}		
		inter1=Dot.linesIntersection(begin, end, elNodes[1], elNodes[2]);
		if(inter1!=null && //lines not parallel
				Dot.isInsideRectangle(inter1, elNodes[1], elNodes[2])&&
				Dot.isInsideRectangle(inter1, begin, end))//intersection inside measure line and on element side   
				{intersects[i]=inter1; i++;
					//System.out.println("Intersection: "+inter1);
				}		
		inter1=Dot.linesIntersection(begin, end, elNodes[2], elNodes[0]);		
		if(inter1!=null && //lines not parallel
				Dot.isInsideRectangle(inter1, elNodes[2], elNodes[0])&&
				Dot.isInsideRectangle(inter1, begin, end))//intersection inside measure line and on element side   
				{intersects[i]=inter1; i++;
					//System.out.println("Intersection: "+inter1);
				}		
		
		//create and fill array of appropriate size
		Dot ready[];
		switch(i)
		{
			case 0: 
				ready=new Dot[0];
				break;
			case 1: 
				ready=new Dot[1];
				ready[0]=intersects[0];
				break;
			case 2: 
				ready=new Dot[2];
				ready[0]=intersects[0]; ready[1]=intersects[1];
				break;
			default:
				ready=null;
				break;
		}//end switch(i)
		
		return ready;
	}//end intersections(Element)
	
	/**
	 * Find elements that are crossed by this measureLine
	 * (arranged from begin to end of line)
	 */
	public List getCrossedElements()
	{
		Dot intersects[];
		SelectedElement selEl, otherSelEl;
		List selElements = new LinkedList();
		List readyElements;
		
		List<Element> allElements  = measure.getMeshPanel().getMesh().getElements();
		int i, l;
		Element element;
		l=allElements.size();
		for(i=0; i<l; i++)
		{
			element = (Element)allElements.get(i);
			intersects = intersections(element);
			double distance;
			selEl=null;
			if(intersects==null) continue;
			if(intersects.length==2)
				{
					distance=begin.distance(  (intersects[0].getX()+intersects[1].getX())/2,
								(intersects[0].getY()+intersects[1].getY())/2 );
					selEl = new SelectedElement(element, distance);
				}
				else
				{
					if(intersects.length==1)
						{
							if(element.isInside(begin)) distance=0;
								else distance=begin.distance(end);
							selEl = new SelectedElement(element, distance);
						}
						else
						continue;
				}
			if(selEl!=null) //if we've got selected element, insert it into selElements, in distance ascending order
			{
				ListIterator elIterator, insIterator;
				SelectedElement elToCheck;
				boolean inserted=false;
				
				for(elIterator=selElements.listIterator(); elIterator.hasNext(); )
				{
					elToCheck=(SelectedElement)elIterator.next();
					//insert selEl at insIterator if found more distant element
					if( elToCheck.distance>selEl.distance )
						{
							elIterator.previous();
							elIterator.add(selEl);  inserted=true; 
							break; 
							}
				}//end for all selElements
				if(!inserted)
					{selElements.add(selEl); inserted=true;}

			}//end if selEl!=null
		}//end for all elements of mesh
		
		readyElements=new ArrayList(selElements.size());
		for(ListIterator li=selElements.listIterator(); li.hasNext(); )
		{
			selEl = (SelectedElement)li.next();
			int num=0;
//			num=measure.getMeshPanel().getMesh().getElements().indexOf(selEl.element);
//			num=selEl.element.getIndex();
//			System.out.println("Element #"+num+"   distance="+selEl.distance);
			readyElements.add(selEl.element);
		}
		
		return readyElements;
	}//end getCrossedElements()


	/**
	 * Find elements  with centers near enough to  this measureLine
	 * (arranged from begin to end of line)
	 */
	public List<Element> getElements()
	{
		SelectedElement selEl, otherSelEl;
		LinkedList selElements = new LinkedList();
		ArrayList readyElements;
		
		List<Element> allElements  = measure.getMeshPanel().getMesh().getElements();
		int i, l;
		double w_distance, l_distance;
		double line_length=begin.distance(end);
		Triangle element;
		l=allElements.size();
		for(i=0; i<l; i++)
		{
			element = (Triangle)allElements.get(i);
			Dot elCenter=element.getCentralDot();
			Dot.translateCoordinates(begin, end, elCenter);
			w_distance=elCenter.getY();
			l_distance=elCenter.getX();
			selEl=null;
			if(Math.abs(w_distance)>captureWidth|| //check if center of element belongs to capture zone
				l_distance<-captureWidth||
				l_distance>line_length+captureWidth) continue;
			selEl = new SelectedElement(element, l_distance);
			
		 	//we've got selected element, insert it into selElements, in distance ascending order
			ListIterator elIterator, insIterator;
			SelectedElement elToCheck;
			boolean inserted=false;
			
			for(elIterator=selElements.listIterator(); elIterator.hasNext(); )
			{
				elToCheck=(SelectedElement)elIterator.next();
				//insert selEl at insIterator if found more distant element
				if( elToCheck.distance>selEl.distance )
					{
						elIterator.previous();
						elIterator.add(selEl);  inserted=true; 
						break; 
						}
			}//end for all selElements
			if(!inserted)
				{selElements.add(selEl); inserted=true;}

		}//end for all elements of mesh
		
		readyElements=new ArrayList(selElements.size());

 		for(ListIterator li=selElements.listIterator(); li.hasNext(); )
		{
			selEl = (SelectedElement)li.next();
			int num=0;
			readyElements.add(selEl.element);
		}
		
		return readyElements;
	}//end getElements()


	public String getName() {
		return name;
	}

	public void setName(String string) {
		name = string;
	}
	
		
	Measure measure;
	String name;
	Dot begin;
	Dot end;
	/** width of capture zone --- the strip around this line. elements with centers in this zone will be selected*/
	double captureWidth=1; 


}//end class MeasureLine



class SelectedElement
{
	Triangle element;
	double distance;
	
	SelectedElement(Triangle el_, double dist_)
	{element=el_; distance=dist_;}
}
