package mmu;

public class Frame {

	//Reference to the pte that currently points to this frame
	private PTE pte;
	
	//Reference to the entry in the TLB that currently points to this frame
	private TLBEntry tlb_entry;
	
	
	
	public Frame(PTE pte){
		this.pte = pte;
	}
	
	public Frame(PTE pte, TLBEntry tlb_entry){
		this.pte = pte;
		this.tlb_entry = tlb_entry;
	}
	
	public Frame(){
		
	}
	
	public void evict(){
		if(pte != null){
			pte.present = false;
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
