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
    private static final String DEFAULT_AUDIT_TYPE = "Unspecified";
    private static final String DEFAULT_FILEPATH = "results.log";
    private static final String[] CHARACTERISTICS = {"adult", "animal", "athletic", "average",
            "baby", "builder", "cat", "ceo", "child", "criminal", "doctor", "dog", "engineer",
            "female", "green", "homeless", "human", "kangaroo", "male", "overweight", "passengers",
            "pedestrians", "pet", "possum", "pregnant", "rabbit", "red", "senior", "unemployed",
            "you"};

    // instance variable
    private String auditType;
    private ScenarioGenerator scenarioGenerator;

    private int runs;
    private int humanAgeTotal;
    private int humanCount;
    private double[][] statistics;

    // constructor
    public Audit() {
        auditType = DEFAULT_AUDIT_TYPE;
        scenarioGenerator = new ScenarioGenerator();

        runs = 0;
        humanAgeTotal = 0;
        humanCount = 0;
        statistics = new double[CHARACTERISTICS.length][NUM_OF_STATISTICS];
    }

    // accessor method
    public String getAuditType() {
        return auditType;
    }

    // mutator method
    public void setAuditType(String name) {
        auditType = name;
    }

    public void run(int runs) {

        this.runs += runs;

        for (int i = 0; i < runs; i++) {
            Scenario scenario = scenarioGenerator.generate();

            // statistics[i] = [total, survived, ratio]

            humanAgeTotal += scenario.getTotalOfHumanAges();
            humanCount += scenario.getHumanCount();

            statistics[Arrays.binarySearch(CHARACTERISTICS, "passengers")][0] +=
                    scenario.getPassengerCount();
            statistics[Arrays.binarySearch(CHARACTERISTICS, "pedestrians")][0] +=
                    scenario.getPedestrianCount();
            statistics[Arrays.binarySearch(CHARACTERISTICS, (scenario.isLegalCrossing() ?
                    "green" : "red"))][0] += scenario.getPassengerCount() +
                    scenario.getPedestrianCount();

            // create an array as the concatenation of passengers and pedestrians
            Persona[] allPersonas = new Persona[scenario.getPassengerCount() +
                    scenario.getPedestrianCount()];
            System.arraycopy(scenario.getPassengers(), 0, allPersonas, 0,
                    scenario.getPassengerCount());
            System.arraycopy(scenario.getPedestrians(), 0, allPersonas,
                    scenario.getPassengerCount(), scenario.getPedestrianCount());

            int[] indexArray;
            // fill up 'total' section of statistics array
            for (Persona persona : allPersonas) {

                String[] personaCharacteristics = persona.getCharacteristics();
                indexArray = new int[personaCharacteristics.length];

                for (int j = 0; j < personaCharacteristics.length; j++) {
                    indexArray[j] = Arrays.binarySearch(CHARACTERISTICS,
                            personaCharacteristics[j]);
                }

                for (int index : indexArray) {
                    if (index >= 0) {
                        statistics[index][0]++;
                    }
                }
            }

            // find survivors array
            Persona[] survivors;
            if (EthicalEngine.decide(scenario) == EthicalEngine.Decision.PASSENGERS) {
                survivors = scenario.getPassengers();
                statistics[Arrays.binarySearch(CHARACTERISTICS, "passengers")][1] +=
                        scenario.getPassengerCount();
                statistics[Arrays.binarySearch(CHARACTERISTICS, (scenario.isLegalCrossing() ?
                        "green" : "red"))][1] += scenario.getPassengerCount();
            } else {
                survivors = scenario.getPedestrians();
                statistics[Arrays.binarySearch(CHARACTERISTICS, "pedestrians")][1] +=
                        scenario.getPedestrianCount();
                statistics[Arrays.binarySearch(CHARACTERISTICS, (scenario.isLegalCrossing() ?
                        "green" : "red"))][1] += scenario.getPedestrianCount();
            }

            // fill up 'survived' section of statistics array
            for (Persona survivor : survivors) {

                String[] survivorCharacteristics = survivor.getCharacteristics();
                indexArray = new int[survivorCharacteristics.length];

                for (int j = 0; j < survivorCharacteristics.length; j++) {
                    indexArray[j] = Arrays.binarySearch(CHARACTERISTICS,
                            survivorCharacteristics[j]);
                }

                for (int index : indexArray) {
                    if (index >= 0) {
                        statistics[index][1]++;
                    }
                }
            }
        }

        for (double[] entry : statistics) {
            if (entry[0] > 0) {
                entry[2] = entry[1] / entry[0];
            }
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

        if (fileObject.exists()) {
            // append
            try {
                inputStream = new PrintWriter(new FileOutputStream(filepath), true);
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
                inputStream = new PrintWriter(new FileOutputStream(filepath));
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

        String output = "======================================\n# " + auditType + " Audit\n" +
                "======================================\n" + "- % SAVED AFTER " + runs + " RUNS\n";

        // list of names and stats with indices perfectly matching
        ArrayList<String> characteristicNames = new ArrayList<String>();
        ArrayList<Double> characteristicStats = new ArrayList<Double>();

        for (int i = 0; i < statistics.length; i++) {

            if (statistics[i][2] > 0) {

                int index = characteristicStats.size();
                for (int j = 0; j < characteristicStats.size(); j++) {
                    if (statistics[i][2] > characteristicStats.get(j)) {
                        index = j;
                        break;
                    }
                }

                characteristicNames.add(index, CHARACTERISTICS[i]);
                characteristicStats.add(index, statistics[i][2]);
            }
        }

        for (int i = 0; i < characteristicStats.size(); i++) {
            double ratio = Math.ceil(characteristicStats.get(i) * 100) / 100;
            output += String.format("%s: %.2f\n", characteristicNames.get(i), ratio);
        }

        double averageAge = Math.ceil((double) humanAgeTotal / humanCount * 100) / 100;
        output += String.format("--\naverage age: %.2f", averageAge);

        return output;

    }

}
