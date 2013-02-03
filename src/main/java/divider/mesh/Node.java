/*
 * Node.java
 *
 * Created on Monday, 28, March 2005, 18:23
 */

package divider.mesh;

import java.util.*;
import java.awt.*;
import divider.Dot;
import divider.figure.CZMark;
import divider.figure.CZone;

/**
 * 
 * @author gefox
 * @version
 */
public class Node extends divider.Dot {

	/**
	 * Creates new Node Adds node to mesh_
	 */
	public Node(Mesh mesh_, double x_, double y_) {
		super(x_, y_);
		mesh = mesh_;
		mesh.nodes.add(this);
	}

	/**
	 * Creates new Node Adds node to mesh_
	 */
	public Node(Mesh mesh_, Dot dot) {
		this(mesh_, dot.x, dot.y);
		if (dot instanceof divider.figure.Node) {
			divider.figure.Node n1 = (divider.figure.Node) dot;
			original = true;
			edge = true;
			originalAngle = n1.angle();
		}
	}

	public static Node createConditionaly(Mesh mesh, Dot dot) {
		Node node;
		if(dot instanceof CZMark)
			node = createFromCZMark(mesh, (CZMark)dot);
		else if(dot instanceof divider.figure.Node)
			node = createFromFigureNode(mesh, (divider.figure.Node)dot);
		else if(dot instanceof divider.figure.CurveMark)
			node = createFromCurveMark(mesh, (divider.figure.CurveMark)dot);
		else 
			node = new Node(mesh, dot);
		return node;
	}

	/**
	 * Creates new Node Adds node to mesh_ node_ is regarded as begining of some
	 * segment
	 */
	public static Node createFromFigureNode(Mesh mesh_,
			divider.figure.Node node_) {
		Node node = new Node(mesh_, (Dot) node_);
		node.segment = node_.getNextSegment();
		node.offset = 0.0;
		node.figureNode = node_;
		return node;
	}

	/**
	 * Creates new Node Adds node to mesh_ node_ is regarded as edge of czone
	 */
	public static Node createFromCZMark(Mesh mesh_,
			divider.figure.CZMark czmark_) {
		Node node = new Node(mesh_, (Dot) czmark_);
		node.czone = czmark_.getCZone();
		node.segment = node.czone.getSegment();
		node.offset = czmark_.getOffset();
		node.figureNode = null;
		return node;
	}

	/**
	 * Creates new Node Adds node to mesh_ node_ is regarded as point,
	 * belonging to curve segment
	 */
	public static Node createFromCurveMark(Mesh mesh_,
			divider.figure.CurveMark cmark) {
		Node node = new Node(mesh_, (Dot)cmark);
		node.segment = cmark.getSegment();
		node.offset = cmark.getOffset();
		node.figureNode = null;
		return node;
	}
	
	/**
	 * Creates new Node between given Nodes If given Nodes have link to segment,
	 * also sets segment and offset of new node Adds node to mesh_
	 * 
	 * @param edge
	 *            --- whether result node will be on edge
	 * @param part
	 *            --- which part of begin to end length will be left for begin
	 *            to this
	 */
	public Node(Node begin, Node end, double part, boolean edge) {
		this(begin.mesh, begin.x + (end.x - begin.x) * part, begin.y
				+ (end.y - begin.y) * part);

		// node will be on edge. We need to know it's segment and offset
		if (edge && begin.segment != null && end.segment != null) {
			this.edge = true;

			Node realBegin = begin, realEnd = end;
			double beginOffset = 0.0, endOffset = 0.0;
			boolean reverse = false;

			// check, whether our nodes are on the ends of the segment
			// both on ends
			if (begin.figureNode != null && end.figureNode != null) {

				// check common segment
				if (!begin.segment.hasNode(end.figureNode)) {
					// reverse order
					realBegin = end;
					realEnd = begin;
				}
				beginOffset = 0.0;
				endOffset = realBegin.segment.getLength();

			} else {
				// one on end, other not
				if (begin.figureNode != null || end.figureNode != null) {

					// assuming begin and end belong to different segments
					if (begin.figureNode != null) {
						// begin is edge of segment
						realBegin = end;
						realEnd = begin;
					} else {
						// end is edge of segment
						realBegin = begin;
						realEnd = end;
					}
					if (begin.segment == end.segment) {
						// on the same segment. Swap realBegin and realEnd
						Node tmp = realBegin;
						realBegin = realEnd;
						realEnd = tmp;
						beginOffset = 0.0;
						endOffset = realEnd.offset;
					} else {
						// begin and end are on different segments. all right.
						beginOffset = realBegin.offset;
						endOffset = realBegin.segment.getLength();
					}

				} else {
					// none on end

					if (begin.offset > end.offset) {
						realBegin = end;
						realEnd = begin;
					}
					beginOffset = realBegin.offset;
					endOffset = realEnd.offset;
				}
			}
			offset = beginOffset + (endOffset - beginOffset)
					* (begin == realBegin ? part : 1 - part);
			segment = realBegin.segment;

		}
	}// end Node(Node begin, Node end, double part, boolean edge)

