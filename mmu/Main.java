package mmu;

import java.util.Calendar;


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
    	
    	//Settings.print();
    	preSimOutput();
		
    	Simulator.init();
		
		long t_start = Calendar.getInstance().getTimeInMillis();
		if(!Simulator.run(trace_path)) printHelp();
		long t_end = Calendar.getInstance().getTimeInMillis();
		
		Simulator.printSummary();
		
		System.out.println("Simulation run time: " + (t_end - t_start));
    }
    
    public static void printHelp(){
    	System.out.println("Usage: mmu <config_file> <trace_file>");
    	System.exit(1);
    }
    
    public static void preSimOutput(){
    	System.out.println("Page bits: " + Settings.frame_bits);
    	System.out.println("Offset bits: " + (Settings.address_size - Settings.frame_bits));
    	System.out.println("TLB size: " + Settings.tlb_size);
    	System.out.println("TLB latency (milliseconds): " + String.format("%.6f", Settings.tlb_latency/((double) 1000000)));
    	System.out.println("Physical memory (bytes): " + Settings.physical_size);
    	System.out.println("Physical frame size (bytes): " + Settings.frame_size);
    	System.out.println("Number of physical frames: " + Settings.physical_size/Settings.frame_size);
    	System.out.println("Memory latency (milliseconds): " + String.format("%.6f", Settings.memory_latency/((double) 1000000)));
    	System.out.println("Number of page table entries: " + String.format("%.0f", Math.pow(2, Settings.address_size)/Settings.frame_size));
    	System.out.println("Page replacement strategy: " + Settings.page_replacement);
    	System.out.println("Disk latency (milliseconds): " + String.format("%.2f", (double) Settings.disk_latency));
    	System.out.println("Logging: " + (Settings.log_output ? "on" : "off"));
    }
    

}
