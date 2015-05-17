package fem.divider.figure;

import fem.geometry.Dot;

/**
 * @author Paul Lysak
 *
 */
public class CurveMark extends Dot {
	
	private Segment segment;
	
	private double offset;
	
	public CurveMark(double x, double y, Segment segment, double offset) {
		super(x, y);
		this.segment = segment;
		this.offset = offset;
	}

	
	public final Segment getSegment() {
		return segment;
	}
	public final void setSegment(Segment segment) {
		this.segment = segment;
	}
	public final double getOffset() {
		return offset;
	}
	public final void setOffset(double offset) {
		this.offset = offset;
	}
}
