package qubexplorer.ui;

import java.io.IOException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.WindowManager;
import org.sonar.runner.api.PrintStreamConsumer;
import qubexplorer.runner.SonarRunnerProccess;
import qubexplorer.runner.SonarRunnerResult;
import qubexplorer.runner.SourcesNotFoundException;
import qubexplorer.ui.options.SonarQubeOptionsPanel;

/**
 *
 * @author Victor TODO: process result
 */
public class SonarRunnerWorker extends SonarQubeWorker<SonarRunnerResult, Void> {

    private Project project;
    private String sonarUrl;
    private ProgressHandle handle;
    private InputOutput io;

    public SonarRunnerWorker(Project project, String sonarUrl) {
        super(null);
        this.project = project;
        this.sonarUrl = sonarUrl;
        setServerUrl(sonarUrl);
        init();
    }

    private void init() {
        handle = ProgressHandleFactory.createHandle("Sonar-runner");
        handle.start();
        handle.switchToIndeterminate();
        io = IOProvider.getDefault().getIO("Sonar-runner", false);
        try {
            io.getOut().reset();
            io.getErr().reset();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        io.select();
        io.getOut().println("Starting sonar-runner");
    }

    @Override
    protected SonarRunnerResult doInBackground() throws Exception {
        PrintStreamConsumer out = new PrintStreamConsumer(null){

            @Override
            public void consumeLine(String line) {
                io.getOut().println(line);
            }
            
        };
        
        PrintStreamConsumer err = new PrintStreamConsumer(null){

            @Override
            public void consumeLine(String line) {
                io.getErr().println(line);
            }
            
        };
        SonarRunnerProccess sonarRunnerProccess = new SonarRunnerProccess(sonarUrl, project);
        sonarRunnerProccess.setAnalysisMode(SonarRunnerProccess.AnalysisMode.valueOf(NbPreferences.forModule(SonarQubeOptionsPanel.class).get("runner.analysisMode", "Preview").toUpperCase()));
        sonarRunnerProccess.setOutConsumer(out);
        sonarRunnerProccess.setErrConsumer(err);
        return sonarRunnerProccess.executeRunner(getAuthentication());
    }

    @Override
    protected void success(SonarRunnerResult result) {
        SonarIssuesTopComponent sonarTopComponent = (SonarIssuesTopComponent) WindowManager.getDefault().findTopComponent("SonarIssuesTopComponent");
        sonarTopComponent.setProjectContext(new ProjectContext(project, getProjectKey()));
        sonarTopComponent.setIssuesContainer(result);
        sonarTopComponent.open();
        sonarTopComponent.requestVisible();
        sonarTopComponent.showSummary(result.getSummary());
    }

    @Override
    protected void error(Throwable cause) {
        if(cause instanceof SourcesNotFoundException) {
            String message = org.openide.util.NbBundle.getMessage(SonarRunnerWorker.class, "SourcesNotFound");
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(message, NotifyDescriptor.WARNING_MESSAGE));
        }else{
            io.getErr().println("Error executing sonar-runner");
            Exceptions.printStackTrace(cause);
        }
    }

    @Override
    protected void finished() {
        handle.finish();
        io.getOut().close();
        io.getErr().close();
    }

    @Override
    protected SonarQubeWorker createCopy() {
        return new SonarRunnerWorker(project, sonarUrl);
    }

}
