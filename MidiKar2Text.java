import javax.sound.midi.ShortMessage;
import java.math.BigDecimal;
import javax.sound.midi.MetaMessage;
import java.util.Iterator;
import java.util.Vector;
import java.io.FileWriter;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Track;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.File;

// 
// Decompiled by Procyon v0.5.36
// 

public class MidiKar2Text
{
    private static MidiKarProcess midiKarProcess;
    private static Ultrastar ultrastar;
    private static long nbMicroSecPerTick;
    private static String fileName;
    private static File midiFile;
    private static Sequence sequence;
    private static boolean melodyFirst;
    private static boolean bLyrics;
    
    static {
        MidiKar2Text.midiKarProcess = null;
        MidiKar2Text.ultrastar = null;
        MidiKar2Text.fileName = null;
        MidiKar2Text.midiFile = null;
        MidiKar2Text.sequence = null;
        MidiKar2Text.melodyFirst = true;
        MidiKar2Text.bLyrics = false;
    }
    
    public static void init(final String sFileName, final int melodyChannel) throws Exception {
        MidiKar2Text.ultrastar = new Ultrastar();
        MidiKar2Text.midiKarProcess = new MidiKarProcess(MidiKar2Text.ultrastar);
        MidiKar2Text.midiFile = new File(sFileName);
        try {
            MidiKar2Text.sequence = MidiSystem.getSequence(MidiKar2Text.midiFile);
        }
        catch (InvalidMidiDataException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        catch (IOException e2) {
            System.out.println(e2.getMessage());
            System.exit(1);
        }
        if (MidiKar2Text.sequence == null) {
            System.out.println("Cannot retrieve Sequence.");
        }
        else {
            final long tickLength = MidiKar2Text.sequence.getTickLength();
            final long timeMicroSecLength = MidiKar2Text.sequence.getMicrosecondLength();
            MidiKar2Text.nbMicroSecPerTick = timeMicroSecLength / tickLength;
            final Track[] tracks = MidiKar2Text.sequence.getTracks();
            for (int nTrack = 0; nTrack < tracks.length; ++nTrack) {
                final Track track = tracks[nTrack];
                for (int nEvent = 0; nEvent < track.size(); ++nEvent) {
                    final MidiEvent event = track.get(nEvent);
                    final MidiMessage message = event.getMessage();
                    final long lTicks = event.getTick();
                    MidiKar2Text.midiKarProcess.setNTrack(nTrack);
                    MidiKar2Text.midiKarProcess.setMelodyChannel(melodyChannel);
                    MidiKar2Text.midiKarProcess.send(message, lTicks);
                }
            }
            MidiKar2Text.ultrastar.setLBPM(MidiKar2Text.midiKarProcess.getLBPM());
        }
    }
    
    public static String genererFichier(final String sMidiKarFileName, final String sTextFileName, final int melodyChannel) {
        try {
            init(sMidiKarFileName, melodyChannel);
        }
        catch (Exception exc) {
            System.out.println(exc.getMessage());
            try {
                final FileWriter fw = new FileWriter("error.log");
                fw.write(exc.getCause() + "\n");
                fw.write(String.valueOf(exc.getMessage()) + "\n");
                fw.close();
            }
            catch (Exception exc2) {
                exc2.printStackTrace();
            }
            return "Kar File corrupt...";
        }
        final StringBuffer sb = new StringBuffer("");
        MidiKar2Text.midiKarProcess = new MidiKarProcess(MidiKar2Text.ultrastar);
        if (!buildFile(sMidiKarFileName, MidiKar2Text.ultrastar, melodyChannel)) {
            return "Conversion errors...please see log file";
        }
        int iStartOld = 0;
        String sTitle = MidiKar2Text.fileName;
        String sArtist = MidiKar2Text.fileName;
        String sMP3 = String.valueOf(MidiKar2Text.fileName) + ".mp3";
        if (MidiKar2Text.ultrastar.getEntete().size() == 2) {
            sTitle = MidiKar2Text.ultrastar.getEntete().get(0);
            sArtist = MidiKar2Text.ultrastar.getEntete().get(1);
            sMP3 = String.valueOf(sArtist) + " - " + sTitle + ".mp3";
        }
        else if (MidiKar2Text.midiKarProcess.getTrackName() != null) {
            sTitle = MidiKar2Text.midiKarProcess.getTrackName();
            sArtist = MidiKar2Text.midiKarProcess.getTrackName();
            sMP3 = String.valueOf(sArtist) + " - " + sTitle + ".mp3";
        }
        sb.append("#TITLE:" + sTitle + "\n");
        sb.append("#ARTIST:" + sArtist + "\n");
        sb.append("#MP3:" + sMP3 + "\n");
        sb.append("#COVER:cover.jpg\n");
        sb.append("#BACKGROUND:background.jpg\n");
        sb.append("#BPM:" + String.valueOf(MidiKar2Text.ultrastar.getLBPM()).replaceAll("[.]", ",") + "\n");
        sb.append("#GAP:0\n");
        String sLigne = "";
        boolean bFirstCoupure = true;
        boolean bForceDuree = false;
        for (int idx = 0; idx < MidiKar2Text.ultrastar.getWords().size(); ++idx) {
            String sSyllabe = MidiKar2Text.ultrastar.getWords().get(idx);
            sSyllabe = sSyllabe.replaceAll("[.]", "");
            String sStart = MidiKar2Text.ultrastar.getStart().get(idx);
            final String sStartOld = String.valueOf(iStartOld);
            final int iStart = Integer.parseInt(sStart);
            if (bForceDuree) {
                ++iStartOld;
                bForceDuree = false;
            }
            int iDuree = iStart - iStartOld;
            if (iDuree == 0) {
                iDuree = 1;
                sStart = String.valueOf(iStart + 1);
                bForceDuree = true;
            }
            if (sSyllabe.startsWith("/") || sSyllabe.startsWith("\\")) {
                sSyllabe = sSyllabe.substring(1);
                if (sSyllabe.trim().length() > 0) {
                    if (!bFirstCoupure) {
                        if (iDuree > 4) {
                            iDuree = 4;
                        }
                        final int iCoupureMin = Integer.parseInt(sStartOld) + iDuree;
                        int iCoupure = (Integer.parseInt(sStartOld) + Integer.parseInt(sStart)) / 2;
                        if (iCoupure < iCoupureMin) {
                            iCoupure = iCoupureMin;
                        }
                        if (!sLigne.equals("")) {
                            sLigne = sLigne.replaceAll("[$]", String.valueOf(iDuree));
                            sb.append(String.valueOf(sLigne) + "\n");
                        }
                        sb.append("- " + iCoupure + "\n");
                    }
                    bFirstCoupure = false;
                }
                iStartOld = iStart;
            }
            else if (!sLigne.equals("")) {
                sLigne = sLigne.replaceAll("[$]", String.valueOf(iDuree));
                sb.append(String.valueOf(sLigne) + "\n");
                iStartOld = iStart;
            }
            if (sSyllabe.trim().length() > 0) {
                String sIntonation = "%";
                final String time = MidiKar2Text.ultrastar.getTime().get(idx);
                String melody = MidiKar2Text.ultrastar.getMelody(time, MidiKar2Text.melodyFirst);
                if (melody == null) {
                    melody = MidiKar2Text.ultrastar.getMelody(time, !MidiKar2Text.melodyFirst);
                }
                if (melody != null) {
                    sIntonation = melody;
                }
                sSyllabe = sSyllabe.replaceAll("\u00e8", "e");
                sSyllabe = sSyllabe.replaceAll("\u00ea", "e");
                sSyllabe = sSyllabe.replaceAll("\u00e0", "a");
                sSyllabe = sSyllabe.replaceAll("\u00e7", "c");
                sSyllabe = sSyllabe.replaceAll("\u00f9", "u");
                sSyllabe = sSyllabe.replaceAll("\u00fb", "u");
                sSyllabe = sSyllabe.replaceAll("\u0153", "oe");
                if (!sIntonation.equals("%")) {
                    final int iIntonation = Integer.parseInt(sIntonation) - 60;
                    sLigne = ": " + sStart + " $ " + iIntonation + " " + sSyllabe;
                }
                else {
                    sLigne = ": " + sStart + " 1 " + sIntonation + " " + sSyllabe;
                }
            }
            else {
                if (sLigne.indexOf("$") > 0) {
                    sLigne = sLigne.replaceAll("[$]", String.valueOf(iDuree));
                    sb.append(String.valueOf(sLigne) + "\n");
                    iStartOld = iStart;
                }
                sLigne = "";
            }
        }
        if (sLigne != "") {
            sLigne = sLigne.replaceAll("[$]", "2");
            sb.append(String.valueOf(sLigne) + "\n");
        }
        sb.append("E");
        try {
            final FileWriter fw2 = new FileWriter(sTextFileName);
            fw2.write(sb.toString());
            fw2.close();
        }
        catch (Exception exc3) {
            exc3.printStackTrace();
        }
        return "Export OK !";
    }
    
    public static boolean buildFile(final String strFilename, final Ultrastar ultrastar, final int melodyChannel) {
        try {
            if (MidiKar2Text.sequence != null) {
                ultrastar.setDuration(MidiKar2Text.sequence.getMicrosecondLength() / 1000L);
                final long tickLength = MidiKar2Text.sequence.getTickLength();
                final long timeMicroSecLength = MidiKar2Text.sequence.getMicrosecondLength();
                MidiKar2Text.nbMicroSecPerTick = timeMicroSecLength / tickLength;
                final Track[] tracks = MidiKar2Text.sequence.getTracks();
                Track track = null;
                final Vector vSyllabe = new Vector();
                final Vector vStart = new Vector();
                final Vector vTime = new Vector();
                for (final Integer iTrack : ultrastar.getWordTrack().keySet()) {
                    track = tracks[iTrack];
                    if (track != null) {
                        for (int nEvent = 0; nEvent < track.size(); ++nEvent) {
                            final MidiEvent event = track.get(nEvent);
                            buildUltrastarWords(event, ultrastar);
                        }
                    }
                    vSyllabe.add(ultrastar.getWords());
                    ultrastar.setWords(new Vector());
                    vStart.add(ultrastar.getStart());
                    ultrastar.setStart(new Vector());
                    vTime.add(ultrastar.getTime());
                    ultrastar.setStart(new Vector());
                }
                if (MidiKar2Text.bLyrics || vSyllabe.size() == 0) {
                    track = ultrastar.getLyricTrack(tracks);
                    if (track != null) {
                        for (int nEvent2 = 0; nEvent2 < track.size(); ++nEvent2) {
                            final MidiEvent event2 = track.get(nEvent2);
                            buildUltrastarWords(event2, ultrastar);
                        }
                        vSyllabe.add(ultrastar.getWords());
                        ultrastar.setWords(new Vector());
                        vStart.add(ultrastar.getStart());
                        ultrastar.setStart(new Vector());
                        vTime.add(ultrastar.getTime());
                        ultrastar.setStart(new Vector());
                    }
                }
                int iNb = 0;
                int iVector = 0;
                for (int idx = 0; idx < vSyllabe.size(); ++idx) {
                    final Vector vWords = vSyllabe.get(idx);
                    if (vWords.size() > iNb) {
                        if (vWords.size() < iNb + 50) {
                            final String langue = vWords.get(0);
                            if (langue.indexOf("LFren") != -1) {
                                iNb = vWords.size();
                                iVector = idx;
                            }
                        }
                        else {
                            iNb = vWords.size();
                            iVector = idx;
                        }
                    }
                }
                if (vSyllabe.size() <= 0) {
                    System.out.println("La piste contenant les paroles n'a pas \u00e9t\u00e9 trouv\u00e9");
                    try {
                        final FileWriter fw = new FileWriter("error.log", false);
                        fw.write("Lyrics's track not found...\n");
                        fw.close();
                    }
                    catch (Exception exc) {
                        exc.printStackTrace();
                    }
                    return false;
                }
                ultrastar.setWords(vSyllabe.get(iVector));
                ultrastar.setStart(vStart.get(iVector));
                ultrastar.setTime(vTime.get(iVector));
                track = ultrastar.getMelodyTrack(tracks);
                if (track == null) {
                    System.out.println("La piste contenant la m\u00e9lodie (channel " + melodyChannel + ") n'a pas \u00e9t\u00e9 trouv\u00e9");
                    try {
                        final FileWriter fw = new FileWriter("error.log", false);
                        fw.write("Melody's track not found...\n");
                        fw.close();
                    }
                    catch (Exception exc) {
                        exc.printStackTrace();
                    }
                    return false;
                }
                for (int nEvent = 0; nEvent < track.size(); ++nEvent) {
                    final MidiEvent event = track.get(nEvent);
                    buildUltrastarMelody(event, ultrastar, melodyChannel);
                }
            }
        }
        catch (Exception exc2) {
            System.out.println(exc2.getMessage());
            try {
                final FileWriter fw2 = new FileWriter("error.log");
                fw2.write(exc2.getCause() + "\n");
                fw2.write(String.valueOf(exc2.getMessage()) + "\n");
                fw2.close();
            }
            catch (Exception exc3) {
                exc3.printStackTrace();
            }
            return false;
        }
        return true;
    }
    
    public static void buildUltrastarWords(final MidiEvent event, final Ultrastar ultrastar) {
        final MidiMessage message = event.getMessage();
        if (message instanceof MetaMessage) {
            final MetaMessage metaMessage = (MetaMessage)message;
            final long lTicks = event.getTick();
            final long lTimeMSec = lTicks * MidiKar2Text.nbMicroSecPerTick / 1000L;
            final long lStart = new BigDecimal(lTimeMSec / 60L * 4L * ultrastar.getLBPM()).longValue();
            MidiKar2Text.midiKarProcess.sendUltrastarWords(ultrastar, metaMessage, lTimeMSec, new BigDecimal(lStart / 1000L).intValue());
        }
    }
    
    public static void buildUltrastarMelody(final MidiEvent event, final Ultrastar ultrastar, final int channel) {
        final MidiMessage message = event.getMessage();
        if (message instanceof ShortMessage) {
            final ShortMessage sm = (ShortMessage)message;
            final long lTicks = event.getTick();
            final long lTimeMSec = lTicks * MidiKar2Text.nbMicroSecPerTick / 1000L;
            MidiKar2Text.midiKarProcess.sendUltrastarMelody(ultrastar, sm, lTimeMSec, channel);
        }
    }
}
