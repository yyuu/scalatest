package org.scalatest.tools.maven;

import org.codehaus.doxia.sink.Sink;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.scalatest.tools.maven.MojoUtils.*;

/**
 * A reporting mojo to capture the ScalaTest output as a file that integrates into the Maven site of a project.
 *
 * @author Sean Griffin
 * @phase site
 * @goal reporter
 */
public class ReporterMojo extends AbstractScalaTestMojo implements MavenReport {

    /**
     * Directory where reports will go.
     *
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     * @readonly
     */
    private File reportingOutputDirectory;

    /**
     * Consists of an optional configuration parameters for the file reporter.
     * For more info on configuring reporters, see the scalatest documentation.
     * @parameter expression="${fileReporterOptions}"
     */
    private String fileReporterOptions;


    public void execute() throws MojoExecutionException, MojoFailureException {
        // no op, Maven doesn't even call this method but I have to implement it because it's on the interface.
    }

    public void generate(Sink sink, Locale locale) throws MavenReportException {
        try {
            runScalaTest(configuration());
        }
        catch (RuntimeException e) {
            throw new MavenReportException("Failure generating ScalaTest report", e);
        }
    }

    private String[] configuration() {
        return concat(
                sharedConfiguration(),
                fileReporterConfig()
        );
    }

    private List<String> fileReporterConfig() {
        return reporterArg("-f", fileReporterOptions + " " + getOutputName() + ".html", fileRelativeTo(reportingOutputDirectory));
    }

    public String getOutputName() {
        return "scalatest-output";
    }

    public String getCategoryName() {
        return CATEGORY_PROJECT_REPORTS;
    }

    public String getName(Locale locale) {
        return getLocalizedString(locale, "reporter.mojo.name");
    }

    public String getDescription(Locale locale) {
        return getLocalizedString(locale, "reporter.mojo.description");
    }

    public void setReportOutputDirectory(File outputDirectory) {
        reportingOutputDirectory = outputDirectory;
    }

    public File getReportOutputDirectory() {
        return reportingOutputDirectory;
    }

    public boolean isExternalReport() {
        return true;
    }

    public boolean canGenerateReport() {
        return true;
    }

    private String getLocalizedString(Locale locale, String resourceKey) {
        return ResourceBundle.getBundle("mojoResources", locale, getClass().getClassLoader()).getString(resourceKey);
    }
}