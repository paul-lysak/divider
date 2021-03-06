/*
 * MeshStreamer.java
 *
 * Created on Sunday, 10, April 2005, 13:16
 */

package divider.mesh;

import divider.Divider;
import divider.Messages;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import divider.figure.CZone;

/**
 * Streamer for saving mesh to multiple files
 * 
 * @author  Paul Lysak
 * @version 
 */
public class MultifileMeshStreamer implements IMeshStreamer {

		/* (non-Javadoc)
		 * @see divider.mesh.IMeshStreamer#save(divider.mesh.Mesh, java.lang.String)
		 */
		public void save(Mesh mesh, String fileBasename) throws Exception
		{
			FileOutputStream coords;
			FileOutputStream indexes;
			FileOutputStream edge_indexes;
			FileOutputStream log;
						
			try
			{
				coords = new FileOutputStream(fileBasename+".koor"); //$NON-NLS-1$
			}
			catch(FileNotFoundException e)
			{
				String msg = Messages.getString("Divider.Can__t_save_to_file__43")+fileBasename+".koor"; //$NON-NLS-1$ //$NON-NLS-2$
				throw new Exception(msg, e);
			}
			try
			{
				indexes = new FileOutputStream(fileBasename+".inds"); //$NON-NLS-1$
			}
			catch(FileNotFoundException e)
			{
				String msg = Messages.getString("Divider.Can__t_save_to_file__47")+fileBasename+".inds";  //$NON-NLS-1$ //$NON-NLS-2$ 
				throw new Exception(msg, e);
			}						
			save(mesh, coords, indexes);
			FileOutputStream contacts, forces;
			try
			{
				contacts = new FileOutputStream(fileBasename+".contact"); //$NON-NLS-1$
			}
			catch(FileNotFoundException e)
			{
				String msg = Messages.getString("Divider.Can__t_save_to_file__56")+fileBasename+".contact";  //$NON-NLS-1$ //$NON-NLS-2$ 
				throw new Exception(msg, e);
			}
			try
			{
			forces = new FileOutputStream(fileBasename+".force"); //$NON-NLS-1$
			}
			catch(FileNotFoundException e)
			{
				String msg = Messages.getString("Divider.Can__t_save_to_file__60")+fileBasename+".force";  //$NON-NLS-1$ //$NON-NLS-2$
				throw new Exception(msg, e);
			}																		
			saveInfluence(mesh, contacts, forces);
			FileOutputStream sforces;
			try
			{
				sforces = new FileOutputStream(fileBasename+".sforce"); //$NON-NLS-1$
			}
			catch(FileNotFoundException e)
			{
				String msg = Messages.getString("Divider.Can__t_save_to_file__56")+fileBasename+".sforce";  //$NON-NLS-1$ //$NON-NLS-2$
				throw new Exception(msg, e);
			}
			saveSForces(mesh, sforces);
		}//end save(Mesh mesh, String fileBasename)
		
		
		
		/**
		 * Saves coordinates of nodes to given stream.
		 * Format is like this:
		 * x1
		 * y1
		 * x2
		 * y2
		 * 
		 * etc.
		 * 
		 * Applies Divider.formatNumberS(..) formatting method to numbers
		 *   
		 * Before calling this methods mesh.updateNodesIndexes() 
		 * should be called (once for one saving process) 
		 * 
		 * @param mesh
		 * @param coords
		 * @throws Exception
		 */
		protected void saveCoords(Mesh mesh, OutputStream coordsStream) throws Exception
		{
			PrintWriter coordsWriter = new PrintWriter(coordsStream, true);
			saveCoords(mesh, coordsWriter);
		}
		
