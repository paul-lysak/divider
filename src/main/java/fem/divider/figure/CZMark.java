/*
 * CZMark.java
 * 
 * Created on 3/7/2006
 */
package fem.divider.figure;

import fem.geometry.Dot;

/**
 * CZMark marks a dot at the begin or end of contact zone.
 * Usage: for adding mesh node at czone begin/end place 
 * 
 * @author gefox
 */
public class CZMark extends Dot {

	/**
	 * @param x_
	 * @param y_
	 */
	public CZMark(double x_, double y_, CZone czone_, double offset_)
	{
		super(x_, y_);
		czone=czone_;
		offset=offset_;
	}

	private CZone czone;
	private double offset;
	/**
	 * @return
	 */
	public CZone getCZone() {
		return czone;
	}

	/**
	 * @return
	 */
	public double getOffset() {
		return offset;
	}

}
