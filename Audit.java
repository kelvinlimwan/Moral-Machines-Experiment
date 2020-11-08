import ethicalengine.*;

import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class Audit {

    // constant variable
    private static final int NUM_OF_STATISTICS = 3;
    private static final int INDEX_OF_TOTAL = 0;
    private static final int INDEX_OF_SURVIVED = 1;
    private static final int INDEX_OF_RATIO = 2;
    private static final int MAX_NUM_OF_PASSENGERS = 7;
    private static final int MAX_NUM_OF_PEDESTRIANS = 10;
    private static final String DEFAULT_AUDIT_TYPE = "Unspecified";
    private static final String DEFAULT_FILEPATH = "results.log";
    private static final ArrayList<String> CHARACTERISTICS =
            new ArrayList<String>(Arrays.asList("adult", "animal", "athletic", "average",
            "baby", "bird", "builder", "cat", "ceo", "child", "criminal", "doctor", "dog", "engineer",
            "female", "ferret", "green", "homeless", "human", "male", "overweight", "passengers",
            "pedestrians", "pet", "pregnant", "red", "senior", "unemployed", "you"));

    // instance variables
    private String auditType;
    private ScenarioGenerator scenarioGenerator;
    private Scenario[] scenarios;
    private int runs;
    private int survivorAgeTotal;
    private int survivorCount;
    private double[][] statistics;
    private ArrayList<String> otherSpecies;
    private ArrayList<Double> otherTotal;
    private ArrayList<Double> otherSurvived;
    private ArrayList<Double> otherRatio;

    // constructor
    public Audit() {
        auditType = DEFAULT_AUDIT_TYPE;
        scenarioGenerator = new ScenarioGenerator();
        scenarioGenerator.setPassengerCountMax(MAX_NUM_OF_PASSENGERS);
        scenarioGenerator.setPedestrianCountMax(MAX_NUM_OF_PEDESTRIANS);

        runs = 0;
        survivorAgeTotal = 0;
        survivorCount = 0;
        statistics = new double[CHARACTERISTICS.size()][NUM_OF_STATISTICS];
        otherSpecies = new ArrayList<String>();
        otherTotal = new ArrayList<Double>();
        otherSurvived = new ArrayList<Double>();
        otherRatio = new ArrayList<Double>();
    }

    public Audit(Scenario[] scenarios) {
        this();
        this.scenarios = new Scenario[scenarios.length];
        System.arraycopy(scenarios, 0, this.scenarios, 0, scenarios.length);
    }

    // accessor method
    public String getAuditType() {
        return auditType;
    }

    // mutator method
    public void setAuditType(String name) {
        auditType = name;
    }

    public void run(Scenario scenario, EthicalEngine.Decision decision) {

        runs++;

        // statistics[i] = [total, survived, ratio]

        statistics[CHARACTERISTICS.indexOf("passengers")][INDEX_OF_TOTAL] +=
                scenario.getPassengerCount();
        statistics[CHARACTERISTICS.indexOf("pedestrians")][INDEX_OF_TOTAL] +=
                scenario.getPedestrianCount();
        statistics[CHARACTERISTICS.indexOf(scenario.isLegalCrossing() ? "green" : "red")]
                [INDEX_OF_TOTAL] += scenario.getPassengerCount() +
                scenario.getPedestrianCount();

        int[] indexArray;
        // fill up 'total' section of statistics array
        for (Persona passenger : scenario.getPassengers()) {

            indexArray = new int[passenger.getCharacteristics().length];

            for (int j = 0; j < passenger.getCharacteristics().length; j++) {
                if (passenger instanceof Animal && j == 1 &&
                        ! CHARACTERISTICS.contains(passenger.getCharacteristics()[j])) {    // when animal species is not in list
                    if (otherSpecies.contains(passenger.getCharacteristics()[j])) {
                        otherTotal.set(otherSpecies.indexOf(passenger.getCharacteristics()[j]),
                                otherTotal.get(otherSpecies.indexOf(passenger.getCharacteristics()[j])) + 1);  // increment at index of otherTotal
                    } else {
                        otherSpecies.add(passenger.getCharacteristics()[j]);    // add species name to otherSpecies
                        otherTotal.add(1.0);
                        otherSurvived.add(0.0);
                        otherRatio.add(0.0);
                    }
                } else {
                    indexArray[j] = CHARACTERISTICS.indexOf(passenger.getCharacteristics()[j]);
                }
            }

            for (int index : indexArray) {
                if (index >= 0) {
                    statistics[index][INDEX_OF_TOTAL]++;
                }
            }
        }
        for (Persona pedestrian : scenario.getPedestrians()) {

            indexArray = new int[pedestrian.getCharacteristics().length];

            for (int j = 0; j < pedestrian.getCharacteristics().length; j++) {
                if (pedestrian instanceof Animal && j == 1 &&
                        ! CHARACTERISTICS.contains(pedestrian.getCharacteristics()[j])) {    // when animal species is not in list
                    if (otherSpecies.contains(pedestrian.getCharacteristics()[j])) {
                        otherTotal.set(otherSpecies.indexOf(pedestrian.getCharacteristics()[j]),
                                otherTotal.get(otherSpecies.indexOf(pedestrian.getCharacteristics()[j])) + 1);  // increment at index of otherTotal
                    } else {
                        otherSpecies.add(pedestrian.getCharacteristics()[j]);    // add species name to otherSpecies
                        otherTotal.add(1.0);
                        otherSurvived.add(0.0);
                        otherRatio.add(0.0);
                    }
                } else {
                    indexArray[j] = CHARACTERISTICS.indexOf(pedestrian.getCharacteristics()[j]);
                }
            }

            for (int index : indexArray) {
                if (index >= 0) {
                    statistics[index][INDEX_OF_TOTAL]++;
                }
            }
        }

        // find survivors array
        Persona[] survivors;
        if (decision == EthicalEngine.Decision.PASSENGERS) {
            survivors = scenario.getPassengers();
            statistics[CHARACTERISTICS.indexOf("passengers")][INDEX_OF_SURVIVED] +=
                    scenario.getPassengerCount();
            statistics[CHARACTERISTICS.indexOf(scenario.isLegalCrossing() ? "green" : "red")]
                    [INDEX_OF_SURVIVED] += scenario.getPassengerCount();
        } else {
            survivors = scenario.getPedestrians();
            statistics[CHARACTERISTICS.indexOf("pedestrians")][INDEX_OF_SURVIVED] +=
                    scenario.getPedestrianCount();
            statistics[CHARACTERISTICS.indexOf(scenario.isLegalCrossing() ? "green" : "red")]
                    [INDEX_OF_SURVIVED] += scenario.getPedestrianCount();
        }

        // fill up 'survived' section of statistics array
        for (Persona survivor : survivors) {

            String[] survivorCharacteristics = survivor.getCharacteristics();
            indexArray = new int[survivorCharacteristics.length];

            for (int j = 0; j < survivorCharacteristics.length; j++) {
                if (survivor instanceof Animal && j == 1 &&
                        ! CHARACTERISTICS.contains(survivor.getCharacteristics()[j])) {    // when animal species is not in list
                    otherSurvived.set(otherSpecies.indexOf(survivor.getCharacteristics()[j]),
                            otherSurvived.get(otherSpecies.indexOf(survivor.getCharacteristics()[j])) + 1);
                } else {
                    indexArray[j] = CHARACTERISTICS.indexOf(survivor.getCharacteristics()[j]);
                }
            }

            for (int index : indexArray) {
                if (index >= 0) {
                    statistics[index][INDEX_OF_SURVIVED]++;
                }
            }

            if (survivor instanceof Human) {
                survivorAgeTotal += survivor.getAge();
                survivorCount++;
            }
        }

        for (double[] entry : statistics) {
            if (entry[INDEX_OF_TOTAL] > 0) {
                entry[INDEX_OF_RATIO] = entry[INDEX_OF_SURVIVED] / entry[INDEX_OF_TOTAL];
            }
        }
        for (int j = 0; j < otherTotal.size(); j++) {
            otherRatio.set(j, otherSurvived.get(j) / otherTotal.get(j));
        }

    }

    public void run() {

        runs += scenarios.length;

        for (Scenario scenario : scenarios) {

            // statistics[i] = [total, survived, ratio]

            statistics[CHARACTERISTICS.indexOf("passengers")][INDEX_OF_TOTAL] +=
                    scenario.getPassengerCount();
            statistics[CHARACTERISTICS.indexOf("pedestrians")][INDEX_OF_TOTAL] +=
                    scenario.getPedestrianCount();
            statistics[CHARACTERISTICS.indexOf(scenario.isLegalCrossing() ? "green" : "red")]
                    [INDEX_OF_TOTAL] += scenario.getPassengerCount() +
                    scenario.getPedestrianCount();

            int[] indexArray;
            // fill up 'total' section of statistics array
            for (Persona passenger : scenario.getPassengers()) {

                indexArray = new int[passenger.getCharacteristics().length];

                for (int j = 0; j < passenger.getCharacteristics().length; j++) {
                    if (passenger instanceof Animal && j == 1 &&
                            ! CHARACTERISTICS.contains(passenger.getCharacteristics()[j])) {    // when animal species is not in list
                        if (otherSpecies.contains(passenger.getCharacteristics()[j])) {
                            otherTotal.set(otherSpecies.indexOf(passenger.getCharacteristics()[j]),
                                    otherTotal.get(otherSpecies.indexOf(passenger.getCharacteristics()[j])) + 1);  // increment at index of otherTotal
                        } else {
                            otherSpecies.add(passenger.getCharacteristics()[j]);    // add species name to otherSpecies
                            otherTotal.add(1.0);
                            otherSurvived.add(0.0);
                            otherRatio.add(0.0);
                        }
                    } else {
                        indexArray[j] = CHARACTERISTICS.indexOf(passenger.getCharacteristics()[j]);
                    }
                }

                for (int index : indexArray) {
                    if (index >= 0) {
                        statistics[index][INDEX_OF_TOTAL]++;
                    }
                }
            }
            for (Persona pedestrian : scenario.getPedestrians()) {

                indexArray = new int[pedestrian.getCharacteristics().length];

                for (int j = 0; j < pedestrian.getCharacteristics().length; j++) {
                    if (pedestrian instanceof Animal && j == 1 &&
                            ! CHARACTERISTICS.contains(pedestrian.getCharacteristics()[j])) {    // when animal species is not in list
                        if (otherSpecies.contains(pedestrian.getCharacteristics()[j])) {
                            otherTotal.set(otherSpecies.indexOf(pedestrian.getCharacteristics()[j]),
                                    otherTotal.get(otherSpecies.indexOf(pedestrian.getCharacteristics()[j])) + 1);  // increment at index of otherTotal
                        } else {
                            otherSpecies.add(pedestrian.getCharacteristics()[j]);    // add species name to otherSpecies
                            otherTotal.add(1.0);
                            otherSurvived.add(0.0);
                            otherRatio.add(0.0);
                        }
                    } else {
                        indexArray[j] = CHARACTERISTICS.indexOf(pedestrian.getCharacteristics()[j]);
                    }
                }

                for (int index : indexArray) {
                    if (index >= 0) {
                        statistics[index][INDEX_OF_TOTAL]++;
                    }
                }
            }

            // find survivors array
            Persona[] survivors;
            if (EthicalEngine.decide(scenario) == EthicalEngine.Decision.PASSENGERS) {
                survivors = scenario.getPassengers();
                statistics[CHARACTERISTICS.indexOf("passengers")][INDEX_OF_SURVIVED] +=
                        scenario.getPassengerCount();
                statistics[CHARACTERISTICS.indexOf(scenario.isLegalCrossing() ? "green" : "red")]
                        [INDEX_OF_SURVIVED] += scenario.getPassengerCount();
            } else {
                survivors = scenario.getPedestrians();
                statistics[CHARACTERISTICS.indexOf("pedestrians")][INDEX_OF_SURVIVED] +=
                        scenario.getPedestrianCount();
                statistics[CHARACTERISTICS.indexOf(scenario.isLegalCrossing() ? "green" : "red")]
                        [INDEX_OF_SURVIVED] += scenario.getPedestrianCount();
            }

            // fill up 'survived' section of statistics array
            for (Persona survivor : survivors) {

                String[] survivorCharacteristics = survivor.getCharacteristics();
                indexArray = new int[survivorCharacteristics.length];

                for (int j = 0; j < survivorCharacteristics.length; j++) {
                    if (survivor instanceof Animal && j == 1 &&
                            ! CHARACTERISTICS.contains(survivor.getCharacteristics()[j])) {    // when animal species is not in list
                        otherSurvived.set(otherSpecies.indexOf(survivor.getCharacteristics()[j]),
                                otherSurvived.get(otherSpecies.indexOf(survivor.getCharacteristics()[j])) + 1);
                    } else {
                        indexArray[j] = CHARACTERISTICS.indexOf(survivor.getCharacteristics()[j]);
                    }
                }

                for (int index : indexArray) {
                    if (index >= 0) {
                        statistics[index][INDEX_OF_SURVIVED]++;
                    }
                }

                if (survivor instanceof Human) {
                    survivorAgeTotal += survivor.getAge();
                    survivorCount++;
                }
            }
        }
        for (double[] entry : statistics) {
            if (entry[INDEX_OF_TOTAL] > 0) {
                entry[INDEX_OF_RATIO] = entry[INDEX_OF_SURVIVED] / entry[INDEX_OF_TOTAL];
            }
        }
        for (int j = 0; j < otherTotal.size(); j++) {
            otherRatio.set(j, otherSurvived.get(j) / otherTotal.get(j));
        }
    }

    public void run(int runs) {

        this.runs += runs;

        for (int i = 0; i < runs; i++) {

            Scenario scenario = scenarioGenerator.generate();

            // statistics[i] = [total, survived, ratio]

            statistics[CHARACTERISTICS.indexOf("passengers")][INDEX_OF_TOTAL] +=
                    scenario.getPassengerCount();
            statistics[CHARACTERISTICS.indexOf("pedestrians")][INDEX_OF_TOTAL] +=
                    scenario.getPedestrianCount();
            statistics[CHARACTERISTICS.indexOf(scenario.isLegalCrossing() ? "green" : "red")]
                    [INDEX_OF_TOTAL] += scenario.getPassengerCount() +
                    scenario.getPedestrianCount();

            int[] indexArray;
            // fill up 'total' section of statistics array
            for (Persona passenger : scenario.getPassengers()) {

                indexArray = new int[passenger.getCharacteristics().length];

                for (int j = 0; j < passenger.getCharacteristics().length; j++) {
                    if (passenger instanceof Animal && j == 1 &&
                            ! CHARACTERISTICS.contains(passenger.getCharacteristics()[j])) {    // when animal species is not in list
                        if (otherSpecies.contains(passenger.getCharacteristics()[j])) {
                            otherTotal.set(otherSpecies.indexOf(passenger.getCharacteristics()[j]),
                                    otherTotal.get(otherSpecies.indexOf(passenger.getCharacteristics()[j])) + 1);  // increment at index of otherTotal
                        } else {
                            otherSpecies.add(passenger.getCharacteristics()[j]);    // add species name to otherSpecies
                            otherTotal.add(1.0);
                            otherSurvived.add(0.0);
                            otherRatio.add(0.0);
                        }
                    } else {
                        indexArray[j] = CHARACTERISTICS.indexOf(passenger.getCharacteristics()[j]);
                    }
                }

                for (int index : indexArray) {
                    if (index >= 0) {
                        statistics[index][INDEX_OF_TOTAL]++;
                    }
                }
            }
            for (Persona pedestrian : scenario.getPedestrians()) {

                indexArray = new int[pedestrian.getCharacteristics().length];

                for (int j = 0; j < pedestrian.getCharacteristics().length; j++) {
                    if (pedestrian instanceof Animal && j == 1 &&
                            ! CHARACTERISTICS.contains(pedestrian.getCharacteristics()[j])) {    // when animal species is not in list
                        if (otherSpecies.contains(pedestrian.getCharacteristics()[j])) {
                            otherTotal.set(otherSpecies.indexOf(pedestrian.getCharacteristics()[j]),
                                    otherTotal.get(otherSpecies.indexOf(pedestrian.getCharacteristics()[j])) + 1);  // increment at index of otherTotal
                        } else {
                            otherSpecies.add(pedestrian.getCharacteristics()[j]);    // add species name to otherSpecies
                            otherTotal.add(1.0);
                            otherSurvived.add(0.0);
                            otherRatio.add(0.0);
                        }
                    } else {
                        indexArray[j] = CHARACTERISTICS.indexOf(pedestrian.getCharacteristics()[j]);
                    }
                }

                for (int index : indexArray) {
                    if (index >= 0) {
                        statistics[index][INDEX_OF_TOTAL]++;
                    }
                }
            }

            // find survivors array
            Persona[] survivors;
            if (EthicalEngine.decide(scenario) == EthicalEngine.Decision.PASSENGERS) {
                survivors = scenario.getPassengers();
                statistics[CHARACTERISTICS.indexOf("passengers")][INDEX_OF_SURVIVED] +=
                        scenario.getPassengerCount();
                statistics[CHARACTERISTICS.indexOf(scenario.isLegalCrossing() ? "green" : "red")]
                        [INDEX_OF_SURVIVED] += scenario.getPassengerCount();
            } else {
                survivors = scenario.getPedestrians();
                statistics[CHARACTERISTICS.indexOf("pedestrians")][INDEX_OF_SURVIVED] +=
                        scenario.getPedestrianCount();
                statistics[CHARACTERISTICS.indexOf(scenario.isLegalCrossing() ? "green" : "red")]
                        [INDEX_OF_SURVIVED] += scenario.getPedestrianCount();
            }

            // fill up 'survived' section of statistics array
            for (Persona survivor : survivors) {

                String[] survivorCharacteristics = survivor.getCharacteristics();
                indexArray = new int[survivorCharacteristics.length];

                for (int j = 0; j < survivorCharacteristics.length; j++) {
                    if (survivor instanceof Animal && j == 1 &&
                            ! CHARACTERISTICS.contains(survivor.getCharacteristics()[j])) {    // when animal species is not in list
                        otherSurvived.set(otherSpecies.indexOf(survivor.getCharacteristics()[j]),
                                otherSurvived.get(otherSpecies.indexOf(survivor.getCharacteristics()[j])) + 1);
                    } else {
                        indexArray[j] = CHARACTERISTICS.indexOf(survivor.getCharacteristics()[j]);
                    }
                }

                for (int index : indexArray) {
                    if (index >= 0) {
                        statistics[index][INDEX_OF_SURVIVED]++;
                    }
                }

                if (survivor instanceof Human) {
                    survivorAgeTotal += survivor.getAge();
                    survivorCount++;
                }
            }
        }

        for (double[] entry : statistics) {
            if (entry[INDEX_OF_TOTAL] > 0) {
                entry[INDEX_OF_RATIO] = entry[INDEX_OF_SURVIVED] / entry[INDEX_OF_TOTAL];
            }
        }
        for (int j = 0; j < otherTotal.size(); j++) {
            otherRatio.set(j, otherSurvived.get(j) / otherTotal.get(j));
        }
    }

    public void printStatistic() {
        System.out.println(toString());
    }

    public void printToFile(String filepath) {

        PrintWriter inputStream;

        File fileObject = new File(DEFAULT_FILEPATH);
        if (filepath != null) {
            fileObject = new File(filepath);
        }

        // TODO: check if isFile or exists or isDirectory
        if (fileObject.isFile()) {
            // append
            try {
                inputStream = new PrintWriter(new FileOutputStream(fileObject), true);
                inputStream.println(toString());
                inputStream.close();
            } catch (FileNotFoundException e) {
                System.err.println("ERROR: could not print results. Target directory does not " +
                        "exist.");
                System.exit(0);
            }
        } else {
            // create new file
            try {
                inputStream = new PrintWriter(new FileOutputStream(fileObject));
                inputStream.println(toString());
                inputStream.close();
            } catch (FileNotFoundException e) {
                System.err.println("ERROR: could not print results. Target directory does not " +
                        "exist.");
                System.exit(0);
            }
        }

    }

    @Override
    public String toString() {

        // TODO: IMPORTANT- IF CSV FILE CONTAINS AN ANIMAL SPECIES THAT I DON'T HAVE, IT WON'T OUTPUT IT
        // THEN HOW TO MAKE IT INCLUDE THE NEW SPECIES WHILE KEEPING THE ALPHABETICAL ORDER ?????

        String output = "======================================\n# " + auditType + " Audit\n" +
                "======================================\n" + "- % SAVED AFTER " + runs + " RUNS\n";

        // list of names and stats with indices perfectly matching
        ArrayList<String> characteristicNames = new ArrayList<String>();
        ArrayList<Double> characteristicStats = new ArrayList<Double>();

        for (int i = 0; i < statistics.length; i++) {

            if (statistics[i][INDEX_OF_TOTAL] > 0) {

                int index = characteristicStats.size();
                for (int j = 0; j < characteristicStats.size(); j++) {
                    if (statistics[i][INDEX_OF_RATIO] > characteristicStats.get(j)) {
                        index = j;
                        break;
                    }
                }

                characteristicNames.add(index, CHARACTERISTICS.get(i));
                characteristicStats.add(index, statistics[i][INDEX_OF_RATIO]);
            }
        }

        for (int i = 0; i < otherSpecies.size(); i++) {
            for (int j = 0; j < characteristicNames.size(); j++) {
                if (otherSpecies.get(i).compareToIgnoreCase(characteristicNames.get(j)) < 0) {
                    characteristicNames.add(j, otherSpecies.get(i));
                    characteristicStats.add(j, otherRatio.get(i));
                    break;
                }
            }
        }

        for (int i = 0; i < characteristicStats.size(); i++) {
            double ratio = Math.ceil(characteristicStats.get(i) * 100) / 100;
            output += String.format("%s: %.2f\n", characteristicNames.get(i), ratio);
        }

        double averageAge = Math.ceil((double) survivorAgeTotal / survivorCount * 100) / 100;
        output += String.format("--\naverage age: %.2f", averageAge);

        return output;

    }

}
