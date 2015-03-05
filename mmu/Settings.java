package mmu;

public final class Settings {

    public enum Policy{
        RANDOM, FIFO, LRU, LFU, MFU
    }

    public static final int address_size = 32; //32 bit virtual addresses
    public static long physical_size;
    public static long frame_size;
    public static int frame_bits; //How many bits are needed to choose a frame = lg(frame_size)
    public static int memory_latency; //in nanoseconds
    public static Policy page_replacement;
    public static long tlb_size;
    public static int tlb_latency; //in nanoseconds
    public static int disk_latency; //in milliseconds
    public static boolean log_output;

    //Don't instantiate this class
    private Settings(){ }

    public static void load(){
        //Load the config settings here
        
    	/*
    	physical_size = 1073741824;
        frame_size = 1024;
        memory_latency = 100;
        page_replacement = Policy.LRU;
        tlb_size = 128;
        tlb_latency = 20;
        disk_latency = 10;
        log_output = true;
        */

    	physical_size = 4096;
        frame_size = 1024;
        frame_bits = (int) (Math.log(frame_size)/Math.log(2));
        memory_latency = 100;
        page_replacement = Policy.RANDOM;
        tlb_size = 2;
        tlb_latency = 20;
        disk_latency = 10;
        log_output = true;
    }
    

}
