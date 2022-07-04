/**
 * Population Setup Class
 *
 * Note :
 * ID = Individual ID & slot position in pop array
 * FITNESS = fitness value of an individual
 */

import java.util.*;
import java.lang.*;

public class Population {

    // Constants for different implementations
    private static final int NO_XCROSS = 1;  // 1 = no crossover, 2 = new_crossover, 3 = old crossover  ( Note: destructive)
    private static final int NO_INV = 1;     // 1 = no inversion
    private static final int NO_MUTP = 1;     // 1 = no point mutation ( Note: destructive)
    private static final int NO_MUTS = 0;     // 1 = no swap mutation
    private static final int NO_CLEANUP = 0; // 1 = no cleanup ( no genePolice() )

    // Initial start city for the tour
    private static final int START_CITY = 0;


    // Public Items - change these to private??
    public static double[] best_individual;                  // ID:FITNESS
    public static double[] worst_individual;                 // ID:FITNESS
    public static double average_fitness;
    public static int generation_number;


    // Private - no peeking!
    private static int[][] pop;         // Population Holder - ID:GENE
    private static double[][] tour_score; // Fitness 'scores' for each inidividual in the population - ID:FITNESS
    private static int POP_SIZE;       // Holders
    private static int NUM_GENES;
    private static int INV_RATE;
    private static int MUT_RATE;
    private static int[][] next_generation;      // Contains mutated/crossed over individuals.
    private static int[] breeders;             // ID of indiviual to be bred
    private static double[] best_so_far;       // GENES..GENES:FITNESS:GENERATION
    private static int last_child;
    private static double[][] rank_order; // Rank ordering of population - [][0] = pop ID, [][1] = fitness value


    /**
      *     Sets up the initial population. Requires the following parameters: <br>
      *     <ul>
      *     <li><code>size</code> : Number of individuals in the population.
      *     <li><code>num_genes</code> : Number of genes in each chromosome. Equal to the number of cities in the TSP.
      *     <li><code>inv_rate</code> : Rate at which the inversion & mutation operators are applied to the population. Ranges from 0..1000.
      *     </ul>
      *     <br>
      *     This constructor also creates the main arrays for the population, and initialises various variables.
      *     It also ensures the initial population is valid.
      */
    public Population(int size, int num_genes, int inv_rate) {     // Constructor

           POP_SIZE = size;
           NUM_GENES = num_genes;
           INV_RATE = inv_rate;
           MUT_RATE = inv_rate;                    // inversion rate & mutation rate same.

           pop = new int[POP_SIZE][NUM_GENES + 1];                // + 1 box = age
           tour_score = new double[POP_SIZE][1];
           rank_order = new double[POP_SIZE][2];
           next_generation = new int[POP_SIZE][NUM_GENES + 1];
           breeders = new int[POP_SIZE/2];

           Random randomize = new Random();

           // Initialise the population with random genes

           for ( int n=0; n < POP_SIZE; n++ ){
              pop[n][0] = START_CITY;                         // Fix the start city
              for ( int m=1; m < NUM_GENES; m++){
                 pop[n][m] =  randomize.nextInt( NUM_GENES );  // nextInt goes from 0 to NUM_GENES - 1! ( see java.util.random )
                 // Note: num_genes = num_cities in the TSP problem being studied. No point having
                 // less genes than cities!
              }
              pop[n][NUM_GENES] = 0;       // Set initial age to 0
           }

           // Run genePolice() to ensure the initial population is valid
           genePolice();                        // Check for invalid DNA & Repair



           // Now, we'll set up the public variables
           best_individual = new double[2];
           worst_individual = new double[2];
           best_so_far = new double[ NUM_GENES + 2 ];

           // Initialise
           best_individual[0] = 0.0;
           best_individual[1] = 0.0;
           best_so_far[ NUM_GENES ] = Double.MAX_VALUE;
           best_so_far[ NUM_GENES + 1 ] = 0.0;
           worst_individual[0] = 0.0;
           worst_individual[1] = Double.MAX_VALUE;       // Set worst fitness to be MAX_VALUE, the
                                                         // largest double we can hold, so we have a reference.

           average_fitness = 0.0;
           generation_number = 1;



     }



