package mmu;

import java.util.LinkedList;
import java.util.ListIterator;

public class Memory {

	private static LinkedList<Frame> frames = new LinkedList<Frame>();
	private static int next_page = 0;
	private static int num_frames;
	
	public static void init(){
		num_frames = (int) (Settings.physical_size/Settings.frame_size);
	}
	
	public static int allocateFrame(PTE pte_ref){
		int page_index = next_page;
		if(next_page < num_frames){
			next_page++;
			frames.add(page_index, new Frame(pte_ref));
		}
		else{
			page_index = evictFrame();
			Frame frame = frames.get(page_index);
			frame.setPTE(pte_ref);
		}
		return page_index;
	}
	
	public static Frame getFrame(int frame_num){
		return frames.get(frame_num);
	}

	public static void print(){
		StringBuilder str = new StringBuilder();
		ListIterator<Frame> it = frames.listIterator();
		str.append("[");
        while(it.hasNext()){
        	str.append("\n\t" + it.next() + ",");
        }
        str.append("\n]");
        System.out.println(str.toString());
	}
	
	private static int evictFrame(){
		int evicted = 0;
		if(Settings.page_replacement.equals(Settings.Policy.RANDOM)){
			evicted = (int) Math.floor(Math.random() * frames.size());
			frames.get(evicted).evict();
		}
		return evicted;
	}
	
	 
}
