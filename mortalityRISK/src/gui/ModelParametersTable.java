package gui;


import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * A class to display the parameters of the model and their definitions
 */
public class ModelParametersTable {

	private static JFrame frame;
	/**
	 * For testing purposes
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize("Spatial");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Launch the application.
	 * @param type "Spatial" or "Non-Spatial"
	 */
	public static void launchApplication(String type) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize(type);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private static void initialize(String type) {

		frame = new JFrame("Model Parameters");
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ModelParametersTable.class.getResource("/resources/Risky 16x16.png")));
		frame.setSize(1000, 500);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		
		final Color BG_DARK = new Color(238, 238, 238);       
	    final Color BG_ALT = new Color(248, 248, 248);   // Slightly lighter for Zebra
	    final Color TEXT_COLOR = new Color(0, 0, 0); 
	    final Color GRID_COLOR = new Color(60, 63, 65);  
	    final Color HEADER_BG = new Color(60, 63, 65);

		JTable table;
		
		// Column Names
		String[] columns = {"Parameter", "Value Type", "Range", "Description"};
		
		if (type.equals("Spatial")) {
			String[][] data = {
					{"Sex Ratio", "Probability", "0 - 1", "Probability that a newborn will be male (instead of female)."},
					{"Infras.-Induced Mortality", "Ind./Inf. Unit/Iteration", "0 - Infinity", "Nnumber of individuals killed by infrastructure per unit of infrastructure and per iteration."},
					{"Survival Rate", "Probability/Iteration", "0 - 1", "Probability that an individual survives each iteration, excluding infrastructure-induced mortality."},
					{"Longevity", "Iterations", "1 - Infinity", "Maximum number of iterations an individual can live."},
					{"Life Phase Change", "Iterations", "1 - Infinity", "Age at which each life phase ends, separated by semicolons (;)"},
					{"Age at First Birth", "Iterations", "0 - Infinity", "Minimum age at which an individual can start reproducing."},
					{"Min Interval Births", "Iterations", "0 - Infinity", "Minimum number of iterations between consecutive reproductive events."},					
					{"Avg Interval Births", "Iterations", "0 - Infinity", "Average number of iterations between reproduction events."},
					{"Birth Rate","Probability/Iteration","0-1","Probability that an individual reproduces during a given iteration. Overrides Avg Interval Births if provided."},
					{"Maximum Offspring Number", "Individuals", "1 - Infinity", "Maximum number of offspring produced in a single reproductive event."},
					{"Minimum Offspring Number", "Individuals", "1 - Infinity", "Minimum number of offspring produced in a single reproductive event."},
					{"Average Offspring Number", "Individuals", "1 - Infinity", "Average number of offspring produced per reproductive event. If used, minimum and maximum litter sizes are ignored."},
					{"Population Density", "Ind./km2", "0 - Infinity", "Number of individuals per km², used to estimate the initial population size per cell."},
					{"Maximum Density", "Ind./km2", "0 - Infinity", "Maximum number of individuals per km² supported by the environment (carrying capacity)."},
					{"Max Dispersal Length", "Meters/Iteration", "0 - Infinity", "Maximum distance an individual can move during a single iteration."},
					{"Mate Finding Radius", "Meters", "0 - Infinity", "Radius within which females search for potential mates."}
			}; 
			// Create a custom JTable for the Zebra Effect
            table = new JTable(data, columns) {

				private static final long serialVersionUID = 1L;

				@Override
                public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                    Component c = super.prepareRenderer(renderer, row, column);
                    if (!isRowSelected(row)) {
                        // Apply Zebra effect: alternate background colors
                        c.setBackground(row % 2 == 0 ? BG_DARK : BG_ALT);
                        c.setForeground(TEXT_COLOR);
                    }
                    return c;
                }
				
	            @Override // Make the table read-only
	            public boolean isCellEditable(int row, int column) {
	                return false; 
	            }
            };
		} else {
			String[][] data = {
					{"Sex Ratio", "Probability", "0 - 1", "The probability that a newborn will be male (instead of female)."},
					{"Infras.-Induced Mortality", "Ind./Inf. Unit/Iteration", "0 - Infinity", "	The number of individuals killed by infrastructure per unit of infrastructure and per iteration."},
					{"Survival Rate", "Probability/Iteration", "0 - 1", "The probability that an individual survives each iteration, excluding infrastructure-induced mortality."},
					{"Longevity", "Iterations", "1 - Infinity", "The maximum number of iterations an individual can live."},
					{"Life Phase Change", "Iterations", "1 - Infinity", "The age at which each life phase ends, separated by semicolons (;)"},
					{"Age at First Birth", "Iterations", "0 - Infinity", "The minimum age at which an individual can start reproducing."},
					{"Min Interval Births", "Iterations", "0 - Infinity", "The minimum number of iterations between consecutive reproductive events."},					
					{"Avg Interval Births", "Iterations", "0 - Infinity", "The average number of iterations between reproduction events."},
					{"Birth Rate","Probability/Iteration","0-1","The probability that an individual reproduces during a given iteration. Overrides Avg Interval Births if provided."},
					{"Maximum Offspring Number", "Individuals", "1 - Infinity", "The maximum number of offspring produced in a single reproductive event."},
					{"Minimum Offspring Number", "Individuals", "1 - Infinity", "The minimum number of offspring produced in a single reproductive event."},
					{"Average Offspring Number", "Individuals", "1 - Infinity", "The average number of offspring produced per reproductive event. If used, minimum and maximum litter sizes are ignored."},
					{"Starting Population", "Individuals", "1 - Infinity", "Initial number of individuals."},
					{"Maximum Population", "Individuals", "0 - Infinity", "Maximum number of individuals supported by the environment (carrying capacity)."}
			};
			//Create a custom JTable for the Zebra Effect
            table = new JTable(data, columns) {

				private static final long serialVersionUID = 1L;

				@Override
                public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                    Component c = super.prepareRenderer(renderer, row, column);
                    if (!isRowSelected(row)) {
                        // Apply Zebra effect: alternate background colors
                        c.setBackground(row % 2 == 0 ? BG_DARK : BG_ALT);
                        c.setForeground(TEXT_COLOR);
                    }
                    return c;
                }
            };
		}

		//Style the Header
        JTableHeader header = table.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(Font.BOLD));

        //General Table Styling
        table.setBackground(BG_DARK);
        table.setGridColor(GRID_COLOR);
        table.setRowHeight(25);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);

     // Fitting the Window
        table.getColumnModel().getColumn(0).setPreferredWidth(170);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(665);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        frame.getContentPane().add(scrollPane);

        frame.pack(); 
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
	}
	
	/**
	 * Checks if the window is open or not
	 * @return
	 */
	public static boolean isOpen() {
		if (frame != null) {
			return frame.isVisible();
		} else {
			return  false;
		}
	}
}