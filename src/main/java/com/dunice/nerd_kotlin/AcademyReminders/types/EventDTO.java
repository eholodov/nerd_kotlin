package com.dunice.nerd_kotlin.AcademyReminders.types;

import com.dunice.nerd_kotlin.academyReminder.types.Event;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventDTO {

    private String date;
    private String trainee;
    private String eventType;
    private String interviewer;
    private List<String> recipients;
    private DayOfWeek dayOfWeek;

    public void setDate(String date) {
        this.date = date;
    }

    public void setTrainee(String trainee) {
        this.trainee = trainee;
    }

    public void setInterviewer(String interviewer) {
        this.interviewer = interviewer;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getDate() {
        return date;
    }

    public String getTrainee() {
        return trainee;
    }

    public String getInterviewer() {
        return interviewer;
    }

    public String getEventType() {
        return eventType;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public static EventDTO toEventDTO(Event event) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setDate(event.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));
        eventDTO.setTrainee(event.getTrainee());
        eventDTO.setInterviewer(event.getInterviewer());
        eventDTO.setEventType(event.getEventType());
        eventDTO.setRecipients(event.getRecipients());
        eventDTO.setDayOfWeek(event.getDate().getDayOfWeek());
        return eventDTO;
    }

}
