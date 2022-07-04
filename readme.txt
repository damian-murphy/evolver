
Evolver, v0.7.1

Date : March 11th, 2003
Author : Damian Murphy
Web : http://minds.may.ie/~murf


============
Introduction
============

Here's some info on the Evolver code.


====================================
1. System Requirements
====================================

I'm unsure what the minimum spec for the code is. The code should compile and
run on any system supporting Sun's JDK 1.3.1 or better. The RAM and disk space
requirements vary with the options used during the code run - for example,
500,000 generations and a population size of 150 used 620Mb of drive space per run.

It's been tested on the following systems, and worked fine:

	Intel Pentium 233, 32MB Ram, Windows 95b
	Intel Pentium 233, 32MB Ram, Red Hat Linux 7
	Intel Pentium IV, 256Mb Ram, Windows NT 4.0 SP6
	Intel Pentium III, 128Mb Ram, Gentoo Linux
	Sun E450 with 4 Sun Sparc CPU's, 2Gb RAM, Sun Solaris 8
	AMD Athlon 850, 128Mb Ram, Windows 2000 SP3
	


====================================
2. Compilation
====================================

Compile all the java source files using 'javac *.java'

You should first choose which genetic operators you want to use for the run, see later for details.


====================================
3. Choosing Operators
====================================

Open the Population.java file with your favourite text editor.
Near the top of this file, you will see something like the following:

// Constants for different implementations
private static final int NO_XCROSS = 1;
private static final int NO_INV = 1;
private static final int NO_MUTP = 1;
private static final int NO_MUTS = 0;
private static final int NO_CLEANUP = 0;

Change these definitions to choose which operators you want to employ.
If they're set to 0 then they are activated, set them to 1 (or some other number) and they will be deactivated for the run. (yes, i know it's reverse logic, i was tired when i coded this!)

The only exception is NO_XCROSS. This is set as follows:

	1 = no crossover
	2 = classic single point crossover
	3 = new variation on single point crossover

Remember to re-compile the code when you've made your changes, or they won't work!


=============================================
4. Running the Code & Command-line parameters
=============================================

To run the code, the following command-line syntax is used:

java Evolver <MUT/INV> <POPSIZE> <CHROMO LENGTH> <GENERATIONS> <PLOTFILE> 

where

<MUT/INV> = Rate of appliction of mutation or inversion operators. Ranges from 0 to 1000.

<POPSIZE> = Size of the population. Any size you like.

<CHROMO LENGTH> = number of cities in the TSP. This parameter is not actually used any more.

<GENERATIONS> = Number of generations to run for.

<PLOTFILE> = Name of file to write the best individual from every 10,000th generation to. This file will be overwritten, so make sure it's got nothing important in it before you start!


Finally, the output is sent to the dos-box screen. Capture this using simple re-direction to a suitable file.

For example, 

java Evolver 2 150 52 500000 run10.plot.txt > run10.dump.txt

sets the mutation rate to 0.02%, pop size at 150, 52 cities, 500,000 generations, saving plot info in run10.plot.txt, and finally capturing the output into the file run10.dump.txt



=============================================
5. Tinkering with the code
=============================================

The code is very specific to the problem I was examining - the effects of operators on genetic algorithms. Poke around with the code, for example the frequency of plot recording (set at every 10,000 generations) is very easy to change - in Evolver.java there's a simple if statement that performs this operation.



=============================================
6. Finally........
=============================================

Have fun!