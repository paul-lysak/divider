package fem.apacker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import fem.common.FemCommonHelper;

public class Mesh {
	private List<TriangularElement> elements = new ArrayList<TriangularElement>(20);
	private List<Node> nodes = new ArrayList<Node>(30);
	
	public List<TriangularElement> getElements() {
		return elements;
	}
	public void setElements(List<TriangularElement> elements) {
		this.elements = elements;
	}
	public List<Node> getNodes() {
		return nodes;
	}
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}


        /**
         * Sort nodes according to their indexes
         */
        public void sortNodes()
        {
            Collections.sort(nodes, new Comparator<Node>() {
                public int compare(Node n1, Node n2) {                    
                    return FemCommonHelper.compare(n1.getIndex(), n2.getIndex());
                  }
                }
            );
        }//enc method sortNodes
        
        /**
         * Sort elements according to their indexes
         */
        public void sortElements()
        {
            Collections.sort(elements, new Comparator<TriangularElement>() {
                public int compare(TriangularElement e1, TriangularElement e2) {                    
                    return FemCommonHelper.compare(e1.getIndex(), e2.getIndex());
                  }
                }
            );
        }//enc method sortNodes        
        
}//end class Mesh
