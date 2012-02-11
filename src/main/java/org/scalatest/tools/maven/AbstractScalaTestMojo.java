package org.scalatest.tools.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineTimeOutException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

import static org.scalatest.tools.maven.MojoUtils.*;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import static java.util.Collections.singletonList;

import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.net.URL;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Provides the base for all mojos.
 *
 * @author Jon-Anders Teigen
 * @author Sean Griffin
 * @author Mike Pilquist
 * @author Bill Venners
 *
 * @requiresDependencyResolution test
 */
abstract class AbstractScalaTestMojo extends AbstractMojo {
    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    MavenProject project;

    /**
     * @parameter expression="${project.testClasspathElements}"
     * @required
     * @readOnly
     */
    List<String> testClasspathElements;

    /**
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     * @readOnly
     */
    File testOutputDirectory;

    /**
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readOnly
     */
    File outputDirectory;

    /**
     * Comma separated list of additional elements to be added
     * to the ScalaTest runpath. <code>${project.build.outputDirectory}</code> and
     * <code>${project.build.testOutputDirectory}</code> are included by default
     * @parameter expression="${runpath}"
     */
    String runpath;

    /**
     * Comma separated list of suites to be executed
     * @parameter expression="${suites}"
     */
    String suites;

    /**
     * Comma separated list of tags to include
     * @parameter expression="${tagsToInclude}"
     */
    String tagsToInclude;

    /**
     * Comma separated list of tags to exclude
     * @parameter expression="${tagsToExclude}"
     */
    String tagsToExclude;

    /**
     * Comma separated list of configuration parameters to pass to ScalaTest.
     * The parameters must be of the format &lt;key&gt;=&lt;value&gt;. E.g <code>foo=bar,monkey=donkey</code>
     * @parameter expression="${config}"
     */
    String config;

    /**
     * Set to true to run suites concurrently
     * @parameter expression="${parallel}"
     */
    boolean parallel;

    /**
     * Comma separated list of packages containing suites to execute
     * @parameter expression="${membersOnlySuites}"
     */
    String membersOnlySuites;

// TODO: Change this to wildcard and membersOnly
    /**
     * Comma separated list of wildcard suite names to execute
     * @parameter expression="${wildcardSuites}"
     */
    String wildcardSuites;

    /**
     * Comma separated list of testNG xml files to execute
     * @parameter expression="${testNGXMLFiles}"
     */
    String testNGConfigFiles;

    /**
     * Comma separated list of JUnit suites/tests to execute
     * @parameter expression="${junitClasses}"
     */
    String jUnitClasses;

    /**
     * Option to specify the forking mode. Can be "never" or "once". "always", which would
     * fork for each test-class, may be supported later.
     *
     * @parameter expression="${forkMode}" default-value="once"
     */
    String forkMode;

    /**
     * Option to specify additional JVM options to pass to the forked process.
     *
     * @parameter expression="${argLine}"
     */
    String argLine;

    /**
     * Additional environment variables to pass to the forked process.
     *
     * @parameter
     */
    Map<String, String> environmentVariables;

    /**
     * Additional system properties to pass to the forked process.
     *
     * @parameter
     */
    Map<String, String> systemProperties;

    /**
     * Option to specify whether the forked process should wait at startup for a remote debugger to attach.
     *
     * <p>If set to <code>true</code>, the forked process will suspend at startup and wait for a remote
     * debugger to attach to the configured port.</p>
     *
     * @parameter expression="${debugForkedProcess}" default-value="false"
     */
    boolean debugForkedProcess;

    /**
     * JVM options to pass to the forked process when <code>debugForkedProcess</code> is true.
     *
     * <p>If set to a non-empty value, the standard debug arguments are replaced by the specified arguments.
     * This allows customization of how remote debugging is done, without having to reconfigure the JVM
     * options in <code>argLine</code>.
     *
     * @parameter expression="${debugArgLine}"
     */
    String debugArgLine;

    /**
     * Port to listen on when debugging the forked process.
     *
     * @parameter expression="${debuggerPort}" default-value="5005"
     */
    int debuggerPort = 5005;

    /**
     * Timeout in seconds to allow the forked process to run before killing it and failing the test run.
     *
     * <p>If set to 0, process never times out.</p>
     *
     * @parameter expression="${timeout}" default-value="0"
     */
    int forkedProcessTimeoutInSeconds = 0;

    /**
     * Whether or not to log the command used to launch the forked process.
     *
     * @parameter expression="${logForkedProcessCommand}" default-value="false"
     */
    boolean logForkedProcessCommand;


    // runScalaTest is called by the concrete mojo subclasses  TODO: make it protected and others too
    // Returns true if all tests pass
    boolean runScalaTest(String[] args) throws MojoFailureException {
        getLog().debug(Arrays.toString(args));
        if (forkMode.equals("never")) {
            return runWithoutForking(args);
        }
        else {
            if (!forkMode.equals("once")) {
                getLog().error("Invalid forkMode: \"" + forkMode + "\"; Using once instead.");
            }
            return runForkingOnce(args);
        }
    }

