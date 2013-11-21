package org.devcon.android.util;

public class AppConfig {


    public static final long CONFERENCE_START_MILLIS = ParserUtils
            .parseTime("2013-11-23T09:00:00.000-07:00");
    public static final long CONFERENCE_END_MILLIS = ParserUtils
            .parseTime("2013-12-24T23:00:00.000-07:00");

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static final String BASE_URL = "http://devconmyanmar.herokuapp.com/api/v1/";
    public static final String SPEAKERS_URL = BASE_URL + "speakers";
    public static final String TALKS_URL= BASE_URL + "schedules";
    // TODO
    public static final String FEEDBACKS_URL = BASE_URL + "feedbacks";

    public static final String DUMMY_PHOTO_URL = "http://devconmyanmar.org/2013/wp-content/uploads/2013/10/default-speaker-150x150.png";

}
