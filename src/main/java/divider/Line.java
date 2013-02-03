/*
 * Line.java
 *
 * Created on Thursday, 31, March 2005, 18:00
 */

package divider;

/**
 *
 * @author  gefox
 * @version 
 */
public class Line {

		/** Creates new Line */
    public Line(Dot begin_, Dot end_) {
				begin=begin_;
				end=end_;
    }

		/**
		 *get distance from line to point
		 */
		public double signedDistance(double x, double y)
		{
				//find out position of point relatively to segment begin and end
				double lineX = end.x - begin.x;
				double lineY = end.y - begin.y;
				double beginX = x - begin.x;
				double beginY = y - begin.y;
				double endX = x - end.x;
				double endY = y - end.y;
				
				// |--*--|
				
				double A = begin.y - end.y;
				double B = end.x - begin.x;
				double C = -B*begin.y-A*begin.x;
				double n = Math.sqrt(A*A+B*B);
				if(n ==0)//null line
						return begin.distance(x, y);
				double d = A*x/n+B*y/n+C/n;
				
				if(lineX*endX+lineY*endY > 0) // |----| *
				{
						if(d>0)
								return end.distance(x, y);
						else
								return -end.distance(x, y);								
				}
				if(lineX*beginX+lineY*beginY < 0) //   *  |----|
				{
						if(d>0)
								return begin.distance(x, y);
						else
								return -begin.distance(x, y);								
				}
				
				return d;
		}		

		public double signedDistance(Dot dot)
		{
				return signedDistance(dot.x,dot.y);
		}
		
		public double distance(double x, double y)
		{
				return Math.abs(signedDistance(x,y));
		}
		
		public double distance(Dot dot)
		{
				return distance(dot.x, dot.y);
		}
		

		Dot begin;
		Dot end;
}
