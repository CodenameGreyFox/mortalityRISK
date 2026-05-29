package gui;


import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;

import model.Map;
import model.Model;
import model.OutputProcessor;
import model.ParameterPackage;
import net.miginfocom.swing.MigLayout;

/**
 * Class that provides the GUI for the Non-Spatial Model
 */

public class NonSpatialMultiSpeciesWindow implements PropertyChangeListener {

	private JFrame frmNonSpatialMultiSpecies;
	private JTextField txtInputFileLocation;

	private String location;
	private JLabel lblInputFile;
	private JLabel lblRoadMapLocation;
	private JProgressBar progressBar;
	private JTextField iterationsToRun;
	private JSpinner numberOfRepetitions;
	private JSpinner numberOfRoadVariations;
	private JLabel lblNumberOfRepetions;
	private JLabel lblNumberOfIterations;
	private JLabel lblNumberOfRoadVariations;
	private JLabel lblOutputLocation;
	private JButton browseOutputFolder;
	private JButton browseInputFile;
	private JTextField txtOutputLocation;
	private JButton btnGenerateTemplate;
	private JButton btnRunModel;
	private JLabel lblNumberOfCores;
	private JSpinner numberOfCores;

	private Model model;
	private JLabel lblCurrentIteration;
	private JLabel lblCurrentIterationValue;

	private ParameterPackage modelParameters;
	//	private JButton btnEditParameters;
	private JButton btnLoadParametersFile;
	private JComboBox<String> comboBoxSpeciesList;
	//	private JButton btnSaveParametersToFile;
	private JTextField textFieldInfraDensity;

	//Model Variables
	private String[][] resultsRepeated;
	private Map[][][] resultsExtinctionRepeated;
	private JComboBox<String> comboBoxTimeUnit;
	private JLabel lblTimeUnit;
	private JLabel lblStartDate;
	private JTextField txtYyyymmdd;
	private JCheckBox sweepMortalityCheck;
	private JLabel lblSweepResolution;
	private JSpinner sweepResolution;
	private JLabel lblSweepStartAndEnd;
	private JSpinner sweepResolutionMin;
	private JSpinner sweepResolutionMax;
	private JLabel lblEstimatedTime;
	private JCheckBox chckbxScaleToYear;
	private JLabel lblMaxProcessedInd;
	private JSpinner maxProcInd;
	private JCheckBox chckbxSweepRoadkillInstead;
	private RunModel rm;
	private int[] itToRun;
	private JLabel lblInfoTemplate;
	private JLabel lblInfoXLSX;
	private JLabel lblInfoVariations;
	private JLabel lblInfoIterations;
	private JLabel lblInfoRepetitions;
	private JLabel lblInfoOutput;
	private JLabel lblInfoDate;
	private JLabel lblInfoTimeUnit;
	private JLabel lblInfoCores;
	private JLabel lblInfoMaxInd;
	private JLabel lblInfoSweep;
	private JLabel lblInfoSweepRes;
	private JLabel lblInfoScale;
	private JLabel lblInfoSweep_1;
	private JLabel lblInfoInfNetwork;
	private JButton btnParameterHelp;

	/**
	 * Launch the application for testing.
	 */
	public static void main(String[] args) {
		launchApplication();
	}

