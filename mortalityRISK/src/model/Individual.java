package model;


/**
 * Class that defines each individual
 */
public class Individual {


	private static final double M_PER_DEGREE = 111319; //Variable for calculations of distance

	private final double SEXRATIO; //The chance of being male


	private final int DATEOFBIRTH;  //Iteration where the individual was born
	private int dateOfLastOffspring; //Iteration where the individual last gave birth
	private final String SEX; //Sex of the individual
	private final int[] LIFEPHASES; //Array with the iteration where the individual moves to the next life phase. (The last life phase is death due to old age)
	private double[] location;//0 - X, 1 - Y
	private double[] tempLocation; //used to store new location before confirmation

	/**
	 * Constructor to create an individual
	 *
	 * @param DoB The iteration where the individual was born
	 */
	public Individual(int DoB, int[] lifePhases, double sexRatio, double[] location) {

		DATEOFBIRTH = DoB;
		
		SEXRATIO = sexRatio;

		dateOfLastOffspring = -100000;

		if (Math.random() > sexRatio) {
			SEX = "Female";
		} else {
			SEX = "Male";
		}

		LIFEPHASES = lifePhases;
		
		this.location = location;
		tempLocation = location.clone();
	}

	/**
	 * Constructor to create an individual
	 *
	 * @param DoB The iteration where the individual was born
	 */
	public Individual(int DoB,int[] lifePhases, int dateOfLastOffspring, double sexRatio, double[] location) {

		DATEOFBIRTH = DoB;
		
		SEXRATIO = sexRatio;
		this.dateOfLastOffspring = dateOfLastOffspring;

		if (Math.random() > SEXRATIO) {
			SEX = "Female";
		} else {
			SEX = "Male";
		}

		LIFEPHASES = lifePhases;

		this.location = location;
		tempLocation = location.clone();
	}

	/**
	 * Constructor to create a clone individual
	 */
	private Individual(Individual ind) {
		
		DATEOFBIRTH = ind.DATEOFBIRTH;
		SEXRATIO = ind.getSexRatio();

		dateOfLastOffspring = ind.dateOfLastOffspring;

		if (ind.isFemale()) {
			SEX = "Female";
		} else {
			SEX = "Male";
		}

		LIFEPHASES = ind.getLifePhases();
		
		location = ind.getLocation().clone();
		tempLocation = location.clone();
	}

	/**
	 * Returns the X location of the individual
	 */
	
	public double getYLocation() {
		return location[1];
	}
	
	/**
	 * Returns the Y location of the individual
	 */
	
	public double getXLocation() {
		return location[0];
	}
	
	
	/**
	 * Returns the location of the individual
	 */
	
	public double[] getLocation() {
		return location;
	}
	
	/**
	 * Returns the temporary location of the individual to confirm
	 */
	
	public double[] getTempLocation() {
		return tempLocation;
	}
	
	
	/**
	 * Move individual
	 * @param direction - Direction in 0 to 360. (0 is east)
	 * @param distance - Receives meters and converts internally to degrees
	 * @return New position
	 */	
	public void move(double direction, double distance) {
		double radians = direction * Math.PI/180.0;

		//Calculates the conversion to meters from degrees depending on the lat and lon

		double latRadians = Math.toRadians(getYLocation());

		//Calculate the specific conversion factor for this latitude
		// 111319.0 is the approximate meters per degree at the equator
		double lonConversion = M_PER_DEGREE * Math.cos(latRadians);
	//	double latConversion = M_PER_DEGREE; no need to declare
		
		// Safety check: Prevent division by zero if at the absolute pole
	    if (lonConversion < 0.00001) lonConversion = 0.00001;
	    
		//Apply different scales to X and Y
		double deltaX = (distance * Math.cos(radians)) / lonConversion;
		double deltaY = (distance * Math.sin(radians)) / M_PER_DEGREE;
		
		//Stores a temporary location to be confirmed later
		tempLocation[0] = location[0] + deltaX;
		tempLocation[1] = location[1] + deltaY;
	}
	
	/**
	 * Confirms a previous move was valid
	 */
	
	public void confirmMove() {		
		location[0] = tempLocation[0];
		location[1] = tempLocation[1];
	}

	/**
	 * Returns the life phase of the individual
	 * @return
	 */
	public int getLifePhase(int currentStep) {
		int age = currentStep - DATEOFBIRTH;
		int phase = 0;
		while (phase < LIFEPHASES.length-1 && LIFEPHASES[phase] < age) {
			phase++;
		}
		return phase;
	}

	/**
	 * Returns the life phases of the individual
	 */
	public int[] getLifePhases() {
		return LIFEPHASES;
	}
	
	/**
	 * Returns the sex ratio of the individuals
	 */
	public double getSexRatio() {
		return SEXRATIO;
	}


	/**
	 * Return the age of the individual
	 * @param currentIteration The current iteration of the model
	 * @return int The current age of the individual;
	 */

	public int getAge(int currentIteration) {
		return(currentIteration-DATEOFBIRTH);
	}

	/**
	 * Returns true if this individual is a male
	 * @return boolean True if male
	 */
	public boolean isMale() {
		return (SEX.equals("Male"));
	}

	/**
	 * Returns true if this individual is a female
	 * @return boolean True if male
	 */
	public boolean isFemale() {
		return (SEX.equals("Female"));
	}

	/**
	 * Return the how long ago the individual gave birth
	 * @param currentIteration The current iteration of the model
	 * @return int The time that has passed since the individual gave birth;
	 */

	public int getTimeSinceLastOffspring(int currentIteration) {
		return(currentIteration-dateOfLastOffspring);
	}

	/**
	 * Sets the time of the last offspring
	 * @param currentIteration The current iteration of the model
	 * @return int The time that has passed since the individual gave birth;
	 */

	public void setDateOfLastOffspring(int currentIteration) {
		dateOfLastOffspring = currentIteration;
	}

	@Override
	public Individual clone() {
		return new Individual(this);
	}

	/**
	 *  Returns true if the individual is apt for giving birth
	 * @param matureAge
	 * @param currentStep
	 * @param minTimeBetweenBreeding
	 */
	public boolean isAvaiableForBirth(int matureAge, int currentStep, int minTimeBetweenBreeding) {
    	if(isFemale() && isMature(matureAge, currentStep) && getTimeSinceLastOffspring(currentStep) >= minTimeBetweenBreeding ) {
    		return true;
    	} else {
    		return false;
    	}
    }

	/**
	 * Returns true if the individual is sexually mature
	 * @param matureAge
	 * @param currentStep
	 */
	public boolean isMature(int matureAge, int currentStep) {
		return (currentStep-DATEOFBIRTH)>matureAge;
	}

	/**
	 * Marks the individual as having given birth in the given time step
	 * @param currentStep
	 */
	public void markAsHavingHadBirth(int currentStep) {
		dateOfLastOffspring = currentStep;

	}

	/**
	 * Adds the value to the date of the last offspring
	 * @param currentStep
	 */
	public void adjustBirthTiming(int adjustment) {
		dateOfLastOffspring += adjustment;

	}

	/**
	 *  Checks if an individual is completely identical to another one (location included).
	 */
	public boolean isSameAs(Individual ind) {

		if(this.DATEOFBIRTH == ind.DATEOFBIRTH && this.dateOfLastOffspring == ind.dateOfLastOffspring && this.SEX.contentEquals(ind.SEX) && this.location[0] == ind.getXLocation() && this.location[1] == ind.getYLocation()) {
			return true;
		}

		return false;
	}


}
