package mmu;

public class SummaryData {
	
	int mem_references = 0;
	int tlb_misses = 0;
	int page_faults = 0;
	int clean_evictions = 0;
	int dirty_evictions = 0;
	int running_latency = 0;
	
	public SummaryData(){
		
	}
}