	/**
	 * Removes all known links to node (in mesh and in elements) Also delete()s
	 * all its elements
	 */
	public void delete() {
		mesh.forget(this);
		if (elements != null) {
			for (Iterator i = elements.iterator(); i.hasNext();) {
				((Element) i.next()).deleteBy(this);
			}
		}
		elements = null;
		valid = false;
	}// end delete()

	/**
	 * Add Element
	 */
	public void add(Element element) {
		elements.add(element);
	}

	/**
	 * Makes node "forger" about element If node "forgets" about all elements,
	 * it delete()s itself
	 */
	public void forget(Element element) {
		elements.remove(element);
		int i = elements.indexOf(element);
		// Seems there is a bug in ArrayList implementation.
		// Sometimes elements are not removed at once,
		// so i had to repeat remove :-(
		if (i != -1)
			elements.remove(element);
		i = elements.indexOf(element);

		if (elements.size() == 0)
			delete();
	}// end forget(Element element)

	public void draw(Graphics2D g) {
		MeshPanel panel = mesh.panel;
		if (czone == null)
			g.drawOval(panel.xsi(x) - 2, panel.ysi(y) - 2, 4, 4);
		else
			g.drawOval(panel.xsi(x) - 4, panel.ysi(y) - 4, 8, 8);

		if (divider.Divider.getDivider().getPreferences()
				.isShowMeshNodeNumbers()) {
			Color col = g.getColor();
			g.setPaint(nodeNumberColor);
			g.drawString((index + 1) + "", panel.xsi(x), panel.ysi(y)); //$NON-NLS-1$
			g.setPaint(col);
		}
		drawInfluence(g);
	}// end of draw method

	private void drawInfluence(Graphics2D g) {
		if (czone == null)
			return;
		MeshPanel panel = mesh.panel;
		switch (czone.getInfluenceMode()) {
		case CZone.INFLUENCE_CONTACT:
			g.drawRect(panel.xsi(x) - 4, panel.ysi(y) - 4, 8, 8);
			Stroke stroke = g.getStroke();
			g.setStroke(new BasicStroke(2.0F));
			if (czone.isForbidXMotion())
				g.drawLine(panel.xsi(x), -8 + panel.ysi(y), panel.xsi(x),
						8 + panel.ysi(y));
			if (czone.isForbidYMotion())
				g.drawLine(panel.xsi(x) - 8, panel.ysi(y), panel.xsi(x) + 8,
						panel.ysi(y));
			g.setStroke(stroke);
			break;
		case CZone.INFLUENCE_FORCE:
		case CZone.INFLUENCE_DISTRIBUTED_FORCE:
			// FORCE, draw an arrow
			double force[] = czone.forceDirection(offset);
			// arrow line
			double xw2 = x - force[0] * panel.ww(25),
			yw2 = y - force[1] * panel.ww(25);
			g.drawLine(panel.xsi(x), panel.ysi(y), panel.xsi(xw2), panel
					.ysi(yw2));
			// arrow hat
			double force1[] = new double[2];
			force1[0] = force[0];
			force1[1] = force[1];
			Dot.turnVector(force1, 20 * Math.PI / 180);
			double xa1 = x - force1[0] * panel.ww(10),
			ya1 = y - force1[1] * panel.ww(10);
			g.drawLine(panel.xsi(xa1), panel.ysi(ya1), panel.xsi(x), panel
					.ysi(y));
			force1[0] = force[0];
			force1[1] = force[1];
			Dot.turnVector(force1, -20 * Math.PI / 180);
			xa1 = x - force1[0] * panel.ww(10);
			ya1 = y - force1[1] * panel.ww(10);
			g.drawLine(panel.xsi(xa1), panel.ysi(ya1), panel.xsi(x), panel
					.ysi(y));
			if (czone.getInfluenceMode() == CZone.INFLUENCE_FORCE) {
				g.drawOval(panel.xsi(xw2) - 2, panel.ysi(yw2) - 2, 4, 4);
			}
			break;
		}
	}// end of drawInfluence method