     /**
       *    Calculates the fitness of each individual in the population.
       *    Fitness values are stored in the tour_score[][] array, in the format individual_ID : Fitness.
       *    Fitness is calculated as the round-trip distance of the tour starting and finishing at the first
       *    gene in the chromosome.
       */
     public void doFitness(){        // Calculates fitness for this population

            double sumfoo = 0.0;
            double distance = 0.0;
            double x1, x2, y1, y2;
            worst_individual[1] = 0.0;
            best_individual[1] = Double.MAX_VALUE;

            // Firstly, reset the tour_score array, or we'll just end up with
            // increasing fitness (but not from evolution! :)

            for ( int n=0; n < POP_SIZE; n++ ){
                tour_score[n][0] = 0.0;
            }

            for ( int n=0; n < POP_SIZE; n++ ){




                for ( int i=0; i < ( NUM_GENES - 1 ); i++ ){

                    // Get the co-ords of each city, then get the distance
                    // to the next city, summing all the way...
                    // Distance bet. 2 points = sqrt( (x2 - x1)^2 + (y2 -y1)^2 )

                    x1 = Evolver.TSPInput.getX( pop[n][i] );
                    y1 = Evolver.TSPInput.getY( pop[n][i] );
                    x2 = Evolver.TSPInput.getX( pop[n][i + 1] );
                    y2 = Evolver.TSPInput.getY( pop[n][i + 1] );

                    distance = Math.sqrt( (Math.pow( (x2 - x1), 2.0 )) + (Math.pow( (y2 - y1), 2.0)) );
                    tour_score[n][0] = tour_score[n][0] + distance;




                }

                // Now add on the distance back to the initial city, so we do a round-trip
                x1 = Evolver.TSPInput.getX( pop[n][NUM_GENES -1] );
                y1 = Evolver.TSPInput.getY( pop[n][NUM_GENES -1] );   // x1,y1 = last city
                x2 = Evolver.TSPInput.getX( pop[n][0] );
                y2 = Evolver.TSPInput.getY( pop[n][0] );              // x2,y2 = first city

                distance = Math.sqrt( (Math.pow( (x2 - x1), 2.0 )) + (Math.pow( (y2 - y1), 2.0)) );
                tour_score[n][0] = tour_score[n][0] + distance;



                // Save the best & worst individuals
                if ( best_individual[1] > tour_score[n][0] ){
                   best_individual[0] = n;
                   best_individual[1] = tour_score[n][0];
                   if ( best_individual[1] < best_so_far[ NUM_GENES ] ){
                      best_so_far[ NUM_GENES ] = best_individual[1];
                      best_so_far[ NUM_GENES + 1 ] = generation_number;
                      for ( int c = 0; c < NUM_GENES; c++ ){
                          best_so_far[c] = pop[n][c];
                      }
                   }
                }

                if ( worst_individual[1] < tour_score[n][0] ){
                   worst_individual[0] = n;
                   worst_individual[1] = tour_score[n][0];
                }
            }

            // Get the average fitness of the whole population
            for ( int n=0; n < POP_SIZE; n++){
                sumfoo = sumfoo + tour_score[n][0];
            }
            average_fitness = (sumfoo / POP_SIZE) ;

            // Rank the population and save the top five.
            // It'll be easy to rank the tour_score array so we can perform true rank order selection if needed.

            for ( int n=0; n < POP_SIZE; n++){
                rank_order[n][0] = n;
                rank_order[n][1] = tour_score[n][0];      // Copy scores
            }
            sortRank();       //  Sort it! Numerical order, so it'll be smallest first.


     }


     /**
       *    Returns the fitness value of the individual specified by <code>serial_num</code>.
       */
     public double getFitness( int serial_num ){


            return tour_score[serial_num][0];
     }


     /**
       *    Prints the fitness value of the individual specified by <code>serial_num</code> to stdout.
       */
     public void printFitness( int serial_num ){

            System.out.print("> Fitness = " + tour_score[serial_num][0]);
     }


     /**
       *    Prints the individual specified by <code>serial_num</code> to stdout.
       */
     public void printIndividual( int serial_num ){

            for ( int n=0; n < NUM_GENES; n++ ){
               System.out.print(":" + pop[serial_num][n] );
            }
     }

