package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles the population map
 *
 */

public class IndMap {

	private String name; //Name of the map
	private final int ncols; //Number of columns in the map
	private final int nrows; //Number of rows in the map
	private final double xllcorner; //Longitude of the leftmost column
	private final double yllcorner; //Latitude of topmost row
	private final double cellsizeX; //Size in degrees of each square
	private final double cellsizeY; //Size in degrees of each square
	private final double  NODATA_value; //Value when there is no data
	private List<Individual> [][] map; //Holds the values of the map for x,y coordinates
	private double minValue; //Minimum value of the map
	private double maxValue; //Maximum value of the map
	private boolean minMaxInit = false;
	private boolean[][] existsMale;
	long[] activeCells; //A set of active cells, to avoid wasting time looking into nodata cells, stored through bit trickery

	/**
	 * Constructor that receives an .asc file location and transforms it into a two dimensional array to be read. (Currently unused)
	 *
	 * @param ascFile File Location of .asc file to be read.
	 */
	/* public IndMap(File ascFile) throws IOException {

		BufferedReader asc = new BufferedReader(new FileReader(ascFile));
		String temp;
		this.name = ascFile.getName().substring(0, ascFile.getName().length()-4);


		temp = asc.readLine();
		//Clear spaces in the end
		while(temp.lastIndexOf(" ")==temp.length()-1) {
			temp = temp.substring(0,temp.length()-1);
		}
		this.ncols = Integer.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		//Clear spaces in the end
		while(temp.lastIndexOf(" ")==temp.length()-1) {
			temp = temp.substring(0,temp.length()-1);
		}
		this.nrows = Integer.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		//Clear spaces in the end
		while(temp.lastIndexOf(" ")==temp.length()-1) {
			temp = temp.substring(0,temp.length()-1);
		}
		this.xllcorner = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		//Clear spaces in the end
		while(temp.lastIndexOf(" ")==temp.length()-1) {
			temp = temp.substring(0,temp.length()-1);
		}
		this.yllcorner = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		//Clear spaces in the end
		while(temp.lastIndexOf(" ")==temp.length()-1) {
			temp = temp.substring(0,temp.length()-1);
		}
		if(temp.startsWith("dx")) {
			this.cellsizeX = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));
			temp = asc.readLine();
			//Clear spaces in the end
			while(temp.lastIndexOf(" ")==temp.length()-1) {
				temp = temp.substring(0,temp.length()-1);
			}
			this.cellsizeY = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		} else {
			this.cellsizeX = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));
			this.cellsizeY = cellsizeX;
		}

		temp = asc.readLine();
		//Clear spaces in the end
		while(temp.lastIndexOf(" ")==temp.length()-1) {
			temp = temp.substring(0,temp.length()-1);
		}
		this.NODATA_value = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));
		List<Long> tempActiveCells = new ArrayList<>();

		@SuppressWarnings("unchecked")
		List<Individual>[][] mapa = new ArrayList[ncols][nrows];
		int space;
		int previousSpace;

		for(int row=0; row <nrows; row++) {
			previousSpace=0;
			temp = asc.readLine();
			//cuts initial space
			if (temp.startsWith(" ")) {
				previousSpace ++;
			}

			for(int col=0; col <ncols; col++) {
				space = temp.indexOf(" ", previousSpace);
				if (space == -1) { //stops at end of line
					if (NODATA_value == Double.valueOf(temp.substring(previousSpace, temp.length()))) {
						mapa[col][row] = null;
					} else {
						mapa[col][row] = new ArrayList<>();
					}
					break;
				}
				if (NODATA_value == Double.valueOf(temp.substring(previousSpace, space))) {
					mapa[col][row] = null;
					checkMinMax(0);
				} else {
					mapa[col][row] = new ArrayList<>();
					tempActiveCells.add(getCellKey(col,row));
				}
				previousSpace = space+1;
			}
		}

		checkMinMax(0);
		asc.close();

		initializeActiveCells(tempActiveCells);
		this.map = mapa;
	} */ 

	/**
	 * Constructor that receives a Map and transforms it into an IndMap.
	 *
	 * @param ascFile File Location of .asc file to be read.
	 */
	public IndMap(Map oldMap) {

		this.name = oldMap.getName();

		this.ncols = oldMap.getNcols();

		this.nrows = oldMap.getNrows();

		this.xllcorner = oldMap.getXcorner();

		this.yllcorner = oldMap.getYcorner();

		this.cellsizeX = oldMap.getCellXSize();
		this.cellsizeY = oldMap.getCellYSize();

		this.NODATA_value = oldMap.getNoDataValue();
		List<Long> tempActiveCells = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<Individual>[][] mapa = new ArrayList[ncols][nrows];
		for(int row=0; row <nrows; row++) {
			for(int col=0; col <ncols; col++) {
				if (NODATA_value == oldMap.getValue(col,row)) {
					mapa[col][row] = null;
				} else {
					mapa[col][row] = new ArrayList<>();
					if (oldMap.getValue(col, row)>0) //Only adds individual if value above 0
						mapa[col][row].add(new Individual(0, new int[] {0}, 0.5, new double[] {this.getLon(col),this.getLat(row)}));
					tempActiveCells.add(getCellKey(col,row));
				}
			}
		}
		checkMinMax(0);
		initializeActiveCells(tempActiveCells);
		this.map = mapa;
	}


