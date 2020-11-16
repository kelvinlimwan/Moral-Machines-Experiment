import ethicalengine.*;

import java.io.File;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Collections;

/**
 * Audit is a class used to summarise scenario-decision results for each characteristic, revealing
 * inherent biases that may be built in as an (un)intended consequence either by simulation or
 * reading input.
 * @author Kelvin Lim Wan
 */
public class Audit {

    // constant variables
    private static final int MAX_NUM_OF_PASSENGERS = 7;
    private static final int MAX_NUM_OF_PEDESTRIANS = 10;
    private static final String DEFAULT_AUDIT_TYPE = "Unspecified";
    private static final String DEFAULT_FILEPATH = "results.log";

    // instance variables
    private String auditType;
    private ScenarioGenerator scenarioGenerator;
    private ArrayList<Scenario> scenarioList;
    private int runs;
    private int survivorAgeTotal;
    private int survivorCount;
    private ArrayList<String> characteristics;
    private ArrayList<Integer> total;
    private ArrayList<Integer> survived;

    // constructor

    /**
     * Creates an audit with a default type, scenario generator, runs, total age of survivors, total
     * number of survivors, characteristics list, total statistics list and survived statistics list
     * and an empty scenario list..
     */
    public Audit() {
        auditType = DEFAULT_AUDIT_TYPE;
        scenarioGenerator = new ScenarioGenerator();
        scenarioGenerator.setPassengerCountMax(MAX_NUM_OF_PASSENGERS);
        scenarioGenerator.setPedestrianCountMax(MAX_NUM_OF_PEDESTRIANS);
        scenarioList = new ArrayList<Scenario>();
        runs = 0;
        survivorAgeTotal = 0;
        survivorCount = 0;
        characteristics = new ArrayList<String>();
        total = new ArrayList<Integer>();
        survived = new ArrayList<Integer>();
    }
    /**
     * Creates an audit with a specified scenario list, a default type, scenario generator, runs,
     * total age of survivors, total number of survivors, characteristics list, total statistics
     * list and survived statistics list.
     */
    public Audit(Scenario[] scenarios) {
        this();
        Collections.addAll(scenarioList, scenarios);
    }

    // accessor method

    /**
     * Gets the audit's type.
     * @return the audit's type.
     */
    public String getAuditType() {
        return auditType;
    }

    // mutator method

    /**
     * Sets the audit's type.
     * @param name the name to set the audit's type to.
     */
    public void setAuditType(String name) {
        auditType = name;
    }

    /**
     * Runs a pre-defined scenario with an already-made decision (by the user) and adds its
     * statistics to the audit.
     * @param scenario the pre-defined scenario.
     * @param decision the already-made decision.
     */
    public void run(Scenario scenario, EthicalEngine.Decision decision) {

        runs++;

        addStatistic("passengers", scenario.getPassengerCount(), total);
        addStatistic("pedestrians", scenario.getPedestrianCount(), total);

        if (scenario.isLegalCrossing()) {
            addStatistic("green", scenario.getPassengerCount() + scenario.getPedestrianCount(),
                    total);
        } else {
            addStatistic("red", scenario.getPassengerCount() + scenario.getPedestrianCount(),
                    total);
        }

        // fill up total array
        for (Persona passenger : scenario.getPassengers()) {
            for (String characteristic : passenger.getCharacteristics()) {
                addStatistic(characteristic, 1, total);
            }
        }
        for (Persona pedestrian : scenario.getPedestrians()) {
            for (String characteristic : pedestrian.getCharacteristics()) {
                addStatistic(characteristic, 1, total);
            }
        }

        // find survivors array
        Persona[] survivors;
        if (decision == EthicalEngine.Decision.PASSENGERS) {
            survivors = scenario.getPassengers();
            addStatistic("passengers", scenario.getPassengerCount(), survived);
            if (scenario.isLegalCrossing()) {
                addStatistic("green", scenario.getPassengerCount(), survived);
            } else {
                addStatistic("red", scenario.getPassengerCount(), survived);
            }
        } else {
            survivors = scenario.getPedestrians();
            addStatistic("pedestrians", scenario.getPedestrianCount(), survived);
            if (scenario.isLegalCrossing()) {
                addStatistic("green", scenario.getPedestrianCount(), survived);
            } else {
                addStatistic("red", scenario.getPedestrianCount(), survived);
            }
        }

        // fill up survived array
        for (Persona survivor : survivors) {
            for (String characteristic : survivor.getCharacteristics()) {
                addStatistic(characteristic, 1, survived);
            }
            if (survivor instanceof Human) {
                survivorAgeTotal += survivor.getAge();
                survivorCount++;
            }
        }
    }

