package org.scalatest.tools.maven;

import org.codehaus.doxia.sink.Sink;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.File;
import java.util.List;
import java.util.Locale;

import static org.scalatest.tools.maven.MojoUtils.concat;
import static org.scalatest.tools.maven.MojoUtils.fileRelativeTo;
import static org.scalatest.tools.maven.MojoUtils.reporterArg;

/**
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
     * Comma separated list of filereporters. A filereporter consists of an optional
     * configuration and a mandatory filename, separated by a whitespace. E.g <code>all.txt,XE ignored_and_pending.txt</code>
     * For more info on configuring reporters, see the scalatest documentation.
     * @parameter expression="${filereports}"
     */
    private String filereports;


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
                filereports()
        );
    }

    private List<String> filereports() {
        return reporterArg("-f", "WD " + getOutputName() + ".html", fileRelativeTo(reportingOutputDirectory));
    }

    public String getOutputName() {
        return "scalatest-output";
    }

    public String getCategoryName() {
        return CATEGORY_PROJECT_REPORTS;
    }

    public String getName(Locale locale) {
        // need to externalize
        return "ScalaTest Output";
    }

    public String getDescription(Locale locale) {
        // need to externalize
        return "The output of the ScalaTest reporter";
    }

    public void setReportOutputDirectory(File outputDirectory) {
        reportingOutputDirectory = outputDirectory;
    }

    public File getReportOutputDirectory() {
        return outputDirectory;
    }

    public boolean isExternalReport() {
        return true;
    }

    public boolean canGenerateReport() {
        return true;
    }
}