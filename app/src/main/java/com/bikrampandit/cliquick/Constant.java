package com.bikrampandit.cliquick;

/**
 * Created by confided on 021, Aug 21, 2017.
 */

class Constant {
    //preferences
    static final String PREFERENCE_NAME = "preference";
    static final String FIRST_LAUNCH = "first launch";

    static final String VOLUME_UP_ENABLED = "volume up enabled";
    static final String VOLUME_UP_TIME_INDEX = "volume up time index";
    static final String VOICE_ENABLED = "voice enabled";
    static final String VOICE_CODE = "voice code";
    static final String TEXT_MESSAGING_ENABLED = "text messaging enabled";
    static final String CALL_ENABLED = "call enabled";

    static final String VOLUME_DOWN_ENABLED = "volume down enabled";
    static final String VOLUME_DOWN_TIME_INDEX = "volume down time index";
    static final String TAKE_PHOTO_BACK_CAM = "take photo from back camera";
    static final String TAKE_PHOTO_FRONT_CAM = "take photo from front camera";
    static final String TAKE_VIDEO = "take video";

    //preferences inside settings
    static final String SCHEDULE = "schedule";
    static final String START_TIME = "start_time";
    static final String END_TIME = "end_time";
    static final String REPEAT_EVERY_WEEK = "repeat_every_week";
    static final String WEEK_ENABLE = "weeks";
    static final String VIBRATE = "vibrate";
    static final String PANIC_TEXT = "panic_text";
    static final String SEND_LOCATION = "send_location";
    static final String SEND_LOCATION_FREQUENCY = "send_location_frequency";

    //database setting
    static final String TABLE_NAME = "setting";
    static final String COLUMN_CONTACT = "contact";
    static final String COLUMN_FLAG = "flag";

    //other
    static final int PICK_CONTACT_FOR_TEXT_MSG = 0;
    static final int PICK_CONTACT_FOR_CALL = 1;
    static final int SETTING_COMPLETE = 100;
    static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    static final int PERMISSIONS_REQUEST_READ_CONTACTS_FOR_TEXT_MSG = 2;
    static final int PERMISSIONS_REQUEST_READ_CONTACTS_FOR_CALL= 3;
    static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4;
    static final int PERMISSIONS_VIBRATE = 5;
    static final int PERMISSIONS_POCKET_SPHINX = 6;
    static final int PERMISSIONS_CAMERA = 7;
    static final int PERMISSION_CAPTURE_IMAGE_IN_BACKGROUND = 8;
    static final String TIME_PICKER = "time picker";

    //defaults
    static final String DEFAULT_PANIC_TEXT = "Help me! I'm in trouble";
    static final String DEFAULT_VOICE_CODE = "5115";
    static final String DEFAULT_START_TIME = "9:00";         // 9 AM
    static  final String DEFAULT_END_TIME = "22:00";   // 10 PM
    static  final String DEFAULT_WEEK = "1111111";
    static final String PREV_VOLUME = "prev_vol";
}
