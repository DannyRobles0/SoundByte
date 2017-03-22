package Project_SoundByte;

import javax.swing.table.AbstractTableModel;

public class SoundByteModel extends AbstractTableModel
{

    private static SoundByteModel model;
    private String[][] items;
    private final String[] columns;

    public SoundByteModel(String[][] items, String[] columns)
    {
        this.items = items;
        this.columns = columns;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return items[rowIndex][columnIndex];
    }

    @Override
    public int getRowCount()
    {
        return items.length;
    }

    @Override
    public int getColumnCount()
    {
        return 7;
    }

    @Override
    public String getColumnName(int column)
    {
        return columns[column];
    }

    public String getNext(String song)
    {
        String next = "none";
        for (int i = 0; i < items.length; i++)
        {
            // find row of current song
            if (items[i][5].compareTo(song) == 0)
            {
                // if current song is the last, return null
                if (i == items.length - 1)
                {
                    return "none";
                }// else return next song
                else
                {
                    return items[i + 1][5];
                }
            }
        }
        return next;
    }

    public String getPrev(String song)
    {
        int row = 0;
        String prev = "none";
        String current = song;
        for (int i = 0; i < items.length; i++)
        {
            //find row of current song
            if (items[i][5].compareTo(song) == 0)
            {
                //if current song is the last, return null
                if (i == 0)
                {
                    return "none";
                } //else return next song
                else
                {
                    return items[i - 1][5];
                }
            }
        }
        return prev;
    }

    public void addList(String[][] items)
    {
        this.items = items;
        this.fireTableDataChanged();
    }

    public int getRow(String song)
    {
        for (int row = 0; row < items.length; row++)
        {
            //find row of current song
            if (items[row][5].compareTo(song) == 0)
            {
                return row;
            }
        }
        return 0;
    }

    public static SoundByteModel getInstance(String[][] items, String[] columns)
    {
        if (model == null)
        {
            model = new SoundByteModel(items, columns);
        }
        return model;
    }
}
