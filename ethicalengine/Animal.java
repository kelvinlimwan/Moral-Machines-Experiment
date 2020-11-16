package ethicalengine;

/**
 * Animal is a derived class from Persona which represents an animal by its species and whether it
 * is a pet.
 * @author Kelvin Lim Wan
 */
public class Animal extends Persona {

    // constant variable
    private static final String DEFAULT_SPECIES = "dog";
    private static final int NUM_OF_CHARACTERISTICS = 3;

    // instance variables
    private String species;
    private boolean isPet;

    // constructors
    /**
     * Creates an animal with a default age, gender, body type, species and whether it is a pet.
     */
    public Animal() {
        species = DEFAULT_SPECIES;
        isPet = false;
    }
    /**
     * Creates an animal with the specified species and a default age, gender, body type and whether
     * it is a pet.
     * @param species the specifies species.
     */
    public Animal(String species) {
        this.species = species.toLowerCase();
        isPet = false;
    }
    /**
     * Creates an animal with the specified age, gender, body type, species and whether it is a pet.
     * @param age the specified age.
     * @param gender the specified gender.
     * @param bodyType the specified body type.
     * @param species the specified species.
     * @param isPet whether it is a pet.
     */
    public Animal(int age, Gender gender, BodyType bodyType, String species, boolean isPet) {
        super(age, gender, bodyType);
        this.species = species.toLowerCase();
        this.isPet = isPet;
    }
    /**
     * Creates an animal with the same age, gender, body type, species and whether it is a pet as
     * another animal.
     * @param otherAnimal the other animal.
     */
    public Animal(Animal otherAnimal) {
        this(otherAnimal.getAge(), otherAnimal.getGender(), otherAnimal.getBodyType(),
                otherAnimal.getSpecies(), otherAnimal.isPet());
    }

    // accessor methods
    /**
     * Gets the animal's species.
     * @return the animal's species.
     */
    public String getSpecies() {
        return species;
    }
    /**
     * Gets whether the animal is a pet.
     * @return whether the animal is a pet.
     */
    public boolean isPet() {
        return isPet;
    }

    /**
     * Gets the animal's characteristics: persona type, species and whether it is a pet.
     * @return a string array of the animal's characteristics.
     */
    @Override
    public String[] getCharacteristics() {

        String[] characteristics = new String[NUM_OF_CHARACTERISTICS];
        characteristics[0] = "animal";
        characteristics[1] = getSpecies().toLowerCase();
        characteristics[2] = isPet() ? "pet" : "";

        return characteristics;
    }

    // mutator methods
    /**
     * Sets the animal's species.
     * @param species the species to set the animal's species to.
     */
    public void setSpecies(String species){
        this.species = species.toLowerCase();
    }
    /**
     * Sets whether the animal is a pet.
     * @param isPet the 'whether the animal is a pet' to set the animal's 'whether it is a pet' to.
     */
    public void setPet(boolean isPet) {
        this.isPet = isPet;
    }

    /**
     * Returns a string representation of the animal.
     * @return the string representation of the animal.
     */
    @Override
    public String toString() {
        return species + (isPet ? " is pet" : "");
    }
}
