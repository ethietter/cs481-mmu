package mmu;

public class Main{

    public static void main(String[] args){
    	
    	
		Settings.load();
		
		Simulator sim = new Simulator();
		sim.run();
    }
    

}
