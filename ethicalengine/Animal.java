package ethicalengine;

public class Animal extends Persona {

    // constant variable
    private static final String DEFAULT_SPECIES = "dog";
    private static final int NUM_OF_CHARACTERISTICS = 3;

    // instance variables
    private String species;
    private boolean isPet;

    // constructors
    public Animal() {
        species = DEFAULT_SPECIES;
        isPet = false;
    }
    public Animal(String species) {
        this.species = species.toLowerCase();
        isPet = false;
    }
    public Animal(int age, Gender gender, BodyType bodyType, String species, boolean isPet) {
        super(age, gender, bodyType);
        this.species = species.toLowerCase();
        this.isPet = isPet;
    }
    public Animal(Animal otherAnimal) {
        this(otherAnimal.getAge(), otherAnimal.getGender(), otherAnimal.getBodyType(),
                otherAnimal.getSpecies(), otherAnimal.isPet());
    }

    // accessor methods
    public String getSpecies() {
        return species;
    }
    public boolean isPet() {
        return isPet;
    }

    public String[] getCharacteristics() {

        String[] characteristics = new String[NUM_OF_CHARACTERISTICS];
        characteristics[0] = "animal";
        characteristics[1] = getSpecies().toLowerCase();
        characteristics[2] = isPet() ? "pet" : "";

        return characteristics;
    }

    // mutator methods
    public void setSpecies(String species){
        this.species = species.toLowerCase();
    }
    public void setPet(boolean isPet) {
        this.isPet = isPet;
    }

    @Override
    public String toString() {
        return species + (isPet() ? " is pet" : "");
    }
}
