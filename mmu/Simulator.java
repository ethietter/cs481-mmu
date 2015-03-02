package mmu;

import java.util.HashMap;

public class Simulator {

	private TLB tlb;
	private HashMap<Integer, PageTable> page_tables = new HashMap<Integer, PageTable>();
	
	public Simulator(){
		tlb = new TLB();
	}
	
	public void run(){
		AddressTrace t1 = new AddressTrace(1, 'R', 0xAAECE01);
		
		
		AddressTrace t2 = new AddressTrace(1, 'R', 0xBAECE02);
		AddressTrace t3 = new AddressTrace(1, 'R', 0xCAECE03);
		AddressTrace t4 = new AddressTrace(1, 'R', 0xFAECE04);
		AddressTrace t5 = new AddressTrace(2, 'R', 0xDAECE05);
		
		doLookup(t1);
		
		
		doLookup(t2);
		
		doLookup(t3);
		doLookup(t4);
		doLookup(t5);
		
		
		System.out.println("----------------------");
		System.out.println(page_tables);
		Memory.print();
	}

    public void doLookup(AddressTrace trace){
    	TLBEntry entry = tlb.lookup(Utils.getPage(trace.v_address));
    	if(entry != null){//TLB hit
    		System.out.println(Utils.getHex(Utils.getPage(trace.v_address)));
    		System.out.println(tlb);
    		System.out.println("---------------------");
    	}
    	else{//TLB miss
    		PageTable curr_table = page_tables.get(trace.pid);
    		//If curr_table doesn't exist, this process has never been accessed
    		//so the page table needs to be created
    		if(curr_table == null){
    			curr_table = new PageTable(trace.pid);
    			page_tables.put(trace.pid, curr_table);
    		}
    		PTE pte = curr_table.getPTE(trace.v_address);
    		entry = new TLBEntry(pte.page_num, pte.frame_num);
    		tlb.addEntry(entry);
    		doLookup(trace);
    	}
    }
}
