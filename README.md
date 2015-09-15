# Memory Management Unit Simulator

I wrote this java program for my Operating Systems course (UNM - CS 481). It simulates the behavior of a Memory Management Unit operating under different configurations.

## Usage

The program is pretty easy to compile and run on any machine with a Java 1.7 compiler.

If you are running on a *nix platform, running the Makefile will produce a .sh script that can be used to run the java program as follows:

```bash
./mmu <config file> <trace file>
```

Alternatively, on Windows you can compile with:

```
mkdir bin
javac -d bin src\mmu\*.java
```

And run with:
```
java -cp bin mmu.Main <config file> <trace file>
```

## Configuration

The config file is a text file with the following format:

```
physical-memory-size: 4096
frame-size: 1024
memory-latency: 100
page-replacement: RANDOM
tlb-size: 2
tlb-latency: 20
disk-latency: 10
logging-output: off
```

physical-memory-size: amount of physical memory the simulated machine has, in bytes  
frame-size: size of physical frames and virtual pages, in bytes  
memory-latency: time, in nanoseconds, to read/write to physical memory  
page-replacement: policy the MMU uses to evict frames from physical memory. Available options:  
* LRU (Least recently used)  
* LFU (Least frequently used)  
* MFU (Most frequently used)  
* RANDOM  
* FIFO (First-in-first-out)  

tlb-size: number of entries stored in the translation lookaside buffer. TLB evictions are made based on a LFU policy  
tlb-latency: time, in nanoseconds, to read an entry from the TLB  
disk-latency: time, in nanoseconds, to read/write to disk  
logging-output: "off" or "on". Provides additional details for each entry in the trace file (not recommended for large trace files)  

The trace file is a text file with the following format. This file can contain an arbitrary number of memory references. Each reference goes on its own line.
Available operations are I, R, and W. I is an instruction fetch, R is a read, and W is a write.

```
<pid> <operation> <virtual address>
```

See the /res folder for a sample config file and various trace files.
