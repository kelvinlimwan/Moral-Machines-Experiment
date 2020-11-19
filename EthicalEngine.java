import ethicalengine.*;

import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * EthicalEngine manages the program execution by reading or generating scenarios, making or taking
 * decisions from the user, printing the summary results to the console and saving the results (with
 * consent).
 * @author Kelvin Lim Wan
 */
public class EthicalEngine {

    /**
     * Manages the program execution.
     * @param args flags that modify the program's operation.
     */
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        EthicalEngine engine = new EthicalEngine();

        // read the flags to prepare engine
        boolean exit = engine.getFlags(args);   // return true if program should terminate
        if (exit) {
            return;
        }

        try {
            if (engine.getConfigurationMode()) {

                // in configuration mode
                try {
                    engine.importScenarios();   // read scenarios from csv file
                } catch (FileNotFoundException e) {
                    System.err.println("ERROR: could not find config file.");
                    return; // naturally terminate the program
                } catch (IOException e) {
                    System.err.println("Could not read from file");
                    return; // naturally terminate the program
                }

                if (engine.getInteractiveMode()) {
                    // in interactive mode
                    engine.interactInConfig(keyboard);
                } else {
                    // not in interactive mode
                    engine.printConfigScenarios();
                }

            } else {

                // not in configuration mode
                if (engine.getInteractiveMode()) {
                    // in interactive mode
                    engine.interactAndGenerate(keyboard);
                } else {
                    // not in interactive mode
                    engine.generateScenarios();
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("ERROR: could not print results. Target directory does not exist.");
        }
    }

    // constant variables
    private static final String TAB = "\t";
    private static final String SPLIT_BY = ",";
    private static final String WELCOME_FILEPATH = "welcome.ascii";
    private static final String DEFAULT_PERSONA_TYPE = "human";
    private static final String DEFAULT_SPECIES = "dog";
    private static final String DEFAULT_ROLE = "passenger";
    private static final String USER_AUDIT_TYPE = "User";
    private static final String ALGORITHM_AUDIT_TYPE = "Algorithm";
    private static final String USER_FILEPATH = "user.log";
    private static final int INITIAL_LINE_COUNT = 2;
    private static final int MAX_NUM_OF_PASSENGERS = 7;
    private static final int MAX_NUM_OF_PEDESTRIANS = 10;
    private static final int NUM_OF_COLUMNS = 10;
    private static final int NUM_OF_GENERATED_SCENARIOS = 100;
    private static final int NUM_OF_SCENARIOS_BEFORE_STATISTIC = 3;
    private static final int DEFAULT_AGE = 15;
    private static final int LIGHT_INDEX = 9;
    private static final int HIGH_SCORE = 5;
    private static final int LOW_SCORE = 2;
    private static final int FACTOR = 2;
    private static final String[] CHARACTERISTICS = {"adult", "athletic", "average", "baby", "ceo",
            "child", "doctor", "engineer", "homeless", "human", "pet", "pregnant", "student",
            "unemployed"};
    private static final double[] CHARACTERISTICS_SCORES = {0.5, 0.5, 0.5, 2, 1, 2, 1.5, 0.5,
            0.5, 0.5, 2, 1.5, 0.5, 0.5};

    // enumeration
    public enum Decision {PASSENGERS, PEDESTRIANS}

    // instance variables
    private boolean configurationMode;
    private boolean interactiveMode;
    private String configFilepath;
    private String resultsFilepath;
    private boolean consent;
    private ArrayList<Scenario> scenarioList;

    /**
     * Creates an ethical engine with a default configuration mode, consent, interactive mode,
     * config filepath, results filepath and empty scenario list.
     */
    public EthicalEngine() {
        configurationMode = false;
        interactiveMode = false;
        configFilepath = null;
        resultsFilepath = null;
        consent = false;
        scenarioList = new ArrayList<Scenario>();
    }

    /**
     * Gets whether the ethical engine is in configuration mode.
     * @return whether the ethical engine is in configuration mode.
     */
    public boolean getConfigurationMode() {
        return configurationMode;
    }

    /**
     * Gets whether the ethical engine is in interactive mode.
     * @return whether the ethical engine is in interactive mode.
     */
    public boolean getInteractiveMode() {
        return interactiveMode;
    }

    /**
     * Decides whether to save the passengers or the pedestrians in a pre-defined scenario using a
     * score-based system.
     * @param scenario the pre-defined scenario.
     * @return whether to save the passengers or the pedestrians.
     */
    public static Decision decide(Scenario scenario) {

        double passengerScore = 0;
        double pedestrianScore = 0;

        // consider the personas counts
        if (scenario.getPassengerCount() >= scenario.getPedestrianCount()) {
            passengerScore += FACTOR * (scenario.getPassengerCount() -
                    scenario.getPedestrianCount());
        } else {
            pedestrianScore += FACTOR * (scenario.getPedestrianCount() -
                    scenario.getPassengerCount());
        }

        // consider the crossing legality
        if (scenario.isLegalCrossing()) {
            pedestrianScore += HIGH_SCORE;
        } else {
            passengerScore += HIGH_SCORE;
        }

        // consider where you are
        if (scenario.hasYouInCar()) {
            passengerScore += LOW_SCORE;
        } else if (scenario.hasYouInLane()) {
            pedestrianScore += LOW_SCORE;
        }

        // consider each characteristic of each passenger
        for (Persona passenger : scenario.getPassengers()) {
            for (int i = 0; i < passenger.getCharacteristics().length; i++) {
                int index = Arrays.binarySearch(CHARACTERISTICS, passenger.getCharacteristics()[i]);
                if (index >= 0) {
                    passengerScore += CHARACTERISTICS_SCORES[index];
                }
            }
        }

        // consider each characteristic of each pedestrian
        for (Persona pedestrian : scenario.getPedestrians()) {
            for (int i = 0; i < pedestrian.getCharacteristics().length; i++) {
                int index = Arrays.binarySearch(CHARACTERISTICS,
                        pedestrian.getCharacteristics()[i]);
                if (index >= 0) {
                    pedestrianScore += CHARACTERISTICS_SCORES[index];
                }
            }
        }

        // return the group with the higher score
        if (passengerScore >= pedestrianScore) {
            return Decision.PASSENGERS;
        } else {
            return Decision.PEDESTRIANS;
        }
    }

    /**
     * Reads the flags and sets the program mode to be operated.
     * @param flags the flags input by the user.
     * @return whether the program should terminate.
     */
    public boolean getFlags(String[] flags) {
        boolean alreadyPrintedHelp = false;
        // get flags
        try {
            for (int i = 0; i < flags.length; i++) {
                switch (flags[i].toLowerCase()) {
                    case "-h":
                    case "--help":
                        printHelp();
                        if (i == flags.length - 1) {
                            return true; // naturally terminate the program when only help is asked
                        }
                        alreadyPrintedHelp = true;
                        break;
                    case "-c":
                    case "--config":
                        if (i == flags.length - 1 || !flags[i + 1].endsWith(".csv")) {
                            if (!alreadyPrintedHelp) {
                                printHelp();
                            }
                            return true; // naturally terminate the program
                        }
                        configurationMode = true;
                        configFilepath = flags[i + 1];
                        i++;    // skip next arg
                        break;
                    case "-r":
                    case "--results":
                        if (i == flags.length - 1 || !flags[i + 1].endsWith(".log")) {
                            if (!alreadyPrintedHelp) {
                                printHelp();
                            }
                            return true; // naturally terminate the program
                        }
                        resultsFilepath = flags[i + 1];
                        i++;    // skip next arg
                        break;
                    case "-i":
                    case "--interactive":
                        interactiveMode = true;
                        break;
                    default:
                        throw new InvalidInputException("Invalid input.");
                }
            }

        } catch (InvalidInputException e) {
            System.err.println(e.getMessage());
            return true;
        }

        return false;
    }

    /**
     * Imports the scenarios from the config filepath (in csv format) to the ethical engine.
     * @throws FileNotFoundException if the config filepath cannot be found.
     * @throws IOException if the config filepath cannot be read.
     */
    public void importScenarios() throws FileNotFoundException, IOException {

        BufferedReader inputStream = new BufferedReader(new FileReader(configFilepath));
        inputStream.readLine(); // discard header row

        boolean isLegalCrossing = true;
        ArrayList<Persona> passengerList = new ArrayList<Persona>();
        ArrayList<Persona> pedestrianList = new ArrayList<Persona>();

        int lineCount = INITIAL_LINE_COUNT;
        String line = inputStream.readLine();
        // read line by line
        while (true) {

            String[] values = line.split(SPLIT_BY, -1);

            // throw exception if number of values in line is not equal to 10
            try {

                if (values.length != NUM_OF_COLUMNS) {
                    throw new InvalidDataFormatException("WARNING: invalid data format in " +
                            "config file in line " + lineCount);
                }

                if (values[0].startsWith("scenario")) {
                    // when not the first scenario line, create scenario and add to scenario list
                    if (lineCount > INITIAL_LINE_COUNT) {
                        Persona[] passengers = new Persona[passengerList.size()];
                        passengers = passengerList.toArray(passengers);
                        Persona[] pedestrians = new Persona[pedestrianList.size()];
                        pedestrians = pedestrianList.toArray(pedestrians);

                        scenarioList.add(new Scenario(passengers, pedestrians, isLegalCrossing));

                        // reset to empty arrays
                        passengerList = new ArrayList<Persona>();
                        pedestrianList = new ArrayList<Persona>();
                    }

                    // get crossing legality for next scenario
                    try {
                        switch (values[0].substring(LIGHT_INDEX)) {
                            case "green":
                                isLegalCrossing = true;
                                break;
                            case "red":
                                isLegalCrossing = false;
                                break;
                            default:
                                throw new InvalidCharacteristicException("WARNING: invalid " +
                                        "characteristic in config file in line " + lineCount);
                        }

                    } catch (InvalidCharacteristicException e) {
                        System.err.println(e.getMessage());
                    }

                } else {

                    // pre-assign variables to their default values in case exceptions are thrown
                    Persona persona;
                    String personaType = DEFAULT_PERSONA_TYPE;
                    Persona.Gender gender = Persona.Gender.UNKNOWN;
                    int age = DEFAULT_AGE;
                    Persona.BodyType bodyType = Persona.BodyType.UNSPECIFIED;
                    Human.Profession profession = Human.Profession.NONE;
                    boolean pregnant = false;
                    boolean isYou = false;
                    String species = DEFAULT_SPECIES;
                    boolean isPet = false;
                    String role = DEFAULT_ROLE;

                    // persona type column
                    try {
                        switch (values[0].toLowerCase()) {
                            case "human":
                            case "animal":
                                personaType = values[0].toLowerCase();
                                break;
                            default:
                                throw new InvalidCharacteristicException("WARNING: invalid " +
                                        "characteristic in config file in line " + lineCount);
                        }

                    } catch (InvalidCharacteristicException e) {
                        System.err.println(e.getMessage());
                    }

                    // gender column
                    try {
                        boolean valid = false;
                        for (Persona.Gender enumGender : Persona.Gender.values()) {
                            if (values[1].equalsIgnoreCase(enumGender.toString())) {
                                gender = enumGender;
                                valid = true;
                                break;
                            }
                        }

                        // when value is not found in enumeration and it is not empty
                        if (!(valid || values[1].isEmpty())) {
                            throw new InvalidCharacteristicException("WARNING: invalid " +
                                    "characteristic in config file in line " + lineCount);
                        }

                    } catch (InvalidCharacteristicException e) {
                        System.err.println(e.getMessage());
                    }

                    // age column
                    try {
                        age = Integer.parseInt(values[2]);

                    } catch (NumberFormatException e) {
                        System.err.println("WARNING: invalid number format in config file in " +
                                "line " + lineCount);
                    }

                    // body type column
                    try {
                        boolean valid = false;
                        for (Persona.BodyType enumBodyType : Persona.BodyType.values()) {
                            if (values[3].equalsIgnoreCase(enumBodyType.toString())) {
                                bodyType = enumBodyType;
                                valid = true;
                                break;
                            }
                        }

                        // when value is not found in enumeration and it is not empty
                        if (!(valid || values[3].isEmpty())) {
                            throw new InvalidCharacteristicException("WARNING: invalid " +
                                    "characteristic in config file in line " + lineCount);
                        }

                    } catch (InvalidCharacteristicException e) {
                        System.err.println(e.getMessage());
                    }

                    // profession column
                    try {
                        boolean valid = false;
                        for (Human.Profession enumProfession : Human.Profession.values()) {
                            if (values[4].equalsIgnoreCase(enumProfession.toString())) {
                                profession = enumProfession;
                                valid = true;
                                break;
                            }
                        }

                        // when value is not found in enumeration and it is not empty
                        if (!(valid || values[4].isEmpty())) {
                            throw new InvalidCharacteristicException("WARNING: invalid " +
                                    "characteristic in config file in line " + lineCount);
                        }

                    } catch (InvalidCharacteristicException e) {
                        System.err.println(e.getMessage());
                    }

                    // pregnant column
                    try {
                        switch(values[5].toLowerCase()) {
                            case "true":
                            case "false":
                            case "":
                                pregnant = Boolean.parseBoolean(values[5]);
                                break;
                            default:
                                throw new InvalidCharacteristicException("WARNING: invalid " +
                                        "characteristic in config file in line " + lineCount);
                        }

                    } catch (InvalidCharacteristicException e) {
                        System.err.println(e.getMessage());
                    }

                    // you column
                    try {
                        switch(values[6].toLowerCase()) {
                            case "true":
                            case "false":
                            case "":
                                isYou = Boolean.parseBoolean(values[6]);
                                break;
                            default:
                                throw new InvalidCharacteristicException("WARNING: invalid " +
                                        "characteristic in config file in line " + lineCount);
                        }

                    } catch (InvalidCharacteristicException e) {
                        System.err.println(e.getMessage());
                    }

                    // species column
                    try {
                        if ((personaType.equals("animal") && values[7].isEmpty())) {
                            throw new InvalidCharacteristicException("WARNING: invalid " +
                                    "characteristic in config file in line " + lineCount);
                        } else {
                            species = values[7].toLowerCase();
                        }

                    } catch (InvalidCharacteristicException e) {
                        System.err.println(e.getMessage());
                    }

                    // pet column
                    try {
                        switch(values[8].toLowerCase()) {
                            case "true":
                            case "false":
                            case "":
                                isPet = Boolean.parseBoolean(values[8]);
                                break;
                            default:
                                throw new InvalidCharacteristicException("WARNING: invalid " +
                                        "characteristic in config file in line " + lineCount);
                        }

                    } catch (InvalidCharacteristicException e) {
                        System.err.println(e.getMessage());
                    }

                    // role column
                    try {
                        switch (values[9].toLowerCase()) {
                            case "passenger":
                            case "pedestrian":
                                role = values[9].toLowerCase();
                                break;
                            default:
                                throw new InvalidCharacteristicException("WARNING: invalid " +
                                        "characteristic in config file in line " + lineCount);
                        }

                    } catch (InvalidCharacteristicException e) {
                        System.err.println(e.getMessage());
                    }


                    if (personaType.equals("human")) {
                        persona = new Human(age, profession, gender, bodyType, pregnant, isYou);
                    } else {
                        persona = new Animal(age, gender, bodyType, species, isPet);
                    }

                    if (role.equals("passenger")) {
                        passengerList.add(persona);
                    } else {
                        pedestrianList.add(persona);
                    }
                }

            } catch (InvalidDataFormatException e) {
                System.err.println(e.getMessage());
            }

            line = inputStream.readLine();
            lineCount++;

            // when all line are done reading, create scenario and add to scenario list
            if (line == null) {
                Persona[] passengers = new Persona[passengerList.size()];
                passengers = passengerList.toArray(passengers);
                Persona[] pedestrians = new Persona[pedestrianList.size()];
                pedestrians = pedestrianList.toArray(pedestrians);

                scenarioList.add(new Scenario(passengers, pedestrians, isLegalCrossing));
                break;
            }
        }
    }

    /**
     * Interacts with the user by showing scenarios from the config filepath and letting him/her
     * decide who to save, after every three scenarios, the statistics is printed to the console and
     * to the results filepath (if consented to prior) and asks whether he/she would like to
     * continue; if not, exits the program when the user presses enter (also happens when there are
     * no more scenarios to show).
     * @param keyboard the input stream to get the user's answers (from the keyboard).
     * @throws FileNotFoundException if the target directory for results does not exist.
     */
    public void interactInConfig(Scanner keyboard) throws FileNotFoundException {

        // show welcome screen and ask for consent
        welcomeUser(keyboard);

        Audit audit = new Audit();
        audit.setAuditType(USER_AUDIT_TYPE);

        for (int i = 0; i < scenarioList.size(); i++) {

            System.out.println(scenarioList.get(i));
            System.out.println("Who should be saved? (passenger(s) [1] or pedestrian(s) [2])");
            Decision decision;

            while (true) {
                try {
                    String answer = keyboard.next();
                    switch (answer) {
                        case "passenger":
                        case "passengers":
                        case "1":
                            decision = Decision.PASSENGERS;
                            break;
                        case "pedestrian":
                        case "pedestrians":
                        case "2":
                            decision = Decision.PEDESTRIANS;
                            break;
                        default:
                            throw new InvalidInputException("Invalid response. Who should be " +
                                    "saved? (passenger(s) [1] or pedestrian(s) [2])");
                    }
                    break;

                } catch (InvalidInputException e) {
                    System.err.println(e.getMessage());
                }
            }

            audit.run(scenarioList.get(i), decision);

            if ((i+1) % NUM_OF_SCENARIOS_BEFORE_STATISTIC == 0 && i < scenarioList.size() - 1) {
                // when three scenarios have been shown and answered
                audit.printStatistic();
                if (consent) {
                    audit.printToFile(resultsFilepath);
                }

                System.out.println("Would you like to continue? (yes/no)");
                while (true) {
                    try {
                        String cont = keyboard.next();

                        if (cont.equalsIgnoreCase("yes")) {
                            break;
                        } else if (cont.equalsIgnoreCase("no")) {
                            System.out.println("That's all. Press Enter to quit.");
                            keyboard.nextLine();
                            keyboard.nextLine();
                            return; // to naturally terminate the program
                        } else {
                            throw new InvalidInputException("Invalid response. Would you like to " +
                                    "continue? (yes/no)");
                        }

                    } catch (InvalidInputException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        }

        // after all scenarios have been presented
        audit.printStatistic();
        if (consent) {
            audit.printToFile(resultsFilepath);
        }
        System.out.println("That's all. Press Enter to quit.");
        keyboard.nextLine();
        keyboard.nextLine();    // to naturally terminate the program
    }

    /**
     * Interacts with the user by showing randomly-generated scenarios and letting him/her decide
     * who to save, after every three scenarios, the statistics is printed to the console and to the
     * results filepath (if consented to prior) and asks whether he/she would like to continue; if
     * not, exits the program when the user presses enter.
     * @param keyboard the input stream to get the user's answers (from the keyboard).
     * @throws FileNotFoundException if the target directory for results does not exist.
     */
    public void interactAndGenerate(Scanner keyboard) throws FileNotFoundException {

        // show welcome screen and ask for consent
        welcomeUser(keyboard);

        ScenarioGenerator generator = new ScenarioGenerator();
        generator.setPassengerCountMax(MAX_NUM_OF_PASSENGERS);
        generator.setPedestrianCountMax(MAX_NUM_OF_PEDESTRIANS);

        Audit audit = new Audit();
        audit.setAuditType(USER_AUDIT_TYPE);

        int i = 1;
        while (true) {
            Scenario scenario = generator.generate();

            System.out.println(scenario);
            System.out.println("Who should be saved? (passenger(s) [1] or pedestrian(s) [2])");
            Decision decision;

            while (true) {
                try {
                    String answer = keyboard.next();
                    switch (answer) {
                        case "passenger":
                        case "passengers":
                        case "1":
                            decision = Decision.PASSENGERS;
                            break;
                        case "pedestrian":
                        case "pedestrians":
                        case "2":
                            decision = Decision.PEDESTRIANS;
                            break;
                        default:
                            throw new InvalidInputException("Invalid response. Who should be " +
                                    "saved? (passenger(s) [1] or pedestrian(s) [2])");
                    }
                    break;

                } catch (InvalidInputException e) {
                    System.err.println(e.getMessage());
                }
            }

            audit.run(scenario, decision);

            if (i % NUM_OF_SCENARIOS_BEFORE_STATISTIC == 0) {
                // when three scenarios have been shown and answered
                audit.printStatistic();
                if (consent) {
                    audit.printToFile(resultsFilepath);
                }

                System.out.println("Would you like to continue? (yes/no)");
                while (true) {
                    try {
                        String cont = keyboard.next();

                        if (cont.equalsIgnoreCase("yes")) {
                            break;
                        } else if (cont.equalsIgnoreCase("no")) {
                            System.out.println("That's all. Press Enter to quit.");
                            keyboard.nextLine();
                            keyboard.nextLine();
                            return; // to naturally terminate the program
                        } else {
                            throw new InvalidInputException("Invalid response. Would you like to " +
                                    "continue? (yes/no)");
                        }

                    } catch (InvalidInputException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }

            i++;
        }
    }

    /**
     * Prints the statistics from the scenarios in the config filepath to the console and to the
     * results filepath.
     * @throws FileNotFoundException if the target directory for results does not exist.
     */
    public void printConfigScenarios() throws FileNotFoundException {
        Scenario[] scenarios = new Scenario[scenarioList.size()];
        scenarios = scenarioList.toArray(scenarios);

        Audit audit = new Audit(scenarios);
        audit.setAuditType(ALGORITHM_AUDIT_TYPE);
        audit.run();
        audit.printStatistic();
        audit.printToFile(resultsFilepath);
    }

    /**
     * Generates 100 random scenarios and prints their statistics to the console and to the results
     * filepath.
     * @throws FileNotFoundException if the target directory for results does not exist.
     */
    public void generateScenarios() throws FileNotFoundException {
        Audit audit = new Audit();
        audit.setAuditType(ALGORITHM_AUDIT_TYPE);
        audit.run(NUM_OF_GENERATED_SCENARIOS);
        audit.printStatistic();
        audit.printToFile(resultsFilepath);
    }

    /**
     * Prints a help documentation to the console to tell users how to correctly call and execute
     * the program.
     */
    private static void printHelp() {
        System.out.println("EthicalEngine - COMP90041 - Final Project");
        System.out.println("Usage: java EthicalEngine [arguments]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("-c or --config" + TAB + "Optional: path to config file");
        System.out.println("-h or --help" + TAB + "Print Help (this message) and exit");
        System.out.println("-r or --results" + TAB + "Optional: path to results log file");
        System.out.println("-i or --interactive" + TAB + "Optional: launches interactive mode");
    }

    /**
     * Interacts with the user by providing background information about moral machines and getting
     * the user's consent before saving any results.
     * @param keyboard the input stream to get the user's answers (from the keyboard).
     */
    private void welcomeUser(Scanner keyboard) {

        // output welcome.ascii file
        try {
            BufferedReader inputStream = new BufferedReader(new FileReader(WELCOME_FILEPATH));

            // print line by line until file is done
            String line = inputStream.readLine();
            do {
                System.out.println(line);
                line = inputStream.readLine();
            } while (line != null);

        } catch (FileNotFoundException e) {
            System.err.println("Could not find file");
        } catch (IOException e) {
            System.err.println("Could not read from file");
        }

        // ask for consent
        System.out.println("Do you consent to have your decisions saved to a file? (yes/no)");
        while (true) {
            try {
                String answer = keyboard.next();
                if (answer.equalsIgnoreCase("yes")) {
                    if (resultsFilepath == null) {
                        resultsFilepath = USER_FILEPATH;
                    }
                    consent = true;
                } else if (answer.equalsIgnoreCase("no")) {
                    consent = false;
                } else {
                    throw new InvalidInputException("Invalid response. Do you consent to have " +
                            "your decisions saved to a file? (yes/no)");
                }
                break;

            } catch (InvalidInputException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}