	/**
	 * Lawson's exchange algorithm
	 * 
	 * @returns set of affected elements
	 */
	public HashSet lawson() {
		// System.out.println("Lawson 4 "+elements.size()+" elements");
		HashSet aff = new HashSet();

		Element myEl, oppEl; // close element and opposite element
		// walk through all Elements. Thay may be added at the time this loop is
		// running
		for (int i = 0; i < elements.size(); i++) {
			myEl = (Element) elements.get(i);
			oppEl = myEl.oppositeOf(this);
			// has no opposite
			if (oppEl == null)
				continue;
			// System.out.println("op4");
			// is this dot insode oppEl's circle?
			if (oppEl.isInsideCircle(this)) {
				if (!myEl.swapDiagonalWith(oppEl))
					continue;// skip if failed
				add(oppEl);
				i--; // we'll look at this element once more
				// remember affected elements
				aff.add(myEl);
				aff.add(oppEl);
			}
		}

		return aff;
	}// end lawson()

	void setSegment(divider.figure.Segment segment_, double offset_) {
		segment = segment_;
		offset = offset_;
		if (segment_ == null)
			edge = false;
		else
			edge = true;
	}

	/**
	 * Check if this node belongs to czone_ Node belongs to czone if it has
	 * samesegment set to same as in czone and nodes offset satisfies czone
	 * requirements, or if czone begin or end includes node of figure, that
	 * equals figureNode property of this node
	 * 
	 * @return result. Also sets czone to czone_, if result is true
	 */
	public boolean checkCZone(divider.figure.CZone czone_) {
		// check if they have same segment
		if (czone_.getSegment() == segment) {
			// check node offset interval
			if (czone_.getRealOffset() < offset
					&& offset < czone_.getRealLength() + czone_.getRealOffset()) {
				czone = czone_;
				czoneOffset = offset - czone_.getRealOffset();
				return true;
			}
			// check "edge" conditions:
			// begin included?
			if (czone_.getOffsetMode() == divider.figure.CZone.OFFSET_BEGIN) {
				// is this node assotiated with the begining of the segment?
				if (czone_.getSegment().getBegin() == figureNode) {
					czone = czone_;
					czoneOffset = 0.0;
					return true;
				}
			}
		}// end of check if segment is same
		// check if include end
		if (czone_.getOffsetMode() == divider.figure.CZone.OFFSET_END
				|| czone_.getLengthMode() == divider.figure.CZone.LENGTH_TO_END) {
			// is this node assotiated with the begining of the segment?
			if (czone_.getSegment().getEnd() == figureNode) {
				czone = czone_;
				czoneOffset = czone.getRealLength();
				return true;
			}
		}
		return false;
	}// end checkCZone(divider.figure.CZone czone_)

	/**
	 * For edge node find nodes that belong to same segment and
	 * 
	 * @return neighbours: {left, right} or null on appropriate places
	 */
	public Node[] getNeighbours() {
		Node neighbours[] = { null, null };
		if (!isOnEdge())
			return neighbours;

		Node left, right, n;
		left = null;
		right = null;
		Element element;
		Node[] elNodes;
		int s = elements.size();
		for (int i = 0; i < s; i++) {
			element = (Element) elements.get(i);
			elNodes = element.getNodes();
			for (int j = 0; j < elNodes.length; j++) {
				n = elNodes[j];
				// skip node if it's this, if not on edge or has segment,
				// different from this
				if (n == this || !n.isOnEdge() || n.segment != segment)
					continue;
				// possibly, update left node
				if (n.offset < offset) {
					if (left == null || left.offset < n.offset)
						left = n;
				}
				// possibly, update left node
				if (n.offset > offset) {
					if (right == null || right.offset > n.offset)
						right = n;
				}
			}
		}// for nodes of element

		neighbours[0] = left;
		neighbours[1] = right;
		return neighbours;
	}// for elements that include this node

	/** Does this node lay on edge? */
	boolean isOnEdge() {
		return edge;
	}

	Mesh mesh;
	ArrayList elements = new ArrayList(5);
	private boolean edge = false;
	boolean valid = true;

	boolean original = false;
	double originalAngle;

	// index in mesh.nodes
	int index;

	// segment (if node is on the edge)
	divider.figure.Segment segment = null;
	// offset from the beginning of the segment
	double offset = 0.0;
	// CZone, this node belongs to
	divider.figure.CZone czone = null;
	// offset from begin of czone
	double czoneOffset = 0.0;
	// Node of figure that is original of this node (if it exists)
	divider.figure.Node figureNode = null;
	public static Color nodeNumberColor = new Color(0, 0, 175);
}// end class Node
