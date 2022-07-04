/**
 *      Project 74 : Evolver
 *
 *      See history.txt for versioning information
 *
 *      General test-bed class for the GA Engine
 *
 *      (C)2002 Damian Murphy
 *
 */


import java.io.*;
import java.lang.*;
import java.util.*;


public class Evolver {


    // Global Constants
    public static final String VER = "0.7.1";

    // Set defaults
    static int POP_SIZE;
    static int NUM_GENES;
    static int INV_RATE;     // inversion rate% = INV_RATE/1000
    static int GENERATIONS;
    static int USE_DISPLAY = 1;  // 0 = no window, 1 = use window

    static double best[] = new double[2];
    static double worst[] = new double[2];
    static double[] best_so_far;

    static Display status;

    public static TSPReader TSPInput;

    static long startTime = System.currentTimeMillis();
    static long endTime;

    static GaPlots plotter;
    static String plotfile = "";

    // Class Methods

    private static int parseCommand(String[] args){

            // Retrieve options from the command line
            // and set variables accordingly...
            // Must have certain parameters upon startup, so
            // return -1 to main so we can exit with help given.

            if ( args.length == 0 ){
               return -1;
            }
            else {

              // Now, we need to parse the command args
              //for ( int i=0; i < args.length; i++ ){

              INV_RATE = Integer.parseInt(args[0]);
              POP_SIZE = Integer.parseInt(args[1]);
              NUM_GENES = Integer.parseInt(args[2]);
              GENERATIONS = Integer.parseInt(args[3]);
              plotfile = args[4];

              return 0;
            }
    }



    private static void helpme(){

            // Print out basic command-line help & version info

            System.out.print("\nEvolver, v" + VER + "\n");
            System.out.print("\nUsage:\n");
            System.out.print("\tevolver [options] PARAMETERS\n");
            System.out.print("\nWhere PARAMETERS are :\n");
            System.out.print("  INV_RATE        \t : the inversion rate (0..1000)\n");
            System.out.print("  POPSIZE         \t : population size\n");
            System.out.print("  NUM_GENES       \t : number of genes in each individual\n");
            System.out.print("  GENERATIONS     \t : number of generations to run for\n");
            System.out.print("  PLOTFILE        \t : output file name for plotting info\n\n");


    }

    private static long getEndTime(){

            endTime = System.currentTimeMillis();
            return endTime;

    }

    private static void printRunTime(){

            long runtime, hours, minutes, seconds;

             runtime = endTime - startTime;

             hours = Math.round( (((runtime / 1000)  / 60) / 60) );
             minutes = Math.round( ((runtime / 1000)  / 60) ) % 60 ;
             seconds = Math.round( runtime / 1000) % 60;

             System.out.print( hours + " hours, " + minutes + " minutes, and " + seconds + " seconds.");


    }






    /**
      *     Evolver main method - does all the work!
      *
      **/


