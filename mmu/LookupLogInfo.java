package mmu;

public class LookupLogInfo {

	//Static class to keep track of what happened during a lookup.
	//This is "acceptable" (it'll work at least) because only one lookup is performed
	//at a time

	public static boolean tlb_hit;
	public static boolean page_fault;
	public static int v_page;
	public static int frame;
	
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

	public static void reset(){
		tlb_hit = false;
		page_fault = false;
		v_page = 0;
		frame = 0;
		TLBEviction.evicted = false;
		TLBEviction.v_page = 0;
		
		MemEviction.evicted = false;
		MemEviction.v_page = 0;
		MemEviction.modified = false;
		MemEviction.pid = 0;
	}
	
	public static void logState(){
		Simulator.log("\tTLB hit? " + (tlb_hit ? "yes" : "no") + "\n");
		if(!tlb_hit){
			Simulator.log("\tPage fault? " + (page_fault ? "yes" : "no") + "\n");
			if(page_fault){
				Simulator.log("\tMain memory eviction? " + (MemEviction.evicted ? "yes" : "no") + "\n");
				if(MemEviction.evicted){
					Simulator.log("\tProcess " + MemEviction.pid + " page " + MemEviction.v_page + 
								  " (" + (MemEviction.modified ? "dirty" : "clean") + ") evicted from memory\n");							  
				}
			}
			Simulator.log("\tTLB eviction? " + (TLBEviction.evicted ? "yes" : "no") + "\n");
			if(TLBEviction.evicted){
				Simulator.log("\tpage " + TLBEviction.v_page + " evicted from TLB\n");
			}
		}
		Simulator.log("\tpage " + v_page + " in frame " + frame + "\n");
	}
	
	
}
