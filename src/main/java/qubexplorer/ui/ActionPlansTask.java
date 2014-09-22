package qubexplorer.ui;

import java.util.List;
import org.openide.windows.WindowManager;
import org.sonar.wsclient.issue.ActionPlan;
import qubexplorer.server.SonarQube;
import qubexplorer.ui.ProjectContext;
import qubexplorer.ui.SonarIssuesTopComponent;
import qubexplorer.ui.task.Task;

/**
 *
 * @author Victor
 */
public class ActionPlansTask extends Task<List<ActionPlan>>{
    private SonarQube sonarQube;

    public ActionPlansTask(SonarQube sonarQube, ProjectContext projectContext) {
        super(projectContext, sonarQube.getServerUrl());
    }

    @Override
    public List<ActionPlan> execute() {
        return sonarQube.getActionPlans(getUserCredentials(), getProjectContext().getProjectKey());
    }

    @Override
    protected void success(List<ActionPlan> result) {
        SonarIssuesTopComponent issuesTopComponent = (SonarIssuesTopComponent) WindowManager.getDefault().findTopComponent("SonarIssuesTopComponent");
        issuesTopComponent.setActionPlans(result);
        issuesTopComponent.open();
        issuesTopComponent.requestVisible();
    }
    
}
