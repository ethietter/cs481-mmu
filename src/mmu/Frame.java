package mmu;

public class Frame {

	//Reference to the pte that currently points to this frame
	private PTE pte;
	
	//Reference to the entry in the TLB that currently points to this frame
	private TLBEntry tlb_entry;
	
	//For use with LFU and MFU eviction schemes
	public long access_count;
	
	
	public Frame(PTE pte){
		LookupLogInfo.addDiskAccess();
		LookupLogInfo.addFrameAccess();
		this.pte = pte;
		this.access_count = 0;
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
		this.pte.present = true;
	}
	
	public void setTLBEntry(TLBEntry t){
		this.tlb_entry = t;
	}
	
	public void write(){
		pte.modified = true;
		LookupLogInfo.addPageTableAccess();
		LookupLogInfo.addFrameAccess();
		if(Settings.page_replacement == Settings.Policy.LFU || Settings.page_replacement == Settings.Policy.MFU){
			pte.access_count++;
		}
	}
	
	public void read(){
		LookupLogInfo.addFrameAccess();
		if(Settings.page_replacement == Settings.Policy.LFU || Settings.page_replacement == Settings.Policy.MFU){
			pte.access_count++;
		}
	}
	
	//For use with the LFU and MFU eviction policies
	public long getAccessCount(){
		if(pte != null){
			return pte.access_count;
		}
		if(Settings.page_replacement == Settings.Policy.LFU){
			return Long.MAX_VALUE;
		}
		else{
			return 0;
		}
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
