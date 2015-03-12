package mmu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class Simulator {

	private HashMap<Integer, PageTable> page_tables = new HashMap<Integer, PageTable>();
	private int curr_process;
	
	public Simulator(){
		
	}
	
	public boolean run(String trace_path){
		try {
    		BufferedReader reader = new BufferedReader(new FileReader(trace_path));
    	    String line;
    	    while ((line = reader.readLine()) != null) {
    	    	//System.out.println(parseAsTrace(line));
    	    	doLookup(parseAsTrace(line));
    	    }
    	    reader.close();
    	}
    	catch (Exception e){
    		System.out.println("Error reading trace file: " + e.getMessage());
    		return false;
    	}
		return true;
		
	}
	
	public void hardwareDump(){
		System.out.println("Page tables: \n" + page_tables);
		System.out.println("Memory: ");
		Memory.print();
		System.out.println("TLB: ");
		System.out.println(TLB.getString());
		System.out.println("LRU List: " + Memory.lru_list);
		System.out.println("\n***********************************************************************************************************\n\n");
	}

    public void doLookup(AddressTrace trace){
    	if(curr_process != trace.pid){
    		TLB.flush();
    		curr_process = trace.pid;
    	}
    	TLBEntry entry = TLB.lookup(Utils.getPage(trace.v_address));
    	if(entry != null){//TLB hit
    		//System.out.println("Hit");
        	hardwareDump();
    		//System.out.println(Utils.getHex(Utils.getPage(trace.v_address)));
    		//System.out.println(TLB.getString());
    		//System.out.println("---------------------");
    	}
    	else{//TLB miss
    		//System.out.println("Miss");
    		PageTable curr_table = page_tables.get(trace.pid);
    		//If curr_table doesn't exist, this process has never been accessed
    		//so the page table needs to be created
    		if(curr_table == null){
    			curr_table = new PageTable(trace.pid);
    			page_tables.put(trace.pid, curr_table);
    		}
    		PTE pte = curr_table.getPTE(trace.v_address);
    		entry = new TLBEntry(pte.page_num, pte.frame_num);
    		Memory.getFrame(pte.frame_num).setTLBEntry(entry);
    		TLB.addEntry(entry);
    		doLookup(trace);
    	}
    }
    
    private AddressTrace parseAsTrace(String line){
    	String[] exploded = line.split(" ");
    	String addr = exploded[2];
    	addr = addr.substring(2);
    	return new AddressTrace(Integer.valueOf(exploded[0]), exploded[1].charAt(0), Long.parseLong(addr, 16));
    }
}
