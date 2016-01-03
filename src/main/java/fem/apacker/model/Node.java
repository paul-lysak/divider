package fem.apacker.model;

import fem.geometry.Dot;
import java.util.ArrayList;
import java.util.List;

public class Node extends Dot {

    private int index;
           
    private NodeInfluence nodeInfluence;
    
    private List<TriangularElement> elements;
    
    public Node(double x, double y)
    {
            super(x, y);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public NodeInfluence getNodeInfluence() {
        return nodeInfluence;
    }

    public void setNodeInfluence(NodeInfluence nodeInfluence) {
        this.nodeInfluence = nodeInfluence;
    }

    
    public List<TriangularElement> getElements() {
        if(elements == null)
            elements = new ArrayList<TriangularElement>();
        return elements;
    }

}