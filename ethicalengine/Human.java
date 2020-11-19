package ethicalengine;

/**
 * Human is a derived class from Persona which represents a human by his/her profession, pregnancy
 * status and whether he/she is 'you' (on top of the characteristics in Persona).
 * @author Kelvin Lim Wan
 */
public class Human extends Persona {

    // constant variables
    private static final int MAX_AGE_BABY = 4;
    private static final int MAX_AGE_CHILD = 16;
    private static final int MAX_AGE_ADULT = 68;
    private static final int NUM_OF_CHARACTERISTICS = 7;

    // enumerations
    public enum AgeCategory {BABY, CHILD, ADULT, SENIOR}
    public enum Profession {DOCTOR, CEO, CRIMINAL, HOMELESS, UNEMPLOYED, ENGINEER, STUDENT, NONE}

    // instance variables
    private Profession profession;
    private boolean isPregnant;
    private boolean isYou;

    /**
     * Creates a human with a default age, gender, body type, profession, pregnancy status and
     * whether he/she is you.
     */
    public Human() {
        profession = Profession.NONE;
        isPregnant = false;
        isYou = false;
    }

    /**
     * Creates a human with the specified age, gender and body type and a default profession,
     * pregnancy status and whether he/she is you.
     * @param age the specified age.
     * @param gender the specified gender.
     * @param bodyType the specified body type.
     */
    public Human(int age, Gender gender, BodyType bodyType) {
        this(age, Profession.NONE, gender, bodyType, false, false);
    }

    /**
     * Creates a human with the specified age, gender, body type, profession, pregnancy status and a
     * default whether he/she is you.
     * @param age the specified age.
     * @param gender the specified gender.
     * @param bodyType the specified body type.
     * @param isPregnant the specified pregnancy status.
     */
    public Human(int age, Profession profession, Gender gender, BodyType bodyType,
                 boolean isPregnant) {
        this(age, profession, gender, bodyType, isPregnant, false);
    }

    /**
     * Creates a human with the specified age, gender, body type, profession, pregnancy status and
     * whether he/she is you.
     * @param age the specified age.
     * @param gender the specified gender.
     * @param bodyType the specified body type.
     * @param isPregnant the specified pregnancy status.
     * @param isYou whether he/she is 'you'.
     */
    public Human(int age, Profession profession, Gender gender, BodyType bodyType,
                 boolean isPregnant, boolean isYou) {
        super(age, gender, bodyType);

        if (getAgeCategory() == AgeCategory.ADULT) {
            this.profession = profession;
        } else {
            this.profession = Profession.NONE;
        }

        if (gender == Gender.FEMALE) {
            this.isPregnant = isPregnant;
        } else {
            this.isPregnant = false;
        }

        this.isYou = isYou;
    }

    /**
     * Creates a human with the same age, gender, body type, profession, pregnancy status and
     * whether he/she is you as another human.
     * @param otherHuman the other human.
     */
    public Human(Human otherHuman) {
        this(otherHuman.getAge(), otherHuman.getProfession(), otherHuman.getGender(),
                otherHuman.getBodyType(), otherHuman.isPregnant(), otherHuman.isYou());
    }

    /**
     * Gets the human's age category.
     * @return the human's age category.
     */
    public AgeCategory getAgeCategory() {
        if (getAge() <= MAX_AGE_BABY) {
            return AgeCategory.BABY;

        } else if (getAge() <= MAX_AGE_CHILD) {
            return AgeCategory.CHILD;

        } else if (getAge() <= MAX_AGE_ADULT) {
            return AgeCategory.ADULT;
        }

        return AgeCategory.SENIOR;
    }

    /**
     * Gets the human's profession.
     * @return the human's profession.
     */
    public Profession getProfession() {
        if (getAgeCategory() != AgeCategory.ADULT) {
            return Profession.NONE;
        }

        return profession;
    }

    /**
     * Gets the human's pregnancy status.
     * @return the human's pregnancy status.
     */
    public boolean isPregnant() {
        if (getGender() != Gender.FEMALE) {
            return false;
        }

        return isPregnant;
    }

    /**
     * Gets whether the human is you.
     * @return whether the human is you.
     */
    public boolean isYou() {
        return isYou;
    }

    /**
     * Gets the human's characteristics: persona type, age category, gender, body type, profession,
     * pregnancy status and whether he/she is you.
     * @return a string array of the human's characteristics.
     */
    @Override
    public String[] getCharacteristics() {
        String[] characteristics = new String[NUM_OF_CHARACTERISTICS];
        characteristics[0] = "human";
        characteristics[1] = getAgeCategory().toString().toLowerCase();
        characteristics[2] = getGender().toString().toLowerCase();
        characteristics[3] = getBodyType().toString().toLowerCase();
        characteristics[4] = getProfession().toString().toLowerCase();
        characteristics[5] = isPregnant() ? "pregnant" : "";
        characteristics[6] = isYou() ? "you" : "";

        return characteristics;
    }

    /**
     * Sets the human's pregnancy status.
     * @param pregnant the pregnancy status to set the human's pregnancy status to.
     */
    public void setPregnant(boolean pregnant) {
        if (getGender() == Gender.FEMALE) {
            isPregnant = pregnant;
        }
    }
    /**
     * Sets whether the human is you.
     * @param isYou the 'whether the human is you' to set the human's 'whether he/she is you' to.
     */
    public void setAsYou(boolean isYou) {
        this.isYou = isYou;
    }

    /**
     * Returns a string representation of the human.
     * @return the string representation of the human.
     */
    @Override
    public String toString() {
        return (isYou ? "you " : "") + getBodyType().toString().toLowerCase() + " " +
                getAgeCategory().toString().toLowerCase() + " " +
                (getAgeCategory() == AgeCategory.ADULT ? profession.toString().toLowerCase() + " " :
                        "") + getGender().toString().toLowerCase() + (isPregnant ? " pregnant" :
                "");
    }
}
