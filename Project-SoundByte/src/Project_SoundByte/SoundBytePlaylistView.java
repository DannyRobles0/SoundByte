package Project_SoundByte;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.ActivationDataFlavor;
import javax.swing.DropMode;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import static javax.swing.ScrollPaneConstants.*;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class SoundBytePlaylistView extends JFrame
{

    static JFileChooser fc;
    // TimerTask needed for action during interval and Timer needed for interval
    static TimerTask timerTask;
    static Timer timer;

    private static final String[] column =
    {
        "Title", "Artist", "Album",
        "Genre", "Length", "Location", "Comment"
    };

    private SoundBytePlaylistView() throws ClassNotFoundException, SQLException
    {
    }

    public static SoundBytePlaylistView getInstance() throws ClassNotFoundException, SQLException
    {
        if (playlistView == null)
        {
            playlistView = new SoundBytePlaylistView();
        }
        return playlistView;
    }
    
    public void initComponents(String n) throws ClassNotFoundException, SQLException
    {
        int size;

        // Set up file chooser
        currentPlaylist = n;
        fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setDragEnabled(true);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Initialize
        file = "none";
        jPanel1 = new JPanel();
        backButton = new JButton("Back");
        skipButton = new JButton("Skip");
        soundSlider = new JSlider();
        playPauseButton = new JButton("Play");
        stopButton = new JButton("Stop");
        progressBar = new JProgressBar();
        jPanel2 = new JPanel();
        jScrollBar1 = new JScrollBar();
        jScrollPane1 = new JScrollPane();
        database = SoundByteDatabase.getInstance();
        jTable1 = new JTable();
        jMenuBar1 = new JMenuBar();
        lTimer = new JLabel(convertTime(0));
        rTimer = new JLabel(convertTime(0));

        // File Menu
        jMenu1 = new JMenu("File");
        jMenu1.setVisible(false);
        menuAddSong = new JMenuItem("Add Song");
        menuDelSong = new JMenuItem("Delete Highlighted Song");
        menuQuit = new JMenuItem("Quit");
        menuOpen = new JMenuItem("Open");
        jTable1.setFocusable(false);

        // Controls Menu
        menuControls = new JMenu("Controls");
        menuPlay = new JMenuItem("Play");
        menuPlay.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
        menuNext = new JMenuItem("Next");
        menuNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ActionEvent.CTRL_MASK));
        menuPrev = new JMenuItem("Previous");
        menuPrev.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.CTRL_MASK));
        menuRecent = new JMenu("Play Recent");
        menuCurrent = new JMenuItem("Go to Current Song");
        menuCurrent.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        menuUp = new JMenuItem("Increase Volume");
        menuUp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        menuDown = new JMenuItem("Decrease Volume");
        menuDown.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        menuShuffle = new JCheckBoxMenuItem("Shuffle");
        menuRepeat = new JCheckBoxMenuItem("Repeat");
        
        //Column header popup menu
        title = new JCheckBoxMenuItem();
        artist = new JCheckBoxMenuItem();
        album = new JCheckBoxMenuItem();
        length = new JCheckBoxMenuItem();
        genre = new JCheckBoxMenuItem();
        comment = new JCheckBoxMenuItem();
        displayColumns = new JPopupMenu();
        title.setText("Title");
        title.setName("0");
        artist.setText("Artist");
        artist.setName("1");
        album.setText("Album");
        album.setName("2");
        length.setText("Length");
        length.setName("4");
        genre.setText("Genre");
        genre.setName("3");
        comment.setText("Comment");
        comment.setName("5");
        title.setSelected(true);
        title.setEnabled(false);
        artist.setSelected(true);
        album.setSelected(true);
        length.setSelected(true);
        genre.setSelected(true);
        comment.setSelected(true);
        displayColumns.add(title);
        displayColumns.add(artist);
        displayColumns.add(album);
        displayColumns.add(genre);
        displayColumns.add(length);
        displayColumns.add(comment);
        jTable1.getTableHeader().setComponentPopupMenu(displayColumns);
        jTable1.getTableHeader().setReorderingAllowed(false);
        
        //Set correct playPause button on startup
        if(database.getListen().getStatus() == 0){
            playPauseButton.setText("Pause");
        }

        // Song Popup Menu
        jPopMenu1 = new JPopupMenu();
        popMenuAddSong = new JMenuItem("Add Song");
        popMenuDelSong = new JMenuItem("Delete Highlighted Song");
        TableTransferHandler handler = new TableTransferHandler();

        size = database.countSongs(currentPlaylist);
        songs = new String[size][7];
        songs = database.newWindowSongs(currentPlaylist);
        tableModel = new SoundByteModel(songs, column);
        jTable1.setModel(tableModel);
        PropertyChangeListener pcl = new PropertyChangeListener()
        {

            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                try
                {
                    if (evt.getPropertyName().compareToIgnoreCase("newSong") == 0
                            || evt.getPropertyName().compareToIgnoreCase("resume") == 0)
                    {
                        playPauseButton.setText("Pause");
                        change.firePropertyChange("newSong", 1, 2);

                    }
                    else if (evt.getPropertyName().compareToIgnoreCase("pause") == 0)
                    {
                        playPauseButton.setText("Play");
                        change.firePropertyChange("pause", 1, 2);
                    }
                    else if(evt.getPropertyName().compareToIgnoreCase("artist") ==0){
                        if(evt.getOldValue() == (Object)1){
                            jTable1.getColumnModel().getColumn(1).setMaxWidth(maxWidth);
                            jTable1.getColumnModel().getColumn(1).setMinWidth(minWidth);
                            jTable1.getColumnModel().getColumn(1).setPreferredWidth(preferredWidth);
                            artist.setSelected(true);}
                        else{
                            jTable1.getColumnModel().getColumn(1).setMaxWidth(0);
                            jTable1.getColumnModel().getColumn(1).setMinWidth(0);
                            jTable1.getColumnModel().getColumn(1).setPreferredWidth(0);
                            artist.setSelected(false);
                        }                        
                    }
                    else if(evt.getPropertyName().compareToIgnoreCase("album") ==0){
                        if(evt.getOldValue() == (Object)1){
                            jTable1.getColumnModel().getColumn(2).setMaxWidth(maxWidth);
                            jTable1.getColumnModel().getColumn(2).setMinWidth(minWidth);
                            jTable1.getColumnModel().getColumn(2).setPreferredWidth(preferredWidth);
                            album.setSelected(true);}
                        else{
                            jTable1.getColumnModel().getColumn(2).setMaxWidth(0);
                            jTable1.getColumnModel().getColumn(2).setMinWidth(0);
                            jTable1.getColumnModel().getColumn(2).setPreferredWidth(0);
                            album.setSelected(false);
                        }                        
                    }
                    else if(evt.getPropertyName().compareToIgnoreCase("genre") ==0){
                        if(evt.getOldValue() == (Object)1){
                            jTable1.getColumnModel().getColumn(3).setMaxWidth(maxWidth);
                            jTable1.getColumnModel().getColumn(3).setMinWidth(minWidth);
                            jTable1.getColumnModel().getColumn(3).setPreferredWidth(preferredWidth);
                            genre.setSelected(true);}
                        else{
                            jTable1.getColumnModel().getColumn(3).setMaxWidth(0);
                            jTable1.getColumnModel().getColumn(3).setMinWidth(0);
                            jTable1.getColumnModel().getColumn(3).setPreferredWidth(0);
                            genre.setSelected(false);
                        }                        
                    }
                    else if(evt.getPropertyName().compareToIgnoreCase("length") ==0){
                        if(evt.getOldValue() == (Object)1){
                            jTable1.getColumnModel().getColumn(4).setMaxWidth(maxWidth);
                            jTable1.getColumnModel().getColumn(4).setMinWidth(minWidth);
                            jTable1.getColumnModel().getColumn(4).setPreferredWidth(preferredWidth);
                            length.setSelected(true);}
                        else{
                            jTable1.getColumnModel().getColumn(4).setMaxWidth(0);
                            jTable1.getColumnModel().getColumn(4).setMinWidth(0);
                            jTable1.getColumnModel().getColumn(4).setPreferredWidth(0);
                            length.setSelected(false);
                        }                        
                    }
                    else if(evt.getPropertyName().compareToIgnoreCase("comment") ==0){
                        if(evt.getOldValue() == (Object)1){
                            jTable1.getColumnModel().getColumn(6).setMaxWidth(maxWidth);
                            jTable1.getColumnModel().getColumn(6).setMinWidth(minWidth);
                            jTable1.getColumnModel().getColumn(6).setPreferredWidth(preferredWidth);
                            comment.setSelected(true);}
                        else{
                            jTable1.getColumnModel().getColumn(6).setMaxWidth(0);
                            jTable1.getColumnModel().getColumn(6).setMinWidth(0);
                            jTable1.getColumnModel().getColumn(6).setPreferredWidth(0);
                            comment.setSelected(false);
                        }                        
                    }
                    else if(evt.getPropertyName().compareToIgnoreCase("recent")==0){
                        try {
                            addNewRecent();
                        } catch (SQLException | ClassNotFoundException ex) {
                            Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else if(evt.getPropertyName().compareToIgnoreCase("shuffle1")==0){
                        if(menuShuffle.isSelected() == true){
                            menuShuffle.setSelected(false);
                        }
                        else{
                            menuShuffle.setSelected(true);
                        }
                    }
                    else if(evt.getPropertyName().compareToIgnoreCase("Repeat1")==0){
                        if(menuRepeat.isSelected() == true){
                            menuRepeat.setSelected(false);
                        }
                        else{
                            menuRepeat.setSelected(true);
                        }
                    }
                    else
                    {
                        songs = database.newWindowSongs(currentPlaylist);
                        tableModel.addList(songs);
                    }
                }
                catch (ClassNotFoundException | SQLException ex)
                {
                    Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                }
                ;
            }
        };
        database.addPropertyChangeListener(pcl);

        setTitle("SoundByte Playlist: " + n);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        jScrollPane1.setViewportView(jTable1);
        jScrollPane1.add(fc);
        jScrollPane1.setVerticalScrollBar(jScrollBar1);
        jScrollPane1.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        jTable1.setDragEnabled(true);
        jTable1.setDropMode(DropMode.INSERT);
        jTable1.setTransferHandler(handler);
        jTable1.setRowSelectionAllowed(true);
        jTable1.setFillsViewportHeight(true);
        jTable1.setAutoCreateRowSorter(true);
        jTable1.getRowSorter().toggleSortOrder(0);

        // Add to File Menu
        jMenu1.add(menuOpen);
        jMenu1.add(menuAddSong);
        jMenu1.add(menuDelSong);
        jMenu1.add(menuQuit);

        // Add to Controls Menu
        menuControls.add(menuPlay);
        menuControls.add(menuNext);
        menuControls.add(menuPrev);
        menuControls.add(menuRecent);
        menuControls.add(menuCurrent);
        menuControls.addSeparator();
        menuControls.add(menuUp);
        menuControls.add(menuDown);
        menuControls.addSeparator();
        menuControls.add(menuShuffle);
        menuControls.add(menuRepeat);

        // Add to Menu Bar
        jMenuBar1.add(jMenu1);
        jMenuBar1.add(menuControls);
        setJMenuBar(jMenuBar1);
        recentMenu();

        // Add to Song Popup Menu
        jPopMenu1.add(popMenuAddSong);
        jPopMenu1.add(popMenuDelSong);
        jTable1.setComponentPopupMenu(jPopMenu1);

        //initialize layouts and models
        ListSelectionModel rowSel = jTable1.getSelectionModel();
        rowSel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        jPanel2Layout.setAutoCreateGaps(true);
        jPanel2Layout.setAutoCreateContainerGaps(true);
        jPanel2.setLayout(jPanel2Layout);
        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1Layout.setAutoCreateGaps(true);
        jPanel1Layout.setAutoCreateContainerGaps(true);
        DataFlavor l = new ActivationDataFlavor(String[].class, DataFlavor.javaJVMLocalObjectMimeType, "String");
        jPanel1.setLayout(jPanel1Layout);
        
        //Create column entities
        maxWidth = jTable1.getColumnModel().getColumn(0).getMaxWidth();
        minWidth = jTable1.getColumnModel().getColumn(0).getMinWidth();
        preferredWidth = jTable1.getColumnModel().getColumn(0).getPreferredWidth();
        columnsDisplayed = database.dispColumns();
        
        updateColumns();
        if(database.getListen().getRepeat() == true){
            menuRepeat.setSelected(true);            
        }
        if(database.getListen().getShuffle() == true){
            menuShuffle.setSelected(true);
        }
        
        jTable1.getColumnModel().getColumn(5).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(0);

        //Select first Row at startup
        jTable1.clearSelection();
        file = "none";
        

        //Set up Drag and Drop
        jTable1.setDropTarget(new DropTarget()
        {
            @Override
            public synchronized void dragOver(DropTargetDragEvent dtde)
            {
                Point point = dtde.getLocation();
                int row = jTable1.rowAtPoint(point);
                if (row < 0)
                {
                    jTable1.clearSelection();
                    file = "none";
                }
                else
                {
                    jTable1.setRowSelectionInterval(row, row);
                }
                dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);

            }

            @Override
            public synchronized void drop(DropTargetDropEvent dtde)
            {
                Point point = dtde.getLocation();
                int row = jTable1.rowAtPoint(point);
                Transferable t = dtde.getTransferable();

                if (dtde.isDataFlavorSupported(l))
                {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    String[] s = null;
                    try
                    {
                        s = (String[]) t.getTransferData(l);
                        for (int i = 0; i < s.length; i++)
                        {
                            database.addToNewWindowPlaylist(s[i], currentPlaylist);
                        }
                    }
                    catch (UnsupportedFlavorException | IOException | ClassNotFoundException | SQLException | UnsupportedTagException | InvalidDataException ex)
                    {
                        Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
                {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    //Transferable t = dtde.getTransferable();
                    List fileList = null;
                    try
                    {
                        fileList = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
                        if (fileList.size() > 0)
                        {
                            jTable1.clearSelection();
                            point = dtde.getLocation();
                            row = jTable1.rowAtPoint(point);
                            File f = null;
                            String fileName;
                            for (Object fileList1 : fileList)
                            {
                                f = (File) fileList1;
                                fileName = f.getAbsolutePath();
                                database.addToNewWindowPlaylist(fileName, currentPlaylist);
                                songs = database.newWindowSongs(currentPlaylist);
                                tableModel.addList(songs);
                            }

                        }
                    }
                    catch (UnsupportedFlavorException | IOException |
                            SQLException | ClassNotFoundException |
                            UnsupportedTagException | InvalidDataException ex)
                    {
                        Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    System.out.println("Drop rejected");
                    dtde.rejectDrop();
                }
            }
        });

        //Set up listeners
        backButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try {
                    backButtonActionPerformed(evt);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        skipButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                try {
                    skipButtonActionPerformed(evt);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        playPauseButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try {
                    playPauseButtonActionPerformed(evt);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        stopButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                stopButtonActionPerformed(evt);
            }
        });
        jTable1.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent evt)
            {   playPauseButton.requestFocusInWindow();
                evt.getSource();
                if (evt.getClickCount() >= 2)
                {
                    if (file.compareToIgnoreCase("none") != 0)
                    {
                        database.getListen().play(file, 1);
                        try {
                            database.addToRecent(file);
                            addNewRecent();
                            change.firePropertyChange("recent", 1, 2);
                        } catch (ClassNotFoundException | SQLException ex) {
                            Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                        }                       
                        playPauseButton.setText("Pause");
                        try
                        {
                            database.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
                        }
                        catch (BasicPlayerException ex)
                        {
                            Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
        soundSlider.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                soundSliderStateChanged(evt);
            }
        });
        rowSel.addListSelectionListener(new ListSelectionListener()
        {
            @Override
            public void valueChanged(ListSelectionEvent evt)
            {
                if (evt.getValueIsAdjusting())
                {
                    return;
                }
                int row = jTable1.getSelectedRow();
                if (row >= 0 && row < jTable1.getModel().getRowCount())
                {
                    String val = (String) jTable1.getValueAt(row, 5);
                    file = val;
                }
            }
        });
        menuAddSong.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try
                {
                    menuAddSongActionPerformed(evt);
                }
                catch (SQLException | ClassNotFoundException |
                        IOException | UnsupportedTagException |
                        InvalidDataException ex)
                {
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        menuDelSong.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try
                {
                    menuDelSongActionPerformed(evt);
                }
                catch (SQLException | ClassNotFoundException ex)
                {
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        menuQuit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                menuQuitActionPerformed(evt);
            }
        });
        menuOpen.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try {
                    menuOpenActionPerformed(evt);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        menuPlay.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try {
                    menuPlayActionPerformed(evt);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        menuNext.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try {
                    skipButtonActionPerformed(evt);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        menuPrev.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try {
                    backButtonActionPerformed(evt);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        menuCurrent.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                menuCurrentActionPerformed(evt);
            }
        });
        menuUp.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                menuUpActionPerformed(evt);
            }
        });
        menuDown.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                menuDownActionPerformed(evt);
            }
        });
        menuShuffle.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                menuShuffleActionPerformed(evt);
            }
        });
        menuRepeat.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                menuRepeatActionPerformed(evt);
            }
        });
        popMenuAddSong.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try
                {
                    menuAddSongActionPerformed(evt);
                }
                catch (SQLException | ClassNotFoundException |
                        IOException | UnsupportedTagException |
                        InvalidDataException ex)
                {
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        popMenuDelSong.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try
                {
                    menuDelSongActionPerformed(evt);
                }
                catch (SQLException | ClassNotFoundException ex)
                {
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        artist.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try {
                    popMenuArtist(evt);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        album.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try {
                    popMenuAlbum(evt);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        genre.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try {
                    popMenuGenre(evt);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        length.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try {
                    popMenuLength(evt);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        comment.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try {
                    popMenuComment(evt);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        // Progress Bar and lTimer and rTimer 
        TimerTask timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                progressBar.setMaximum((int) database.getListen().getTotalTime());
                if (database.getListen().getPlayer().getStatus() == BasicPlayer.PLAYING)
                {
                    progressBar.setValue((int) database.getListen().getCurrentTime());
                    lTimer.setText(convertTime(database.getListen().getCurrentTime()));
                    rTimer.setText(convertTime(database.getListen().getTotalTime() - database.getListen().getCurrentTime()));
                }
                if (database.getListen().getPlayer().getStatus() == BasicPlayer.STOPPED)
                {
                    progressBar.setValue(0);
                    lTimer.setText(convertTime(0));
                    rTimer.setText(convertTime(0));
                }
            }
        };
        timer = new Timer("Progress");
        timer.scheduleAtFixedRate(timerTask, 0, 1000);

        //set up layouts and groups
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1));

        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane1))
                        .addContainerGap())
        );

        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(lTimer)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(progressBar)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(rTimer))
                                .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(backButton)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(playPauseButton)
                                        .addGap(14, 14, 14)
                                        .addComponent(stopButton)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(skipButton)
                                        .addComponent(soundSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(68, 68, 68))))
                .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2)
                        .addContainerGap())
        );

        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(lTimer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(rTimer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(backButton)
                                        .addComponent(skipButton)
                                        .addComponent(stopButton))
                                .addComponent(playPauseButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(soundSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
        );

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    /*=========Listener Implementations=======================================*/
    private void menuAddSongActionPerformed(ActionEvent evt) throws
            SQLException, ClassNotFoundException, IOException,
            UnsupportedTagException, InvalidDataException
    {
        if (fc.showOpenDialog(SoundBytePlaylistView.this) == JFileChooser.APPROVE_OPTION)
        {
            File files = fc.getSelectedFile();
            String name = files.getAbsolutePath();
            database.addToNewWindowPlaylist(name, currentPlaylist);
            songs = database.newWindowSongs(currentPlaylist);
            tableModel.addList(songs);
        }
    }

    private void menuQuitActionPerformed(ActionEvent evt)
    {
        System.exit(0);
    }

    private void menuDelSongActionPerformed(ActionEvent evt) throws ClassNotFoundException, SQLException
    {
        database.delFromNewWindowPlaylist(file, currentPlaylist);
        songs = database.newWindowSongs(currentPlaylist);
        tableModel.addList(songs);
        if (jTable1.getModel().getRowCount() < 1)
        {
            jTable1.clearSelection();
            file = "none";
        }
        else
        {
            jTable1.setRowSelectionInterval(0, 0);
            file = (String) jTable1.getModel().getValueAt(0, 5);
        }
    }

    private void menuOpenActionPerformed(ActionEvent evt) throws ClassNotFoundException, SQLException
    {
        if (fc.showOpenDialog(SoundBytePlaylistView.this) == JFileChooser.APPROVE_OPTION)
        {
            File files = fc.getSelectedFile();
            String name = files.getAbsolutePath();
            database.getListen().play(name, 1);
            database.addToRecent(file);
            addNewRecent();
            change.firePropertyChange("recent", 1, 2);
            playPauseButton.setText("Pause");
            try
            {
                database.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
            }
            catch (BasicPlayerException ex)
            {
                Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void menuAddPlaylistActionPerformed(ActionEvent evt) throws ClassNotFoundException, SQLException
    {
        String s = (String) JOptionPane.showInputDialog(jTable1, "Name the Playlist",
                "New Playlist", JOptionPane.PLAIN_MESSAGE, null, null, null);
        database.createPlaylist(s);
    }

    private void skipButtonActionPerformed(ActionEvent evt) throws ClassNotFoundException, SQLException
    {
        if(menuShuffle.isSelected()==false){
            String current = database.getListen().getSongName();
            String next = tableModel.getNext(current);
            String nullString = "none";
            if (next.compareTo(nullString) != 0)
            {
                database.getListen().play(next, 1);
                database.addToRecent(next);
                addNewRecent();
                change.firePropertyChange("recent", 1, 2);
                int row = tableModel.getRow(next);
                jTable1.setRowSelectionInterval(row, row);
                playPauseButton.setText("Pause");
                try
                {
                    database.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
                }
                catch (BasicPlayerException ex)
                {
                    Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else{
            change.firePropertyChange("shuffle1", 1, 2);
            jTable1.clearSelection();
        }
    }

    private void backButtonActionPerformed(ActionEvent evt) throws ClassNotFoundException, SQLException
    {
        
            String current = database.getListen().getSongName();
            String prev = tableModel.getPrev(current);
            String nullString = "none";
            if (prev.compareTo(nullString) != 0)
            {
                database.getListen().play(prev, 1);
                database.addToRecent(prev);
                addNewRecent();
                change.firePropertyChange("recent", 1, 2);
                int row = tableModel.getRow(prev);
                jTable1.setRowSelectionInterval(row, row);
                playPauseButton.setText("Pause");
                try
                {
                    database.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
                }
                catch (BasicPlayerException ex)
                {
                    Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        
    }

    private void stopButtonActionPerformed(ActionEvent evt)
    {
        database.getListen().stop();
        if (database.getListen().getPlayer().getStatus() != 0)
        {
            playPauseButton.setText("Play");
        }
    }

    private void playPauseButtonActionPerformed(ActionEvent evt) throws ClassNotFoundException, SQLException
    {
        if (database.getListen().getPlayer().getStatus() != 0)
        {
            if (database.getListen().getSongName().compareToIgnoreCase(file) != 0)
            {
                database.getListen().play(file, 1);
                database.addToRecent(file);
                addNewRecent();
                change.firePropertyChange("recent", 1, 2);
                playPauseButton.setText("Pause");
                try
                {
                    database.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
                }
                catch (BasicPlayerException ex)
                {
                    Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                database.getListen().play(file, 0);
                database.addToRecent(file);
                addNewRecent();
                change.firePropertyChange("recent", 1, 2);
                playPauseButton.setText("Pause");
                try
                {
                    database.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
                }
                catch (BasicPlayerException ex)
                {
                    Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else
        {
            database.getListen().pause();
            playPauseButton.setText("Play");
        }
    }

    private void soundSliderStateChanged(javax.swing.event.ChangeEvent evt)
    {
        if (database.getListen().getPlayer().getStatus() == 0)
        {
            JSlider source = (JSlider) evt.getSource();
            try
            {
                database.getListen().getControl().setGain(source.getValue() / 100.0);
            }
            catch (BasicPlayerException ex)
            {
                Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void menuPlayActionPerformed(ActionEvent evt) throws ClassNotFoundException, SQLException
    {
        int a = jTable1.getSelectedRow();
        if (file.compareToIgnoreCase("none") != 0 && a != -1)
        {
            database.getListen().play(file, 1);
            database.addToRecent(file);
            addNewRecent();
            change.firePropertyChange("recent", 1, 2);
            playPauseButton.setText("Pause");
            try
            {
                database.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
            }
            catch (BasicPlayerException ex)
            {
                Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(database.getListen().getShuffle() == true){
            change.firePropertyChange("shuffle1", 1, 2);
        }
        else{
            int row = jTable1.getRowSorter().convertRowIndexToModel(0);
            file = (String)jTable1.getModel().getValueAt(row,5);
            database.getListen().play(file, 1);
            database.addToRecent(file);
            addNewRecent();
            playPauseButton.setText("Pause");
        }
    }

    private void menuCurrentActionPerformed(ActionEvent evt)
    {
        if(database.getListen().getStatus() != 0){
            String current = database.getListen().getSongName();
            String nullString = "none";
            if (current.compareTo(nullString) != 0)
            {
                current = database.getListen().getSongName();
                int row = database.getModel().getRow(current);
                row = jTable1.getRowSorter().convertRowIndexToView(row);
                jTable1.setRowSelectionInterval(row, row);
                Rectangle r = jTable1.getCellRect(row, 0, true);
                jTable1.scrollRectToVisible(r);
            }
        }
        else{
            int row = database.getModel().getRow(file);
            row = jTable1.getRowSorter().convertRowIndexToView(row);
            jTable1.setRowSelectionInterval(row, row);
            Rectangle r = jTable1.getCellRect(row, 0, true);
            jTable1.scrollRectToVisible(r);
        }
    }

    private void menuUpActionPerformed(ActionEvent evt)
    {
        soundSlider.setValue(soundSlider.getValue() + 5);
        if (database.getListen().getPlayer().getStatus() == BasicPlayer.PLAYING)
        {
            try
            {
                database.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
            }
            catch (BasicPlayerException ex)
            {
                Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void menuDownActionPerformed(ActionEvent evt)
    {
        soundSlider.setValue(soundSlider.getValue() - 5);
        if (database.getListen().getPlayer().getStatus() == BasicPlayer.PLAYING)
        {
            try
            {
                database.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
            }
            catch (BasicPlayerException ex)
            {
                Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    

    private void menuShuffleActionPerformed(ActionEvent evt)
    {
        JCheckBoxMenuItem box = (JCheckBoxMenuItem)evt.getSource();
        if(box.isSelected()){
            database.getListen().setShuffle(true);
            if(database.getListen().getStatus() != 0){
                change.firePropertyChange("shuffle1", 1, 2);
            }
        }
        else{
            database.getListen().setShuffle(false);
        }
        change.firePropertyChange("shuffle2", 1, 2);
    }

    private void menuRepeatActionPerformed(ActionEvent evt)
    {
        JCheckBoxMenuItem box = (JCheckBoxMenuItem)evt.getSource();
        if(box.isSelected()){
            database.getListen().setRepeat(true);
        }
        else{
            database.getListen().setRepeat(false);
        }
        change.firePropertyChange("Repeat1", 1, 2);
    }

    /*=========End Listener Implementations===================================*/
    private void recentMenu() throws ClassNotFoundException, SQLException
    {
        
        int b = database.countRecent();
        if(b>0){
            menuRecentSub = new JMenuItem[b];
            String[] a = new String[b];
            a = database.recentNames(b);
            for (int c = 0; c < b; c++)
            {
                menuRecentSub[c] = new JMenuItem(a[c]);
                menuRecent.add(menuRecentSub[c]);
                final int z = c;
                menuRecentSub[c].addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        try
                        {
                            menuRecentSelect(evt, z);
                        }
                        catch (ClassNotFoundException | SQLException | IOException | UnsupportedTagException | InvalidDataException ex)
                        {
                            Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
            menuRecent.getPopupMenu().pack();
        }
    }
    private void addNewRecent() throws SQLException, ClassNotFoundException{
        menuRecent.removeAll();
        recentMenu();
    }
    
    private void menuRecentSelect(ActionEvent evt, int a) throws
            ClassNotFoundException, SQLException, IOException,
            UnsupportedTagException, InvalidDataException
    {
        file = database.getRecent(a);
        database.getListen().play(file, 1);
        database.addToRecent(file);
        addNewRecent();
        change.firePropertyChange("recent", 1, 2);
            playPauseButton.setText("Pause");
            try
            {
                database.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
            }
            catch (BasicPlayerException ex)
            {
                Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
            }        
    }
    
    public void changePlaylist(String n) throws ClassNotFoundException, SQLException
    {
        songs = database.newWindowSongs(n);
        tableModel.addList(songs);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        change = new PropertyChangeSupport(this);
        change.addPropertyChangeListener(listener);
    }
    
    private void popMenuArtist(ActionEvent evt) throws ClassNotFoundException, SQLException{
        JCheckBoxMenuItem box = (JCheckBoxMenuItem)evt.getSource();
        if(box.isSelected()){
            jTable1.getColumnModel().getColumn(1).setMaxWidth(maxWidth);
            jTable1.getColumnModel().getColumn(1).setMinWidth(minWidth);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(preferredWidth);
            columnsDisplayed[0]= true;
            database.updateColumns(1, true);
            change.firePropertyChange("artist", 1, 2);
        }
        else{
            jTable1.getColumnModel().getColumn(1).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(1).setMinWidth(0);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(0);
            columnsDisplayed[0] = false;
            database.updateColumns(1, false);
            change.firePropertyChange("artist", 2, 1);

        }
    }
    
    private void popMenuAlbum(ActionEvent evt) throws ClassNotFoundException, SQLException{
        JCheckBoxMenuItem box = (JCheckBoxMenuItem)evt.getSource();
        if(box.isSelected()){
            jTable1.getColumnModel().getColumn(2).setMaxWidth(maxWidth);
            jTable1.getColumnModel().getColumn(2).setMinWidth(minWidth);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(preferredWidth);
            columnsDisplayed[0]= true;
            database.updateColumns(2, true);
            change.firePropertyChange("album", 1, 2);
        }
        else{
            jTable1.getColumnModel().getColumn(2).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(2).setMinWidth(0);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(0);
            columnsDisplayed[1] = false;
            database.updateColumns(2, false);
            change.firePropertyChange("album", 2, 1);
        }
    }
    
    private void popMenuGenre(ActionEvent evt) throws ClassNotFoundException, SQLException{
        JCheckBoxMenuItem box = (JCheckBoxMenuItem)evt.getSource();
        if(box.isSelected()){
            jTable1.getColumnModel().getColumn(3).setMaxWidth(maxWidth);
            jTable1.getColumnModel().getColumn(3).setMinWidth(minWidth);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(preferredWidth);
            columnsDisplayed[0]= true;
            database.updateColumns(3, true);
            change.firePropertyChange("genre", 1, 2);
        }
        else{
            jTable1.getColumnModel().getColumn(3).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(3).setMinWidth(0);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(0);
            columnsDisplayed[2] = false;
            database.updateColumns(3, false);
            change.firePropertyChange("genre", 2, 1);
        }
    }
    
    private void popMenuLength(ActionEvent evt) throws ClassNotFoundException, SQLException{
        JCheckBoxMenuItem box = (JCheckBoxMenuItem)evt.getSource();
        if(box.isSelected()){
            jTable1.getColumnModel().getColumn(4).setMaxWidth(maxWidth);
            jTable1.getColumnModel().getColumn(4).setMinWidth(minWidth);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(preferredWidth);
            columnsDisplayed[0]= true;    
            database.updateColumns(4, true);
            change.firePropertyChange("length", 1, 2);
        }
        else{
            jTable1.getColumnModel().getColumn(4).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(4).setMinWidth(0);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(0);
            columnsDisplayed[3] = false;
            database.updateColumns(4, false);
            change.firePropertyChange("length", 2, 1);
        }
    }
    
    private void popMenuComment(ActionEvent evt) throws ClassNotFoundException, SQLException{
        JCheckBoxMenuItem box = (JCheckBoxMenuItem)evt.getSource();
       if(box.isSelected()){
            jTable1.getColumnModel().getColumn(6).setMaxWidth(maxWidth);
            jTable1.getColumnModel().getColumn(6).setMinWidth(minWidth);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(preferredWidth);
            columnsDisplayed[0]= true; 
            database.updateColumns(6, true);
            change.firePropertyChange("comment", 1, 2);
        }
        else{
            jTable1.getColumnModel().getColumn(6).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(6).setMinWidth(0);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(0);
            columnsDisplayed[4] = false;
            database.updateColumns(6, false);
            change.firePropertyChange("comment", 2, 1);
        }
    }

    private void updateColumns(){
        for(int i = 0;i<4;i++){ 
            if(columnsDisplayed[i] == false){
                jTable1.getColumnModel().getColumn(i+1).setMaxWidth(0);
                jTable1.getColumnModel().getColumn(i+1).setMinWidth(0);
                jTable1.getColumnModel().getColumn(i+1).setPreferredWidth(0);
                if(i == 0){artist.setSelected(false);}
                if(i == 1){album.setSelected(false);}
                if(i == 2){genre.setSelected(false);}
                if(i == 3){length.setSelected(false);}
            }
            
        }
        if(columnsDisplayed[4] == false){
            jTable1.getColumnModel().getColumn(6).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(6).setMinWidth(0);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(0);
            comment.setSelected(false);
        }
    }
    
    private String convertTime(long time)
    {
        String str = "";
        str = time / 3600 + ":";
        time = time % 3600;
        str = str + String.format("%02d", time / 60) + ":";
        time = time % 60;
        str = str + String.format("%02d", time);
        return str;
    }
    /*=========Variable Declarations==========================================*/
    private String file;
    private String[][] songs;
    private String currentPlaylist;
    private JButton backButton;
    private SoundByteDatabase database;
    private JTable jTable1;
    private JMenu jMenu1;
    private JMenu menuControls;
    private JMenuBar jMenuBar1;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JScrollBar jScrollBar1;
    private JScrollPane jScrollPane1;
    private JMenuItem menuAddSong;
    private JMenuItem menuQuit;
    private JMenuItem menuDelSong;
    private JMenuItem menuOpen;
    private JMenuItem menuPlay;
    private JMenuItem menuNext;
    private JMenuItem menuPrev;
    private JMenu menuRecent;
    private JMenuItem menuCurrent;
    private JMenuItem menuUp;
    private JMenuItem menuDown;
    private JCheckBoxMenuItem menuShuffle;
    private JCheckBoxMenuItem menuRepeat;
    private JCheckBoxMenuItem title;
    private JCheckBoxMenuItem artist;
    private JCheckBoxMenuItem album;
    private JCheckBoxMenuItem genre;
    private JCheckBoxMenuItem length;
    private JCheckBoxMenuItem comment;
    private JMenuItem popMenuAddSong;
    private JMenuItem popMenuDelSong;
    private JButton playPauseButton;
    private JProgressBar progressBar;
    private JButton skipButton;
    private JButton stopButton;
    private JSlider soundSlider;
    private JPopupMenu jPopMenu1;
    private JPopupMenu displayColumns;
    private SoundByteModel tableModel;
    private static SoundBytePlaylistView playlistView;
    private JLabel lTimer;
    private JLabel rTimer;
    private boolean alreadyInstantiated;
    protected PropertyChangeSupport change;
    private int maxWidth;
    private int minWidth;
    private int preferredWidth;
    private boolean[] columnsDisplayed;
    private JMenuItem[] menuRecentSub;
}
