package uk.gov.digital.ho.hocs.notify.application;

public enum LogEvent {
    INFO_CLIENT_GET_CONTACTS_SUCCESS,
    INFO_CLIENT_GET_USER_SUCCESS,
    INFO_CLIENT_GET_TEAM_SUCCESS,
    REST_HELPER_GET,
    NOTIFY_EMAIL_FAILED,
    NOTIFY_COMMAND_CREATION_FAILED;
    public static final String EVENT = "event_id";
    public static final String EXCEPTION = "exception";

}
