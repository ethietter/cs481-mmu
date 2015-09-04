test:
	mkdir bin
	javac -d bin src/mmu/*.java
	cp src/mmu_exec mmu
	chmod a+x mmu

clean:
	rm -rf bin
	rm -f mmu
