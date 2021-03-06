package lamzone.com.service;

import java.util.Calendar;
import java.util.List;

import lamzone.com.model.Meeting;
import lamzone.com.model.Participant;
import lamzone.com.model.Room;

/**
 * Neighbour API client
 * Created by Steve LEROY on 04/04/2020.
 */

public interface MeetingApiService {

    /**
     * Get all my Meetings
     *
     * @return {@link List}
     */
    List<Meeting> getMeetings();   // renvoie meeting

    List<Room> getRooms();

    List<Participant> getParticipants();

    /**
     * Deletes a meeting
     *
     * @param meeting
     * @return
     */
    List<Meeting> deleteMeeting(Meeting meeting);  // supprime le meeting


    /**
     * Add meeting
     */
    void addMeeting(Meeting meeting);     // ajoute un meeting


    /**
     * Get all Meetings in order by Day (liste filtrée)
     */
    List<Meeting> filterMeetingListForDay(Calendar calendar);


    /**
     * Get all Meetings in order of Rooms (liste filtrée)
     */
    List<Meeting> filterMeetingListForRoom(String room);

}