		protected void saveCoords(Mesh mesh, PrintWriter coordsWriter) throws Exception
		{
			Iterator<Node> i = mesh.nodes.iterator();
			while(i.hasNext())
			{
				Node node = i.next();
				coordsWriter.println(Divider.formatNumberS(node.x)); 
				coordsWriter.println(Divider.formatNumberS(node.y));
			}
			
		}
		
		
		/**
		 * Saves elements indexes to given stream
		 * Format is like this:
		 * el1_node1_number el1_node2_number el1_node3_number
		 * el2_node1_number el2_node2_number el2_node3_number
		 * etc.
		 * 
		 * Before calling this methods mesh.updateNodesIndexes() 
		 * should be called (once for one saving process) 
		 * 
		 * @param mesh
		 * @param elements
		 * @throws Exception
		 */
		protected void saveIndexes(Mesh mesh, OutputStream indexesStream) throws Exception
		{
			PrintWriter out = new PrintWriter(indexesStream, true);
			saveIndexes(mesh, out);
		}
		
		protected void saveIndexes(Mesh mesh, PrintWriter indexesWriter) throws Exception
		{
			Iterator<Element> i = mesh.elements.iterator();
			Element element;
			while(i.hasNext())
			{
				element = i.next();
				indexesWriter.println( (element.nodes[0].index+1)+" "+
						(element.nodes[1].index+1)+" "+
						(element.nodes[2].index+1)
						);
			}
			
		}
		
		/**
		 *Save mesh to two files: nodes coordinates and element nodes indexes
		 */
		public void save(Mesh mesh, OutputStream coords, OutputStream indexes) throws Exception
		{								
			mesh.updateNodesIndexes();

			saveCoords(mesh, coords);
			saveIndexes(mesh, indexes);
			
		}//end String save(Mesh mesh, OutputStream coords, OutputStream indexes)


	
	/**
	 * Saves edge nodes
	 * @param mesh
	 * @param edge_indexes
	 * @param log
	 * @return
	 */
	public void saveEdgeNodes(Mesh mesh, OutputStream edge_indexes) throws Exception
	{
		
		class EdgeNode
		{
			EdgeNode(int ni, int zi, int gi)
			{node_index=ni; zone_index=zi; group_index=gi;}
			public String toString() {return (node_index+1)+"   "+zone_index+"   "+group_index;}
			int node_index, zone_index, group_index;
		}
		
		
		int i,j,n,s, zone_name_number, group_name_number;
		s=mesh.nodes.size();
		PrintWriter out = new PrintWriter( edge_indexes, true);
		Node node;
		divider.figure.CZone czone;
		ArrayList edge_nodes = new ArrayList(50);
		ArrayList zones_names = new ArrayList(5); 
		ArrayList groups_names = new ArrayList(3); 
		
		zones_names.add(new String(""));
		groups_names.add(new String(""));
		String czname, cgname;
		EdgeNode enode;
		
		//collect information about edge nodes, contact zones, and contact groups
		for(i=0; i<s; i++)
		{
				node = (Node)mesh.nodes.get(i);
				if(node.isOnEdge()) //node on edge, let's remember it
				{
					czone = node.czone;
					if(czone!=null) //has a czone
						{
							czname=czone.getName();
							cgname=czone.getGroupName();
							
							
							//begin of  update zones_names list
							zone_name_number= -1; //search for this contact zone in list
							for(j=0; j<zones_names.size(); j++)
								if(czname.equals(zones_names.get(j)))
									{zone_name_number=j; break;}
							if(zone_name_number<0)  //no such zone yet, let's addit to list
							{
								zones_names.add(czname);
								zone_name_number=zones_names.size()-1;
							} //end of update zones_names list
							
							//begin of  update groups_names list
							group_name_number= -1; //search for this contact group in list
							for(j=0; j<groups_names.size(); j++)
								if(cgname.equals(groups_names.get(j)))
									{group_name_number=j; break;}
							if(group_name_number<0)  //no such zone yet, let's addit to list
							{
								groups_names.add(cgname);
								group_name_number=groups_names.size()-1;
							} //end of update groups_names list 	
							
							enode = new EdgeNode(node.index, zone_name_number, group_name_number);
						}
					else //doesn't have a czone
						{
							enode = new EdgeNode(node.index, 0, 0);
						}
					edge_nodes.add(enode);
				}
		}//end of collecting information

		s=zones_names.size(); 
		for(i=1; i<s; i++) //write zones, skip zero
		{
			czname = (String)zones_names.get(i);
			out.println(i+" "+czname);
		}
		out.println("");
		s=groups_names.size();
		for(i=1; i<s; i++) //write groups, skip zero
		{
			cgname = (String)groups_names.get(i);
			out.println(i+" "+cgname);
		}
		out.println("");
		s=edge_nodes.size();
		for(i=0; i<s; i++)//write edge nodes
		{
			enode = (EdgeNode)edge_nodes.get(i);
			out.println(enode);
		}
		out.println("");
	}//end save(Mesh mesh, OutputStream coords, OutputStream indexes, OutputStream edge_indexes)
	
	
		
	
	
	
	
