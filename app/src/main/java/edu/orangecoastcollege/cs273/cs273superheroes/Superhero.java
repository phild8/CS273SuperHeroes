package edu.orangecoastcollege.cs273.cs273superheroes;

/**
 * @author Phillip Davis
 *
 * Created by Phil
 *
 * The model for the CS273Superheroes
 * Creates new superheroes with mUserName, mName, mSuperpower, mOneThing, mFileName
 */

public class Superhero {
    private String mUserName;
    private String mName;
    private String mSuperpower;
    private String mOneThing;
    private String mFileName;

    /**
     * Overloaded Superhero constructor
     * @param userName the new mUserName
     * @param name the new mName
     * @param superpower the new mSuperpower
     * @param oneThing the new mOneTHing
     */
    public Superhero(String userName, String name, String superpower, String oneThing) {
        mUserName = userName;
        mName = name;
        mSuperpower = superpower;
        mOneThing = oneThing;

        mFileName = "Superheroes/" + userName + ".png";
    }

    /**
     * gets m username
     * @return mUsername
     */
    public String getUserName() {
        return mUserName;
    }

    /**
     * gets the name
     * @return mUsername
     */
    public String getName() {
        return mName;
    }

    /**
     * get the superpower
     * @return mSuperPower
     */
    public String getSuperpower() {
        return mSuperpower;
    }

    /**
     * gets the one thing
     * @return mOneThing
     */
    public String getOneThing() {
        return mOneThing;
    }

    /**
     * get the file name
     * @return mFileName
     */
    public String getFileName() {
        return mFileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Superhero superhero = (Superhero) o;

        if (getUserName() != null ? !getUserName().equals(superhero.getUserName()) : superhero.getUserName() != null)
            return false;
        if (getName() != null ? !getName().equals(superhero.getName()) : superhero.getName() != null)
            return false;
        if (getSuperpower() != null ? !getSuperpower().equals(superhero.getSuperpower()) : superhero.getSuperpower() != null)
            return false;
        if (getOneThing() != null ? !getOneThing().equals(superhero.getOneThing()) : superhero.getOneThing() != null)
            return false;
        return getFileName() != null ? getFileName().equals(superhero.getFileName()) : superhero.getFileName() == null;

    }

    @Override
    public int hashCode() {
        int result = getUserName() != null ? getUserName().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getSuperpower() != null ? getSuperpower().hashCode() : 0);
        result = 31 * result + (getOneThing() != null ? getOneThing().hashCode() : 0);
        result = 31 * result + (getFileName() != null ? getFileName().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Superhero{" +
                "mUserName='" + mUserName + '\'' +
                ", mName='" + mName + '\'' +
                ", mSuperpower='" + mSuperpower + '\'' +
                ", mOneThing='" + mOneThing + '\'' +
                ", mFileName='" + mFileName + '\'' +
                '}';
    }
}
