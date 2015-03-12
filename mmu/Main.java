package mmu;


public class Main{

    public static void main(String[] args){
    	
    	if(args.length != 2){
    		printHelp();
    	}
    	
    	String config_path = args[0];
    	String trace_path = args[1];
    	
    	if(!Settings.load(config_path)) printHelp();
    	
		Memory.init();
    	TLB.init();
    	
    	Settings.print();
		
		Simulator sim = new Simulator();
		if(!sim.run(trace_path)) printHelp();
    }
    
    public static void printHelp(){
    	System.out.println("Usage: mmu <config_file> <trace_file>");
    	System.exit(1);
    }
    

}