    public static void main(String[] args) {

        int errorlevel = 0;
        int sample = 0;


        // Firstly, check & parse the command line
        if ( parseCommand(args) == -1 ){
           helpme();
           System.exit(255);           // Bye-bye!
        }


        TSPInput = new TSPReader();
        NUM_GENES = TSPInput.numCities();

        plotter = new GaPlots( plotfile );
        plotter.init();

        best_so_far = new double[ NUM_GENES + 2 ];

        Population family = new Population (POP_SIZE, NUM_GENES, INV_RATE);

        // Create & show Window
        if ( USE_DISPLAY == 1 ){
           status = new Display( POP_SIZE, NUM_GENES, INV_RATE, GENERATIONS, VER);
           status.show();
        }

        // Dump out time & other stuff.
        System.out.print("Started at UNIX time : " + startTime + "\n\n");
        System.out.print("Evolver v" + VER + "\n");
        System.out.print("Running on " + System.getProperty("os.name") + ", version " + System.getProperty("os.version") + "\nProcessor type : " + System.getProperty("os.arch") + "\n\n");


        for ( int years = 0; years < GENERATIONS; years++){

            System.out.print("Generation : " + family.getGeneration() + "\n\n");

            family.doFitness();

            best = family.getBest();
            worst = family.getWorst();
            best_so_far = family.getBestSoFar();

            System.out.print("Best Pupil : " + (int) best[0]
                              + ", Fitness = " + best[1] /*+ ", Birthday = " + family.getAge( (int) best[0] )*/
                              + "\nWorst Troublemaker : "
                              + (int) worst[0] + ", Fitness = " + worst[1] /*+ ", Birthday = " + family.getAge( (int) worst[0] )*/
                              + "\nAverage Fitness = "
                              + family.getAverage() + "\nBest Fitness so far = " + best_so_far[NUM_GENES] + ", in Generation " + (int) best_so_far[NUM_GENES + 1] + "\n\n");


            // Dump out the top five individuals
            System.out.print("+++ TOP 5 +++\n");
            family.printTopFive();
            System.out.print("=== TOP 5 ===\n\n");

            // Update the window display
            if ( USE_DISPLAY == 1 ){
               status.update( best, worst, family.getGeneration(), family.getAverage(), best_so_far[NUM_GENES], best_so_far[NUM_GENES + 1] );
            }

            // Save plotting info
            if ( sample == 0 ){  // every 10,000 generations, plot a value
               plotter.save( family.getGeneration(), best[1] );
            }


            // Dump out the initial population
            if ( years == 0 ){
            for (int n=0; n < POP_SIZE; n++ ){

                System.out.print("Individual No." + n + " -> ");

                family.printIndividual(n);
                family.printFitness(n);
                System.out.print("\n");

            }
            }

            // Dump out every 'sample' and 'sample+1' population
            /*if ( ( sample == 0 ) || ( sample == 1 ) ){
            for (int n=0; n < POP_SIZE; n++ ){

                System.out.print("Individual No." + n + " -> ");

                family.printIndividual(n);
                family.printFitness(n);
                System.out.print("\n");

            }
            }*/
            // Dump out the final generation
            if ( years == (GENERATIONS - 1) ){
            for (int n=0; n < POP_SIZE; n++ ){

                System.out.print("Individual No." + n + " -> ");

                family.printIndividual(n);
                family.printFitness(n);
                System.out.print("\n");

            }
            }


            family.doSelection();
            System.out.print("\n<->\n\n");
            sample++;
            if ( sample == 10000 ){
              sample = 0;
            }
        }

        // Finished the run.
        // Dump the best ever individual and quit!
        System.out.print("Finished!\n");
        System.out.print("Best Ever Individual was found in generation " + (int) best_so_far[NUM_GENES + 1] + ", with fitness of " + best_so_far[NUM_GENES] + "\n");
        System.out.print("Best ->");

        // Save the best over tour in the plotfile for plotting purposes
        plotter.saveBestInit();       // print headers

         for ( int n=0; n < NUM_GENES; n++ ){
               System.out.print(":" + (int) best_so_far[n] );
               plotter.saveBest( (int) best_so_far[n], TSPInput.getX( (int) best_so_far[n] ), TSPInput.getY( (int) best_so_far[n] ) );
         }

        System.out.print("\n\n");
        System.out.print(" In Numerical Order ->");
        Arrays.sort( best_so_far, 0, NUM_GENES );
        for ( int c=0; c<NUM_GENES; c++){
             System.out.print( (int) best_so_far[c] + ":");
        }


        System.out.print("\n\nRun completed at : " + getEndTime() + "\n");
        System.out.print("Run Time was ");
        printRunTime();
        System.out.print("\n\n");

        // Close plotting file
        plotter.finish();


        System.out.print("\n0 OK, 0:1\n");

        System.exit(0);       // Bye-bye!

    }


}
