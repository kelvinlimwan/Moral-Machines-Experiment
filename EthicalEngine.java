import ethicalengine.*;

/**
 * COMP90041, Sem2, 2020: Final Project: A skeleton code for you to update
 * @author: tilman.dingler@unimelb.edu.au
 */
public class EthicalEngine {

    public enum Decision {PASSENGERS, PEDESTRIANS};

    /**
     * Decides whether to save the passengers or the pedestrians
     * @param Scenario scenario: the ethical dilemma
     * @return Decision: which group to save
     */
    public static Decision decide(Scenario scenario) {
        // TODO: take into account at least 5 scenario characteristics and improve decision
        if (scenario.getPassengerCount() >= scenario.getPedestrianCount()) {
            return Decision.PASSENGERS;
        } else {
            return Decision.PEDESTRIANS;
        }
    }

    public static void main(String[] args) {
    	//System.out.println(decide(null).toString() + " survive.");
        Persona[] passengers = new Persona[2];
        passengers[0] = new Human(5, Persona.Gender.MALE, Persona.BodyType.AVERAGE);
        ((Human) passengers[0]).setAsYou(true);
        passengers[1] = new Human(40, Human.Profession.CRIMINAL, Persona.Gender.FEMALE, Persona.BodyType.ATHLETIC, true);

        Persona[] pedestrians = new Persona[1];
        pedestrians[0] = new Animal("DOGGO");
        ((Animal) pedestrians[0]).setPet(true);

        Scenario scen = new Scenario(passengers, pedestrians, true);
        System.out.println(scen);
    }
}