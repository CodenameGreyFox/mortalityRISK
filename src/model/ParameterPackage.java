package model;

import java.io.FileNotFoundException;
import java.io.IOException;

import gui.XLSreader;

/**
 * Class to store the parameters of the model
 */

public class ParameterPackage {

	public String[] speciesNames;
	private String[] noRoadVariationSpeciesNames;
	public int[][] lifePhases;	
	public double[] populationDensity;
	public double[] maxPopulation;
	public int[] matAge;
	public int[] minLitSize;
	public int[] maxLitSize;
	public double[] avgLitSize;
	public int[] minTimeBetweenBreeding;
	public double[] avgTimeBetweenBreeding;
	public double[] mateFindingRadius;
	public double[] disRan;
	public double[][][] baseBirthMort; //The base birth rate and mortality rate for each species - [0][species] Birth rate - [1][species] Mortality rate of first age group, [2][species] Mortality rate of second age group, etc , {male,female}
	public double[][] roadkillPerKmPerYear;
	public boolean sweepingRoadkill;
	public int[] startPopValue;
	public double[] sexRatio; //Male odds
	@SuppressWarnings("unused")
	private int nRoadkillVariations;

	private String[] originalSpeciesNames;

	/**
	 * Constructor to create an empty parameter package
	 */
	public ParameterPackage() {
		
	}
	/**
	 * Constructor for the class, which loads a XLSX file to retrieve the various parameters
	 * @param filePath
	 * @param nRoadkillVariations
	 * @param type
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public ParameterPackage(String filePath, int nRoadkillVariations, String type) throws FileNotFoundException, IOException {

		XLSreader xls = new XLSreader(filePath);

		//Checks if its a vertical or horizontal file
		if (xls.readCell(0, 1).contentEquals("Sex")) { //Vertical file
			//Do Vertical Load
			verticalLoad(xls, nRoadkillVariations, type);
		} else if (xls.readCell(1, 0).contentEquals("Sex")) { //Horizontal File
			//Do horizontal load			
			horizontalLoad(xls, nRoadkillVariations, type);
		} else {
			xls.close();
			throw new IllegalArgumentException("Invalid file format");
		}	

		xls.close();
	}




	/**
	 * Private method that loads an horizontal xls file
	 * 
	 * @param xls
	 * @param nRoadkillVariations
	 * @param type
	 * @throws FileNotFoundException
	 * @throws IOException
	 */

