import ethicalengine.*;

import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * COMP90041, Sem2, 2020: Final Project: A skeleton code for you to update
 * @author: tilman.dingler@unimelb.edu.au
 */
public class EthicalEngine {

    // constant variables
    private static final String TAB = "\t";
    private static final String SPLIT_BY = ",";
    private static final String WELCOME_FILEPATH = "welcome.ascii";
    private static final String DEFAULT_SPECIES = "dog";
    private static final String USER_AUDIT_TYPE = "User";
    private static final String USER_FILEPATH = "user.log";
    private static final int MAX_NUM_OF_PASSENGERS = 7;
    private static final int MAX_NUM_OF_PEDESTRIANS = 10;
    private static final int NUM_OF_COLUMNS = 10;
    private static final int NUM_OF_GENERATED_SCENARIOS = 100;
    private static final int NUM_OF_SCENARIOS_TO_SHOW = 3;
    private static final int DEFAULT_AGE = 30;
    private static final int LIGHT_INDEX = 9;
    private static final int HIGH_SCORE = 5;
    private static final int LOW_SCORE = 2;
    private static final String[] CHARACTERISTICS = {"adult", "animal", "athletic", "average",
            "baby", "bird", "builder", "cat", "ceo", "child", "criminal", "doctor", "dog", "engineer",
            "female", "ferret", "homeless", "human", "male", "overweight", "pet", "pregnant",
            "senior", "unemployed", "you"};
    private static final double[] CHARACTERISTICS_SCORES = {0.5, 0, 0.5, 0.5, 2, 0, 0.5, 0.5, 0.5, 2,
            0, 1, 0, 0.5, 0, 0, 0.5, 1, 0, 0, 3, 2, 0, 0.5, 0};

    // enumerated variable
    public enum Decision {PASSENGERS, PEDESTRIANS};

    // instance variables
    private boolean configuration;
    private boolean consent;
    private boolean interactiveMode;
    private String configFilepath;
    private String resultsFilepath;
    private ArrayList<Scenario> scenarioList;

    public EthicalEngine() {
        configuration = false;
        consent = false;
        interactiveMode = true;
        configFilepath = null;
        resultsFilepath = null;
        scenarioList = new ArrayList<Scenario>();
    }

    // accessor methods
    public boolean getConfiguration() {
        return configuration;
    }
    public boolean getConsent() {
        return consent;
    }
    public boolean getInteractiveMode() {
        return interactiveMode;
    }
    public String getConfigFilepath() {
        return configFilepath;
    }
    public String getResultsFilepath() {
        return resultsFilepath;
    }
    public ArrayList<Scenario> getScenarioList() {
        return new ArrayList<Scenario>(scenarioList);
    }

    // mutator methods
    public void setConfiguration(boolean bool) {
        configuration = bool;
    }
    public void setConsent(boolean bool) {
        consent = bool;
    }
    public void setInteractiveMode(boolean bool) {
        interactiveMode = bool;
    }
    public void setConfigFilepath(String configFilepath) {
        this.configFilepath = configFilepath;
    }
    public void setResultsFilepath(String resultsFilepath) {
        this.resultsFilepath = resultsFilepath;
    }

