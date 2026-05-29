package gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
 * Class that provides the GUI for the Spatial Model
 */

public class SpatialMultiSpeciesWindow implements PropertyChangeListener {

	private JFrame frmSpatialMultiSpecies;
	private JTextField txtInputFileLocation;
	private JTextField txtRoadFileLocation;

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
	private JButton browseRoadFile;
	private JButton browseInputFile;
	private JTextField txtOutputLocation;
	private JButton btnGenerateTemplate;
	private JButton btnRunModel;
	private JSpinner minPersistenceThreshold;
	private JLabel lblminPersistenceThreshold;
	private JLabel lblNumberOfCores;
	private JSpinner numberOfCores;
	private RunModel rm;

	private Model model;
	private JLabel lblCurrentIteration;
	private JLabel lblCurrentIterationValue;

	private String[][] resultsRepeated;
	private Map[][][] resultsExtinctionRepeated;
	private ParameterPackage modelParameters;
	private JTextField txtInitialPopulationLocation;
	private JButton browseInitialPopulationFolder;
	private JLabel lblInitialPopulationFolder;
	//	private JButton btnEditParameters;
	private JButton btnLoadParametersFile;
	private JComboBox<String> comboBoxSpeciesList;
	//	private JButton btnSaveParametersToFile;
	private JLabel lblTimeUnit;
	private JComboBox<String> comboBoxTimeUnit;
	private JLabel lblStartDate;
	private JTextField txtYyyymmdd;
	private JLabel lblEstimatedTime;
	private JLabel lblMaxProcessedInd;
	private JSpinner maxProcInd;
	private int[] itToRun;
	private JLabel lblInfoXLSX;
	private JLabel lblInfoSpeciesMap;
	private JLabel lblInfoRoadMap;
	private JLabel lblInfoCutOff;
	private JLabel lblInfoRepetitions;
	private JLabel lblInfoVariations;
	private JLabel lblInfoIterations;
	private JLabel lblInfoMaxInd;
	private JLabel lblInfoDate;
	private JLabel lblInfoTimeUnit;
	private JLabel lblInfoCores;
	private JLabel lblInfoTemplate;
	private JLabel lblInfoOutput;
	private JButton btnParameterHelp;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		launchApplication();
	}

	public static void launchApplication() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					SpatialMultiSpeciesWindow window = new SpatialMultiSpeciesWindow();
					window.frmSpatialMultiSpecies.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SpatialMultiSpeciesWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		model = null;
		location = System.getProperty("user.dir");

		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

		frmSpatialMultiSpecies = new JFrame();
		frmSpatialMultiSpecies.setIconImage(Toolkit.getDefaultToolkit().getImage(SpatialMultiSpeciesWindow.class.getResource("/resources/Risky 16x16.png")));
		frmSpatialMultiSpecies.setTitle("mortalityRISK - Spatial Multi-Species Model");
		frmSpatialMultiSpecies.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSpatialMultiSpecies.getContentPane().setLayout(new MigLayout("", "[75.00px][63.00px,grow][150.00px][103px,grow]", "[30px][30px][30px][30px][30px][30px][30px][][30px][30px][30px,grow][][30px][][30px][30px]"));

		lblInputFile = new JLabel("Species/Population Life Traits:");
		frmSpatialMultiSpecies.getContentPane().add(lblInputFile, "flowx,cell 0 0 2 1");

		SpinnerNumberModel spinnerModelCore = new SpinnerNumberModel((int)Math.round(((double)Runtime.getRuntime().availableProcessors())/2+((double)Runtime.getRuntime().availableProcessors())/4), 1, Runtime.getRuntime().availableProcessors(), 1);

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
		frmSpatialMultiSpecies.getContentPane().add(browseInputFile, "flowx,cell 0 1");

		txtInputFileLocation = new JTextField();
		txtInputFileLocation.setText(location + System.getProperty("file.separator") + "InputFile.xlsx");
		frmSpatialMultiSpecies.getContentPane().add(txtInputFileLocation, "cell 1 1 3 1,growx");
		txtInputFileLocation.setColumns(10);


		btnLoadParametersFile = new JButton("Validate");
		btnLoadParametersFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				try {
					modelParameters = new ParameterPackage(txtInputFileLocation.getText(),(int)numberOfRoadVariations.getValue(),"Spatial");
					comboBoxSpeciesList.setModel(new DefaultComboBoxModel<>(modelParameters.getOriginalSpeciesNames(false)));

				} catch (Exception e1) {
					progressBar.setString(e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		frmSpatialMultiSpecies.getContentPane().add(btnLoadParametersFile, "cell 1 2,alignx center");

		/* To be added
		btnEditParameters = new JButton("Edit");		
		btnEditParameters.addActionListener(new ParameterActionListener(this));		
		frmSpatialMultiSpecies.getContentPane().add(btnEditParameters, "cell 2 2,alignx center");

		btnSaveParametersToFile = new JButton("Save");
		btnSaveParametersToFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String	location = "";
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
						modelParameters.saveToFile(file.getAbsolutePath(),"Spatial");

					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}


			}
		});
		frmSpatialMultiSpecies.getContentPane().add(btnSaveParametersToFile, "cell 3 2"); */

		comboBoxSpeciesList = new JComboBox<String>();
		frmSpatialMultiSpecies.getContentPane().add(comboBoxSpeciesList, "cell 2 2,growx");

		btnParameterHelp = new JButton("Parameters");
		btnParameterHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!ModelParametersTable.isOpen()) {
					ModelParametersTable.launchApplication("Spatial");
				}
			}
		});		
		btnParameterHelp.setIcon(new ImageIcon(SpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		frmSpatialMultiSpecies.getContentPane().add(btnParameterHelp, "cell 3 2");

		lblInitialPopulationFolder = new JLabel("Species/Population Distribution Map:");
		frmSpatialMultiSpecies.getContentPane().add(lblInitialPopulationFolder, "cell 0 3 2 1");

		browseInitialPopulationFolder = new JButton("Browse...");
		browseInitialPopulationFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setCurrentDirectory(new File(location + System.getProperty("file.separator")));
				fileChooser.showOpenDialog(new JFrame());
				if (fileChooser.getSelectedFile() != null) {
					txtInitialPopulationLocation.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});	
		frmSpatialMultiSpecies.getContentPane().add(browseInitialPopulationFolder, "flowx,cell 0 4");

		txtInitialPopulationLocation = new JTextField();
		txtInitialPopulationLocation.setText(location + System.getProperty("file.separator") + "Initial Population" + System.getProperty("file.separator"));
		txtInitialPopulationLocation.setColumns(10);
		frmSpatialMultiSpecies.getContentPane().add(txtInitialPopulationLocation, "cell 1 4 3 1,growx");

		lblRoadMapLocation = new JLabel("Infrastructure Network:");
		frmSpatialMultiSpecies.getContentPane().add(lblRoadMapLocation, "cell 0 5");

		browseRoadFile = new JButton("Browse...");		
		browseRoadFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				javax.swing.filechooser.FileFilter ascFilter = new javax.swing.filechooser.FileNameExtensionFilter(".Asc Files","asc");
				fileChooser.setFileFilter(ascFilter);
				fileChooser.setCurrentDirectory(new File(location + System.getProperty("file.separator")));
				fileChooser.showOpenDialog(new JFrame());
				if (fileChooser.getSelectedFile() != null) {
					txtRoadFileLocation.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});		
		frmSpatialMultiSpecies.getContentPane().add(browseRoadFile, "flowx,cell 0 6");

		txtRoadFileLocation = new JTextField();
		txtRoadFileLocation.setText(location + System.getProperty("file.separator") + "RoadMap.asc");
		frmSpatialMultiSpecies.getContentPane().add(txtRoadFileLocation, "cell 1 6 3 1,growx");
		txtRoadFileLocation.setColumns(10);


		SpinnerNumberModel spinnerModel01 = new SpinnerNumberModel(0.1,0.0,1.0,0.05);  
		lblminPersistenceThreshold = new JLabel("Survival Cut-Off:");
		frmSpatialMultiSpecies.getContentPane().add(lblminPersistenceThreshold, "cell 0 8,alignx right");
		minPersistenceThreshold = new JSpinner(spinnerModel01);
		minPersistenceThreshold.setPreferredSize(new Dimension(50,20));
		frmSpatialMultiSpecies.getContentPane().add(minPersistenceThreshold, "flowx,cell 1 8");

		lblNumberOfRoadVariations = new JLabel("Extra Scenarios:");
		frmSpatialMultiSpecies.getContentPane().add(lblNumberOfRoadVariations, "cell 2 8,alignx right");
		numberOfRoadVariations = new JSpinner(new SpinnerNumberModel(1, 0, 99, 1));
		numberOfRoadVariations.setPreferredSize(new Dimension(50,20));
		frmSpatialMultiSpecies.getContentPane().add(numberOfRoadVariations, "flowx,cell 3 8");


		lblNumberOfRepetions = new JLabel("Number of repetitions:");
		frmSpatialMultiSpecies.getContentPane().add(lblNumberOfRepetions, "cell 0 9,alignx right");
		numberOfRepetitions = new JSpinner(new SpinnerNumberModel(100, 0, 10000, 1));
		numberOfRepetitions.setPreferredSize(new Dimension(50,20));
		frmSpatialMultiSpecies.getContentPane().add(numberOfRepetitions, "flowx,cell 1 9");

		lblNumberOfIterations = new JLabel("Number of iterations:");
		frmSpatialMultiSpecies.getContentPane().add(lblNumberOfIterations, "cell 2 9,alignx right");
		iterationsToRun = new JTextField();
		iterationsToRun.setText("50;100");
		iterationsToRun.setPreferredSize(new Dimension(100, 20));
		frmSpatialMultiSpecies.getContentPane().add(iterationsToRun, "flowx,cell 3 9,aligny center");

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

		lblMaxProcessedInd = new JLabel("Max. Processed Ind. :");
		frmSpatialMultiSpecies.getContentPane().add(lblMaxProcessedInd, "cell 2 10,alignx right");

		SpinnerNumberModel spinnerModelMPI = new SpinnerNumberModel(0,0,1000000,1);  
		maxProcInd = new JSpinner(spinnerModelMPI);
		maxProcInd.setPreferredSize(new Dimension(40, 20));
		frmSpatialMultiSpecies.getContentPane().add(maxProcInd, "flowx,cell 3 10");

		lblOutputLocation = new JLabel("Output Location:");
		frmSpatialMultiSpecies.getContentPane().add(lblOutputLocation, "cell 0 11");
		frmSpatialMultiSpecies.getContentPane().add(browseOutputFolder, "flowx,cell 0 12");

		txtOutputLocation = new JTextField();
		txtOutputLocation.setText(location + System.getProperty("file.separator") + "Output Folder"+ System.getProperty("file.separator"));
		txtOutputLocation.setColumns(10);
		frmSpatialMultiSpecies.getContentPane().add(txtOutputLocation, "cell 1 12 3 1,growx");

		btnRunModel = new JButton("Run Model");
		btnRunModel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnRunModel.getText().equals("Run Model")) {
					btnRunModel.setText("Stop");

					try {
						modelParameters = new ParameterPackage(txtInputFileLocation.getText(),(int)numberOfRoadVariations.getValue(),"Spatial");
						runModel();
					} catch (Exception e1) {
						progressBar.setString("A file is missing!");
						e1.getMessage();
						e1.printStackTrace();
						btnRunModel.setText("Run Model");
					}
				} else {
					btnRunModel.setText("Stopping...");
					btnRunModel.setEnabled(false);
					rm.cancel(true);
				}
			}
		});	

		lblStartDate = new JLabel("Start Date (YYYY-MM-DD):");
		frmSpatialMultiSpecies.getContentPane().add(lblStartDate, "cell 0 13,alignx trailing");

		txtYyyymmdd = new JTextField();
		txtYyyymmdd.setText("YYYY-MM-DD");
		frmSpatialMultiSpecies.getContentPane().add(txtYyyymmdd, "flowx,cell 1 13,growx");
		txtYyyymmdd.setColumns(10);

		lblTimeUnit = new JLabel("Time Unit:");
		frmSpatialMultiSpecies.getContentPane().add(lblTimeUnit, "cell 2 13,alignx trailing");

		comboBoxTimeUnit = new JComboBox<String>();
		comboBoxTimeUnit.setModel(new DefaultComboBoxModel<String>(new String[] {"Day", "Week", "Month", "Year"}));
		frmSpatialMultiSpecies.getContentPane().add(comboBoxTimeUnit, "flowx,cell 3 13,growx");
		btnRunModel.setIcon(null);
		frmSpatialMultiSpecies.getContentPane().add(btnRunModel, "cell 0 14 2 1,grow");

		lblCurrentIteration = new JLabel("Current Iteration:");
		frmSpatialMultiSpecies.getContentPane().add(lblCurrentIteration, "cell 2 14,alignx right");

		lblCurrentIterationValue = new JLabel("0");
		frmSpatialMultiSpecies.getContentPane().add(lblCurrentIterationValue, "cell 3 14");

		btnGenerateTemplate = new JButton("Generate Template");
		btnGenerateTemplate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GenerateTemplateWindow.isOpen()) {
					GenerateTemplateWindow.launchApplication("Spatial");
				}
			}
		});		
		frmSpatialMultiSpecies.getContentPane().add(btnGenerateTemplate, "flowx,cell 2 0 2 1");

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(300,25));
		frmSpatialMultiSpecies.getContentPane().add(progressBar, "flowx,cell 0 15 2 1");


		lblEstimatedTime = new JLabel("");
		frmSpatialMultiSpecies.getContentPane().add(lblEstimatedTime, "cell 2 15");

		lblNumberOfCores = new JLabel("N. of Cores:");
		frmSpatialMultiSpecies.getContentPane().add(lblNumberOfCores, "flowx,cell 3 15,alignx right");
		numberOfCores = new JSpinner(spinnerModelCore);
		frmSpatialMultiSpecies.getContentPane().add(numberOfCores, "cell 3 15");

		lblInfoXLSX = new JLabel("");
		lblInfoXLSX.setHorizontalAlignment(SwingConstants.RIGHT);
		lblInfoXLSX.setToolTipText("<html>\r\n<p>Select a .xlsx file that provides the life traits of the species to model.</p>\r\n<p>Use \"Generate Template\" to create a template if needed.</p>\r\n</html>");
		lblInfoXLSX.setIcon(new ImageIcon(SpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		frmSpatialMultiSpecies.getContentPane().add(lblInfoXLSX, "cell 0 1,alignx right");

		lblInfoSpeciesMap = new JLabel("");
		lblInfoSpeciesMap.setIcon(new ImageIcon(SpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoSpeciesMap.setToolTipText("<html>\r\n<p>Select a folder containing one .asc raster file per species,</p>\r\n<p>with each file named exactly as the corresponding species in the .xlsx file.</p>\r\n<p>Example: \"Canis lupus.asc\"</p>\r\n</html>");
		lblInfoSpeciesMap.setHorizontalAlignment(SwingConstants.RIGHT);
		frmSpatialMultiSpecies.getContentPane().add(lblInfoSpeciesMap, "cell 0 4");

		lblInfoRoadMap = new JLabel("");
		lblInfoRoadMap.setIcon(new ImageIcon(SpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoRoadMap.setToolTipText("<html>\r\n<p>Select an .asc raster file where each cell provides the infrastructure density per cell.</p>\r\n<p>Example: total length of road, number of wind turbines.</p>\r\n<p>Should match the same unit as \"Infrastructure-Induced Mortality\" in the species traits.</p>\r\n<p>Ideally, use the same resolution as the species' maps.</p>\r\n</html>");
		lblInfoRoadMap.setHorizontalAlignment(SwingConstants.RIGHT);
		frmSpatialMultiSpecies.getContentPane().add(lblInfoRoadMap, "cell 0 6");

		lblInfoCutOff = new JLabel("");
		lblInfoCutOff.setIcon(new ImageIcon(SpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoCutOff.setToolTipText("<html>\r\n<p>Threshold (0\u20131) for considering a cell locally extinct.</p>\r\n<p>A cell is classified as a local extinction if the species survives in less than this proportion of repetitions.</p>\r\n<p>Used to calculate area loss and extinction maps per cell.</p>\r\n<p>Example: 0.1 - local extinction if the species survives in fewer than 10% of repetitions (meaning it went extinct 90% of the time)</p>\r\n</html>");
		lblInfoCutOff.setHorizontalAlignment(SwingConstants.RIGHT);
		frmSpatialMultiSpecies.getContentPane().add(lblInfoCutOff, "cell 1 8");

		lblInfoRepetitions = new JLabel("");
		lblInfoRepetitions.setIcon(new ImageIcon(SpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoRepetitions.setToolTipText("<html>\r\n<p>Number of model repetitions. </p>\r\n<p>Higher values provide more robust results, given the stochasticity of the model, but increases the running time linearly.</p>\r\n</html>");
		lblInfoRepetitions.setHorizontalAlignment(SwingConstants.RIGHT);
		frmSpatialMultiSpecies.getContentPane().add(lblInfoRepetitions, "cell 1 9");

		lblInfoVariations = new JLabel("");
		lblInfoVariations.setIcon(new ImageIcon(SpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoVariations.setToolTipText("<html>\r\n<p>Select the number of infrastructure mortality scenarios.</p>\r\n<p>A value of 0 provides only the real scenario, while 1 provides both real and a scenario with no additional mortality</p>\r\n<p>Values greater than 1 provide progressively finer mortality variations.</p>\r\n<p>For example, 4, provides 0%,25%,50%,75% and 100% additional mortality.</p>\r\n</html>");
		lblInfoVariations.setHorizontalAlignment(SwingConstants.RIGHT);
		frmSpatialMultiSpecies.getContentPane().add(lblInfoVariations, "cell 3 8");

		lblInfoIterations = new JLabel("");
		lblInfoIterations.setIcon(new ImageIcon(SpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoIterations.setToolTipText("<html>\r\n<p>Number of model iterations.</p>\r\n<p>Each iteration represents a duration of time defined by the user according to the species life traits.</p>\r\n<p>Multiple values can be separated by ; to save results for different durations simultaneously.</p>\r\n<p>For example: 50;100;150 stores results for 50, 100, and 150 iterations.\r\n</html>");
		lblInfoIterations.setHorizontalAlignment(SwingConstants.RIGHT);
		frmSpatialMultiSpecies.getContentPane().add(lblInfoIterations, "cell 3 9");

		lblInfoMaxInd = new JLabel("");
		lblInfoMaxInd.setIcon(new ImageIcon(SpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoMaxInd.setToolTipText("<html>\r\n<p>Maximum number of individuals explicitly simulated per spatial cell.</p>\r\n<p>Results are subsequently upscaled to represent the full population, if needed. </p>\r\n<p>Set to 0 to allow for an unlimited number of individuals.</p>\r\n<p>Very low values may increase model instability, as stochastic events have proportionally larger effects on the scaled population.</p>\r\n<p>Choose a value that balances computational efficiency and demographic realism.</p>\r\n</html>");
		lblInfoMaxInd.setHorizontalAlignment(SwingConstants.RIGHT);
		frmSpatialMultiSpecies.getContentPane().add(lblInfoMaxInd, "cell 3 10");

		lblInfoDate = new JLabel("");
		lblInfoDate.setIcon(new ImageIcon(SpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoDate.setToolTipText("<html>\r\n<p>Simulation start date. </p>\r\n<p>Used only to label the x-axis of the graphs.</p>\r\n</html>");
		lblInfoDate.setHorizontalAlignment(SwingConstants.RIGHT);
		frmSpatialMultiSpecies.getContentPane().add(lblInfoDate, "cell 1 13");

		lblInfoTimeUnit = new JLabel("");
		lblInfoTimeUnit.setIcon(new ImageIcon(SpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoTimeUnit.setToolTipText("<html>\r\n<p>Informs the model of the time unit selected by the user.</p>\r\n<p>Only relevant for the label of the x axis of the graphs.</p>\r\n</html>");
		lblInfoTimeUnit.setHorizontalAlignment(SwingConstants.RIGHT);
		frmSpatialMultiSpecies.getContentPane().add(lblInfoTimeUnit, "cell 3 13");

		lblInfoCores = new JLabel("");
		lblInfoCores.setIcon(new ImageIcon(SpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoCores.setToolTipText("<html>\r\n<p>Number of CPU cores to use.</p>\r\n<p>Ideally, the program uses one core per scenario per species.</p>\r\n<p>Additional cores beyond this limit are ignored.</p>\r\n</html>");
		lblInfoCores.setHorizontalAlignment(SwingConstants.RIGHT);
		frmSpatialMultiSpecies.getContentPane().add(lblInfoCores, "cell 3 15");

		lblInfoTemplate = new JLabel("");
		lblInfoTemplate.setIcon(new ImageIcon(SpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoTemplate.setToolTipText("<html>\r\n<p>Generate a template for entering species life-trait data.</p>\r\n</html>");
		lblInfoTemplate.setHorizontalAlignment(SwingConstants.RIGHT);
		frmSpatialMultiSpecies.getContentPane().add(lblInfoTemplate, "cell 2 0 2 1");

		lblInfoOutput = new JLabel("");
		lblInfoOutput.setIcon(new ImageIcon(SpatialMultiSpeciesWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoOutput.setToolTipText("<html>\r\n<p>Folder where the results will be saved.</p>\r\n</html>");
		lblInfoOutput.setHorizontalAlignment(SwingConstants.RIGHT);
		frmSpatialMultiSpecies.getContentPane().add(lblInfoOutput, "cell 0 12");

		frmSpatialMultiSpecies.pack();
		frmSpatialMultiSpecies.setLocationRelativeTo(null);
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

			int sweepRes = 1;			

			int repetitions = (int) numberOfRepetitions.getValue()*sweepRes;

			progressBar.setMaximum(repetitions);
			progressBar.setString("Running Model...");
			resultsRepeated = new String[repetitions][];
			resultsExtinctionRepeated = new Map[itToRun.length][repetitions][];		

			long startTime = System.currentTimeMillis();

			ModelLoop:
				for (int sweepN = 0 ; sweepN < sweepRes ; sweepN++) {
					for (int i = 0; i < (int)numberOfRepetitions.getValue();i++) {
						if (isCancelled()) {
							break ModelLoop;
						}

						//Avoid memory leak
						System.gc();
						progressBar.setValue(i+sweepN*(int)numberOfRepetitions.getValue());
						try {
							Map roadMap = new Map(new File(txtRoadFileLocation.getText()));
							Map[] initialPopulation = initialPopulationLoader(txtInitialPopulationLocation.getText());
							//check if fake map
							if (initialPopulation[0].getCellXSize()== 0) {
								throw new Exception("Missing " + initialPopulation[0].getName() + "'s .asc file.");
							}
							//Sets up the model with the chosen parameters
							model = new Model( modelParameters, initialPopulation,roadMap, (int)numberOfRoadVariations.getValue() ,(int)numberOfCores.getValue(),(int)maxProcInd.getValue());

						} catch (NumberFormatException e) {
							btnRunModel.setText("Run Model");
							progressBar.setString(e.getMessage());
							btnRunModel.setEnabled(true);
							e.printStackTrace();
							break ModelLoop;

						} catch (Exception e) {
							btnRunModel.setText("Run Model");
							progressBar.setString(e.getMessage());
							btnRunModel.setEnabled(true);
							e.printStackTrace();
							break ModelLoop;
						}

						//Runs in sections and stores the values
						int cutNumber = 0;
						while (cutNumber < itToRun.length) {
							int modifier = 0;
							if (cutNumber != 0) {
								modifier = itToRun[cutNumber-1];
							}
							model.run(itToRun[cutNumber]-modifier);
							resultsExtinctionRepeated[cutNumber][i+sweepN*((int)numberOfRepetitions.getValue())] = model.getExtinction();

							cutNumber ++;
						}

						lblCurrentIterationValue.setText((i+1)+"");

						resultsRepeated[i+sweepN*((int)numberOfRepetitions.getValue())] = model.getTextRowToSaveToCSV();

						//Estimates time left
						predictTime( startTime,   i,  sweepN, sweepRes);


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
				OutputProcessor.process(resultsRepeated,resultsExtinctionRepeated,model.getRoadMortality() ,txtOutputLocation.getText(), model.getSpeciesNames(), "Spatial",(double) minPersistenceThreshold.getValue(),(int)numberOfRoadVariations.getValue(),
						modelParameters,txtYyyymmdd.getText(), (String) comboBoxTimeUnit.getSelectedItem(), 1, 0, 0, itToRun , (int)numberOfRepetitions.getValue(), generateCommand());


				
				btnRunModel.setText("Run Model");
				btnRunModel.setEnabled(true);
				if ( !progressBar.getString().contentEquals("Error, debug for more details!") && !progressBar.getString().contentEquals("Missing part of road.")) {
					progressBar.setString("Done!");
				}

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
					} else {
						progressBar.setString(e.getMessage());
					}
					e.printStackTrace();
				}
			}
		}
	}


	/**
	 * Predicts remaining time for model to finish
	 * @param startTime
	 * @param currentTime
	 * @param i
	 * @param sweepN
	 * @param sweepRes
	 */
	public void predictTime(long startTime, int i, int sweepN, int sweepRes) {
		long currentTime = System.currentTimeMillis();
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
	 * Loads all species ascs from folder
	 * @param folder
	 * @return
	 */
	private Map[] initialPopulationLoader(String folder) {
		String[] speciesNames = modelParameters.getOriginalSpeciesNames(true);

		Map[] initialPopulation = new Map[speciesNames.length];
		ArrayList<File> filesInFolder = listFilesInFolder(new File(folder));
		Boolean speciesExists;
		for (int species = 0 ; species < speciesNames.length ; species ++) {
			speciesExists = false;
			for (int i = 0; i < filesInFolder.size(); i++) {
				if(filesInFolder.get(i).getName().equals(speciesNames[species]+".asc")) {
					try {
						initialPopulation[species] = new Map(new File(folder + System.getProperty("file.separator")+speciesNames[species]+".asc"));
						speciesExists = true;
						break;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if (!speciesExists) {
				Map[] fakeMap = {new Map(speciesNames[species],0,0)};
				return fakeMap;
			}
		}
		return initialPopulation;

	}


	/**
	 *  Receives a folder and return an ArrayList of the files present there in alphabetical order.
	 *
	 * @param folder File The folder to check for files
	 * @return ArrayList<File> An ArrayList with the files in the folder
	 */

	private  ArrayList<File> listFilesInFolder(File folder) {
		ArrayList<File> filesInFolder = new ArrayList<>();
		File[] orderedFiles = folder.listFiles();
		Arrays.sort(orderedFiles);
		for (File fileEntry : orderedFiles) {
			if (!fileEntry.isDirectory()) {
				filesInFolder.add(fileEntry);
			}
		}
		return filesInFolder;
	}


	/**
	 * For opening the window for editing parameters
	 */

	/* Currently deactivated
	private class ParameterActionListener implements ActionListener {
		private SpatialMultiSpeciesWindow window;

		public ParameterActionListener(SpatialMultiSpeciesWindow window) {
			this.window = window;
		}

		public void actionPerformed(ActionEvent e) {
			SpatialMultiSpeciesParametersWindow.launchApplication(window);
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
				"\""+ txtInitialPopulationLocation.getText().replace("\\", "/") + "\" " + 
				"\""+ txtRoadFileLocation.getText().replace("\\", "/") + "\" " +			
				iterationsToRun.getText() + " " +
				numberOfRepetitions.getValue() + " " +
				numberOfRoadVariations.getValue() +	 " " +
				minPersistenceThreshold.getValue() + " " +
				"\""+ txtOutputLocation.getText().replace("\\", "/") +	 "\" " +	
				numberOfCores.getValue()+	 " " +	
				txtYyyymmdd.getText()+	 " " +	
				comboBoxTimeUnit.getSelectedItem()+	 " " +	
				maxProcInd.getValue();

		return command;
	}
}