	/**
	 * Constructor that receives a Map and transforms it into an IndMap.
	 *
	 * @param ascFile File Location of .asc file to be read.
	 */
	public IndMap(Map oldMap, int[] lifePhases) {

		this.name = oldMap.getName();

		this.ncols = oldMap.getNcols();

		this.nrows = oldMap.getNrows();

		this.xllcorner = oldMap.getXcorner();

		this.yllcorner = oldMap.getYcorner();

		this.cellsizeX = oldMap.getCellXSize();
		this.cellsizeY = oldMap.getCellYSize();

		this.NODATA_value = oldMap.getNoDataValue();
		List<Long> tempActiveCells = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<Individual>[][] mapa = new ArrayList[ncols][nrows];
		for(int row=0; row <nrows; row++) {
			for(int col=0; col <ncols; col++) {
				if (NODATA_value == oldMap.getValue(col,row)) {
					mapa[col][row] = null;
				} else {
					mapa[col][row] = new ArrayList<>();
					for (int i = 0; i < (int) oldMap.getValue(col,row) ; i++) {
						mapa[col][row].add(new Individual(0, lifePhases, 0.5,new double[] {this.getLon(col),this.getLat(row)}));
						tempActiveCells.add(getCellKey(col,row));
					}
				}
			}
		}
		checkMinMax(0);
		initializeActiveCells(tempActiveCells);
		this.map = mapa;
	}

	/**
	 * Constructor that receives a Map and a mask and transforms it into an IndMap.
	 *
	 * @param ascFile File Location of .asc file to be read.
	 */
	public IndMap(Map oldMap, int[] lifePhases, Map mask) {

		this.name = oldMap.getName();

		this.ncols = mask.getNcols();

		this.nrows = mask.getNrows();

		this.xllcorner = mask.getXcorner();

		this.yllcorner = mask.getYcorner();

		this.cellsizeX = mask.getCellXSize();
		this.cellsizeY = mask.getCellYSize();

		this.NODATA_value = oldMap.getNoDataValue();
		List<Long> tempActiveCells = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<Individual>[][] mapa = new ArrayList[ncols][nrows];
		for(int row=0; row <nrows; row++) {
			for(int col=0; col <ncols; col++) {
				int xcoord = oldMap.getLonCol(mask.getLon(col));
				int ycoord = oldMap.getLatRow(mask.getLat(row));	
				if(xcoord == -1 || ycoord == -1) { //If the presence map is out of bounds of the mask map, skip it
					continue;
				}
				if (NODATA_value == oldMap.getValue(xcoord,ycoord) || mask.getValue(col, row) == mask.getNoDataValue()) {
					mapa[col][row] = null;
				} else {
					mapa[col][row] = new ArrayList<>();
					for (int i = 0; i < (int) oldMap.getValue(xcoord,ycoord) ; i++) {
						mapa[col][row].add(new Individual(0, lifePhases, 0.5,new double[] {this.getLon(col),this.getLat(row)}));
						tempActiveCells.add(getCellKey(col,row));
					}

				}
			}
		}
		checkMinMax(0);
		initializeActiveCells(tempActiveCells);
		this.map = mapa;
	}


	/**
	 * Constructor that receives the parameters of an .asc file to create a two dimensional array to be read.
	 * Initiates full of "No Data Value".
	 *
	 * @param species String The name of the species
	 * @param ncol int Number of columns
	 * @param nrow int Number of rows
	 * @param xllcorner double Longitude at the left border
	 * @param yllcorner double Latitude at the lower border
	 * @param cellsize double Size of each cell
	 * @param nodatavalue int Value when there is no data
	 */

	public IndMap(IndMap exampleMap ,String species, int ncols, int nrows, double xllcorner, double yllcorner, double cellsizeX, double cellsizeY, double nodatavalue ) {

		this.name = species;

		this.ncols = ncols;

		this.nrows = nrows;

		this.xllcorner = xllcorner;

		this.yllcorner = yllcorner;

		this.cellsizeX = cellsizeX;
		this.cellsizeY = cellsizeY;

		this.NODATA_value = nodatavalue;
		List<Long> tempActiveCells = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<Individual>[][] mapa = new ArrayList[ncols][nrows];

		for(int row=0; row <nrows; row++) {
			for(int col=0; col <ncols; col++) {
				if (NODATA_value == exampleMap.getCount(col,row)) {
					mapa[col][row] = null;
				} else {
					mapa[col][row] = new ArrayList<>();
					tempActiveCells.add(getCellKey(col,row));
				}
			}
		}

		checkMinMax(0);
		initializeActiveCells(tempActiveCells);
		this.map = mapa;
	}

