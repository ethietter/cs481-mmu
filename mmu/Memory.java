package mmu;

import java.util.LinkedList;
import java.util.ListIterator;

public class Memory {

	private static LinkedList<Frame> pages = new LinkedList<Frame>();
	private static int next_page = 0;
	private static int num_frames;
	
	public static void init(){
		num_frames = Settings.physical_size/Settings.frame_size;
	}
	
	public static int allocateFrame(PTE pte_ref){
		int page_index = next_page;
		if(next_page < num_frames){
			next_page++;
			pages.add(page_index, new Frame(pte_ref));
		}
		else{
			page_index = evictFrame();
			pages.get(page_index).setPTE(pte_ref);
		}
		return page_index;
	}
	
	private static int evictFrame(){
		int evicted = 0;
		if(Settings.page_replacement.equals(Settings.Policy.RANDOM)){
			evicted = (int) Math.floor(Math.random() * pages.size());
			pages.get(evicted).evict();
		}
		return evicted;
	}
	
	public static void print(){
		StringBuilder str = new StringBuilder();
		ListIterator<Frame> it = pages.listIterator();
		str.append("[");
        while(it.hasNext()){
        	str.append("\n\t" + it.next());
        }
        str.append("\n]");
        System.out.println(str.toString());
	}
	 
}
