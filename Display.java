/**
  *     Display Class for Printing to Screen...
  *
  *
  **/


import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;

public class Display implements ActionListener {


       static JFrame statusBox = new JFrame("Evolver : Status Window");
       static JMenuBar menuBar = new JMenuBar();

       // MenuItems
       private JMenuItem aboutItem;
       private JMenuItem exitItem;

       // Holders
       private static int POP_SIZE;
       private static int NUM_GENES;
       private static int INV_RATE;     // inversion rate% = INV_RATE/1000
       private static int GENERATIONS;
       private static String VERSION;

       // JLabels
       private JLabel generationLabel;
       private JLabel bestLabel;
       private JLabel worstLabel;
       private JLabel averageLabel;
       private JLabel bestsofarLabel;

       public Display(int pop_size, int num_genes, int inv, int lifespan, String VER){

              POP_SIZE = pop_size;
              NUM_GENES = num_genes;
              INV_RATE = inv;
              GENERATIONS = lifespan;
              VERSION = new String( VER );

              statusBox.setBounds(500, 200, 400, 350);
              statusBox.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

              statusBox.setJMenuBar(menuBar);

              // Create Menus
              JMenu fileMenu = new JMenu("File");
              JMenu helpMenu = new JMenu("Help");

              // Add Menu Items
              exitItem = new JMenuItem("Exit");
              aboutItem = new JMenuItem("About");

              fileMenu.add( exitItem );
              helpMenu.add( aboutItem );

              // Add Menus to menuBar
              menuBar.add(fileMenu);
              menuBar.add(helpMenu);

              // Add listeners
              aboutItem.addActionListener( this );
              exitItem.addActionListener( this );



              // Left Frame Content
              Box left = Box.createVerticalBox();
              left.add( Box.createVerticalStrut(5) );
              left.add( new JLabel( "Inv/Mut Rate : " + INV_RATE));
              left.add( new JLabel( "Pop. Size    : " + POP_SIZE));
              left.add( new JLabel( "Gene Length  : " + NUM_GENES));
              left.add( new JLabel( "Lifespan     : " + GENERATIONS + " generations"));


              left.add( Box.createGlue() );


              // Left Frame
              JPanel frameLeft = new JPanel( new BorderLayout() );
              frameLeft.setBorder( new TitledBorder( new EtchedBorder(), "Run Info") );
              frameLeft.add( left, BorderLayout.CENTER );

              // Right Frame Content
              Box right = Box.createVerticalBox();
              right.add( Box.createVerticalStrut(5) );

              // Create labels
              generationLabel = new JLabel( "Generation  : ");
              bestLabel = new JLabel( "Best Individual  : ");
              worstLabel = new JLabel( "Worst Individual : " );
              averageLabel = new JLabel( "Average Fitness  : ");
              bestsofarLabel = new JLabel( "Best So Far : " );

              // Add 'em
              right.add( generationLabel );
              right.add( bestLabel );
              right.add( worstLabel );
              right.add( averageLabel );
              right.add( bestsofarLabel );

              // Right Frame
              JPanel frameRight = new JPanel( new BorderLayout() );
              frameRight.setBorder( new TitledBorder( new EtchedBorder(), "Current Generation") );
              frameRight.add( right, BorderLayout.CENTER );

              // Row to hold left and right
              //Box holder = Box.createHorizontalBox();
              Box holder = Box.createVerticalBox();
              holder.add( frameLeft );
              holder.add( Box.createHorizontalStrut(5) );
              holder.add( frameRight );

              Container content = statusBox.getContentPane();
              BoxLayout box = new BoxLayout( content, BoxLayout.Y_AXIS );

              content.setLayout( new BorderLayout() );
              content.add( holder, BorderLayout.CENTER );

       }



       public void update(double[] best, double[] worst, int generation, double average, double best_fitness, double best_fit_gen){

              // Update evolver status info
              generationLabel.setText( "Generation  : " + generation);
              bestLabel.setText( "Best Individual  : " + (int) best[0] + ", fitness is " + best[1] );
              worstLabel.setText( "Worst Individual : " + (int) worst[0] + ", fitness is " + worst[1] );
              averageLabel.setText( "Average Fitness  : " + average);
              bestsofarLabel.setText( "Best So Far : " + best_fitness + ", in generation " + (int) best_fit_gen );



       }



       static public void show(){

              statusBox.setVisible(true);
       }

       public void actionPerformed( ActionEvent e ){

              if ( e.getSource() == aboutItem){

                 // Show dialog box...
                 AboutDialog aboutEvolver = new AboutDialog( statusBox, "About Evolver");
                 /*JOptionPane.showMessageDialog( this,
                                                "evolver v0.3, (C)2003 D.Murphy, G.Mitchell, NUI Maynooth.",
                                                "About Evolver",
                                                JOptionPane.INFORMATION_MESSAGE);*/
              }
              else
                  if ( e.getSource() == exitItem ){

                     System.exit(254);           // Bye bye!
                  }
       }


       // About dialog box code
       class AboutDialog extends JDialog implements ActionListener{

             public AboutDialog( Frame parent, String title ){

                 super(parent, title, true);

                 if ( parent != null ){
                   Dimension parentSize = parent.getSize();
                   Point p = parent.getLocation();
                   setLocation( p.x+parentSize.width/4, p.y+parentSize.height/4 );
                 }


                 // Create the message pane
                 JPanel messagePane = new JPanel();
                 messagePane.add( new JLabel( "evolver, v" + VERSION ));
                 messagePane.add( new JLabel( "(C)2003 D. Murphy & G. Mitchell," ));
                 messagePane.add( new JLabel( "Dept. Comp. Sci., NUI Maynooth" ));
                 getContentPane().add( messagePane );

                 // Create the ok button and add
                 JPanel buttonPane = new JPanel();
                 JButton okButton = new JButton("OK!");
                 buttonPane.add( okButton );
                 okButton.addActionListener( this );
                 getContentPane().add( buttonPane, BorderLayout.SOUTH );
                 setDefaultCloseOperation( DISPOSE_ON_CLOSE );
                 pack();
                 setVisible( true );

             }

             // OK Button Action
             public void actionPerformed( ActionEvent e ){

                 setVisible( false );
                 dispose();
             }
       }



}
