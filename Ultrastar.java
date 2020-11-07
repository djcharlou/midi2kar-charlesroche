import javax.sound.midi.Track;
import java.util.HashMap;
import java.util.Vector;

// 
// Decompiled by Procyon v0.5.36
// 

public class Ultrastar
{
    private float lBPM;
    private long duration;
    private Vector words;
    private Vector start;
    private Vector entete;
    private Vector time;
    private HashMap melody;
    private HashMap wordTrack;
    private int lyricTrack;
    private int melodyTrack;
    
    public Ultrastar() {
        this.words = new Vector();
        this.start = new Vector();
        this.entete = new Vector();
        this.time = new Vector();
        this.melody = new HashMap();
        this.wordTrack = new HashMap();
        this.lyricTrack = -1;
        this.melodyTrack = -1;
    }
    
    public float getLBPM() {
        return this.lBPM;
    }
    
    public void setLBPM(final float lbpm) {
        this.lBPM = lbpm;
    }
    
    public void addSyllabe(final String syllabe) {
        this.words.add(syllabe);
    }
    
    public void addStart(final String start) {
        this.start.add(start);
    }
    
    public void addEntete(final String entete) {
        this.entete.add(entete);
    }
    
    public void addTime(final String time) {
        this.time.add(time);
    }
    
    public void addMelody(final String time, final String intonation) {
        this.melody.put(time, intonation);
    }
    
    public String getMelody(final String time, final boolean melodyFirst) {
        long lTime = Long.parseLong(time);
        String intonation = this.melody.get(time);
        if (melodyFirst) {
            while (intonation == null) {
                if (lTime < 0L) {
                    break;
                }
                --lTime;
                intonation = this.melody.get(String.valueOf(lTime));
            }
        }
        else {
            while (intonation == null && lTime <= this.duration) {
                ++lTime;
                intonation = this.melody.get(String.valueOf(lTime));
            }
        }
        if (intonation == null) {
            if (melodyFirst) {
                while (intonation == null) {
                    if (lTime > this.duration) {
                        break;
                    }
                    ++lTime;
                    intonation = this.melody.get(String.valueOf(lTime));
                }
            }
            else {
                while (intonation == null && lTime >= 0L) {
                    --lTime;
                    intonation = this.melody.get(String.valueOf(lTime));
                }
            }
        }
        return intonation;
    }
    
    public Vector getStart() {
        return this.start;
    }
    
    public Vector getWords() {
        return this.words;
    }
    
    public Vector getEntete() {
        return this.entete;
    }
    
    public Vector getTime() {
        return this.time;
    }
    
    public long getDuration() {
        return this.duration;
    }
    
    public void setDuration(final long duration) {
        this.duration = duration;
    }
    
    public Track getMelodyTrack(final Track[] tracks) {
        if (this.melodyTrack != -1) {
            return tracks[this.melodyTrack];
        }
        return null;
    }
    
    public void setMelodyTrack(final int melodyTrack) {
        this.melodyTrack = melodyTrack;
    }
    
    public int getMelodyTrackNumber() {
        return this.melodyTrack;
    }
    
    public HashMap getWordTrack() {
        return this.wordTrack;
    }
    
    public Track getLyricTrack(final Track[] tracks) {
        if (this.lyricTrack != -1) {
            return tracks[this.lyricTrack];
        }
        return null;
    }
    
    public void setLyricTrack(final int lyricTrack) {
        this.lyricTrack = lyricTrack;
    }
    
    public void setStart(final Vector start) {
        this.start = start;
    }
    
    public void setTime(final Vector time) {
        this.time = time;
    }
    
    public void setWords(final Vector words) {
        this.words = words;
    }
}
