package ethicalengine;

public class Persona {

    /*
    class invariant: age >= 0
     */

    // constants
    private static final int MIN_AGE = 0;
    private static final int DEFAULT_AGE = 30;

    // instance variables
    private int age;
    private Gender gender;
    private BodyType bodyType;

    // enumeration types
    public enum Gender {FEMALE, MALE, UNKNOWN};
    public enum BodyType {AVERAGE, ATHLETIC, OVERWEIGHT, UNSPECIFIED};

    // constructors
    public Persona() {
        this(DEFAULT_AGE, Gender.UNKNOWN, BodyType.UNSPECIFIED);
    }
    public Persona(int age, Gender gender, BodyType bodyType) {

        if (age >= MIN_AGE) {
            this.age = age;
        } else {
            this.age = DEFAULT_AGE;
        }

        this.gender = gender;
        this.bodyType = bodyType;
    }
    public Persona(Persona otherPersona) {
        this(otherPersona.getAge(), otherPersona.getGender(), otherPersona.getBodyType());
    }

    // accessor methods
    public int getAge() {
        return age;
    }
    public Gender getGender() {
        return gender;
    }
    public BodyType getBodyType() {
        return bodyType;
    }

    // mutator methods
    public void setAge(int age) {
        if (age >= MIN_AGE) {
            this.age = age;
        }
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }
}
