package qubexplorer.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.netbeans.api.project.Project;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import qubexplorer.MvnModelInputException;
import qubexplorer.Summary;
import qubexplorer.filter.IssueFilter;
import qubexplorer.server.SonarQube;
import qubexplorer.ui.task.TaskExecutor;

@ActionID(
        category = "Build",
        id = "qubexplorer.ui.SonarDialogAction")
@ActionRegistration(
        displayName = "#CTL_SonarDialogAction")
@Messages("CTL_SonarDialogAction=Get Issues from Server")
@ActionReferences(value = {
    @ActionReference(path = "Projects/Actions", position = 8962, separatorBefore = 8956, separatorAfter = 8968),
    @ActionReference(path = "Menu/Source", position = 8962, separatorBefore = 8956, separatorAfter = 8968)})
public final class ServerIssuesAction implements ActionListener {

    private final Project context;

    public ServerIssuesAction(Project context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        try {
            final ProjectContext projectContext = new ProjectContext(context, SonarQube.toResource(context));
            final SonarQube sonarQube = SonarQubeFactory.createForDefaultServerUrl();
            TaskExecutor.execute(new SummaryTask(sonarQube, projectContext, new IssueFilter[0]){

                @Override
                protected void success(Summary summary) {
                    super.success(summary);
                    TaskExecutor.execute(new ActionPlansTask(sonarQube, projectContext));
                }
                
            });
        } catch (MvnModelInputException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
