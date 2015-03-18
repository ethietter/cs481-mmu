package mmu;

public class Frame {

	//Reference to the pte that currently points to this frame
	private PTE pte;
	
	//Reference to the entry in the TLB that currently points to this frame
	private TLBEntry tlb_entry;
	
	
	public Frame(PTE pte){
		LookupLogInfo.addDiskAccess();
		LookupLogInfo.addFrameAccess();
		this.pte = pte;
	}
	
	public void evict(){
		LookupLogInfo.MemEviction.evicted = true;
		if(pte != null){
			LookupLogInfo.MemEviction.modified = pte.modified;
			LookupLogInfo.MemEviction.v_page = pte.page_num;
			LookupLogInfo.MemEviction.pid = pte.pid;
			pte.present = false;
			LookupLogInfo.addPageTableAccess();
			

	    	if(pte.modified){
	    		LookupLogInfo.addDirtyEviction();
	    	}
	    	else{
	    		LookupLogInfo.addCleanEviction();
	    	}
    		LookupLogInfo.addDiskAccess();
			//Simulator.frameEvicted(pte.pid, pte.modified);
			//Simulator.memReference(pte.pid);
			if(tlb_entry != null){
				TLB.removeEntry(tlb_entry);
			}
		}
		else{
			throw new UnsupportedOperationException("No virtual page occupies this frame");
		}
	}
	
	public void setPTE(PTE pte){
		this.pte = pte;
	}
	
	public void setTLBEntry(TLBEntry t){
		this.tlb_entry = t;
	}
	
	
	public void write(){
		pte.modified = true;
		LookupLogInfo.addPageTableAccess();
		LookupLogInfo.addFrameAccess();
	}
	
	public void read(){
		LookupLogInfo.addFrameAccess();
	}
	
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("{\n\t\tPTE=");
		str.append(pte.toString());
		str.append("\n\t\tTLBEntry=");
		if(tlb_entry != null){
			str.append(tlb_entry.toString());
		}
		else{
			str.append("null");
		}
		str.append("\n\t}");
		return str.toString();
	}
}
