package qubexplorer;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Victor
 */
public class SonarMvnProject implements SonarQubeProjectConfiguration {

    private final Model model;

    public SonarMvnProject(Project project) throws MvnModelInputException {
        this.model = createModel(project);
    }

    @Override
    public String getName() {
        return model.getName() != null ? model.getName() : model.getArtifactId();
    }

    @Override
    public ResourceKey getKey() {
        String groupId = model.getGroupId();
        if (groupId == null && model.getParent() != null) {
            groupId = model.getParent().getGroupId();
        }
        return new ResourceKey(groupId, model.getArtifactId());
    }

    @Override
    public String getVersion() {
        String version = model.getVersion();
        if (version == null && model.getParent() != null) {
            version = model.getParent().getVersion();
        }
        return version;
    }

    public static Model createModel(Project project) throws MvnModelInputException {
        FileObject pomFile = getPomFileObject(project);
        MavenXpp3Reader mavenreader = new MavenXpp3Reader();
        try (Reader reader = new InputStreamReader(pomFile.getInputStream())) {
            Model model = mavenreader.read(reader);
            model.setPomFile(new File(pomFile.getPath()));
            return model;
        } catch (XmlPullParserException | IOException ex) {
            throw new MvnModelInputException(ex);
        }
    }

    public static boolean isMvnProject(Project project) {
        return getPomFileObject(project) != null;
    }
    
    public static FileObject getPomFileObject(Project project) {
        return project.getProjectDirectory().getFileObject("pom.xml");
    }

    public static MavenProject createMavenProject(Project project) throws MvnModelInputException {
        return new MavenProject(createModel(project));
    }
    
}
