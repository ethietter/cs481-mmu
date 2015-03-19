package mmu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;


public class Memory {

	//A list of frames which behaves just like in actual hardware - a linear array
	//of frames.
	private static ArrayList<Frame> frames = new ArrayList<Frame>();
	//For use with the LRU replacement policy
	public static LRUStruct lru_list = new LRUStruct();
	
	//The next_page index is used for all policies until memory is full,
	//and for FIFO at each allocateFrame() request
	private static int next_frame = 0;
	private static int num_frames;
	
	public static void init(){
		num_frames = (int) (Settings.physical_size/Settings.frame_size);
	}
	
	public static int allocateFrame(PTE pte_ref, int pid){
		int page_index = next_frame;
		if(next_frame < num_frames){
			frames.add(page_index, new Frame(pte_ref));
			pte_ref.present = true;
			//A page table access does NOT need to be recorded here, because technically (according to the specs)
			//the pte_ref.present bool is being set at the same time the translation is updated. It just happens
			//elsewhere (when the PTE is looked up initially, in PageTable.java).
			if(Settings.page_replacement == Settings.Policy.LRU){
				lru_list.addNode(next_frame);
			}
			next_frame++;
		}
		else{
			page_index = evictFrame();
			Frame frame = frames.get(page_index);
			frame.setPTE(pte_ref);
		}
		//All calls to allocateFrame indicate that a page fault happened
		LookupLogInfo.page_fault = true;
		return page_index;
	}
	
	public static Frame getFrame(int frame_num){
		return frames.get(frame_num);
	}
	
	public static void frameHit(int frame_num){
		if(Settings.page_replacement == Settings.Policy.LRU){
			lru_list.useNode(frame_num);
		}
	}
	
	public static void readFrame(int frame_num){
		frames.get(frame_num).read();
		frameHit(frame_num);
	}
	
	public static void writeFrame(int frame_num){
		frames.get(frame_num).write();
		frameHit(frame_num);
	}

	public static void print(){
		StringBuilder str = new StringBuilder();
		ListIterator<Frame> it = frames.listIterator();
		str.append("[");
        while(it.hasNext()){
        	str.append("\n\t" + it.next() + ",");
        }
        str.append("\n]");
        System.err.println(str.toString());
	}
	
	private static int evictFrame(){
		int evicted = 0;
		switch(Settings.page_replacement){
			case RANDOM:
				evicted = (int) Math.floor(Math.random() * frames.size());
				break;
			case FIFO:
				evicted = next_frame % num_frames;
				next_frame++;
				break;
			case LFU:
				evicted = getLFU();
				break;
			case LRU:
				evicted = lru_list.getLRUNode().val;
				break;
			case MFU:
				evicted = getMFU();
				break;
			default:
				break;
		}
		frames.get(evicted).evict();
		return evicted;
	}
	
	private static int getLFU(){
		//First try it out with just a linear search, and see if it runs fast enough
		long min_val = frames.get(0).getAccessCount(); 
		int min_frame_index = 0; 
		for(int i = 1; i < frames.size(); i++){
			Frame curr_frame = frames.get(i);
			if(curr_frame.getAccessCount() < min_val){
				min_frame_index = i;
				min_val = curr_frame.getAccessCount();
			}
		}
		return min_frame_index;
	}
	
	private static int getMFU(){
		//First try it out with just a linear search, and see if it runs fast enough
		long max_val = frames.get(0).getAccessCount(); 
		int max_frame_index = 0; 
		for(int i = 1; i < frames.size(); i++){
			Frame curr_frame = frames.get(i);
			if(curr_frame.getAccessCount() > max_val){
				max_frame_index = i;
				max_val = curr_frame.getAccessCount();
			}
		}
		return max_frame_index;
	}
	
	 
}