     /**
       *    Returns the average fitness value of the current population.
       */
     public double getAverage(){

            return average_fitness;
     }

     /**
       *    Returns the fitness value of the best-fit individual in the current population.
       */
     public double[] getBest(){

            return best_individual;
     }

     /**
       *    Returns the fitness value of the best-fit individual found so far.
       */
     public double[] getBestSoFar(){
            return best_so_far;
     }

     /**
       *    Returns the fitness value of the least-fit individual in the current population.
       */
     public double[] getWorst(){

            return worst_individual;
     }

     /**
       *    Increments the generation number.
       */
     private void incGeneration(){

            generation_number++;
     }

     /**
       *    Returns the current generation number of the population.
       */
     public int getGeneration(){

            return generation_number;
     }
     /**
       *    Prints out the top five ranking chromosomes.
       */

     public void printTopFive(){

            for( int r=0; r < 5; r++){
                 System.out.print("(" + (r+1) + ") Individual No." + (int) rank_order[r][0] + " ->");
                 printIndividual( (int) rank_order[r][0] );
                 System.out.print("> Fitness = " + rank_order[r][1] + "\n");
            }
     }

     /**
       *    Performs a bubble sort of the population based on fitness.
       *    The population itself isn't sorted, just the ID's in the rank_order array.
       */

     private void sortRank(){

             double swapfit;
             double swapID;

             // Sort the 2-d array rank_order into ascending nuumerical order based on fitness.
             boolean change_made = false;

             do{

                change_made = false;

                for( int n=0; n < (POP_SIZE-1); n++){
                     if ( rank_order[n][1] > rank_order[n+1][1] ){

                        swapfit = rank_order[n][1];
                        swapID = rank_order[n][0];
                        rank_order[n][1] = rank_order[n+1][1];
                        rank_order[n][0] = rank_order[n+1][0];
                        rank_order[n+1][1] = swapfit;
                        rank_order[n+1][0] = swapID;

                        change_made = true;
                     }
                 }

              }while( change_made == true );


     }

     /*
     public int getBirthGen( int serial_num ){

            return pop[serial_num][NUM_GENES];
     }

     public int getAge( int serial_num ){

            return ( generation_number - pop[serial_num][NUM_GENES] );
     }
     */

     /**
       *    Truncated Selection.
       *    Selects 50% of the population to become parents for the next generation. Selection is performed by
       *    comparing individual 1 with individual 2, 3 with 4, etc. The individual with better fitness is selected.
       *    This method ensures the best individual is always selected, however the second best may not be.
       *    Some number of 'less fit' individuals are also selected.
       *
       */
     public void doSelection(){

            // Perform Tournament Selection to get the best 50%
            // Place these into the breeders array, then apply crossover
            // and mutation/inversion.
            // Deathmatch!

            int selected = 0;
            int slot = 0;

            while ( selected < POP_SIZE ){             // remember -> array index = POP_SIZE-1

                  if ( tour_score[selected][0] <= tour_score[selected + 1][0] ){
                      breeders[slot] = selected;
                  }
                  else{
                      breeders[slot] = selected + 1;
                  }

                  selected = selected + 2;
                  slot++;
            }

            switch ( NO_XCROSS ){         // decide which xover to use, if any at all.

                   case 1:
                        no_crossover();
                        break;

                   case 2:
                        crossover();
                        break;

                   case 3:
                        old_crossover();
                        break;

            }

            if ( NO_INV == 0 ){
                  inversion();
            }

            if ( NO_MUTS == 0 ){
                  swap_mutation();
            }

            if ( NO_MUTP == 0 ){
                  point_mutation();
            }
            popFerry();

            if ( NO_CLEANUP == 0 ){
                 genePolice();                        // Check for invalid DNA & Repair
            }

            incGeneration();

     }



