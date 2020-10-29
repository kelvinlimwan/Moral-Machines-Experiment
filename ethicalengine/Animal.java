package ethicalengine;

public class Animal extends Persona {

    // constant variable
    private static final String DEFAULT_SPECIES = "dog";

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

    // mutator methods
    public void setSpecies(String species){
        this.species = species.toLowerCase();
    }
    public void setPet(boolean isPet) {
        this.isPet = isPet;
    }

    @Override
    public String toString() {
        String optIsPet = (isPet())? " is pet" : "";
        return species + (isPet() ? " is pet" : "");
    }
}
