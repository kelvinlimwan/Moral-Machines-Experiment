package ethicalengine;

/**
 * Persona is an abstract base class to represent a persona (human or animal) by his/her age,
 * gender and body type.
 * @author Kelvin Lim Wan
 */
public abstract class Persona {

    // constant variables
    private static final int MIN_AGE = 0;
    private static final int DEFAULT_AGE = 15;

    // enumerations
    public enum Gender {FEMALE, MALE, UNKNOWN};
    public enum BodyType {AVERAGE, ATHLETIC, OVERWEIGHT, UNSPECIFIED};

    // instance variables
    private int age;
    private Gender gender;
    private BodyType bodyType;

    /**
     * Creates a persona with a default age, gender and body type.
     */
    public Persona() {
        this(DEFAULT_AGE, Gender.UNKNOWN, BodyType.UNSPECIFIED);
    }

    /**
     * Creates a persona with the specified age, gender and body type.
     * @param age the specified age.
     * @param gender the specified gender.
     * @param bodyType the specified body type.
     */
    public Persona(int age, Gender gender, BodyType bodyType) {

        if (age >= MIN_AGE) {
            this.age = age;
        } else {
            this.age = DEFAULT_AGE;
        }

        this.gender = gender;
        this.bodyType = bodyType;
    }

    /**
     * Creates a persona with the same age, gender and body type as another persona.
     * @param otherPersona the other persona.
     */
    public Persona(Persona otherPersona) {
        this(otherPersona.getAge(), otherPersona.getGender(), otherPersona.getBodyType());
    }

    /**
     * Gets the persona's age.
     * @return the persona's age.
     */
    public int getAge() {
        return age;
    }

    /**
     * Gets the persona's gender.
     * @return the persona's gender.
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Gets the persona's body type.
     * @return the persona's body type.
     */
    public BodyType getBodyType() {
        return bodyType;
    }

    /**
     * Gets the persona's characteristics (see more in the derived classes Human and Animal).
     * @return a string array of the persona's characteristics.
     */
    public abstract String[] getCharacteristics();

    /**
     * Sets the persona's age.
     * @param age the age to set the persona's age to.
     */
    public void setAge(int age) {
        if (age >= MIN_AGE) {
            this.age = age;
        }
    }

    /**
     * Sets the persona's gender.
     * @param gender the gender to set the persona's gender to.
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * Sets the persona's body type.
     * @param bodyType the body type to set the persona's body type to.
     */
    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }
}
