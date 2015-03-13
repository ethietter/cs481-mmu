package mmu;


public class PageTable{

    int pid;
    
    SparseArray<PTE> contents = new SparseArray<PTE>();
    
    public PageTable(int pid){
    	this.pid = pid;
    }
    
    //Originally this just returned the valid PTE, hiding everything from the calling function. However,
    //the calling function is logging stuff, so it needs to know if it page faulted. This is the least-hackish
    //way I could think of managing that. Thought about introducing some global state variable, but that could
    //have gotten dangerous in a hurry.
    public LookupRecord getPTE(long v_address){
    	int page_num = Utils.getPage(v_address);
    	LookupRecord record = new LookupRecord();
    	
    	//This virtual page has never been accessed so we need to allocate a
    	//frame for it in physical memory
    	if((record.pte = contents.get(page_num)) == null){
    		record.pte = new PTE();
    		record.pte.setTranslation(page_num, Memory.allocateFrame(record.pte, pid), pid);
    		record.did_fault = true;
    		contents.add(page_num, record.pte);
    	}
    	else {
    		record.pte = contents.get(page_num);
    		record.did_fault = false;
    		if(!record.pte.present){ //This virtual page is not resident in memory, so allocate a frame for it
    			record.pte.setTranslation(page_num, Memory.allocateFrame(record.pte, pid), pid);
    			record.did_fault = true;
    		}
    	}
    	
    	Simulator.memReference(pid);
    	
    	return record;
    }
    
    public String toString(){
    	StringBuilder str = new StringBuilder();
    	str.append("{\n\tpid=" + pid + ",\n\t[");
    	for(int i = 0; i < contents.size(); i++){
    		PTE pte = contents.get(i);
    		if(pte == null) continue;
    		
    		str.append("\n\t\t");
    		str.append(String.format("0x%08X", i) + ": ");
    		str.append(pte);
    		str.append(",");
    	}
    	str.append("\n\t]");
    	str.append("\n}");
    	return str.toString();
    }
    
    class LookupRecord {
    	public PTE pte; //The actual PTE we need
    	public boolean did_fault; //Tells the caller if a page fault happened
    }
}
