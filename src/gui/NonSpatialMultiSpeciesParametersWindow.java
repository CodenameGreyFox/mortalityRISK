package gui;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

/**
 * Class to provide a window to edit parameters. Currently not in use.
 */
public class NonSpatialMultiSpeciesParametersWindow {

	private static NonSpatialMultiSpeciesWindow mainWindow;
	private static JFrame frmSpeciesParameters;
	private static JTextField textMatureAge;
	private static JLabel lblMatureAge;
	private static JLabel lblMatureAgeValue;
	private static int speciesIndex;
	private static JLabel lblLongevity;
	private static JLabel lblLongevityValue;
	private static JTextField textLongevity;
	private static JLabel lblLifePhases;
	private static JLabel lblLifePhasesValue;
	private static JTextField textLifePhases;
	private static JButton btnSave;
	private static JButton btnCancel;
	private static JLabel lblMaximumPopulation;
	private static JLabel lblMaximumPopulationValue;
	private static JTextField textMaximumPopulation;
	private static JLabel lblMaximumLitterSize;
	private static JLabel lblMaximumLitterSizeValue;
	private static JTextField textMaximumLitterSize;
	private static JLabel lblMinimumIntervalBirths;
	private static JLabel lblMinimumIntervalBirthsValue;
	private static JTextField textMinimumIntervalBirths;
	private static JLabel lblSexRatio;
	private static JLabel lblSexRatioValue;
	private static JTextField textSexRatio;
	private static JLabel lblRoadkillKmYearMale;
	private static JLabel lblRoadkillKmYearMaleValue;
	private static JTextField textRoadkillKmYearMale;
	private static JLabel lblDeathProbabilityMale;
	private static JLabel lblDeathProbabilityMaleValue;
	private static JTextField textDeathProbabilityMale;
	private static JLabel lblAverageIntervalBirths;
	private static JLabel lblAverageIntervalBirthsValue;
	private static JTextField textAverageIntervalBirths;
	private static JLabel lblMaximumDispersal;
	private static JLabel lblMaximumDispersalValue;
	private static JTextField textMaximumDispersal;
	private static JLabel lblDeathProbabilityFemale;
	private static JTextField textDeathProbabilityFemale;
	private static JLabel lblDeathProbabilityFemaleValue;
	private static JLabel lblRoadkillkmYearFemale;
	private static JLabel lblRoadkillKmYearFemaleValue;
	private static JTextField textRoadkillkmYearFemale;
	private static JTextField textField_1;
	private static JLabel lblMinimumLitterSize;
	private static JLabel lblMinimumLitterSizeValue;

	/**
	 * Launch the application for testing purposes
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
					frmSpeciesParameters.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Launch the Non Spatial Parameters
	 */
	public static void launchApplication (NonSpatialMultiSpeciesWindow window) {

		mainWindow = window;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
					frmSpeciesParameters.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Checks if the window is open
	 * @return
	 */
	public static boolean isOpen() {
		if (frmSpeciesParameters != null) {
			return frmSpeciesParameters.isVisible();
		} else {
			return  false;
		}
	}


	/**
	 * Create the application.
	 */
	public NonSpatialMultiSpeciesParametersWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private static void initialize() {
		speciesIndex = mainWindow.getParameterPackage().getSpeciesIndex(mainWindow.getCurrentlySelectedSpecies());
		frmSpeciesParameters = new JFrame();
		frmSpeciesParameters.setAlwaysOnTop(true);
		frmSpeciesParameters.setIconImage(Toolkit.getDefaultToolkit().getImage(NonSpatialMultiSpeciesParametersWindow.class.getResource("/resources/Risky 16x16.png")));
		frmSpeciesParameters.setTitle( mainWindow.getParameterPackage().getOriginalSpeciesNames(false)[speciesIndex] + " traits");
		frmSpeciesParameters.setBounds(100, 100, 446, 396);
		frmSpeciesParameters.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmSpeciesParameters.getContentPane().setLayout(new MigLayout("", "[][][33.00][grow]", "[][][][][][][][][][][][][][][][][][][]"));

		lblDeathProbabilityMale = new JLabel("Death Probability (Male):");
		frmSpeciesParameters.getContentPane().add(lblDeathProbabilityMale, "cell 1 0,alignx right");
		textDeathProbabilityMale = new JTextField();
		textDeathProbabilityMale.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {			
				try {
					String[] MortalityString = textDeathProbabilityMale.getText().split(";");
					double [] Mortality = new double[MortalityString.length];
					for (int i = 0; i < Mortality.length; i++) {
						Mortality[i] = Double.parseDouble(MortalityString[i]);
						if (Mortality[i]<0 || Mortality[i]>1) { 
							throw new NumberFormatException();
						}
					}	
					lblDeathProbabilityMaleValue.setText(textDeathProbabilityMale.getText());
					textDeathProbabilityMale.setText("");		
				} catch (NumberFormatException exception) {
					textDeathProbabilityMale.setText("Invalid!");
				} catch (NullPointerException exception) {
					//ignore
				}
			}
		});
		textDeathProbabilityMale.setColumns(10);
		frmSpeciesParameters.getContentPane().add(textDeathProbabilityMale, "cell 3 0");

