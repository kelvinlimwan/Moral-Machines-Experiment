package ethicalengine;

public class Human extends Persona {

    /*
    class invariant: if the human's gender is not female, the human cannot be pregnant AND
    only humans who belong to the age category ADULT have a profession
     */

    // constant variables
    private static final int MAX_AGE_BABY = 4;
    private static final int MAX_AGE_CHILD = 16;
    private static final int MAX_AGE_ADULT = 68;
    private static final int NUM_OF_CHARACTERISTICS = 7;

    // enumeration types
    public enum AgeCategory {BABY, CHILD, ADULT, SENIOR};
    public enum Profession {DOCTOR, CEO, CRIMINAL, HOMELESS, UNEMPLOYED, ENGINEER, BUILDER, NONE}

    // instance variables
    private Profession profession;
    private boolean isPregnant;
    private boolean isYou;

    // constructors
    public Human() {
        profession = Profession.NONE;
        isPregnant = false;
        isYou = false;
    }
    public Human(int age, Gender gender, BodyType bodyType) {
        super(age, gender, bodyType);
        profession = Profession.NONE;
        isPregnant = false;
        isYou = false;
    }
    public Human(int age, Profession profession, Gender gender, BodyType bodyType,
                 boolean isPregnant) {

        super(age, gender, bodyType);

        if (getAgeCategory() == AgeCategory.ADULT) {
            this.profession = profession;
        } else {
            this.profession = Profession.NONE;
        }

        if (getGender() == Gender.FEMALE) {
            this.isPregnant = isPregnant;
        } else {
            this.isPregnant = false;
        }

        isYou = false;
    }
    public Human(Human otherHuman) {
        this(otherHuman.getAge(), otherHuman.getProfession(), otherHuman.getGender(),
                otherHuman.getBodyType(), otherHuman.isPregnant());
        isYou = otherHuman.isYou();
    }

    // accessor methods
    public AgeCategory getAgeCategory() {

        // invariant in Persona class ensures age >= 0
        if (getAge() <= MAX_AGE_BABY) {
            return AgeCategory.BABY;
        } else if (getAge() <= MAX_AGE_CHILD) {
            return AgeCategory.CHILD;
        } else if (getAge() <= MAX_AGE_ADULT) {
            return AgeCategory.ADULT;
        }
        return AgeCategory.SENIOR;
    }
    public Profession getProfession() {
        if (getAgeCategory() != AgeCategory.ADULT) {
            return Profession.NONE;
        }
        return profession;
    }
    public boolean isPregnant() {
        if (getGender() != Gender.FEMALE) {
            return false;
        }
        return isPregnant;
    }
    public boolean isYou() {
        return isYou;
    }

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

    // mutator methods
    public void setPregnant(boolean pregnant) {
        if (getGender() == Gender.FEMALE) {
            isPregnant = pregnant;
        }
    }
    public void setAsYou(boolean isYou) {
        this.isYou = isYou;
    }

    @Override
    public String toString() {
        return (isYou() ? "you " : "") + getBodyType().toString().toLowerCase() + " " +
                getAgeCategory().toString().toLowerCase() + " " +
                (getAgeCategory() == AgeCategory.ADULT ? profession.toString().toLowerCase() +
                        " " : "") + getGender().toString().toLowerCase() + (isPregnant() ?
                " pregnant" : "");
    }
}
