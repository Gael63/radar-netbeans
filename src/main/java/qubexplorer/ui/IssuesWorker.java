package qubexplorer.ui;

import java.util.List;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;
import org.openide.windows.WindowManager;
import qubexplorer.IssuesContainer;
import qubexplorer.RadarIssue;
import qubexplorer.filter.IssueFilter;

/**
 *
 * @author Victor
 */
public class IssuesWorker extends SonarQubeWorker<List<RadarIssue>, Void> {
    private Project project;
    private ProgressHandle handle;
    private IssueFilter[] filters;
    private IssuesContainer issuesContainer;
    
    public IssuesWorker(IssuesContainer container, Project project, String resourceKey, IssueFilter... filters) {
        super(resourceKey);
        this.project=project;
        this.filters=filters;
        this.issuesContainer=container;
        init();
    }

    private void init() {
        handle = ProgressHandleFactory.createHandle("Sonar");
        handle.start();
        handle.switchToIndeterminate();
    }

    @Override
    protected List<RadarIssue> doInBackground() throws Exception {
        return issuesContainer.getIssues(getAuthentication(), getResourceKey(), filters);
    }

    @Override
    protected void success(List<RadarIssue> result) {
        SonarIssuesTopComponent sonarTopComponent = (SonarIssuesTopComponent) WindowManager.getDefault().findTopComponent("SonarIssuesTopComponent");
        sonarTopComponent.open();
        sonarTopComponent.requestVisible();
        sonarTopComponent.setProject(project);
        sonarTopComponent.showIssues(filters, result.toArray(new RadarIssue[0]));
    }

    @Override
    protected SonarQubeWorker createCopy() {
        return new IssuesWorker(issuesContainer, project, getResourceKey(), filters);
    }

    @Override
    protected void finished() {
        handle.finish();
    }
    
}
