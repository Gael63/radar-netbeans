package qubexplorer.ui;

import java.awt.Component;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.openide.util.Exceptions;
import org.sonar.wsclient.services.Rule;
import qubexplorer.Severity;
import qubexplorer.Summary;
import qubexplorer.runner.SonarRunnerSummary;

/**
 *
 * @author Victor
 */
public class SummaryModel extends AbstractTreeTableModel {

    public SummaryModel(Summary summary) {
        super(summary);
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(Object node, int i) {
        Summary summary = getSummary();
        if(node instanceof Summary){
            if(i == 0){
                return "Issues";
            }else{
                return summary.getCount();
            }
        }else if (node instanceof Severity) {
            if (i == 0) {
                return ((Severity) node).name();
            } else {
                return summary.getCount((Severity) node);
            }
        } else if (node instanceof Rule) {
           if(i == 0) {
               return ((Rule)node).getDescription();
           }else{
               return summary.getCount((Rule) node);
           }
        } else {
            return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "";
        } else {
            return "Count";
        }
    }
    
    @Override
    public Object getChild(Object parent, int i) {
        if (parent instanceof Summary) {
            return Severity.values()[i];
        } else if (parent instanceof Severity) {
            Rule[] rules = getSummary().getRules((Severity) parent).toArray(new Rule[0]);
            Arrays.sort(rules, new Comparator<Rule>() {

                @Override
                public int compare(Rule t, Rule t1) {
                    int count1=getSummary().getCount(t);
                    int count2=getSummary().getCount(t1);
                    return count2 - count1;
                }
                
            });
            return rules[i];
        } else {
            throw new AssertionError("Unknown parent object");
        }
    }

    public Summary getSummary() {
        return (Summary) getRoot();
    }
    
    

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof Summary) {
            return Severity.values().length;
        } else if (parent instanceof Severity) {
            return getSummary().getRules((Severity) parent).size();
        } else {
            return 0;
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object o1) {
        if (parent instanceof Summary) {
            return Arrays.asList(Severity.values()).indexOf(o1);
        } else if (parent instanceof Severity) {
            return -1;
        } else {
            return -1;
        }
    }

}
