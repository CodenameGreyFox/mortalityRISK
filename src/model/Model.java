package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.script.ScriptException;


/**
 * Models species growth and dispersal.
 */

public class Model {

	private ArrayList<Integer>[] popSizePerStep; //Stores the population size each step
	private IndMap[] pop; //Population
	private Map env; //Environment (infrastructures)
	private int[] matAge; //Step species hit maturity (0 = Newborn)
	private int nCores; // Number of CPU Cores to use
	private int[][] lifePhases; // Number of stages
	private int[] minLitSize; //Minimum litter size
	private int[] maxLitSize; //Maximum litter size
	private double[] avgLitSize; //Average litter size
	private double[] sexRatio; // Ratio of males
	private double[] disRan; //Maximum range of dispersal
	private double[] maxPopulation; //The maximum number of individuals that can be present in a single cell
	private double[][][] maxPopulationDensity; //The maximum density of individuals that can be present in a single cell
	private double[][][] mortalityPerCell; //The random modifiers for the repeats
	private double[][][] baseBirthMort; //The base birth rate and mortality rate for each species - [0][species] Birth rate - [1][species] Mortality rate of first age group, [2][species] Mortality rate of second age group, etc , {male,female}
	private double[] mateFindingRadius; // The range at which females can find males for reproduction in km
	private int[] minTimeBetweenBreeding; //Minimum time between breeding events for the same individual
	private double[] avgTimeBetweenBreeding; //Minimum time between breeding events for the same individual
	private Map[] initialPopulation;
	private double[] populationDensity; //initial population density
	private double[] absoluteRoadkillPerKmPerYear; //Number of individuals killed per km per year
	@SuppressWarnings("unused")
	private int nRoadkillVariations; //How the roadkill value will be modified (multiplied by)
	private boolean spatial; //true if it is a spatial model, false if not
	private boolean sweepingRoadkill; //changes model behaviour if sweeping for infrastructure mortality

	//To minimize processing time
	private int maxProcessedIndividuals; //Maximum number of processed individuals
	private double[][][] actualIndRatio;  // nIndividuals/maxProcessedIndividuals


	//Introduction Parameters //// Not currently accessible through the GUI
	@SuppressWarnings("unused")
	private int frequencyOfIntroduction; //The frequency at which individuals are introduced to the population
	@SuppressWarnings("unused")
	private int ageOfIntroduction; //The age of the individuals added to the population
	@SuppressWarnings("unused")
	private int numberOfIntroduction; //How many individuals are introduced to the population
	//////////////////////////////////////////////////////////////

	private String[] speciesNames; //Names of the species

	private int[] startPopValue; //Initial starting population

	private int currentStep;	//Number of steps the model has ran

	/**
	 *  Constructor of the model Class for the non-spatial model
	 * @param parameterPackage The parameters of the model
	 * @param roadKm The density of infrastructure present
	 * @param nRoadkillVariations Number of scenarios
	 * @param nCores
	 * @param maxProcessedIndividuals
	 * @throws Exception
	 */
	public Model(ParameterPackage parameterPackage, double roadKm, int nRoadkillVariations, int nCores, int maxProcessedIndividuals) throws Exception {
		this.spatial = false;
		this.startPopValue = parameterPackage.startPopValue;

		// Create the fake "infrastructure"
		this.env = new Map("Infrastructure", roadKm, 10);

		// Initialize populations specifically for non-spatial
		this.pop = new IndMap[parameterPackage.speciesNames.length];
		this.initialPopulation = new Map[parameterPackage.speciesNames.length];
		for (int s = 0; s < pop.length; s++) {
			pop[s] = new IndMap(parameterPackage.speciesNames[s], 10);
			this.initialPopulation[s] = new Map(pop[s]);
		}

		commonInit(parameterPackage, nRoadkillVariations, nCores, maxProcessedIndividuals);
	}

	/**
	 *  Constructor of the model Class for the spatial model
	 * @param parameterPackage The parameters of the model
	 * @param initialPopulation  Map The initial location of the population
	 * @param environment Map Map of the infrastructures
	 * @param nRoadkillVariations Number of scenarios
	 * @param nCores
	 * @param maxProcessedIndividuals
	 * @throws Exception
	 */
	public Model(ParameterPackage parameterPackage, Map[] initialPopulation, Map environment, int nRoadkillVariations, int nCores, int maxProcessedIndividuals) throws Exception {
		this.spatial = true;
		this.populationDensity = parameterPackage.populationDensity; 
		this.maxPopulationDensity = new double[parameterPackage.speciesNames.length][][];

		this.env = environment;
		this.initialPopulation = initialPopulation;

		// Initialize populations specifically for spatial
		this.pop = new IndMap[parameterPackage.speciesNames.length];
		for (int s = 0; s < pop.length; s++) {
			pop[s] = new IndMap(initialPopulation[s]);
		}

		commonInit(parameterPackage, nRoadkillVariations, nCores, maxProcessedIndividuals);
	}