    public void importScenarios() {
        try {
            BufferedReader inputStream = new BufferedReader(new FileReader(getConfigFilepath()));

            inputStream.readLine(); // discard header row

            Scenario scenario;
            boolean isLegalCrossing = false;
            ArrayList<Persona> passengerList = new ArrayList<Persona>();
            ArrayList<Persona> pedestrianList = new ArrayList<Persona>();

            int row = 2;
            String line = inputStream.readLine();
            String[] values;
            while (line != null) {

                values = line.split(SPLIT_BY);

                Persona persona = new Human(); // to keep compiler happy
                String personaType = "human";
                Persona.Gender gender = Persona.Gender.UNKNOWN;
                int age = DEFAULT_AGE;
                Persona.BodyType bodyType = Persona.BodyType.UNSPECIFIED;
                Human.Profession profession = Human.Profession.NONE;
                boolean pregnant = false;
                boolean isYou = false;
                String species = DEFAULT_SPECIES;
                boolean isPet = false;
                String role = "pedestrian";

                if (values[0].startsWith("scenario")) {

                    if (row > 2) {
                        // create new scenario and add to scenarioList
                        Persona[] passengers = new Persona[passengerList.size()];
                        passengers = passengerList.toArray(passengers);
                        Persona[] pedestrians = new Persona[pedestrianList.size()];
                        pedestrians = pedestrianList.toArray(pedestrians);

                        scenario = new Scenario(passengers, pedestrians, isLegalCrossing);
                        scenarioList.add(scenario);

                        passengerList = new ArrayList<Persona>();
                        pedestrianList = new ArrayList<Persona>();
                    }

                    if (values[0].substring(LIGHT_INDEX).equals("green")) {
                        isLegalCrossing = true;
                    } else if (values[0].substring(LIGHT_INDEX).equals("red")) {
                        isLegalCrossing = false;
                    }

                    line = inputStream.readLine();
                    row++;
                    continue;

                } else {

                    // catch if number of values is not equal to 10
                    try {
                        if (values.length != NUM_OF_COLUMNS) {
                            throw new InvalidDataFormatException("WARNING: invalid data format in " +
                                    "config file in line " + row);
                        }
                    } catch (InvalidDataFormatException e) {
                        //TODO : discard that line for entire scenario? then what happens to the line count?
                        System.out.println(e.getMessage());
                        line = inputStream.readLine();
                        row++;
                        continue;
                    }

                    for (int i = 0; i < values.length; i++) {

                        switch (i) {
                            case 0:
                                try {
                                    switch (values[i].toLowerCase()) {
                                        case "human":
                                            personaType = "human";
                                            break;
                                        case "animal":
                                            personaType = "animal";
                                            break;
                                        default:
                                            throw new InvalidCharacteristicException("WARNING: " +
                                                    "invalid characteristic in config file in line "
                                                    + row);
                                    }
                                } catch (InvalidCharacteristicException e) {
                                    System.out.println(e.getMessage());
                                    personaType = "human";
                                }
                                break;
                            case 1:
                                try {
                                    switch (values[i].toLowerCase()) {
                                        case "male":
                                            gender = Persona.Gender.MALE;
                                            break;
                                        case "female":
                                            gender = Persona.Gender.FEMALE;
                                            break;
                                        default:
                                            throw new InvalidCharacteristicException("WARNING: " +
                                                    "invalid characteristic in config file in line "
                                                    + row);
                                    }
                                } catch (InvalidCharacteristicException e) {
                                    System.out.println(e.getMessage());
                                    gender = Persona.Gender.UNKNOWN;
                                }
                                break;
                            case 2:
                                try {
                                    age = Integer.parseInt(values[i]);
                                } catch (NumberFormatException e) {
                                    System.out.println("WARNING: invalid number format in config " +
                                            "file in line " + row);
                                }
                                break;
                            case 3:
                                try {
                                    switch (values[i].toLowerCase()) {
                                        case "average":
                                            bodyType = Persona.BodyType.AVERAGE;
                                            break;
                                        case "athletic":
                                            bodyType = Persona.BodyType.ATHLETIC;
                                            break;
                                        case "overweight":
                                            bodyType = Persona.BodyType.OVERWEIGHT;
                                            break;
                                        case "":
                                            bodyType = Persona.BodyType.UNSPECIFIED;
                                            break;
                                        default:
                                            throw new InvalidCharacteristicException("WARNING: " +
                                                    "invalid characteristic in config file in line "
                                                    + row);
                                    }
                                } catch (InvalidCharacteristicException e) {
                                    System.out.println(e.getMessage());
                                    bodyType = Persona.BodyType.UNSPECIFIED;
                                }
                                break;
                            case 4:
                                if (personaType.equalsIgnoreCase("human")) {
                                    try {
                                        switch (values[i].toLowerCase()) {
                                            case "doctor":
                                                profession = Human.Profession.DOCTOR;
                                                break;
                                            case "ceo":
                                                profession = Human.Profession.CEO;
                                                break;
                                            case "criminal":
                                                profession = Human.Profession.CRIMINAL;
                                                break;
                                            case "homeless":
                                                profession = Human.Profession.HOMELESS;
                                                break;
                                            case "unemployed":
                                                profession = Human.Profession.UNEMPLOYED;
                                                break;
                                            case "engineer":
                                                profession = Human.Profession.ENGINEER;
                                                break;
                                            case "builder":
                                                profession = Human.Profession.BUILDER;
                                                break;
                                            case "":
                                                profession = Human.Profession.NONE;
                                                break;
                                            default:
                                                throw new InvalidCharacteristicException("WARNING: " +
                                                        "invalid characteristic in config file in line "
                                                        + row);
                                        }
                                    } catch (InvalidCharacteristicException e) {
                                        System.out.println(e.getMessage());
                                        profession = Human.Profession.NONE;
                                    }
                                }
                                break;
                            case 5:
                                try {
                                    switch (values[i].toLowerCase()) {
                                        case "true":
                                            pregnant = true;
                                            break;
                                        case "false":
                                            pregnant = false;
                                            break;
                                        default:
                                            throw new InvalidCharacteristicException("WARNING: " +
                                                    "invalid characteristic in config file in line "
                                                    + row);
                                    }
                                } catch (InvalidCharacteristicException e) {
                                    System.out.println(e.getMessage());
                                    pregnant = false;
                                }
                                break;
                            case 6:
                                try {
                                    switch (values[i].toLowerCase()) {
                                        case "true":
                                            isYou = true;
                                            break;
                                        case "false":
                                            isYou = false;
                                            break;
                                        default:
                                            throw new InvalidCharacteristicException("WARNING: " +
                                                    "invalid characteristic in config file in line "
                                                    + row);
                                    }
                                } catch (InvalidCharacteristicException e) {
                                    System.out.println(e.getMessage());
                                    isYou = false;
                                }
                                break;
                            case 7:
                                if (personaType.equalsIgnoreCase("animal")) {
                                    species = values[i].toLowerCase();
                                    // TODO : do i need an exception here? since I decided the animal species
                                }
                                break;
                            case 8:
                                if (personaType.equalsIgnoreCase("animal")) {
                                     try {
                                         switch (values[i].toLowerCase()) {
                                             case "true":
                                                 isPet = true;
                                                 break;
                                             case "false":
                                                 isPet = false;
                                                 break;
                                             default:
                                                 throw new InvalidCharacteristicException("WARNING: " +
                                                         "invalid characteristic in config file in line "
                                                         + row);
                                         }
                                     } catch (InvalidCharacteristicException e) {
                                         System.out.println(e.getMessage());
                                         isPet = false;
                                     }
                                }
                                break;
                            case 9:
                                try {
                                    switch (values[i].toLowerCase()) {
                                        case "passenger":
                                            role = "passenger";
                                            break;
                                        case "pedestrian":
                                            role = "pedestrian";
                                            break;
                                        default:
                                            throw new InvalidCharacteristicException("WARNING: " +
                                                    "invalid characteristic in config file in line "
                                                    + row);
                                    }
                                } catch (InvalidCharacteristicException e) {
                                    System.out.println(e.getMessage());
                                    role = "passenger";
                                }
                                break;
                        }
                    }
                }

                if (personaType.equalsIgnoreCase("human")) {
                    persona = new Human(age, profession, gender, bodyType, pregnant);
                    ((Human) persona).setAsYou(isYou);
                } else if (personaType.equalsIgnoreCase("animal")) {
                    persona = new Animal(age, gender, bodyType, species, isPet);
                }

                if (role.equalsIgnoreCase("passenger")) {
                    passengerList.add(persona);
                } else if (role.equalsIgnoreCase("pedestrian")) {
                    pedestrianList.add(persona);
                }

                line = inputStream.readLine();
                row++;

                if (line == null) {
                    // create new scenario and add to scenarioList
                    Persona[] passengers = new Persona[passengerList.size()];
                    passengers = passengerList.toArray(passengers);
                    Persona[] pedestrians = new Persona[pedestrianList.size()];
                    pedestrians = pedestrianList.toArray(pedestrians);

                    scenario = new Scenario(passengers, pedestrians, isLegalCrossing);
                    scenarioList.add(scenario);
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("Could not find file");
        } catch (IOException e) {
            System.err.println("Could not read from file");
        }
    }
    public void generateScenarios() {
        ScenarioGenerator generator = new ScenarioGenerator();
        generator.setPassengerCountMax(MAX_NUM_OF_PASSENGERS);
        generator.setPedestrianCountMax(MAX_NUM_OF_PEDESTRIANS);
        for (int i = 0; i < NUM_OF_GENERATED_SCENARIOS; i++) {
            scenarioList.add(generator.generate());
        }
    }
    public void interact(Scanner keyboard) {

        Audit audit = new Audit();
        audit.setAuditType(USER_AUDIT_TYPE);

        Scenario[] scenarios = new Scenario[NUM_OF_SCENARIOS_TO_SHOW];
        for (int i = 0; i < scenarioList.size(); i++) {

            scenarios[(i+1) % 3] = scenarioList.get(i);

            System.out.println(scenarios[(i+1) % 3]);
            System.out.println("Who should be saved? (passenger(s) [1] or pedestrian(s) [2])");
            String answer = keyboard.next();
            Decision decision = Decision.PASSENGERS;
            if (answer.equalsIgnoreCase("passenger") || answer.equalsIgnoreCase("passengers") ||
                    answer.equalsIgnoreCase("1")) {
                decision = Decision.PASSENGERS;
            } else if (answer.equalsIgnoreCase("pedestrian") || answer.equalsIgnoreCase("pedestrians") ||
                    answer.equalsIgnoreCase("2")) {
                decision = Decision.PEDESTRIANS;
            }
            // TODO: what to do with other outputs?

            audit.run(scenarios[(i+1) % 3], decision);

            if ((i+1) % 3 == 0 && i < scenarioList.size() - 1) {

                audit.printStatistic();

                System.out.println("Would you like to continue? (yes/no)");
                String cont = keyboard.next();
                // TODO: what if answer is neither yes nor no
                if (cont.equalsIgnoreCase("no")) {
                    if (consent) {
                        audit.printToFile(USER_FILEPATH);
                    }
                    System.out.println("That's all. Press Enter to quit.");
                    keyboard.nextLine();
                    String exit = keyboard.nextLine();
                    if (exit.isEmpty()) {
                        System.exit(0);
                    }
                }
            }
        }
        audit.printStatistic();
        if (consent) {
            audit.printToFile(USER_FILEPATH);
        }
        System.out.println("That's all. Press Enter to quit.");
        keyboard.nextLine();
        String exit = keyboard.nextLine();
        if (exit.isEmpty()) {
            System.exit(0);
        }
    }


    /**
     * Decides whether to save the passengers or the pedestrians
     * @param Scenario scenario: the ethical dilemma
     * @return Decision: which group to save
     */
    public static Decision decide(Scenario scenario) {

        // scenario characteristics used: passenger count, pedestrian count, legality of crossing,
        // whether you in car, whether you in lane

        double passengerScore = 0;
        double pedestrianScore = 0;

        if (scenario.getPassengerCount() >= scenario.getPedestrianCount()) {
            passengerScore += 2 * (scenario.getPassengerCount() - scenario.getPedestrianCount());
        } else {
            pedestrianScore += 2 * (scenario.getPedestrianCount() - scenario.getPassengerCount());
        }

        if (scenario.isLegalCrossing()) {
            pedestrianScore += HIGH_SCORE;
        } else {
            passengerScore += HIGH_SCORE;
        }

        if (scenario.hasYouInCar()) {
            passengerScore += LOW_SCORE;
        } else if (scenario.hasYouInLane()) {
            pedestrianScore += LOW_SCORE;
        }

        for (Persona passenger : scenario.getPassengers()) {
            for (int i = 0; i < passenger.getCharacteristics().length; i++) {
                int index = Arrays.binarySearch(CHARACTERISTICS, passenger.getCharacteristics()[i]);
                if (index >= 0) {
                    passengerScore += CHARACTERISTICS_SCORES[index];
                }
            }
        }
        for (Persona pedestrian : scenario.getPedestrians()) {
            for (int i = 0; i < pedestrian.getCharacteristics().length; i++) {
                int index = Arrays.binarySearch(CHARACTERISTICS,
                        pedestrian.getCharacteristics()[i]);
                if (index >= 0) {
                    passengerScore += CHARACTERISTICS_SCORES[index];
                }
            }
        }

        if (passengerScore >= pedestrianScore) {
            return Decision.PASSENGERS;
        } else {
            return Decision.PEDESTRIANS;
        }
    }

    public static void main(String[] args) {

        Scanner keyboard = new Scanner(System.in);
        EthicalEngine engine = new EthicalEngine();

        // output welcome.ascii file
        try {
            BufferedReader inputStream = new BufferedReader(new FileReader(WELCOME_FILEPATH));

            String line = inputStream.readLine();
            while (line != null) {
                System.out.println(line);
                line = inputStream.readLine();
            }

        } catch (FileNotFoundException e) {
            System.err.println("Could not find file");
        } catch (IOException e) {
            System.err.println("Could not read from file");
        }


        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("-h") || args[i].equals("--help")) {
                printHelp();

            } else if (args[i].equals("-c") || args[i].equals("--config")) {
                // print help when flag is last in args or next arg is not a csv filepath
                if (i == args.length - 1 || ! args[i+1].endsWith(".csv")) {
                    printHelp();
                } else {

                    File fileObject = new File(args[i+1]);
                    try {
                        // TODO: check if isFile or exists or isDirectory
                        if (fileObject.isFile()) {
                            engine.setConfigFilepath(args[i+1]);
                        } else {
                            throw new FileNotFoundException("ERROR: could not find config file.");
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println(e.getMessage());
                        System.exit(0);
                    }

                    engine.setConfiguration(true);
                }
                i++;    // skip next arg

            } else if (args[i].equals("-r") || args[i].equals("--results")) {
                engine.setResultsFilepath(args[i+1]);
                i++;    // skip next arg

            } else if (args[i].equals("-i") || args[i].equals("--interactive")) {
                engine.setInteractiveMode(true);
            }
        }

        // ask for consent
        if (engine.getInteractiveMode()) {
            System.out.println("Do you consent to have your decisions saved to a file? (yes/no)");

            while (true) {
                try {
                    String answer = keyboard.next();
                    if (answer.equalsIgnoreCase("yes")) {
                        engine.setConsent(true);
                    } else if (answer.equalsIgnoreCase("no")) {
                        engine.setConsent(false);
                    } else {
                        throw new InvalidInputException("Invalid response. Do you consent to have " +
                                "your decisions saved to a file? (yes/no)");
                    }

                    break;

                } catch (InvalidInputException e) {
                    System.out.println();
                    System.out.println(e.getMessage());
                }
            }
        }

        // configuration
        if (engine.getConfiguration()) {

            engine.importScenarios();   // read scenarios from csv file

            if (engine.getInteractiveMode()) {

                engine.interact(keyboard);

            } else {

                Scenario[] scenarios = new Scenario[engine.getScenarioList().size()];
                scenarios = engine.getScenarioList().toArray(scenarios);

                Audit audit = new Audit(scenarios);
                audit.run();
                audit.printStatistic();
                audit.printToFile(engine.getResultsFilepath());
            }

        } else {

            if (engine.getInteractiveMode()) {

                engine.generateScenarios(); // generate 100 random scenarios
                engine.interact(keyboard);

            } else {
                Audit audit = new Audit();
                audit.run(NUM_OF_GENERATED_SCENARIOS);
                audit.printToFile(engine.getResultsFilepath());
            }
        }

    }

    private static void printHelp() {
        System.out.println("EthicalEngine - COMP90041 - Final Project");
        System.out.println("Usage: java EthicalEngine [arguments]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("-c or --config" + TAB + "Optional: path to config file");
        System.out.println("-h or --help" + TAB + "Print Help (this message) and exit");
        System.out.println("-r or --results" + TAB + "Optional: path to results log file");
        System.out.print("-i or --interactive" + TAB + "Optional: launches interactive mode");
        // TODO: check why using constant variable tab doesn't match out_help
        System.exit(0);
    }

}