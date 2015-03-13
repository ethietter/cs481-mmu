package mmu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Simulator {

	private static HashMap<Integer, PageTable> page_tables = new HashMap<Integer, PageTable>();
	private static HashMap<Integer, SummaryData> summaries = new HashMap<Integer, SummaryData>();
	private static int curr_process;
	
	private Simulator(){
	}
	
	public static void init(){
		
	}
	
	public static boolean run(String trace_path){
		try {
    		BufferedReader reader = new BufferedReader(new FileReader(trace_path));
    	    String line;
    	    while ((line = reader.readLine()) != null) {
    	    	//System.out.println(parseAsTrace(line));
    	    	doLookup(parseAsTrace(line));
    	    }
    	    reader.close();
    	}
    	catch (IOException e){
    		System.out.println("Error reading trace file: " + e.getMessage());
    		return false;
    	}
		return true;
		
	}
	
	public static void hardwareDump(){
		System.out.println("Page tables: \n" + page_tables);
		System.out.println("Memory: ");
		Memory.print();
		System.out.println("TLB: ");
		System.out.println(TLB.getString());
		System.out.println("LRU List: " + Memory.lru_list);
		System.out.println("\n***********************************************************************************************************\n\n");
	}

    public static void doLookup(AddressTrace trace){
    	if(curr_process != trace.pid){
    		TLB.flush();
    		curr_process = trace.pid;
    	}
    	TLBEntry entry = TLB.lookup(Utils.getPage(trace.v_address));
    	if(entry != null){//TLB hit
    		memReference(trace.pid);
        	hardwareDump();
    	}
    	else{//TLB miss
    		PageTable curr_table = page_tables.get(trace.pid);
    		//If curr_table doesn't exist, this process has never been accessed
    		//so the page table needs to be created, along with a SummaryData object
    		if(curr_table == null){
    			summaries.put(trace.pid, new SummaryData());
    			curr_table = new PageTable(trace.pid);
    			page_tables.put(trace.pid, curr_table);
    		}
    		tlbMiss(trace.pid);
    		PTE pte = curr_table.getPTE(trace.v_address);
    		entry = new TLBEntry(pte.page_num, pte.frame_num);
    		Memory.getFrame(pte.frame_num).setTLBEntry(entry);
    		TLB.addEntry(entry);
    		doLookup(trace);
    	}
    }
    
    public static void pageFault(int pid){
    	summaries.get(pid).page_faults++;
    }
    
    public static void memReference(int pid){
    	summaries.get(pid).mem_references++;
    }
    
    public static void frameEvicted(int pid, boolean is_dirty){
    	if(is_dirty){
    		summaries.get(pid).dirty_evictions++;
    	}
    	else{
    		summaries.get(pid).clean_evictions++;
    	}
    }
    
    public static void tlbMiss(int pid){
    	summaries.get(pid).tlb_misses++;
    }
    
    public static void printSummary(){
    	int overall_latency = 0;
    	int avg_latency = 0;
    	int slowdown = 0;
    	
    	System.out.println("Overall latency (milliseconds): " + overall_latency);
    	System.out.println("Average memory access latency (milliseconds/reference): " + avg_latency);
    	System.out.println("Slowdown: " + slowdown);
    	System.out.println("");
    	
    	int o_mem_references = 0;
    	int o_tlb_misses = 0;
    	int o_page_faults = 0;
    	int o_clean_evictions = 0;
    	int o_dirty_evictions = 0;
    	int o_percent_dirty = 0;
    	
    	System.out.println("Overall");
    	System.out.println("\tMemory References: " + o_mem_references);
    	System.out.println("\tTLB misses: " + o_tlb_misses);
    	System.out.println("\tPage faults: " + o_page_faults);
    	System.out.println("\tClean evictions: " + o_clean_evictions);
    	System.out.println("\tDirty evictions: " + o_dirty_evictions);
    	System.out.println("\tPercentage dirty evictions: " + o_percent_dirty);
    	System.out.println("");
    	
    	for (Entry<Integer, SummaryData> entry : summaries.entrySet()) {
        	printProcessSummary(entry.getKey(), entry.getValue());
    	}
    }
    
    private static void printProcessSummary(int pid, SummaryData summary){
    	System.out.println("Process " + pid);
    	System.out.println("\tMemory References: " + summary.mem_references);
    	System.out.println("\tTLB misses: " + summary.tlb_misses);
    	System.out.println("\tPage faults: " + summary.page_faults);
    	System.out.println("\tClean evictions: " + summary.clean_evictions);
    	System.out.println("\tDirty evictions: " + summary.dirty_evictions);
    	System.out.println("\tPercentage dirty evictions: " + ((float) summary.dirty_evictions)/(summary.clean_evictions + summary.dirty_evictions));
    	System.out.println("");
    }
    
    private static AddressTrace parseAsTrace(String line){
    	String[] exploded = line.split(" ");
    	String addr = exploded[2];
    	addr = addr.substring(2);
    	return new AddressTrace(Integer.valueOf(exploded[0]), exploded[1].charAt(0), Long.parseLong(addr, 16));
    }
}
