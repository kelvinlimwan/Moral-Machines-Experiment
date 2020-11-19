package ethicalengine;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Scenario is a class that represents a scenario that entails an autonomous car whose brakes fail
 * at a pedestrian crossing and contains the list of passengers, the list of pedestrians and whether
 * the crossing is legal (green or red).
 * @author Kelvin Lim Wan
 */
public class Scenario {

    // constant variable
    public static final String NEW_LINE = "\n";

    // instance variables
    private ArrayList<Persona> passengerList;
    private ArrayList<Persona> pedestrianList;
    private boolean isLegalCrossing;

    /**
     * Creates a scenario with the specified passengers, pedestrians and whether the crossing is
     * legal.
     * @param passengers a persona array of the specified passengers.
     * @param pedestrians a persona array of the specified pedestrians.
     * @param isLegalCrossing whether the crossing is legal.
     */
	public Scenario(Persona[] passengers, Persona[] pedestrians, boolean isLegalCrossing) {

	    passengerList = new ArrayList<Persona>();
        Collections.addAll(passengerList, passengers);

        pedestrianList = new ArrayList<Persona>();
        Collections.addAll(pedestrianList, pedestrians);

        this.isLegalCrossing = isLegalCrossing;
    }

    /**
     * Gets whether you are in the car.
     * @return whether you are in the car.
     */
    public boolean hasYouInCar() {
	    for (Persona passenger : passengerList) {
	        if (passenger instanceof Human && ((Human) passenger).isYou()) {
	            return true;
            }
        }
	    return false;
    }

    /**
     * Gets whether you are in the lane.
     * @return whether you are in the lane.
     */
    public boolean hasYouInLane() {
        for (Persona pedestrian : pedestrianList) {
            if (pedestrian instanceof Human && ((Human) pedestrian).isYou()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the scenario's passengers.
     * @return a persona array of the scenario's passengers.
     */
    public Persona[] getPassengers() {
	    Persona[] copy = new Persona[passengerList.size()];
        copy = passengerList.toArray(copy);
	    return copy;
    }

    /**
     * Gets the scenario's pedestrians.
     * @return a persona array of the scenario's pedestrians.
     */
    public Persona[] getPedestrians() {
        Persona[] copy = new Persona[pedestrianList.size()];
        copy = pedestrianList.toArray(copy);
        return copy;
    }

    /**
     * Gets whether the scenario's crossing is legal.
     * @return whether the scenario's crossing is legal.
     */
    public boolean isLegalCrossing() {
	    return isLegalCrossing;
    }

    /**
     * Gets the scenario's number of passengers.
     * @return a persona array of the scenario's number of passengers.
     */
    public int getPassengerCount() {
	    return passengerList.size();
    }

    /**
     * Gets the scenario's number of pedestrians.
     * @return a persona array of the scenario's number of pedestrians.
     */
    public int getPedestrianCount() {
	    return pedestrianList.size();
    }

    /**
     * Sets whether the crossing is legal.
     * @param isLegalCrossing the 'whether the crossing is legal' to set the scenario's 'whether the
     *                        crossing is legal' to.
     */
    public void setLegalCrossing(boolean isLegalCrossing) {
	    this.isLegalCrossing = isLegalCrossing;
    }

    /**
     * Returns a string representation of the scenario.
     * @return the string representation of the scenario.
     */
    @Override
    public String toString() {

	    String output = "======================================" + NEW_LINE + "# Scenario" +
                NEW_LINE + "======================================" + NEW_LINE +
                "Legal Crossing: " + (isLegalCrossing ? "yes" : "no") + NEW_LINE;

        // add all passengers
        output += "Passengers (" + getPassengerCount() + ")" + NEW_LINE;
        for (int i = 0; i < getPassengerCount(); i++) {
            output += "- " + passengerList.get(i) + NEW_LINE;
        }

        // add all pedestrians
        output += "Pedestrians (" + getPedestrianCount() + ")" + (getPedestrianCount() == 0 ? "" :
                NEW_LINE);
        for (int i = 0; i < getPedestrianCount(); i++) {
            output += "- " + pedestrianList.get(i) + (i == getPedestrianCount() - 1 ? "" :
                    NEW_LINE);
        }

        return output;
    }
    
}