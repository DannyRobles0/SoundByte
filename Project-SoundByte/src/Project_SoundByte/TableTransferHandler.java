package Project_SoundByte;

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;

class TableTransferHandler extends TransferHandler
{

    private int[] rows = null;
    private int addIndex = -1; //Location where items were added
    private int addCount = 0;  //Number of items added.
    private final DataFlavor localObjectFlavor;
    private Object[] transferedObjects = null;
    private JComponent source = null;

    public TableTransferHandler()
    {
        localObjectFlavor = new ActivationDataFlavor(
                Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
    }

    @Override
    protected Transferable createTransferable(JComponent c)
    {
        source = c;
        SoundByteDatabase table = (SoundByteDatabase) c;

        rows = table.getSelectedRows();
        String[] s = new String[rows.length];
        for (int i = 0; i < rows.length; i++)
        {
            s[i] = (String) table.getValueAt(rows[i], 5);
        }
        return new DataHandler(s, localObjectFlavor.getMimeType());
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info)
    {
        JTable t = (JTable) info.getComponent();
        boolean b = info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
        //XXX bug?
        t.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
        return b;
    }

    @Override
    public int getSourceActions(JComponent c)
    {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info)
    {
        System.out.println("did it!!");
        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable t, int act)
    {
        cleanup(c, act == MOVE);
    }

    private void cleanup(JComponent src, boolean remove)
    {
        if (remove && rows != null)
        {
            JTable table = (JTable) src;
            src.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            if (addCount > 0)
            {
                for (int i = 0; i < rows.length; i++)
                {
                    if (rows[i] >= addIndex)
                    {
                        rows[i] += addCount;
                    }
                }
            }
            for (int i = rows.length - 1; i >= 0; i--)
            {
                model.removeRow(rows[i]);
            }
        }
        rows = null;
        addCount = 0;
        addIndex = -1;
    }
}
