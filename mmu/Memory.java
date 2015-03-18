package mmu;

import java.util.ArrayList;
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
	
	//All calls to allocateFrame indicate that a page fault happened
	//All calls also indicate that a disk access happened
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
        System.out.println(str.toString());
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
				break;
			case LRU:
				evicted = lru_list.getLRUNode().val;
				break;
			case MFU:
				break;
			default:
				break;
		}
		frames.get(evicted).evict();
		return evicted;
	}
	
	 
}