		lblDeathProbabilityFemale = new JLabel("Death Probability (Female):");
		frmSpeciesParameters.getContentPane().add(lblDeathProbabilityFemale, "cell 1 1,alignx right");

		textDeathProbabilityFemale = new JTextField();
		textDeathProbabilityFemale.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {			
				try {
					String[] MortalityString = textDeathProbabilityFemale.getText().split(";");
					double [] Mortality = new double[MortalityString.length];
					for (int i = 0; i < Mortality.length; i++) {
						Mortality[i] = Double.parseDouble(MortalityString[i]);
						if (Mortality[i]<0 || Mortality[i]>1) { //Makes sure each life phases comes after the next 
							throw new NumberFormatException();
						}
					}	
					lblDeathProbabilityFemaleValue.setText(textDeathProbabilityFemale.getText());
					textDeathProbabilityFemale.setText("");		
				} catch (NumberFormatException exception) {
					textDeathProbabilityFemale.setText("Invalid!");
				} catch (NullPointerException exception) {
					//ignore
				}
			}
		});
		textDeathProbabilityFemale.setColumns(10);
		frmSpeciesParameters.getContentPane().add(textDeathProbabilityFemale, "cell 3 1");



		lblMatureAge = new JLabel("Mature Age:");
		frmSpeciesParameters.getContentPane().add(lblMatureAge, "cell 0 2 2 1,alignx right");

		lblMatureAgeValue = new JLabel(Integer.toString(mainWindow.getParameterPackage().matAge[speciesIndex]));
		frmSpeciesParameters.getContentPane().add(lblMatureAgeValue, "cell 2 2,alignx left");

		textMatureAge = new JTextField();
		textMatureAge.addActionListener(new InputIntegerRangeActionListener(0,10000000,textMatureAge, lblMatureAgeValue));

		frmSpeciesParameters.getContentPane().add(textMatureAge, "cell 3 2");
		textMatureAge.setColumns(10);

		lblLongevity = new JLabel("Maximum Longevity:");
		frmSpeciesParameters.getContentPane().add(lblLongevity, "cell 1 3,alignx right");

		lblLongevityValue = new JLabel(Integer.toString(mainWindow.getParameterPackage().lifePhases[speciesIndex][mainWindow.getParameterPackage().lifePhases[speciesIndex].length-1]));
		frmSpeciesParameters.getContentPane().add(lblLongevityValue, "cell 2 3,alignx left");

		textLongevity = new JTextField();
		textLongevity.addActionListener(new InputIntegerRangeActionListener(0,100000000,textLongevity, lblLongevityValue));
		textLongevity.setColumns(10);
		frmSpeciesParameters.getContentPane().add(textLongevity, "cell 3 3");

		lblLifePhases = new JLabel("Life Phases:");
		frmSpeciesParameters.getContentPane().add(lblLifePhases, "cell 1 4,alignx right");

		String lifePhasesLabelValue = "";
		for(int i = 0 ; i < mainWindow.getParameterPackage().lifePhases[speciesIndex].length-1; i++) {
			lifePhasesLabelValue += mainWindow.getParameterPackage().lifePhases[speciesIndex][i]+";";
		}
		if (lifePhasesLabelValue.length()>0) {
			lifePhasesLabelValue = lifePhasesLabelValue.substring(0,lifePhasesLabelValue.length()-1);
		}
		lblLifePhasesValue = new JLabel(lifePhasesLabelValue);
		frmSpeciesParameters.getContentPane().add(lblLifePhasesValue, "cell 2 4");

		textLifePhases = new JTextField();
		textLifePhases.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {			
				try {
					String[] lifePhasesString = textLifePhases.getText().split(";");
					int [] lifePhases = new int[lifePhasesString.length];
					for (int i = 0; i < lifePhases.length; i++) {
						lifePhases[i] = Integer.parseInt(lifePhasesString[i]);
						if (!(i==0 || lifePhases[i]>lifePhases[i-1]) || lifePhases[i] <1) { //Makes sure each life phases comes after the next
							throw new NumberFormatException();
						}
					}		
					lblLifePhasesValue.setText(textLifePhases.getText());
					textLifePhases.setText("");						

				} catch (NumberFormatException exception) {
					textLifePhases.setText("Invalid!");
				} catch (NullPointerException exception) {
					//ignore
				}
			}
		});
		textLifePhases.setColumns(10);
		frmSpeciesParameters.getContentPane().add(textLifePhases, "cell 3 4");

		lblMaximumPopulation = new JLabel("Maximum Population:");
		frmSpeciesParameters.getContentPane().add(lblMaximumPopulation, "cell 1 5,alignx right");

		lblMaximumPopulationValue = new JLabel(Double.toString(mainWindow.getParameterPackage().maxPopulation[speciesIndex]));
		frmSpeciesParameters.getContentPane().add(lblMaximumPopulationValue, "cell 2 5,alignx left");

		textMaximumPopulation = new JTextField();
		textMaximumPopulation.addActionListener(new InputIntegerRangeActionListener(0,1000000,textMaximumPopulation, lblMaximumPopulationValue));
		textMaximumPopulation.setColumns(10);
		frmSpeciesParameters.getContentPane().add(textMaximumPopulation, "cell 3 5");

		lblMinimumLitterSize = new JLabel("Minimum Litter Size:");
		lblMinimumLitterSize.setHorizontalAlignment(SwingConstants.TRAILING);
		frmSpeciesParameters.getContentPane().add(lblMinimumLitterSize, "cell 1 6,alignx right");

		lblMinimumLitterSizeValue = new JLabel(Integer.toString(mainWindow.getParameterPackage().minLitSize[speciesIndex]));
		frmSpeciesParameters.getContentPane().add(lblMinimumLitterSizeValue, "cell 2 6");

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		frmSpeciesParameters.getContentPane().add(textField_1, "cell 3 6");

		lblMaximumLitterSize = new JLabel("Maximum Litter Size:");
		frmSpeciesParameters.getContentPane().add(lblMaximumLitterSize, "cell 1 7,alignx right");

		lblMaximumLitterSizeValue = new JLabel(Integer.toString(mainWindow.getParameterPackage().maxLitSize[speciesIndex]));
		frmSpeciesParameters.getContentPane().add(lblMaximumLitterSizeValue, "cell 2 7");

		textMaximumLitterSize = new JTextField();
		textMaximumLitterSize.addActionListener(new InputIntegerRangeActionListener(0,1000000,textMaximumLitterSize, lblMaximumLitterSizeValue));
		textMaximumLitterSize.setColumns(10);
		frmSpeciesParameters.getContentPane().add(textMaximumLitterSize, "cell 3 7");

		lblMinimumIntervalBirths = new JLabel("Minimum Interval Between Births:");
		frmSpeciesParameters.getContentPane().add(lblMinimumIntervalBirths, "cell 1 8");

		lblMinimumIntervalBirthsValue = new JLabel(Integer.toString(mainWindow.getParameterPackage().minTimeBetweenBreeding[speciesIndex]));
		frmSpeciesParameters.getContentPane().add(lblMinimumIntervalBirthsValue, "cell 2 8");

		textMinimumIntervalBirths = new JTextField();
		textMinimumIntervalBirths.addActionListener(new InputIntegerRangeActionListener(0,1000000,textMinimumIntervalBirths, lblMinimumIntervalBirthsValue));
		textMinimumIntervalBirths.setColumns(10);
		frmSpeciesParameters.getContentPane().add(textMinimumIntervalBirths, "cell 3 8");

		lblSexRatio = new JLabel("Sex Ratio (Male):");
		frmSpeciesParameters.getContentPane().add(lblSexRatio, "cell 1 9,alignx right");

		lblSexRatioValue = new JLabel(Double.toString(mainWindow.getParameterPackage().sexRatio[speciesIndex]));
		frmSpeciesParameters.getContentPane().add(lblSexRatioValue, "cell 2 9");

		textSexRatio = new JTextField();
		textSexRatio.addActionListener(new InputDoubleRangeActionListener(0,1,textSexRatio, lblSexRatioValue));
		textSexRatio.setColumns(10);
		frmSpeciesParameters.getContentPane().add(textSexRatio, "cell 3 9");



		lblAverageIntervalBirths = new JLabel("Average Interval Between Births:");
		frmSpeciesParameters.getContentPane().add(lblAverageIntervalBirths, "cell 1 10,alignx right");

		lblAverageIntervalBirthsValue = new JLabel(Double.toString(mainWindow.getParameterPackage().avgTimeBetweenBreeding[speciesIndex]));
		lblAverageIntervalBirthsValue.setHorizontalAlignment(SwingConstants.LEFT);
		frmSpeciesParameters.getContentPane().add(lblAverageIntervalBirthsValue, "cell 2 10,alignx left");

		textAverageIntervalBirths = new JTextField();
		textAverageIntervalBirths.addActionListener(new InputDoubleRangeActionListener(0,10000000,textAverageIntervalBirths, lblAverageIntervalBirthsValue));
		textAverageIntervalBirths.setColumns(10);
		frmSpeciesParameters.getContentPane().add(textAverageIntervalBirths, "cell 3 10");



		lblMaximumDispersal = new JLabel("Maximum Dispersal Length:");
		frmSpeciesParameters.getContentPane().add(lblMaximumDispersal, "cell 1 11,alignx right");

		lblMaximumDispersalValue = new JLabel("0.0");
		frmSpeciesParameters.getContentPane().add(lblMaximumDispersalValue, "cell 2 11,alignx left");

		textMaximumDispersal = new JTextField();
		textMaximumDispersal.addActionListener(new InputDoubleRangeActionListener(0,10000000,textMaximumDispersal, lblMaximumDispersalValue));
		textMaximumDispersal.setColumns(10);
		frmSpeciesParameters.getContentPane().add(textMaximumDispersal, "cell 3 11");

		String DeathProbabilityFemaleValue = "";
		for(int i = 1 ; i < mainWindow.getParameterPackage().baseBirthMort[speciesIndex].length; i++) { //Skips 0 as it is birth rate.
			DeathProbabilityFemaleValue += mainWindow.getParameterPackage().baseBirthMort[speciesIndex][i][1]+";";
		}
		if (DeathProbabilityFemaleValue.length()>0) {
			DeathProbabilityFemaleValue = DeathProbabilityFemaleValue.substring(0,DeathProbabilityFemaleValue.length()-1);
		}		

		lblDeathProbabilityFemaleValue = new JLabel(DeathProbabilityFemaleValue);
		frmSpeciesParameters.getContentPane().add(lblDeathProbabilityFemaleValue, "cell 2 13,alignx left");

		String DeathProbabilityMaleValue = "";
		for(int i = 1 ; i < mainWindow.getParameterPackage().baseBirthMort[speciesIndex].length; i++) { //Skips 0 as it is birth rate.
			DeathProbabilityMaleValue += mainWindow.getParameterPackage().baseBirthMort[speciesIndex][i][0]+";";
		}
		if (DeathProbabilityMaleValue.length()>0) {
			DeathProbabilityMaleValue = DeathProbabilityMaleValue.substring(0,DeathProbabilityMaleValue.length()-1);
		}		
		lblDeathProbabilityMaleValue = new JLabel(DeathProbabilityMaleValue);
		frmSpeciesParameters.getContentPane().add(lblDeathProbabilityMaleValue, "cell 2 14,alignx left");

		lblRoadkillKmYearMale = new JLabel("Roadkill/Km/Year (Male):");
		frmSpeciesParameters.getContentPane().add(lblRoadkillKmYearMale, "cell 1 15,alignx right");

		lblRoadkillKmYearMaleValue = new JLabel(Double.toString(mainWindow.getParameterPackage().roadkillPerKmPerYear[speciesIndex][0]));
		frmSpeciesParameters.getContentPane().add(lblRoadkillKmYearMaleValue, "cell 2 15,alignx left");

		textRoadkillKmYearMale = new JTextField();
		textRoadkillKmYearMale.addActionListener(new InputDoubleRangeActionListener(0,10000000,textRoadkillKmYearMale, lblRoadkillKmYearMaleValue));
		textRoadkillKmYearMale.setColumns(10);
		frmSpeciesParameters.getContentPane().add(textRoadkillKmYearMale, "cell 3 15");

		lblRoadkillkmYearFemale = new JLabel("Roadkill/Km/Year (Female):");
		frmSpeciesParameters.getContentPane().add(lblRoadkillkmYearFemale, "cell 1 16,alignx right");

		lblRoadkillKmYearFemaleValue = new JLabel(Double.toString(mainWindow.getParameterPackage().roadkillPerKmPerYear[speciesIndex][1]));
		frmSpeciesParameters.getContentPane().add(lblRoadkillKmYearFemaleValue, "cell 2 16,alignx left");

		textRoadkillkmYearFemale = new JTextField();
		textRoadkillkmYearFemale.addActionListener(new InputDoubleRangeActionListener(0,10000000,textRoadkillkmYearFemale, lblRoadkillKmYearFemaleValue));
		textRoadkillkmYearFemale.setColumns(10);
		frmSpeciesParameters.getContentPane().add(textRoadkillkmYearFemale, "cell 3 16");

		btnCancel = new JButton("Cancel Changes");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmSpeciesParameters.setVisible(false); 
				frmSpeciesParameters.dispose();
			}
		});

		frmSpeciesParameters.getContentPane().add(btnCancel, "cell 3 18");


		btnSave = new JButton("Save Changes");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveParameters();
				frmSpeciesParameters.setVisible(false); 
				frmSpeciesParameters.dispose();
			}
		});

		frmSpeciesParameters.getContentPane().add(btnSave, "cell 1 18");


		frmSpeciesParameters.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmSpeciesParameters.pack();

	}

	/**
	 * Saves the set parameters
	 */

	static private void saveParameters() {

		mainWindow.getParameterPackage().matAge[speciesIndex] = Integer.parseInt(lblMatureAgeValue.getText());


		String[] lifePhasesString = lblLifePhasesValue.getText().split(";");
		mainWindow.getParameterPackage().lifePhases[speciesIndex] = new int[lifePhasesString.length+1]; //Creates a new array of lifephases +1 for the longevity.
		for (int i = 0; i < lifePhasesString.length; i++) {
			if(!lifePhasesString[i].contentEquals("")) {
				mainWindow.getParameterPackage().lifePhases[speciesIndex][i] = Integer.parseInt(lifePhasesString[i]);
			}
		}		
		mainWindow.getParameterPackage().lifePhases[speciesIndex][lifePhasesString.length] = Integer.parseInt(lblLongevityValue.getText());

		mainWindow.getParameterPackage().maxPopulation[speciesIndex] = Integer.parseInt(lblMaximumPopulationValue.getText());

		mainWindow.getParameterPackage().minLitSize[speciesIndex] = Integer.parseInt(lblMinimumLitterSizeValue.getText());
		mainWindow.getParameterPackage().maxLitSize[speciesIndex] = Integer.parseInt(lblMaximumLitterSizeValue.getText());		


		mainWindow.getParameterPackage().sexRatio[speciesIndex] = Double.parseDouble(lblSexRatioValue.getText());

		mainWindow.getParameterPackage().roadkillPerKmPerYear[speciesIndex][0] = Double.parseDouble(lblRoadkillKmYearMaleValue.getText());
		mainWindow.getParameterPackage().roadkillPerKmPerYear[speciesIndex][1] = Double.parseDouble(lblRoadkillKmYearFemaleValue.getText());

		String[] DeathProbMaleString = lblDeathProbabilityMaleValue.getText().split(";");		
		String[] DeathProbFemaleString = lblDeathProbabilityFemaleValue.getText().split(";");	
		mainWindow.getParameterPackage().baseBirthMort[speciesIndex] = new double[DeathProbMaleString.length+1][2];		
		for (int i = 0; i < DeathProbMaleString.length; i++) {
			mainWindow.getParameterPackage().baseBirthMort[speciesIndex][i+1][0] = Double.parseDouble(DeathProbMaleString[i]);
			mainWindow.getParameterPackage().baseBirthMort[speciesIndex][i+1][1] = Double.parseDouble(DeathProbFemaleString[i]);
		}

		mainWindow.getParameterPackage().minTimeBetweenBreeding[speciesIndex] = Integer.parseInt(lblMinimumIntervalBirthsValue.getText());

		mainWindow.getParameterPackage().avgTimeBetweenBreeding[speciesIndex] = Double.parseDouble(lblAverageIntervalBirthsValue.getText());


		mainWindow.getParameterPackage().disRan[speciesIndex] = Double.parseDouble(lblMaximumDispersalValue.getText());

		mainWindow.getParameterPackage().updatePackage();

		frmSpeciesParameters.dispose();

	}

	/**
	 * For updating values on text fields with values going from 0 - infinity
	 */
	static private class InputIntegerRangeActionListener implements ActionListener {
		private JTextField textField;
		private JLabel valueLable;
		private int minimum;
		private int maximum;


		public InputIntegerRangeActionListener(int minimum, int maximum, JTextField textField, JLabel valueLable) {
			this.textField = textField;
			this.valueLable = valueLable;
			this.minimum = minimum;
			this.maximum = maximum;
		}		


		public void actionPerformed(ActionEvent e) {
			try {
				if (Integer.parseInt(textField.getText()) < minimum ) {
					valueLable.setText(Integer.toString(minimum));
					textField.setText("");
				} else if (Integer.parseInt(textField.getText()) > maximum) {
					valueLable.setText(Integer.toString(maximum));
					textField.setText("");
				} else {
					valueLable.setText(textField.getText());
					textField.setText("");
				}
			} catch (NumberFormatException exception) {
				textField.setText("Invalid!");
			} catch (NullPointerException exception) {
				//ignore
			}	    }
	}

	/**
	 * For updating values on text fields with values going from 0 - infinity
	 */
	static private class InputDoubleRangeActionListener implements ActionListener {
		private JTextField textField;
		private JLabel valueLable;
		private int minimum;
		private int maximum;


		public InputDoubleRangeActionListener(int minimum, int maximum, JTextField textField, JLabel valueLable) {
			this.textField = textField;
			this.valueLable = valueLable;
			this.minimum = minimum;
			this.maximum = maximum;
		}		


		public void actionPerformed(ActionEvent e) {
			try {
				if (Double.parseDouble(textField.getText()) < minimum ) {
					valueLable.setText(Double.toString(minimum));
					textField.setText("");
				} else if (Double.parseDouble(textField.getText()) > maximum) {
					valueLable.setText(Double.toString(maximum));
					textField.setText("");
				} else {
					valueLable.setText(textField.getText());
					textField.setText("");
				}
			} catch (NumberFormatException exception) {
				textField.setText("Invalid!");
			} catch (NullPointerException exception) {
				//ignore
			}	    }
	}


}


