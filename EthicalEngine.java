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
        boolean exit = false;   // to exit program

        // get flags
        for (int i = 0; i < args.length; i++) {

            // print help when flag is last in args or next arg is not a csv filepath
            switch (args[i]) {
                case "-h":
                case "--help":
                    printHelp();
                    exit = true;
                case "-c":
                case "--config":
                    if (i == args.length - 1 || !args[i + 1].endsWith(".csv")) {
                        printHelp();
                        exit = true;
                    }
                    engine.setConfigurationMode(true);
                    engine.setConfigFilepath(args[i + 1]);
                    i++;    // skip next arg
                    break;
                case "-r":
                case "--results":
                    if (i == args.length - 1 || !args[i + 1].endsWith(".log")) {
                        printHelp();
                        exit = true;
                    }
                    engine.setResultsFilepath(args[i + 1]);
                    i++;    // skip next arg
                    break;
                case "-i":
                case "--interactive":
                    engine.setInteractiveMode(true);
                    break;
            }
        }

        if (exit) {
            return;
        }

        if (engine.getInteractiveMode()) {
            engine.welcomeUser(keyboard);
        }

        try {
            // configuration
            if (engine.getConfigurationMode()) {

                try {
                    engine.importScenarios();   // read scenarios from csv file
                } catch (FileNotFoundException e) {
                    System.err.println("ERROR: could not find config file.");
                    return;
                } catch (IOException e) {
                    System.err.println("Could not read from file");
                    return;
                }

                if (engine.getInteractiveMode()) {

                    engine.interactInConfig(keyboard);

                } else {

                    engine.printConfigScenarios();
                }

            } else {

                if (engine.getInteractiveMode()) {

                    engine.interactAndGenerate(keyboard);

                } else {
                    engine.generateScenarios();
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: could not print results. Target directory does not exist.");
        } catch (UserExitException e) {

        }

    }

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
    private static final int DEFAULT_AGE = 15;
    private static final int LIGHT_INDEX = 9;
    private static final int HIGH_SCORE = 5;
    private static final int LOW_SCORE = 2;
    private static final int FACTOR = 2;
    private static final String[] CHARACTERISTICS = {"adult", "animal", "athletic", "average",
            "baby", "bird", "builder", "cat", "ceo", "child", "criminal", "doctor", "dog", "engineer",
            "female", "ferret", "homeless", "human", "male", "overweight", "pet", "pregnant",
            "senior", "unemployed", "you"};
    private static final double[] CHARACTERISTICS_SCORES = {0.5, 0, 0.5, 0.5, 2, 0, 0.5, 0.5, 0.5, 2,
            0, 1, 0, 0.5, 0, 0, 0.5, 1, 0, 0, 3, 2, 0, 0.5, 0};
    //TODO: ADJUST SCORES

    // enumerated variable
    public enum Decision {PASSENGERS, PEDESTRIANS};

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

    // accessor methods

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

    // mutator methods

    /**
     * Sets whether the ethical engine is in configuration mode.
     * @param configurationMode the 'whether the ethical engine is in configuration mode' to set the
     *                          ethical engine's 'whether the ethical engine is in configuration
     *                          mode' to.
     */
    public void setConfigurationMode(boolean configurationMode) {
        this.configurationMode = configurationMode;
    }
    /**
     * Sets whether the ethical engine is in interactive mode.
     * @param interactiveMode the 'whether the ethical engine is in interactive mode' to set the
     *                          ethical engine's 'whether the ethical engine is in interactive
     *                          mode' to.
     */
    public void setInteractiveMode(boolean interactiveMode) {
        this.interactiveMode = interactiveMode;
    }

    /**
     * Sets the ethical engine's config file path.
     * @param configFilepath the config file path to set the ethical engine's config file path to.
     */
    public void setConfigFilepath(String configFilepath) {
        if (configurationMode) {
            this.configFilepath = configFilepath;
        }
    }
    /**
     * Sets the ethical engine's results file path.
     * @param resultsFilepath the results file path to set the ethical engine's results file path
     *                        to.
     */
    public void setResultsFilepath(String resultsFilepath) {
        this.resultsFilepath = resultsFilepath;
    }

    /**
     * Decides whether to save the passengers or the pedestrians in a pre-defined scenario.
     * @param scenario the pre-defined scenario.
     * @return whether to save the passengers or the pedestrians.
     */
    public static Decision decide(Scenario scenario) {

        // scenario characteristics used: passenger count, pedestrian count, legality of crossing,
        // whether you in car, whether you in lane

        double passengerScore = 0;
        double pedestrianScore = 0;

        if (scenario.getPassengerCount() >= scenario.getPedestrianCount()) {
            passengerScore += FACTOR * (scenario.getPassengerCount() - scenario.getPedestrianCount());
        } else {
            pedestrianScore += FACTOR * (scenario.getPedestrianCount() - scenario.getPassengerCount());
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

    /**
     * Interacts with the user by providing background information about moral machines and getting
     * the user's consent before saving any results.
     * @param keyboard the input stream to get the user's answers.
     */
    public void welcomeUser(Scanner keyboard) {

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

        // ask for consent
        System.out.println("Do you consent to have your decisions saved to a file? (yes/no)");

        while (true) {
            try {
                String answer = keyboard.next();
                if (answer.equalsIgnoreCase("yes")) {
                    resultsFilepath = USER_FILEPATH;
                    consent = true;
                } else if (answer.equalsIgnoreCase("no")) {
                    consent = false;
                } else {
                    throw new InvalidInputException("Invalid response. Do you consent to have " +
                            "your decisions saved to a file? (yes/no)");
                }

                break;

            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Imports the scenarios from the config filepath (in csv format) to the ethical engine.
     * @throws FileNotFoundException if the config filepath cannot be found.
     * @throws IOException if the config filepath cannot be read.
     */
    public void importScenarios() throws FileNotFoundException, IOException {

        BufferedReader inputStream = new BufferedReader(new FileReader(configFilepath));

        inputStream.readLine(); // discard header row

        boolean isLegalCrossing = false;
        ArrayList<Persona> passengerList = new ArrayList<Persona>();
        ArrayList<Persona> pedestrianList = new ArrayList<Persona>();

        int row = 2;
        String line = inputStream.readLine();
        while (line != null) {

            String[] values = line.split(SPLIT_BY, -1);

            // catch if number of values is not equal to 10
            try {
                if (values.length != NUM_OF_COLUMNS) {
                    throw new InvalidDataFormatException("WARNING: invalid data format in " +
                            "config file in line " + row);
                }
            } catch (InvalidDataFormatException e) {
                //TODO : discard that line for entire scenario? then what happens to the line count?
                System.err.println(e.getMessage());
                line = inputStream.readLine();
                row++;
                continue;
            }

            Persona persona = new Human();
            //pre-assigned to their default values in case exception is thrown
            String personaType = "human";
            Persona.Gender gender = Persona.Gender.UNKNOWN;
            int age = DEFAULT_AGE;
            Persona.BodyType bodyType = Persona.BodyType.UNSPECIFIED;
            Human.Profession profession = Human.Profession.NONE;
            boolean pregnant = false;
            boolean isYou = false;
            String species = DEFAULT_SPECIES;
            boolean isPet = false;
            String role = "passenger";

            if (values[0].startsWith("scenario")) {

                if (row > 2) {
                    // create new scenario and add to scenarioList
                    Persona[] passengers = new Persona[passengerList.size()];
                    passengers = passengerList.toArray(passengers);
                    Persona[] pedestrians = new Persona[pedestrianList.size()];
                    pedestrians = pedestrianList.toArray(pedestrians);

                    scenarioList.add(new Scenario(passengers, pedestrians, isLegalCrossing));

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

                // human or animal ?
                try {
                    if (!(values[0].equalsIgnoreCase("human") ||
                            values[0].equalsIgnoreCase("animal"))) {
                        throw new InvalidCharacteristicException("WARNING: invalid " +
                                "characteristic in config file in line " + row);
                    }
                    personaType = values[0].toLowerCase();
                } catch (InvalidCharacteristicException e) {
                    System.err.println(e.getMessage());
                }

                // gender
                try {

                    boolean valid = false;

                    for (Persona.Gender enumGender : Persona.Gender.values()) {
                        if (values[1].equalsIgnoreCase(enumGender.toString())) {
                            gender = enumGender;
                            valid = true;
                            break;
                        }
                    }

                    if (!(valid || values[1].isEmpty())) {
                        throw new InvalidCharacteristicException("WARNING: invalid " +
                                "characteristic in config file in line " + row);
                    }
                } catch (InvalidCharacteristicException e) {
                    System.err.println(e.getMessage());
                }

                // age
                try {
                    age = Integer.parseInt(values[2]);
                } catch (NumberFormatException e) {
                    System.err.println("WARNING: invalid number format in config " +
                            "file in line " + row);
                }

                // bodytype
                try {

                    boolean valid = false;

                    for (Persona.BodyType enumBodyType : Persona.BodyType.values()) {
                        if (values[3].equalsIgnoreCase(enumBodyType.toString())) {
                            bodyType = enumBodyType;
                            valid = true;
                            break;
                        }
                    }

                    if (!(valid || values[3].isEmpty())) {
                        throw new InvalidCharacteristicException("WARNING: invalid " +
                                "characteristic in config file in line " + row);
                    }
                } catch (InvalidCharacteristicException e) {
                    System.err.println(e.getMessage());
                }

                // profession
                try {

                    boolean valid = false;

                    for (Human.Profession enumProfession : Human.Profession.values()) {
                        if (values[4].equalsIgnoreCase(enumProfession.toString())) {
                            profession = enumProfession;
                            valid = true;
                            break;
                        }
                    }

                    if (!(valid || values[4].isEmpty())) {
                        throw new InvalidCharacteristicException("WARNING: invalid " +
                                "characteristic in config file in line " + row);
                    }
                } catch (InvalidCharacteristicException e) {
                    System.err.println(e.getMessage());
                }

                // pregnant ?
                try {
                    if (!(values[5].equalsIgnoreCase("true") ||
                            values[5].equalsIgnoreCase("false") || values[5].isEmpty())) {
                        throw new InvalidCharacteristicException("WARNING: invalid " +
                                "characteristic in config file in line " + row);
                    }
                    pregnant = Boolean.parseBoolean(values[5]);
                } catch (InvalidCharacteristicException e) {
                    System.err.println(e.getMessage());
                }

                // you ?
                try {
                    if (!(values[6].equalsIgnoreCase("true") ||
                            values[6].equalsIgnoreCase("false") || values[6].isEmpty())) {
                        throw new InvalidCharacteristicException("WARNING: invalid " +
                                "characteristic in config file in line " + row);
                    }
                    isYou = Boolean.parseBoolean(values[6]);
                } catch (InvalidCharacteristicException e) {
                    System.err.println(e.getMessage());
                }

                // species
                try {
                    if (personaType.equals("animal") && values[7].isEmpty()) {
                        throw new InvalidCharacteristicException("WARNING: invalid " +
                                "characteristic in config file in line " + row);
                    }
                    species = values[7].toLowerCase();
                } catch (InvalidCharacteristicException e) {
                    System.err.println(e.getMessage());
                }

                // is pet ?
                try {
                    if (!(values[8].equalsIgnoreCase("true") ||
                            values[8].equalsIgnoreCase("false") || values[8].isEmpty())) {
                        throw new InvalidCharacteristicException("WARNING: invalid " +
                                "characteristic in config file in line " + row);
                    }
                    isPet = Boolean.parseBoolean(values[8]);
                } catch (InvalidCharacteristicException e) {
                    System.err.println(e.getMessage());
                }

                // passenger or pedestrian
                try {
                    if (!(values[9].equalsIgnoreCase("passenger") ||
                            values[9].equalsIgnoreCase("pedestrian"))) {
                        throw new InvalidCharacteristicException("WARNING: invalid " +
                                "characteristic in config file in line " + row);
                    }
                    role = values[9].toLowerCase();
                } catch (InvalidCharacteristicException e) {
                    System.err.println(e.getMessage());
                }
            }

            if (personaType.equals("human")) {
                persona = new Human(age, profession, gender, bodyType, pregnant, isYou);
            } else if (personaType.equals("animal")) {
                persona = new Animal(age, gender, bodyType, species, isPet);
            }

            if (role.equals("passenger")) {
                passengerList.add(persona);
            } else if (role.equals("pedestrian")) {
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

                scenarioList.add(new Scenario(passengers, pedestrians, isLegalCrossing));
            }
        }
    }

    /**
     * Interacts with the user by showing scenarios from the config filepath and letting him/her
     * decide who to save, after every three scenarios, the statistics is printed to the console and
     * to the results filepath (if consented to prior) and asks whether he/she would like to
     * continue; if not, exits the program when the user presses enter (also happens when there are
     * no more scenarios to show).
     * @param keyboard the input stream to get the user's answers.
     * @throws FileNotFoundException if the target directory does not exist.
     * @throws UserExitException if the user prompts enter, wanting to exit the program.
     */
    public void interactInConfig(Scanner keyboard) throws FileNotFoundException, UserExitException {

        Audit audit = new Audit();
        audit.setAuditType(USER_AUDIT_TYPE);

        Scenario[] scenarios = new Scenario[NUM_OF_SCENARIOS_TO_SHOW];
        for (int i = 0; i < scenarioList.size(); i++) {

            scenarios[(i+1) % 3] = scenarioList.get(i);

            System.out.println(scenarios[(i+1) % 3]);
            System.out.println("Who should be saved? (passenger(s) [1] or pedestrian(s) [2])");
            String answer = keyboard.next();
            Decision decision = Decision.PASSENGERS;

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
            }
            // TODO: what to do with other outputs?

            audit.run(scenarios[(i+1) % 3], decision);

            if ((i+1) % 3 == 0 && i < scenarioList.size() - 1) {

                audit.printStatistic();

                if (consent) {
                    audit.printToFile(resultsFilepath);
                }

                System.out.println("Would you like to continue? (yes/no)");
                String cont = keyboard.next();
                // TODO: what if answer is neither yes nor no
                if (cont.equalsIgnoreCase("no")) {
                    System.out.println("That's all. Press Enter to quit.");
                    keyboard.nextLine();
                    String exit = keyboard.nextLine();
                    if (exit.isEmpty()) {
                        throw new UserExitException();
                    }
                }
            }
        }
        audit.printStatistic();
        if (consent) {
            audit.printToFile(resultsFilepath);
        }
        System.out.println("That's all. Press Enter to quit.");
        keyboard.nextLine();
        String exit = keyboard.nextLine();
        if (exit.isEmpty()) {
            throw new UserExitException();
        }
    }

    /**
     * Interacts with the user by showing randomly-generated scenarios and letting him/her decide
     * who to save, after every three scenarios, the statistics is printed to the console and to the
     * results filepath (if consented to prior) and asks whether he/she would like to continue; if
     * not, exits the program when the user presses enter.
     * @param keyboard the input stream to get the user's answers.
     * @throws FileNotFoundException if the target directory does not exist.
     * @throws UserExitException if the user prompts enter, wanting to exit the program.
     */
    public void interactAndGenerate(Scanner keyboard) throws FileNotFoundException,
            UserExitException {

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
            String answer = keyboard.next();
            Decision decision = Decision.PASSENGERS;

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
            }
            // TODO: what to do with other outputs?

            audit.run(scenario, decision);

            if (i % 3 == 0) {

                audit.printStatistic();

                if (consent) {
                    audit.printToFile(resultsFilepath);
                }

                System.out.println("Would you like to continue? (yes/no)");
                String cont = keyboard.next();
                // TODO: what if answer is neither yes nor no
                if (cont.equalsIgnoreCase("no")) {
                    System.out.println("That's all. Press Enter to quit.");
                    keyboard.nextLine();
                    String exit = keyboard.nextLine();
                    if (exit.isEmpty()) {
                        throw new UserExitException();
                    }
                }
            }

            i++;
        }
    }

    /**
     * Prints the statistics from the scenarios in the config filepath to the console and to the
     * results filepath.
     * @throws FileNotFoundException if the target directory does not exist.
     */
    public void printConfigScenarios() throws FileNotFoundException {
        Scenario[] scenarios = new Scenario[scenarioList.size()];
        scenarios = scenarioList.toArray(scenarios);

        Audit audit = new Audit(scenarios);
        audit.run();
        audit.printStatistic();
        audit.printToFile(resultsFilepath);
    }

    /**
     * Generates 100 randomly-generated scenarios and prints their statistics to the console and to
     * the results filepath.
     * @throws FileNotFoundException if the target directory does not exist.
     */
    public void generateScenarios() throws FileNotFoundException {
        Audit audit = new Audit();
        audit.run(NUM_OF_GENERATED_SCENARIOS);
        audit.printStatistic();
        audit.printToFile(resultsFilepath);
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
    }

}