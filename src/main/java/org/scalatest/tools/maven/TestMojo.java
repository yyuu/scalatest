package org.scalatest.tools.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import static org.scalatest.tools.maven.MojoUtils.*;

import java.io.File;
import java.util.Collections;
import static java.util.Collections.singletonList;
import java.util.List;

/**
 * Provides a bridge between Maven and the command-line form of ScalaTest's Runner.
 * Many of the configuration options available on this goal
 * are directly reflected in the Runner ScalaDoc on http://www.scalatest.org.
 * 
 * @author Jon-Anders Teigen
 * @phase test
 * @goal test
 */
public class TestMojo extends AbstractScalaTestMojo {

    /**
     * @parameter expression="${project.build.directory}/scalatest-reports"
     * @required
     */
    File reportsDirectory;

    /**
     * Set to true to skip execution of tests.
     * @parameter expression="${skipTests}"
     */
    boolean skipTests;

    /**
     * Set to true to avoid failing the build when tests fail
     * @parameter expression="${maven.test.failure.ignore}"
     */
    boolean testFailureIgnore;

    /**
     * Comma separated list of filereporters. A filereporter consists of an optional
     * configuration and a mandatory filename, separated by a whitespace. E.g <code>all.txt,XE ignored_and_pending.txt</code>
     * For more info on configuring reporters, see the scalatest documentation.
     * @parameter expression="${filereports}"
     */
    String filereports;

    /**
     * Comma separated list of reporters. A reporter consist of an optional configuration
     * and a mandatory reporter classname, separated by whitespace. The reporter classname
     * must be the fully qualified name of a class extending <code>org.scalatest.Reporter</code>
     * E.g <code>C my.SuccessReporter,my.EverythingReporter</code>
     * For more info on configuring reporters, see the scalatest documentation.
     * @parameter expression="${reporters}"
     */
    String reporters;

    /**
     * Comma separated list of junitxml. A junitxml consists of an optional configuration
     * and a mandatory directory for the xml files, separated by whitespace.
     * For more info on configuring reporters, see the scalatest documentation.
     * @parameter expression="${junitxml}"
     */
    String junitxml;

    /**
     * Configuration for logging to stdout. (This logger is always enabled)
     * For more info on configuring reporters, see the scalatest documentation.
     * @parameter expression="${stdout}"
     */
    String stdout;

    /**
     * Configuration for logging to stderr. It is disabled by default, but will be enabled
     * when configured. Empty configuration just means enable.
     * For more info on configuring reporters, see the scalatest documentation.
     * @parameter expression="${stderr}"
     */
    String stderr;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipTests) {
            getLog().info("Tests are skipped.");
        } else {
            if (!runScalaTest(configuration()) && !testFailureIgnore) {
                throw new MojoFailureException("There are test failures");
            }
        }
    }

    String[] configuration() {
        return concat(
                sharedConfiguration(),
                stdout(),
                stderr(),
                filereports(),
                reporters(),
                junitxml()
        );
    }

    // These private methods create the relevant portion of the command line
    // to pass to Runner based on the corresponding Maven configuration parameter.
    private List<String> stdout() {
        return singletonList(stdout == null ? "-o" : "-o" + stdout);
    }

    private List<String> stderr() {
        return stderr == null ? Collections.<String>emptyList() : singletonList("-e" + stderr);
    }

    private List<String> filereports() {
        return reporterArg("-f", filereports, fileRelativeTo(reportsDirectory));
    }

    private List<String> reporters() {
        return reporterArg("-r", reporters, passThrough);
    }

    private List<String> junitxml(){
        return reporterArg("-u", junitxml, dirRelativeTo(reportsDirectory));
    }
}