	private void horizontalLoad(XLSreader xls, int nRoadkillVariations, String type) throws FileNotFoundException, IOException {

		//Counts the number of species
		int col = 1;
		int speciesN = 0;
		while(!(xls.readCell(0,col).contentEquals("") && xls.readCell(0,col-1).contentEquals(""))) { //Stops at two consecutive blank names
			if(!(xls.readCell(0,col).contentEquals("") || xls.readCell(0,col).contentEquals(xls.readCell(0,col-1)))) { //Increases species count if not blank and not equal to previous
				speciesN++;
			}
			col++;
		}
		this.nRoadkillVariations = nRoadkillVariations;

		noRoadVariationSpeciesNames = new String[speciesN];

		speciesN = speciesN*(nRoadkillVariations+1);

		int currentTermRow = 0;		

		speciesNames = new String[speciesN];
		originalSpeciesNames = new String[speciesN];
		lifePhases = new int[speciesN][];	
		populationDensity = new double[speciesN];
		maxPopulation = new double[speciesN];
		matAge = new int[speciesN];
		minLitSize = new int[speciesN];
		maxLitSize = new int[speciesN];
		avgLitSize = new double[speciesN];
		minTimeBetweenBreeding = new int[speciesN];
		mateFindingRadius = new double[speciesN];
		disRan = new double[speciesN];
		baseBirthMort = new double[speciesN][][];
		roadkillPerKmPerYear = new double[speciesN][2];
		startPopValue = new int[speciesN];
		sexRatio = new double[speciesN];
		avgTimeBetweenBreeding = new double[speciesN];

		int columnToRead = 1;
		double roadkillAdjustment = 1;

		for (int species = 0; species < speciesN; species++) {

			//makes so it goes back to the start when all species have been read and then adjusts mortality
			if(xls.readCell(xls.getTermRow("Name","Species"),columnToRead).contentEquals("")) {
				columnToRead = 1;
				roadkillAdjustment = 1-((double)species)/((double)(speciesN-speciesN/(nRoadkillVariations+1)));
			}

			boolean differentSexes = (xls.readCell(xls.getTermRow("Sex"),columnToRead).contains("Male") ||xls.readCell(xls.getTermRow("Sex"),columnToRead).contains("Female"))  ;
			currentTermRow = xls.getTermRow("Species","Name");	

			speciesNames[species] = xls.readCell(currentTermRow,columnToRead) + (nRoadkillVariations > 0 ? (" "+String.format( "%.2f", roadkillAdjustment)):"");
			originalSpeciesNames[species] = xls.readCell(currentTermRow,columnToRead) ;

			if (species < noRoadVariationSpeciesNames.length) { //Gets only the original species names
				noRoadVariationSpeciesNames[species] = xls.readCell(currentTermRow,columnToRead) ;
			}


			currentTermRow = xls.getTermRow("Life Phase Change","Life Phases");			
			String [] temp = xls.readCell(currentTermRow,columnToRead).split(";");
			lifePhases[species]= new int[1];
			if (!temp[0].isEmpty()) {
				lifePhases[species]= new int[temp.length+1];		
				for ( int z = 0; z < temp.length; z ++) {
					lifePhases[species][z] = (int) Double.parseDouble(temp[z]);
				}						
			}
			currentTermRow = xls.getTermRow("Longevity");
			lifePhases[species][lifePhases[species].length-1] = (int) Double.parseDouble(xls.readCell(currentTermRow,columnToRead));

			currentTermRow = xls.getTermRow("Sex Ratio");		
			sexRatio[species] = Double.parseDouble(xls.readCell(currentTermRow,columnToRead));	


			currentTermRow = xls.getTermRow("Population Density", "Starting population", "Starting Population");		
			populationDensity[species] = Double.parseDouble(xls.readCell(currentTermRow,columnToRead));	
			startPopValue[species] = (int) Double.parseDouble(xls.readCell(currentTermRow,columnToRead));

			currentTermRow = xls.getTermRow("Maximum Population","Maximum Density");		
			maxPopulation[species] = Double.parseDouble(xls.readCell(currentTermRow,columnToRead));	

			currentTermRow = xls.getTermRow("Maturity Age","Age at First Birth");		
			matAge[species] = (int) Double.parseDouble(xls.readCell(currentTermRow,columnToRead));	

			try {
				currentTermRow = xls.getTermRow("Average Litter Size", "Average Offspring Number");		
				avgLitSize[species] = Double.parseDouble(xls.readCell(currentTermRow,columnToRead));
				minLitSize[species] = -1;
				maxLitSize[species] = -1;
			} catch (NumberFormatException | NullPointerException e) {
				avgLitSize[species] = -1;
				currentTermRow = xls.getTermRow("Minimum Litter Size", "Minimum Offspring Number");		
				minLitSize[species] = (int) Double.parseDouble(xls.readCell(currentTermRow,columnToRead));				
				currentTermRow = xls.getTermRow("Maximum Litter Size","Maximum Offspring Number");		
				maxLitSize[species] = (int) Double.parseDouble(xls.readCell(currentTermRow,columnToRead));
			}

			currentTermRow = xls.getTermRow("Min Interval Between Births","Min Interval Births");		
			minTimeBetweenBreeding[species] = (int) Double.parseDouble(xls.readCell(currentTermRow,columnToRead));	


			if (type.contentEquals("Spatial")) {
				currentTermRow = xls.getTermRow("Mate Finding Radius (m)","Mate Finding Radius");		
				mateFindingRadius[species] = Double.parseDouble(xls.readCell(currentTermRow,columnToRead));

				currentTermRow = xls.getTermRow("Max Dispersal Length","Dispersal Range / Iteration");		
				disRan[species] = Double.parseDouble(xls.readCell(currentTermRow,columnToRead));	

			} else {
				mateFindingRadius[species] = 0.0;	

				disRan[species] = 0.0;	

			}

			baseBirthMort[species] = new double[lifePhases[species].length+1][2];
			try {
				currentTermRow = xls.getTermRow("Birth Probability/Iteration","Birth Rate");
				baseBirthMort[species][0][0] = Double.parseDouble(xls.readCell(currentTermRow,columnToRead)); //male
				baseBirthMort[species][0][1] = Double.parseDouble(xls.readCell(currentTermRow,columnToRead)); //female
			} catch (NumberFormatException | NullPointerException e) {
				currentTermRow = xls.getTermRow("Average Interval Between Births","Avg Interval Between Births","Avg Interval Births");		
				avgTimeBetweenBreeding[species] = Double.parseDouble(xls.readCell(currentTermRow,columnToRead));
				//	double birthProbPerIt = 1-Math.pow(0.5,1/(avgTimeBetweenBreeding[species] - minTimeBetweenBreeding[species]));
				baseBirthMort[species][0][0] = -1; //male
				baseBirthMort[species][0][1] = -1; //female
			}


			try {
				currentTermRow = xls.getTermRow("Mortality/Iteration","Death Probability / Iteration");
				//male
				temp = xls.readCell(currentTermRow,columnToRead).split(";");
				for ( int z = 0; z < temp.length; z ++) { 
					baseBirthMort[species][z+1][0] = Double.parseDouble(temp[z]);
				}	
				//female
				temp = xls.readCell(currentTermRow, differentSexes? columnToRead+1 : columnToRead ).split(";");
				for ( int z = 0; z < temp.length; z ++) { 
					baseBirthMort[species][z+1][1] = Double.parseDouble(temp[z]);
				}	
			} catch (NumberFormatException | NullPointerException e) {
				currentTermRow = xls.getTermRow("Survival/Iteration","Survival Probability / Iteration","Survival Rate");
				//male
				temp = xls.readCell(currentTermRow,columnToRead).split(";");
				for ( int z = 0; z < temp.length; z ++) { 
					baseBirthMort[species][z+1][0] = 1-Double.parseDouble(temp[z]);
				}	
				//female
				temp = xls.readCell(currentTermRow, differentSexes? columnToRead+1 : columnToRead ).split(";");
				for ( int z = 0; z < temp.length; z ++) { 
					baseBirthMort[species][z+1][1] = 1-Double.parseDouble(temp[z]);
				}	
			}

			currentTermRow = xls.getTermRow("Roadkill Rate","Roadkill/Km/Year","Infrastructure-Induced Mortality","Infras.-Induced Mortality");		
			roadkillPerKmPerYear[species][0] = Double.parseDouble(xls.readCell(currentTermRow,columnToRead))*roadkillAdjustment;	//male
			roadkillPerKmPerYear[species][1] = Double.parseDouble(xls.readCell(currentTermRow,differentSexes? columnToRead+1 : columnToRead))*roadkillAdjustment;	//female

			if (differentSexes) {
				columnToRead = columnToRead + 2;
			} else {
				columnToRead = columnToRead + 1;	
			}

		}
	}

