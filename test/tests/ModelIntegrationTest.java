package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import model.IndMap;
import model.Map;
import model.Model;
import model.ParameterPackage;

public class ModelIntegrationTest {

	@Test
	public void testTotalinitialPopulation() throws Exception {
		// Set up custom parameters
		int startingPopulation = 100;
		ParameterPackage parameters = new ParameterPackage();
		parameters.speciesNames = new String[]{"TestSpecies"};
		parameters.startPopValue = new int[]{startingPopulation}; // Start with 100 individuals
		parameters.lifePhases = new int[][]{{5, 24, 36}};
		parameters.matAge = new int[]{5};
		parameters.minLitSize = new int[]{1};
		parameters.maxLitSize = new int[]{-1};
		parameters.avgLitSize = new double[]{2};
		parameters.sexRatio = new double[]{0.5};
		parameters.minTimeBetweenBreeding = new int[]{10};
		parameters.avgTimeBetweenBreeding = new double[]{10.0};
		parameters.mateFindingRadius = new double[]{1000.0};
		parameters.disRan = new double[]{5.0};
		parameters.maxPopulation = new double[] {1000};
		parameters.roadkillPerKmPerYear = new double[][]{{0.0}}; // Turn off infrastructure effects for baseline isolation
		parameters.sweepingRoadkill = false;

		// Force a 100% baseline mortality (1.0) for males and females across all possible phases
		// Matrix dimensions: [species][phaseIndex][sexIndex 0=Male, 1=Female]
		parameters.baseBirthMort = new double[1][4][2]; 
		parameters.baseBirthMort[0][0][0] = -1.0;
		parameters.baseBirthMort[0][0][1] = -1.0;
		for (int phase = 1; phase < 4; phase++) {
			parameters.baseBirthMort[0][phase][0] = 1.0; // 100% Male Mortality
			parameters.baseBirthMort[0][phase][1] = 1.0; // 100% Female Mortality
		}

		// Initialize the non-spatial model constructor with controlled test parameters
		// Arguments: parameters, roadKm, nRoadkillVariations, nCores, maxProcessedIndividuals
		Model model = new Model(parameters, 0.0, 1, 1, 0);

		// Execute exactly 1 time-step of the model pipeline
		model.run(1);

		// Index 0 is the starting population (100)
		int populationAfterStepOne = model.getPopulationHistory(0).get(0);

		assertEquals(startingPopulation, populationAfterStepOne, 
				"The population should be match the provided initial population size at step 0 (using avg. time between breeding.");

		//Repeats the test with different birth rate
		parameters.avgTimeBetweenBreeding = new double[]{-1.0};
		parameters.baseBirthMort[0][0][0] = 0.2;
		parameters.baseBirthMort[0][0][1] = 0.2;

		// Initialize the non-spatial model constructor with controlled test parameters
		// Arguments: parameters, roadKm, nRoadkillVariations, nCores, maxProcessedIndividuals
		model = new Model(parameters, 0.0, 1, 1, 0);

		// Execute exactly 1 time-step of the model pipeline
		model.run(1);

		// Index 0 is the starting population (100)
		populationAfterStepOne = model.getPopulationHistory(0).get(0);
		assertEquals(startingPopulation, populationAfterStepOne, 
				"The population should be match the provided initial population size at step 0 (using birth rate.");        
	}



	@Test
	public void testTotalExtinctionIntegration() throws Exception {
		// Set up custom parameters
		int startingPopulation = 100;
		ParameterPackage parameters = new ParameterPackage();
		parameters.speciesNames = new String[]{"TestSpecies"};
		parameters.startPopValue = new int[]{startingPopulation}; // Start with 100 individuals
		parameters.lifePhases = new int[][]{{5, 24, 36}};
		parameters.matAge = new int[]{5};
		parameters.minLitSize = new int[]{1};
		parameters.maxLitSize = new int[]{-1};
		parameters.avgLitSize = new double[]{2};
		parameters.sexRatio = new double[]{0.5};
		parameters.minTimeBetweenBreeding = new int[]{10};
		parameters.avgTimeBetweenBreeding = new double[]{10.0};
		parameters.mateFindingRadius = new double[]{1000.0};
		parameters.disRan = new double[]{5.0};
		parameters.maxPopulation = new double[] {1000};
		parameters.roadkillPerKmPerYear = new double[][]{{0.0}}; // Turn off infrastructure effects for baseline isolation
		parameters.sweepingRoadkill = false;

		// Force a 100% baseline mortality (1.0) for males and females across all possible phases
		// Matrix dimensions: [species][phaseIndex][sexIndex 0=Male, 1=Female]
		parameters.baseBirthMort = new double[1][4][2]; 
		parameters.baseBirthMort[0][0][0] = -1.0;
		parameters.baseBirthMort[0][0][1] = -1.0;
		for (int phase = 1; phase < 4; phase++) {
			parameters.baseBirthMort[0][phase][0] = 1.0; // 100% Male Mortality
			parameters.baseBirthMort[0][phase][1] = 1.0; // 100% Female Mortality
		}

		// Initialize the non-spatial model constructor with controlled test parameters
		// Arguments: parameters, roadKm, nRoadkillVariations, nCores, maxProcessedIndividuals
		Model model = new Model(parameters, 0.0, 1, 1, 0);

		// Execute exactly 1 time-step of the model pipeline
		model.run(1);

		// Verify the outcome using your new getter
		// Index 0 is the starting population (100), Index 1 is the population after Step 1
		int populationAfterStepOne = model.getPopulationHistory(0).get(1);

		assertEquals(0, populationAfterStepOne, 
				"The population should be completely wiped out (0) after 1 step of 100% mortality.");
	}

