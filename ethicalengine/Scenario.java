package ethicalengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Scenario {

    // constant variable
    public static final String NEW_LINE = "\n";

    // instance variables
    private ArrayList<Persona> passengers;
    private ArrayList<Persona> pedestrians;
    private boolean isLegalCrossing;

    // constructor
	public Scenario(Persona[] passengers, Persona[] pedestrians, boolean isLegalCrossing) {

	    this.passengers = new ArrayList<Persona>();
        Collections.addAll(this.passengers, passengers);

        this.pedestrians = new ArrayList<Persona>();
        this.pedestrians.addAll(Arrays.asList(pedestrians));

        this.isLegalCrossing = isLegalCrossing;
    }

    // accessor methods
    public boolean hasYouInCar() {
	    for (Persona passenger : passengers) {
	        if (passenger instanceof Human && ((Human) passenger).isYou()) {
	            return true;
            }
        }
	    return false;
    }
    public boolean hasYouInLane() {
        for (Persona pedestrian : pedestrians) {
            if (pedestrian instanceof Human && ((Human) pedestrian).isYou()) {
                return true;
            }
        }
        return false;
    }
    public Persona[] getPassengers() {
	    Persona[] passengersCopy = new Persona[passengers.size()];
	    for (int i = 0; i < passengers.size(); i++) {
	        passengersCopy[i] = passengers.get(i);
        }
	    return passengersCopy;
    }
    public Persona[] getPedestrians() {
        Persona[] pedestriansCopy = new Persona[pedestrians.size()];
        for (int i = 0; i < pedestrians.size(); i++) {
            pedestriansCopy[i] = pedestrians.get(i);
        }
        return pedestriansCopy;
    }
    public boolean isLegalCrossing() {
	    return isLegalCrossing;
    }
    public int getPassengerCount() {
	    return passengers.size();
    }
    public int getPedestrianCount() {
	    return pedestrians.size();
    }

    // mutator method
    public void setLegalCrossing(boolean isLegalCrossing) {
	    this.isLegalCrossing = isLegalCrossing;
    }

    @Override
    public String toString() {

	    String output = "======================================" + NEW_LINE + "# Scenario" +
                NEW_LINE + "======================================" + NEW_LINE;
        output += (isLegalCrossing())? "Legal Crossing: yes" + NEW_LINE : "Legal Crossing: no" +
                NEW_LINE;

        output += "Passengers (" + getPassengerCount() + ")" + NEW_LINE;
        for (int i = 0; i < getPassengerCount(); i++) {
            output += "- " + passengers.get(i) + NEW_LINE;
        }

        output += "Pedestrians (" + getPedestrianCount() + ")" +
                (getPedestrianCount() == 0 ? "" : NEW_LINE);
        for (int i = 0; i < getPedestrianCount(); i++) {
            output += "- " + pedestrians.get(i) + (i == getPedestrianCount() - 1 ? "" : NEW_LINE);
        }

        return output;
    }
    
}