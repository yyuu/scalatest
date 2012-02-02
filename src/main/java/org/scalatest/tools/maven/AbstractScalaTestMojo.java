package org.scalatest.tools.maven;

import org.apache.maven.plugin.AbstractMojo;
import static org.scalatest.tools.maven.MojoUtils.*;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
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
     * to the scalatest runpath. ${project.build.outputDirectory} and
     * ${project.build.testOutputDirectory} are included by default
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
     * Comma separated list of configuration parameters to pass to scalatest.
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
     * Comma separated list of members to execute
     * @parameter expression="${membersOnlySuites}"
     */
    String membersOnlySuites;

    /**
     * Comma separated list of wildcard suites to execute
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
     * Option to specify whether the forked process should wait at startup for a remote debugger to attach.
     *
     * <p>If set to <code>true</code>, the forked process will suspend at startup and wait for a remote
     * debugger to attach to the configured port.</p>
     *
     * <p>If set to any other string, the string will be concatenated with <code>argLine</code>, allowing
     * arbitrary debug options to be set without having to manually concatenate <code>argLine</code>.</p>
     *
     * <p>Note: this behavior is compatible with maven-surefire-plugin's usage of <code>debugForkedProcess</code> and
     * <code>argLine</code>.</p>
     *
     * @parameter expression="${debugForkedProcess}"
     */
    String debugForkedProcess;

    /**
     * Port to listen on when debugging the forked process.
     *
     * @parameter expression="${debuggerPort}" default-value="5005"
     */
    int debuggerPort = 5005;

    // runScalaTest is called by the concrete mojo subclasses  TODO: make it protected and others too
    // Returns true if all tests pass
    boolean runScalaTest(String[] args) {
        getLog().debug(Arrays.toString(args));
        // System.out.println("##### " + Arrays.toString(args));
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
    private boolean runForkingOnce(String[] args) {

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
        String classPath = buf.toString();

        List<String> commandArgs = new ArrayList<String>();
        commandArgs.add("java");
        commandArgs.add(String.format("-Dbasedir=%s", System.getProperty("basedir")));
        if (argLine != null) {
            commandArgs.add(argLine);
        }
        if (shouldDebugForkedProcess()) {
            commandArgs.addAll(forkedProcessDebuggingArguments());
        }
        commandArgs.add("org.scalatest.tools.Runner");
        commandArgs.addAll(Arrays.asList(args));

        if (getLog().isDebugEnabled()) {
            getLog().debug("Forking java process via: " + commandArgs);
        }

        try {
            ProcessBuilder builder = new ProcessBuilder(commandArgs);
            builder.redirectErrorStream(true);
            builder.environment().put("CLASSPATH", classPath);
            Process process = builder.start();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                try {
                    String line = reader.readLine();
                    while (line != null) {
                        System.out.println(line);
                        line = reader.readLine();
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int exitValue = process.waitFor();
                return exitValue == 0;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean shouldDebugForkedProcess() {
        return debugForkedProcess != null && debugForkedProcess.trim().length() > 0;
    }

    private List<String> forkedProcessDebuggingArguments() {
        final String trimmedDebugForkedProcess = debugForkedProcess.trim();
        if ("true".equals(trimmedDebugForkedProcess)) {
            return Arrays.asList(
                "-Xdebug",
                String.format("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=%s", debuggerPort)
            );
        } else {
            return Arrays.asList(trimmedDebugForkedProcess);
        }
    }

    // sideeffect! Currently not used, was probably used for debugging
    private void print(String[] args) {
        StringBuffer sb = new StringBuffer("org.scalatest.tools.Runner.run(");
        for (int i = 0; i < args.length; i++) {
            boolean ws = args[i].contains(" ");
            if (ws) {
                sb.append("\"");
            }
            sb.append(args[i]);
            if (ws) {
                sb.append("\"");
            }
            if (i + 1 < args.length) {
                sb.append(", ");
            }
        }
        sb.append(")");
        getLog().info(sb.toString());
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