	@Test
	public void testDoubleGrowth() throws Exception {
		boolean test = false;
		int populationAfterStepOne=0;
		//Due to randomness, tries twice 
		for (int attempt =0; attempt < 2; attempt++) {
			// Set up custom parameters
			int startingPopulation = 100;
			ParameterPackage parameters = new ParameterPackage();
			parameters.speciesNames = new String[]{"TestSpecies"};
			parameters.startPopValue = new int[]{startingPopulation}; // Start with 100 individuals
			parameters.lifePhases = new int[][]{{5, 24, 36}};
			parameters.matAge = new int[]{0};
			parameters.minLitSize = new int[]{1};
			parameters.maxLitSize = new int[]{-1};
			parameters.avgLitSize = new double[]{1};
			parameters.sexRatio = new double[]{0.05};
			parameters.minTimeBetweenBreeding = new int[]{0};
			parameters.avgTimeBetweenBreeding = new double[]{0};
			parameters.mateFindingRadius = new double[]{1000.0};
			parameters.disRan = new double[]{5.0};
			parameters.maxPopulation = new double[] {1000};
			parameters.roadkillPerKmPerYear = new double[][]{{0.0}}; // Turn off infrastructure effects for baseline isolation
			parameters.sweepingRoadkill = false;

			// Force a 0% baseline mortality (1.0) for males and females across all possible phases
			// Matrix dimensions: [species][phaseIndex][sexIndex 0=Male, 1=Female]
			parameters.baseBirthMort = new double[1][4][2]; 
			parameters.baseBirthMort[0][0][0] = -1.0;
			parameters.baseBirthMort[0][0][1] = -1.0;
			for (int phase = 1; phase < 4; phase++) {
				parameters.baseBirthMort[0][phase][0] = 0.0; // 0% Male Mortality
				parameters.baseBirthMort[0][phase][1] = 0.0; // 0% Female Mortality
			}

			// Initialize the non-spatial model constructor with controlled test parameters
			// Arguments: parameters, roadKm, nRoadkillVariations, nCores, maxProcessedIndividuals
			Model model = new Model(parameters, 0.0, 1, 1, 0);

			// Execute exactly 1 time-step of the model pipeline
			model.run(1);

			// Verify the outcome using your new getter
			// Index 0 is the starting population (100), Index 1 is the population after Step 1
			populationAfterStepOne = model.getPopulationHistory(0).get(1);
			test = (populationAfterStepOne>165 && populationAfterStepOne<235);
			if (test == true) {
				break;
			}
		}
		assertTrue(test,"The population should be close to double (200) after 1 step but was "+populationAfterStepOne+".");
	}

