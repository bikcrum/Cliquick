package com.bikrampandit.cliquick.Utility;

/**
 * Created by confided on 021, Aug 21, 2017.
 */

public class Constant {
    //preferences
    public static final String PREFERENCE_NAME = "preference";
    public static final String FIRST_LAUNCH = "first launch";

    public static final String VOLUME_UP_ENABLED = "volume up enabled";
    public static final String VOLUME_UP_TIME_INDEX = "volume up time index";
    public static final String VOICE_ENABLED = "voice enabled";
    public static final String VOICE_CODE = "voice code";
    public static final String TEXT_MESSAGING_ENABLED = "text messaging enabled";
    public static final String CALL_ENABLED = "call enabled";

    public static final String VOLUME_DOWN_ENABLED = "volume down enabled";
    public static final String VOLUME_DOWN_TIME_INDEX = "volume down time index";
    public static final String TAKE_PHOTO_BACK_CAM = "take photo from back camera";
    public static final String TAKE_PHOTO_FRONT_CAM = "take photo from front camera";
    public static final String TAKE_VIDEO = "take video";

    //preferences inside settings
    public static final String SCHEDULE = "schedule";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String REPEAT_EVERY_WEEK = "repeat_every_week";
    public static final String WEEK_ENABLE = "weeks";
    public static final String SHUTTER_SOUND = "shutter_sound";
    public static final String PANIC_TEXT = "panic_text";
    public static final String SEND_LOCATION = "send_location";
    public static final String SEND_LOCATION_FREQUENCY = "send_location_frequency";

    //database setting
    public static final String TABLE_NAME = "setting";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_FLAG = "flag";

    //other
    public static final int PICK_CONTACT_FOR_TEXT_MSG = 0;
    public static final int PICK_CONTACT_FOR_CALL = 1;
    public static final int SETTING_COMPLETE = 100;
    public static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS_FOR_TEXT_MSG = 2;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS_FOR_CALL = 3;
    public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4;
    public static final int PERMISSIONS_VIBRATE = 5;
    public static final int PERMISSIONS_POCKET_SPHINX = 6;
    public static final int PERMISSIONS_CAMERA = 7;
    public static final int PERMISSION_CAPTURE_IMAGE_IN_BACKGROUND = 8;
    public static final String TIME_PICKER = "time picker";
    public static final String FILE_PATH = "/storage/emulated/0/Cliquick";
    public static final String VOICE_SWITCH_CHANGED = "voice_switch_changed";
    public static final String IMAGE_FILE_EXTENSION = ".jpg";
    public static final String VIDEO_FILE_EXTENSION = ".mp4";
    public static final String NEW_FILE_CREATED = "new file recorded";
    public static final String VOICE_MATCHED = "voice_matched";
    public static final String ERROR_WHILE_PARSING_VOICE_SEARCH_CODE = "error while parsing voice search code";
    public static final String ERROR_RECOGNISING = "error recognising";
    public static final String  CURRENT_FILE_SELECTION = "current file selection";
    public static final String UPDATE_FILES = "update_files";

    //defaults
    public static final String DEFAULT_PANIC_TEXT = "Help me! I'm in trouble";
    public static final String DEFAULT_VOICE_CODE = "5115";
    public static final String DEFAULT_START_TIME = "9:00";         // 9 AM
    public static final String DEFAULT_END_TIME = "22:00";   // 10 PM
    public static final String DEFAULT_WEEK = "1111111";
    public static final String PREV_VOLUME = "prev_vol";
    public static final long[] VIBRATE_PATTERN  = {0,20,100,20};;
    public static String DELETED_POSITIONS = "deleted_positions";
    public static int REQUEST_DELETED_POSITIONS = 5;
}
