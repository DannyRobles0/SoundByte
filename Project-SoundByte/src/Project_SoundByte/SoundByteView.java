package Project_SoundByte;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import static java.awt.event.ActionEvent.CTRL_MASK;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;
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
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import static javax.swing.ScrollPaneConstants.*;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javazoom.jlgui.basicplayer.BasicPlayer;
import static javazoom.jlgui.basicplayer.BasicPlayer.PLAYING;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class SoundByteView extends JFrame
{

    static JFileChooser fc;
    // TimerTask needed for action during interval and Timer needed for interval
    static TimerTask timerTask;
    static Timer timer;

    public SoundByteView() throws ClassNotFoundException, SQLException
    {
        initComponents();
    }
    
    private void initComponents() throws ClassNotFoundException, SQLException
    {
        // Set up file chooser
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
        jTable1 = SoundByteDatabase.getInstance();
        jMenuBar1 = new JMenuBar();
        lTimer = new JLabel(convertTime(0));
        rTimer = new JLabel(convertTime(0));
        tableModel = jTable1.getModel();
        rand = new Random();
        

        
       

        // File Menu
        jMenu1 = new JMenu("File");
        jMenu1.setVisible(true);
        menuAddSong = new JMenuItem("Add Song");
        menuDelSong = new JMenuItem("Delete Highlighted Song");
        menuAddPlaylist = new JMenuItem("Create Playlist");
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

        // Song Popup Menu
        jPopMenu1 = new JPopupMenu();
        popMenuAddSong = new JMenuItem("Add Song");
        popMenuDelSong = new JMenuItem("Delete Highlighted Song");
        popMenuPlaylist = new JMenu("Add Highlighted Song to Playlist");

        // Tree Popup Menu
        treePopMenu = new JPopupMenu();
        popMenuNewWindow = new JMenuItem("Open in New Window");
        popMenuDeletePlaylist = new JMenuItem("Delete Playlist");
        DragSource ds = new DragSource();
        TableTransferHandler handler = new TableTransferHandler();
        
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

        setTitle("SoundByte");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jScrollPane1.setViewportView(jTable1);
        jScrollPane1.add(fc);
        jScrollPane1.setVerticalScrollBar(jScrollBar1);
        jScrollPane1.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        jTable1.setTransferHandler(handler);
        jTable1.setDragEnabled(true);
        jTable1.setDropMode(DropMode.INSERT);
        jTable1.setRowSelectionAllowed(true);
        jTable1.setFillsViewportHeight(true);
        jTable1.setAutoCreateRowSorter(true);
        jTable1.getRowSorter().toggleSortOrder(0);

        

        // Add to File Menu
        jMenu1.add(menuOpen);
        jMenu1.add(menuAddSong);
        jMenu1.add(menuDelSong);
        jMenu1.add(menuAddPlaylist);
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
        playlistMenu();
        jPopMenu1.add(popMenuPlaylist);
        jTable1.setComponentPopupMenu(jPopMenu1);

        // Add to Tree Popup Menu
        treePopMenu.add(popMenuNewWindow);
        treePopMenu.add(popMenuDeletePlaylist);
        jTable1.getTree().setComponentPopupMenu(treePopMenu);

        // Initialize layouts and models
        ListSelectionModel rowSel = jTable1.getSelectionModel();
        rowSel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        jPanel2Layout.setAutoCreateGaps(true);
        jPanel2Layout.setAutoCreateContainerGaps(true);
        jPanel2.setLayout(jPanel2Layout);
        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1Layout.setAutoCreateGaps(true);
        jPanel1Layout.setAutoCreateContainerGaps(true);
        jPanel1.setLayout(jPanel1Layout);
        jScrollPane2 = new JScrollPane(jTable1.getTree());
        jScrollPane2.setViewportView(jTable1.getTree());
        jScrollPane2.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        
        //Create column entities
        maxWidth = jTable1.getColumnModel().getColumn(1).getMaxWidth();
        minWidth = jTable1.getColumnModel().getColumn(1).getMinWidth();
        preferredWidth = jTable1.getColumnModel().getColumn(1).getPreferredWidth();
        columnsDisplayed = jTable1.dispColumns();

                  
        updateColumns();
        jTable1.getColumnModel().getColumn(5).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(0);

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
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
                {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    Transferable t = dtde.getTransferable();
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
                                jTable1.addDB(fileName);
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
                    dtde.rejectDrop();
                }
            }
        });

        // Set up listeners
        pcl = new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                if (evt.getPropertyName().compareToIgnoreCase("newSong") == 0
                        || evt.getPropertyName().compareToIgnoreCase("resume") == 0)
                {
                    playPauseButton.setText("Pause");
                }
                else if (evt.getPropertyName().compareToIgnoreCase("pause") == 0)
                {
                    playPauseButton.setText("Play");
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
                    shuffle();
                }
                else if(evt.getPropertyName().compareToIgnoreCase("shuffle2")==0){
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
            }
        };
        
        pcl2 = new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                if(evt.getPropertyName().compareToIgnoreCase("end")==0){
                    playPauseButton.setText("Play");
                }
                else if(evt.getPropertyName().compareToIgnoreCase("shuffle") ==0){
                    shuffle();
                }
            }
        };
        jTable1.addPropertyChangeListener(pcl2, 1);

        backButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt
            )
            {
                try {
                    backButtonActionPerformed(evt);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        popMenuNewWindow.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try
                {
                    popMenuNewWindowActionPerformed(evt);
                }
                catch (ClassNotFoundException | SQLException ex)
                {
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        popMenuDeletePlaylist.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try
                {
                    popMenuDeletePlaylistActionPerformed(evt);
                }
                catch (ClassNotFoundException | SQLException ex)
                {
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
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
                        jTable1.getListen().play(file, 1);
                        int a = jTable1.getModel().getRow(file);
                        String s = (String)jTable1.getModel().getValueAt(a, 0);
                        try {
                            jTable1.addToRecent(file);
                            addNewRecent();
                        } catch (ClassNotFoundException | SQLException ex) {
                            Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        playPauseButton.setText("Pause");
                        try
                        {
                            jTable1.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
                        }
                        catch (BasicPlayerException ex)
                        {
                            Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });

        jTable1.getTree().addMouseListener(new MouseAdapter()
        {   
            @Override
            public void mouseClicked(MouseEvent evt)
            {
                playPauseButton.requestFocusInWindow();
                evt.getSource();
                if (evt.getClickCount() >= 2)
                {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTable1.getTree().getLastSelectedPathComponent();
                    // A playlist is selected
                    if (node != null)
                    {
                        Object nodeInfo = node.getUserObject();

                        if (node.isLeaf())
                        {
                            try
                            {
                                
                                jTable1.choosePlaylist(nodeInfo.toString());
                                
                            }
                            catch (ClassNotFoundException | SQLException ex)
                            {
                                Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        });

        jTable1.getTree().addTreeSelectionListener(new TreeSelectionListener()
        {
            @Override
            public void valueChanged(TreeSelectionEvent e)
            {
                // Returns the last path element of the selection.
                // This method is useful only when the selection model allows a single selection.
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTable1.getTree().getLastSelectedPathComponent();
                // A playlist is selected
                if (node != null)
                {
                    Object nodeInfo = node.getUserObject();

                    if (node.isLeaf() && nodeInfo != "Library" && nodeInfo != "Playlists")
                    {
                        current = nodeInfo.toString();
                    }
                    else
                    {
                        current = null;
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
            {   playPauseButton.requestFocusInWindow();
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
                menuOpenActionPerformed(evt);
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
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
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

        menuAddPlaylist.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try
                {
                    menuAddPlaylistActionPerformed(evt);
                }
                catch (ClassNotFoundException | SQLException ex)
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

        TimerTask timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                progressBar.setMaximum((int) jTable1.getListen().getTotalTime());
                if (jTable1.getListen().getPlayer().getStatus() == BasicPlayer.PLAYING)
                {
                    progressBar.setValue((int) jTable1.getListen().getCurrentTime());
                    lTimer.setText(convertTime(jTable1.getListen().getCurrentTime()));
                    rTimer.setText(convertTime(jTable1.getListen().getTotalTime() - jTable1.getListen().getCurrentTime()));
                }
                if (jTable1.getListen().getPlayer().getStatus() == BasicPlayer.STOPPED)
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
                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                        200)
                .addComponent(jScrollPane1));

        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane2)
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
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
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
            UnsupportedTagException, InvalidDataException//GEN-FIRST:event_menuAddSongActionPerformed
    {
        if (fc.showOpenDialog(SoundByteView.this) == JFileChooser.APPROVE_OPTION)
        {
            File files = fc.getSelectedFile();
            String name = files.getAbsolutePath();
            jTable1.addDB(name);
        }
    }

    private void menuQuitActionPerformed(ActionEvent evt)
    {
        System.exit(0);
    }

    private void menuDelSongActionPerformed(ActionEvent evt) throws ClassNotFoundException, SQLException
    {
        jTable1.delDB(file);
    }

    private void menuOpenActionPerformed(ActionEvent evt)
    {
        if (fc.showOpenDialog(SoundByteView.this) == JFileChooser.APPROVE_OPTION)
        {
            File files = fc.getSelectedFile();
            jTable1.getListen().play(files.getAbsolutePath(), 1);
            playPauseButton.setText("Pause");
            try
            {
                jTable1.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
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
        jTable1.createPlaylist(s);
        addNewPlaylistButton(s);
    }

    private void skipButtonActionPerformed(ActionEvent evt) throws ClassNotFoundException, SQLException
    {
        if(menuShuffle.isSelected() == false){
            String curr = jTable1.getListen().getSongName();
            int a = jTable1.getModel().getRow(curr);
            int b = jTable1.getRowSorter().convertRowIndexToView(a);
            b++;
            a = jTable1.getRowSorter().convertRowIndexToModel(b);
            String next = (String)jTable1.getModel().getValueAt(a, 5);
            String nullString = "none";
            if (next.compareTo(nullString) != 0)
            {
                jTable1.getListen().play(next, 1);
                jTable1.addToRecent(next);
                addNewRecent();
                int row = jTable1.getModel().getRow(next);
                row = jTable1.getRowSorter().convertRowIndexToView(row);
                jTable1.setRowSelectionInterval(row, row);
                playPauseButton.setText("Pause");
                try
                {
                    jTable1.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
                }
                catch (BasicPlayerException ex)
                {
                    Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else{
            shuffle();
        }
    }

    private void backButtonActionPerformed(ActionEvent evt) throws ClassNotFoundException, SQLException
    {
        String curr = jTable1.getListen().getSongName();
        int a = jTable1.getModel().getRow(curr);
        int b = jTable1.getRowSorter().convertRowIndexToView(a);
        b--;
        String prev = "none";
        if(b>=0){
        a = jTable1.getRowSorter().convertRowIndexToModel(b);
        prev = (String)jTable1.getModel().getValueAt(a, 5);
        }
        String nullString = "none";
        if (prev.compareTo(nullString) != 0)
        {
            jTable1.getListen().play(prev, 1);
            jTable1.addToRecent(prev);
            addNewRecent();
            int row = jTable1.getModel().getRow(prev);
            row = jTable1.getRowSorter().convertRowIndexToView(row);
            jTable1.setRowSelectionInterval(row, row);
            playPauseButton.setText("Pause");
            try
            {
                jTable1.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
            }
            catch (BasicPlayerException ex)
            {
                Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void stopButtonActionPerformed(ActionEvent evt)
    {
        jTable1.getListen().stop();
        if (jTable1.getListen().getPlayer().getStatus() != 0)
        {
            playPauseButton.setText("Play");
        }
    }

    private void playPauseButtonActionPerformed(ActionEvent evt) throws ClassNotFoundException, SQLException
    {
        if (jTable1.getListen().getPlayer().getStatus() != 0)
        {
            if (jTable1.getListen().getSongName().compareToIgnoreCase(file) != 0)
            {
                jTable1.getListen().play(file, 1);
                jTable1.addToRecent(file);
                addNewRecent();
                playPauseButton.setText("Pause");
                try
                {
                    jTable1.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
                }
                catch (BasicPlayerException ex)
                {
                    Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                jTable1.getListen().play(file, 0);
                jTable1.addToRecent(file);
                addNewRecent();
                playPauseButton.setText("Pause");
                try
                {
                    jTable1.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
                }
                catch (BasicPlayerException ex)
                {
                    Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else
        {
            jTable1.getListen().pause();
            playPauseButton.setText("Play");
        }
    }

    private void soundSliderStateChanged(javax.swing.event.ChangeEvent evt)
    {
        if (jTable1.getListen().getPlayer().getStatus() == 0)
        {
            JSlider source = (JSlider) evt.getSource();
            try
            {
                jTable1.getListen().getControl().setGain(source.getValue() / 100.0);
            }
            catch (BasicPlayerException ex)
            {
                Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void popMenuNewWindowActionPerformed(java.awt.event.ActionEvent evt) throws ClassNotFoundException, SQLException
    {
        if (current != null)
        {
            if (isInstantiated == true)
            {
                playlistView.setVisible(true);
                changeNewWindowPlaylist();
                playlistView.setTitle("SoundByte Playlist: " + current);
                jTable1.setLibraryModel();
            }
            else
            {
                isInstantiated = true;
                playlistView = SoundBytePlaylistView.getInstance();

                playlistView.initComponents(current);
                playlistView.addPropertyChangeListener(pcl);
                playlistView.setVisible(true);
                jTable1.setLibraryModel();
            }
        }
    }

    private void changeNewWindowPlaylist() throws ClassNotFoundException, SQLException
    {
        playlistView.changePlaylist(current);
    }

    private void popMenuDeletePlaylistActionPerformed(java.awt.event.ActionEvent evt) throws ClassNotFoundException, SQLException
    {
        if (current != null)
        {
            jTable1.delPlaylist(current);
            popMenuPlaylist.removeAll();
            playlistMenu();
        }
    }

    private void menuPlayActionPerformed(ActionEvent evt) throws ClassNotFoundException, SQLException
    {
        int a = jTable1.getSelectedRow();
        if (file.compareToIgnoreCase("none") != 0 && a != -1)
        {
            jTable1.getListen().play(file, 1);
            jTable1.addToRecent(file);
            addNewRecent();
            playPauseButton.setText("Pause");
            try
            {
                jTable1.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
            }
            catch (BasicPlayerException ex)
            {
                Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(jTable1.getListen().getShuffle() == true){
            shuffle();
        }
        else{
            int row = jTable1.getRowSorter().convertRowIndexToModel(0);
            file = (String)jTable1.getModel().getValueAt(row,5);
            jTable1.getListen().play(file, 1);
            jTable1.addToRecent(file);
            addNewRecent();
            playPauseButton.setText("Pause");
        }
    }

    private void menuCurrentActionPerformed(ActionEvent evt)
    {
        if(jTable1.getListen().getStatus() == 0){
            String current = jTable1.getListen().getSongName();
            String nullString = "none";
            if (current.compareTo(nullString) != 0)
            {
                current = jTable1.getListen().getSongName();
                int row = jTable1.getModel().getRow(current);
                row = jTable1.getRowSorter().convertRowIndexToView(row);
                jTable1.setRowSelectionInterval(row, row);
                Rectangle r = jTable1.getCellRect(row, 0, true);
                jTable1.scrollRectToVisible(r);
            }
        }
        else{
            int row = jTable1.getModel().getRow(file);
            row = jTable1.getRowSorter().convertRowIndexToView(row);
            jTable1.setRowSelectionInterval(row, row);
            Rectangle r = jTable1.getCellRect(row, 0, true);
            jTable1.scrollRectToVisible(r);
        }
    }

    private void menuUpActionPerformed(ActionEvent evt)
    {
        soundSlider.setValue(soundSlider.getValue() + 5);
        if (jTable1.getListen().getPlayer().getStatus() == BasicPlayer.PLAYING)
        {
            try
            {
                jTable1.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
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
        if (jTable1.getListen().getPlayer().getStatus() == BasicPlayer.PLAYING)
        {
            try
            {
                jTable1.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
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
            jTable1.getListen().setShuffle(true);
            if(jTable1.getListen().getStatus() != 0){
                shuffle();
            }
        }
        else{
            jTable1.getListen().setShuffle(false);
        }
        jTable1.updateButton("shuffle1");
    }

    private void menuRepeatActionPerformed(ActionEvent evt)
    {
        JCheckBoxMenuItem box = (JCheckBoxMenuItem)evt.getSource();
        if(box.isSelected()){
            jTable1.getListen().setRepeat(true);
        }
        else{
            jTable1.getListen().setRepeat(false);
        }
        jTable1.updateButton("repeat1");
    }

    /*=========End Listener Implementations===================================*/
    private void recentMenu() throws ClassNotFoundException, SQLException
    {
        
        int b = jTable1.countRecent();
        if(b>0){
            menuRecentSub = new JMenuItem[b];
            String[] a = new String[b];
            a = jTable1.recentNames(b);
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
    private void playlistMenu() throws ClassNotFoundException, SQLException
    {
        int b = jTable1.countPlaylists();
        popMenuSubPlaylist = new JMenuItem[b];
        String[] a = new String[b];
        a = jTable1.playlistNames();
        for (int c = 0; c < b; c++)
        {
            popMenuSubPlaylist[c] = new JMenuItem(a[c]);
            popMenuPlaylist.add(popMenuSubPlaylist[c]);
            popMenuSubPlaylist[c].addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent evt)
                {
                    try
                    {
                        popMenuPlaylistSelect(evt);
                    }
                    catch (ClassNotFoundException | SQLException | IOException | UnsupportedTagException | InvalidDataException ex)
                    {
                        Logger.getLogger(SoundByteView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
        popMenuPlaylist.getPopupMenu().pack();
    }

    private void addNewPlaylistButton(String n) throws ClassNotFoundException, SQLException
    {
        popMenuPlaylist.removeAll();
        playlistMenu();
    }
    
    private void addNewRecent() throws SQLException, ClassNotFoundException{
        menuRecent.removeAll();
        recentMenu();
    }

    private void popMenuPlaylistSelect(ActionEvent evt) throws
            ClassNotFoundException, SQLException, IOException,
            UnsupportedTagException, InvalidDataException
    {
        if (jTable1.isLibraryEmpty() == false)
        {
            jTable1.addToPlaylist(file, ((JMenuItem) evt.getSource()).getText());
        }
    }
    private void menuRecentSelect(ActionEvent evt, int a) throws
            ClassNotFoundException, SQLException, IOException,
            UnsupportedTagException, InvalidDataException
    {
        file = jTable1.getRecent(a);
        jTable1.getListen().play(file, 1);
        jTable1.addToRecent(file);
        addNewRecent();
        int row = jTable1.getModel().getRow(file);
        String s = (String) jTable1.getModel().getValueAt(row, 0);
        
            playPauseButton.setText("Pause");
            try
            {
                jTable1.getListen().getControl().setGain((soundSlider.getValue()) / 100.0);
            }
            catch (BasicPlayerException ex)
            {
                Logger.getLogger(SoundBytePlaylistView.class.getName()).log(Level.SEVERE, null, ex);
            }        
    }
    
    private void popMenuArtist(ActionEvent evt) throws ClassNotFoundException, SQLException{
        JCheckBoxMenuItem box = (JCheckBoxMenuItem)evt.getSource();
        if(box.isSelected()){
            jTable1.getColumnModel().getColumn(1).setMaxWidth(maxWidth);
            jTable1.getColumnModel().getColumn(1).setMinWidth(minWidth);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(preferredWidth);
            columnsDisplayed[0]= true;
            jTable1.updateColumns(1, true);
        }
        else{
            jTable1.getColumnModel().getColumn(1).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(1).setMinWidth(0);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(0);
            columnsDisplayed[0] = false;
            jTable1.updateColumns(1, false);
        }
    }
    
    private void popMenuAlbum(ActionEvent evt) throws ClassNotFoundException, SQLException{
        JCheckBoxMenuItem box = (JCheckBoxMenuItem)evt.getSource();
        if(box.isSelected()){
            jTable1.getColumnModel().getColumn(2).setMaxWidth(maxWidth);
            jTable1.getColumnModel().getColumn(2).setMinWidth(minWidth);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(preferredWidth);
            columnsDisplayed[0]= true;
            jTable1.updateColumns(2, true);
        }
        else{
            jTable1.getColumnModel().getColumn(2).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(2).setMinWidth(0);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(0);
            columnsDisplayed[1] = false;
            jTable1.updateColumns(2, false);
        }
    }
    
    private void popMenuGenre(ActionEvent evt) throws ClassNotFoundException, SQLException{
        JCheckBoxMenuItem box = (JCheckBoxMenuItem)evt.getSource();
        if(box.isSelected()){
            jTable1.getColumnModel().getColumn(3).setMaxWidth(maxWidth);
            jTable1.getColumnModel().getColumn(3).setMinWidth(minWidth);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(preferredWidth);
            columnsDisplayed[0]= true;
            jTable1.updateColumns(3, true);
        }
        else{
            jTable1.getColumnModel().getColumn(3).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(3).setMinWidth(0);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(0);
            columnsDisplayed[2] = false;
            jTable1.updateColumns(3, false);
        }
    }
    
    private void popMenuLength(ActionEvent evt) throws ClassNotFoundException, SQLException{
        JCheckBoxMenuItem box = (JCheckBoxMenuItem)evt.getSource();
        if(box.isSelected()){
            jTable1.getColumnModel().getColumn(4).setMaxWidth(maxWidth);
            jTable1.getColumnModel().getColumn(4).setMinWidth(minWidth);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(preferredWidth);
            columnsDisplayed[0]= true;    
            jTable1.updateColumns(4, true);
        }
        else{
            jTable1.getColumnModel().getColumn(4).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(4).setMinWidth(0);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(0);
            columnsDisplayed[3] = false;
            jTable1.updateColumns(4, false);
        }
    }
    
    private void popMenuComment(ActionEvent evt) throws ClassNotFoundException, SQLException{
        JCheckBoxMenuItem box = (JCheckBoxMenuItem)evt.getSource();
       if(box.isSelected()){
            jTable1.getColumnModel().getColumn(6).setMaxWidth(maxWidth);
            jTable1.getColumnModel().getColumn(6).setMinWidth(minWidth);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(preferredWidth);
            columnsDisplayed[0]= true; 
            jTable1.updateColumns(6, true);
        }
        else{
            jTable1.getColumnModel().getColumn(6).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(6).setMinWidth(0);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(0);
            columnsDisplayed[4] = false;
            jTable1.updateColumns(6, false);
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
            else{
                jTable1.getColumnModel().getColumn(i+1).setMaxWidth(maxWidth);
                jTable1.getColumnModel().getColumn(i+1).setMinWidth(minWidth);
                jTable1.getColumnModel().getColumn(i+1).setPreferredWidth(preferredWidth);
                if(i == 0){artist.setSelected(true);}
                if(i == 1){album.setSelected(true);}
                if(i == 2){genre.setSelected(true);}
                if(i == 3){length.setSelected(true);}                
            }
        }
        if(columnsDisplayed[4] == false){
            jTable1.getColumnModel().getColumn(6).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(6).setMinWidth(0);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(0);
            comment.setSelected(false);
        }
        else{
            jTable1.getColumnModel().getColumn(6).setMaxWidth(maxWidth);
            jTable1.getColumnModel().getColumn(6).setMinWidth(minWidth);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(preferredWidth);
            comment.setSelected(true);
        }
    }
    
    private void shuffle(){
        int a = jTable1.getLibraryModel().getRowCount();
        int b;
        if(a>0){
            b = rand.nextInt(a);
            String s = (String)jTable1.getLibraryModel().getValueAt(b, 5);
            jTable1.getListen().play(s, 1);
            s = (String)jTable1.getLibraryModel().getValueAt(b, 0);
            if(jTable1.getIsPlaylist() == false){
                b = jTable1.getRowSorter().convertRowIndexToView(b);
                jTable1.setRowSelectionInterval(b, b);
            }
            else{
                jTable1.clearSelection();
            }
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
    


    public static void main(String[] args)
    {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    new SoundByteView().setVisible(true);
                }
                catch (ClassNotFoundException | SQLException ex)
                {
                    Logger.getLogger(SoundByteView.class
                            .getName()).log(Level.SEVERE, null, ex);

                }
            }
        }
        );
    }

    // Variables declaration
    private String file;
    private JButton backButton;
    private SoundByteDatabase jTable1;
    private JMenu jMenu1;
    private JMenu menuControls;
    private JMenuBar jMenuBar1;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JScrollBar jScrollBar1;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
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
    private JMenuItem menuAddPlaylist;
    private JMenuItem[] popMenuSubPlaylist;
    private JMenuItem[] menuRecentSub;
    private JMenu popMenuPlaylist;
    private JButton playPauseButton;
    private JProgressBar progressBar;
    private JButton skipButton;
    private JButton stopButton;
    private JSlider soundSlider;
    private JPopupMenu jPopMenu1;
    private JPopupMenu treePopMenu;
    private JPopupMenu displayColumns;
    private JMenuItem popMenuNewWindow;
    private JMenuItem popMenuDeletePlaylist;
    private JLabel lTimer;
    private JLabel rTimer;
    private String current;
    private SoundBytePlaylistView playlistView;
    private boolean isInstantiated;
    private PropertyChangeListener pcl;
    private PropertyChangeListener pcl2;
    private SoundByteModel tableModel;
    private TableRowSorter rowSorter;
    private int maxWidth;
    private int minWidth;
    private int preferredWidth;
    private boolean[] columnsDisplayed;
    private Random rand;
}
