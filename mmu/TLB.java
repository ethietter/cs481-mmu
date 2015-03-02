package mmu;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class TLB {

    int max_size;
    int latency; //in nanoseconds
    
    private LinkedList<TLBEntry> cache = new LinkedList<TLBEntry>();
    
    public TLB(){
        this.max_size = Settings.tlb_size;
    }

    /*
	Returns the relevant TLBEntry on a hit and null on
	a miss.
    */
    public TLBEntry lookup(int vpn){
        ListIterator<TLBEntry> it = cache.listIterator();
        TLBEntry item;
        while(it.hasNext()){
            item = it.next();
            if(item.virtual_page == vpn){
            	it.remove();
            	cache.addFirst(item);
                return item;
            }
        }
        return null;
    }

    public void addEntry(TLBEntry entry){
        cache.add(0, entry);
        if(cache.size() > max_size){
            //The last element in the list is the LRU, because of how elements
            //are accessed
            cache.remove(max_size - 1);
        }
    }
    
    public void flush(){
    	cache.clear();
    }
    
    public String toString(){
    	return cache.toString();
    }
}