	/**
	 * Launch the application.
	 */
	public static void launchApplication() {
		EventQueue.invokeLater(new Runnable() {			
			public void run() {
				try {
					NonSpatialMultiSpeciesWindow window = new NonSpatialMultiSpeciesWindow();
					window.frmNonSpatialMultiSpecies.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public NonSpatialMultiSpeciesWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		model = null;
		location = System.getProperty("user.dir");

		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

		frmNonSpatialMultiSpecies = new JFrame();
		frmNonSpatialMultiSpecies.setIconImage(Toolkit.getDefaultToolkit().getImage(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Risky 16x16.png")));
		frmNonSpatialMultiSpecies.setTitle("mortalityRISK - Non-Spatial Multi-Species Model");
		frmNonSpatialMultiSpecies.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmNonSpatialMultiSpecies.getContentPane().setLayout(new MigLayout("", "[75.00px][63.00px,grow][150.00px][103px,grow]", "[30px][30px][30px][][30px][30px][30px][][][][30px][][30px][30px]"));

		lblInputFile = new JLabel("Species/Population Life Traits:");
		frmNonSpatialMultiSpecies.getContentPane().add(lblInputFile, "flowx,cell 0 0 2 1");


		SpinnerNumberModel spinnerModelCore = new SpinnerNumberModel(1, 1, Runtime.getRuntime().availableProcessors(), 1);//(int)Math.round(((double)Runtime.getRuntime().availableProcessors())/2+((double)Runtime.getRuntime().availableProcessors())/4), 1, Runtime.getRuntime().availableProcessors(), 1);
		SpinnerNumberModel spinnerModelRoad = new SpinnerNumberModel(1,0,99,1);  


		browseInputFile = new JButton("Browse...");		
		browseInputFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				javax.swing.filechooser.FileFilter csvFilter = new javax.swing.filechooser.FileNameExtensionFilter("Input Files","xlsx","xls","csv");
				fileChooser.setFileFilter(csvFilter);
				fileChooser.setCurrentDirectory(new File(location + System.getProperty("file.separator")));
				fileChooser.showOpenDialog(new JFrame());
				if (fileChooser.getSelectedFile() != null) {
					txtInputFileLocation.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});		
		frmNonSpatialMultiSpecies.getContentPane().add(browseInputFile, "flowx,cell 0 1");

		txtInputFileLocation = new JTextField();
		txtInputFileLocation.setText(location + System.getProperty("file.separator") + "InputFile.xlsx");
		frmNonSpatialMultiSpecies.getContentPane().add(txtInputFileLocation, "cell 1 1 3 1,growx");
		txtInputFileLocation.setColumns(10);


		btnLoadParametersFile = new JButton("Validate");
		btnLoadParametersFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				try {
					modelParameters = new ParameterPackage(txtInputFileLocation.getText(),(int)numberOfRoadVariations.getValue(), "NonSpatial");
					comboBoxSpeciesList.setModel(new DefaultComboBoxModel<>(modelParameters.getOriginalSpeciesNames(false)));

				} catch (Exception e1) {
					progressBar.setString(e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		frmNonSpatialMultiSpecies.getContentPane().add(btnLoadParametersFile, "cell 1 2,alignx center");

		/* To be added
		btnEditParameters = new JButton("Edit");		
		btnEditParameters.addActionListener(new ParameterActionListener(this));		
		frmNonSpatialMultiSpecies.getContentPane().add(btnEditParameters, "cell 2 2,alignx center");


		btnSaveParametersToFile = new JButton("Save");
		btnSaveParametersToFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select Location");
				fileChooser.setCurrentDirectory(new File(location + System.getProperty("file.separator")));
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if (fileChooser.showSaveDialog( new JFrame()) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("xlsx")) {
						// filename is OK as-is
					} else {
						file = new File(file.getParentFile(), FilenameUtils.getBaseName(file.getName())+".xlsx"); // ALTERNATIVELY: remove the extension (if any) and replace it with ".xml"
					}

					// save to file
					try {
						modelParameters.saveToFile(file.getAbsolutePath(), "NonSpatial");

					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}


			}
		});
		frmNonSpatialMultiSpecies.getContentPane().add(btnSaveParametersToFile, "cell 3 2");
		 */

		comboBoxSpeciesList = new JComboBox<String>();
		frmNonSpatialMultiSpecies.getContentPane().add(comboBoxSpeciesList, "cell 2 2,growx");

		btnParameterHelp = new JButton("Parameters");
		btnParameterHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!ModelParametersTable.isOpen()) {
					ModelParametersTable.launchApplication("Non-Spatial");
				}
			}
		});		
		btnParameterHelp.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		frmNonSpatialMultiSpecies.getContentPane().add(btnParameterHelp, "cell 3 2");

		lblRoadMapLocation = new JLabel("Infrastructure Density");
		frmNonSpatialMultiSpecies.getContentPane().add(lblRoadMapLocation, "cell 0 4,alignx trailing");

		textFieldInfraDensity = new JTextField();
		textFieldInfraDensity.setHorizontalAlignment(SwingConstants.RIGHT);
		textFieldInfraDensity.setText("100.00");
		frmNonSpatialMultiSpecies.getContentPane().add(textFieldInfraDensity, "flowx,cell 1 4,growx");
		textFieldInfraDensity.setColumns(10);

		lblNumberOfRoadVariations = new JLabel("Extra scenarios:");
		frmNonSpatialMultiSpecies.getContentPane().add(lblNumberOfRoadVariations, "cell 2 4,alignx right");
		numberOfRoadVariations = new JSpinner(spinnerModelRoad);
		numberOfRoadVariations.setPreferredSize(new Dimension(50,20));
		frmNonSpatialMultiSpecies.getContentPane().add(numberOfRoadVariations, "flowx,cell 3 4");


		lblNumberOfRepetions = new JLabel("Number of repetitions:");
		frmNonSpatialMultiSpecies.getContentPane().add(lblNumberOfRepetions, "cell 0 5,alignx right");
		numberOfRepetitions = new JSpinner(new SpinnerNumberModel(100, 0, 10000, 1));
		numberOfRepetitions.setPreferredSize(new Dimension(50,20));
		frmNonSpatialMultiSpecies.getContentPane().add(numberOfRepetitions, "flowx,cell 1 5");

		lblNumberOfIterations = new JLabel("Number of iterations:");
		frmNonSpatialMultiSpecies.getContentPane().add(lblNumberOfIterations, "cell 2 5,alignx right");
		iterationsToRun = new JTextField();
		iterationsToRun.setText("50;100");
		iterationsToRun.setPreferredSize(new Dimension(100, 20));
		frmNonSpatialMultiSpecies.getContentPane().add(iterationsToRun, "flowx,cell 3 5,aligny center");

		browseOutputFolder = new JButton("Browse...");
		browseOutputFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setCurrentDirectory(new File(location + System.getProperty("file.separator")));
				fileChooser.showOpenDialog(new JFrame());
				if (fileChooser.getSelectedFile() != null) {
					txtOutputLocation.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});		

		sweepMortalityCheck = new JCheckBox("Sweep mortality?");
		frmNonSpatialMultiSpecies.getContentPane().add(sweepMortalityCheck, "flowx,cell 0 7,alignx right");

		lblSweepResolution = new JLabel("Sweep Resolution:");
		frmNonSpatialMultiSpecies.getContentPane().add(lblSweepResolution, "cell 1 7,alignx right");

		sweepResolution = new JSpinner(new SpinnerNumberModel(21, 2, 10000, 1));
		sweepResolution.setPreferredSize(new Dimension(50, 20));		

		frmNonSpatialMultiSpecies.getContentPane().add(sweepResolution, "flowx,cell 2 7");

		chckbxSweepRoadkillInstead = new JCheckBox("Sweep Infr. Mortality");
		frmNonSpatialMultiSpecies.getContentPane().add(chckbxSweepRoadkillInstead, "flowx,cell 3 7");

		lblSweepStartAndEnd = new JLabel("Sweep Start and End:");
		frmNonSpatialMultiSpecies.getContentPane().add(lblSweepStartAndEnd, "cell 1 8,alignx right");

		sweepResolutionMin = new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01));
		sweepResolutionMin.setPreferredSize(new Dimension(50, 20));
		frmNonSpatialMultiSpecies.getContentPane().add(sweepResolutionMin, "flowx,cell 2 8");

		chckbxScaleToYear = new JCheckBox("Scale mortality to yearly?");
		frmNonSpatialMultiSpecies.getContentPane().add(chckbxScaleToYear, "flowx,cell 3 8,alignx right");

		lblOutputLocation = new JLabel("Output Location:");
		frmNonSpatialMultiSpecies.getContentPane().add(lblOutputLocation, "cell 0 9");
		SpinnerNumberModel spinnerModelMPI = new SpinnerNumberModel(0,0,1000000,1);  

		lblMaxProcessedInd = new JLabel("Max. Processed Ind. :");
		frmNonSpatialMultiSpecies.getContentPane().add(lblMaxProcessedInd, "cell 2 9,alignx right");
		maxProcInd = new JSpinner(spinnerModelMPI);
		maxProcInd.setPreferredSize(new Dimension(40, 20));
		frmNonSpatialMultiSpecies.getContentPane().add(maxProcInd, "flowx,cell 3 9");
		frmNonSpatialMultiSpecies.getContentPane().add(browseOutputFolder, "flowx,cell 0 10");

		txtOutputLocation = new JTextField();
		txtOutputLocation.setText(location + System.getProperty("file.separator") + "Output Folder"+ System.getProperty("file.separator"));
		txtOutputLocation.setColumns(10);
		frmNonSpatialMultiSpecies.getContentPane().add(txtOutputLocation, "cell 1 10 3 1,growx");

		btnRunModel = new JButton("Run Model");
		btnRunModel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnRunModel.getText().equals("Run Model")) {
					btnRunModel.setText("Stop");

					if (chckbxSweepRoadkillInstead.isSelected() && sweepMortalityCheck.isSelected() ) {
						numberOfRoadVariations.setValue(0);

					}

					try {
						modelParameters = new ParameterPackage(txtInputFileLocation.getText(),(int)numberOfRoadVariations.getValue(), "NonSpatial");
						model = new Model( modelParameters, Double.parseDouble(textFieldInfraDensity.getText()), (int)numberOfRoadVariations.getValue() , (int)numberOfCores.getValue(), (int)maxProcInd.getValue());
					} catch (Exception e1) {
						progressBar.setString(e1.getMessage());
						e1.printStackTrace();
						btnRunModel.setText("Run Model");
					}

					runModel();
				} else {										
					btnRunModel.setText("Stopping...");
					btnRunModel.setEnabled(false);
					rm.cancel(true);
				}
			}
		});	

		lblStartDate = new JLabel("Start Date (YYYY-MM-DD):");
		frmNonSpatialMultiSpecies.getContentPane().add(lblStartDate, "cell 0 11,alignx trailing");

		txtYyyymmdd = new JTextField();
		txtYyyymmdd.setText("YYYY-MM-DD");
		frmNonSpatialMultiSpecies.getContentPane().add(txtYyyymmdd, "flowx,cell 1 11,growx");
		txtYyyymmdd.setColumns(10);

		lblTimeUnit = new JLabel("Time Unit:");
		frmNonSpatialMultiSpecies.getContentPane().add(lblTimeUnit, "cell 2 11,alignx right");

		comboBoxTimeUnit = new JComboBox <String>();
		comboBoxTimeUnit.setModel(new DefaultComboBoxModel<String>(new String[] {"Day", "Week", "Month", "Year"}));
		frmNonSpatialMultiSpecies.getContentPane().add(comboBoxTimeUnit, "flowx,cell 3 11,growx");
		btnRunModel.setIcon(null);
		frmNonSpatialMultiSpecies.getContentPane().add(btnRunModel, "cell 0 12 2 1,grow");

		lblCurrentIteration = new JLabel("Current Iteration:");
		frmNonSpatialMultiSpecies.getContentPane().add(lblCurrentIteration, "cell 2 12,alignx right");

		lblCurrentIterationValue = new JLabel("0");
		frmNonSpatialMultiSpecies.getContentPane().add(lblCurrentIterationValue, "cell 3 12");



		btnGenerateTemplate = new JButton("Generate Template");
		btnGenerateTemplate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GenerateTemplateWindow.isOpen()) {
					GenerateTemplateWindow.launchApplication("NonSpatial");
				}
			}
		});		
		frmNonSpatialMultiSpecies.getContentPane().add(btnGenerateTemplate, "flowx,cell 2 0 2 1");

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(300,25));
		frmNonSpatialMultiSpecies.getContentPane().add(progressBar, "cell 0 13 2 1");

		lblEstimatedTime = new JLabel("");
		frmNonSpatialMultiSpecies.getContentPane().add(lblEstimatedTime, "cell 2 13");

		lblNumberOfCores = new JLabel("N. of Cores:");
		frmNonSpatialMultiSpecies.getContentPane().add(lblNumberOfCores, "flowx,cell 3 13,alignx right");
		numberOfCores = new JSpinner(spinnerModelCore);
		frmNonSpatialMultiSpecies.getContentPane().add(numberOfCores, "cell 3 13,alignx right");

		sweepResolutionMax = new JSpinner(new SpinnerNumberModel(1, 0, 1, 0.01));
		sweepResolutionMax.setPreferredSize(new Dimension(50, 20));
		frmNonSpatialMultiSpecies.getContentPane().add(sweepResolutionMax, "cell 2 8");

		lblInfoTemplate = new JLabel("");
		lblInfoTemplate.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoTemplate.setToolTipText("<html>\r\n<p>Generate a template for entering species life-trait data.</p>\r\n</html>");
		lblInfoTemplate.setHorizontalAlignment(SwingConstants.RIGHT);
		frmNonSpatialMultiSpecies.getContentPane().add(lblInfoTemplate, "cell 2 0 2 1");

		lblInfoXLSX = new JLabel("");
		lblInfoXLSX.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoXLSX.setToolTipText("<html>\r\n<p>Select a .xlsx file that provides the life traits of the species to model.</p>\r\n<p>Use \"Generate Template\" to create a template if needed.</p>\r\n</html>");
		lblInfoXLSX.setHorizontalAlignment(SwingConstants.RIGHT);
		frmNonSpatialMultiSpecies.getContentPane().add(lblInfoXLSX, "cell 0 1");

		lblInfoVariations = new JLabel("");
		lblInfoVariations.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoVariations.setToolTipText("<html>\r\n<p>Select the number of infrastructure mortality scenarios.</p>\r\n<p>A value of 0 provides only the real scenario, while 1 provides both real and a scenario with no additional mortality</p>\r\n<p>Values greater than 1 provide progressively finer mortality variations.</p>\r\n<p>For example, 4, provides 0%,25%,50%,75% and 100% additional mortality.</p>\r\n</html>");
		lblInfoVariations.setHorizontalAlignment(SwingConstants.RIGHT);
		frmNonSpatialMultiSpecies.getContentPane().add(lblInfoVariations, "cell 3 4");

		lblInfoIterations = new JLabel("");
		lblInfoIterations.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoIterations.setToolTipText("<html>\r\n<p>Number of model iterations.</p>\r\n<p>Each iteration represents a duration of time defined by the user according to the species life traits.</p>\r\n<p>Multiple values can be separated by ; to save results for different durations simultaneously.</p>\r\n<p>For example: 50;100;150 stores results for 50, 100, and 150 iterations.\r\n</html>");
		lblInfoIterations.setHorizontalAlignment(SwingConstants.RIGHT);
		frmNonSpatialMultiSpecies.getContentPane().add(lblInfoIterations, "cell 3 5");

		lblInfoRepetitions = new JLabel("");
		lblInfoRepetitions.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoRepetitions.setToolTipText("<html>\r\n<p>Number of model repetitions. </p>\r\n<p>Higher values provide more robust results, given the stochasticity of the model, but increases the running time linearly.</p>\r\n</html>");
		lblInfoRepetitions.setHorizontalAlignment(SwingConstants.RIGHT);
		frmNonSpatialMultiSpecies.getContentPane().add(lblInfoRepetitions, "cell 1 5");

		lblInfoOutput = new JLabel("");
		lblInfoOutput.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoOutput.setToolTipText("<html>\r\n<p>Folder where the results will be saved.</p>\r\n</html>");
		lblInfoOutput.setHorizontalAlignment(SwingConstants.RIGHT);
		frmNonSpatialMultiSpecies.getContentPane().add(lblInfoOutput, "cell 0 10");

		lblInfoDate = new JLabel("");
		lblInfoDate.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoDate.setToolTipText("<html>\r\n<p>Simulation start date. </p>\r\n<p>Used only to label the x-axis of the graphs.</p>\r\n</html>");
		lblInfoDate.setHorizontalAlignment(SwingConstants.RIGHT);
		frmNonSpatialMultiSpecies.getContentPane().add(lblInfoDate, "cell 1 11");

		lblInfoTimeUnit = new JLabel("");
		lblInfoTimeUnit.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoTimeUnit.setToolTipText("<html>\r\n<p>Informs the model of the time unit selected by the user.</p>\r\n<p>Important if using \"Scale mortality to yearly?\" for the program to know the original time unit.</p>\r\n</html>");
		lblInfoTimeUnit.setHorizontalAlignment(SwingConstants.RIGHT);
		frmNonSpatialMultiSpecies.getContentPane().add(lblInfoTimeUnit, "cell 3 11");

		lblInfoCores = new JLabel("");
		lblInfoCores.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoCores.setToolTipText("<html>\r\n<p>Number of CPU cores to use.</p>\r\n<p>Ideally, the program uses one core per scenario per species.</p>\r\n<p>Additional cores beyond this limit are ignored.</p>\r\n</html>");
		lblInfoCores.setHorizontalAlignment(SwingConstants.RIGHT);
		frmNonSpatialMultiSpecies.getContentPane().add(lblInfoCores, "cell 3 13");

		lblInfoMaxInd = new JLabel("");
		lblInfoMaxInd.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoMaxInd.setToolTipText("<html>\r\n<p>Maximum number of individuals explicitly simulated per spatial cell.</p>\r\n<p>Results are subsequently upscaled to represent the full population, if needed. </p>\r\n<p>Set to 0 to allow for an unlimited number of individuals.</p>\r\n<p>Very low values may increase model instability, as stochastic events have proportionally larger effects on the scaled population.</p>\r\n<p>Choose a value that balances computational efficiency and demographic realism.</p>\r\n</html>\r\n</html>");
		lblInfoMaxInd.setHorizontalAlignment(SwingConstants.RIGHT);
		frmNonSpatialMultiSpecies.getContentPane().add(lblInfoMaxInd, "cell 3 9");

		lblInfoSweep = new JLabel("");
		lblInfoSweep.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoSweep.setToolTipText("<html>\r\n<p>Instead of the normal model, performs a sensitivity analysis for the base mortality.</p>\r\n<p>If \"Sweep Infr. Mortality\" is also selected, instead perfoms a senstivity analysis for infrastructure-induced mortality.</p>\r\n</html>");
		lblInfoSweep.setHorizontalAlignment(SwingConstants.RIGHT);
		frmNonSpatialMultiSpecies.getContentPane().add(lblInfoSweep, "cell 0 7");

		lblInfoSweepRes = new JLabel("");
		lblInfoSweepRes.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoSweepRes.setToolTipText("<html>\r\n<p>Number of sampling points between the sweep start and end.</p>\r\n<p>For example, if Sweep Start and End are at 0 and 0.5, and Sweep Resolution is at 3,</p>\r\n<p>the analysis is performed thrice, at 0%, 0.25% and 50% mortality.</p>\r\n</html>");
		lblInfoSweepRes.setHorizontalAlignment(SwingConstants.RIGHT);
		frmNonSpatialMultiSpecies.getContentPane().add(lblInfoSweepRes, "cell 2 7");

		lblInfoScale = new JLabel("");
		lblInfoScale.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoScale.setToolTipText("<html>\r\n<p>Scales the values in the X axis of the graph to be in yearly mortality.</p>\r\n</html>");
		lblInfoScale.setHorizontalAlignment(SwingConstants.RIGHT);
		frmNonSpatialMultiSpecies.getContentPane().add(lblInfoScale, "cell 3 8");

		lblInfoSweep_1 = new JLabel("");
		lblInfoSweep_1.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoSweep_1.setToolTipText("<html>\r\n<p>If selected and \"Sweep mortality?\" is also selected, the program instead perfoms a senstivity analysis for infrastructure-induced mortality.</p>\r\n</html>");
		lblInfoSweep_1.setHorizontalAlignment(SwingConstants.RIGHT);
		frmNonSpatialMultiSpecies.getContentPane().add(lblInfoSweep_1, "cell 3 7");

		lblInfoInfNetwork = new JLabel("");
		lblInfoInfNetwork.setIcon(new ImageIcon(NonSpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoInfNetwork.setToolTipText("<html>\r\n<p>The amount of infrastructure present (e.g., km of road, number of wind turbines).</p>\r\n<p>Should match the same unit as the \"Infrastructure Mortality Rate\" in the species traits.</p>\r\n</html>");
		lblInfoInfNetwork.setHorizontalAlignment(SwingConstants.RIGHT);
		frmNonSpatialMultiSpecies.getContentPane().add(lblInfoInfNetwork, "cell 1 4");

		frmNonSpatialMultiSpecies.pack();
		frmNonSpatialMultiSpecies.setLocationRelativeTo(null);


	}


	/**
	 * Runs the model
	 */

	protected void runModel() {
		try {
			rm = new RunModel();
			rm.addPropertyChangeListener(this);

			rm.execute();
		} catch (OutOfMemoryError e) {
			progressBar.setString("Out of memory error! Increase RAM allocation.");
		}
	}

	/**
	 * Class to run the model in another thread
	 */

	private class RunModel extends SwingWorker<Void, Void> {

		@Override
		public Void doInBackground() {


			//Creates an array with the points where the model should capture results
			String[] itToRunString = iterationsToRun.getText().split(";");
			itToRun = new int[itToRunString.length];
			for (int i = 0; i < itToRunString.length; i++) {
				itToRun[i] = Integer.parseInt(itToRunString[i]);
			}


			//If no sweep is being made, set sweepRes to 1
			int sweepRes = (int) sweepResolution.getValue();
			if (!sweepMortalityCheck.isSelected()) {
				sweepRes = 1;
			}


			//The actual number of repetitions depends on the sweep value
			int repetitions = (int) numberOfRepetitions.getValue()*sweepRes;

			progressBar.setMaximum(repetitions);
			progressBar.setString("Running Model...");
			resultsRepeated = new String[repetitions][];
			resultsExtinctionRepeated = new Map[itToRun.length][repetitions][];		

			long startTime = System.currentTimeMillis();
			long currentTime =System.currentTimeMillis();

			//Runs through the various values of the sweep (or just once if sweepRes is the default 1)
			ModelLoop:
				for (int sweepN = 0 ; sweepN < sweepRes ; sweepN++) {
					if (sweepMortalityCheck.isSelected()) {		//Sweeps base mortality
						double minSweep = (double) sweepResolutionMin.getValue();
						double maxSweep = (double) sweepResolutionMax.getValue();
						double currentMortality =  minSweep +  sweepN*(maxSweep-minSweep)/((double)sweepRes-1);							

						if (!chckbxSweepRoadkillInstead.isSelected() ) { //Sweeps base mortality or roadkill mortality, depending on the selection
							for (int numSpecies = 0; numSpecies < modelParameters.baseBirthMort.length; numSpecies++) {

								//Scales the provided mortality to yearly, based on the provided time unit
								if (!chckbxScaleToYear.isSelected()) {
									modelParameters.baseBirthMort[numSpecies][modelParameters.baseBirthMort[numSpecies].length-1][0] = currentMortality;
									modelParameters.baseBirthMort[numSpecies][modelParameters.baseBirthMort[numSpecies].length-1][1] = currentMortality;

								} else {
									switch((String) comboBoxTimeUnit.getSelectedItem()) {
									case "Day":
										modelParameters.baseBirthMort[numSpecies][modelParameters.baseBirthMort[numSpecies].length-1][0] = (1-Math.pow(1-currentMortality,1.0/365.0)); 
										modelParameters.baseBirthMort[numSpecies][modelParameters.baseBirthMort[numSpecies].length-1][1] = (1-Math.pow(1-currentMortality,1.0/365.0));
										break;
									case "Week":
										modelParameters.baseBirthMort[numSpecies][modelParameters.baseBirthMort[numSpecies].length-1][0] = (1-Math.pow(1-currentMortality,1.0/52.1429)); 
										modelParameters.baseBirthMort[numSpecies][modelParameters.baseBirthMort[numSpecies].length-1][1] = (1-Math.pow(1-currentMortality,1.0/52.1429));
										break;
									case "Month":
										modelParameters.baseBirthMort[numSpecies][modelParameters.baseBirthMort[numSpecies].length-1][0] = (1-Math.pow(1-currentMortality,1.0/12.0)); 
										modelParameters.baseBirthMort[numSpecies][modelParameters.baseBirthMort[numSpecies].length-1][1] = (1-Math.pow(1-currentMortality,1.0/12.0));
										break;
									case "Year":
										modelParameters.baseBirthMort[numSpecies][modelParameters.baseBirthMort[numSpecies].length-1][0] = currentMortality;
										modelParameters.baseBirthMort[numSpecies][modelParameters.baseBirthMort[numSpecies].length-1][1] = currentMortality;
										break;
									}
								}
							}
						} else {//Sweeps roadkill mortality
							double[][] roadkillPercentage = new double[modelParameters.baseBirthMort.length][2];

							for (int numSpecies = 0; numSpecies < modelParameters.baseBirthMort.length; numSpecies++) {
								//Scales the provided roadkill mortality to yearly, based on the provided time unit
								if (!chckbxScaleToYear.isSelected()) {
									roadkillPercentage[numSpecies][0] = currentMortality;
									roadkillPercentage[numSpecies][1] = currentMortality;
								} else {
									switch((String) comboBoxTimeUnit.getSelectedItem()) {
									case "Day":
										roadkillPercentage[numSpecies][0] = (1-Math.pow(1-currentMortality,1.0/365.0)); 
										roadkillPercentage[numSpecies][1] = (1-Math.pow(1-currentMortality,1.0/365.0));
										break;
									case "Week":
										roadkillPercentage[numSpecies][0] = (1-Math.pow(1-currentMortality,1.0/52.1429)); 
										roadkillPercentage[numSpecies][1] = (1-Math.pow(1-currentMortality,1.0/52.1429));
										break;
									case "Month":
										roadkillPercentage[numSpecies][0] = (1-Math.pow(1-currentMortality,1.0/12.0)); 
										roadkillPercentage[numSpecies][1] = (1-Math.pow(1-currentMortality,1.0/12.0));
										break;
									case "Year":
										roadkillPercentage[numSpecies][0] = currentMortality;
										roadkillPercentage[numSpecies][1] = currentMortality;
										break;
									}
								}
							}

							modelParameters.setUpRoadkillSweep(roadkillPercentage);

						}
					}
					for (int i = 0; i < (int)numberOfRepetitions.getValue();i++) {
						if (isCancelled()) { //Checks if the thread as has been stopped by the stop button.
							break ModelLoop;
						}
						//Avoid memory leak
						System.gc();
						progressBar.setValue(i+sweepN*(int)numberOfRepetitions.getValue());

						try { //Sets up the model with the chosen parameters, if able
							model = new Model( modelParameters, Double.parseDouble(textFieldInfraDensity.getText()),(int) numberOfRoadVariations.getValue() ,(int) numberOfCores.getValue(), (int) maxProcInd.getValue());
						} catch (Exception e) {
							e.printStackTrace();
							progressBar.setString(e.getMessage());
							return null;
						}

						int cutNumber = 0;
						while (cutNumber < itToRun.length) { //Runs the model, stopping at each needed section to save 
							int modifier = 0;
							if (cutNumber != 0) {
								modifier = itToRun[cutNumber-1];
							}
							model.run(itToRun[cutNumber]-modifier);
							resultsExtinctionRepeated[cutNumber][i+sweepN*((int)numberOfRepetitions.getValue())] = model.getExtinction(); //Saves the map
							cutNumber ++;
						}


						if (sweepMortalityCheck.isSelected()) {
							lblCurrentIterationValue.setText((i+1) + " - " + (sweepN+1));
						} else {
							lblCurrentIterationValue.setText((i+1)+"");
						}

						resultsRepeated[i+sweepN*((int)numberOfRepetitions.getValue())] = model.getTextRowToSaveToCSV();

						//Estimates time left
						currentTime = System.currentTimeMillis();
						long elapsedTime = currentTime-startTime;
						long expectedTime =elapsedTime/((i+1)+((sweepN)*(int)numberOfRepetitions.getValue()))*(int)numberOfRepetitions.getValue()*sweepRes;
						double seconds = (expectedTime-elapsedTime)/1000.0;
						if (seconds > 60) {
							double minutes = seconds/60.0;			
							if (minutes > 60) {
								double hours = minutes/60.0;
								lblEstimatedTime.setText("Time Left: " +(int)Math.ceil(hours) + " h");

							} else {
								lblEstimatedTime.setText("Time Left: " +(int)Math.ceil(minutes) + " m");
							}
						} else {
							lblEstimatedTime.setText("Time Left: " +(int)Math.ceil(seconds) + " s");
						}
					}
				}

			progressBar.setValue(0);

			return null;
		}


		@Override
		public void done() {

			if (isCancelled()) {
				btnRunModel.setText("Run Model");
				btnRunModel.setEnabled(true);
				progressBar.setString("Model Cancelled");
			} else {
				//Processes the results and generates the maps and graphs
				OutputProcessor.process(resultsRepeated,resultsExtinctionRepeated,model.getRoadMortality() ,txtOutputLocation.getText(), model.getSpeciesNames(), "NonSpatial", 0.5 ,(int)numberOfRoadVariations.getValue(),
						modelParameters,txtYyyymmdd.getText(), (String) comboBoxTimeUnit.getSelectedItem(), sweepMortalityCheck.isSelected()?((int)sweepResolution.getValue()):1, (double) sweepResolutionMin.getValue(), (double) sweepResolutionMax.getValue(),itToRun , (int)numberOfRepetitions.getValue(), generateCommand());

				generateCommand();

				progressBar.setString("Done!");
				btnRunModel.setText("Run Model");
				btnRunModel.setEnabled(true);
				try {
					get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					if (e.getCause() instanceof OutOfMemoryError) {
						if (Integer.parseInt(System.getProperty("sun.arch.data.model")) == 64) {
							progressBar.setString("Out Of Memory! Needs more allocated RAM");
						} else {
							progressBar.setString("Out Of Memory! Consider installing the 64bit version of java");
						}
					}
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Invoked when task's progress property changes.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}

	/**
	 * For opening the window for editing parameters
	 */
	/**
	private class ParameterActionListener implements ActionListener {
		private NonSpatialMultiSpeciesWindow window;

		public ParameterActionListener(NonSpatialMultiSpeciesWindow window) {
			this.window = window;
		}

		public void actionPerformed(ActionEvent e) {
			NonSpatialMultiSpeciesParametersWindow.launchApplication(window);
		}
	}
	 */

	/**
	 * Returns the parameter package associated with the model
	 * @return ParameterPackage
	 */
	public ParameterPackage getParameterPackage() {
		return modelParameters;
	}

	/**
	 * Returns the currently selected species in the combo box
	 * @return String
	 */
	public String getCurrentlySelectedSpecies() {
		return (String) comboBoxSpeciesList.getSelectedItem();
	}

	/**
	 * Generates the command to run the model
	 */

	private String generateCommand() {

		String command = "java -jar mortalityRISK.jar " +
				"Parameters.xlsx" + " " +
				textFieldInfraDensity.getText() + " " +			
				iterationsToRun.getText() + " " +
				numberOfRepetitions.getValue() + " " +
				numberOfRoadVariations.getValue() +	 " " +
				"\""+txtOutputLocation.getText().replace("\\", "/") +	 "\" " +	
				numberOfCores.getValue()+	 " " +	
				txtYyyymmdd.getText()+	 " " +	
				comboBoxTimeUnit.getSelectedItem()+	 " " +	
				maxProcInd.getValue();
		if (sweepMortalityCheck.isSelected()) {
			command += " " + sweepResolutionMin.getValue() +	 " " +	
					sweepResolutionMax.getValue() +	 " " +	
					Boolean.toString(chckbxSweepRoadkillInstead.isSelected())+	 " " +	
					sweepResolution.getValue() +	 " " +	
					Boolean.toString(chckbxScaleToYear.isSelected());
		}

		return command;
	}

}
