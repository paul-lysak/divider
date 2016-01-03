package fem.apacker.out;

import fem.apacker.model.ModelFacade;
import fem.common.IPmdSettings;
import java.io.PrintStream;
import java.util.Iterator;
import fem.apacker.model.Node;
import fem.apacker.model.NodeInfluence;
import fem.apacker.model.SegmentInfluence;
import fem.apacker.model.TriangularElement;
import fem.geometry.Dot;
import fem.geometry.Triangle;
import java.util.ArrayList;
import java.util.List;

public class PmdStreamer implements IStreamer{

    private PrintStream stream;
    
    private ModelFacade modelFacade;
    
    private List<NodeConstraint> nodeConstraints;
    
    private List<NodeLoad> nodeLoads;
    
//    private static final String SECTION_SETTINGS = "[settings]";
//    private static final String SECTION_INDEXES = "[inds]";
//    private static final String SECTION_COORDINATES = "[koor]";
//    private static final String SECTION_CONSTRAINTS = "[contact]";
//    private static final String SECTION_LOADS = "[force]";
//
//    private static final String PROPERTY_NNODES = "n_nodes="; 
//    private static final String PROPERTY_NELEMENTS = "n_elements="; 
//    private static final String PROPERTY_NCONTACTS = "n_contacts="; 
//    private static final String PROPERTY_NFORCES = "n_forces="; 
    
    public PrintStream getPrintStream() {
        return stream;
    }

    public void setPrintStream(PrintStream stream) {
        this.stream = stream;
    }

    public ModelFacade getModelFacace() {
        return modelFacade;
    }

    public void setModelFacade(ModelFacade modelFacade) {
        this.modelFacade = modelFacade;
    }

    

    public void write() {
        collectInformation();
        writeSettings();
        writeIndexes();
        writeCoordinates();
        writeConstraints();
        writeLoads();
    }//end method write

    /**
     * Collect information that should be known before writing data to file.
     * For example, we need to know number of loads and constraints, and nodes 
     * to which they are applied
     */
    public void collectInformation() {
        //TODO
        nodeConstraints = new ArrayList<NodeConstraint>();
        nodeLoads = new ArrayList<NodeLoad>();
        for(Iterator<Node> i = modelFacade.getMesh().getNodes().iterator(); i.hasNext(); ) {
            Node node = i.next();
            NodeInfluence influence = node.getNodeInfluence();
            if(influence != null)
                handleNewNodeInfluence(node, influence);
//            System.out.println("Handling node #"+node.getIndex());
            NodeLoad load = collectNodeLoad(node);  //TODO, currently doesn't work
            if(load != null)
                nodeLoads.add(load);
        }
    }//end method collectInformation
            
    private void handleNewNodeInfluence(Node node, NodeInfluence influence) {
        NodeConstraint constraint = new NodeConstraint();
        constraint.index = node.getIndex();
        constraint.fixX = influence.isFixedX();
        constraint.fixY = influence.isFixedY();
        nodeConstraints.add(constraint);
    }
    
    
    private static NodeLoad collectNodeLoad(Node node) {
        NodeLoad load  = null;
        if(node.getElements() == null)
            return load;

        for(Iterator<TriangularElement> i = node.getElements().iterator(); i.hasNext(); ) {
            TriangularElement element = i.next();
            SegmentInfluence[] influences = element.getSegmentInfluences();
            Node[] elementNodes = element.getNodes();
            if(influences == null)
                continue;
            
            for(int j=0; j<3; j++) {
                if(elementNodes[j].getIndex() != node.getIndex())
                    continue;
                
                //check ahear segment
                if(//elementNodes[j].getIndex() != node.getIndex() &&
                        influences[j] != null &&
                        influences[j].isLoaded1()) { //that's our node, it has load
                    load = appendLoadToNode(load, influences[j], 
                            node, elementNodes[Triangle.getNextIndex(j)], true); 
                }//end if ahead segment load found
                
                
                //check preceeding segment
                if(//elementNodes[j].getIndex() != node.getIndex() &&
                        influences[Triangle.getPreviousIndex(j)] != null &&
                        influences[Triangle.getPreviousIndex(j)].isLoaded2()) { //that's our node, it has load
                    load = appendLoadToNode(load, influences[Triangle.getPreviousIndex(j)], 
                            node, elementNodes[Triangle.getPreviousIndex(j)], false); 
                }//end if preceeding segment load found
            }//for influences
        }//end for elements
        return load;
    }
       
