/*
 * Created on 8/10/2006
 */
package fem.divider.mesh.measure;
import fem.divider.mesh.*;

import java.util.*;
import java.awt.Color;

/**
 * Measure --- main class for measurement system
 * @author gefox
 */
public class Measure {
	/**
	 * Constructor
	 * Before actual usage of tis class instance you should call setMeshPanel method of this class
	 * and setMeasure method of MeshPanel class
	 */
	public Measure(){
		measureController = new MeasureController(this);
	}

	/**
	 * Add one measure line to measureLines field
	 * @param line - line to add
	 */
	public void addLine(MeasureLine line)
		{measureLines.add(line);}
	
	/**
	 * Remove one measure line ftom measureLines field
	 * @param line - line to remove
	 */
	public void removeLine(MeasureLine line)
		{measureLines.remove(line);}
		
	/**
	 * Set content of measureLines to what given in lines parameter.
	 * Removes all from measureLines, then copies all from lines to measureLines
	 * @param lines -- lines to be placed
	 */
	public void setLines(ArrayList lines)
	{
		measureLines.clear();
		measureLines.addAll(lines);
	}

	/**
	 * Remove all lines from measureLines
	 */	
	public void removeAllLines()
	{
		measureLines.clear();
	}

	/**
	 * Draw on mesh panel. Actually calls draw(g2) method for all measureLines 
	 */
	public void draw(java.awt.Graphics2D g2) {
		MeasureLine line;
		int l = measureLines.size();
		for(int i=0; i<l; i++)
		{
			line =(MeasureLine)measureLines.get(i);
			line.draw(g2);
		}
		measureController = new MeasureController(this);
	}
	
	public MeshPanel getMeshPanel() {
		return meshPanel;
	}

	/**
	 * Set meshPanel
	 */
	public void setMeshPanel(MeshPanel panel) {
		meshPanel = panel;
	}

	public ArrayList getMeasureLines() {
		return measureLines;
	}

	public void setMeasureLines(ArrayList list) {
		measureLines = list;
	}
	
	public MeasureController getMeasureController() {return measureController;}
	
	private MeshPanel meshPanel=null;
	private ArrayList measureLines = new ArrayList(3);
	private MeasureController measureController;

	public static Color measureColor = new Color(0,0xaa,0); //green

}
