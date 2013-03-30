package utils;

import com.avaje.ebean.Ebean;
import models.Presentation;
import models.Slot;
import models.Track;
import org.joda.time.DateTime;
import play.Logger;

import java.util.Collections;
import java.util.List;

/**
 * This utility class will add some slots and presentations to the database
 */
public class InitialDataUtil {

    public static void addInitialData() {
        Logger.of(InitialDataUtil.class).info("Adding initial data");

        addSlotsWithPresentations();
    }

    /**
     * Helper method that adds the slots and populate them with presentations.
     * We group them, adding "presentation slots" first and breaks(15 min) after.
     * This is so that we don't assume anything from the ordering in the real database, as this is test data.
     */
    private static void addSlotsWithPresentations() {
        // First presentation slot - keynote
        addSlot(9, 0, 30, SlotType.KEYNOTE);

        // Second presentation slot
        addSlot(9, 45, 30, SlotType.PRESENTATIONS);

        // Third presentation slot
        addSlot(10, 30, 30, SlotType.PRESENTATIONS);

        // Fourth presentation slot
        addSlot(11, 15, 30, SlotType.PRESENTATIONS);

        // Break 1
        addSlot(9, 30, 15, SlotType.BREAK);

        // Break 2
        addSlot(10, 15, 15, SlotType.BREAK);

        // Break 3
        addSlot(11, 00, 15, SlotType.BREAK);
    }

    /**
     * Adds a slot to the database.
     *
     * @param hour     the hour of the day for the presentation
     * @param minute   the minute of the hour for the presentation
     * @param duration duration of the slot in minutes
     */
    private static void addSlot(Integer hour, Integer minute, Integer duration, SlotType type)
    {
        // Create slot
        Slot slot = crateSlot(hour, minute, duration);

        // Save it
        Ebean.save(slot);

        // Do something for the slot, depending on which type it is
        switch (type) {
            case PRESENTATIONS:
                addPresentations(slot);
                break;

            case KEYNOTE:
                addKeynote(slot);
                break;

            case BREAK:
                slot.isBreak = true;
                Ebean.update(slot);
                break;

            default:
                // Do nothing
        }
    }

    /**
     * Adds sample presentations for a slot
     * @param slot to add presentations for
     */
    private static void addPresentations(Slot slot) {

        // Get all Tracks and sort them
        List<Track> tracks = Track.find.all();
        Collections.sort(tracks);

        for (Track track : tracks) {
            addPresentation(slot, track);
        }
    }

    private static void addPresentation(Slot slot, Track track) {

        Presentation presentation = new Presentation();
        presentation.name = "Hello World";
        presentation.description = "This is a description of the presentation. Formally we like to call it an abstract. This should give a brief introduction to the topic";
        presentation.presenter = "Stephen Hawking";
        presentation.slot = slot;
        presentation.track = track;

        // For now we only add one presentation per slot. They can all have rank 1.
        presentation.rank = 1;

        Ebean.save(presentation);
    }

    /**
     * Adds a keynote for slot
     * @param slot to add keynote for
     */
    private static void addKeynote(Slot slot) {

        Presentation presentation = new Presentation();
        presentation.name = "Keynote Yeah";
        presentation.description = "In this keynote, something interesting will be discussed. Everyone should be here or be []";
        presentation.presenter = "Someone Awesome";
        presentation.slot = slot;

        // Assign to the first track
        presentation.track = Track.findAll().get(0);

        // For now we only add one presentation per slot. They can all have rank 1.
        presentation.rank = 1;

        // This is a keynote
        presentation.isKeynote = true;

        // Save it
        Ebean.save(presentation);
    }

    /**
     * Creates a slot object
     * @param hour the hour of the day this slot starts
     * @param minute the minute of the hour this slot start
     * @param duration slot length
     * @return fresh slot
     */
    private static Slot crateSlot(Integer hour, Integer minute, Integer duration) {
        // Create a new slot
        Slot slot = new Slot();

        // Set startTime
        slot.startTime = new DateTime(2013, 4, 25, hour, minute).toDate();
        slot.endTime = new DateTime(slot.startTime).plusMinutes(duration).toDate();
        return slot;
    }

    /**
     * Types of slots
     */
    private enum SlotType {
        BREAK, KEYNOTE, PRESENTATIONS
    }
}