     /**
       *    Classic Single point crossover operation.<p>
       *    Two parents used to create two children. A randomly chosen point is picked, and the two
       *    chromosomes are then crossed to produce children in the following way :<br>
       *    Parent 1 -> genestr1:genestr2, Parent 2 -> genestr3:genestr4<br>
       *    become<br>
       *    Child 1 -> genestr1:genestr4, Child 2 -> genestr3:genestr2
       *    Notes : New version
       */
     private void crossover(){

             // Crossover
             // genestr1:genestr2 \ / genestr1:genestr3
             //                    X
             // genestr3:genestr4 / \ genestr2:genestr4

            int selected = 0;
            int breeder_index = 0;
            int xcross;
            int gene_string1[];
            int gene_string2[];
            int gene_string3[];
            int gene_string4[];
            Random randomize = new Random();


            xcross = randomize.nextInt( NUM_GENES );    // Random crossover point

            // Ensure xcross != 0, as the start city can't be moved.
            if ( xcross == 0 ){
              xcross = 1;
            }


            gene_string1 = new int[xcross];
            gene_string2 = new int[(NUM_GENES - xcross)];
            gene_string3 = new int[xcross];
            gene_string4 = new int[(NUM_GENES - xcross)];


            // Perform crossover
            //while ( selected != (POP_SIZE/2) - 1 ){             // remember -> array index = POP_SIZE-1
            while ( selected < (POP_SIZE/2) - 1 ){

            //System.err.print( selected + ":" + POP_SIZE + ":" + NUM_GENES + "\n");

                  for ( int n=0; n < xcross; n++){
                      gene_string1[n] = pop[ breeders[selected] ][n];
                  }
                  for ( int n=xcross; n < NUM_GENES; n++){
                      gene_string2[n-(xcross)] = pop[ breeders[selected] ][n];
                  }
                  for ( int n=0; n < xcross; n++){
                      gene_string3[n] = pop[ breeders[selected+1] ][n];
                  }
                  for ( int n=xcross; n < NUM_GENES; n++){
                      gene_string4[(n - (xcross))] = pop[ breeders[selected+1] ][n];
                  }

                  // Now, do the switch
                  for ( int n=0; n < gene_string1.length; n++){
                      next_generation[selected][n] = gene_string1[n];
                  }
                  for ( int n=xcross; n < NUM_GENES; n++){
                      next_generation[selected][n] = gene_string4[n - xcross];
                  }
                  for ( int n=0; n < gene_string3.length; n++){
                      next_generation[selected+1][n] = gene_string3[n];
                  }
                  for ( int n = gene_string3.length; n < NUM_GENES; n++){
                      next_generation[selected+1][n] = gene_string2[n - gene_string3.length];
                  }

                  next_generation[selected][NUM_GENES] = generation_number;      // reset age of children to current generation
                  next_generation[selected + 1][NUM_GENES] = generation_number;  // reset age of children to current generation

                  selected = selected + 2;

            }
            // Pass last one in untouched : temporary kludge
            for ( int n=0; n < NUM_GENES; n++){
                  next_generation[selected][n] = pop[ breeders[selected] ][n];
            }

            // Store the last child's position for inversion purposes

            last_child = selected;


            selected = selected + 1;   // Move the pop array slot past the last child's position

            // Now, throw in the parents directly.
            // A mad 'elite' strategy
            while ( selected < POP_SIZE ){
                  for ( int n=0; n < NUM_GENES; n++){
                       next_generation[selected][n] = pop[ breeders[breeder_index] ][n];

                  }
                  //next_generation[selected][NUM_GENES] = pop[ breeders[breeder_index] ][NUM_GENES];  // copy age
                  selected = selected + 1;
                  breeder_index = breeder_index + 1;
            }

     }




