package mmu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.AbstractMap;

public final class Settings {

    public enum Policy{
        RANDOM, FIFO, LRU, LFU, MFU
    }

    public static final int address_size = 32; //32 bit virtual addresses
    public static long physical_size;
    public static long frame_size;
    public static int frame_bits; //How many bits are needed to choose a frame = lg(frame_size)
    public static int memory_latency; //in nanoseconds
    public static Policy page_replacement;
    public static long tlb_size;
    public static int tlb_latency; //in nanoseconds
    public static int disk_latency; //in milliseconds
    public static boolean log_output;
    public static long offset_mask;

    //Don't instantiate this class
    private Settings(){ }

    public static Boolean load(String config_path){
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(config_path));
    	    String line;
    	    while ((line = reader.readLine()) != null) {
    	    	assignVariable(parseLine(line));
    	    }
    	    reader.close();
    	}
    	catch (Exception e){
    		System.out.println("Error reading config file: " + e.getMessage());
    		return false;
    	}

        frame_bits = (int) (Math.log(frame_size)/Math.log(2));
        int offset_bits = address_size - frame_bits;
        offset_mask = 0;
        for(int i = 0; i < offset_bits; i++){
        	offset_mask = offset_mask << 1;
        	offset_mask++;
        }
        return true;
    }
    
    public static void print(){
    	System.out.println("Physical Size: " + physical_size);
    	System.out.println("Frame Size: " + frame_size);
    	System.out.println("Frame Bits: " + frame_bits);
    	System.out.println("Memory Latency: " + memory_latency);
    	System.out.println("Page Replacement: " + page_replacement.toString());
    	System.out.println("TLB Size: " + tlb_size);
    	System.out.println("TLB Latency: " + tlb_latency);
    	System.out.println("Disk Latency: " + disk_latency);
    	System.out.println("Log Output: " + log_output);
    }
    
    private static AbstractMap.SimpleEntry<String, String> parseLine(String line){
    	int index = 0;
    	StringBuilder item = new StringBuilder();
    	AbstractMap.SimpleEntry<String, String> kv = null;
    	while(index < line.length()){
    		char c = line.charAt(index);
    		if(c == ':'){
    			kv = new AbstractMap.SimpleEntry<String, String>(item.toString(), "");
    			index++; //Skip the space
    			item = new StringBuilder();//reset item
    		}
    		else{
    			item.append(c);
    		}
    		index++;
    	}
    	kv.setValue(item.toString());
    	return kv;
    }
    
    private static void assignVariable(AbstractMap.SimpleEntry<String, String> kv){
    	switch(kv.getKey()){
    		case "physical-memory-size":
    			physical_size = Long.valueOf(kv.getValue());
    			break;
    		case "frame-size":
    			frame_size = Long.valueOf(kv.getValue());
    			break;
    		case "memory-latency":
    			memory_latency = Integer.valueOf(kv.getValue());
    			break;
    		case "page-replacement":
    			page_replacement = Policy.valueOf(kv.getValue());
    			break;
    		case "tlb-size":
    			tlb_size = Long.valueOf(kv.getValue());
    			break;
    		case "tlb-latency":
    			tlb_latency = Integer.valueOf(kv.getValue());
    			break;
    		case "disk-latency":
    			disk_latency = Integer.valueOf(kv.getValue());
    			break;
    		case "logging-output":
    			log_output = "on".equals(kv.getValue()) ? true : false;
    			break;
    	}
    }

}
