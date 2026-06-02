package gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import model.Map;
import model.Model;
import model.OutputProcessor;
import model.ParameterPackage;

/**
 * Class to run the spatial model through command line
 */
public class NoGUI {

	private static String txtInputFileLocation;
	private static String txtInitialPopulationLocation;
	private static String txtRoadFileLocation;
	private static double infrastructureDensity;
	private static String iterationsToRun;
	private static int numberOfRepetitions;
	private static int numberOfRoadVariations;
	private static double minPersistenceThreshold;
	private static String txtOutputLocation;
	private static int numberOfCores;
	private static String txtYyyymmdd;
	private static String TimeUnit;
	private static int maxProcInd;
	private static double sweepResolutionMin;
	private static double sweepResolutionMax;
	private static boolean sweepRoadkillInstead;
	private static int sweepResolution;
	private static boolean scaleToYear;


	private static Model model;
	private static String[][] resultsRepeated;
	private static Map[][][] resultsExtinctionRepeated;
	private static ParameterPackage modelParameters;
	private static int[] itToRun;
	private static boolean sweepMortalityCheck;


	/**
	 * Launch the application through the command line
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * 
	 * The order of the arguments is: 
	 * InputFile - String path for XLSX
	 * Species Folder - String path for folder (spatial only)
	 * Infrastructure File - String path for infrastructure density asc (spatial only)
	 * Infrastructure density - double (non-spatial only)
	 * Number of iterations - int
	 * Number of repetitions - int
	 * Number of extra scenarios - int
	 * Minimum Persistence Threshold - double (spatial only)
	 * Output Folder - String path for folder
	 * Number of Cores - int
	 * The initial date (yyyy-mm-dd) - String
	 * Time unit (Day, Month, Year) - String
	 * Maximum Processed Individuals - int
	 * Sweep Initial Value - double
	 * Sweep Final Value - double
	 * Sweep Infrastructure mortality? - boolean
	 * Sweep Resolution - int
	 * Scale sweep mortality to yearly - boolean
	 */
	public static void initialization(String[] args) throws FileNotFoundException, IOException {

		if (args.length == 12) {
			spatialInitialization(args);
		} else if (args.length ==10 ||args.length ==15) {
			nonSpatialInitialization(args);
		} else {
			System.out.println("Invalid arguments.");
			return;
		}



		System.exit(0);
	}

	/**
	 * Initializes the non-spatial version of the model 
	 * @param args
	 */
	private static void nonSpatialInitialization(String[] args) {
		txtInputFileLocation = args [0];
		infrastructureDensity =Double.parseDouble(args [1]);
		iterationsToRun = args [2];
		numberOfRepetitions = Integer.parseInt(args [3]);
		numberOfRoadVariations =Integer.parseInt(args [4]);
		txtOutputLocation = args [5];
		numberOfCores = Integer.parseInt(args [6]);
		txtYyyymmdd = args [7];
		TimeUnit = args [8];
		maxProcInd = Integer.parseInt(args [9]);		
		if (args.length == 15) {
			sweepMortalityCheck = true;
			sweepResolutionMin = Double.parseDouble(args [10]);	
			sweepResolutionMax = Double.parseDouble(args [11]);
			sweepRoadkillInstead = Boolean.parseBoolean(args [12]);
			sweepResolution = Integer.parseInt(args [13]);
			scaleToYear = Boolean.parseBoolean(args [14]);
		} else {
			sweepMortalityCheck = false;
		}

		try {
			modelParameters = new ParameterPackage(txtInputFileLocation,numberOfRoadVariations,"NonSpatial");
		} catch (Exception e) {
			e.printStackTrace();
		}
		runModelNonSpatial();		
	}