    /**
     * Append load to possibly existing object 'load' and return object to which
     * load has been added.
     * If load == null then creates such object before adding load
     * 
     * @param load
     * @param segmentInfluence
     * @param thisNode 
     * @param anotherNode another node of the segment
     * @param anotherIsAhead  if anotheê node is ahead of this in the segment 
     * (otherwise it is before)
     * @return
     */
    private static NodeLoad appendLoadToNode(NodeLoad nodeLoad, SegmentInfluence segmentInfluence,
            Node thisNode, Node anotherNode, boolean anotherIsAhead) {
        NodeLoad load = nodeLoad;
        if(load == null) {
            load = new NodeLoad();
            load.index = thisNode.getIndex();
        }
        
        double zoneLength = thisNode.distance(anotherNode)/2; //zone to which force is applied
        double zoneLoad;
        if(anotherIsAhead)
            zoneLoad = (3*segmentInfluence.getLoad1() + segmentInfluence.getLoad2())/4;
        else
            zoneLoad = (segmentInfluence.getLoad1() + 3*segmentInfluence.getLoad2())/4;
            
        double value = zoneLoad*zoneLength;
        
        Dot loadForce = new Dot(0, -value); //load vector in system where X axis is thisNode->followingNode
        if(anotherIsAhead)
            Dot.restoreCoordinatesOnlyRotation(thisNode, anotherNode, loadForce);
        else
            Dot.restoreCoordinatesOnlyRotation(anotherNode, thisNode, loadForce);
        
        load.valueX += loadForce.getX();
        load.valueY += loadForce.getY();

        return load;
    }//end method appendLoadToNode
    
    /**
     * Write [settings] section
     */
    public void writeSettings() {
        stream.println(IPmdSettings.SECTION_TAG_SETTINGS);
        stream.println(IPmdSettings.PROPERTY_NNODES + modelFacade.getMesh().getNodes().size());
        stream.println(IPmdSettings.PROPERTY_NELEMENTS + modelFacade.getMesh().getElements().size());
        stream.println(IPmdSettings.PROPERTY_NCONTACTS + nodeConstraints.size());
        stream.println(IPmdSettings.PROPERTY_NFORCES + nodeLoads.size()); //TODO
    }//end method writeSettings
    
    /**
     * Write [inds] section
     */
    public void writeIndexes() {
        stream.println(IPmdSettings.SECTION_TAG_INDEXES);
        for(Iterator<TriangularElement> i = modelFacade.getMesh().getElements().iterator(); i.hasNext(); ) {
            TriangularElement element = i.next();
            for(int j=0; j<3; j++) {
                int index = element.getNode(j).getIndex() + 1;
                stream.print(index + " ");
            }
            stream.println();
        }
    }//end method writeIndexes
        
    /**
     * Write [koor] section
     */
    public void writeCoordinates() {
        stream.println(IPmdSettings.SECTION_TAG_COORDINATES);
        for(Iterator<Node> i = modelFacade.getMesh().getNodes().iterator(); i.hasNext(); ) {
            Node node = i.next();                
            stream.println(node.getX());
            stream.println(node.getY());
        }        
    }//end method writeCoordinates
    
    
    /**
     * Write [contact]  section
     */
    public void writeConstraints() {
        stream.println(IPmdSettings.SECTION_TAG_CONTACTS);
        for(Iterator<NodeConstraint> i = nodeConstraints.iterator(); i.hasNext(); ) {
            NodeConstraint constraint = i.next();
            int code = 0;
            if(constraint.fixY) //to be contifmed
                code += 1;
            if(constraint.fixX) //to be contifmed
                code += 2;
            stream.println((constraint.index+1) + " " + code);
        }
    }//end method writeConstraints
    
    /**
     * Write [force] section
     */
    public void writeLoads() {
        stream.println(IPmdSettings.SECTION_TAG_FORCES);
        for(Iterator<NodeLoad> i = nodeLoads.iterator(); i.hasNext(); ) {
            NodeLoad load = i.next();
            stream.println(load.index+" "+load.valueX+" "+load.valueY);
        }
    }
            
    
    
    private static class NodeConstraint {
        protected int index;
        protected boolean fixX;
        protected boolean fixY;
    }
    
    private  static class NodeLoad {
        protected int index;
        protected double valueX;
        protected double valueY;
    }
    
}//end class PmdStreamer

