package mmu;


public class PageTable{

    int pid;
    
    SparseArray<PTE> contents = new SparseArray<PTE>();
    
    public PageTable(int pid){
    	this.pid = pid;
    }
    
    public PTE getPTE(long v_address){
    	LookupLogInfo.addPageTableAccess();
    	int page_num = Utils.getPage(v_address);
    	PTE pte;
    	
    	//This virtual page has never been accessed so we need to allocate a
    	//frame for it in physical memory
    	if((pte = contents.get(page_num)) == null){
    		pte = new PTE();
    		pte.setTranslation(page_num, Memory.allocateFrame(pte, pid), pid);
    		contents.add(page_num, pte);
    	}
    	else {
    		pte = contents.get(page_num);
    		if(!pte.present){ //This virtual page is not resident in memory, so allocate a frame for it
    			pte.setTranslation(page_num, Memory.allocateFrame(pte, pid), pid);
    		}
    	}
    	
    	return pte;
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
