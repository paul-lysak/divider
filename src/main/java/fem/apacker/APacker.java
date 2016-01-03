package fem.apacker;

import fem.apacker.model.Mesh;
import fem.apacker.model.ModelFacade;
import fem.apacker.out.IStreamer;
import fem.apacker.out.PmdStreamer;
import fem.apacker.out.TempNodesStreamer;
import fem.apacker.out.TempStreamer;
import fem.apacker.parsers.DParser;
import fem.apacker.parsers.EParser;
import fem.apacker.parsers.NParser;
import fem.apacker.parsers.SFParser;
import fem.apacker.parsers.TParser;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class APacker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CmdLineDetails cmdLineDetails = new CmdLineDetails(args);
        if(cmdLineDetails.isHelpMode())
        {
        	System.out.println(cmdLineDetails.getHelpText());
        	return;
        }

		try {
            cmdLineDetails.ensureNecessaryParametersExist();
        }
        catch(MissingParametersException e) {
            System.err.println("Error: some parameters missing: \n"+e.getMessage());
            System.err.print("Use parameter --help to get information about parameters");
            return;
        }
        
        
        Mesh mesh = new Mesh();
        ModelFacade modelFacade = new ModelFacade(mesh);
        
        //nodes
        NParser nparser = null;
        if(cmdLineDetails.getNListFile() != null)
        	nparser = new NParser(modelFacade, cmdLineDetails.getNListFile());
        //elements
        EParser eparser = null;
        if(cmdLineDetails.getEListFile() != null)
        	eparser = new EParser(modelFacade, cmdLineDetails.getEListFile());
        //constraints
        DParser dparser = null;
        if(cmdLineDetails.getDListFile() != null)
        	dparser = new DParser(modelFacade, cmdLineDetails.getDListFile());
        //loads
        SFParser sfparser = null;
        if(cmdLineDetails.getSfListFile() != null)
        	sfparser = new SFParser(modelFacade, cmdLineDetails.getSfListFile());
        //temperature
        TParser tparser = null;
        if(cmdLineDetails.getTempInFile() != null)
            tparser = new TParser(modelFacade, cmdLineDetails.getTempInFile());
        
        //output main
        IStreamer streamer = new PmdStreamer();
        streamer.setModelFacade(modelFacade);
        
        
        try {
        	if(nparser != null)
        		nparser.parse();
            if(eparser != null)
            	eparser.parse();
            if(dparser != null)
            	dparser.parse();
            if(sfparser != null)
            	sfparser.parse();
            
            if(tparser != null)
                tparser.parse();
            
            //output main
            if(cmdLineDetails.getPmdFile() != null) {
                PrintStream fpStream = new PrintStream(new FileOutputStream(cmdLineDetails.getPmdFile()));
                streamer.setPrintStream(fpStream);
                }
            else
                streamer.setPrintStream(System.out);                        
            streamer.write();
            
            
            //output temperature
            if(cmdLineDetails.getTempOutFile() != null) {
                PrintStream fpStream = new PrintStream(new FileOutputStream(cmdLineDetails.getTempOutFile()));
                IStreamer tstreamer = new TempStreamer();
                tstreamer.setModelFacade(modelFacade);
                tstreamer.setPrintStream(fpStream);
                tstreamer.write();
            }
            if(cmdLineDetails.getTempNodesOutFile() != null) {
                PrintStream fpStream = new PrintStream(new FileOutputStream(cmdLineDetails.getTempNodesOutFile()));
                IStreamer tstreamer = new TempNodesStreamer();
                tstreamer.setModelFacade(modelFacade);
                tstreamer.setPrintStream(fpStream);
                tstreamer.write();
            }                    

        }
        catch(FileNotFoundException e)
        {
            System.err.println("File not found: "+e.getMessage());
//                    e.printStackTrace();
            return;
        }
        catch(IOException e)
        {
            System.err.println("I/O exception: "+e.getMessage());
            e.printStackTrace();
            return;
        }
        
        System.out.println("APacker successfully ended");
	}//end method main

}
