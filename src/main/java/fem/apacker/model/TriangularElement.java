package fem.apacker.model;

import fem.geometry.Triangle;
import fem.geometry.Dot;
import java.text.MessageFormat;


public class TriangularElement extends Triangle {

    public TriangularElement(Dot A_, Dot B_, Dot C_) {
      super(A_, B_, C_);
   }
   
    public TriangularElement(){
       super(null, null, null);
    }

   private int index;

    private SegmentInfluence[] segmentInfluences = new SegmentInfluence[3];
    
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
	
    
    public void setNodes(Node[] nodes)
    {
            setCorners(nodes);
    }

    @Override
    protected Dot[] createCornersArray() {
        return new Node[3];
    }
		
                
                
    public Node[] getNodes()
    {
            return (Node[])getCorners();
    }

    public Node getNode(int i)
    {
        return getNodes()[i];
    }
    
    /**
     * Get node number (0..2, inside of element)
     * by node index (0..nNodex, inside of all mesh)
     * If node not found return -1
     * 
     * @param index
     * @return
     */
    public int getNodeNumber(int index) {
        Node[] nodes = getNodes();
        for(int i=0; i<3; i++)
        {
            if(nodes[i].getIndex() == index)
                return i;
        }
        return -1;
    }//end method getNodeNumber

    /**
     * Same as getNodeNumber but throws runtime exception instead of returning -1
     * if node doesn't belong to this element.
     * 
     * @param index
     * @return
     */
    public int getNodeNumberE(int index) {
        int num = getNodeNumber(index);
        if(num == -1)
            throw new RuntimeException(MessageFormat.format("Node {0} doesn't belong to element {1}",
                    new Object[]{index, this.getIndex()}));
        return num;
    }//end method getNodeNumberE

    
    /**
     * Get segment influences.
     * 
     * @return array with 3 elements, elements may be null
     */
    public SegmentInfluence[] getSegmentInfluences() {
        return segmentInfluences;
    }

    /**
     * Set segment influences.
     * There should be array of 3 elements
     * 
     */
    public void setSegmentInfluences(SegmentInfluence[] segmentInfluences) {
        this.segmentInfluences = segmentInfluences;
    }
    
}//end class TriangularElement
