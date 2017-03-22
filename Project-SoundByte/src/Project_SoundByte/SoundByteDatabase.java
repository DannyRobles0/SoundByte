package Project_SoundByte;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class SoundByteDatabase extends JTable
{

    private static SoundByteDatabase database;
    private static SoundByteListener listen;
    private static SoundByteModel model;
    private static SoundByteModel playlistModel;
    private static final String JDBC_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    // After creating the database and connecting to it, it's URL should reflect it's name
    private static final String DB_URL = "jdbc:derby://localhost:1527/Project_SoundByte";
    private static Connection con = null;
    private static Statement stmt = null;
    private static boolean isPlaylist;
    private static String sql;
    private static String file;
    private static String currentPlaylist;
    private static String[][] strings;
    private static String[][] playlistSongs;
    private static String[][] newWindowSongs;
    protected PropertyChangeSupport change;
    protected PropertyChangeSupport change2;
    private static final String[] column =
    {
        "Title", "Artist", "Album",
        "Genre", "Length", "Location", "Comment"
    };
    private static JTree tree;
    private static DefaultMutableTreeNode root;
    private static DefaultMutableTreeNode library;
    private static DefaultMutableTreeNode playlists;
    private static DefaultTreeModel defaultTreeModel;

    private SoundByteDatabase() throws ClassNotFoundException, SQLException
    {
        calcDB();
        calcTree();
        listen = SoundByteListener.getInstance();
        model = new SoundByteModel(strings, column);
        playlistModel = new SoundByteModel(playlistSongs, column);
        setModel(model);
        currentPlaylist = "Library";
        PropertyChangeListener pcl = new PropertyChangeListener()
        {

            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateButton(evt.getPropertyName());
                
            }
        };
        listen.addPropertyChangeListener(pcl);
    }

    /**
     * Loads the database elements into string[][] array.
     */
    public void calcDB() throws ClassNotFoundException, SQLException
    {
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        //Count elements in database
        sql = "Select * from songs";
        ResultSet rs = stmt.executeQuery(sql);
        int size = 0;
        while (rs.next())
        {
            size++;
        }
        strings = new String[size][7];
        rs.beforeFirst();

        //load Titles into table
        int iter = 0;
        while (rs.next())
        {
            strings[iter][0] = rs.getString("Title");
            strings[iter][1] = rs.getString("Artist");
            strings[iter][2] = rs.getString("Album");
            strings[iter][3] = rs.getString("genre");
            strings[iter][4] = rs.getString("song_length");
            strings[iter][5] = rs.getString("file_location");
            strings[iter][6] = rs.getString("comment");
            iter++;
        }
        rs.close();

    }

    public String[][] newWindowSongs(String n) throws ClassNotFoundException, SQLException
    {
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        //Count elements in database
        sql = "select * from songs join playlistSongs on songs.FILE_LOCATION"
                + "= PLAYLISTSONGS.FILE_LOCATION where playlistsongs.PNAME = '"
                + n + "'";
        ResultSet rs = stmt.executeQuery(sql);
        int size = 0;
        while (rs.next())
        {
            size++;
        }
        newWindowSongs = new String[size][7];
        rs.beforeFirst();

        //load Titles into table
        int iter = 0;
        while (rs.next())
        {
            newWindowSongs[iter][0] = rs.getString("Title");
            newWindowSongs[iter][1] = rs.getString("Artist");
            newWindowSongs[iter][2] = rs.getString("Album");
            newWindowSongs[iter][3] = rs.getString("genre");
            newWindowSongs[iter][4] = rs.getString("song_length");
            newWindowSongs[iter][5] = rs.getString("file_location");
            newWindowSongs[iter][6] = rs.getString("comment");
            iter++;
        }
        rs.close();
        return newWindowSongs;
    }

    public void calcPlaylist(String n) throws ClassNotFoundException, SQLException
    {
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        //Count elements in database
        sql = "select * from songs join playlistSongs on songs.FILE_LOCATION"
                + "= PLAYLISTSONGS.FILE_LOCATION where playlistsongs.PNAME = '"
                + n + "'";
        ResultSet rs = stmt.executeQuery(sql);
        int size = 0;
        while (rs.next())
        {
            size++;
        }
        playlistSongs = new String[size][7];
        rs.beforeFirst();

        //load Titles into table
        int iter = 0;
        while (rs.next())
        {
            playlistSongs[iter][0] = rs.getString("Title");
            playlistSongs[iter][1] = rs.getString("Artist");
            playlistSongs[iter][2] = rs.getString("Album");
            playlistSongs[iter][3] = rs.getString("genre");
            playlistSongs[iter][4] = rs.getString("song_length");
            playlistSongs[iter][5] = rs.getString("file_location");
            playlistSongs[iter][6] = rs.getString("comment");
            iter++;
        }
        rs.close();
    }

    public void setCurrentPlaylist(String n)
    {
        currentPlaylist = n;
    }

    public void addSongFromNewWindow(String song) throws IOException, UnsupportedTagException,
            InvalidDataException, SQLException, ClassNotFoundException
    {
        Mp3File mp3 = new Mp3File(song);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        if (mp3.hasId3v2Tag() == true)
        {
            ID3v2 id3 = mp3.getId3v2Tag();
            int time = (int) mp3.getLengthInSeconds();
            int min = time / 60;
            int sec = time % 60;
            String minSec = min + ":" + sec;
            if (sec < 10)
            {
                minSec = min + ":0" + sec;
            }
            sql = "Insert into songs values('"
                    + fixApostrophe(dispNull(id3.getTitle()))
                    + "','" + fixApostrophe(dispNull(id3.getArtist())) + "','"
                    + fixApostrophe(dispNull(id3.getAlbum())) + "','"
                    + fixApostrophe(genreString(id3.getGenre())) + "','" + minSec + "','"
                    + fixApostrophe(song) + "','" 
                    + fixApostrophe(dispNull(id3.getComment()))+"')";
            stmt.executeUpdate(sql);
            calcDB();
            model.addList(strings);
            setModel(model);
            change.firePropertyChange(currentPlaylist, 1, 2);

            if (model.getRowCount() < 1)
            {
                clearSelection();
                file = "none";
            }
            else
            {
                int row = model.getRow(song);
                file = (String) model.getValueAt(row, 5);
            }
        }
        else
        {
            sql = "Insert into songs values('N/A', 'N/A', 'N/A'"
                    + ", 'N/A', 'N/A', '" + song + "', 'N/A')";
            stmt.executeUpdate(sql);
            calcDB();
            model.addList(strings);
            change.firePropertyChange(currentPlaylist, 1, 2);
            if (model.getRowCount() < 1)
            {
                clearSelection();
            }
            else
            {
                int row = model.getRow(song);
                file = (String) model.getValueAt(row, 5);
            }
        }

    }

    // Adds song to database with ID3v2 tags
    public void addDB(String song) throws IOException, UnsupportedTagException,
            InvalidDataException, SQLException, ClassNotFoundException
    {
        if (isPlaylist == false)
        {
            Mp3File mp3 = new Mp3File(song);
            con = DriverManager.getConnection(DB_URL);
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            if (mp3.hasId3v2Tag() == true)
            {
                ID3v2 id3 = mp3.getId3v2Tag();
                int time = (int) mp3.getLengthInSeconds();
                int min = time / 60;
                int sec = time % 60;
                String minSec = min + ":" + sec;
                if (sec < 10)
                {
                    minSec = min + ":0" + sec;
                }
                sql = "Insert into songs values('"
                        + fixApostrophe(dispNull(id3.getTitle()))
                        + "','" + fixApostrophe(dispNull(id3.getArtist())) + "','"
                        + fixApostrophe(dispNull(id3.getAlbum())) + "','"
                        + fixApostrophe(genreString(id3.getGenre())) + "','" + minSec + "','"
                        + fixApostrophe(song) +"','" 
                        + fixApostrophe(dispNull(id3.getComment()))+ "')";
                stmt.executeUpdate(sql);
                calcDB();
                model.addList(strings);
                setModel(model);
                if (model.getRowCount() < 1)
                {
                    clearSelection();
                    file = "none";
                }
                else
                {
                    int row = model.getRow(song);
                    setRowSelectionInterval(row, 0);
                    file = (String) model.getValueAt(row, 5);
                }
            }
            else
            {
                sql = "Insert into songs values('N/A', 'N/A', 'N/A'"
                        + ", 'N/A', 'N/A', '" + song + "', 'N/A')";
                stmt.executeUpdate(sql);
                calcDB();
                model.addList(strings);
                if (model.getRowCount() < 1)
                {
                    clearSelection();
                }
                else
                {
                    int row = model.getRow(song);
                    setRowSelectionInterval(row, 0);
                    file = (String) model.getValueAt(row, 5);
                }
            }
        }
        else
        {
            addToPlaylist(song, currentPlaylist);
        }
    }

    public int countPlaylists() throws ClassNotFoundException, SQLException
    {
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "Select * from playlists";
        ResultSet rs = stmt.executeQuery(sql);
        int a = 0;
        while (rs.next())
        {
            a++;
        }
        return a;
    }
    
        public int countRecent() throws ClassNotFoundException, SQLException
    {
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "Select * from recent";
        ResultSet rs = stmt.executeQuery(sql);
        int a = 0;
        while (rs.next())
        {
            a++;
        }
        if(a >10){a = 10;}
        rs.close();
        return a;
    }

    public int countSongs(String n) throws ClassNotFoundException, SQLException
    {
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "Select * from playlistsongs where pname = '" + n + "'";
        ResultSet rs = stmt.executeQuery(sql);
        int a = 0;
        while (rs.next())
        {
            a++;
        }
        rs.close();
        return a;
    }

    public String[] playlistNames() throws ClassNotFoundException, SQLException
    {
        String[] a = new String[countPlaylists()];
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "Select * from playlists";
        ResultSet rs = stmt.executeQuery(sql);
        int b = 0;
        while (rs.next())
        {
            a[b] = rs.getString("pName");
            b++;
        }
        rs.close();
        return a;

    }
    public String[] recentNames(int z) throws ClassNotFoundException, SQLException
    {
        String[] a = new String[z];
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "Select * from recent order by datetime desc "
                + "fetch first " + z + " rows only";
        ResultSet rs = stmt.executeQuery(sql);
        int b = 0;
        while (rs.next())
        {
            a[b] = rs.getString("title");
            b++;
        }
        rs.next();
        return a;

    }
    
    public String getRecent(int z) throws ClassNotFoundException, SQLException{
        String a = new String();
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "Select * from recent order by datetime desc offset " + z + " rows fetch next row only";
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            a = rs.getString("file_location");
        }
        rs.close();
        return a;
    }
    
    public void addToRecent(String z) throws ClassNotFoundException, SQLException{
        String a = new String();
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "Select * from songs where file_location = '" + fixApostrophe(z) + "'";
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            a = rs.getString("title");
        }
        sql = "insert into recent values('" + fixApostrophe(a) 
                + "','" +fixApostrophe(z) +"', current_timestamp)";
        rs.close();
        stmt.execute(sql);
        change.firePropertyChange("recent", 1, 2);
    }

    public void addToNewWindowPlaylist(String song, String playlist) throws
            ClassNotFoundException, SQLException, IOException,
            UnsupportedTagException, InvalidDataException
    {
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "Select * from songs where File_Location = '" + fixApostrophe(song) + "'";
        ResultSet rs = stmt.executeQuery(sql);
        if (!rs.next())
        {
            addSongFromNewWindow(song);
        }
        rs.close();
        sql = "insert into playlistsongs values ('" + playlist + "','"
                + fixApostrophe(song) + "')";
        stmt.execute(sql);
        if (playlist.compareToIgnoreCase(currentPlaylist) == 0)
        {
            calcPlaylist(currentPlaylist);
            playlistModel.addList(playlistSongs);
        }
        change.firePropertyChange(currentPlaylist, 1, 2);
    }

    public void addToPlaylist(String song, String playlist) throws
            ClassNotFoundException, SQLException, IOException,
            UnsupportedTagException, InvalidDataException
    {
        currentPlaylist = playlist;
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "Select * from songs where File_Location = '" + fixApostrophe(song) + "'";
        ResultSet rs = stmt.executeQuery(sql);
        if (!rs.next())
        {
            isPlaylist = false;
            addDB(song);
            isPlaylist = true;
        }
        rs.close();
        sql = "insert into playlistsongs values ('" + playlist + "','"
                + fixApostrophe(song) + "')";
        stmt.execute(sql);
        calcPlaylist(currentPlaylist);
        playlistModel.addList(playlistSongs);
        change.firePropertyChange(currentPlaylist, 1, 2);

    }

    public boolean isLibraryEmpty() throws ClassNotFoundException, SQLException
    {
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "Select * from songs";
        ResultSet rs = stmt.executeQuery(sql);
        if (!rs.next())
        {
            rs.close();
            return true;
        }
        else
        {   rs.close();
            return false;
        }
        
    }

    public void delFromPlaylist(String song) throws ClassNotFoundException, SQLException
    {
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "Delete from playlistSongs where file_location = '" + fixApostrophe(song) + "'";
        stmt.execute(sql);
        calcPlaylist(currentPlaylist);
        playlistModel.addList(playlistSongs);
        change.firePropertyChange(currentPlaylist, 1, 2);
    }

    public void delFromNewWindowPlaylist(String song, String Playlist) throws ClassNotFoundException, SQLException
    {
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "Delete from playlistSongs where file_location = '" + fixApostrophe(song) + "'";
        stmt.execute(sql);
        if (Playlist.compareToIgnoreCase(currentPlaylist) == 0)
        {
            calcPlaylist(currentPlaylist);
            playlistModel.addList(playlistSongs);
        }

        change.firePropertyChange(currentPlaylist, 1, 2);
    }

    /**
     * Deletes a song from the database.
     */
    public void delDB(String song) throws ClassNotFoundException, SQLException
    {
        if (isPlaylist == false)
        {
            file = song;
            Class.forName(JDBC_DRIVER);
            con = DriverManager.getConnection(DB_URL);
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            sql = "Select * from playlistSongs where file_location = '" + fixApostrophe(song) + "'";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
            {
                delFromPlaylist(song);
            }
            rs.close();
            sql = "Delete from Songs where file_location = '"
                    + fixApostrophe(file)
                    + "'";
            if (listen.getSongName().compareTo(file) == 0)
            {
                listen.stop();
            }
            stmt.execute(sql);
            calcDB();
            model.addList(strings);
            change.firePropertyChange(currentPlaylist, 1, 2);
            if (model.getRowCount() < 1)
            {
                clearSelection();
                file = "none";
            }
            else
            {
                setRowSelectionInterval(0, 0);
                file = (String) model.getValueAt(0, 5);
            }
        }
        else if (isPlaylist == true)
        {
            delFromPlaylist(song);
            change.firePropertyChange(currentPlaylist, 1, 2);

        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        change = new PropertyChangeSupport(this);
        change.addPropertyChangeListener(listener);
    }
        public void addPropertyChangeListener(PropertyChangeListener listener, int a)
    {
        change2 = new PropertyChangeSupport(this);
        change2.addPropertyChangeListener(listener);
    }

    public void calcTree() throws ClassNotFoundException, SQLException
    {

        tree = new JTree();
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        root = new DefaultMutableTreeNode("Contents");
        library = new DefaultMutableTreeNode("Library");
        playlists = new DefaultMutableTreeNode("Playlists");
        defaultTreeModel = new DefaultTreeModel(root);

        root.add(library);
        root.add(playlists);
        tree.setModel(defaultTreeModel);
        tree.setRootVisible(false);

        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "Select * from playlists";
        ResultSet rs = stmt.executeQuery(sql);
        String s;
        while (rs.next())
        {
            s = rs.getString("pName");
            playlists.add(new DefaultMutableTreeNode(s));
        }
        rs.close();
    }

    public JTree getTree()
    {
        return tree;
    }
    
    public boolean getIsPlaylist(){
        return isPlaylist;
    }

    private TreePath find(DefaultMutableTreeNode root, String s)
    {
        @SuppressWarnings("unchecked")
        Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
        while (e.hasMoreElements())
        {
            DefaultMutableTreeNode node = e.nextElement();
            if (node.toString().equalsIgnoreCase(s))
            {
                return new TreePath(node.getPath());
            }
        }
        return null;
    }

    public void createPlaylist(String n) throws ClassNotFoundException, SQLException
    {
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "Insert into playlists values('" + fixApostrophe(n) + "')";
        stmt.execute(sql);
        defaultTreeModel.insertNodeInto(new DefaultMutableTreeNode(n), playlists, playlists.getChildCount());
        calcPlaylist(n);
        playlistModel.addList(playlistSongs);
        setModel(playlistModel);
        currentPlaylist = n;
        isPlaylist = true;
        TreePath path = find(playlists, n);
        tree.setSelectionPath(path);

    }


    
    public void delPlaylist(String n) throws ClassNotFoundException, SQLException
    {

        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "Delete from playlistSongs where pname = '" + n + "'";
        stmt.execute(sql);
        sql = "Delete from playlists where pname = '" + n + "'";
        stmt.execute(sql);
        calcPlaylist(n);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        defaultTreeModel.removeNodeFromParent(node);
        if (currentPlaylist.compareToIgnoreCase(n) == 0)
        {
            calcDB();
            model.addList(strings);
            setModel(model);
            isPlaylist = false;
        }
        change.firePropertyChange(currentPlaylist, 1, 2);
    }

    public void choosePlaylist(String n) throws ClassNotFoundException, SQLException
    {
        if (n.compareToIgnoreCase("library") != 0 && n.compareToIgnoreCase("playlists") != 0)
        {
            currentPlaylist = n;
            Class.forName(JDBC_DRIVER);
            con = DriverManager.getConnection(DB_URL);
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            sql = "select * from songs join playlistSongs on songs.FILE_LOCATION"
                    + " = PLAYLISTSONGS.FILE_LOCATION where playlistsongs.PNAME"
                    + " = '" + n + "'";
            ResultSet rs = stmt.executeQuery(sql);
            int size = 0;
            while (rs.next())
            {
                size++;
            }
            playlistSongs = new String[size][7];
            rs.beforeFirst();

            //load Titles into table
            int iter = 0;
            while (rs.next())
            {
                playlistSongs[iter][0] = rs.getString("Title");
                playlistSongs[iter][1] = rs.getString("Artist");
                playlistSongs[iter][2] = rs.getString("Album");
                playlistSongs[iter][3] = rs.getString("genre");
                playlistSongs[iter][4] = rs.getString("song_length");
                playlistSongs[iter][5] = rs.getString("file_location");
                playlistSongs[iter][6] = rs.getString("comment");
                iter++;
            }
            rs.close();
            //calcPlaylist(n);
            playlistModel.addList(playlistSongs);
            setModel(playlistModel);
            isPlaylist = true;
            currentPlaylist = n;
        }
        else if (n.compareToIgnoreCase("library") == 0)
        {
            setModel(model);
            isPlaylist = false;
        }

    }
    // Handles possible apostrophes in strings for SQL
    /**
     * Handles possible apostrophes in strings for SQL.
     *
     * @param apos the string to be manipulated
     * @return the string with the right format
     */
    private String fixApostrophe(String apos)
    {
        return apos.replaceAll("'", "''");
    }

    /**
     * Used for null entries in database.
     *
     * @param input the string that will be checked
     * @return if there is an entry that is null
     */
    public static String dispNull(String input)
    {

        if (input == null || input.length() == 0)
        {
            return "N/A";
        }
        else
        {
            return input;
        }
    }

    public static SoundByteDatabase getInstance() throws ClassNotFoundException, SQLException
    {
        if (database == null)
        {
            database = new SoundByteDatabase();
        }
        return database;
    }

    public SoundByteModel getModel()
    {
        if (isPlaylist == false)
        {
            return model;
        }
        else
        {
            return playlistModel;
        }
    }

    public SoundByteModel getLibraryModel(){
        return model;
    }
    public void setLibraryModel() throws ClassNotFoundException, SQLException
    {
        isPlaylist = false;
        calcDB();
        setModel(model);
        TreePath path = find(library, "Library");
        tree.setSelectionPath(path);
        
    }
    
    public void updateButton(String s){
        if(s.compareToIgnoreCase("resume") == 0){
            change.firePropertyChange("resume", 1, 2);
        }
        else if(s.compareToIgnoreCase("pause") == 0){
            change.firePropertyChange("pause", 1, 2);
        }
        else if(s.compareToIgnoreCase("newSong")==0){
            change.firePropertyChange("newSong", 1, 2);
        }
        else if(s.compareToIgnoreCase("end")==0){
            change.firePropertyChange("end", 1, 2);
            change2.firePropertyChange("end", 1, 2);
        }
        else if(s.compareToIgnoreCase("shuffle")==0){
            change2.firePropertyChange("shuffle", 1, 2);
        }
        else if(s.compareToIgnoreCase("shuffle1")==0){
            change.firePropertyChange("shuffle1", 1, 2);
        }
        else if(s.compareToIgnoreCase("repeat1")==0){
            change.firePropertyChange("repeat1", 1, 2);
        }
            
    }
    
    public boolean[] dispColumns() throws ClassNotFoundException, SQLException{
        boolean[] s = new boolean[5];
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        sql = "select * from columns";
        ResultSet rs = stmt.executeQuery(sql);
        int iter = 0;
        while(rs.next()){
            s[iter] = rs.getBoolean("showing");
            iter++;
        }
        rs.close();
        return s;
    }
    
    public void updateColumns(int i, boolean a) throws ClassNotFoundException, SQLException{
        Class.forName(JDBC_DRIVER);
        con = DriverManager.getConnection(DB_URL);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        if(i == 1){
            sql = "update columns set showing = " + a + " where cName = 'artist'";
            stmt.execute(sql);
            if(a){
                change.firePropertyChange("artist", 1, 2);
            }
            else{
                change.firePropertyChange("artist", 2, 1);
            }
        }
        if(i == 2){
            sql = "update columns set showing = " + a + " where cName = 'album'";
            stmt.execute(sql);
            if(a){
                change.firePropertyChange("album", 1, 2);
            }
            else{
                change.firePropertyChange("album", 2, 1);
            }
        }
        if(i == 3){
            sql = "update columns set showing = " + a + " where cName = 'genre'";
            stmt.execute(sql);
            if(a){
                change.firePropertyChange("genre", 1, 2);
            }
            else{
                change.firePropertyChange("genre", 2, 1);
            }
        }
        if(i == 4){
            sql = "update columns set showing = " + a + " where cName = 'length'";
            stmt.execute(sql);
            if(a){
                change.firePropertyChange("length", 1, 2);
            }
            else{
                change.firePropertyChange("length", 2, 1);
            }
        }
        if(i == 6){
            sql = "update columns set showing = " + a + " where cName = 'comment'";
            stmt.execute(sql);
            if(a){
                change.firePropertyChange("comment", 1, 2);
            }
            else{
                change.firePropertyChange("comment", 2, 1);
            }
        }
        
    }

    public SoundByteListener getListen()
    {
        return listen;
    }

    public String genreString(int num)
    {
        String genre = "N/A";

        if (num == 0)
        {
            genre = "Blues";
        }
        if (num == 1)
        {
            genre = "Classic Rock";
        }
        if (num == 2)
        {
            genre = "Country";
        }
        if (num == 3)
        {
            genre = "Dance";
        }
        if (num == 4)
        {
            genre = "Disco";
        }
        if (num == 5)
        {
            genre = "Funk";
        }
        if (num == 6)
        {
            genre = "Grunge";
        }
        if (num == 7)
        {
            genre = "Hip-Hop";
        }
        if (num == 8)
        {
            genre = "Jazz";
        }
        if (num == 9)
        {
            genre = "Metal";
        }
        if (num == 10)
        {
            genre = "New Age";
        }
        if (num == 11)
        {
            genre = "Oldies";
        }
        if (num == 12)
        {
            genre = "Other";
        }
        if (num == 13)
        {
            genre = "Pop";
        }
        if (num == 14)
        {
            genre = "R&B";
        }
        if (num == 15)
        {
            genre = "Rap";
        }
        if (num == 16)
        {
            genre = "Reggae";
        }
        if (num == 17)
        {
            genre = "Rock";
        }
        if (num == 18)
        {
            genre = "Techno";
        }
        if (num == 19)
        {
            genre = "Industrial";
        }
        if (num == 20)
        {
            genre = "Alternative";
        }
        if (num == 21)
        {
            genre = "Ska";
        }
        if (num == 22)
        {
            genre = "Death Metal";
        }
        if (num == 23)
        {
            genre = "Pranks";
        }
        if (num == 24)
        {
            genre = "SoundTrack";
        }
        if (num == 25)
        {
            genre = "Euro-Techno";
        }
        if (num == 26)
        {
            genre = "Ambient";
        }
        if (num == 27)
        {
            genre = "Trip-Hop";
        }
        if (num == 28)
        {
            genre = "Vocal";
        }
        if (num == 29)
        {
            genre = "Jazz+Funk";
        }
        if (num == 30)
        {
            genre = "Fusion";
        }
        if (num == 31)
        {
            genre = "Trance";
        }
        if (num == 32)
        {
            genre = "Classical";
        }
        if (num == 33)
        {
            genre = "Instrumental";
        }
        if (num == 34)
        {
            genre = "Acid";
        }
        if (num == 35)
        {
            genre = "House";
        }
        if (num == 36)
        {
            genre = "Game";
        }
        if (num == 37)
        {
            genre = "Sound Clip";
        }
        if (num == 38)
        {
            genre = "Gospel";
        }
        if (num == 39)
        {
            genre = "Noise";
        }
        if (num == 40)
        {
            genre = "Alternative Rock";
        }
        if (num == 41)
        {
            genre = "Bass";
        }
        if (num == 42)
        {
            genre = "Soul";
        }
        if (num == 43)
        {
            genre = "Punk";
        }
        if (num == 44)
        {
            genre = "Space";
        }
        if (num == 45)
        {
            genre = "Meditative";
        }
        if (num == 46)
        {
            genre = "Instrumental Pop";
        }
        if (num == 47)
        {
            genre = "Instrumental Rock";
        }
        if (num == 48)
        {
            genre = "Ethnic";
        }
        if (num == 49)
        {
            genre = "Gothic";
        }
        if (num == 50)
        {
            genre = "Darkwave";
        }
        if (num == 51)
        {
            genre = "Techno-Industrial";
        }
        if (num == 52)
        {
            genre = "Electronic";
        }
        if (num == 53)
        {
            genre = "Pop-Folk";
        }
        if (num == 54)
        {
            genre = "Eurodance";
        }
        if (num == 55)
        {
            genre = "Dream";
        }
        if (num == 56)
        {
            genre = "Southern Rock";
        }
        if (num == 57)
        {
            genre = "Comedy";
        }
        if (num == 58)
        {
            genre = "Cult";
        }
        if (num == 59)
        {
            genre = "Gangsta";
        }
        if (num == 60)
        {
            genre = "Top 40";
        }
        if (num == 61)
        {
            genre = "Christian Rap";
        }
        if (num == 62)
        {
            genre = "Pop/Funk";
        }
        if (num == 63)
        {
            genre = "Jungle";
        }
        if (num == 64)
        {
            genre = "Native American";
        }
        if (num == 65)
        {
            genre = "Cabaret";
        }
        if (num == 66)
        {
            genre = "New Wave";
        }
        if (num == 67)
        {
            genre = "Psychedelic";
        }
        if (num == 68)
        {
            genre = "Rave";
        }
        if (num == 69)
        {
            genre = "Showtunes";
        }
        if (num == 70)
        {
            genre = "Trailer";
        }
        if (num == 71)
        {
            genre = "Lo-Fi";
        }
        if (num == 72)
        {
            genre = "Tribal";
        }
        if (num == 73)
        {
            genre = "Acid Punk";
        }
        if (num == 74)
        {
            genre = "Acid Jazz";
        }
        if (num == 75)
        {
            genre = "Polka";
        }
        if (num == 76)
        {
            genre = "Retro";
        }
        if (num == 77)
        {
            genre = "Musical";
        }
        if (num == 78)
        {
            genre = "Rock & Roll";
        }
        if (num == 79)
        {
            genre = "Hard Rock";
        }
        if (num == 131)
        {
            genre = "Indie";
        }

        return genre;
    }
}
