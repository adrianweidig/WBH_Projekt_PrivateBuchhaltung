package wbh.wbh_projekt_privatebuchhaltung.models.interfaces;

import wbh.wbh_projekt_privatebuchhaltung.models.userProfile.Profile;

/**
 * Interface for classes that require a {@link Profile} instance.
 * Implementing classes must provide a method to set the profile.
 */
public interface ProfileAware {

    /* -------------------------------- */
    /* ------ Abstract Methods   ------ */
    /* -------------------------------- */

    /**
     * Sets the {@link Profile} for the implementing class.
     *
     * @param profile the {@link Profile} instance to be set
     */
    void setProfile(Profile profile);
}
