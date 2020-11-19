package ethicalengine;

import java.util.Random;

/**
 * ScenarioGenerator is a class that generates random scenarios; a scenario with a random number of
 * passengers, a random number of pedestrians, random personas and a random crossing.
 * @author Kelvin Lim Wan
 */
public class ScenarioGenerator {

    // constant variables
    private static final String[] SPECIES_LIST = {"dog", "cat", "bird", "possum", "kangaroo"};
    private static final int DEFAULT_MIN_COUNT = 1;
    private static final int DEFAULT_MAX_COUNT = 5;
    private static final int HUMAN_AGE_CEILING = 120;
    private static final int ANIMAL_AGE_CEILING = 20;
    private static final int NUM_OF_GENDERS = 2;
    private static final int NUM_OF_BODY_TYPES = 3;
    private static final int NUM_OF_PROFESSIONS = 7;

    // instance variables
    private Random random;
    private int passengerCountMinimum;
    private int passengerCountMaximum;
    private int pedestrianCountMinimum;
    private int pedestrianCountMaximum;

    /**
     * Creates a scenario generator with a Random object with a completely random seed, a default
     * minimum number of passengers, maximum number of passengers, minimum number of pedestrians and
     * maximum number of pedestrians.
     */
    public ScenarioGenerator() {
        random = new Random();
        this.passengerCountMinimum = DEFAULT_MIN_COUNT;
        this.passengerCountMaximum = DEFAULT_MAX_COUNT;
        this.pedestrianCountMinimum = DEFAULT_MIN_COUNT;
        this.pedestrianCountMaximum = DEFAULT_MAX_COUNT;
    }

    /**
     * Creates a scenario generator with a Random object with a specified seed, a default minimum
     * number of passengers, maximum number of passengers, minimum number of pedestrians and maximum
     * number of pedestrians.
     * @param seed the specified seed.
     */
    public ScenarioGenerator(long seed) {
        this(seed, DEFAULT_MIN_COUNT, DEFAULT_MAX_COUNT, DEFAULT_MIN_COUNT, DEFAULT_MAX_COUNT);
    }

    /**
     * Creates a scenario generator with a Random object with a specified seed, a specified minimum
     * number of passengers, maximum number of passengers, minimum number of pedestrians and maximum
     * number of pedestrians.
     * @param seed the specified seed.
     * @param passengerCountMinimum the specified minimum number of passengers.
     * @param passengerCountMaximum the specified maximum number of passengers.
     * @param pedestrianCountMinimum the specified minimum number of pedestrians.
     * @param pedestrianCountMaximum the specified maximum number of pedestrians.
     */
    public ScenarioGenerator(long seed, int passengerCountMinimum, int passengerCountMaximum,
                             int pedestrianCountMinimum, int pedestrianCountMaximum) {
        random = new Random(seed);

        this.passengerCountMinimum = passengerCountMinimum;
        // since max cannot be smaller than min
        this.passengerCountMaximum = Math.max(passengerCountMaximum, passengerCountMinimum);

        this.pedestrianCountMinimum = pedestrianCountMinimum;
        // since max cannot be smaller than min
        this.pedestrianCountMaximum = Math.max(pedestrianCountMaximum, pedestrianCountMinimum);
    }

    /**
     * Sets the scenario generator's minimum number of passengers.
     * @param min the minimum number of passengers to set the scenario generator's minimum number of
     *            passengers to.
     */
    public void setPassengerCountMin(int min) {
        passengerCountMinimum = min;
    }

    /**
     * Sets the scenario generator's maximum number of passengers.
     * @param max the maximum number of passengers to set the scenario generator's maximum number of
     *            passengers to.
     */
    public void setPassengerCountMax(int max) {
        passengerCountMaximum = Math.max(max, passengerCountMinimum);
    }

