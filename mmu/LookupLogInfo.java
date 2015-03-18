package mmu;

public class LookupLogInfo {

	//Record everything about the trace here, and then use that to figure out what memory/disk accesses were performed
	
	//Static class to keep track of what happened during a lookup.
	//This is "acceptable" (it'll work at least) because only one lookup is performed
	//at a time

	public static AddressTrace trace;
	public static boolean tlb_hit;
	public static boolean page_fault;
	public static int v_page;
	public static int frame;
	
	private static int tlb_accesses = 0;
	private static int page_table_accesses = 0;
	private static int frame_accesses = 0;
	private static int disk_accesses = 0;
	private static int clean_evictions = 0;
	private static int dirty_evictions = 0;
	
	public static void reset(){
		trace = null;
		tlb_hit = false;
		page_fault = false;
		v_page = 0;
		frame = 0;
		tlb_accesses = 0;
		page_table_accesses = 0;
		frame_accesses = 0;
		disk_accesses = 0;
		clean_evictions = 0;
		dirty_evictions = 0;
		TLBEviction.evicted = false;
		TLBEviction.v_page = 0;
		
		MemEviction.evicted = false;
		MemEviction.v_page = 0;
		MemEviction.modified = false;
		MemEviction.pid = 0;
	}
	
	public static class MemEviction{
		public static boolean evicted = false;
		public static boolean modified = false;
		public static int pid = 0;
		public static int v_page = 0;
	}
	
	public static class TLBEviction{
		public static boolean evicted = false;
		public static int v_page = 0;
	}

	
	public static void addTLBAccess(){
		tlb_accesses++;
	}
	
	public static void addPageTableAccess(){
		page_table_accesses++;
	}
	
	public static void addFrameAccess(){
		frame_accesses++;
	}
	
	public static void addDiskAccess(){
		disk_accesses++;
	}
	
	public static void addCleanEviction(){
		clean_evictions++;
	}
	
	public static void addDirtyEviction(){
		dirty_evictions++;
	}
	
	public static void logState(SummaryData data){
		/* Consolidate all of the data recorded for this trace */
		data.mem_references += (page_table_accesses + frame_accesses);
		data.dirty_evictions += dirty_evictions;
		data.clean_evictions += clean_evictions;
		if(!tlb_hit) data.tlb_misses++;
		if(page_fault) data.page_faults++;
		
		int trace_latency = 0;
		trace_latency += (page_table_accesses + frame_accesses)*Settings.memory_latency; //Already in nanoseconds
		trace_latency += disk_accesses*(Settings.disk_latency*Math.pow(10, 6)); //Convert milliseconds to nanoseconds
		trace_latency += tlb_accesses*Settings.tlb_latency; //Already in nanoseconds
		data.running_latency += trace_latency;
		
		/*
		 * Log #1 as defined in specs
		 */
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
		Simulator.log("Process[" + trace.pid + "]: " + op + " " + Utils.getHex(trace.v_address) +
			" (page: " + Utils.getPage(trace.v_address) + ", offset: " + Utils.getOffset(trace.v_address) + ")\n");
		
		/*
		 * Log #2 as defined in the specs
		 */
		Simulator.log("\tTLB hit? " + (tlb_hit ? "yes" : "no") + "\n");
		
		/*
		 * Log #3 as defined in the specs.
		 * Only occurs on a TLB hit
		 */
		if(!tlb_hit){
			Simulator.log("\tPage fault? " + (page_fault ? "yes" : "no") + "\n");
			
			/*
			 * Log #4 as defined in the specs.
			 * Only occurs on a page fault
			 */
			if(page_fault){
				Simulator.log("\tMain memory eviction? " + (MemEviction.evicted ? "yes" : "no") + "\n");
				
				/*
				 * Log #5 as defined in the specs.
				 * Only occurs on a main memory eviction
				 */
				if(MemEviction.evicted){
					Simulator.log("\tProcess " + MemEviction.pid + " page " + MemEviction.v_page + 
								  " (" + (MemEviction.modified ? "dirty" : "clean") + ") evicted from memory\n");							  
				}
			}
			
			/*
			 * Log #6 as defined in the specs.
			 * Only occurs on a tlb miss
			 */
			Simulator.log("\tTLB eviction? " + (TLBEviction.evicted ? "yes" : "no") + "\n");
			
			/*
			 * Log #7 as defined in the specs.
			 * Only occurs on a TLB eviction
			 */
			if(TLBEviction.evicted){
				Simulator.log("\tpage " + TLBEviction.v_page + " evicted from TLB\n");
			}
		}
		
		/*
		 * Log #8 as defined in the specs
		 */
		Simulator.log("\tpage " + v_page + " in frame " + frame + "\n");
	}
	
	
}
