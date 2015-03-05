package mmu;

import java.util.HashMap;

public class Simulator {

	private HashMap<Integer, PageTable> page_tables = new HashMap<Integer, PageTable>();
	private int curr_process;
	
	public Simulator(){
		
	}
	
	public void run(){
		AddressTrace t1 = new AddressTrace(1, 'R', 0xA123451);
		
		
		AddressTrace t2 = new AddressTrace(1, 'R', 0xB123452);
		AddressTrace t3 = new AddressTrace(1, 'R', 0xC123453);
		AddressTrace t4 = new AddressTrace(1, 'R', 0xF123454);
		AddressTrace t5 = new AddressTrace(2, 'R', 0xD123455);
		
		doLookup(t1);
		
		
		doLookup(t2);
		
		doLookup(t3);
		doLookup(t4);
		doLookup(t5);
		
		
	}
	
	public void hardwareDump(){
		System.out.println("Page tables: \n" + page_tables);
		System.out.println("Memory: ");
		Memory.print();
		System.out.println("TLB: ");
		System.out.println(TLB.getString());
		System.out.println("\n***********************************************************************************************************\n\n");
	}

    public void doLookup(AddressTrace trace){
    	if(curr_process != trace.pid){
    		TLB.flush();
    		curr_process = trace.pid;
    	}
    	TLBEntry entry = TLB.lookup(Utils.getPage(trace.v_address));
    	if(entry != null){//TLB hit
    		System.out.println("Hit");
        	hardwareDump();
    		//System.out.println(Utils.getHex(Utils.getPage(trace.v_address)));
    		//System.out.println(TLB.getString());
    		//System.out.println("---------------------");
    	}
    	else{//TLB miss
    		System.out.println("Miss");
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
}
