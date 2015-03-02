package mmu;

public class Frame {

	//Reference to the pte that currently points to this frame
	private PTE pte;
	
	public Frame(PTE pte){
		this.pte = pte;
	}
	
	public Frame(){
		
	}
	
	public void evict(){
		if(pte != null){
			pte.present = false;
		}
		else{
			throw new UnsupportedOperationException("No virtual page occupies this frame");
		}
	}
	
	public void setPTE(PTE pte){
		this.pte = pte;
	}
	
	public String toString(){
		return pte.toString();
	}
}
