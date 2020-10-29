package ethicalengine;

import java.util.Random;

public class ScenarioGenerator {

    // constant variables
    private static final int DEFAULT_MIN_COUNT = 1;
    private static final int DEFAULT_MAX_COUNT = 5;
    private static final int AGE_CEILING = 120;
    private static final int NUM_OF_PROFESSIONS = 7;
    private static final int NUM_OF_GENDERS = 2;
    private static final int NUM_OF_BODY_TYPES = 3;

    // instance variables
    private long seed;
    private int passengerCountMinimum;
    private int passengerCountMaximum;
    private int pedestrianCountMinimum;
    private int pedestrianCountMaximum;
    private Random random;

    // constructors
    public ScenarioGenerator() {
        random = new Random();
        seed = random.nextLong();
        random.setSeed(seed);

        this.passengerCountMinimum = DEFAULT_MIN_COUNT;
        this.passengerCountMaximum = DEFAULT_MAX_COUNT;
        this.pedestrianCountMinimum = DEFAULT_MIN_COUNT;
        this.pedestrianCountMaximum = DEFAULT_MAX_COUNT;
    }
    public ScenarioGenerator(long seed) {
        this(seed, DEFAULT_MIN_COUNT, DEFAULT_MAX_COUNT, DEFAULT_MIN_COUNT, DEFAULT_MAX_COUNT);
    }
    public ScenarioGenerator(long seed, int passengerCountMinimum, int passengerCountMaximum,
                             int pedestrianCountMinimum, int pedestrianCountMaximum) {

        this.seed = seed;
        random = new Random(seed);

        this.passengerCountMinimum = passengerCountMinimum;

        if (passengerCountMaximum >= passengerCountMinimum) {
            this.passengerCountMaximum = passengerCountMaximum;
        } else {
            this.passengerCountMaximum = passengerCountMinimum; // set max equal to min if max is smaller than min
        }

        this.pedestrianCountMinimum = pedestrianCountMinimum;

        if (pedestrianCountMaximum >= pedestrianCountMinimum) {
            this.pedestrianCountMaximum = pedestrianCountMaximum;
        } else {
            this.pedestrianCountMaximum = pedestrianCountMinimum; // set max equal to min if max is smaller than min
        }
    }

    // mutator methods
    public void setPassengerCountMin(int min) {
        passengerCountMinimum = min;
    }
    public void setPassengerCountMax(int max) {
        if (max >= passengerCountMinimum) {
            passengerCountMaximum = max;
        } else {
            passengerCountMaximum = passengerCountMinimum;
        }
    }
    public void setPedestrianCountMin(int min) {
        pedestrianCountMinimum = min;
    }
    public void setPedestrianCountMax(int max) {
        if (max >= pedestrianCountMinimum) {
            pedestrianCountMaximum = max;
        } else {
            pedestrianCountMaximum = pedestrianCountMinimum;
        }
    }

    public Human getRandomHuman() {

        int age = random.nextInt(AGE_CEILING);
        Human.Profession profession = Human.Profession.values()[random.nextInt(NUM_OF_PROFESSIONS)];
        Persona.Gender gender = Persona.Gender.values()[random.nextInt(NUM_OF_GENDERS)];
        Persona.BodyType bodyType = Persona.BodyType.values()[random.nextInt(NUM_OF_BODY_TYPES)];
        boolean isPregnant = random.nextBoolean();

        return new Human(age, profession, gender, bodyType, isPregnant);
    }

    public Animal getRandomAnimal() {

        String[] speciesList = {"dog", "cat", "possum", "fox", "kangaroo", "dingo", "wombat",
                "wallaby", "echidna", "quokka"};

        int age = random.nextInt(AGE_CEILING);
        Persona.Gender gender = Persona.Gender.values()[random.nextInt(NUM_OF_GENDERS)];
        Persona.BodyType bodyType = Persona.BodyType.values()[random.nextInt(NUM_OF_BODY_TYPES)];
        String species = speciesList[random.nextInt(speciesList.length)];
        boolean isPet = random.nextBoolean();

        return new Animal(age, gender, bodyType, species, isPet);
    }

    public Scenario generate() {

        // create random passengers array
        Persona[] passengers = new Persona[passengerCountMinimum +
                random.nextInt(passengerCountMaximum - passengerCountMinimum + 1)];
        int humanPassengerCount = random.nextInt(passengers.length + 1);
        for (int i = 0; i < passengers.length; i++) {
            if (i < humanPassengerCount) {
                passengers[i] = getRandomHuman();
            } else {
                passengers[i] = getRandomAnimal();
            }
        }

        // create random pedestrians array
        Persona[] pedestrians = new Persona[pedestrianCountMinimum +
                random.nextInt(pedestrianCountMaximum - pedestrianCountMinimum + 1)];
        int humanPedestrianCount = random.nextInt(pedestrians.length + 1);
        for (int i = 0; i < pedestrians.length; i++) {
            if (i < humanPedestrianCount) {
                pedestrians[i] = getRandomHuman();
            } else {
                pedestrians[i] = getRandomAnimal();
            }
        }

        boolean youInScenario = random.nextBoolean();   // decides if you in scenario or not
        // when you in scenario, randomly assign to passenger or pedestrian
        if (youInScenario) {
            int rand = random.nextInt(humanPassengerCount + humanPedestrianCount);
            if (rand < humanPassengerCount) {
                ((Human) passengers[rand]).setAsYou(true);
            } else {
                ((Human) pedestrians[rand - humanPassengerCount]).setAsYou(true);
            }
        }

        boolean isLegalCrossing = random.nextBoolean(); // decides if green or red light

        return new Scenario(passengers, pedestrians, isLegalCrossing);
    }
}