	/**
	 * Constructor that receives the parameters of an .asc file from another .asc file to create a two dimensional array to be read.
	 *
	 * @param file File Location of .asc file to be read.
	 * @param newMap boolean If true, creates a new map with the parameters of "file", if false, loads "file"
	 */

	public IndMap(File ascFile, boolean newMap) throws IOException {


		BufferedReader asc = new BufferedReader(new FileReader(ascFile));
		String temp;
		this.name = ascFile.getName().substring(0, ascFile.getName().length()-4);


		temp = asc.readLine();
		this.ncols = Integer.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		this.nrows = Integer.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		this.xllcorner = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		this.yllcorner = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		if(temp.startsWith("dx")) {
			this.cellsizeX = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));
			temp = asc.readLine();
			this.cellsizeY = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		} else {
			this.cellsizeX = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));
			this.cellsizeY = cellsizeX;
		}

		temp = asc.readLine();
		this.NODATA_value = Integer.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));
		List<Long> tempActiveCells = new ArrayList<>();

		@SuppressWarnings("unchecked")
		List<Individual>[][] mapa = new ArrayList[ncols][nrows];

		if (!newMap) {
			int espaco;
			for(int row=0; row <nrows; row++) {
				temp = asc.readLine();
				//cuts initial space
				if (temp.startsWith(" ")) {
					temp = temp.substring(1);
				}
				for(int col=0; col <ncols; col++) {
					espaco = temp.indexOf(" ");
					if (espaco == -1) { //stops at end of line
						if (NODATA_value == Double.valueOf(temp.substring(0, temp.length()))) {
							mapa[col][row] = null;

						} else {
							mapa[col][row] = new ArrayList<>();
							tempActiveCells.add(getCellKey(col,row));
						}
						checkMinMax(0);
						break;
					}
					if (NODATA_value ==  Double.valueOf(temp.substring(0, espaco))) {
						mapa[col][row] = null;

					} else {
						mapa[col][row] = new ArrayList<>();
						tempActiveCells.add(getCellKey(col,row));
					}
					checkMinMax(0);
					temp = temp.substring(espaco+1);
				}
			}
		} else {
			for(int row=0; row <nrows; row++) {
				for(int col=0; col <ncols; col++) {
					map[col][row]=null;
				}
			}
		}


		asc.close();
		initializeActiveCells(tempActiveCells);
		this.map = mapa;
	}

	/**
	 * Constructor that receives only the initial population and creates a map with a single point. For non-spatial analysis.
	 *
	 * @param species String The name of the species.
	 * @param popSize int The initial population size.
	 */

	@SuppressWarnings("unchecked")
	public IndMap(String species, double area) {

		this.name = species;

		this.ncols = 1;

		this.nrows = 1;

		this.xllcorner = 1;

		this.yllcorner = 1;

		this.cellsizeX = Math.sqrt(area);
		this.cellsizeY = Math.sqrt(area);

		this.NODATA_value = -9999;

		this.map = new ArrayList[ncols][nrows];

		map[0][0]= new ArrayList<>();
		map[0][0].add(new Individual(0,new int[] {0},0.5,new double[] {0,0}));
		List<Long> tempActiveCells = new ArrayList<>();
		tempActiveCells.add(getCellKey(0,0));
		initializeActiveCells(tempActiveCells);

	}


	/**
	 * Returns the list of individuals of a certain pixel given row and column
	 *
	 * @param x  int Column of the pixel
	 * @param y int Row of the pixel
	 * @return List<Individual> List of individuals on the pixel
	 */
	public List<Individual> getValue(int x, int y) {
		try {
			return map[x][y];
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the count of individuals of a certain pixel given row and column
	 *
	 * @param x  int Column of the pixel
	 * @param y int Row of the pixel
	 * @return int Value of the pixel
	 */
	public double getCount(int x, int y) {

		if (map[x][y] == null) {
			return NODATA_value;
		} else {
			return map[x][y].size();
		}

	}

	/**
	 * Returns the column given longitude
	 *
	 * @param lon  double longitude
	 * @return int Column number (-1 if out of bounds)
	 */
	public int getLonCol(double lon) {
		int col = (int) Math.round(((lon - xllcorner - cellsizeX*0.5)/cellsizeX));
		if (col >= ncols || col < 0) {
			col = -1;
		}
		return col;
	}

	/**
	 * Returns the row given latitude
	 *
	 * @param lat  double latitude
	 * @return int row number (-1 if out of bounds)
	 */
	public int getLatRow(double lat) {
		int row = (int) Math.round(((-lat + yllcorner + cellsizeY * (nrows) - 0.5*cellsizeY)/cellsizeY));
		if (row >= nrows || row < 0) {
			row = -1;
		}
		return row;
	}

	/**
	 * Sets the value of a certain pixel
	 *
	 * @param x  int Column of the pixel
	 * @param y int Row of the pixel
	 * @param double value Value of the pixel
	 */
	public void setValue(List<Individual> value, int x, int y) {
		if (value!= null) {
			map[x][y] = new ArrayList<>(value);
			checkMinMax(value.size());
		}
	}


	/**
	 * Returns the number of columns in the map
	 *
	 * @return int Number of columns
	 */
	public int getNcols() {
		return ncols;

	}

	/**
	 * Returns the number of rows in the map
	 *
	 * @return int Number of rows
	 */
	public int getNrows() {
		return nrows;
	}

	/**
	 * Returns the minimum value in the map
	 *
	 * @return double Minimum value
	 */
	public double getMin() {
		return minValue;
	}

	/**
	 * Returns the maximum value in the map
	 *
	 * @return double Maximum value
	 */
	public double getMax() {
		return maxValue;
	}

	/**
	 * Returns the xllcorner of the map
	 *
	 * @return double xllcorner
	 */
	public double getXcorner() {
		return xllcorner;
	}

	/**
	 * Returns the yllcorner of the map
	 *
	 * @return double yllcorner
	 */
	public double getYcorner(){
		return yllcorner;
	}


	/**
	 * Returns the cell size of the map
	 *
	 * @return double Cell size
	 */
	public double getCellXSize(){
		return cellsizeX;
	}

	/**
	 * Returns the cell size of the map
	 *
	 * @return double Cell size
	 */
	public double getCellYSize(){
		return cellsizeY;
	}



	/**
	 * Returns the value used when there is no data
	 *
	 * @return double No data value
	 */
	public double getNoDataValue(){
		return NODATA_value;
	}

	/**
	 * Returns the longitude of a certain column
	 *
	 * @param x int The column number
	 *
	 * @return double Longitude
	 */
	public double getLon(int x) {
		return xllcorner + cellsizeX * (x + 0.5);
	}

	/**
	 * Returns the latitude of a certain row
	 *
	 * @param y int The row number
	 *
	 * @return double Latitude
	 */
	public double getLat(int y) {
		return yllcorner + cellsizeY * ((nrows - y)-0.5);
	}

	/**
	 * Returns the map's name
	 *
	 * @return String The map's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Private method that compares a new value with min and max and substitutes if needed.
	 *
	 * @param value double New value to be compared
	 */
	private void checkMinMax(double value) {
		if (value != NODATA_value) {
			if (minMaxInit) {
				if (value > maxValue) {
					maxValue = value;
				} else if (value < minValue) {
					minValue = value;
				}
			} else {
				minMaxInit = true;
				maxValue = value;
				minValue = value;
			}
		}
	}


	/**
	 * Writes this Map object as .asc (GRIDASCII) files.
	 *
	 * @param file String Location to save .asc file.
	 */

	public void saveToFile(String file) {

		try {

			String tempFile = file;
			File f = new File(tempFile);
			int fileNumber = 1;
			while (f.exists()) { // Check if the file already exists. If so, changes file name.
				file = tempFile.substring(0, tempFile.length()-4) + "(" + fileNumber + ")" + tempFile.substring(tempFile.length()-4,tempFile.length());
				f = new File(file);
				fileNumber++;
			}

			BufferedWriter writer = new BufferedWriter (new FileWriter(new File(file)));

			writer.write("ncols        "+ncols+"\n");
			writer.write("nrows        "+nrows+"\n");
			writer.write("xllcorner    "+xllcorner+"\n");
			writer.write("yllcorner    "+yllcorner+"\n");
			if (cellsizeX == cellsizeY) {
				writer.write("cellsize     "+cellsizeX+"\n");
			} else {
				writer.write("dx           "+cellsizeX+"\n");
				writer.write("dy           "+cellsizeX+"\n");				
			}
			writer.write("NODATA_value "+NODATA_value);

			for(int row=0; row <nrows; row++) {
				writer.write("\n");
				for(int col=0; col <ncols; col++) {
					writer.write(" "+getCount(col,row));
				}
			}

			writer.close();
			//	System.out.println("File saved at "+file+".");



		} catch (IOException e) {
			System.out.println("Failed to write to file.");
			e.printStackTrace();
		}
	}

	/**
	 * Clones this Map
	 *
	 * @return Map Clone of this Map
	 */

	@Override
	public IndMap clone() {

		IndMap clone = new IndMap(this , name, ncols, nrows, xllcorner, yllcorner, cellsizeX, cellsizeY, NODATA_value );

		for(int x = 0; x < ncols; x++ ) {
			for (int y = 0; y < nrows; y++) {
				checkNulls(x,y,"While cloning");
				for(int i = 0; i < this.getCount(x, y); i++) {
					clone.addIndividual(this.getIndividual(i,x,y).clone() , x, y);
				}
			}
		}


		return clone;
	}

	/**
	 * Changes the map's name
	 * @param name String The map's name
	 */

	public void setName(String name) {
		this.name= name;
	}

	/**
	 * Kill a random individual at the chosen location
	 * @param x
	 * @param y
	 *
	 * @return boolean True if an individual was killed False if not
	 */
	public boolean killRandom(int x, int y) {
		if(getCount(x,y)>0) {
			map[x][y].remove((int)(Math.random()*getCount(x,y)));
			return true;
		} else {
			return false;
		}
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param currentIteration
	 * @param minTimeBetweenBreeding
	 * @return
	 */

	public int getAvailableForBirthCount(int x, int y, int currentIteration, int minTimeBetweenBreeding) {
		int available = 0;

		for (Individual element : map[x][y]) {
			if(element.getTimeSinceLastOffspring(currentIteration) > minTimeBetweenBreeding) {
				available ++;
			}
		}

		return available;
	}

	/**
	 *
	 * @param breedingEvents
	 * @param currentStep
	 */

	public void markAsHavingHadBirth(int breedingEvents, int currentStep,int x, int y) {

		List<Integer> femalePosition = new ArrayList<>(); //Gets the position of all females in the list
		try {
			for (int i = 0; i < getCount(x,y); i++) {
				if(map[x][y].get(i).isFemale()) {
					femalePosition.add(i);
				}
			}
		} catch (NullPointerException e) {
			System.out.println("Error at marking birth");
			System.out.println(getCount(x,y));
			System.out.println(map[x][y].size());
			e.printStackTrace();
			System. exit(0);

		}

		if (femalePosition.size() < breedingEvents) { //Confirms that there are no more breeding events than females
			breedingEvents = femalePosition.size();
		}

		for (int i = 0; i < breedingEvents; i++) { //Chooses random females from the available ones to mark as having given birth
			int femaleIndex = (int)(Math.random()*((double)femalePosition.size()));
			map[x][y].get(femaleIndex).setDateOfLastOffspring(currentStep);
			femalePosition.remove(femaleIndex);
		}

	}

	/**
	 * Adds the desired number of newborns
	 * @param newBorns
	 * @param currentStep
	 * @param x
	 * @param y
	 */
	public void addNewborns(int newBorns, int currentStep, int[] lifePhases,double sexRatio , int x, int y) {
		for (int i = 0; i < newBorns; i++) {
			map[x][y].add(new Individual(currentStep, lifePhases,sexRatio,new double[] {this.getLon(x),this.getLat(y)}) );
		}
	}

	/**
	 * Adds one newborns and returns it
	 * @param newBorns
	 * @param currentStep
	 * @param x
	 * @param y
	 */
	public Individual addNewborn( int currentStep, int[] lifePhases, double sexRatio, int x, int y) {
		Individual newborn = new Individual(currentStep, lifePhases, sexRatio,new double[] {this.getLon(x),this.getLat(y)}) ;
		map[x][y].add(newborn );
		return newborn;
	}

	/**
	 * Adds the desired number of individuals with random ages
	 * @param newBorns
	 * @param currentStep
	 * @param x
	 * @param y
	 */
	private int randomIndCounter;
	private int nInd;
	public void addRandomIndividuals(int nIndividuals, int currentStep, int[] lifePhases, int minTimeBetweenBreeding, int maturityAge, int litterSize, double birthProb, double sexRatio, int x, int y) {
		randomIndCounter = 0;
		nInd = nIndividuals;
		while ( randomIndCounter < nInd) {
			double age = Math.random()*((double)lifePhases[lifePhases.length-1]);
			double averageTimeToBirth = birthProb;
			if (averageTimeToBirth>0 && averageTimeToBirth<1) { //Then it's a birth probability and not an average value
				averageTimeToBirth = minTimeBetweenBreeding + 1.0/(birthProb)-1;
			}

			//As currently they reproduce as soon as they can, and the delay is caused by considering that the birth happened in the "future"
			//int dateOfLastOffspring =(int) (currentStep -(averageTimeToBirth+minTimeBetweenBreeding) * Math.random());	
			//The individual can wait from 0 steps to avg*2-min (that is, the distance from the min to the avg doubled)
			//	int dateOfLastOffspring =(int) (currentStep  -Math.round((double) (minTimeBetweenBreeding +(averageTimeToBirth-minTimeBetweenBreeding)*2) * Math.random()));

			//Makes it so the individuals reproduce over their entire reproduction range
			//	int dateOfLastOffspring =(int) (currentStep -minTimeBetweenBreeding + Math.round((averageTimeToBirth + (averageTimeToBirth-minTimeBetweenBreeding))*Math.random()));
			int dateOfLastOffspring =(int) (currentStep + (averageTimeToBirth-minTimeBetweenBreeding)*2 - (minTimeBetweenBreeding+(averageTimeToBirth-minTimeBetweenBreeding)*2)*Math.random());

			map[x][y].add(new Individual((int) (currentStep- age),lifePhases, dateOfLastOffspring, sexRatio,new double[] {this.getLon(x),this.getLat(y)}));
			randomIndCounter++;
			//Adds all children the individual could possibly have had. as well as children of the children		
			for (int i = 0 ; i < litterSize; i ++) {
				recursiveAddChildIndividual(age,currentStep, lifePhases, minTimeBetweenBreeding, maturityAge, litterSize, averageTimeToBirth, sexRatio, x, y) ; //Next child
			}
		} 
	}

	private void recursiveAddChildIndividual(double ageAtPreviousBirth ,int currentStep, int[] lifePhases, int minTimeBetweenBreeding, int maturityAge, int litterSize, double averageTimeToBirth, double sexRatio, int x, int y) {
		if ( ageAtPreviousBirth < maturityAge ) {
			return;
		}

		if ( randomIndCounter >= nInd) {
			return;
		}

		//	int dateOfLastOffspring =(int) (currentStep - minTimeBetweenBreeding + Math.round((averageTimeToBirth + (averageTimeToBirth-minTimeBetweenBreeding))*Math.random()));
		int dateOfLastOffspring =(int) (currentStep + (averageTimeToBirth-minTimeBetweenBreeding)*2 - (minTimeBetweenBreeding+(averageTimeToBirth-minTimeBetweenBreeding)*2)*Math.random());


		//Adds the oldest son possible, that is, it has the age of the parent, less the time to maturity (assumes parent gave birth ASAP)
		map[x][y].add(new Individual((int) (currentStep-(ageAtPreviousBirth-maturityAge)),lifePhases, dateOfLastOffspring, sexRatio,new double[] {this.getLon(x),this.getLat(y)}));
		ageAtPreviousBirth = ageAtPreviousBirth - averageTimeToBirth;
		randomIndCounter++;
		for (int i = 0 ; i < litterSize; i ++) {
			recursiveAddChildIndividual(ageAtPreviousBirth ,currentStep, lifePhases, minTimeBetweenBreeding, maturityAge, litterSize, averageTimeToBirth, sexRatio, x, y) ; //Next child
		}
		//recursiveAddParentIndividual((ageAtPreviousBirth + averageTimeToBirth)-maturityAge ,currentStep, lifePhases, minTimeBetweenBreeding, maturityAge, averageTimeToBirth, sexRatio, x, y) ; //Children of the children

	}

	/*
	public void addRandomIndividuals(int nIndividuals, int currentStep, int[] lifePhases, int minTimeBetweenBreeding, String birthProb, Map initialPopulation, double sexRatio, int x, int y) {
		if (initialPopulation.getValue(x,y) == initialPopulation.getNoDataValue() || initialPopulation.getValue(x,y) == 0) {
			return;
		}
		for (int i = 0; i < nIndividuals; i ++) {
			//Skips if out of bounds ( or if no presences - not now)
			double averageTimeToBirth = minTimeBetweenBreeding + 1/(birthProb);
			int dateOfLastOffspring =(int) (currentStep -( averageTimeToBirth-minTimeBetweenBreeding) * Math.random());	
			map[x][y].add(new Individual((int)(currentStep-Math.random()*((double)lifePhases[lifePhases.length-1])), lifePhases,dateOfLastOffspring, sexRatio,new double[] {this.getLon(x),this.getLat(y)}));
		}
	}
	 */

	/**
	 * Converts this IndMap into a Map
	 * @return The Map
	 */

	public Map toMap() {
		return new Map(this);
	}

	/**
	 * Gets the specified individual at that location
	 * @param indIndex
	 * @param x
	 * @param y
	 * @return
	 */
	public Individual getIndividual(int indIndex, int x, int y) {
		return map[x][y].get(indIndex);
	}

	/**
	 * Moves an individual a certain amount in a certain direction
	 * @param indIndex
	 * @param direction - 0 is east
	 * @param y
	 * @return true if it moved to another cell
	 */
	public void moveIndividual(int x, int y, int indIndex, double direction, double length) {
		Individual movingIndividual = map[x][y].get(indIndex);
		movingIndividual.move(direction, length);	
		double[] tempLocation = movingIndividual.getTempLocation();
		if (this.getValue(getLonCol(tempLocation[0]), getLatRow(tempLocation[1])) != null) {
			map[x][y].get(indIndex).confirmMove();			
		}
	}

	/**
	 * Applies all moves simultaneously
	 */

	public void applyAllMoves() {
		for (long cell : getOccupiedCells()) {
			int x = getXFromCellKey(cell);
			int y = getYFromCellKey(cell); 
			for(int i = 0; i < map[x][y].size(); i++) {
				int newX = getLonCol(map[x][y].get(i).getXLocation());
				int newY = getLatRow(map[x][y].get(i).getYLocation());
				if(newX != x || newY != y) { //If the new location is on a different grid, moves the individual
					map[newX][newY].add(map[x][y].get(i));
					map[x][y].remove(i);
					i--;
				}
			}
		}
	}

	/**
	 * Kills the specified individual at that location
	 * @param indIndex
	 * @param x
	 * @param y
	 */
	public void killIndividual(int indIndex, int x, int y) {
		map[x][y].remove(indIndex);
	}

	/**
	 * Kills the specified individual at that location
	 * @param ind
	 * @param x
	 * @param y
	 */

	public void killIndividual(Individual ind, int x, int y) {
		map[x][y].remove(findIndexOf(ind,x,y));
	}

	/**
	 * Finds if a particular individual is present, and if so where
	 * @param ind
	 * @param x
	 * @param y
	 * @return
	 */

	private int findIndexOf (Individual ind, int x, int y) {
		for(int i = 0; i < this.getCount(x, y); i++) {
			if ( this.getIndividual(i, x, y).isSameAs(ind) ) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Adds the specified individual at that location
	 * @param individual
	 * @param x
	 * @param y
	 */
	public void addIndividual(Individual individual, int x, int y) {
		map[x][y].add(individual.clone());
	}

	/**
	 * Checks whether there are nulls in the map
	 * @p
	 * aram x
	 * @param y
	 * @param errorMessage
	 */
	public void checkNulls(int x, int y, String errorMessage) {

		if (map[x][y] != null) {
			for (int i= 0 ; i < map[x][y].size(); i++) {
				if(map[x][y].get(i) == null) {
					if (errorMessage != null) {
						System.out.println("Null detected! Error message: " + errorMessage);
					}
					map[x][y].remove(i);
					i--;
				}
			}
		}
	}

	/**
	 * Returns the average of the given variable for the whole population
	 * @param envVariable
	 * @return
	 */
	public double populationEnvAverage (Map envVariable) {
		double occupiedCount= 0;
		double totalSum = 0;
		for(int x = 0; x < ncols; x++ ) {
			for (int y = 0; y < nrows; y++) {
				if(this.getValue(x, y)!=null && this.getValue(x, y).size() > 0) {
					occupiedCount ++;
					double value = envVariable.getValue(envVariable.getLonCol(this.getLon(x)), envVariable.getLatRow(this.getLat(y)));
					if(value != envVariable.getNoDataValue()) {
						totalSum += value;
					}

				}
			}
		}
		return totalSum/occupiedCount;
	}

	/**
	 * Returns the sum of the given variable for the whole population
	 * @param envVariable
	 * @return
	 */
	public double populationEnvSum (Map envVariable) {
		double totalSum = 0;
		for(int x = 0; x < ncols; x++ ) {
			for (int y = 0; y < nrows; y++) {
				if(this.getValue(x, y)!=null && this.getValue(x, y).size() > 0) {
					double value = envVariable.getValue(envVariable.getLonCol(this.getLon(x)), envVariable.getLatRow(this.getLat(y)));
					if(value != envVariable.getNoDataValue()) {
						totalSum += value;
					}

				}
			}
		}
		return totalSum;
	}

	/**
	 * Returns the total number of individuals present
	 * @param 
	 * @return
	 */
	public int getPopulationSize () {
		int totalSum = 0;
		for (long cell : getOccupiedCells()) {
			int x = getXFromCellKey(cell);
			int y = getYFromCellKey(cell); 
			totalSum += getValue(x, y).size();
		}
		return totalSum;
	}

	/**
	 * Returns the total number of individuals present with the specified age range [initialAge,finalAge]
	 * @param envVariable
	 * @return
	 */
	public int getPopulationSizeWithinAge (int initialAge, int finalAge, int currentIteration) {
		int totalSum = 0;
		for (long cell : getOccupiedCells()) {
			int x = getXFromCellKey(cell);
			int y = getYFromCellKey(cell); 
			for (Individual element : getValue(x, y)) {
				if (element.getAge(currentIteration) >= initialAge &&element.getAge(currentIteration) <= finalAge) {
					totalSum++;
				}
			}
		}
		return totalSum;
	}

	/**
	 * Prints the age of each individual
	 * @param envVariable
	 * @return
	 */
	public void printAgeOfIndividuals (int currentIteration) {
		for(int x = 0; x < ncols; x++ ) {
			for (int y = 0; y < nrows; y++) {
				if(this.getValue(x, y)!=null) {
					for (Individual element : getValue(x, y)) {
						System.out.println(element.getAge(currentIteration));
					}
				}
			}
		}

	}


	/**
	 * Kills all individuals that are too old in the selected location
	 * @param x
	 * @param y
	 * @param currentStep
	 * @param maxAge
	 */
	public void killOldAge(int x, int y, int currentStep, int maxAge) {
		int nInd = (int) getCount(x,y);
		int nDeaths = 0;
		for (int i = 0; i < nInd; i++) {
			if (getIndividual(i-nDeaths, x,y).getAge(currentStep) > maxAge) {
				killIndividual(i-nDeaths, x,y);
				nDeaths ++;
			}
		}
	}

	/**
	 * Kills all individuals at the selected location
	 * @param x
	 * @param y
	 */
	public void killAllIndividuals(int x, int y) {
		map[x][y]= new ArrayList<>();


	}

	/**
	 * Kills random individuals at x y
	 */

	public void killRandomIndividuals(int n, int x, int y) {

		if (n > getPopulationSize() ) {
			n = getPopulationSize();
		}
		for (int killCount = 0; killCount < n; killCount++) {
			killRandom(x,y);
		}
	}

	/**
	 * Adds individuals with a specified age randomly
	 * @param n
	 * @param age
	 */
	//TODO add extra father having child
	public void addIndividuals(int n, int age, int currentStep, int[] lifePhases, int minTimeBetweenBreeding, double birthProb, double sexRatio) {

		List<Integer[]> haveIndividuals = new ArrayList<Integer[]>();
		for(int x = 0; x < ncols; x++ ) {
			for (int y = 0; y < nrows; y++) {
				if(this.getValue(x, y)!=null) {
					haveIndividuals.add(new Integer[] {x,y});
				}
			}
		}

		for (int i = 0 ; i < n ; i++) {
			int randomLocation = (int) (Math.random()*haveIndividuals.size());
			double averageTimeToBirth = minTimeBetweenBreeding + 1/(birthProb);
			int dateOfLastOffspring =(int) (currentStep -( averageTimeToBirth) * Math.random());	
			map[haveIndividuals.get(randomLocation)[0]][haveIndividuals.get(randomLocation)[1]].add(new Individual(currentStep-age, lifePhases, dateOfLastOffspring, sexRatio,new double[] {this.getLon(haveIndividuals.get(randomLocation)[0]),this.getLat(haveIndividuals.get(randomLocation)[1])}));
		}
	}

	/**
	 * Returns the square kilometers of the pixel
	 * @return double Square kilometers of the pixel
	 */


	public double getSqKMeters(int x, int y) {


		return Map.haversine(getLon(x),getLat(y),getLon(x+1),getLat(y))*Map.haversine(getLon(x),getLat(y),getLon(x),getLat(y+1));

	}

	/**
	 * Gets the population size while compensating the size with the given ratio
	 * @param indRatio
	 * @return
	 */

	public Integer getPopulationSize(double[][] indRatio) {
		double totalSum = 0;
		for (long cell : getOccupiedCells()) {
			int x = getXFromCellKey(cell);
			int y = getYFromCellKey(cell); 
			totalSum += ((double) getCount(x, y))*indRatio[x][y];							
		}
		return (int) totalSum;
	}

	/**
	 * Returns true if at least one sexually active male exists
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean maleExists(int x, int y, int maturityAge, int currentIteration) {
		int nInd = 0;
		try{
			nInd = (int) getCount(x,y);
			for (int i = 0; i < nInd; i++) {
				if (getIndividual(i,x,y).isMale() && getIndividual(i,x,y).getAge(currentIteration) >= maturityAge ) {
					return true;
				}
			}
		}
		catch (Exception e )  {
			return false;
		}

		return false;
	}

	/**
	 * Checks for the existence of males in all cells of the model and stores the results in a boolean[][]
	 */
	public void maleCheck(int maturityAge, int currentIteration) {
		existsMale = new boolean[ncols][nrows];
		for (long cell : getOccupiedCells()) {
			int x = getXFromCellKey(cell);
			int y = getYFromCellKey(cell); 
			existsMale[x][y] = maleExists(x,y,maturityAge,currentIteration);				
		}
	}

	/**
	 * Returns true if at least one sexually active male exists
	 * @param x
	 * @param y
	 * @return 
	 */	
	public boolean maleExists(int x, int y) {
		return existsMale[x][y];
	}


	public double calculateAvgCellSize() {
		int totalValidCells = 0;
		int totalSize = 0;

		for (int col = 0; col < ncols; col++) {
			for (int row = 0; row < nrows; row++) {		        
				// Only count cells that are part of the active study area
				if (map[col] [row] != null) {
					totalValidCells++;
					totalSize += getSqKMeters(col,row);
				}
			}
		}
		// Guard against division by zero just in case the map is empty
		if (totalValidCells == 0) {
			return 0.0; 
		}

		// Calculate the true landscape-wide average population per cell
		return ((double) totalSize) / (totalValidCells);
	}


	public long[] getOccupiedCells() {
		return activeCells;
	}

	private static Long getCellKey(int x, int y) {
		return ((long) x << 32) | (y & 0xFFFFFFFFL);		
	}

	public static int getXFromCellKey(long key) {
		return (int) (key >> 32);		
	}

	public static int getYFromCellKey(long key) {
		return (int) (key & 0xFFFFFFFFL);
	}
	
	private void initializeActiveCells(List<Long> longList) {
		activeCells = new long[longList.size()];
		for (int i = 0; i < longList.size(); i++) {
		    activeCells[i] = longList.get(i);
		}
	}
}


