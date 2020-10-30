package ethicalengine;

import java.util.ArrayList;

public class Scenario {

    // instance variables
    private ArrayList<Persona> passengers;
    private ArrayList<Persona> pedestrians;
    private boolean isLegalCrossing;

    // constructor
	public Scenario(Persona[] passengers, Persona[] pedestrians, boolean isLegalCrossing) {

	    this.passengers = new ArrayList<Persona>();
        for (Persona passenger : passengers) {
            this.passengers.add(passenger);
        }

        this.pedestrians = new ArrayList<Persona>();
        for (Persona pedestrian : pedestrians) {
            this.pedestrians.add(pedestrian);
        }

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

    public int getTotalOfHumanAges() {
	    int total = 0;

	    for (Persona passenger : passengers) {
	        if (passenger instanceof Human) {
	            total += passenger.getAge();
            }
        }
	    for (Persona pedestrian : pedestrians) {
	        if (pedestrian instanceof Human) {
	            total += pedestrian.getAge();
            }
        }

	    return total;
    }

    public int getHumanCount() {
        int count = 0;

        for (Persona passenger : passengers) {
            if (passenger instanceof Human) {
                count++;
            }
        }
        for (Persona pedestrian : pedestrians) {
            if (pedestrian instanceof Human) {
                count++;
            }
        }

        return count;
    }

    // mutator method
    public void setLegalCrossing(boolean isLegalCrossing) {
	    this.isLegalCrossing = isLegalCrossing;
    }

    @Override
    public String toString() {

	    String output = "======================================\n# Scenario\n" +
                "======================================\n";
        output += (isLegalCrossing())? "Legal Crossing: yes\n" : "Legal Crossing: no\n";

        output += "Passengers (" + getPassengerCount() + ")\n";
        for (int i = 0; i < getPassengerCount(); i++) {
            output += "- " + passengers.get(i) + "\n";
        }

        output += "Pedestrians (" + getPedestrianCount() + ")" +
                (getPedestrianCount() == 0 ? "" : "\n");
        for (int i = 0; i < getPedestrianCount(); i++) {
            output += "- " + pedestrians.get(i) + (i == getPedestrianCount() - 1 ? "" : "\n");
        }

        return output;
    }
    
}