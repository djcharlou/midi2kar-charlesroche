import java.math.BigDecimal;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

// 
// Decompiled by Procyon v0.5.36
// 

public class MidiKarProcess implements Receiver
{
    private Ultrastar ultrastar;
    private float lBPM;
    private String trackName;
    private int nTrack;
    private int melodyChannel;
    
    public MidiKarProcess(final Ultrastar ultrastar) {
        this.lBPM = 0.0f;
        this.nTrack = 0;
        this.melodyChannel = -1;
        this.ultrastar = ultrastar;
    }
    
    public void close() {
    }
    
    public void send(final MidiMessage message, final long lTimeStamp) {
        if (message instanceof ShortMessage) {
            final ShortMessage sm = (ShortMessage)message;
            if (sm.getCommand() != 240) {
                final int nChannel = sm.getChannel() + 1;
                if (nChannel == this.melodyChannel && this.ultrastar.getMelodyTrackNumber() == -1) {
                    this.ultrastar.setMelodyTrack(this.nTrack);
                }
            }
        }
        else if (message instanceof MetaMessage) {
            this.decodeMetaMessage((MetaMessage)message);
        }
    }
    
    public void sendUltrastarWords(final Ultrastar ultrastar, final MetaMessage metaMessage, final long lTime, final int lStart) {
        if (metaMessage.getType() == 1 || metaMessage.getType() == 5) {
            final byte[] abData = metaMessage.getData();
            final String strText = new String(abData);
            if (strText.trim().length() > 0 && !strText.startsWith("@LENGL") && !strText.startsWith("@TVtffc")) {
                if (strText.startsWith("@T")) {
                    ultrastar.addEntete(strText.substring(2));
                }
                else {
                    ultrastar.addSyllabe(strText);
                    ultrastar.addStart(String.valueOf(lStart));
                    ultrastar.addTime(String.valueOf(lTime));
                }
            }
        }
    }
    
    public void sendUltrastarMelody(final Ultrastar ultrastar, final ShortMessage sm, final long lTimeMS, final int channel) {
        if (sm.getCommand() == 144 && sm.getChannel() + 1 == channel) {
            ultrastar.addMelody(String.valueOf(lTimeMS), String.valueOf(sm.getData1()));
        }
    }
    
    public void decodeMetaMessage(final MetaMessage message) {
        final byte[] abData = message.getData();
        switch (message.getType()) {
            case 1: {
                this.ultrastar.getWordTrack().put(new Integer(this.nTrack), new Integer(this.nTrack));
                break;
            }
            case 3: {
                final String strTrackName = new String(abData);
                this.trackName = strTrackName;
                break;
            }
            case 5: {
                this.ultrastar.setLyricTrack(this.nTrack);
                break;
            }
            case 81: {
                final int nTempo = (abData[0] & 0xFF) << 16 | (abData[1] & 0xFF) << 8 | (abData[2] & 0xFF);
                float bpm = this.convertTempo((float)nTempo);
                bpm = Math.round(bpm * 100.0f) / 100.0f;
                if (this.lBPM == 0.0f) {
                    this.lBPM = new BigDecimal(bpm).floatValue();
                    break;
                }
                break;
            }
        }
    }
    
    private float convertTempo(float value) {
        if (value <= 0.0f) {
            value = 0.1f;
        }
        return 6.0E7f / value;
    }
    
    public float getLBPM() {
        return this.lBPM;
    }
    
    public String getTrackName() {
        return this.trackName;
    }
    
    public void setNTrack(final int track) {
        this.nTrack = track;
    }
    
    public void setMelodyChannel(final int melodyChannel) {
        this.melodyChannel = melodyChannel;
    }
}