    /**
     * Runs each scenario in the pre-defined scenario list with each decision made through the
     * ethical engine's static decision method and adds their statistics to the audit.
     */
    public void run() {

        runs += scenarioList.size();

        for (Scenario scenario : scenarioList) {

            addStatistic("passengers", scenario.getPassengerCount(), total);
            addStatistic("pedestrians", scenario.getPedestrianCount(), total);

            if (scenario.isLegalCrossing()) {
                addStatistic("green", scenario.getPassengerCount() + scenario.getPedestrianCount(),
                        total);
            } else {
                addStatistic("red", scenario.getPassengerCount() + scenario.getPedestrianCount(),
                        total);
            }

            // fill up total array
            for (Persona passenger : scenario.getPassengers()) {
                for (String characteristic : passenger.getCharacteristics()) {
                    addStatistic(characteristic, 1, total);
                }
            }
            for (Persona pedestrian : scenario.getPedestrians()) {
                for (String characteristic : pedestrian.getCharacteristics()) {
                    addStatistic(characteristic, 1, total);
                }
            }

            // find survivors array
            Persona[] survivors;
            if (EthicalEngine.decide(scenario) == EthicalEngine.Decision.PASSENGERS) {
                survivors = scenario.getPassengers();
                addStatistic("passengers", scenario.getPassengerCount(), survived);
                if (scenario.isLegalCrossing()) {
                    addStatistic("green", scenario.getPassengerCount(), survived);
                } else {
                    addStatistic("red", scenario.getPassengerCount(), survived);
                }
            } else {
                survivors = scenario.getPedestrians();
                addStatistic("pedestrians", scenario.getPedestrianCount(), survived);
                if (scenario.isLegalCrossing()) {
                    addStatistic("green", scenario.getPedestrianCount(), survived);
                } else {
                    addStatistic("red", scenario.getPedestrianCount(), survived);
                }
            }

            // fill up survived array
            for (Persona survivor : survivors) {
                for (String characteristic : survivor.getCharacteristics()) {
                    addStatistic(characteristic, 1, survived);
                }
                if (survivor instanceof Human) {
                    survivorAgeTotal += survivor.getAge();
                    survivorCount++;
                }
            }
        }
    }

    /**
     * Runs a number of randomly-generated scenarios with each decision made through the ethical
     * engine's static decision method and adds their statistics to the audit.
     * @param runs the number of randomly-generated scenarios to run.
     */
    public void run(int runs) {

        this.runs += runs;

        for (int i = 0; i < runs; i++) {

            Scenario scenario = scenarioGenerator.generate();

            addStatistic("passengers", scenario.getPassengerCount(), total);
            addStatistic("pedestrians", scenario.getPedestrianCount(), total);

            if (scenario.isLegalCrossing()) {
                addStatistic("green", scenario.getPassengerCount() + scenario.getPedestrianCount(),
                        total);
            } else {
                addStatistic("red", scenario.getPassengerCount() + scenario.getPedestrianCount(),
                        total);
            }

            // fill up total array
            for (Persona passenger : scenario.getPassengers()) {
                for (String characteristic : passenger.getCharacteristics()) {
                    addStatistic(characteristic, 1, total);
                }
            }
            for (Persona pedestrian : scenario.getPedestrians()) {
                for (String characteristic : pedestrian.getCharacteristics()) {
                    addStatistic(characteristic, 1, total);
                }
            }

            // find survivors array
            Persona[] survivors;
            if (EthicalEngine.decide(scenario) == EthicalEngine.Decision.PASSENGERS) {
                survivors = scenario.getPassengers();
                addStatistic("passengers", scenario.getPassengerCount(), survived);
                if (scenario.isLegalCrossing()) {
                    addStatistic("green", scenario.getPassengerCount(), survived);
                } else {
                    addStatistic("red", scenario.getPassengerCount(), survived);
                }
            } else {
                survivors = scenario.getPedestrians();
                addStatistic("pedestrians", scenario.getPedestrianCount(), survived);
                if (scenario.isLegalCrossing()) {
                    addStatistic("green", scenario.getPedestrianCount(), survived);
                } else {
                    addStatistic("red", scenario.getPedestrianCount(), survived);
                }
            }

            // fill up survived array
            for (Persona survivor : survivors) {
                for (String characteristic : survivor.getCharacteristics()) {
                    addStatistic(characteristic, 1, survived);
                }
                if (survivor instanceof Human) {
                    survivorAgeTotal += survivor.getAge();
                    survivorCount++;
                }
            }
        }
    }

