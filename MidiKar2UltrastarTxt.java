import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;

// 
// Decompiled by Procyon v0.5.36
// 

public class MidiKar2UltrastarTxt
{
    private static String fileNamePath;
    private static String defaultExportFolder;
    private static String fileName;
    private static String message;
    private static Label lblKarFileName;
    private static Text txtKarFileName;
    private static Button open;
    private static Label lblNomFichier;
    private static Text txtNomRepertoire;
    private static Button btnOuvrir;
    private static Label lblNumMelodyTrack;
    private static Text txtNumMelodyTrack;
    private static Label lblEmpty;
    private static Button btnConvert;
    private static Button btnClose;
    private static Label lblMessage;
    
    static {
        MidiKar2UltrastarTxt.fileNamePath = "";
        MidiKar2UltrastarTxt.defaultExportFolder = "";
        MidiKar2UltrastarTxt.fileName = "";
        MidiKar2UltrastarTxt.message = "";
    }
    
    public static void main(final String[] args) {
        final Display display = new Display();
        final Shell shell = new Shell(display, 2144);
        shell.setText("MidiKar2UltrastarTxt");
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        shell.setLayout((Layout)gridLayout);
        (MidiKar2UltrastarTxt.lblKarFileName = new Label((Composite)shell, 0)).setText("Karaoke File Name : ");
        (MidiKar2UltrastarTxt.txtKarFileName = new Text((Composite)shell, 2048)).setSize(200, 10);
        (MidiKar2UltrastarTxt.open = new Button((Composite)shell, 8)).setText("Open...");
        MidiKar2UltrastarTxt.open.addSelectionListener((SelectionListener)new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                getFile(shell);
            }
        });
        GridData data = new GridData();
        data.widthHint = 200;
        MidiKar2UltrastarTxt.lblKarFileName.setLayoutData((Object)data);
        data = new GridData();
        data.widthHint = 420;
        MidiKar2UltrastarTxt.txtKarFileName.setLayoutData((Object)data);
        data = new GridData();
        data.widthHint = 80;
        MidiKar2UltrastarTxt.open.setLayoutData((Object)data);
        (MidiKar2UltrastarTxt.lblNomFichier = new Label((Composite)shell, 0)).setText("Ultrastar TXT File Export's Folder : ");
        (MidiKar2UltrastarTxt.txtNomRepertoire = new Text((Composite)shell, 2056)).setText("");
        MidiKar2UltrastarTxt.txtNomRepertoire.setSize(200, 10);
        (MidiKar2UltrastarTxt.btnOuvrir = new Button((Composite)shell, 8)).setText("Choose dir...");
        MidiKar2UltrastarTxt.btnOuvrir.addListener(13, (Listener)new Listener() {
            public void handleEvent(final Event e) {
                getDirectory(shell);
            }
        });
        data = new GridData();
        data.widthHint = 200;
        MidiKar2UltrastarTxt.lblNomFichier.setLayoutData((Object)data);
        data = new GridData();
        data.widthHint = 420;
        MidiKar2UltrastarTxt.txtNomRepertoire.setLayoutData((Object)data);
        data = new GridData();
        data.widthHint = 80;
        MidiKar2UltrastarTxt.btnOuvrir.setLayoutData((Object)data);
        (MidiKar2UltrastarTxt.lblNumMelodyTrack = new Label((Composite)shell, 0)).setText("Melody Track Number : ");
        MidiKar2UltrastarTxt.txtNumMelodyTrack = new Text((Composite)shell, 2048);
        data = new GridData();
        data.widthHint = 200;
        MidiKar2UltrastarTxt.lblNumMelodyTrack.setLayoutData((Object)data);
        data = new GridData();
        data.widthHint = 20;
        data.horizontalSpan = 2;
        MidiKar2UltrastarTxt.txtNumMelodyTrack.setLayoutData((Object)data);
        MidiKar2UltrastarTxt.lblEmpty = new Label((Composite)shell, 0);
        data = new GridData();
        data.horizontalSpan = 3;
        data.horizontalAlignment = 16777216;
        MidiKar2UltrastarTxt.lblEmpty.setLayoutData((Object)data);
        (MidiKar2UltrastarTxt.btnConvert = new Button((Composite)shell, 8)).setText("Convert");
        MidiKar2UltrastarTxt.btnConvert.addListener(13, (Listener)new Listener() {
            public void handleEvent(final Event e) {
                final String karName = MidiKar2UltrastarTxt.txtKarFileName.getText();
                String txtName = MidiKar2UltrastarTxt.txtNomRepertoire.getText();
                txtName = String.valueOf(txtName) + "\\" + MidiKar2UltrastarTxt.fileName.substring(0, MidiKar2UltrastarTxt.fileName.length() - 3) + "txt";
                int melodyTrack = -1;
                if (MidiKar2UltrastarTxt.txtNumMelodyTrack.getText() != "") {
                    melodyTrack = Integer.parseInt(MidiKar2UltrastarTxt.txtNumMelodyTrack.getText());
                }
                if (karName != "" && txtName != "") {
                    MidiKar2UltrastarTxt.lblMessage.setText("Convert in progress...");
                    MidiKar2UltrastarTxt.access$7(MidiKar2Text.genererFichier(karName, txtName, melodyTrack));
                    MidiKar2UltrastarTxt.lblMessage.setText(MidiKar2UltrastarTxt.message);
                }
            }
        });
        data = new GridData();
        data.widthHint = 80;
        data.horizontalSpan = 2;
        data.horizontalAlignment = 16777216;
        MidiKar2UltrastarTxt.btnConvert.setLayoutData((Object)data);
        (MidiKar2UltrastarTxt.btnClose = new Button((Composite)shell, 8)).setText("Close");
        MidiKar2UltrastarTxt.btnClose.addListener(13, (Listener)new Listener() {
            public void handleEvent(final Event e) {
                shell.close();
            }
        });
        data = new GridData();
        data.widthHint = 80;
        data.horizontalAlignment = 131072;
        MidiKar2UltrastarTxt.btnClose.setLayoutData((Object)data);
        MidiKar2UltrastarTxt.lblEmpty = new Label((Composite)shell, 0);
        data = new GridData();
        data.horizontalSpan = 3;
        data.horizontalAlignment = 16777216;
        MidiKar2UltrastarTxt.lblEmpty.setLayoutData((Object)data);
        (MidiKar2UltrastarTxt.lblMessage = new Label((Composite)shell, 0)).setText("Choose the kar, the export's folder, put the melody's track number and click the convert button...");
        data = new GridData();
        data.horizontalSpan = 3;
        data.horizontalAlignment = 16777216;
        MidiKar2UltrastarTxt.lblMessage.setLayoutData((Object)data);
        MidiKar2UltrastarTxt.lblEmpty = new Label((Composite)shell, 0);
        data = new GridData();
        data.horizontalSpan = 3;
        data.horizontalAlignment = 16777216;
        MidiKar2UltrastarTxt.lblEmpty.setLayoutData((Object)data);
        (MidiKar2UltrastarTxt.lblMessage = new Label((Composite)shell, 16384)).setText("Copyright Sonyfan / Ultrastar-fr : http://ultrastar.tuxfamily.org  ");
        data = new GridData();
        data.horizontalSpan = 3;
        data.horizontalAlignment = 16777216;
        MidiKar2UltrastarTxt.lblMessage.setLayoutData((Object)data);
        shell.pack();
        centerOnScreen(display, shell);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
    
    private static void getFile(final Shell shell) {
        final FileDialog dialog = new FileDialog(shell, 4096);
        dialog.setFilterNames(new String[] { "Karaoke Files", "Midi Files" });
        dialog.setFilterExtensions(new String[] { "*.kar", "*.mid" });
        dialog.setFilterPath("c:\\");
        MidiKar2UltrastarTxt.fileNamePath = dialog.open();
        if (MidiKar2UltrastarTxt.fileNamePath == null) {
            MidiKar2UltrastarTxt.fileNamePath = "";
        }
        else {
            MidiKar2UltrastarTxt.fileName = dialog.getFileName();
        }
        MidiKar2UltrastarTxt.defaultExportFolder = MidiKar2UltrastarTxt.fileNamePath.substring(0, MidiKar2UltrastarTxt.fileNamePath.length() - MidiKar2UltrastarTxt.fileName.length());
        MidiKar2UltrastarTxt.txtKarFileName.setText(MidiKar2UltrastarTxt.fileNamePath);
        MidiKar2UltrastarTxt.txtNomRepertoire.setText(MidiKar2UltrastarTxt.defaultExportFolder);
    }
    
    private static void getDirectory(final Shell shell) {
        final DirectoryDialog dialog = new DirectoryDialog(shell, 4096);
        dialog.setFilterPath(MidiKar2UltrastarTxt.defaultExportFolder = MidiKar2UltrastarTxt.fileNamePath.replaceAll(MidiKar2UltrastarTxt.fileName, ""));
        dialog.setMessage("Choose the txt export's folder");
        String nomRepertoire = dialog.open();
        if (nomRepertoire == null) {
            nomRepertoire = "";
        }
        MidiKar2UltrastarTxt.txtNomRepertoire.setText(nomRepertoire);
    }
    
    private static void centerOnScreen(final Display display, final Shell shell) {
        final Rectangle rect = display.getClientArea();
        final Point size = shell.getSize();
        final int x = (rect.width - size.x) / 2;
        final int y = (rect.height - size.y) / 2;
        shell.setLocation(new Point(x, y));
    }
    
    static /* synthetic */ void access$7(final String message) {
        MidiKar2UltrastarTxt.message = message;
    }
}