	/**
	 * Shared initializing logic for both Spatial and Non-Spatial models
	 */
	@SuppressWarnings("unchecked")
	private void commonInit(ParameterPackage pkg, int nRoadkillVariations, int nCores, int maxProcessedIndividuals) throws Exception {
		this.nRoadkillVariations = nRoadkillVariations;
		this.speciesNames = pkg.speciesNames;
		this.lifePhases = pkg.lifePhases;
		this.matAge = pkg.matAge;
		this.minLitSize = pkg.minLitSize;
		this.maxLitSize = pkg.maxLitSize;
		this.avgLitSize = pkg.avgLitSize;
		this.minTimeBetweenBreeding = pkg.minTimeBetweenBreeding;
		this.avgTimeBetweenBreeding = pkg.avgTimeBetweenBreeding;
		this.mateFindingRadius = pkg.mateFindingRadius;
		this.disRan = pkg.disRan;
		this.baseBirthMort = pkg.baseBirthMort;
		this.sexRatio = pkg.sexRatio;
		this.maxPopulation = pkg.maxPopulation;
		this.sweepingRoadkill = pkg.sweepingRoadkill; // Included if present in both
		this.maxProcessedIndividuals = maxProcessedIndividuals;
		this.currentStep = 0;
		this.frequencyOfIntroduction = -1;

		this.absoluteRoadkillPerKmPerYear = new double[pkg.roadkillPerKmPerYear.length];
		for (int i = 0; i < pkg.roadkillPerKmPerYear.length; i++) {
			this.absoluteRoadkillPerKmPerYear[i] = pkg.roadkillPerKmPerYear[i][0];
		}

		this.nCores = Math.min(nCores, pop.length);
		this.mortalityPerCell = new double[pop.length][][];
		this.actualIndRatio = new double[pop.length][][];

		// Initialize list where population size is stored
		this.popSizePerStep = new ArrayList[pop.length];
		for (int s = 0; s < pop.length; s++) {
			this.popSizePerStep[s] = new ArrayList<>();
		}

		runParallelInitialization();
	}

	/**
	 * Private method to initialize the populations.
	 * @throws Exception 
	 * 
	 */

	private void runParallelInitialization() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(nCores);
		List<Callable<Void>> initTasks = new ArrayList<>();
		for (int core = 0; core < nCores; core++) {
			final int c = core;
			initTasks.add(() -> {
				try {
					new processInitPop(c).run();
				} catch (Throwable t) {
					System.err.println("Error in core " + c + ": " + t.getMessage());
					throw t;
				}
				return null;
			});
		}

		List<Future<Void>> futures = null;
	    try {
	        futures = executor.invokeAll(initTasks); 
	        
	        //If any core failed, .get() will throw an ExecutionException
	        for (Future<Void> future : futures) {
	            future.get(); 
	        }
	        
	    } catch (java.util.concurrent.ExecutionException e) {

	    	Throwable myCustomException = e.getCause();
	        
	        // Pass the message up to the GUI
	        throw new Exception(myCustomException.getMessage(), myCustomException);
	        
	    } finally {
	        // 4. Clean up: physically shut down the threads
	        executor.shutdownNow(); 
	    }

