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
        for (Persona pass : passengers) {
            this.passengers.add(pass);
        }

        this.pedestrians = new ArrayList<Persona>();
        for (Persona pedes : pedestrians) {
            this.pedestrians.add(pedes);
        }

        this.isLegalCrossing = isLegalCrossing;
    }

    // accessor methods
    public boolean hasYouInCar() {
	    for (Persona pass : passengers) {
	        if (pass instanceof Human && ((Human) pass).isYou()) {
	            return true;
            }
        }
	    return false;
    }
    public boolean hasYouInLane() {
        for (Persona pedes : pedestrians) {
            if (pedes instanceof Human && ((Human) pedes).isYou()) {
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