	/**
	 * Runs the non-spatial model
	 */	
	private static void runModelNonSpatial()  {
		//Creates an array with the points where the model should capture results
		String[] itToRunString = iterationsToRun.split(";");
		itToRun = new int[itToRunString.length];
		for (int i = 0; i < itToRunString.length; i++) {
			itToRun[i] = Integer.parseInt(itToRunString[i]);
		}


		//If no sweep is being made, set sweepRes to 1
		int sweepRes = (int) sweepResolution;
		if (!sweepMortalityCheck) {
			sweepRes = 1;
		}

		//The actual number of repetitions depends on the sweep value
		int repetitions = (int) numberOfRepetitions*sweepRes;

		resultsRepeated = new String[repetitions][];
		resultsExtinctionRepeated = new Map[itToRun.length][repetitions][];		

		long startTime = System.currentTimeMillis();
		long currentTime =System.currentTimeMillis();

		//Runs through the various values of the sweep (or just once if sweepRes is the default 1)
		for (int sweepN = 0 ; sweepN < sweepRes ; sweepN++) {
			if (sweepMortalityCheck) {		//Sweeps base mortality

				double currentMortality =  sweepResolutionMin +  sweepN*(sweepResolutionMax-sweepResolutionMin)/((double)sweepRes-1);							

				if (sweepRoadkillInstead) { //Sweeps base mortality or roadkill mortality, depending on the selection
					for (int numSpecies = 0; numSpecies < modelParameters.baseBirthMort.length; numSpecies++) {

						//Scales the provided mortality to yearly, based on the provided time unit
						if (!scaleToYear) {
							modelParameters.baseBirthMort[numSpecies][modelParameters.baseBirthMort[numSpecies].length-1][0] = currentMortality;
							modelParameters.baseBirthMort[numSpecies][modelParameters.baseBirthMort[numSpecies].length-1][1] = currentMortality;

						} else {
							switch(TimeUnit) {
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
						if (scaleToYear) {
							roadkillPercentage[numSpecies][0] = currentMortality;
							roadkillPercentage[numSpecies][1] = currentMortality;
						} else {
							switch(TimeUnit) {
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
			for (int i = 0; i < numberOfRepetitions;i++) {
				//Avoid memory leak
				System.gc();
				System.out.println("Currently at " + (((double) (i+sweepN*numberOfRepetitions))/((double) repetitions)*100)+"%");

				try { //Sets up the model with the chosen parameters, if able
					model = new Model( modelParameters, infrastructureDensity,numberOfRoadVariations , numberOfCores, maxProcInd);


					int cutNumber = 0;
					while (cutNumber < itToRun.length) { //Runs the model, stopping at each needed section to save 
						int modifier = 0;
						if (cutNumber != 0) {
							modifier = itToRun[cutNumber-1];
						}
						model.run(itToRun[cutNumber]-modifier);
						resultsExtinctionRepeated[cutNumber][i+sweepN*numberOfRepetitions] = model.getExtinction(); //Saves the map
						cutNumber ++;
					}

					resultsRepeated[i+sweepN*numberOfRepetitions] = model.getTextRowToSaveToCSV();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				//Estimates time left
				currentTime = System.currentTimeMillis();
				long elapsedTime = currentTime-startTime;
				long expectedTime =elapsedTime/((i+1)+((sweepN)*numberOfRepetitions))*numberOfRepetitions*sweepRes;
				double seconds = (expectedTime-elapsedTime)/1000.0;
				if (seconds > 60) {
					double minutes = seconds/60.0;			
					if (minutes > 60) {
						double hours = minutes/60.0;
						System.out.println("Time Left: " +(int)Math.ceil(hours) + " h");

					} else {
						System.out.println("Time Left: " +(int)Math.ceil(minutes) + " m");
					}
				} else {
					System.out.println("Time Left: " +(int)Math.ceil(seconds) + " s");
				}
			}
		}

		OutputProcessor.process(resultsRepeated,resultsExtinctionRepeated,model.getRoadMortality() ,txtOutputLocation, model.getSpeciesNames(), "NonSpatial", 0.5,numberOfRoadVariations,
				modelParameters,txtYyyymmdd, TimeUnit, sweepMortalityCheck?(sweepResolution):1, (double) sweepResolutionMin, (double) sweepResolutionMax,itToRun , numberOfRepetitions,generateCommand("NonSpatial"));


	}



	/**
	 * Initializes the spatial version of the model 
	 * @param args
	 */
	private static void spatialInitialization(String[] args) {

		txtInputFileLocation = args[0];
		txtInitialPopulationLocation = args[1];
		txtRoadFileLocation = args[2];
		iterationsToRun = args[3];
		numberOfRepetitions = Integer.parseInt(args[4]);
		numberOfRoadVariations = Integer.parseInt(args[5]);
		minPersistenceThreshold = Double.parseDouble(args[6]);
		txtOutputLocation = args[7];
		numberOfCores = Integer.parseInt(args[8]);
		txtYyyymmdd = args[9];
		TimeUnit = args[10];
		maxProcInd = Integer.parseInt(args[11]);

		try {
			modelParameters = new ParameterPackage(txtInputFileLocation,numberOfRoadVariations,"Spatial");
		} catch (Exception e) {
			e.printStackTrace();
		}
		runModelSpatial();		
	}

	/**
	 * Runs the spatial model
	 */
	private static void runModelSpatial() {
		//Creates an array with the points where the model should capture results
		String[] itToRunString = iterationsToRun.split(";");
		itToRun = new int[itToRunString.length];
		for (int i = 0; i < itToRunString.length; i++) {
			itToRun[i] = Integer.parseInt(itToRunString[i]);
		}

		sweepMortalityCheck= false;
		int sweepRes = 1;

		int repetitions = numberOfRepetitions*sweepRes;

		System.out.println("Running Model...");
		resultsRepeated = new String[repetitions][];
		resultsExtinctionRepeated = new Map[itToRun.length][repetitions][];		

		long startTime = System.currentTimeMillis();

		for (int sweepN = 0 ; sweepN < sweepRes ; sweepN++) {
			for (int i = 0; i < numberOfRepetitions;i++) {

				//Avoid memory leak
				System.gc();
				System.out.println("Currently at " + (((double) (i+sweepN*numberOfRepetitions))/((double) repetitions)*100)+"%");
				try {
					Map roadMap = new Map(new File(txtRoadFileLocation));
					Map[] initialPopulation = initialPopulationLoader(txtInitialPopulationLocation);
					//check if fake map
					if (initialPopulation[0].getCellXSize()== 0) {
						throw new Exception("Missing " + initialPopulation[0].getName() + "'s .asc file.");
					}
					model = new Model( modelParameters, initialPopulation,roadMap, numberOfRoadVariations , numberOfCores,maxProcInd);



					//Runs in sections and stores the values
					int cutNumber = 0;
					while (cutNumber < itToRun.length) {
						int modifier = 0;
						if (cutNumber != 0) {
							modifier = itToRun[cutNumber-1];
						}
						model.run(itToRun[cutNumber]-modifier);
						resultsExtinctionRepeated[cutNumber][i+sweepN*(numberOfRepetitions)] = model.getExtinction();

						cutNumber ++;
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
					break;

				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				//Saves results
				resultsRepeated[i+sweepN*(numberOfRepetitions)] = model.getTextRowToSaveToCSV();

				//Estimates time left
				predictTime( startTime,   i,  sweepN, sweepRes);


			}


		}


		OutputProcessor.process(resultsRepeated,resultsExtinctionRepeated,model.getRoadMortality() ,txtOutputLocation, model.getSpeciesNames(), "Spatial", minPersistenceThreshold,numberOfRoadVariations,
				modelParameters,txtYyyymmdd, TimeUnit, 1, 0, 0, itToRun , numberOfRepetitions,generateCommand("Spatial"));



	}




	/**
	 * Predicts remaining time for model to finish
	 * @param startTime
	 * @param currentTime
	 * @param currentIteration
	 * @param sweepN
	 * @param sweepRes
	 */
	public static void predictTime(long startTime, int currentIteration, int sweepN, int sweepRes) {
		long currentTime = System.currentTimeMillis();
		long elapsedTime = currentTime-startTime;
		long expectedTime =elapsedTime/((currentIteration+1)+((sweepN)*numberOfRepetitions))*numberOfRepetitions*sweepRes;
		double seconds = (expectedTime-elapsedTime)/1000.0;
		if (seconds > 60) {
			double minutes = seconds/60.0;			
			if (minutes > 60) {
				double hours = minutes/60.0;
				System.out.println("Time Left: " +(int)Math.ceil(hours) + " h");

			} else {
				System.out.println("Time Left: " +(int)Math.ceil(minutes) + " m");
			}
		} else {
			System.out.println("Time Left: " +(int)Math.ceil(seconds) + " s");
		}
	}


	/**
	 * Loads all species ascs from folder
	 * @param folder
	 * @return
	 */
	private static Map[] initialPopulationLoader(String folder) {
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

	private static  ArrayList<File> listFilesInFolder(File folder) {
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
	 * Generates the command to run the model
	 */

	private static String generateCommand(String type) {

		String command = "java -jar mortalityRISK.jar " +
				"Parameters.xlsx" + " ";
		if (type.contentEquals("Spatial")) 
			command += "\""+txtInitialPopulationLocation.replace("\\", "/") + "\" " + 
					"\""+txtRoadFileLocation.replace("\\", "/") + "\" ";
		else
			command += infrastructureDensity + " ";
		command += iterationsToRun + " " +
				numberOfRepetitions + " " +
				numberOfRoadVariations +	 " ";
		if (type.contentEquals("Spatial"))
			command += minPersistenceThreshold + " ";
		command += "\""+txtOutputLocation.replace("\\", "/")+	 "\" " +	
				numberOfCores+	 " " +	
				txtYyyymmdd+	 " " +	
				TimeUnit+	 " " +	
				maxProcInd;
		if (sweepMortalityCheck) {
			command += " " + sweepResolutionMin +	 " " +	
					sweepResolutionMax+	 " " +	
					Boolean.toString(sweepRoadkillInstead)+	 " " +	
					sweepResolution+	 " " +	
					Boolean.toString(scaleToYear);
		}

		return command;

	}




}
