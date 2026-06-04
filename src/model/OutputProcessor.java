package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;

import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

import graphics.DeviationGrapher;
import graphics.StatisticalBarChart;

/**
 * Class that processes the results and provides csv and images.
 */

public class OutputProcessor {


	/**
	 * Processes the results from the model to provide csv files and graphs
	 * 
	 * @param resultsRepeated Array for each species of a list for the various runs
	 * @param workDirectory
	 * @param speciesNames
	 * @param nRoadVariations
	 * @param section
	 * @param minPersistenceThreshold - percentage (0-1) of runs that led to extinction for it to be considered an extinction
	 */
	static public void process( String[][] resultsRepeated, Map[][][] resultsExtinctionRepeated, String workDirectory, String[] speciesNames, String type, double minPersistenceThreshold, ParameterPackage parameters, int[] iterations, int repetitions, String command ) {
		process( resultsRepeated, resultsExtinctionRepeated, null,  workDirectory, speciesNames, type, minPersistenceThreshold, 0,parameters,null,null,1,0,1, iterations, repetitions, command ) ;
	}
	
	/**
	 * Processes the results from the model to provide csv files and graphs, with sweep information
	 * @param resultsRepeated Array for each species of a list for the various runs
	 * @param workDirectory
	 * @param speciesNames
	 * @param nRoadVariations
	 * @param section
	 */
	static public void process( String[][] resultsRepeated, Map[][][] resultsExtinctionRepeated, Map[] roadMortality, String workDirectory, String[] speciesNames, String type,
			double minPersistenceThreshold, double nRoadVariations, ParameterPackage parameters,String dateString,String timeUnit, int sweepRes, double sweepMin, double sweepMax, int[] iterations, int repetitions, String command) {

		//Formats the date
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		LocalDate date = null;
		try {
			date = LocalDate.parse((String) dateString, formatter);
		} catch (DateTimeParseException e) {

		}

		//Creates the folder name
		int[] trueIndexes = new int[(int)(speciesNames.length/(nRoadVariations+1))];
		for (int i = 0; i < trueIndexes.length; i++) {
			trueIndexes[i] = (int)(i);
		}

		String spNames = "";		
		for (int i = 0; i < trueIndexes.length; i++) {
			if (nRoadVariations == 0) {
				spNames += speciesNames[trueIndexes[i]] + " - ";
			} else {
				spNames += speciesNames[trueIndexes[i]].substring(0, speciesNames[i].lastIndexOf(" ")) + " - "; 
			}
		}

		spNames= (String) spNames.subSequence(0, spNames.length()-3);		
		Date currentDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String currentDateTime = dateFormat.format(currentDate);

		String fileName = "Model - "+spNames+ " - " + currentDateTime;

		File resultsFolder = Path.of(workDirectory, fileName).toFile();//new File(workDirectory+ fileName);
		int fileNumber = 0;
		while(true) {
			if (resultsFolder.mkdirs()) {
				break;
			} else {
				fileNumber++;
				if (fileNumber > 1000 ) { 	//Safeguard for file names that are too long
					fileName =  "Model "+spNames;
					String newFileName = "Model ";
					for(int sI = 0; sI < fileName.length(); sI ++) {
						if (fileName.charAt(sI) == ' ') {							
							newFileName += fileName.charAt(sI+1); 
						}						
					}
					newFileName += " " + currentDateTime;
					if (fileNumber == 1001) {
						fileName = newFileName;
					} else {
						int fileNumberTemp = fileNumber - 1000;
						fileName = newFileName + "("+fileNumberTemp +")";
					}
				} else {				 
					fileName =  "Model "+spNames+ " " + currentDateTime + "("+fileNumber +")";
				}

				resultsFolder = Path.of(workDirectory, fileName).toFile();

			}

		}

		Path filePath = Paths.get(resultsFolder.toString(), "command.txt");
		try {
			Files.writeString(filePath, command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Processes each cut
		for (int cut = 0; cut < iterations.length; cut++) {
			//Creates a folder with the iterations
			resultsFolder = Path.of(workDirectory, fileName,iterations[cut] + " Iterations "+ repetitions + " repetitions").toFile();
			resultsFolder.mkdir();

			try {
				parameters.saveToFile(Path.of(resultsFolder.toString(),"Parameters.xlsx").toString(),type);
			} catch (IOException e) {
				e.printStackTrace();
			}	

			if (sweepRes >1) {
				processSweep(resultsRepeated, resultsFolder.toString(), speciesNames, nRoadVariations, date, timeUnit, sweepRes, sweepMin, sweepMax, iterations[cut] );
			} else {
				processData(resultsRepeated, resultsFolder.toString(), speciesNames, nRoadVariations, date, timeUnit, iterations[cut] );
			}
			if(type.contentEquals("Spatial")) {
				processMaps( resultsExtinctionRepeated[cut],roadMortality ,resultsFolder.toString(), speciesNames, minPersistenceThreshold ,nRoadVariations);
			}
		}
	}


	static private void processData( String[][] resultsRepeated, String workDirectory, String[] speciesNames, double nRoadVariations, LocalDate date, String timeUnit, int maxIteration) {
		//Gets the index value of the true road size - ALWAYS 0 AS OF NOW
		int[] trueIndexes = new int[(int)(speciesNames.length/(nRoadVariations+1))];
		for (int i = 0; i < trueIndexes.length; i++) {
			trueIndexes[i] = (int)(i);
		}

		//Calculate the odds of survival per month
		String[] survivalPercentage = new String[resultsRepeated[0].length];


		double[][] averageForGraph = new double[resultsRepeated[0].length][];
		double[][] maxForGraph = new double[resultsRepeated[0].length][];
		double[][] minForGraph = new double[resultsRepeated[0].length][];
		double[] finalSurvivalRateEachRoad = new double[resultsRepeated[0].length];

		for ( int species = 0; species < resultsRepeated[0].length; species++) {

			survivalPercentage[species]= "";

			String[][] tempSplit = new  String[resultsRepeated.length][resultsRepeated[0][species].split(",").length]; 
			String[][] split = new  String[resultsRepeated.length][maxIteration];
			for (int i = 0 ; i < resultsRepeated.length; i++) {
				tempSplit[i] = resultsRepeated[i][species].split(",");
				//reduces the split until the number of desired iterations
				for(int y = 0; y < maxIteration; y++ ) {
					split[i][y] = tempSplit[i][y];
				}
			}

			averageForGraph[species] = new double[split[0].length];
			maxForGraph[species] = new double[split[0].length];
			minForGraph[species] = new double[split[0].length];
			double[][] allValues = new double[split[0].length][split.length];

			for (int i = 0 ; i < split[0].length ; i++ ) {
				double sum = 0;


				for (int z = 0 ; z < split.length ; z++ ) {
					double currentValue = Double.parseDouble(split[z][i]) ;
					allValues[i][z] = Double.parseDouble(split[z][i]);
					sum += currentValue > 0 ? 1:0;
					averageForGraph[species][i] += currentValue;
				}
				averageForGraph[species][i] = averageForGraph[species][i]/split.length;
				survivalPercentage[species] += sum/split.length*100 +"%,";

				if (i == split[0].length-1) {
					finalSurvivalRateEachRoad[species] =  sum/split.length*100;
				}
				Arrays.sort(allValues[i]);
				maxForGraph[species][i] = allValues[i][(int)Math.ceil((allValues[i].length-1)*0.975)]; //Grabs the top 97.5% Percentile
				minForGraph[species][i] = allValues[i][(int)((allValues[i].length-1)*0.025)];//Grabs the bottom 2.5% Percentile

			}

		}

		//Creates a graph of only the true roads
		YIntervalSeriesCollection dataset2 = new YIntervalSeriesCollection();
		for ( int species = 0; species < trueIndexes.length; species++) {
			YIntervalSeries series2 = new YIntervalSeries(speciesNames[trueIndexes[species]]);
			for (int i = 0; i < averageForGraph[trueIndexes[species]].length; i++) {


				series2.add(i, averageForGraph[trueIndexes[species]][i], minForGraph[trueIndexes[species]][i], maxForGraph[trueIndexes[species]][i]);
			}
			System.out.println("Added Species "+species);

			dataset2.addSeries(series2);
		}

		if (date != null) {
			String[] dates = new String[averageForGraph[trueIndexes[0]].length];
			LocalDate currentDate = date;
			for (int i = 0; i < averageForGraph[trueIndexes[0]].length; i++) {
				currentDate = advanceDate(1,currentDate, timeUnit);
				dates[i] = currentDate.format(DateTimeFormatter.ofPattern(dateFormat(timeUnit)));
			}
			DeviationGrapher.graph(dataset2, Path.of(workDirectory, "Population Time Series No Variations.png").toFile() ,"Population Time Series" ,"Number of Individuals" ,"Date" ,dates, true);
		} else {
			DeviationGrapher.graph(dataset2, Path.of(workDirectory, "Population Time Series No Variations.png").toFile(), "Population Time Series" ,"Number of Individuals" ,"Time", true );

		}


		//Only if there are multiple road variations
		if (nRoadVariations > 0) {
			//Creates a graph of all road steps
			YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
			for ( int species = 0; species < resultsRepeated[0].length; species++) {
				YIntervalSeries series1 = new YIntervalSeries(speciesNames[species]);
				for (int i = 0; i < averageForGraph[species].length; i++) {
					series1.add(i, averageForGraph[species][i], minForGraph[species][i], maxForGraph[species][i]);
				}
				dataset.addSeries(series1);
			}
			//	DeviationGrapher.graph(dataset);
			if (date != null) {
				String[] dates = new String[averageForGraph[trueIndexes[0]].length];
				LocalDate currentDate = date;
				for (int i = 0; i < averageForGraph[trueIndexes[0]].length; i++) {
					currentDate = advanceDate(1,currentDate, timeUnit);
					dates[i] = currentDate.format(DateTimeFormatter.ofPattern(dateFormat(timeUnit)));
				}
				DeviationGrapher.graph(dataset, Path.of(workDirectory,"Population Time Series.png").toFile(), "Population Time Series" ,"Number of Individuals" ,"Date" ,dates, true);
			} else {
				DeviationGrapher.graph(dataset, Path.of(workDirectory,"Population Time Series.png").toFile(), "Population Time Series" ,"Number of Individuals" ,"Time", true );		
			}



			//Creates a graph of the percentage of death with road size

			//[(int)(species-(nRoadVariations+1)*Math.floor(species/(nRoadVariations+1)))] = 1;
			DefaultStatisticalCategoryDataset defaultstatisticalcategorydataset = new DefaultStatisticalCategoryDataset();

			for (int i = finalSurvivalRateEachRoad.length-1; i >= 0; i --) {
				defaultstatisticalcategorydataset.add(100-finalSurvivalRateEachRoad[i], 0, speciesNames[i].substring(0, speciesNames[i].lastIndexOf(" ")), speciesNames[i].substring(speciesNames[i].lastIndexOf(" ")));
			}

			StatisticalBarChart.graph(defaultstatisticalcategorydataset, Path.of(workDirectory,"Survival Rate Over Road Density.png").toFile());

			///Creates a population time series graph for each species and road variations
			for (int speciesN = 0; speciesN<trueIndexes.length; speciesN++) {				
				//Creates a graph of all road steps
				dataset = new YIntervalSeriesCollection();

				for ( int roadVar = 0; roadVar < nRoadVariations+1; roadVar++) {
					YIntervalSeries series1 = new YIntervalSeries(speciesNames[speciesN+roadVar*trueIndexes.length]);
					for (int i = 0; i < averageForGraph[speciesN+roadVar*trueIndexes.length].length; i++) {
						series1.add(i, averageForGraph[speciesN+roadVar*trueIndexes.length][i], minForGraph[speciesN+roadVar*trueIndexes.length][i], maxForGraph[speciesN+roadVar*trueIndexes.length][i]);
					}
					dataset.addSeries(series1);
				}
				//	DeviationGrapher.graph(dataset);
				if (date != null) {
					String[] dates = new String[averageForGraph[trueIndexes[0]].length];
					LocalDate currentDate = date;
					for (int i = 0; i < averageForGraph[trueIndexes[0]].length; i++) {
						currentDate = advanceDate(1,currentDate, timeUnit);
						dates[i] = currentDate.format(DateTimeFormatter.ofPattern(dateFormat(timeUnit)));
					}
					DeviationGrapher.graph(dataset, Path.of(workDirectory,speciesNames[speciesN] +" Population Time Series.png").toFile(), "Population Time Series" ,"Number of Individuals" ,"Date" ,dates, true);
				} else {
					DeviationGrapher.graph(dataset, Path.of(workDirectory,speciesNames[speciesN] +" Population Time Series.png").toFile(), "Population Time Series" ,"Number of Individuals" ,"Time", true );		
				}
			}
		}

		//Write results to csv
		try {
			for ( int species = 0; species < resultsRepeated[0].length; species++) {

				String fileName = speciesNames[species]+" population time series.csv";
				File resultsFile = Path.of(workDirectory,fileName).toFile();

				int fileNumber = 0;
				while(true) {
					if (resultsFile.createNewFile()) {
						System.out.println("File created: " + resultsFile.getName());
						break;
					} else {
						fileNumber++;
						fileName = speciesNames[species]+" population time series("+fileNumber +").csv";
						resultsFile = Path.of(workDirectory,fileName).toFile();
					}
				}
				FileWriter myWriter = new FileWriter(Path.of(workDirectory,fileName).toFile());


				//Write header
				String header = "";
				String [] temp = resultsRepeated[0][0].split(",");
				for (int i = 1 ; i < temp.length+1; i++) {
					header += i+",";
				}
				myWriter.write(header+ "\n");


				//Write data
				for (String[] element : resultsRepeated) {
					myWriter.write(element[species] + "\n");
				}
				myWriter.close();

				System.out.println("Results saved to file: "+workDirectory +fileName);

			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Deals with creating the raster file output
	 * @param resultsExtinctionRepeated
	 * @param roadMortality
	 * @param workDirectory
	 * @param speciesNames
	 * @param minPersistenceThreshold
	 * @param nRoadVariations
	 */
	static private void processMaps( Map[][] resultsExtinctionRepeated, Map[] roadMortality, String workDirectory, String[] speciesNames, double minPersistenceThreshold, double nRoadVariations ) {

		//Gets the index value of the true road size
		int[] trueIndexes = new int[(int)(speciesNames.length/(nRoadVariations+1))];
		for (int i = 0; i < trueIndexes.length; i++) {
			trueIndexes[i] = i;
		}

		//Calculate the odds of survival
		Map[] survivalPercentage = new Map[resultsExtinctionRepeated[0].length ];

		for ( int species = 0; species < resultsExtinctionRepeated[0].length; species++) {

			survivalPercentage[species]= resultsExtinctionRepeated[0][species].clone();

			//Zeroes out the map
			for (int x = 0; x < survivalPercentage[species].getNcols(); x++) {
				for (int y = 0; y < survivalPercentage[species].getNrows(); y++) {
					if (survivalPercentage[species].getValue(x, y) != survivalPercentage[species].getNoDataValue()
							&&  survivalPercentage[species].getValue(x, y) != -1) {
						survivalPercentage[species].setValue(0,x, y);
					}
				}
			}

			//Calculates the average
			for(int i = 0; i < resultsExtinctionRepeated.length; i++) {
				for (int x = 0; x < survivalPercentage[species].getNcols(); x++) {
					for (int y = 0; y < survivalPercentage[species].getNrows(); y++) {
						if (survivalPercentage[species].getValue(x, y) != survivalPercentage[species].getNoDataValue()
								&&  survivalPercentage[species].getValue(x, y) != -1) {

							double value = (survivalPercentage[species].getValue(x, y)*i+resultsExtinctionRepeated[i][species].getValue(x,y))/(1+i);
							survivalPercentage[species].setValue(value, x, y);

						}
					}
				}
			}

			//Saves the resulting extinction map for each species
			survivalPercentage[species].saveToFile(Path.of(workDirectory,speciesNames[species]+" Survival Rate.asc").toString());

			if (roadMortality != null) {
				roadMortality[species].saveToFile(Path.of(workDirectory,speciesNames[species]+" Roadkill Mortality Percentage.asc").toString());
			}


		}

		//
		//After the all extinction maps are done, calculates for each cell the number of species that are expected to go extinct
		//

		Map extinctSpeciesN = createCompositeMap(survivalPercentage);

		//Checks how many species went extinct in each cell
		for(int y = 0; y < extinctSpeciesN.getNrows(); y++) {
			for (int x = 0; x < extinctSpeciesN.getNcols(); x++) {
				int extinctSpecies= 0;
					for (int trueMap = 0; trueMap < trueIndexes.length; trueMap ++) {					
					int xOfCollection = survivalPercentage[trueIndexes[trueMap]].getLonCol(extinctSpeciesN.getLon(x));
					int yOfCollection = survivalPercentage[trueIndexes[trueMap]].getLatRow(extinctSpeciesN.getLat(y));
					if (xOfCollection != -1 &&
							yOfCollection != -1 &&
							survivalPercentage[trueIndexes[trueMap]].getValue(xOfCollection,yOfCollection) != survivalPercentage[trueIndexes[trueMap]].getNoDataValue() &&
							survivalPercentage[trueIndexes[trueMap]].getValue(xOfCollection,yOfCollection) < minPersistenceThreshold) {
						extinctSpecies++;
					} else {
						continue;
					}
					extinctSpeciesN.setValue(extinctSpecies, x, y);
				}
			}
		}

		extinctSpeciesN.saveToFile(Path.of(workDirectory,"Number Of Extinct Species.asc").toString());

		//
		//Create a table detailing the various parameters of extinction
		//
		//(maior perda de popula��o, menor popula��o)

		//Calculates the final area for each species
		double[] finalSurvingArea = new double[survivalPercentage.length];
		double[] finalExtinctArea = new double[survivalPercentage.length];

		for (int i = 0; i < finalSurvingArea.length; i++) {
			finalSurvingArea[i] = 0;
			finalExtinctArea[i] = 0;
		}
		for ( int species = 0; species < survivalPercentage.length; species++) {
			for(int y = 0; y < survivalPercentage[species].getNrows(); y++) {
				for (int x = 0; x < survivalPercentage[species].getNcols(); x++) {
					if (survivalPercentage[species].getValue(x, y) != survivalPercentage[species].getNoDataValue()
							&&  survivalPercentage[species].getValue(x, y) != -1) {
						if ( survivalPercentage[species].getValue(x,y) > minPersistenceThreshold) {
							finalSurvingArea[species] += survivalPercentage[species].getAvgSqKMeters() ;
						} else {
							finalExtinctArea[species] += survivalPercentage[species].getAvgSqKMeters() ;
						}
					}
				}
			}
		}

		String finalTableOutput = "Species,Final Surviving Area (km2),Final Extinct Area (km2),Suriving Area %\n";
		for ( int species = 0; species < survivalPercentage.length; species++) {
			finalTableOutput += speciesNames[species]+","+finalSurvingArea[species]+","+finalExtinctArea[species] +","+(finalSurvingArea[species]/(finalSurvingArea[species]+finalExtinctArea[species])) +"\n";
		}



		try {
			String fileName = "Final Report.csv";
			File resultsFile = Path.of(workDirectory, "Final Report.csv").toFile();

			int fileNumber = 0;
			while(true) {
				if (resultsFile.createNewFile()) {
					System.out.println("File created: " + resultsFile.getName());
					break;
				} else {
					fileNumber++;
					fileName = "Final Report("+fileNumber +").csv";
					resultsFile = Path.of(workDirectory, fileName).toFile();
				}
			}
			FileWriter myWriter = new FileWriter(Path.of(workDirectory, fileName).toFile());

			myWriter.write(	finalTableOutput);
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Over and done!");
	}

	/**
	 * Private method that returns a map with a size that captures all maps given in the collection
	 * @param mapCollection
	 */
	private static Map createCompositeMap(Map[] mapCollection) {
	    double cellsizeX = 9999, cellsizeY = 9999;
	    double xllcorner = 9999;  // Global Min X
	    double xllmax = -9999;    // Global Max X
	    double yllmin = 9999;     // Global Min Y
	    double yllcorner = -9999; // Global Max Y (top corner)
	    
	    for (Map element : mapCollection) {
	        if (element.getCellXSize() < cellsizeX) cellsizeX = element.getCellXSize();
	        if (element.getCellYSize() < cellsizeY) cellsizeY = element.getCellYSize();
	        
	        if (element.getXcorner() < xllcorner) xllcorner = element.getXcorner();
	        if (element.getXmax() > xllmax) xllmax = element.getXmax();
	        if (element.getYmin() < yllmin) yllmin = element.getYmin();
	        if (element.getYcorner() > yllcorner) yllcorner = element.getYcorner();
	    }

	    // Corrected X/Y assignments
	    int ncols = (int) Math.ceil((xllmax - xllcorner) / cellsizeX);
	    int nrows = (int) Math.ceil((yllcorner - yllmin) / cellsizeY);

	    Map compositeMap = new Map("Composite Map", ncols, nrows, xllcorner, yllcorner, cellsizeX, cellsizeY, mapCollection[0].getNoDataValue());

	    for(int y = 0; y < compositeMap.getNrows(); y++) {
	        for (int x = 0; x < compositeMap.getNcols(); x++) {
	            boolean hasData = false; // Changed from isNoData to handle Union
	            for (Map element : mapCollection) {
	                int xCol = element.getLonCol(compositeMap.getLon(x));
	                int yRow = element.getLatRow(compositeMap.getLat(y));
	                if (xCol != -1 && yRow != -1 && element.getValue(xCol, yRow) != element.getNoDataValue()) {
	                    hasData = true;
	                    break;
	                }
	            }
	            if (hasData) {
	                compositeMap.setValue(0, x, y);
	            } else {
	                compositeMap.setValue(compositeMap.getNoDataValue(), x, y);
	            }
	        }
	    }
	    return compositeMap;
	}
	
	/**
	 * Private method do advance the date the required amount
	 * @param i
	 * @param currentDate
	 * @param timeUnit "Year" "Month" or "Day"
	 * @return
	 */
	private static LocalDate advanceDate(int i, LocalDate currentDate, String timeUnit) {
		switch(timeUnit) {
		case "Day":
			return currentDate.plusDays(i);
		case "Week":
			return currentDate.plusWeeks(i);			
		case "Month":
			return currentDate.plusMonths(i);
		case "Year":
			return currentDate.plusYears(i);
		}
		return null;
	}
	/**
	 * Private method do give the correct date format
	 * @param i
	 * @param currentDate
	 * @param timeUnit "Year" "Month" or "Day"
	 * @return
	 */
	private static String dateFormat(String timeUnit) {
		switch(timeUnit) {
		case "Day":
			return "yyyy/MM/dd";
		case "Week":
			return "yyyy/MM/dd";			
		case "Month":
			return "yyyy/MM";
		case "Year":
			return "yyyy";
		}
		return null;
	}


	/**	
	 * Processes the results from the sweep analysis
	 * @param resultsRepeated
	 * @param workDirectory
	 * @param speciesNames
	 * @param nRoadVariations
	 * @param date
	 * @param timeUnit
	 * @param sweepRes
	 * @param minSweep
	 * @param maxSweep
	 * @param maxIteration
	 */
	static private void processSweep( String[][] resultsRepeated, String workDirectory, String[] speciesNames, double nRoadVariations, LocalDate date,
			String timeUnit, int sweepRes, double minSweep, double maxSweep, int maxIteration) {
		//Gets the index value of the true road size - ALWAYS 0 AS OF NOW
		int[] trueIndexes = new int[(int)(speciesNames.length/(nRoadVariations+1))];
		for (int i = 0; i < trueIndexes.length; i++) {
			//	trueIndexes[i] = 0;
			trueIndexes[i] = (int)(i);
		}

		int eachRepLength = resultsRepeated.length/sweepRes;

		double[][] averageForGraph = new double[resultsRepeated[0].length][];

		for ( int species = 0; species < resultsRepeated[0].length; species++) {

			String[][] tempSplit = new  String[resultsRepeated.length][resultsRepeated[0][species].split(",").length];

			String[][] split = new  String[resultsRepeated.length][maxIteration];
			for (int i = 0 ; i < resultsRepeated.length; i++) {
				tempSplit[i] = resultsRepeated[i][species].split(",");
				//reduces the split until the number of desired iterations
				for(int y = 0; y < maxIteration; y++ ) {
					split[i][y] = tempSplit[i][y];
				}
			}


			//resultsRepeated[number of repetitions, loops on mortality sweep change][species] -> contains string of csv of that species over time
			//split[number of repetitions, loops on mortality sweep change][value over time] (one split contains info for one species)
			averageForGraph[species] = new double[sweepRes];

			for(int sweepN = 0 ; sweepN < sweepRes; sweepN ++) {
				for (int i = 0 ; i < eachRepLength ; i++ ) {	
					double currentValue = Double.parseDouble(split[i+sweepN*eachRepLength][split[i+sweepN*eachRepLength].length-1]) ;
					averageForGraph[species][sweepN] += currentValue > 0 ? 1:0;;
				}		
				averageForGraph[species][sweepN] = 	averageForGraph[species][sweepN]/eachRepLength;
			}


		}
		//Creates a graph of only the true roads
		YIntervalSeriesCollection dataset2 = new YIntervalSeriesCollection();
		for ( int species = 0; species < trueIndexes.length; species++) {
			YIntervalSeries series2 = new YIntervalSeries(speciesNames[trueIndexes[species]]);
			for (int i = 0; i < averageForGraph[trueIndexes[species]].length; i++) {


				series2.add(i, averageForGraph[trueIndexes[species]][i], averageForGraph[trueIndexes[species]][i], averageForGraph[trueIndexes[species]][i]);
			}
			System.out.println("Added Species "+species);

			dataset2.addSeries(series2);
		}

		//	DeviationGrapher.graph(dataset2);
		String[] mortalities = new String[sweepRes];
		for (int sweepN = 0 ; sweepN < sweepRes ; sweepN++) {
			double d = minSweep +  sweepN*(maxSweep-minSweep)/((double)sweepRes-1);
			String str = DecimalFormat.getNumberInstance().format(d);
			//if you don't want formatting
			str = new DecimalFormat("#.000#").format(d); // rounded to 2 decimal places
			mortalities[sweepN] = str;	
		}			
		DeviationGrapher.graph(dataset2, Path.of(workDirectory,"Survival Over Mortality No Variations.png").toFile(), "Survival Over Mortality" ,"Probability of Survival" ,"Mortality %" ,mortalities, false);



		//Only if there are multiple road variations
		if (nRoadVariations > 0) {
			//Creates a graph of all road steps
			YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
			for ( int species = 0; species < resultsRepeated[0].length; species++) {
				YIntervalSeries series1 = new YIntervalSeries(speciesNames[species]);
				for (int i = 0; i < averageForGraph[species].length; i++) {
					series1.add(i, averageForGraph[species][i], averageForGraph[species][i], averageForGraph[species][i]);
				}			
				dataset.addSeries(series1);
			}
			//	DeviationGrapher.graph(dataset);
			mortalities = new String[sweepRes];
			for (int sweepN = 0 ; sweepN < sweepRes ; sweepN++) {
				double d = minSweep +  sweepN*(maxSweep-minSweep)/((double)sweepRes-1);
				String str = DecimalFormat.getNumberInstance().format(d);
				//if you don't want formatting
				str = new DecimalFormat("#.000#").format(d); // rounded to 2 decimal places
				mortalities[sweepN] = str;			
			}		
			DeviationGrapher.graph(dataset, Path.of(workDirectory,"Survival Over Mortality.png").toFile(),  "Survival Over Mortality" ,"Probability of Survival" ,"Mortality %" ,mortalities, false);
		} 



		//Write results to csv
		try {
			for ( int species = 0; species < resultsRepeated[0].length; species++) {

				String fileName = speciesNames[species]+" final population over mortality.csv";
				File resultsFile = Path.of(workDirectory,fileName).toFile();

				int fileNumber = 0;
				while(true) {
					if (resultsFile.createNewFile()) {
						System.out.println("File created: " + resultsFile.getName());
						break;
					} else {
						fileNumber++;
						fileName = speciesNames[species]+" final population over mortality("+fileNumber +").csv";
						resultsFile = Path.of(workDirectory, fileName).toFile();
					}
				}
				FileWriter myWriter = new FileWriter(Path.of(workDirectory, fileName).toFile());


				//Write header
				String line = "";
				for (int i = 0 ; i < mortalities.length; i++) {
					line += mortalities[i]+",";
				}
				myWriter.write(line+ "\n");


				//Write data


				String[][] split = new  String[resultsRepeated.length][resultsRepeated[0][species].split(",").length];
				for (int i = 0 ; i < resultsRepeated.length; i++) {
					split[i] = resultsRepeated[i][species].split(",");
				}

				for (int i = 0 ; i < eachRepLength ; i++ ) {
					line = "";
					for (int sweepN = 0 ; sweepN < sweepRes; sweepN++) {											
						line += split[i+sweepN*eachRepLength][split[i+sweepN*eachRepLength].length-1] +",";
					}
					myWriter.write(line + "\n");

				}

				myWriter.close();

				System.out.println("Results saved to file: "+workDirectory +fileName);

			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}
}

