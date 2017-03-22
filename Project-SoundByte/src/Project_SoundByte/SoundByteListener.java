package Project_SoundByte;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import static javazoom.jlgui.basicplayer.BasicPlayer.PLAYING;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

/**
 * The player/controller for Project SoundByte.
 *
 * @author Danny Robles and Nicholas Sugimoto
 */
public class SoundByteListener implements BasicPlayerListener
{
    // What time the counter is at the moment
    private static long currentLength;
    // How long the song is
    private static long songLength;
    private static SoundByteListener listener;
    private static PrintStream out = null;
    private static BasicController control;
    private static BasicPlayer player;
    private static String songName = "none";
    protected PropertyChangeSupport change;
    private static boolean repeat = false;
    private static boolean shuffle = false;

    private SoundByteListener()
    {
        player = new BasicPlayer();
        control = (BasicController) player;
        player.addBasicPlayerListener(this);
    }

    /**
     * Play the song in the player.
     *
     * @param name the name of the song
     * @param a if it's being played from open or from the database
     */
    public boolean play(String name, int a)
    {
        try
        {
            if (name.compareToIgnoreCase("none") == 0)
            {
            }
            else if (a == 1)
            {
                songName = name;
                Mp3File file = new Mp3File(songName);
                songLength = file.getLengthInSeconds();
                currentLength = 0;
                control.open(new File(songName));
                control.play();
                change.firePropertyChange("newSong", 1, 2);
            }
            else if (player.getStatus() == BasicPlayer.PAUSED)
            {
                control.resume();
                change.firePropertyChange("resume", 1, 2);
            }
            else if (player.getStatus() == BasicPlayer.STOPPED || player.getStatus() == BasicPlayer.UNKNOWN)
            {
                songName = name;
                Mp3File file = new Mp3File(songName);
                songLength = file.getLengthInSeconds();
                currentLength = 0;
                control.open(new File(songName));
                control.play();
                change.firePropertyChange("newSong", 1, 2);
            }
            else if (player.getStatus() == BasicPlayer.OPENED)
            {
                control.play();

            }
            return true;
        }
        catch (BasicPlayerException ex)
        {
            Logger.getLogger(SoundByteListener.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        catch (IOException | UnsupportedTagException | InvalidDataException ex)
        {
            Logger.getLogger(SoundByteListener.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Pause the player.
     */
    public void pause()
    {
        try
        {
            
            control.pause();
            change.firePropertyChange("pause", 1, 2);
        }
        catch (BasicPlayerException ex)
        {
            Logger.getLogger(SoundByteListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Stop the player.
     */
    public void stop()
    {
        try
        {
            control.stop();
            change.firePropertyChange("pause", 1, 2);
            currentLength = 0;
            songLength = 0;
        }
        catch (BasicPlayerException ex)
        {
            Logger.getLogger(SoundByteListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setRepeat(boolean a){
        repeat = a;
    }
    
    public void setShuffle(boolean a){
        shuffle = a;
    }

    @Override
    public void opened(Object stream, Map properties)
    {
        display("opened : " + properties.toString());
    }

    @Override
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties)
    {
        currentLength = microseconds/1000000;
        display("progress : " + properties.toString());
    }

    @Override
    public void stateUpdated(BasicPlayerEvent event)
    {
        if(event.getCode() == BasicPlayerEvent.EOM){
            
            if(repeat == true){
                play(songName, 1);                                

            }
            else if(shuffle == true){
                change.firePropertyChange("shuffle", 1, 2);

            }
            else{
                change.firePropertyChange("end", 1, 2);
            }
        }
        
        display("stateUpdated : " + event.toString());
    }

    @Override
    public void setController(BasicController controller)
    {
        display("setController : " + controller);
    }

    /**
     * Displays the message if out is not null.
     *
     * @param msg the string to be displayed
     */
    public void display(String msg)
    {
        if (out != null)
        {
            out.println(msg);
        }
    }

    public static SoundByteListener getInstance()
    {
        if (listener == null)
        {
            listener = new SoundByteListener();
        }
        return listener;
    }

    /**
     * Sets the printstream.
     *
     * @param out the new printstream
     */
    public void setOut(PrintStream out)
    {
        SoundByteListener.out = out;
    }

    /**
     * Gets the printstream.
     *
     * @return out
     */
    public PrintStream getOut()
    {
        return out;
    }

    /**
     * Sets the controller.
     *
     * @param control the new controller
     */
    public void setControl(BasicController control)
    {
        SoundByteListener.control = control;
    }

    /**
     * Gets the controller.
     *
     * @return control
     */
    public BasicController getControl()
    {
        return control;
    }

    /**
     * Sets the player.
     *
     * @param player the new player
     */
    public void setPlayer(BasicPlayer player)
    {
        SoundByteListener.player = player;
    }

    /**
     * Gets the player.
     *
     * @return player
     */
    public BasicPlayer getPlayer()
    {
        return player;
    }
    public int getStatus(){
        if(player.getStatus() == PLAYING){
            return 0;
        }
        else{
            return 1;
        }
    }
    public boolean getRepeat(){
        return repeat;
    }
    public boolean getShuffle(){
        return shuffle;
    }

    /**
     * Sets the song name.
     *
     * @param songName the new song name
     */
    public void setSongName(String songName)
    {
        SoundByteListener.songName = songName;
    }

    /**
     * Gets the song name.
     *
     * @return songName
     */
    public String getSongName()
    {
        return songName;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        change = new PropertyChangeSupport(this);
        change.addPropertyChangeListener(listener);
    }


    public long getCurrentTime()
    {
        return currentLength;
    }
    public long getTotalTime()
    {
        return songLength;
    }
}