     /**
       *    Modified Single point crossover operation.<p>
       *    Two parents used to create two children. A randomly chosen point is picked, and the two
       *    chromosomes are then crossed to produce children in the following way :<br>
       *    Parent 1 -> genestr1:genestr2, Parent 2 -> genestr3:genestr4<br>
       *    become<br>
       *    Child 1 -> genestr1:genestr3, Child 2 -> genestr2:genestr4
       *    Notes : This form of crossover relies on there being no phenotype, only a genotype
       *    representation. In other words, it mixes the chromosomes in such a way as to destroy any phenotype.
       *    However, it does not matter with a genotype representation, where the gene positions have no 'special'
       *    meaning.
       */
     private void old_crossover(){

             // Crossover
             // genestr1:genestr2 \ / genestr1:genestr3
             //                    X
             // genestr3:genestr4 / \ genestr2:genestr4

            int selected = 0;
            int breeder_index = 0;
            int xcross;
            int gene_string1[];
            int gene_string2[];
            int gene_string3[];
            int gene_string4[];
            Random randomize = new Random();


            xcross = randomize.nextInt( NUM_GENES );    // Random crossover point

            // Ensure xcross != 0, as the start city can't be moved.
            if ( xcross == 0 ){
              xcross = 1;
            }

            gene_string1 = new int[xcross];
            gene_string2 = new int[(NUM_GENES - xcross)];
            gene_string3 = new int[(NUM_GENES - xcross)];
            gene_string4 = new int[xcross];


            // Perform crossover
            //while ( selected != (POP_SIZE/2) - 1 ){             // remember -> array index = POP_SIZE-1
            while ( selected < (POP_SIZE/2) - 1 ){

            //System.err.print( selected + ":" + POP_SIZE + ":" + NUM_GENES + "\n");

                  for ( int n=1; n < xcross; n++){
                      gene_string1[n] = pop[ breeders[selected] ][n];
                  }
                  for ( int n=xcross; n < NUM_GENES; n++){
                      gene_string2[n-(xcross)] = pop[ breeders[selected] ][n];
                  }
                  for ( int n=1; n < (NUM_GENES - xcross); n++){
                      gene_string3[n] = pop[ breeders[selected+1] ][n];
                  }
                  for ( int n=(NUM_GENES - xcross); n < NUM_GENES; n++){
                      gene_string4[(n - (NUM_GENES - xcross))] = pop[ breeders[selected+1] ][n];
                  }

                  // next_generation[][] is empty, so set the start city accordingly...
                  next_generation[selected][0] = START_CITY;
                  next_generation[selected+1][0] = START_CITY;

                  // Now, do the switch
                  for ( int n=1; n < gene_string1.length; n++){
                      next_generation[selected][n] = gene_string1[n];
                  }
                  for ( int n=xcross; n < NUM_GENES; n++){
                      next_generation[selected][n] = gene_string3[n - xcross];
                  }
                  for ( int n=1; n < gene_string2.length; n++){
                      next_generation[selected+1][n] = gene_string2[n];
                  }
                  for ( int n = gene_string2.length; n < NUM_GENES; n++){
                      next_generation[selected+1][n] = gene_string4[n - gene_string2.length];
                  }

                  next_generation[selected][NUM_GENES] = generation_number;      // reset age of children to current generation
                  next_generation[selected + 1][NUM_GENES] = generation_number;  // reset age of children to current generation

                  selected = selected + 2;

            }
            // Pass last one in untouched : temporary kludge
            for ( int n=0; n < NUM_GENES; n++){
                  next_generation[selected][n] = pop[ breeders[selected] ][n];
            }

            // Store the last child's position for inversion purposes

            last_child = selected;


            selected = selected + 1;   // Move the pop array slot past the last child's position

            // Now, throw in the parents directly.
            // A mad 'elite' strategy
            while ( selected < POP_SIZE ){
                  for ( int n=0; n < NUM_GENES; n++){
                       next_generation[selected][n] = pop[ breeders[breeder_index] ][n];

                  }
                  //next_generation[selected][NUM_GENES] = pop[ breeders[breeder_index] ][NUM_GENES];  // copy age
                  selected = selected + 1;
                  breeder_index = breeder_index + 1;
            }

     }


