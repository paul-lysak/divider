package divider.figure;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import divider.Divider;
import divider.Dot;
import divider.RectangleArea;

public class LineSegmentGeometry extends AbstractSegmentGeometry {

	public static final String GEOMETRY_TYPE = "line";
	
	public Map<String, String> getParameters() {
		Map<String, String> params = new HashMap<String, String>();

		params.put(PARAM_GEOMETRY_TYPE, GEOMETRY_TYPE);

		return params;
	}

	
	
	
	public void draw(Segment segment, Graphics2D graphics) {
		FigurePanel panel = segment.getBegin().contour.figure.panel;
		if (panel == null) {
			System.err.println("Can't draw segment --- panel is null"); //$NON-NLS-1$
			return;
		}
		graphics.drawLine(panel.xsi(segment.getBegin().x), panel.ysi(segment.getBegin().y), panel
				.xsi(segment.getEnd().x), panel.ysi(segment.getEnd().y));
		
		//Draw czones
		for (Iterator<CZone> i = segment.czones.iterator(); i.hasNext();) {
			CZone czone = i.next();
			drawCzone(segment, czone, graphics, panel);
		}
	}// end of draw

	public void drawCzone(Segment segment, CZone czone, Graphics2D graphics, FigurePanel panel) {
		Stroke stroke = graphics.getStroke();
		graphics.setStroke(new BasicStroke(5.0F));
		double x1 = segment.getBegin().x;
		double y1 = segment.getBegin().y;
		double x2 = segment.getEnd().x;
		double y2 = segment.getEnd().y;

		x1 += segment.shift_x(czone.getRealOffset());
		y1 += segment.shift_y(czone.getRealOffset());
		x2 = x1 + segment.shift_x(czone.getRealLength());
		y2 = y1 + segment.shift_y(czone.getRealLength());

		graphics.drawLine(panel.xsi(x1), panel.ysi(y1), panel.xsi(x2), panel
				.ysi(y2));
		graphics.drawOval(panel.xsi(x1) - 2, panel.ysi(y1) - 2, 5, 5);
		graphics.drawOval(panel.xsi(x2) - 2, panel.ysi(y2) - 2, 5, 5);
		graphics.setStroke(stroke);

		drawInfluence(segment, czone, graphics, panel);
	}// end of draw_czone




	public double getLength(Segment segment) {
		return segment.getBegin().distance(segment.getEnd());
	}

	public double distance(Segment segment, Dot dot) {
		double x = dot.x;
		double y = dot.y;
		// find out position of point relatively to segment begin and end
		double lineX = segment.getEnd().x - segment.getBegin().x;
		double lineY = segment.getEnd().y - segment.getBegin().y;
		double beginX = x - segment.getBegin().x;
		double beginY = y - segment.getBegin().y;
		double endX = x - segment.getEnd().x;
		double endY = y - segment.getEnd().y;

		if (lineX * endX + lineY * endY > 0) // |----| *
		{
			return segment.getEnd().distance(x, y);
		}
		if (lineX * beginX + lineY * beginY < 0) // * |----|
		{
			return segment.getBegin().distance(x, y);
		}
		// |--*--|

		double A = segment.getBegin().y - segment.getEnd().y;
		double B = segment.getEnd().x - segment.getBegin().x;
		double C = -B * segment.getBegin().y - A * segment.getBegin().x;
		double n = Math.sqrt(A * A + B * B);
		if (n == 0)// null line
			return segment.getBegin().distance(x, y);
		double d = A * x / n + B * y / n + C / n;
		return Math.abs(d);
	}



	public Dot shiftDot(Segment segment, double offset) {
		double dx = segment.getEnd().x - segment.getBegin().x;
		double dy = segment.getEnd().y - segment.getBegin().y;
		double dl = Math.sqrt(dx * dx + dy * dy);

		double x = offset * dx / dl;
		double y = offset * dy / dl;
		return new Dot(x, y);
	}


	public Dot getDirection(Segment segment, double offset) {
		double x = segment.getEnd().x - segment.getBegin().x;
		double y = segment.getEnd().y - segment.getBegin().y;
		double l = segment.getLength();
		x = x / l;
		y = y / l;
		return new Dot(x, y);
	}


	public RectangleArea calculateBounds(Segment segment) {
		return new RectangleArea(segment.getBegin(), segment.getEnd());	
	}

	public int countRightXRayIntersections(Segment segment, 
			Dot dot) {
		if (!(dot.y >= Math.min(segment.getBegin().y, segment.getEnd().y) && dot.y < Math.max(segment.getBegin().y,
				segment.getEnd().y))) {// if dot isn't in y-range
			return 0;
		}
		// segment is vertical
		if (Math.abs(segment.getEnd().x - segment.getBegin().x) < Divider.GENERAL_ACCURACY) {// if this
			if (segment.getBegin().x > dot.x)
				return 1;
			else
				return 0;
		}
		double k = (segment.getEnd().y - segment.getBegin().y) / (segment.getEnd().x - segment.getBegin().x);
		double b = -segment.getBegin().x * k + segment.getBegin().y;
		double y = k * dot.x + b;
		if (k > 0) // /
		{
			if (y < dot.y)
				return 1;
			else
				return 0;
		} else // \
		{
			if (y > dot.y)
				return 1;
			else
				return 0;
			// return 0;
		}
	}//end countRightXRayIntersections
	
}