		//Stores the population size in the first step
		for (int s = 0; s < pop.length; s++) {
			popSizePerStep[s].add(pop[s].getPopulationSize(actualIndRatio[s]));
		}
	}

	/**
	 * Class that initializes the population in another thread
	 */

	private class processInitPop implements Runnable {
		int coreN;

		processInitPop(int cn) {
			coreN = cn;
		}

		@Override
		public void run() {

			int startSp = (pop.length * coreN) / nCores;
			int endSp = (pop.length * (coreN + 1)) / nCores;

			for (int species = startSp; species < endSp; species ++) {

				int ncols = pop[species].getNcols();
				int nrows = pop[species].getNrows();

				mortalityPerCell[species] = new double [ncols][nrows];
				actualIndRatio[species] = new double [ncols][nrows];

				double avgnIndividuals = 0;				
				if (spatial) {
					maxPopulationDensity[species] = new double [pop[species].getNcols()][pop[species].getNrows()];
					avgnIndividuals = populationDensity[species] * pop[species].calculateAvgCellSize();
				}

				int pixelCounter= 0;
				//If spatial initalizes maximum population and starting population for each cell, as area changes				
				//Runs through cells again for initializing
				for (int x = 0; x < pop[species].getNcols(); x++) {
					for(int y = 0; y < pop[species].getNrows(); y++) {

						//RAM check
						pixelCounter++;
						if (pixelCounter % 1000 == 0) {
							try {
								checkMemorySafety();
							} catch (Exception e) {
								throw new RuntimeException(e.getMessage(), e);
							}
						}
						
						//Gets the road coordinates to calculate roadkill
						int roadCol = env.getLonCol(pop[species].getLon(x));
						int roadRow = env.getLatRow(pop[species].getLat(y));


						//Skips if out of bounds ( or if no presences )
						if (pop[species].getCount(x,y) == pop[species].getNoDataValue()) {
							mortalityPerCell[species][x][y] = 1;
							continue;
						}

						double nIndividuals; 


						pop[species].killAllIndividuals(x,y);
						if (spatial) {

							nIndividuals = populationDensity[species] * pop[species].getSqKMeters(x, y);							
							maxPopulationDensity[species][x][y] = maxPopulation[species] * pop[species].getSqKMeters(x, y);


							//Mortality in each cell is based on the initial proportion of individuals killed there with the observed roadkill
							//Important, the number of individuals is considered constant throughout all cells (even those that start with no individuals).
							//Based on a constant hazard rate model derived from a continuous-time Poisson process.
							try {
								mortalityPerCell[species][x][y]= 1-Math.pow(Math.E,-(absoluteRoadkillPerKmPerYear[species]*env.getValue(roadCol, roadRow))/avgnIndividuals);
							} catch (ArrayIndexOutOfBoundsException e) {
								throw new RuntimeException("The infr. raster is smaller than the species'.", e);
							}


							if (maxProcessedIndividuals > 0 && nIndividuals > maxProcessedIndividuals) {
								actualIndRatio[species][x][y] = nIndividuals/((double)maxProcessedIndividuals);
								nIndividuals = maxProcessedIndividuals ;
							} else {
								actualIndRatio[species][x][y] = 1;
							}							

							int litSz = 0;
							if ( maxLitSize[species] == -1) {
								litSz = (int) Math.round(avgLitSize[species]);
							} else {
								litSz = (int) Math.round((maxLitSize[species] + minLitSize[species])/2); 
							}
							if (baseBirthMort[species][0][1] != -1) { //If not using odds for reproduction, just time spacing.
								pop[species].addRandomIndividuals((int)nIndividuals,currentStep, lifePhases[species], minTimeBetweenBreeding[species], matAge[species], litSz,baseBirthMort[species][0][1] , sexRatio[species],x, y);
							} else {
								pop[species].addRandomIndividuals((int)nIndividuals,currentStep, lifePhases[species], minTimeBetweenBreeding[species], matAge[species], litSz,avgTimeBetweenBreeding[species] , sexRatio[species],x, y);
							}
						} else { //if not spatial

							nIndividuals = startPopValue[species];

							if (maxProcessedIndividuals > 0 && nIndividuals > maxProcessedIndividuals) {
								actualIndRatio[species][x][y] = ((double)nIndividuals)/((double)maxProcessedIndividuals);
								nIndividuals = maxProcessedIndividuals;
							} else {
								actualIndRatio[species][x][y] = 1;
							}		

							int litSz = 0;
							if ( maxLitSize[species] == -1) {
								litSz = (int) Math.round(avgLitSize[species]);
							} else {
								litSz = (int) Math.round((maxLitSize[species] + minLitSize[species])/2); 
							}
							if (baseBirthMort[species][0][1] != -1) {
								pop[species].addRandomIndividuals((int)nIndividuals,currentStep, lifePhases[species], minTimeBetweenBreeding[species], matAge[species], litSz, baseBirthMort[species][0][1], sexRatio[species],x, y);
							} else {
								pop[species].addRandomIndividuals((int)nIndividuals,currentStep, lifePhases[species], minTimeBetweenBreeding[species], matAge[species], litSz, avgTimeBetweenBreeding[species], sexRatio[species],x, y);
							}

							if (sweepingRoadkill) {
								mortalityPerCell[species][x][y] =absoluteRoadkillPerKmPerYear[species];
							} else {

								//Mortality in each cell is based on the initial proportion of individuals killed there with the observed roadkill
								//Based on a constant hazard rate model derived from a continuous-time Poisson process.
								mortalityPerCell[species][x][y]= 1-Math.pow(Math.E,-(absoluteRoadkillPerKmPerYear[species]*env.getValue(roadCol, roadRow))/((double)startPopValue[species]));

							}
						}
					}
				}				
			}
		}
	}


	/**
	 * Returns the age at which a species is able to reproduce
	 * @param species int The species
	 * @return int The stage at which species are able to reproduce
	 */
	public int getMatureAge(int species) {
		return matAge[species];
	}

	/**
	 * Returns the maximum litter size of a species
	 * @param species int The species
	 * @return int The maximum litter size
	 */
	public int getMaxLitterSize(int species) {
		return maxLitSize[species];
	}

	/**
	 * Returns the minimum litter size of a species
	 * @param species int The species
	 * @return int The minimum litter size
	 */
	public int getMinLitterSize(int species) {
		return minLitSize[species];
	}

	/**
	 * Returns the maximum dispersal range in km of a species
	 * @param species int The species
	 * @return double The maximum dispersal range (km)
	 */
	public double getDispersalRange(int species) {
		return disRan[species];
	}

	/**
	 * Returns the names of the species present
	 *
	 * @return String[] Array with the names of the species listed
	 */
	public String[] getSpeciesNames() {

		return speciesNames;
		/**
		String[] speciesNames = new String[pop.length];
		for (int i = 0 ; i < pop.length; i++) {
			speciesNames[i] = pop[i].getName();
		}
		return speciesNames;
		 */
	}

	/**
	 *  Makes the model perform one or more steps.
	 *
	 * @param nSteps int Number of steps to perform
	 * @throws Exception 
	 * @throws ScriptException
	 */

	public void run(int nSteps) throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(nCores);

		for (int step = 0; step < nSteps; step++) {
			checkMemorySafety();
			currentStep ++;

			//Checks for cancellation
			if (Thread.currentThread().isInterrupted()) {
				break;
			}

			//Process Births and Deaths
			List<Callable<Void>> bdTasks = new ArrayList<>();
			for (int core = 0; core < nCores; core++) {
				final int c = core;
				bdTasks.add(() -> {
					// Execute the logic directly instead of spawning a new raw Thread		
					try {
						new processBirthsAndDeaths(c).run();
					} catch (Throwable t) {
						System.err.println("Error in core " + c + ": " + t.getMessage());
						t.printStackTrace();
					}
					return null;
				});
			}

			try {
				// Blocks efficiently until all births/deaths are processed
				executor.invokeAll(bdTasks); 
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			//Checks for cancellation
			if (Thread.currentThread().isInterrupted()) {
				break;
			}

			//Deals with migration if spatial
			if (spatial) {
				//Runs the class that deals with migration
				List<Callable<Void>> migTasks = new ArrayList<>();
				for (int core = 0; core < nCores; core++) {
					final int c = core;
					migTasks.add(() -> {
						try {
							new processMigrations(c, disRan).run();
						} catch (Throwable t) {
							System.err.println("Error in core " + c + ": " + t.getMessage());
							throw t;
						}
						return null;
					});
				}

				try {
					executor.invokeAll(migTasks);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

			for (int species = 0; species < pop.length; species ++) {
				popSizePerStep[species].add(pop[species].getPopulationSize(actualIndRatio[species]));
			}
		}
		// Clean up the thread pool when the run finishes
		executor.shutdown();
	}

	/**
	 * Returns the model's current step
	 *
	 * @return int The model's current step
	 */
	public int getCurrentStep() {
		return currentStep;
	}

	/**
	 * Saves the .ascs of each species and rank, as well as the parameters, to a folder named with the current step, WIP
	 *
	 * @param folder File Folder to save the .asc to
	 * @param all Boolean True if all stages are to be saved, false if only the compiled one
	 * @throws IOException


	public void saveSpeciesToFile(File folder, boolean all, int repetition) throws IOException {

		String pathNameBase;
		if (all) {
			pathNameBase = folder.getAbsolutePath()+System.getProperty("file.separator")+"Current Step "+currentStep+System.getProperty("file.separator");
		} else {
			pathNameBase = folder.getAbsolutePath()+System.getProperty("file.separator")+"Compiled Repeats"+System.getProperty("file.separator")+"Repetition "+ repetition+System.getProperty("file.separator");
		}
		String pathName;
		Path path = Paths.get(pathNameBase);

		while (Files.exists(path)) {
			repetition++;
			pathNameBase = folder.getAbsolutePath()+System.getProperty("file.separator")+"Compiled Repeats"+System.getProperty("file.separator")+"Repetition "+ repetition+System.getProperty("file.separator");
			path = Paths.get(pathNameBase);
		}


		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (all) {
			String stageFormatted;
			for	(int species = 0; species <pop.length; species ++) {
				for (int stage = 0; stage < nStages[species]; stage ++) {
					stageFormatted = String.format("%03d", stage);
					pathName = pathNameBase + pop[species][stage].getName()+"_Stage " + stageFormatted +".asc";
					pop[species][stage].saveToFile(pathName);
				}
			}
		}
		for	(int speciesNumber = 0; speciesNumber <pop.length; speciesNumber ++) {
			Map compiledMap = new Map (pop[speciesNumber][0].getName(), pop[speciesNumber][0].getNcols(), pop[speciesNumber][0].getNrows(), pop[speciesNumber][0].getXcorner(), pop[speciesNumber][0].getYcorner(), pop[speciesNumber][0].getCellSize(), pop[speciesNumber][0].getNoDataValue() );
			double summedValues;
			for(int x = 0; x < pop[speciesNumber][0].getNcols(); x++) {
				for(int y = 0; y < pop[speciesNumber][0].getNrows(); y++) {
					if (pop[speciesNumber][0].getCount(x,y)!= pop[speciesNumber][0].getNoDataValue()) {
						summedValues = 0;
						for(int stage = 0; stage < nStages[speciesNumber]; stage ++ ) {
							summedValues += pop[speciesNumber][stage].getCount(x,y);
						}
						compiledMap.setValue(summedValues, x, y);
					}
				}
			}
			pathName = pathNameBase + compiledMap.getName()+"_CompiledStages.asc";
			compare.comp(compiledMap,pathNameBase);
			compiledMap.saveToFile(pathName);
		}



		try {
			BufferedWriter paramTxt = new BufferedWriter(new FileWriter(new File(pathNameBase + "Parameters.txt")));
			paramTxt.write("Maximum population:\r\n" + maxPopulation +"\r\n");
			paramTxt.write("Number of species:\r\n" + pop.length +"\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(pop[species][0].getName() +"\r\n");
			}

			paramTxt.write("Number of stages:\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(nStages[species] +"\r\n");
			}
			paramTxt.write("Mature stage:\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(matSta[species]+"\r\n");
			}
			paramTxt.write("Migration rate:\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(migR[species]+"\r\n");
			}
			paramTxt.write("Stage duration:\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(staDur[species]+"\r\n");
			}
			paramTxt.write("Maximum litter size:\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(litSize[species]+"\r\n");
			}
			paramTxt.write("Dispersal range:\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(disRan[species]+"\r\n");
			}
			paramTxt.write("Swims or flies:\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(swimFly[species] + "\r\n");
			}
			paramTxt.write("Modifiers:\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(randModifiers[0]+"\r\n"+randModifiers[1]+"\r\n"+randModifiers[2]+"\r\n");
			}

			paramTxt.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedWriter equationTxt = new BufferedWriter(new FileWriter(new File(pathNameBase + "Equations.txt")));

			for (int species = 0; species < pop.length; species++) {
				equationTxt.write(equationsBackup[0][species] +"\r\n");
				equationTxt.write(equationsBackup[1][species] +"\r\n\r\n");
			}

			equationTxt.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}	 */


	/**
	 * Class that processes births and deaths in another thread
	 */

	private class processBirthsAndDeaths implements Runnable {
		int coreN;
		//	IndMap[] prevPop;

		processBirthsAndDeaths(int cn) {
			//	System.out.println("Birth start");
			coreN = cn;
		}
		@Override
		public void run() {
			double birthR;
			int newBorns;
			int startSp;
			int endSp;

			Random randomizer = new Random();

			startSp = (pop.length * coreN) / nCores;
			endSp = (pop.length * (coreN + 1)) / nCores;


			for (int species = startSp; species <endSp; species ++) {

				//First updates the existence of males grid
				pop[species].maleCheck(matAge[species], currentStep);

				//Runs through all active cells and applies birth/mortality rate				
				for (long cell : pop[species].getOccupiedCells()) {
					int x= IndMap.getXFromCellKey(cell);
					int y= IndMap.getYFromCellKey(cell);

					newBorns = 0;

					//Randomly defines if birth or mortality comes first
					boolean birthFirst = Math.random() < 0.5 ? true : false;

					//Randomly decides how many individuals die
					if(!birthFirst) {	//Calculates mortality first
						double roadMortality =	mortalityPerCell[species][x][y];	//Gets the road mortality associated with the cell
						for (int i = (int) pop[species].getCount(x, y)-1; i >= 0; i --) {
							double actualMortality = baseBirthMort[species][1+pop[species].getIndividual(i,x,y).getLifePhase(currentStep)]
									[pop[species].getIndividual(i, x, y).isMale() ? 0:1];

							if (randomizer.nextDouble() < actualMortality || randomizer.nextDouble() < roadMortality) { // Can die due to roadMortality or due to base mortality
								pop[species].killIndividual(i,x,y);

							}
						}
					}

					birthR = 0;
					int areThereMales = -1; //-1 Unknown, 0 No males, 1 some males
					for (int i = 0; i < pop[species].getCount(x, y); i ++) {
						if (areThereMales == 0) {
							break;
						}

						if (pop[species].getIndividual(i, x, y).isAvaiableForBirth(matAge[species],currentStep, minTimeBetweenBreeding[species])){
							if (baseBirthMort[species][0][1] == -1 || randomizer.nextDouble() < birthR+baseBirthMort[species][0][1]) { //random chance (or 100% if using uniform distribution for birth prob)
								if (areThereMales == -1) { //If already found a male for other female, there also exists one for this one.
									//Find maximum male search radius	
									//Find distance from down to up
									double upDownDistance = Map.haversine(pop[species].getLon(x), pop[species].getLat(y), pop[species].getLon(x), pop[species].getLat(y)+pop[species].getCellYSize());
									//Find distance from left to right
									double leftRightDistance = Map.haversine(pop[species].getLon(x), pop[species].getLat(y), pop[species].getLon(x)+pop[species].getCellXSize(), pop[species].getLat(y));
									double averageDist = (upDownDistance+leftRightDistance)/2;

									int searchRadius = (int)Math.floor(mateFindingRadius[species]/1000.0/averageDist); //matefindingradius is in meters, thus /1000

									//Bounds the search to the map size
									int minX =  Math.max(0, x - searchRadius);
									int maxX = Math.min(pop[species].getNcols() - 1, x + searchRadius);
									int minY = Math.max(0, y - searchRadius);
									int maxY = Math.min(pop[species].getNrows() - 1, y + searchRadius);
									searchLoop:
										for (int xx = minX; xx <= maxX; xx++) {											
											for (int yy = minY; yy <= maxY; yy++) {
												if (areThereMales == -1) { // Only reproduces if there is at least one male in the same cell. Extra steps avoid multiple checks for same cell
													if (pop[species].maleExists(xx,yy) ) {
														areThereMales = 1;
														break searchLoop;
													}
												}
											}
										}
									if (areThereMales  == -1) { //If it searched every cell and did not find a male, define areThereMales as 0 so others don't need to search
										areThereMales = 0;
									}
								}
								if (areThereMales == 1) {									

									if ( avgLitSize[species] == -1) { //Depending if the use gave min/max or avg litter size
										newBorns += randomizer.nextInt(maxLitSize[species]-minLitSize[species]+1)+minLitSize[species];
									} else { //If average litter size
										newBorns ++;
										while(Math.random() > (1.0/(avgLitSize[species]))) {
											newBorns ++;
										}
									}

									pop[species].getIndividual(i, x, y).markAsHavingHadBirth(currentStep);
									if (baseBirthMort[species][0][1] == -1) {//If using uniform distribution over birth range
										//Sets the timing of the next reproductive event
										double spacing  = Math.round(avgTimeBetweenBreeding[species] -  minTimeBetweenBreeding[species]) ;
										pop[species].getIndividual(i, x, y).adjustBirthTiming((int)Math.round((Math.random()*(spacing*2))));
									}
								}
							}
						}
					}

					//Adds newborns
					pop[species].addNewborns(newBorns,currentStep, lifePhases[species], sexRatio[species], x,y);


					if(birthFirst) { //Calculates mortality last
						double roadMortality =	mortalityPerCell[species][x][y];	//Gets the road mortality associated with the cell
						for (int i = (int) pop[species].getCount(x, y)-1; i >= 0; i --) {
							double actualMortality = baseBirthMort[species][1+pop[species].getIndividual(i,x,y).getLifePhase(currentStep)]
									[pop[species].getIndividual(i, x, y).isMale() ? 0:1];					

							if (randomizer.nextDouble() < actualMortality || randomizer.nextDouble() < roadMortality) { // Can die due to roadMortality or due to base mortality
								pop[species].killIndividual(i,x,y);
							}
						}
					}

					pop[species].killOldAge(x, y, currentStep, lifePhases[species][lifePhases[species].length-1]);

					//Adds or removes individuals too keep with the real number of individuals and the maximum population		

					int popSize = (int) pop[species].getCount(x, y);
					double actualPopSize = ((double)popSize)*actualIndRatio[species][x][y];

					int maxPop = 0;
					if (maxPopulationDensity != null) {//Depending on if the model is spatial or not
						maxPop = (int) maxPopulationDensity[species][x][y]; 
					} else {
						maxPop = (int) maxPopulation[species];
					}


					double targetPop = Math.min(actualPopSize, maxPop);
					if (maxProcessedIndividuals > 0 && targetPop > maxProcessedIndividuals) {
						actualIndRatio[species][x][y] = targetPop/((double)maxProcessedIndividuals); //Calculates the new ratio
						targetPop = maxProcessedIndividuals;
					} else {
						actualIndRatio[species][x][y] = 1; //The number of individuals in the model is now the real number of individuals
					}

					if (targetPop > popSize) {	//If the simulated population ended up becoming smaller than what it was supposed to due to the max processed individuals, add individuals
						int litSz = 0;
						if ( maxLitSize[species] == -1) {
							litSz = (int) Math.round(avgLitSize[species]);
						} else {
							litSz = (int) Math.round((maxLitSize[species] + minLitSize[species])/2); 
						}
						if (baseBirthMort[species][0][1] != -1) {
							pop[species].addRandomIndividuals((int)Math.round(targetPop)-popSize,currentStep, lifePhases[species], minTimeBetweenBreeding[species], matAge[species],litSz,baseBirthMort[species][0][1] , sexRatio[species],x, y);
						} else {
							pop[species].addRandomIndividuals((int)Math.round(targetPop)-popSize,currentStep, lifePhases[species], minTimeBetweenBreeding[species], matAge[species],litSz,avgTimeBetweenBreeding[species] , sexRatio[species],x, y);
						}

					} else if (targetPop < popSize) { //If there are more individuals than the carrying capacity of the maximum simulated individuals, kill individuals
						pop[species].killRandomIndividuals(popSize-(int)Math.round(targetPop),x, y);
					}
				}
			}
		}
	}

	/**
	 * Class that processes migrations in another thread
	 */

	private class processMigrations implements Runnable {
		int coreN;
		double[] dispersalRange;

		processMigrations(int cn, double[] actualDispersalRange) {
			coreN = cn;
			dispersalRange = actualDispersalRange;

		}
		@Override
		public void run() {
			int startSpecies;
			int endSpecies;

			startSpecies = (pop.length * coreN) / nCores;
			endSpecies = (pop.length * (coreN + 1)) / nCores;

			for (int species = startSpecies; species < endSpecies; species ++) {

				//If the species does not migrate, skip it
				if (dispersalRange[species] == 0) {
					continue;
				}

				//Runs through active cells for migration
				for (long cell : pop[species].getOccupiedCells()) {
					int x= IndMap.getXFromCellKey(cell);
					int y= IndMap.getYFromCellKey(cell);


					for (int movingInd = 0; movingInd < pop[species].getCount(x,y); movingInd++) {
						//Provided dispersalRange is in meters
						pop[species].moveIndividual(x,y,movingInd,Math.random()*360.0, Math.sqrt(Math.random())*dispersalRange[species]);
					}					
				}
				//Applies the planned moves simultaneously
				pop[species].applyAllMoves();
			}
		}
	}


	/**
	 *  Returns the number of species
	 * @return int The number of species
	 */

	public int getNSpecies() {
		return pop.length;
	}

	/**
	 * Sets the mature age of a certain species
	 * @param species int The Species
	 * @param matureAge int The mature age
	 */

	public void setMatureAge(int species, int matureAge) {
		matAge[species] = matureAge;
	}

	/**
	 * Sets the dispersal range of a certain species
	 * @param species int The Species
	 * @param dispersalRange double The dispersal range (km)
	 */

	public void setDispersalRange(int species, double dispersalRange) {
		disRan[species] = dispersalRange;
	}

	/**
	 * Sets the maximum litter size of a certain species
	 * @param species int The Species
	 * @param litterSize int The litter size
	 */
	public void setMaxLitterSize(int species, int litterSize) {
		maxLitSize[species] = litterSize;
	}

	/**
	 * Sets the minimum litter size of a certain species
	 * @param species int The Species
	 * @param litterSize int The litter size
	 */
	public void setMinLitterSize(int species, int litterSize) {
		minLitSize[species] = litterSize;
	}

	/**
	 * Sets the maximum population in a cell
	 * @param maxPopulation int The maximum number of individuals present in a cell
	 */

	public void setMaximumPopulation(int maxPopulation,int species) {
		this.maxPopulation[species] = maxPopulation;
	}


	/**
	 * Saves the results of the runs to a csv
	 */

	public String[] getTextRowToSaveToCSV() {
		String[] output = new String [pop.length];
		for(int species = 0; species < pop.length; species++) {
			output[species] = "";
			for(int i = 0; i < popSizePerStep[0].size();i++) {
				output[species] += popSizePerStep[species].get(i) + ",";
			}
		}
		return output;

	}

	/**
	 * Saves the results of the runs to a csv
	 */

	public int[][] getNumbersToSaveToCSV() {
		int[][] output = new int [pop.length][popSizePerStep[0].size()];
		for(int species = 0; species < pop.length; species++) {
			for(int i = 0; i < popSizePerStep[0].size();i++) {
				output[species][i] = popSizePerStep[species].get(i);
			}
		}
		return output;

	}


	/**
	 * Saves the ascs to the specified folder
	 */
	public void saveAsc(String folder) {

		for (int species = 0; species < pop.length; species++) {
			pop[species].saveToFile(folder+getSpeciesNames()[species]+".asc");
		}

	}

	/**
	 * Returns a Map[] where each cell has -1 if the species never existed there, 0 if it went extinct and 1 if it still occurs
	 *
	 */

	public Map[] getExtinction() {
		Map[] extinctionMap = new Map [initialPopulation.length];
		for (int i = 0; i < pop.length; i++) {
			extinctionMap[i] = initialPopulation[i].clone();

			for (int x = 0; x < extinctionMap[i].getNcols(); x++) {
				for(int y = 0; y < extinctionMap[i].getNrows(); y++) {
					if (extinctionMap[i].getValue(x, y) != extinctionMap[i].getNoDataValue()) {
						if (extinctionMap[i].getValue(x, y) == 0) {
							extinctionMap[i].setValue(-1,x, y); //Sets to -1 if it did not exist there to being with
						} else {
							extinctionMap[i].setValue(pop[i].getCount(x, y) == 0 ? 0 : 1  ,x, y);  //Sets to 0 if no individuals there and 1 if there are individuals
						}
					}
				}
			}
		}

		return extinctionMap;
	}

	/**
	 * Prints out all parameters (currently not in use)
	 */
	/**
	public void printParameters() {

		for (int species = 0; species < speciesNames.length; species++) {

			System.out.println("The " + speciesNames[species] + " has as parameters:");

			String temp = "";
			for(int i = 0; i < lifePhases[species].length; i++) {
				temp += lifePhases[species][i]+" ; ";
			}
			System.out.println("Life phases: " + temp);
			System.out.println("Maximum population: "+ maxPopulation[species]);			
			System.out.println("Maturity age: " + matAge[species]);
			System.out.println("Minimum litter size: " + minLitSize[species]);
			System.out.println("Maximum litter size: " + maxLitSize[species]);
			System.out.println("Minimum time between breeding: "+ minTimeBetweenBreeding[species]);
			System.out.println("Mate Finding Radius: "+ mateFindingRadius[species] + "m");
			System.out.println("Birth probability: " + baseBirthMort[species][0][0]);
			temp = "";
			for(int i = 1; i < baseBirthMort[species].length; i++) {
				temp += baseBirthMort[species][i][0]+" ; ";
			}
			System.out.println("Death Probability Male: " + temp);
			temp = "";
			for(int i = 1; i < baseBirthMort[species].length; i++) {
				temp += baseBirthMort[species][i][1]+" ; ";
			}
			System.out.println("Death Probability Female: " + temp);
			System.out.println("Roadkill/Km/Year Absolute: "+ absoluteRoadkillPerKmPerYear[species] );				
			System.out.println("Roadkill/Km/Year % : " +mortalityPerCell[species] );
			System.out.println("Migration distance: " + disRan[species]);

		}

	} */

	/**
	 * Introduces individuals into all the populations
	 */

	public void introduceIndividuals(int n, int age) {
		for (int i = 0 ; i < pop.length; i++) {

			pop[i].addIndividuals(n,age,currentStep,lifePhases[i],minTimeBetweenBreeding[i],baseBirthMort[i][0][1],sexRatio[i]);

		}
	}

	/**
	 * Sets how the introduction of individuals should be handled by the model
	 * @param frequency
	 * @param n
	 * @param age
	 */

	public void setIntroductionOfIndividuals(int frequency, int n, int age) {
		frequencyOfIntroduction = frequency;
		ageOfIntroduction = n;
		numberOfIntroduction = age;
	}

	/**
	 * Returns the map with the road mortality per cell given as percentage
	 */

	public Map[] getRoadMortality (){
		Map[] roadMortality = new Map[mortalityPerCell.length];

		for(int i = 0; i < roadMortality.length; i++) {
			roadMortality[i] = new Map(mortalityPerCell[i],initialPopulation[i]);
		}

		return roadMortality;
	}

	/**
	 * Returns a deep clone of the model


	public Model clone() {
		return new Model(this);		
	}
	 */

	/**
	 * Private method to clone model
	 * @param model

	@SuppressWarnings("unchecked")
	private Model(Model model) {

		popSizePerStep = new ArrayList[model.popSizePerStep.length]; 
		for (int i =0 ; i < popSizePerStep.length; i++) {
			popSizePerStep[i] = new ArrayList<Integer>();
			for (int z = 0; z < model.popSizePerStep[i].size(); z++) {	
				popSizePerStep[i].add(model.popSizePerStep[i].get(z)); 
			}
		}

		pop = new IndMap[model.pop.length];
		for (int i =0 ; i < pop.length; i++) {
			pop[i] = model.pop[i].clone();
		}

		env = model.env.clone();


		matAge = new int[model.matAge.length];
		for (int i =0 ; i < matAge.length; i++) {
			matAge[i] = model.matAge[i];
		}


		nCores = model.nCores;

		lifePhases = new int[model.lifePhases.length][];
		for (int i =0 ; i < lifePhases.length; i++) {
			lifePhases[i] = new int[model.lifePhases[i].length];
			for (int z =0 ; z < lifePhases[i].length;z++) {
				lifePhases[i][z] = model.lifePhases[i][z];
			}
		}

		minLitSize = new int[model.minLitSize.length];
		for (int i =0 ; i < minLitSize.length; i++) {
			minLitSize[i] = model.minLitSize[i];
		}

		maxLitSize = new int[model.maxLitSize.length];
		for (int i =0 ; i < maxLitSize.length; i++) {
			maxLitSize[i] = model.maxLitSize[i];
		}

		sexRatio = new double[model.sexRatio.length];
		for (int i =0 ; i < sexRatio.length; i++) {
			sexRatio[i] = model.sexRatio[i];
		}

		disRan = new double[model.disRan.length];
		for (int i =0 ; i < disRan.length; i++) {
			disRan[i] = model.disRan[i];
		}

		maxPopulation = new double[model.maxPopulation.length];
		for (int i =0 ; i < maxPopulation.length; i++) {
			maxPopulation[i] = model.maxPopulation[i];
		}

		//		randModifier = model.randModifier;

		mortalityPerCell = new double[model.mortalityPerCell.length][][];
		for (int i =0 ; i < mortalityPerCell.length; i++) {
			mortalityPerCell[i] = new double [model.mortalityPerCell[i].length][];
			for (int z =0 ; z < mortalityPerCell[i].length; z++) {
				mortalityPerCell[i][z] = new double [model.mortalityPerCell[i][z].length];
				for (int x =0 ; x < mortalityPerCell[i][z].length; x++) {
					mortalityPerCell[i][z][x] = model.mortalityPerCell[i][z][x];					
				}					
			}
		}

		baseBirthMort = new double[model.baseBirthMort.length][][];
		for (int i =0 ; i < baseBirthMort.length; i++) {
			baseBirthMort[i] = new double [model.baseBirthMort[i].length][];
			for (int z =0 ; z < baseBirthMort[i].length; z++) {
				baseBirthMort[i][z] = new double [model.baseBirthMort[i][z].length];
				for (int x =0 ; x < baseBirthMort[i][z].length; x++) {
					baseBirthMort[i][z][x] = model.baseBirthMort[i][z][x];					
				}					
			}
		}

		mateFindingRadius = new double[model.mateFindingRadius.length];
		for (int i =0 ; i < mateFindingRadius.length; i++) {
			mateFindingRadius[i] = model.mateFindingRadius[i];
		}

		minTimeBetweenBreeding = new int[model.minTimeBetweenBreeding.length];
		for (int i =0 ; i < minTimeBetweenBreeding.length; i++) {
			minTimeBetweenBreeding[i] = model.minTimeBetweenBreeding[i];
		}

		initialPopulation = new Map[model.initialPopulation.length];
		for (int i =0 ; i < initialPopulation.length; i++) {
			initialPopulation[i] = model.initialPopulation[i].clone();
		}

		populationDensity = new double[model.populationDensity.length];
		for (int i =0 ; i < populationDensity.length; i++) {
			populationDensity[i] = model.populationDensity[i];
		}

		absoluteRoadkillPerKmPerYear = new double[model.absoluteRoadkillPerKmPerYear.length];
		for (int i =0 ; i < absoluteRoadkillPerKmPerYear.length; i++) {
			absoluteRoadkillPerKmPerYear[i] = model.absoluteRoadkillPerKmPerYear[i];
		}

		nRoadkillVariations = model.nRoadkillVariations;

		spatial = model.spatial;

		frequencyOfIntroduction = model.frequencyOfIntroduction;
		ageOfIntroduction = model.ageOfIntroduction;
		numberOfIntroduction = model.numberOfIntroduction;

		speciesNames = new String[model.speciesNames.length];
		for (int i =0 ; i < speciesNames.length; i++) {
			speciesNames[i] = model.speciesNames[i];
		}

		startPopValue = new int[model.startPopValue.length];
		for (int i =0 ; i < startPopValue.length; i++) {
			startPopValue[i] = model.startPopValue[i];
		}

		currentStep = model.currentStep;

	}
	 */
	/**
	 * Returns the population size per step for testing purposes
	 * @param speciesIndex
	 * @return
	 */
	public java.util.ArrayList<Integer> getPopulationHistory(int speciesIndex) {
		return this.popSizePerStep[speciesIndex];
	}

	
	/**
	 * Avoids memory crashes
	 * @throws Exception
	 */
	public void checkMemorySafety() throws Exception {
	    Runtime runtime = Runtime.getRuntime();
	    long maxMemory = runtime.maxMemory();
	    long allocatedMemory = runtime.totalMemory();
	    long freeMemory = runtime.freeMemory();
	    
	    long totalFreeMemory = freeMemory + (maxMemory - allocatedMemory);
	    	    
	    // If less than 500 Megabytes of RAM remain, abort safely
	    if (totalFreeMemory < 250 * 1024 * 1024) { 
	        throw new Exception("Out of Memory: Simulation aborted.");
	    }
	}
}