	/**save contact and force information
	 * line of contacts will look like:
	 * number_of_node(from 1)    fix_x  fix_y
	 * line of forces will look like:
	 *number_of_node(from 1)    x_part   y_part
	 *
	 *@return array with 2 int elements - count of contacts and forces respectively
	*/
	public int[] saveInfluence(Mesh mesh, OutputStream contacts, OutputStream forces) throws Exception
	{
		PrintWriter contacts_out = new PrintWriter( contacts, true);		
		PrintWriter forces_out = new PrintWriter( forces, true);
		return saveInfluence(mesh, contacts_out, forces_out);
	}
	
	public int[] saveInfluence(Mesh mesh, PrintWriter contacts_out, PrintWriter forces_out) throws Exception
	{
		InfluencesCollector collector = new InfluencesCollector();
		collector.collectNodes(mesh);
		collector.sortCZoneNodes(null);

		int i, s, contactsCount = 0, forcesCount = 0;
		Node node, node_back, node_forward;

		CZone czone;
		Map.Entry<CZone, List<Node>> entry;
		List<Node> zone_nodes;
		Set<Map.Entry<CZone, List<Node>>> czoneSet = collector.getCzoneNodes().entrySet();
		for(Iterator<Map.Entry<CZone, List<Node>>> cz_it=czoneSet.iterator(); cz_it.hasNext(); ) //for all czones
		{
			entry = cz_it.next();
			czone = entry.getKey();
			zone_nodes = entry.getValue();
			//here deal with nodes of one czone

			s=zone_nodes.size();
			//walk through all czone nodes, looking for those under influence
			for(i=0; i<s; i++)
			{
					node = zone_nodes.get(i);
						switch(czone.getInfluenceMode())
						{
							case divider.figure.CZone.INFLUENCE_CONTACT: //node has contact, write it
								int fx, fy;
								if( czone.isForbidXMotion() ) fx=2; else fx=0;
								if( czone.isForbidYMotion() ) fy=1; else fy=0;
								int f=fx+fy;
								contactsCount++;
								contacts_out.println( (node.index+1)+" "+f);
								break;
							case divider.figure.CZone.INFLUENCE_DISTRIBUTED_FORCE:
							case divider.figure.CZone.INFLUENCE_FORCE: //node has force (concentrated or spread), write it
								double force[]=czone.forceDirection(node.offset);
								force[0]*=czone.getForceValue();
								force[1]*=czone.getForceValue();
								if(czone.getInfluenceMode()==divider.figure.CZone.INFLUENCE_DISTRIBUTED_FORCE)
								{
									double force_area=0.0;
									node_back=node_forward=null;
									if(i>0)
										{node_back = zone_nodes.get(i-1);
										force_area+=node.distance(node_back)/2;}
									if(i<s-1) 
										{node_forward = zone_nodes.get(i+1);
											force_area+=node.distance(node_forward)/2;
										} 
										force[0]*=force_area;
										force[1]*=force_area;
								}//end if spread force (
								forcesCount++;
								forces_out.println( (node.index+1)+" "+Divider.formatNumberS(force[0])+" "+Divider.formatNumberS(force[1]));
								break;
							default: //not under influence, skip
								continue;
						}//end  switch(czone.getInfluenceMode())
					}//end for all nodes of czone
			
			//end deal with nodes of one
		}//end for all czones
		return new int[]{contactsCount, forcesCount};
	}//end saveInfluence(Mesh mesh, OutputStream contacts, OutputStream forces)