     /**
       *    Performs inversion on % of population. Inversion simply reverses a random portion of
       *    a tour. For example, the tour 1:2:3:4:5:6 could become 1:2:5:4:3:6.
       *    This method will not produce invalid tours.
       *
       */
     private void inversion(){

            int chance = 0;
            int pos1, pos2;     // Array positions for inversion
            int p1, p2;         // Temp. variables
            int swap;

            Random randomize = new Random();

            for ( int n=0; n <= last_child; n++ ){

                     pos1 = randomize.nextInt( NUM_GENES );

                     // Ensure pos1 isn't = 0, as this city is fixed as the start
                     if ( pos1 == 0 ){
                       pos1 = 1;
                     }

                     pos2 = ( randomize.nextInt( NUM_GENES - pos1 ) ) + pos1;  // pos2 must be bigger than pos1
                                                                               // and still smaller than NUM_GENES
                     chance = randomize.nextInt( 1000 );


                     if ( chance <= INV_RATE ){

                        if ( pos1 != pos2 ){        // if pos1 = pos2, nothing happens.

                           // Perform Inversion
                           while ( pos1 < pos2 ){
                                 swap = next_generation[n][pos2];                     //System.err.print( swap + ":pos2:" + next_generation[n][pos2] + ":pos1:" + next_generation[n][pos1] + ":p1:" + pos1 + ":p2:" + pos2 + "\n");
                                 next_generation[n][pos2] = next_generation[n][pos1]; //System.err.print( swap + ":pos2:" + next_generation[n][pos2] + ":pos1:" + next_generation[n][pos1] + ":p1:" + pos1 + ":p2:" + pos2 + "\n");
                                 next_generation[n][pos1] = swap;                     //System.err.print( swap + ":pos2:" + next_generation[n][pos2] + ":pos1:" + next_generation[n][pos1] + ":p1:" + pos1 + ":p2:" + pos2 + "\n");
                                 pos1++;
                                 pos2--;
                           }
                        }


                     }
            }


     }

     /**
       *    Performs point mutation on % of population. Point Mutation replaces a single random city (gene)
       *    with a randomly selected city. For example, the tour 1:2:3:4:5:6 could become 1:2:3:1:5:6,
       *    (4 replaced with 1) given that there are 6 cities.
       *    This method can produce invalid tours.
       *
       */
     private void point_mutation(){

            int chance = 0;
            int pos;            // Array positions for mutation
            int new_city;

            Random randomize = new Random();
            Random selection = new Random();

            for ( int n=0; n <= last_child; n++ ){

                     pos = randomize.nextInt( NUM_GENES );                     // select a random point

                     // Ensure pos != 0, as this is the fixed start city
                     if ( pos == 0 ){
                       pos = 1;
                     }

                     chance = randomize.nextInt( 1000 );                       // Chance of this chromosome being mutated
                     new_city = selection.nextInt( NUM_GENES );               // pick a random city from all cities.

                     if ( chance <= MUT_RATE ){

                        // Perform mutation
                        next_generation[n][pos] = new_city;                     //System.err.print( swap + ":pos2:" + next_generation[n][pos2] + ":pos1:" + next_generation[n][pos1] + ":p1:" + pos1 + ":p2:" + pos2 + "\n");

                     }



            }


     }

     /**
       *    Performs swap mutation on % of population. Swap Mutation switches the positions of two
       *    randomly chosen cities (genes). For example, the tour 1:2:3:4:5:6 could become 1:5:3:4:2:6
       *    (2 switched with 5).
       *    This method will not produce invalid tours.
       *
       */
     private void swap_mutation(){

            int chance = 0;
            int pos1, pos2;     // Array positions for mutation
            int p1, p2;         // Temp. variables
            int swap;

            Random randomize = new Random();

            for ( int n=0; n <= last_child; n++ ){

                     pos1 = randomize.nextInt( NUM_GENES );

                     // Ensure pos != 0, as this is the fixed start city
                     if ( pos1 == 0 ){
                       pos1 = 1;
                     }

                     pos2 = ( randomize.nextInt( NUM_GENES - pos1 ) ) + pos1;  // pos2 must be bigger than pos1
                                                                               // and still smaller than NUM_GENES
                     chance = randomize.nextInt( 1000 );


                     if ( chance <= MUT_RATE ){

                        if ( pos1 != pos2 ){        // if pos1 = pos2, nothing happens.

                           // Perform Swap
                           swap = next_generation[n][pos2];                     //System.err.print( swap + ":pos2:" + next_generation[n][pos2] + ":pos1:" + next_generation[n][pos1] + ":p1:" + pos1 + ":p2:" + pos2 + "\n");
                           next_generation[n][pos2] = next_generation[n][pos1]; //System.err.print( swap + ":pos2:" + next_generation[n][pos2] + ":pos1:" + next_generation[n][pos1] + ":p1:" + pos1 + ":p2:" + pos2 + "\n");
                           next_generation[n][pos1] = swap;                     //System.err.print( swap + ":pos2:" + next_generation[n][pos2] + ":pos1:" + next_generation[n][pos1] + ":p1:" + pos1 + ":p2:" + pos2 + "\n");

                        }


                     }
            }


     }