    // Returns true if all tests pass
    private boolean runWithoutForking(String[] args) {
        try {
            return (Boolean) run().invoke(null, new Object[]{args});
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();
            if(target instanceof RuntimeException){
                throw (RuntimeException)target;
            } else {
                throw new IllegalArgumentException(target);
            }
        }
    }

    // Returns true if all tests pass
    private boolean runForkingOnce(String[] args) throws MojoFailureException {

        final Commandline cli = new Commandline();
        cli.setWorkingDirectory(project.getBasedir());
        cli.setExecutable("java");

        // Set up environment
        if (environmentVariables != null) {
            for (final Map.Entry<String, String> entry : environmentVariables.entrySet()) {
                cli.addEnvironment(entry.getKey(), entry.getValue());
            }
        }
        cli.addEnvironment("CLASSPATH", buildClassPathEnvironment());

        // Set up system properties
        if (systemProperties != null) {
            for (final Map.Entry<String, String> entry : systemProperties.entrySet()) {
                cli.createArg().setValue(String.format("-D%s=%s", entry.getKey(), entry.getValue()));
            }
        }
        cli.createArg().setValue(String.format("-Dbasedir=%s", project.getBasedir().getAbsolutePath()));

        // Set user specified JVM arguments
        if (argLine != null) {
            cli.createArg().setLine(argLine);
        }

        // Set debugging JVM arguments if debugging is enabled
        if (debugForkedProcess) {
            cli.createArg().setLine(forkedProcessDebuggingArguments());
        }

        // Set ScalaTest arguments
        cli.createArg().setValue("org.scalatest.tools.Runner");
        for (final String arg : args) {
            cli.createArg().setValue(arg);
        }

        // Log command string
        final String commandLogStatement = "Forking ScalaTest via: " + cli;
        if (logForkedProcessCommand) {
            getLog().info(commandLogStatement);
        } else {
            getLog().debug(commandLogStatement);
        }

        final StreamConsumer streamConsumer = new StreamConsumer() {
            public void consumeLine(final String line) {
                System.out.println(line);
            }
        };
        try {
            final int result = CommandLineUtils.executeCommandLine(cli, streamConsumer, streamConsumer, forkedProcessTimeoutInSeconds);
            return result == 0;
        }
        catch (final CommandLineTimeOutException e) {
            throw new MojoFailureException(String.format("Timed out after %d seconds waiting for forked process to complete.", forkedProcessTimeoutInSeconds));
        }
        catch (final CommandLineException e) {
            throw new MojoFailureException("Exception while executing forked process.", e);
        }
    }

    private String buildClassPathEnvironment() {
        StringBuffer buf = new StringBuffer();
        boolean first = true;
        for (String e : testClasspathElements) {
            if (first) {
                first = false;
            }
            else {
                buf.append(File.pathSeparator);
            }
            buf.append(e);
        }
        return buf.toString();
    }

    private String forkedProcessDebuggingArguments() {
        if (debugArgLine == null) {
            return String.format("-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=%s", debuggerPort);
        } else {
            return debugArgLine;
        }
    }

    // This is just used by runScalaTest to get the method to invoke
    private Method run() {
        try {
            Class<?> runner = classLoader().loadClass("org.scalatest.tools.Runner");
            return runner.getMethod("run", String[].class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("scalatest is missing from classpath");
        }
    }

    // This is just used by run to get a class loader from which to load ScalaTest
    private ClassLoader classLoader() {
        try {
            List<URL> urls = new ArrayList<URL>();
            for (String element : testClasspathElements) {
                File file = new File(element);
                if (file.isFile()) {
                    urls.add(file.toURI().toURL());
                }
            }
            URL[] u = urls.toArray(new URL[urls.size()]);
            return new URLClassLoader(u);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    // This is the configuration parameters shared by all concrete Mojo subclasses
    List<String> sharedConfiguration() {
        return new ArrayList<String>() {{
            addAll(runpath());
            addAll(config());
            addAll(tagsToInclude());
            addAll(tagsToExclude());
            addAll(parallel());
            addAll(suites());
            addAll(membersOnlySuites());
            addAll(wildcardSuites());
            addAll(testNGConfigFiles());
            addAll(junitClasses());
        }};
    }

    private List<String> config() {
        List<String> c = new ArrayList<String>();
        for(String pair : splitOnComma(config)){
            c.add("-D"+pair);
        }
        return c;
    }

    private List<String> runpath() {
        return compoundArg("-p",
                outputDirectory.getAbsolutePath(),
                testOutputDirectory.getAbsolutePath(),
                runpath);
    }

    private List<String> tagsToInclude() {
        return compoundArg("-n", tagsToInclude);
    }

    private List<String> tagsToExclude() {
        return compoundArg("-l", tagsToExclude);
    }

    private List<String> parallel() {
        return parallel ? singletonList("-c") : Collections.<String>emptyList();
    }

    private List<String> suites() {
        return suiteArg("-s", suites);
    }

    private List<String> membersOnlySuites() {
        return suiteArg("-m", membersOnlySuites);
    }

    private List<String> wildcardSuites() {
        return suiteArg("-w", wildcardSuites);
    }

    private List<String> testNGConfigFiles() {
        return suiteArg("-t", testNGConfigFiles);
    }

    private List<String> junitClasses() {
        return suiteArg("-j", jUnitClasses);
    }
}

