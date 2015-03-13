package mmu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class Simulator {

	private static HashMap<Integer, PageTable> page_tables = new HashMap<Integer, PageTable>();
	private static HashMap<Integer, SummaryData> summaries = new HashMap<Integer, SummaryData>();
	private static int curr_process;
	
	private static long running_latency = 0; //In nanoseconds
	private static int real_mem_ref_count = 0; //*Actual* memory references that the process knows about
	private static int disk_accesses = 0;
	private static int mem_accesses = 0;
	private static int tlb_accesses = 0;
	
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
    	    	real_mem_ref_count++;
    	    	startLookup(parseAsTrace(line));
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
	
	public static void log(String str){
		if(Settings.log_output) System.err.print(str);
	}

	private static void startLookup(AddressTrace trace){
		LookupLogInfo.reset();
		String op = "";
		switch(trace.op){
			case R:
				op = "Load from";
				break;
			case W:
				op = "Store to";
				break;
			case I:
				op = "Instruction fetch from";
				break;
		}
		log("Process[" + trace.pid + "]: " + op + " " + Utils.getHex(trace.v_address) +
			" (page: " + Utils.getPage(trace.v_address) + ", offset: " + Utils.getOffset(trace.v_address) + ")\n");
		
		doLookup(trace);
		LookupLogInfo.logState();
	}
	
	private static void doLookup(AddressTrace trace){
		doLookup(trace, false);
	}
	
	//The is_recursive parameter lets us know if this is the first call to doLookup.
	//If so, it will log TLB hit info
    private static void doLookup(AddressTrace trace, boolean is_recursive){
    	
    	if(curr_process != trace.pid){
    		TLB.flush();
    		curr_process = trace.pid;
    	}
    	TLBEntry entry = TLB.lookup(Utils.getPage(trace.v_address));
    	if(entry != null){//TLB hit
    		if(!is_recursive) LookupLogInfo.tlb_hit = true;
    		
    		if(trace.op.equals(AddressTrace.Op.I) || trace.op.equals(AddressTrace.Op.R)){
    			Memory.readFrame(entry.physical_frame, trace.pid);
    		}
    		if(trace.op.equals(AddressTrace.Op.W)){
    			Memory.writeFrame(entry.physical_frame, trace.pid);
    		}
        	//hardwareDump();
    	}
    	else{//TLB miss
    		if(!is_recursive) {
    			LookupLogInfo.tlb_hit = false;
    		}
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
    		doLookup(trace, true);
    	}
    }
    
    public static void pageFault(int pid){
    	summaries.get(pid).page_faults++;
    }
    
    
    public static void frameEvicted(int pid, boolean is_dirty){
    	if(is_dirty){
    		summaries.get(pid).dirty_evictions++;
    		diskAccess();
    	}
    	else{
    		summaries.get(pid).clean_evictions++;
    	}
    }
    
    public static void tlbMiss(int pid){
    	summaries.get(pid).tlb_misses++;
    }

    public static void memReference(int pid){
    	summaries.get(pid).mem_references++;
    	running_latency += Settings.memory_latency; //Already in nanoseconds
    	mem_accesses++;
    }
    
    public static void diskAccess(){
    	running_latency += Settings.disk_latency * Math.pow(10, 6); //Convert milliseconds to nanoseconds
    	disk_accesses++;
    }
    
    public static void tlbAccess(){
    	running_latency += Settings.tlb_latency; //Already in nanoseconds
    	tlb_accesses++;
    }
    
    public static void printSummary(){
    	double overall_latency = running_latency/((double) 1000000);
    	double avg_latency = overall_latency/real_mem_ref_count;
    	double slowdown = overall_latency/(real_mem_ref_count*((float) Settings.memory_latency)/1000000);
    	
    	//System.out.println("Mem=" + mem_accesses + " Disk=" + disk_accesses + " TLB=" + tlb_accesses);
    	
    	System.out.println("Overall latency (milliseconds): " + String.format("%.6f", overall_latency) + ".");
    	System.out.println("Average memory access latency (milliseconds/reference): " + String.format("%.6f", avg_latency) + ".");
    	System.out.println("Slowdown: " + String.format("%.2f", slowdown) + ".");
    	System.out.println("");
    	
    	int o_mem_references = 0;
    	int o_tlb_misses = 0;
    	int o_page_faults = 0;
    	int o_clean_evictions = 0;
    	int o_dirty_evictions = 0;
    	double o_percent_dirty = 0;
    	
    	int[] process_ids = new int[summaries.size()];
    	int pid_index = 0;

    	for (Entry<Integer, SummaryData> entry : summaries.entrySet()) {
    		SummaryData summary = entry.getValue();
    		o_mem_references	+= summary.mem_references;
    		o_tlb_misses 		+= summary.tlb_misses;
    		o_page_faults		+= summary.page_faults;
    		o_clean_evictions	+= summary.clean_evictions;
    		o_dirty_evictions	+= summary.dirty_evictions;
    		process_ids[pid_index] = entry.getKey();
    		pid_index++;
    	}
    	
    	if(o_clean_evictions + o_dirty_evictions != 0){
    		o_percent_dirty = 100*o_dirty_evictions/(o_clean_evictions + o_dirty_evictions);
    	}
    	System.out.println("Overall");
    	System.out.println("\tMemory References: " + o_mem_references);
    	System.out.println("\tTLB misses: " + o_tlb_misses);
    	System.out.println("\tPage faults: " + o_page_faults);
    	System.out.println("\tClean evictions: " + o_clean_evictions);
    	System.out.println("\tDirty evictions: " + o_dirty_evictions);
    	System.out.println("\tPercentage dirty evictions: " + String.format("%.2f", o_percent_dirty) + "%");
    	System.out.println("");
    	
    	Arrays.sort(process_ids);
    	for(int i = 0; i < process_ids.length; i++){
    		printProcessSummary(process_ids[i], summaries.get(process_ids[i]));
    	}
    	
    }
    
    private static void printProcessSummary(int pid, SummaryData summary){
    	System.out.println("Process " + pid);
    	System.out.println("\tMemory References: " + summary.mem_references);
    	System.out.println("\tTLB misses: " + summary.tlb_misses);
    	System.out.println("\tPage faults: " + summary.page_faults);
    	System.out.println("\tClean evictions: " + summary.clean_evictions);
    	System.out.println("\tDirty evictions: " + summary.dirty_evictions);

    	double percent_dirty = 0;
    	if(summary.clean_evictions + summary.dirty_evictions != 0){
    		percent_dirty = 100*summary.dirty_evictions/(summary.clean_evictions + summary.dirty_evictions);
    	}
    	
    	System.out.println("\tPercentage dirty evictions: " + String.format("%.2f", percent_dirty) + "%");
    	System.out.println("");
    }
    
    private static AddressTrace parseAsTrace(String line){
    	String[] exploded = line.split(" ");
    	String addr = exploded[2];
    	addr = addr.substring(2);
    	return new AddressTrace(Integer.valueOf(exploded[0]), exploded[1].charAt(0), Long.parseLong(addr, 16));
    }
}