    /**
     * Prints the audit's statistics to the console.
     */
    public void printStatistic() {
        System.out.println(toString());
    }

    /**
     * Prints the audit's statistics to a specified filepath; if the filepath is not specified, it
     * is printed to a default filepath, if the filepath already exists, the statistics is appended
     * to the filepath and otherwise it is created.
     * @param filepath the specified filepath.
     */
    public void printToFile(String filepath) throws FileNotFoundException {

        PrintWriter inputStream;

        File fileObject = new File(DEFAULT_FILEPATH);
        if (filepath != null) {
            fileObject = new File(filepath);
        }

        if (fileObject.exists()) {
            // append
            inputStream = new PrintWriter(new FileOutputStream(fileObject, true));
        } else {
            // create new file
            inputStream = new PrintWriter(new FileOutputStream(fileObject));
        }

        inputStream.println(toString());
        inputStream.close();
    }

    /**
     * Returns a string representation of the audit's statistics.
     * @return the string representation ot the audit's statistics.
     */
    @Override
    public String toString() {

        if (runs == 0) {
            return "no audit available";
        }

        String output = "======================================\n# " + auditType + " Audit\n" +
                "======================================\n" + "- % SAVED AFTER " + runs + " RUNS\n";

        // list of names and stats with indices perfectly matching
        ArrayList<String> characteristicNames = new ArrayList<String>();
        ArrayList<Double> characteristicStats = new ArrayList<Double>();

        for (int i = 0; i < characteristics.size(); i++) {

            if (total.get(i) > 0) {

                double roundedRatio = Math.ceil((double) survived.get(i) / total.get(i) * 100) / 100;
                int index = characteristicStats.size();
                for (int j = 0; j < characteristicStats.size(); j++) {
                    if (roundedRatio > characteristicStats.get(j)) {
                        index = j;
                        break;
                    }
                }

                characteristicNames.add(index, characteristics.get(i));
                characteristicStats.add(index, roundedRatio);
            }
        }

        for (int i = 0; i < characteristicStats.size(); i++) {
            output += String.format("%s: %.2f\n", characteristicNames.get(i),
                    characteristicStats.get(i));
        }

        double averageAge = Math.ceil((double) survivorAgeTotal / survivorCount * 100) / 100;
        output += String.format("--\naverage age: %.2f", averageAge);

        return output;

    }

    private void addStatistic(String characteristic, int toAdd, ArrayList<Integer> array) {

        if (characteristic.isEmpty() || characteristic.equals("unknown") ||
                characteristic.equals("unspecified") || characteristic.equals("none")) {
            return;
        }

        if (characteristics.contains(characteristic)) {
            int index = characteristics.indexOf(characteristic);
            array.set(index, array.get(index) + toAdd);
        } else {
            int index = characteristics.size();
            for (int i = 0; i < characteristics.size(); i++) {
                if (characteristic.compareTo(characteristics.get(i)) < 0) {
                    index = i;
                    break;
                }
            }
            characteristics.add(index, characteristic);
            array.add(index, toAdd);

            if (array.equals(total)) {
                survived.add(index, 0);
            }
        }
    }
}
