package mmu;

public class TLBEntry {


    int physical_frame; //Physical frame number
    int virtual_page;   //Virtual page number

    public TLBEntry(int page, int frame){
    	virtual_page = page;
    	physical_frame = frame;
    }
    
    public String toString(){
    	return (String.format("0x%08X", virtual_page) + "=>" + String.format("0x%08X", physical_frame));
    }
}
