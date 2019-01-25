package com.techart.cidrz.constants;

/**
 * Created by brad on 2017/02/05.
 * Stores firebase node keys and other com.techart.cidrz.constants to prevent spelling mistakes in different part of
 * the apps
 */

public class Constants {
    public static final String IMAGE_URL = "imageUrl";
    public static final String TIME_CREATED = "timeCreated";
    public static final String FACILITY_NAME = "facilityName";
    public static final String FACILITY_MODE = "facilityMode";
    public static final String FACILITY_KEY = "Facility";

    // Name of Notification Channel for verbose notifications of background work
    public static final CharSequence VERBOSE_NOTIFICATION_CHANNEL_NAME =
            "Verbose WorkManager Notifications";
    public static String VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
            "Shows notifications whenever work starts";
    public static final CharSequence NOTIFICATION_TITLE = "Progress report";
    public static final String CHANNEL_ID = "VERBOSE_NOTIFICATION" ;
    public static final int NOTIFICATION_ID = 1;
}
