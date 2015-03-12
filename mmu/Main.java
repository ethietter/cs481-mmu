package mmu;


public class Main{

    public static void main(String[] args){
    	
    	
		Settings.load();
		Memory.init();
    	TLB.init();
    	
    	Settings.print();
		
		Simulator sim = new Simulator();
		sim.run();
    }
    

}
