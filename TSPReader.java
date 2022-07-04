/**
  *     TSPReader : Reads in TSP problems from files
  *
  *
  **/


  // NOTE: This class is temporarily hard coded to fill the array lookuptable with
  // values given below.



import java.io.*;
import java.lang.*;


public class TSPReader {


       // TEMP STATIC ARRAY OF CITIES
       private double[] holder = { 565.0, 575.0, 25.0, 185.0, 345.0, 750.0, 945.0, 685.0, 845.0, 655.0, 880.0, 660.0, 25.0, 230.0, 525.0, 1000.0, 580.0, 1175.0, 650.0, 1130.0, 1605.0, 620.0,  1220.0, 580.0, 1465.0, 200.0, 1530.0, 5.0, 845.0, 680.0, 725.0, 370.0, 145.0, 665.0, 415.0, 635.0, 510.0, 875.0, 560.0, 365.0, 300.0, 465.0, 520.0, 585.0, 480.0, 415.0, 835.0, 625.0, 975.0, 580.0, 1215.0, 245.0, 1320.0, 315.0, 1250.0, 400.0, 660.0, 180.0, 410.0, 250.0, 420.0, 555.0, 575.0, 665.0, 1150.0, 1160.0, 700.0, 580.0, 685.0, 595.0, 685.0, 610.0, 770.0, 610.0, 795.0, 645.0, 720.0, 635.0, 760.0, 650.0,  475.0, 960.0,  95.0, 260.0,  875.0, 920.0, 700.0, 500.0,  555.0, 815.0,  830.0, 485.0, 1170.0, 65.0, 830.0, 610.0, 605.0, 625.0,  595.0, 360.0, 1340.0, 725.0, 1740.0, 245.0 };

       // Globals for this class

       private int NUM_CITIES;

       private double[][] lookUpTable;


       public TSPReader() {

       int x = 0;
       int y = 1;

       NUM_CITIES = 52;

       // Fill up the lookUpTable
       lookUpTable = new double[ NUM_CITIES ][2];

           for ( int n=0; n < NUM_CITIES; n++ ){

               lookUpTable[n][0] = holder[x];
               lookUpTable[n][1] = holder[y];

               x = x + 2;
               y = y + 2;
           }


       }

       /**
         * Returns the number of cities in the TSP
         *
         */
       public int numCities() {

              return NUM_CITIES;
       }

       /**
         * Returns the X Co-ordinate of the city specified by cityID
         *
         */
       public double getX( int cityID ) {

              return lookUpTable[ cityID ][0];
       }

       /**
         * Returns the Y Co-ordinate of the city specified by cityID
         *
         */
       public double getY( int cityID ) {

              return lookUpTable[ cityID ][1];
       }

}





