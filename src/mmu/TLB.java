package mmu;

import java.util.LinkedList;
import java.util.ListIterator;

public class TLB {

    public static long max_size;
    public static int latency; //in nanoseconds
    
    private static LinkedList<TLBEntry> cache = new LinkedList<TLBEntry>();
    
    //Don't instantiate this class
    private TLB(){}
    
    public static void init(){
        max_size = Settings.tlb_size;
    }

    /*
	Returns the relevant TLBEntry on a hit and null on
	a miss.
    */
    public static TLBEntry lookup(int vpn){
    	LookupLogInfo.addTLBAccess();
        ListIterator<TLBEntry> it = cache.listIterator();
        TLBEntry item;
        while(it.hasNext()){
            item = it.next();
            if(item.virtual_page == vpn){
            	//Move the element to the front of the linked list representing the cache
            	it.remove();
            	cache.addFirst(item);
                return item;
            }
        }
        return null;
    }

    public static void addEntry(TLBEntry entry){
        cache.add(0, entry);
        if(cache.size() > max_size){
            //The last element in the list is the LRU
        	LookupLogInfo.TLBEviction.evicted = true;
        	LookupLogInfo.TLBEviction.v_page = cache.get((int) max_size).virtual_page;
            cache.remove((int) max_size);
        }
		LookupLogInfo.addTLBAccess();
    }
    
    public static void removeEntry(TLBEntry entry){
    	cache.remove(entry);
		LookupLogInfo.addTLBAccess();
    }
    
    public static void flush(){
    	cache.clear();
		LookupLogInfo.addTLBAccess();
    }
    
    public static String getString(){
    	return cache.toString();
    }
}