	/**
	 * Private method that loads a vertical xls file
	 * @param xls
	 * @param nRoadkillVariations
	 * @param type
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void verticalLoad(XLSreader xls, int nRoadkillVariations, String type) throws FileNotFoundException, IOException {

		//Counts the number of species
		int row = 1;
		int speciesN = 0;
		try {
			while(!(xls.readCell(row,0).contentEquals("") && xls.readCell(row-1,0).contentEquals(""))) { //Stops at two consecutive blank names
				if(!(xls.readCell(row,0).contentEquals("") || xls.readCell(row,0).contentEquals(xls.readCell(row-1,0)))) { //Increases species count if not blank and not equal to previous
					speciesN++;
				}
				row++;
			}
		}catch (Exception e) {
			//Move on
		}


		this.nRoadkillVariations = nRoadkillVariations;

		noRoadVariationSpeciesNames = new String[speciesN];

		speciesN = speciesN*(nRoadkillVariations+1);

		int currentTermCol = 0;		

		speciesNames = new String[speciesN];
		originalSpeciesNames = new String[speciesN];
		lifePhases = new int[speciesN][];	
		populationDensity = new double[speciesN];
		maxPopulation = new double[speciesN];
		matAge = new int[speciesN];
		minLitSize = new int[speciesN];
		maxLitSize = new int[speciesN];
		avgLitSize = new double[speciesN];
		minTimeBetweenBreeding = new int[speciesN];
		mateFindingRadius = new double[speciesN];
		disRan = new double[speciesN];
		baseBirthMort = new double[speciesN][][];
		roadkillPerKmPerYear = new double[speciesN][2];
		startPopValue = new int[speciesN];
		sexRatio = new double[speciesN];
		avgTimeBetweenBreeding = new double[speciesN];

		int rowToRead = 1;
		double roadkillAdjustment = 1;

		for (int species = 0; species < speciesN; species++) {

			//makes so it goes back to the start when all species have been read and then adjusts mortality
			try {
				xls.readCell(rowToRead,xls.getTermCol("Name","Species"));
			} catch (NullPointerException e) {
				rowToRead = 1;
				roadkillAdjustment = 1-((double)species)/((double)(speciesN-speciesN/(nRoadkillVariations+1)));
			}

			boolean differentSexes = (xls.readCell(rowToRead,xls.getTermCol("Sex")).contains("Male") ||xls.readCell(rowToRead,xls.getTermCol("Sex")).contains("Female"))  ;
			currentTermCol = xls.getTermCol("Species","Name");	

			speciesNames[species] = xls.readCell(rowToRead,currentTermCol) + (nRoadkillVariations > 0 ? (" "+String.format( "%.2f", roadkillAdjustment)):"");
			originalSpeciesNames[species] = xls.readCell(rowToRead,currentTermCol) ;

			if (species < noRoadVariationSpeciesNames.length) { //Gets only the original species names
				noRoadVariationSpeciesNames[species] = xls.readCell(rowToRead,currentTermCol) ;
			}


			currentTermCol = xls.getTermCol("Life Phase Change","Life Phases");	
			String [] temp = xls.readCell(rowToRead,currentTermCol).split(";");
			lifePhases[species]= new int[1];
			if (!temp[0].isEmpty()) {
				lifePhases[species]= new int[temp.length+1];		
				for ( int z = 0; z < temp.length; z ++) {
					lifePhases[species][z] = (int) Double.parseDouble(temp[z]);
				}						
			}
			currentTermCol = xls.getTermCol("Longevity");
			lifePhases[species][lifePhases[species].length-1] = (int) Double.parseDouble(xls.readCell(rowToRead,currentTermCol));

			currentTermCol = xls.getTermCol("Sex Ratio");		
			sexRatio[species] = Double.parseDouble(xls.readCell(rowToRead,currentTermCol));	


			currentTermCol = xls.getTermCol("Population Density", "Starting population", "Starting Population");		
			populationDensity[species] = Double.parseDouble(xls.readCell(rowToRead,currentTermCol));	
			startPopValue[species] = (int) Double.parseDouble(xls.readCell(rowToRead,currentTermCol));

			currentTermCol = xls.getTermCol("Maximum Population","Maximum Density");		
			maxPopulation[species] = Double.parseDouble(xls.readCell(rowToRead,currentTermCol));	

			currentTermCol = xls.getTermCol("Maturity Age","Age at First Birth");		
			matAge[species] = (int) Double.parseDouble(xls.readCell(rowToRead,currentTermCol));	

			try {
				currentTermCol = xls.getTermCol("Average Litter Size","Average Offspring Number");		
				avgLitSize[species] = Double.parseDouble(xls.readCell(rowToRead,currentTermCol));
				minLitSize[species] = -1;
				maxLitSize[species] = -1;
			} catch (NullPointerException| IllegalArgumentException  e) {
				avgLitSize[species] = -1;
				currentTermCol = xls.getTermCol("Minimum Litter Size","Minimum Offspring Number");		
				minLitSize[species] = (int) Double.parseDouble(xls.readCell(rowToRead,currentTermCol));				
				currentTermCol = xls.getTermCol("Maximum Litter Size","Maximum Offspring Number");		
				maxLitSize[species] = (int) Double.parseDouble(xls.readCell(rowToRead,currentTermCol));
			}

			currentTermCol = xls.getTermCol("Min Interval Between Births","Min Interval Births");		
			minTimeBetweenBreeding[species] = (int) Double.parseDouble(xls.readCell(rowToRead,currentTermCol));	


			if (type.contentEquals("Spatial")) {
				currentTermCol = xls.getTermCol("Mate Finding Radius (m)","Mate Finding Radius");		
				mateFindingRadius[species] = Double.parseDouble(xls.readCell(rowToRead,currentTermCol));

				currentTermCol = xls.getTermCol("Max Dispersal Length","Dispersal Range / Iteration");		
				disRan[species] = Double.parseDouble(xls.readCell(rowToRead,currentTermCol));	

			} else {
				mateFindingRadius[species] = 0.0;	

				disRan[species] = 0.0;	

			}

			baseBirthMort[species] = new double[lifePhases[species].length+1][2];
			try {
				currentTermCol = xls.getTermCol("Birth Probability/Iteration","Birth Rate");
				baseBirthMort[species][0][0] = Double.parseDouble(xls.readCell(rowToRead,currentTermCol)); //male
				baseBirthMort[species][0][1] = Double.parseDouble(xls.readCell(rowToRead,currentTermCol)); //female
			} catch (NullPointerException | IllegalArgumentException e) {
				currentTermCol = xls.getTermCol("Average Interval Between Births","Avg Interval Between Births","Avg Interval Births");		
				avgTimeBetweenBreeding[species] = Double.parseDouble(xls.readCell(rowToRead,currentTermCol));
				//	double birthProbPerIt = 1-Math.pow(0.5,1/(avgTimeBetweenBreeding[species] - minTimeBetweenBreeding[species]));
				baseBirthMort[species][0][0] = -1; //male
				baseBirthMort[species][0][1] = -1; //female
			}


			try {
				currentTermCol = xls.getTermCol("Mortality/Iteration","Death Probability / Iteration","Mortality Rate");
				//male
				temp = xls.readCell(rowToRead,currentTermCol).split(";");
				for ( int z = 0; z < temp.length; z ++) { 
					baseBirthMort[species][z+1][0] = Double.parseDouble(temp[z]);
				}	
				//female
				temp = xls.readCell(differentSexes? rowToRead+1 : rowToRead ,currentTermCol).split(";");
				for ( int z = 0; z < temp.length; z ++) { 
					baseBirthMort[species][z+1][1] = Double.parseDouble(temp[z]);
				}	
			} catch (NullPointerException | IllegalArgumentException e) {
				currentTermCol = xls.getTermCol("Survival/Iteration","Survival Probability / Iteration","Survival Rate");
				//male
				temp = xls.readCell(rowToRead,currentTermCol).split(";");
				for ( int z = 0; z < temp.length; z ++) { 
					baseBirthMort[species][z+1][0] = 1-Double.parseDouble(temp[z]);
				}	
				//female
				temp = xls.readCell(differentSexes? rowToRead+1 : rowToRead ,currentTermCol).split(";");
				for ( int z = 0; z < temp.length; z ++) { 
					baseBirthMort[species][z+1][1] = 1-Double.parseDouble(temp[z]);
				}	
			}

			currentTermCol = xls.getTermCol("Roadkill Rate","Roadkill/Km/Year","Infrastructure-Induced Mortality","Infras.-Induced Mortality");		
			roadkillPerKmPerYear[species][0] = Double.parseDouble(xls.readCell(rowToRead,currentTermCol))*roadkillAdjustment;	//male
			roadkillPerKmPerYear[species][1] = Double.parseDouble(xls.readCell(differentSexes? rowToRead+1 : rowToRead ,currentTermCol))*roadkillAdjustment;	//female

			if (differentSexes) {
				rowToRead = rowToRead + 2;
			} else {
				rowToRead = rowToRead + 1;	
			}

		}
	}

	/**
	 * 
	 * Returns the species names, ignoring roadkill variations
	 * 
	 * @param includeRoadVariations Returns the species names of all road variations if true, or just the original species if false
	 * @return
	 */

