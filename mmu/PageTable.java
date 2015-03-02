package mmu;


public class PageTable{

    int pid;
    
    SparseArray<PTE> contents = new SparseArray<PTE>();
    
    public PageTable(int pid){
    	this.pid = pid;
    }
    
    public PTE getPTE(int v_address){
    	int page_num = Settings.getPage(v_address);
    	PTE item;
    	
    	//This virtual page has never been accessed so we need to allocate a
    	//frame for it in physical memory
    	if((item = contents.get(page_num)) == null){
    		item = new PTE(page_num, Memory.allocateFrame());
    		contents.add(page_num, item);
    	}
    	return item;
    }
    
    public String toString(){
    	StringBuilder str = new StringBuilder();
    	str.append("{\n\tpid=" + pid + ",\n\t[");
    	for(int i = 0; i < contents.size(); i++){
    		PTE item = contents.get(i);
    		if(item == null) continue;
    		
    		str.append("\n\t\t");
    		str.append(String.format("0x%08X", i) + ": ");
    		str.append(item);
    		str.append(",");
    	}
    	str.append("\n\t]");
    	str.append("\n}");
    	return str.toString();
    }
}