     /**
       *    Copies the individuals in the next_generation[][] array into the pop[][] array.
       *
       */
     private void popFerry(){

             for ( int n = 0; n < POP_SIZE; n++ ){
                 for ( int i = 0; i < NUM_GENES; i++ ){
                     pop[n][i] = next_generation[n][i];
                 }
             //pop[n][NUM_GENES] = next_generation[n][NUM_GENES];                          // Pass over the age
             }
     }

     /**
       *     Performs CleanUp on the population to ensure all chromosomes are valid tours.
       *     This is done by scanning through each chromosome, looking for duplicated cities.
       *     If we find a duplicate, we replace it with a randomly selected city. If this city
       *     has already been used, we simply select another random city until we get one which
       *     has not been used yet. This operator works on the pop[][] array.<br>
       *     Example : <br>
       *     Invalid Chromosome -> 0:1:6:5:3:4:5:6:8:9 ( 5,6 doubled, missing city 2,7) becomes<br>
       *     Valid Chromosome -> 0:1:6:5:3:4:7:2:8:9   ( 5,6 replaced with random missing cities )
       *     It is hoped the random-ness of this operation will prevent the CleanUp operator from
       *     moving the population into a sub-set of the total valid chromosomes.
       **/
     private void genePolice(){

             // Fix invalid tours in the gene pool
             // :)


             int[] done = new int[ NUM_GENES ];    // 1 = city present, 0 = not present
             Random randomize = new Random();
             int m = 0;
             int test =0;
             boolean changed = false;

             for ( int n=0; n < POP_SIZE; n++ ){

                for ( int filler = 0; filler < NUM_GENES; filler++){   // Reset done array
                 done[filler] = 0;
                }

                changed = false;                                       // Reset changed condition for each individual

                for ( int i=0; i < NUM_GENES; i++ ){                // Loop through genes

                   changed = false;                                 // Each time we check a gene, reset the changed var
                   switch ( done[ pop[n][i] ]){

                      case 0:

                        done[ pop[n][i] ] = 1;                     // if we dont have this town, record it.
                        break;                                           // if we do, replace with next unrecorded town.

                      case 1:
                        do {


                          m = randomize.nextInt( NUM_GENES );

                          if ( done[m] == 0 ){

                               pop[n][i] = m;
                               done[m] = 1;
                               changed = true;

                          }
                        } while ( ( changed == false ) );
                        // end do-while
                        break;

                   }// end - switch

                }// end - gene - for



             }// End - pop - for


     }

     /**
       *     Fills the next_generation[][] array with the children & parents.
       *     Used instead of the crossover() functions, which usually perform this role in addition to crossover.
       *
       */
     private void no_crossover(){

             // The way crossover works here leaves the next_generation array empty unless we
             // use this function!

             int selected = 0;
             int breeder_index = 0;

             while ( selected < (POP_SIZE/2) ){

                   for ( int n=0; n < NUM_GENES; n++){
                        next_generation[selected][n] = pop[breeders[selected]][n];
                   }
                   //next_generation[selected][NUM_GENES] = generation_number;  // reset age of children to current generation

                   selected++;
            }

            // Store the last child's position for mutation purposes

            last_child = selected;

            //selected = selected + 1;   // Move the pop array slot past the last child's position

            // Now, throw in the parents directly.
            // A mad 'elite' strategy
            while ( selected < POP_SIZE ){
                  for ( int n=0; n < NUM_GENES; n++){
                       next_generation[selected][n] = pop[ breeders[breeder_index] ][n];
                  }
                  //next_generation[selected][NUM_GENES] = pop[ breeders[breeder_index] ][NUM_GENES];  // copy age
                  selected = selected + 1;
                  breeder_index = breeder_index + 1;
            }


     }

}