    /**
     * Sets the scenario generator's minimum number of pedestrians.
     * @param min the minimum number of pedestrians to set the scenario generator's minimum number
     *            of pedestrians to.
     */
    public void setPedestrianCountMin(int min) {
        pedestrianCountMinimum = min;
    }

    /**
     * Sets the scenario generator's maximum number of pedestrians.
     * @param max the maximum number of pedestrians to set the scenario generator's maximum number
     *            of pedestrians to.
     */
    public void setPedestrianCountMax(int max) {
        pedestrianCountMaximum = Math.max(max, pedestrianCountMinimum);
    }

    /**
     * Creates a random Human object with random characteristics.
     * @return the random human.
     */
    public Human getRandomHuman() {
        int age = random.nextInt(HUMAN_AGE_CEILING);
        Human.Profession profession = Human.Profession.values()[random.nextInt(NUM_OF_PROFESSIONS)];
        Persona.Gender gender = Persona.Gender.values()[random.nextInt(NUM_OF_GENDERS)];
        Persona.BodyType bodyType = Persona.BodyType.values()[random.nextInt(NUM_OF_BODY_TYPES)];
        boolean isPregnant = random.nextBoolean();

        return new Human(age, profession, gender, bodyType, isPregnant);
    }

    /**
     * Creates a random Animal object with random characteristics.
     * @return the random animal.
     */
    public Animal getRandomAnimal() {

        int age = random.nextInt(ANIMAL_AGE_CEILING);
        Persona.Gender gender = Persona.Gender.values()[random.nextInt(NUM_OF_GENDERS)];
        Persona.BodyType bodyType = Persona.BodyType.values()[random.nextInt(NUM_OF_BODY_TYPES)];
        String species = SPECIES_LIST[random.nextInt(SPECIES_LIST.length)];
        boolean isPet = random.nextBoolean();

        return new Animal(age, gender, bodyType, species, isPet);
    }

    /**
     * Generates a random scenario, with a random number of passengers between the minimum and
     * maximum number of passengers set, a random number of pedestrians between the minimum and
     * maximum number of pedestrians set, where the personas are random humans or animals with the
     * possibility that one of the humans is you and the crossing is randomly legal or not.
     * @return the random scenario.
     */
    public Scenario generate() {

        // create random passengers array
        int numOfPassengers = passengerCountMinimum + random.nextInt(passengerCountMaximum -
                passengerCountMinimum + 1);
        Persona[] passengers = new Persona[numOfPassengers];
        int humanPassengerCount = random.nextInt(passengers.length + 1);
        for (int i = 0; i < passengers.length; i++) {
            if (i < humanPassengerCount) {
                passengers[i] = getRandomHuman();
            } else {
                passengers[i] = getRandomAnimal();
            }
        }

        // create random pedestrians array
        int numOfPedestrians = pedestrianCountMinimum + random.nextInt(pedestrianCountMaximum -
                pedestrianCountMinimum + 1);
        Persona[] pedestrians = new Persona[numOfPedestrians];
        int humanPedestrianCount = random.nextInt(pedestrians.length + 1);
        for (int i = 0; i < pedestrians.length; i++) {
            if (i < humanPedestrianCount) {
                pedestrians[i] = getRandomHuman();
            } else {
                pedestrians[i] = getRandomAnimal();
            }
        }

        // when there is at least one human
        if (humanPassengerCount + humanPedestrianCount > 0) {
            // 50% chance of you being in the scenario
            if (random.nextBoolean()) {
                Human human;
                int rand = random.nextInt(humanPassengerCount + humanPedestrianCount);
                if (rand < humanPassengerCount) {
                    human = (Human) passengers[rand];
                } else {
                    int index = rand - humanPassengerCount;
                    human = (Human) pedestrians[index];
                }

                human.setAsYou(true);
            }
        }

        boolean isLegalCrossing = random.nextBoolean();

        return new Scenario(passengers, pedestrians, isLegalCrossing);
    }
}