	public String[] getOriginalSpeciesNames(boolean includeRoadVariations) {
		if (includeRoadVariations) {
			return originalSpeciesNames;
		} else {
			return noRoadVariationSpeciesNames;
		}
	}

	/**
	 * Returns the index of the provided species
	 * @param speciesName
	 * @return
	 */
	public int getSpeciesIndex(String speciesName) {
		for (int i = 0; i < noRoadVariationSpeciesNames.length; i++ ) {
			if (noRoadVariationSpeciesNames[i].contentEquals(speciesName)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Updates the package based on the parameter window, currently not in use
 */
	//TODO bug check
	public void updatePackage() {
/**
		for (int species = 0; species < noRoadVariationSpeciesNames.length; species ++) {
			double birthProbPerIt = 1-Math.pow(0.5,1/(avgTimeBetweenBreeding[species] - minTimeBetweenBreeding[species]));
			baseBirthMort[species][0][0] = birthProbPerIt; //male
			baseBirthMort[species][0][1] = birthProbPerIt; //female
		}

		int variationStep = noRoadVariationSpeciesNames.length;
		for(int variation = 1; variation < nRoadkillVariations+1; variation ++) {
			
			for (int species = 0; species < noRoadVariationSpeciesNames.length; species ++) {

				lifePhases[species+variationStep*variation] = lifePhases[species];					
				populationDensity[species+variationStep*variation] = populationDensity[species];
				maxPopulation[species+variationStep*variation] = maxPopulation[species];
				matAge[species+variationStep*variation] = matAge[species];
				avgLitSize[species+variationStep*variation] = avgLitSize[species];
				minLitSize[species+variationStep*variation] = minLitSize[species];
				maxLitSize[species+variationStep*variation] = maxLitSize[species];				
				minTimeBetweenBreeding[species+variationStep*variation] = minTimeBetweenBreeding[species];
				avgTimeBetweenBreeding[species+variationStep*variation] = avgTimeBetweenBreeding[species];
				mateFindingRadius[species+variationStep*variation] = mateFindingRadius[species];
				disRan[species+variationStep*variation] = disRan[species];
				baseBirthMort[species+variationStep*variation] = baseBirthMort[species];				
				startPopValue[species+variationStep*variation] = startPopValue[species];
				sexRatio[species+variationStep*variation] = sexRatio[species]; //Male odds

				double roadkillAdjustment = 1-((double)(species+variationStep*variation))/((double)(speciesNames.length-speciesNames.length/(nRoadkillVariations+1)));
				roadkillPerKmPerYear[species+variationStep*variation][0] = roadkillPerKmPerYear[species][0]*roadkillAdjustment;
				roadkillPerKmPerYear[species+variationStep*variation][1] = roadkillPerKmPerYear[species][1]*roadkillAdjustment;
			}
		}
			 */
	}

	
	/**
	 * Saves to file
	 * @param filePath
	 * @param type
	 * @throws IOException
	 */
	public void saveToFile(String filePath, String type) throws IOException {

		//Checks if there are differences in sex
		boolean sexDif = false;		
		for (int i = 0; i< roadkillPerKmPerYear.length; i++) {
			if (roadkillPerKmPerYear[i][0] != roadkillPerKmPerYear[i][1]) {
				sexDif = true;
				break;
			}
		}
		if (!sexDif) {
			for (int i = 0; i< baseBirthMort.length; i++) {
				for (int z = 0; z< baseBirthMort[i].length; z++) {
					if (baseBirthMort[i][z][0] != baseBirthMort[i][z][1]) {
						sexDif = true;
						break;
					}
				}
				if(sexDif) {
					break;
				}
			}
		}

		//Creates the template and then writes on it
		XLSreader.createTemplate(filePath, noRoadVariationSpeciesNames.length,  sexDif, type, false);
		XLSreader xls = new XLSreader(filePath);

		int totalColumns = noRoadVariationSpeciesNames.length * (sexDif?2:1);
		for (int columnToWrite = 1 ; columnToWrite < totalColumns+1; columnToWrite++) {
			int species = (int) (Math.ceil(columnToWrite/((double)(sexDif?2:1)))-1);

			int currentTermRow = xls.getTermRow("Species","Name");	
			xls.writeCell(currentTermRow,columnToWrite,noRoadVariationSpeciesNames[species]);	

			currentTermRow = xls.getTermRow("Life Phase Change","Life Phases");	
			String toWrite = "";
			for(int z = 0; z < lifePhases[species].length-1; z++) {
				toWrite += lifePhases[species][z] +";";
			}
			if (toWrite.length()>0) {
				toWrite = toWrite.substring(0,toWrite.length()-1);
			}				
			xls.writeCell(currentTermRow,columnToWrite,toWrite);

			currentTermRow = xls.getTermRow("Longevity");
			xls.writeCell(currentTermRow,columnToWrite,Integer.toString(lifePhases[species][lifePhases[species].length-1]));

			currentTermRow = xls.getTermRow("Sex Ratio");	
			xls.writeCell(currentTermRow,columnToWrite, Double.toString(sexRatio[species]));	
			if (sexDif) {
				xls.writeCell(currentTermRow,columnToWrite+1, Double.toString(1-sexRatio[species]));
			}
			
			currentTermRow = xls.getTermRow("Population Density", "Starting Population", "Starting population");
			if(type.contentEquals("Spatial")) {
				xls.writeCell(currentTermRow,columnToWrite,Double.toString(populationDensity[species] ));	
			} else {
				xls.writeCell(currentTermRow,columnToWrite,Double.toString(startPopValue[species] ));
			}

			currentTermRow = xls.getTermRow("Maximum Population","Maximum Density");		
			xls.writeCell(currentTermRow,columnToWrite,Double.toString(maxPopulation[species] ));	

			currentTermRow = xls.getTermRow("Age at First Birth","Maturity Age");	
			xls.writeCell(currentTermRow,columnToWrite,Integer.toString(matAge[species] ));	

			
			if (minLitSize[species] == -1) { 
				currentTermRow = xls.getTermRow("Average Litter Size","Average Offspring Number");		
				xls.writeCell(currentTermRow,columnToWrite,Double.toString(avgLitSize[species]));	
			} else {
				currentTermRow = xls.getTermRow("Minimum Litter Size","Minimum Offspring Number");		
				xls.writeCell(currentTermRow,columnToWrite,Integer.toString(minLitSize[species]));	

				currentTermRow = xls.getTermRow("Maximum Litter Size","Maximum Offspring Number");		
				xls.writeCell(currentTermRow,columnToWrite,Integer.toString(maxLitSize[species]));	
			}

			currentTermRow = xls.getTermRow("Min Interval Between Births","Min Interval Births");	
			xls.writeCell(currentTermRow,columnToWrite,Integer.toString(minTimeBetweenBreeding[species] ));	
			
			if (baseBirthMort[species][0][0] == -1) {
				currentTermRow = xls.getTermRow("Average Interval Between Births","Avg Interval Between Births","Avg Interval Births");		
				xls.writeCell(currentTermRow,columnToWrite,Double.toString(avgTimeBetweenBreeding[species] ));	
			} else {
				currentTermRow = xls.getTermRow("Birth Probability/Iteration","Birth Rate");
				xls.writeCell(currentTermRow,columnToWrite,Double.toString(baseBirthMort[species][0][0] ));
			}					

			if (type.contentEquals("Spatial")) {
				currentTermRow = xls.getTermRow("Mate Finding Radius (m)","Mate Finding Radius");		
				xls.writeCell(currentTermRow,columnToWrite,Double.toString(mateFindingRadius[species] ));	

				currentTermRow = xls.getTermRow("Max Dispersal Length","Dispersal Range / Iteration");		
				xls.writeCell(currentTermRow,columnToWrite,Double.toString(disRan[species] ));	
			}
			
			
			currentTermRow = xls.getTermRow("Survival/Iteration","Survival Probability / Iteration","Survival Rate");
			toWrite = "";
			for(int z = 1; z < baseBirthMort[species].length; z++) {
				toWrite += (1-baseBirthMort[species][z][0]) +";";
			}
			if (toWrite.length()>0) {
				toWrite = toWrite.substring(0,toWrite.length()-1);
			}		
			xls.writeCell(currentTermRow,columnToWrite,toWrite);
			if (sexDif) {
				toWrite = "";
				for(int z = 1; z < baseBirthMort[species].length; z++) {
					toWrite += (1-baseBirthMort[species][z][1]) +";";
				}
				if (toWrite.length()>0) {
					toWrite = toWrite.substring(0,toWrite.length()-1);
				}		
				xls.writeCell(currentTermRow,columnToWrite+1,toWrite);	
			}

			currentTermRow = xls.getTermRow("Average Interval Between Births","Avg Interval Between Births","Avg Interval Births");	
			xls.writeCell(currentTermRow,columnToWrite,Double.toString(avgTimeBetweenBreeding[species]));	



			currentTermRow = xls.getTermRow("Roadkill Rate","Roadkill / Km / Year","Infras.-Induced Mortality","Infrastructure-Induced Mortality");	
			xls.writeCell(currentTermRow,columnToWrite,Double.toString(roadkillPerKmPerYear[species][0]));	
			if (sexDif) {
				xls.writeCell(currentTermRow,columnToWrite+1,Double.toString(roadkillPerKmPerYear[species][1]));	
			}
			if (sexDif) {
				columnToWrite++;	
			}
		}

		xls.close();


	}

	/**
	 * Sets up a roadkill sweep by giving absolute roadkill percentage
	 * @param roadkillPercentage
	 */

	public void setUpRoadkillSweep(double[][] roadkillPercentage) {
		this.roadkillPerKmPerYear = roadkillPercentage;
		sweepingRoadkill = true;

	}

}