	/**
	 * format of .sforce file:
	 */
	public String saveSForces(Mesh mesh, OutputStream sforces)
	{
		InfluencesCollector collector = new InfluencesCollector();
		collector.collectNodes(mesh);
		collector.outputSForces(sforces);
		return null;
	}

}//end class MeshStreamer











/**
 *Allows to gather information about what nodes belong to what czones.
 *In order for those methods to work, czone and czoneOffset properties for nodes of mesh 
 *need to be set earlier (may be done with Mesh.determineCZones(figure_) )
 */
class InfluencesCollector
{	
	private Map<CZone, List<Node>> czoneNodes = new HashMap<CZone, List<Node>>();

	/**
	 * Collect all nodes from given mesh
	 * @param mesh_
	 */
	void collectNodes(Mesh mesh)
	{
		Node node;
		int i;
		int s=mesh.nodes.size();
		//walk through all mesh nodes, looking for those under influence
		for(i=0; i<s; i++)
		{
			node = (Node)mesh.nodes.get(i);
			collectNode(node);
		}		
	}
	
	/**
	 * Get list of nodes for specified czone.
	 * If for this czone list not exists yet creates it.
	 * If czone is null or by any other reason it's imposible to 
	 * return list it returns null
	 */
	public List<Node> getCZoneNodesList(CZone czone)
	{
		if(czone==null) return null;
		List<Node> zoneNodes;
		zoneNodes =czoneNodes.get(czone);
		if(zoneNodes==null) 
		{
			zoneNodes=new ArrayList<Node>(10);
			czoneNodes.put(czone, zoneNodes);
		}
		return zoneNodes;
	}
	
	/**
	 * If node is some czone,  add it to collection
	 */
	private void collectNode(Node node)
	{
		if(node == null)
			return;
		List<Node> zoneNodes = getCZoneNodesList(node.czone);
		if(zoneNodes != null)
			zoneNodes.add(node);
	}
	
	/**
	 * Sort nodes of al czones - separate
	 */
	void sortAllNodes()
	{
		sortCZoneNodes(null);
	}
	
	/**
	 * Sort nodes, collected for specified czone, by their distance from czone begin
	 * If czone_==null, sort nodes for all czones 
	 * @param czone_
	 */
	void sortCZoneNodes(CZone czone_)
	{
		CZone czone;
		Map.Entry<CZone, List<Node>> entry;
		List<Node> zone_nodes;
		Set<Map.Entry<CZone, List<Node>>> czoneSet=czoneNodes.entrySet();
		for(Iterator<Map.Entry<CZone, List<Node>>> cz_it=czoneSet.iterator(); cz_it.hasNext(); ) //for all czones
		{
			entry = cz_it.next();
			czone= entry.getKey();
			if(czone_!=null&&czone_!=czone) continue; //skip not needed zones
			zone_nodes= entry.getValue();
			//here go sorting nodes for one czone
			Collections.sort(zone_nodes, 
				new Comparator<Node>()
				{public int compare(Node n1, Node n2)
					{ 
					 	if(n1.czoneOffset>n2.czoneOffset) return 1;
						if(n1.czoneOffset<n2.czoneOffset) return 0;
						return 0;
					}
				}
				);
			//end sorting nodes for one czone
			if(czone_!=null&&czone_==czone) break; //break if we've dealed with al zones we need to deal with
		}//end for all czones
	}
	
	/** Alias for outputSForces()
	 */
	String output(OutputStream out_)
	{return this.outputSForces(out_);
	}
	