	@Test
	public void testDoubleGrowthWithCap() throws Exception {
		boolean test = false;
		int populationAfterStepOne=0;
		//Due to randomness, tries twice 
		for (int attempt =0; attempt < 2; attempt++) {
			// Set up custom parameters
			int startingPopulation = 100;
			ParameterPackage parameters = new ParameterPackage();
			parameters.speciesNames = new String[]{"TestSpecies"};
			parameters.startPopValue = new int[]{startingPopulation}; // Start with 100 individuals
			parameters.lifePhases = new int[][]{{5, 24, 36}};
			parameters.matAge = new int[]{0};
			parameters.minLitSize = new int[]{1};
			parameters.maxLitSize = new int[]{-1};
			parameters.avgLitSize = new double[]{1};
			parameters.sexRatio = new double[]{0.05};
			parameters.minTimeBetweenBreeding = new int[]{0};
			parameters.avgTimeBetweenBreeding = new double[]{0};
			parameters.mateFindingRadius = new double[]{1000.0};
			parameters.disRan = new double[]{5.0};
			parameters.maxPopulation = new double[] {1000};
			parameters.roadkillPerKmPerYear = new double[][]{{0.0}}; // Turn off infrastructure effects for baseline isolation
			parameters.sweepingRoadkill = false;

			// Force a 0% baseline mortality (1.0) for males and females across all possible phases
			// Matrix dimensions: [species][phaseIndex][sexIndex 0=Male, 1=Female]
			parameters.baseBirthMort = new double[1][4][2]; 
			parameters.baseBirthMort[0][0][0] = -1.0;
			parameters.baseBirthMort[0][0][1] = -1.0;
			for (int phase = 1; phase < 4; phase++) {
				parameters.baseBirthMort[0][phase][0] = 0.0; // 0% Male Mortality
				parameters.baseBirthMort[0][phase][1] = 0.0; // 0% Female Mortality
			}

			// Initialize the non-spatial model constructor with controlled test parameters
			// Arguments: parameters, roadKm, nRoadkillVariations, nCores, maxProcessedIndividuals
			Model model = new Model(parameters, 0.0, 1, 1, 50);

			// Execute exactly 1 time-step of the model pipeline
			model.run(1);

			// Verify the outcome using your new getter
			// Index 0 is the starting population (100), Index 1 is the population after Step 1
			populationAfterStepOne = model.getPopulationHistory(0).get(1);
			test = (populationAfterStepOne>165 && populationAfterStepOne<235);
			if (test == true) {
				break;
			}
		}

		assertTrue(test, 
				"The population should be close to double (200) after 1 step but was "+populationAfterStepOne+" with a processing cap of 50.");
	}


	@Test
	public void testSpatialTotalExtinctionIntegration() throws Exception {
		// Create the fake spatial environment (1x1 grid)
		// Constructor: name, ncols, nrows, xllcorner, yllcorner, cellsizeX, cellsizeY, nodatavalue
		// A cell size of 0.01 degrees is roughly 1 sq km at the equator
		Map envMap = new Map("Infrastructure", 1, 1, 0.0, 0.0, 0.01, 0.01, -9999);
		envMap.setValue(0.0, 0, 0); // Set road density to 0 in our single cell

		// Create the fake initial presence map for the species
		Map initPopMap = new Map("TestSpecies Presence", 1, 1, 0.0, 0.0, 0.01, 0.01, -9999);
		initPopMap.setValue(1.0, 0, 0); // Value must be > 0 so the cell isn't skipped during initialization
		Map[] initialPopulation = new Map[]{ initPopMap };

		// Set up parameters
		ParameterPackage parameters = new ParameterPackage(); 
		parameters.speciesNames = new String[]{"TestSpecies"};
		parameters.lifePhases = new int[][]{{12, 24, 36}};
		parameters.matAge = new int[]{5};
		parameters.minLitSize = new int[]{1};
		parameters.maxLitSize = new int[]{-1};
		parameters.avgLitSize = new double[]{2};
		parameters.sexRatio = new double[]{0.5};
		parameters.minTimeBetweenBreeding = new int[]{10};
		parameters.avgTimeBetweenBreeding = new double[]{10.0};
		parameters.mateFindingRadius = new double[]{1000.0};
		parameters.disRan = new double[]{5.0};
		parameters.roadkillPerKmPerYear = new double[][]{{0.0}}; 
		parameters.sweepingRoadkill = false;

		// SPATIAL-SPECIFIC PARAMETERS:
		// In the spatial model, startPopValue is ignored. Instead, individuals are generated
		// based on populationDensity * square kilometers of the cell.
		parameters.populationDensity = new double[]{100.0}; // 100 individuals per sq km
		parameters.maxPopulation = new double[]{1000.0};    // Max capacity per cell

		// Force a 100% baseline mortality (1.0) for males and females across all possible phases
		parameters.baseBirthMort = new double[1][5][2]; 
		for (int phase = 0; phase < 5; phase++) {
			parameters.baseBirthMort[0][phase][0] = 1.0; // 100% Male Mortality
			parameters.baseBirthMort[0][phase][1] = 1.0; // 100% Female Mortality
		}

		// Initialize the SPATIAL model constructor
		// Arguments: parameters, initialPopulation Map[], environment Map, nRoadkillVariations, nCores, maxProcessedIndividuals
		Model model = new Model(parameters, initialPopulation, envMap, 1, 1, 1000);

		// Optional sanity check: Verify that the density math actually spawned individuals at step 0
		int initialPopulationSize = model.getPopulationHistory(0).get(0);
		assertTrue(initialPopulationSize > 0, "The spatial initialization should have spawned individuals in cell 0,0.");

		// Execute exactly 1 time-step of the model pipeline
		model.run(1);

		// Verify the population was entirely wiped out by the 100% mortality rate
		int populationAfterStepOne = model.getPopulationHistory(0).get(1);
		assertEquals(0, populationAfterStepOne, 
				"The spatial population should be completely wiped out (0) after 1 step of 100% mortality.");
	}
	
