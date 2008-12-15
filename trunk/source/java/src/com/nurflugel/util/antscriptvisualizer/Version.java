package com.nurflugel.util.antscriptvisualizer;

/**
 * This class represents a version of the app.  That includes the version number, as well as a list of new features.
 */

@SuppressWarnings({"AccessingNonPublicFieldOfAnotherObject"})
public class Version implements Comparable {

    private String[] features;
    private int      major;
    private int      minor;
    private int      point;

    public Version(String version) {
        String[] strings = version.split("\\.");

        if ((strings.length > 0) && (strings[0].length() > 0)) {
            major = Integer.parseInt(strings[0]);
        }

        if ((strings.length > 1) && (strings[1].length() > 0)) {
            minor = Integer.parseInt(strings[1]);
        }

        if ((strings.length > 2) && (strings[2].length() > 0)) {
            point = Integer.parseInt(strings[2]);
        }
    }


    public void setFeatures(String[] features) {
        this.features = features;
    }


    public String[] getFeatures() {
        return features;
    }


    public int getMajor() {
        return major;
    }


    public int getMinor() {
        return minor;
    }


    public int getPoint() {
        return point;
    }


    public int compareTo(Object object) {
        Version otherVersion = (Version) object;

        if (otherVersion.major < major) {
            return 1;
        }

        if (otherVersion.major > major) {
            return -1;
        } else {

            if (otherVersion.minor < minor) {
                return 1;
            }

            if (otherVersion.minor > minor) {
                return -1;
            } else {

                if (otherVersion.point < point) {
                    return 1;
                }

                if (otherVersion.point > point) {
                    return -1;
                }
            }
        }

        return 0;
    }


    public String toString() {
        return major + "." + minor + "." + point;
    }
}