	/**
	 * Output collected data to stream out_
	 * @return null on success, error message on failure
	 */
	String outputSForces(OutputStream out_)
	{
		PrintWriter out = new PrintWriter( out_, true);
		Set czoneSet = czoneNodes.entrySet();//get set of paris czone-nodes array
		Iterator i= czoneSet.iterator();
		CZone czone;
		ArrayList zone_nodes;
		Node node;
		while(i.hasNext())//for all czones -- force value
		{
			Map.Entry entry=(Map.Entry)i.next();
			czone=(CZone)(entry.getKey());
			if(czone.getInfluenceMode()==CZone.INFLUENCE_CONTACT) continue; //skip contact, deal only with force 
			zone_nodes=(ArrayList)(entry.getValue());
			node=(Node)zone_nodes.get(0);
			double force_dir[] = czone.forceDirection(node.offset);
			if(Math.abs(force_dir[0])>divider.Divider.GENERAL_ACCURACY &&
				Math.abs(force_dir[1])>divider.Divider.GENERAL_ACCURACY )//if we need to represent one force as two 
			{
				out.print(Divider.formatNumberS(czone.getForceValue()*force_dir[0])+" ");
				out.print(Divider.formatNumberS(czone.getForceValue()*force_dir[1]));
			}
			else
				{if(Math.abs(force_dir[1])>Math.abs(force_dir[0]))
					out.print(Divider.formatNumberS(czone.getForceValue()*force_dir[1]));
					else
					out.print(Divider.formatNumberS(czone.getForceValue()*force_dir[0]));
				}
		}//end for all czones -- force value
		out.println("");
		
		i= czoneSet.iterator();
		while(i.hasNext())//for all czones -- force value
		{
			Map.Entry entry=(Map.Entry)i.next();
			czone=(CZone)(entry.getKey());
			if(czone.getInfluenceMode()==CZone.INFLUENCE_CONTACT) continue; //skip contact, deal only with force 
			zone_nodes=(ArrayList)(entry.getValue());
			node=(Node)zone_nodes.get(0);

			out.print(getCZoneString(czone, zone_nodes));
		}//end for all czones -- nodes count
		
		i= czoneSet.iterator();
		while(i.hasNext())//for all czones -- nodes list
		{
			Map.Entry entry=(Map.Entry)i.next();
			czone=(CZone)(entry.getKey());
			if(czone.getInfluenceMode()==CZone.INFLUENCE_CONTACT) continue; //skip contact, deal only with force 
			zone_nodes=(ArrayList)(entry.getValue());
			node=(Node)zone_nodes.get(0);
			double force_dir[] = czone.forceDirection(node.offset);
			int k_c=1;
			if(Math.abs(force_dir[0])>divider.Divider.GENERAL_ACCURACY &&
				Math.abs(force_dir[1])>divider.Divider.GENERAL_ACCURACY )//if we need to represent one force as two 
				{k_c=2;}
			for(int k=0; k<k_c; k++) //this will print nodes for each czone one or two times (if forse is separated on 2 parts)
			{
				Iterator j = zone_nodes.iterator();
				while(j.hasNext())
				{
					node=(Node)j.next();
					out.print((node.index+1)+" ");	
				}
				out.println("");
			}
		}//end for all czones -- nodes list

		
		return null;	
	}//end output(...)


	/**
	 * get string with node count for given czone
	 * If force is not || to OX or OY, then write 2 lines 
	 */
	private static String getCZoneString(CZone czone, ArrayList zone_nodes)
	{
		StringBuffer result=new StringBuffer();
		Node node=(Node)zone_nodes.get(0);
		double force_dir[] = czone.forceDirection(node.offset);
		if(force_dir[0]>divider.Divider.GENERAL_ACCURACY) //+x direction
		{result.append( zone_nodes.size()+" 0 0 0\n");
		}//end if +x (true)
		else 
		{
			if(force_dir[0]<-divider.Divider.GENERAL_ACCURACY)//-x direction
			{result.append("0 "+zone_nodes.size()+" 0 0\n");
			}
		}//end if +x (false)
		if(force_dir[1]>divider.Divider.GENERAL_ACCURACY)//+y direction
		{result.append("0 0 "+zone_nodes.size()+" 0\n");
		}//end if +y (true)
		else
		{
			if(force_dir[1]<-divider.Divider.GENERAL_ACCURACY)//-y direction
			{result.append("0 0 0 "+zone_nodes.size()+"\n");
			}//end if -y (true)
		}//end if +y (false)
		
		return result.toString();
	}//end getCZoneString(..)
	
	

	public final Map<CZone, List<Node>> getCzoneNodes() {
		return czoneNodes;
	}
}//end class ForcesCollector
