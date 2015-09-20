package fem.apacker.model;

import fem.geometry.Triangle;
import java.util.ArrayList;
import java.util.List;

public class ModelFacade {
    private static final int NODAL_TEMPERATURES_DEFAULT_SIZE = 50;
    private Mesh mesh;

    private List<Double> nodalTemperature;
    
    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
    
    public ModelFacade(Mesh mesh)
    {
        setMesh(mesh);
    }
    
    /**
     * Add a node to model. 
     * If node with such index exists it throws runtime eexception.
     * 
     * @param n node index
     * @param x node coordinate x
     * @param y node coordinate y
     */
    public void addNode(int n, double x, double y)
    {
        Node node = new Node(x, y);
        node.setIndex(n);
        getMesh().getNodes().add(node);
    }
    
    
    /**
     * Get node or throw runtime exception if no such node found
     * 
     * @param index
     * @return
     */
    private Node getNode(int index) {
        Node node;
        try {
            node = getMesh().getNodes().get(index);
        }
        catch(IndexOutOfBoundsException e)
        {
            throw new RuntimeException("Node not found: "+index, e);                    
        }
        if(node == null)
            throw new RuntimeException("Node not found: "+index);        
        return node;
    }//end method getNode

    /**
     * Get element or throw runtime exception if no such element found
     * 
     * @param index
     * @return
     */
    private TriangularElement getElement(int index) {
        TriangularElement element;
        try {
            element = getMesh().getElements().get(index);
        }
        catch(IndexOutOfBoundsException e)
        {
            throw new RuntimeException("Node not found: "+index, e);                    
        }
        if(element == null)
            throw new RuntimeException("Node not found: "+index);        
        return element;
    }//end method getNode    
    
    /**
     * Get influence from node or 
     * if it is null create new one and set it to node.
     * 
     * @param node
     * @return
     */
    private static NodeInfluence getNodeInfluence(Node node)
    {
        NodeInfluence influence = node.getNodeInfluence();
        if(influence == null)
            influence = new NodeInfluence();
        node.setNodeInfluence(influence);
        return influence;
    }
    
    /**
     * Get influence for segment of element that connects two given nodes.
     * If such influence doesn't exist yet then create it and return.
     * 
     * @param element
     * @param nodeIndex1
     * @param nodeIndex2
     * @return
     */
    private static SegmentInfluence getSegmentInfluence(TriangularElement element, int nodeIndex1, int nodeIndex2)
    {
        SegmentInfluence[] influences = element.getSegmentInfluences();
        
        int node1 = element.getNodeNumberE(nodeIndex1);
        int node2 = element.getNodeNumberE(nodeIndex2);

        int earlierIndex = Triangle.getEarlierIndex(node1, node2);

        if(influences[earlierIndex] == null)
        {
            influences[earlierIndex] = new SegmentInfluence();
        }
        
        return influences[earlierIndex];
    }//end method getSegmentInfluence
    
    
    /**
     * Add element to the model
     * 
     * @param n element index
     * @param nodeIndex1
     * @param nodeIndex2
     * @param nodeIndex3
     */
    public void addElement(int n, int nodeIndex1, int nodeIndex2, int nodeIndex3)
    {
        TriangularElement element = new TriangularElement();
        
        element.setIndex(n);
        
        if(nodeIndex1 == nodeIndex2 || nodeIndex1 == nodeIndex3)
            throw new RuntimeException("Element "+n+" has duplicate nodes:"+nodeIndex1);
        if(nodeIndex2 == nodeIndex3)
            throw new RuntimeException("Element "+n+" has duplicate nodes:"+nodeIndex2);
        
        Node node1 = getNode(nodeIndex1);
        Node node2 = getNode(nodeIndex2);
        Node node3 = getNode(nodeIndex3);
        
        node1.getElements().add(element);
        node2.getElements().add(element);
        node3.getElements().add(element);
        
        element.setNodes(new Node[] {node1, node2, node3});
        
        getMesh().getElements().add(element);
    }//end method addElement
    
    
    public void applyConstraint(int nodeIndex, boolean fixX, boolean fixY) {
        Node node = getNode(nodeIndex);
        
        NodeInfluence influence = getNodeInfluence(node);
        influence.setFixedX(fixX);
        influence.setFixedY(fixY);
//        System.out.println(MessageFormat.format("Fixed node {0}: x={1}, y={2}", 
//                   new Object[]{nodeIndex, fixX, fixY}));
    }
    
    /**
     * Apply load.
     * 
     * Order indexes of nodes not essential, it's only important that
     * value1 would correspond to nodeIndex1 and value2 to nodeIndex2
     * 
     * @param elementIndex
     * @param nodeIndex1
     * @param nodeIndex2
     * @param value1
     * @param value2
     */
    public void applyLoad(int elementIndex, int nodeIndex1, int nodeIndex2, double value1, double value2) {
        TriangularElement element = getElement(elementIndex);
        
        SegmentInfluence influence = getSegmentInfluence(element, nodeIndex1, nodeIndex2);
        if(Triangle.isAscendingOrder(nodeIndex1, nodeIndex2)) {
            influence.setLoad1(value1);
            influence.setLoad2(value2);
        }
        else {
            influence.setLoad1(value2);
            influence.setLoad2(value1);
        }
        influence.setLoaded1(true);
        influence.setLoaded2(true);
    }//end method applyLoad

    /**
     * Add temperature record to model
     * TODO currently it supports only data that come in order of increading node:
     * 1,2,3,4, etc.
     * 
     * @param node
     * @param temperature
     */
    public void addNodalTemperature(int node, double temperature) {
//        System.out.println("node="+node+", t="+temperature);
        getNodalTemperature().add(new Double(temperature));
    }
    
    /**
     * Get nodal temperatures list. If such list is null then creates it and returns.
     * @return
     */
    public List<Double> getNodalTemperature() {
        if(nodalTemperature == null)
            nodalTemperature = new ArrayList<Double>(NODAL_TEMPERATURES_DEFAULT_SIZE);
        return nodalTemperature;
    }

    public void setNodalTemperature(List<Double> nodalTemperature) {
        this.nodalTemperature = nodalTemperature;
    }
    
}//end class 
