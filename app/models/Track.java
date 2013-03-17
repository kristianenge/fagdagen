package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Represents a track.
 * Within a track, there are presentations.
 *
 * @author kristian.haugene
 */
@Entity
public class Track extends Model {

    @Id
    public Long id;

    // Each track should have a name
    @Constraints.Required
    public String name;

    // Each track has a rank that allows us to place it correctly
    @Constraints.Required
    public Long rank;

    // Presentations for this Track
    @OneToMany(mappedBy="track", cascade= CascadeType.ALL)
    public List<Presentation> presentations;

    public static Model.Finder<String,Track> find = new Model.Finder(String.class, Track.class);

    /**
     * Retrieve all tracks.
     */
    public static List<Track> findAll() {
        return find.all();
    }

    /**
     * Retrieve relative rank compared to other tracks
     */
    public int getRelativeRanking() {
        List<Track> tracks = find.all();

        int relativeRank = 1;

        for(Track track: tracks) {
            if(rank > track.rank) {
                relativeRank++;
            }
        }

        return relativeRank;
    }

}