	@Test
	public void testSpatialMovement() throws Exception {
		// Create the fake spatial environment (2x2 grid)
		// Constructor: name, ncols, nrows, xllcorner, yllcorner, cellsizeX, cellsizeY, nodatavalue
		// A cell size of 0.01 degrees is roughly 1 sq km at the equator
		Map envMap = new Map("Infrastructure", 2, 2, 0.0, 0.0, 0.01, 0.01, -9999);
		envMap.setValue(0.0, 0, 0); // Set road density to 0 all cells
		envMap.setValue(0.0, 1, 0); // Set road density to 0 all cells
		envMap.setValue(0.0, 0, 1); // Set road density to 0 all cells
		envMap.setValue(0.0, 1, 1); // Set road density to 0 all cells

		// Create the fake initial presence map for the species
		Map initPopMap = new Map("TestSpecies Presence", 2, 2, 0.0, 0.0, 0.01, 0.01, -9999);
		initPopMap.setValue(1.0, 0, 0); // Initializes the population on in one cell
		initPopMap.setValue(0.0, 1, 0); // Makes terrain available on other cells
		initPopMap.setValue(0.0, 0, 1); // Makes terrain available on other cells
		initPopMap.setValue(0.0, 1, 1); // Makes terrain available on other cells
		Map[] initialPopulation = new Map[]{ initPopMap };

		//Set up parameters
		ParameterPackage parameters = new ParameterPackage(); 
		parameters.speciesNames = new String[]{"TestSpecies"};
		parameters.lifePhases = new int[][]{{12, 24, 36}};
		parameters.matAge = new int[]{5};
		parameters.minLitSize = new int[]{1};
		parameters.maxLitSize = new int[]{-1};
		parameters.avgLitSize = new double[]{2};
		parameters.sexRatio = new double[]{0.5};
		parameters.minTimeBetweenBreeding = new int[]{10};
		parameters.avgTimeBetweenBreeding = new double[]{10.0};
		parameters.mateFindingRadius = new double[]{100.0};
		parameters.disRan = new double[]{0.0}; //No movement
		parameters.roadkillPerKmPerYear = new double[][]{{0.0}}; 
		parameters.sweepingRoadkill = false;

		// SPATIAL-SPECIFIC PARAMETERS:
		// In the spatial model, startPopValue is ignored. Instead, individuals are generated
		// based on populationDensity * square kilometers of the cell.
		parameters.populationDensity = new double[]{100.0}; // 100 individuals per sq km
		parameters.maxPopulation = new double[]{1000.0};    // Max capacity per cell

		// Force a 0% baseline mortality (0.0) for males and females across all possible phases
		parameters.baseBirthMort = new double[1][5][2]; 
		for (int phase = 0; phase < 5; phase++) {
			parameters.baseBirthMort[0][phase][0] = 0.0; // 0% Male Mortality
			parameters.baseBirthMort[0][phase][1] = 0.0; // 0% Female Mortality
		}

		// Initialize the SPATIAL model constructor
		// Arguments: parameters, initialPopulation Map[], environment Map, nRoadkillVariations, nCores, maxProcessedIndividuals
		Model model = new Model(parameters, initialPopulation, envMap, 1, 1, 1000);
		

		// Execute exactly 1 time-step of the model pipeline
		model.run(5);
		
		//Verify the population did not migrate		
		IndMap popMap = model.getPopulationMap(0);
		assertTrue(popMap.getCount(0, 0) > 0, "The spatial initialization should have spawned individuals in cell 0,0.");
		assertEquals(popMap.getCount(0, 1) , 0, "Individuals should not have moved from cell 0,0 to cell 0,1.");
		
		//Adjust to allow for movement
		parameters.disRan = new double[]{1000.0};
		 model = new Model(parameters, initialPopulation, envMap, 1, 1, 1000);
		// Execute exactly 1 time-step of the model pipeline
		model.run(5);
		
		//Verify the population did not migrate		
		popMap = model.getPopulationMap(0);
		assertTrue(popMap.getCount(0, 0) > 0, "The spatial initialization should have spawned individuals in cell 0,0.");
		assertTrue(popMap.getCount(0, 1) > 0, "Individuals should have moved from cell 0,0 to cell 0,1.");
		
	}
}