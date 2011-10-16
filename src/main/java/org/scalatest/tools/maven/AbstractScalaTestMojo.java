package org.scalatest.tools.maven;

import org.apache.maven.plugin.AbstractMojo;
import static org.scalatest.tools.maven.MojoUtils.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import static java.util.Collections.singletonList;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.net.URL;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Provides the base for all mojos.
 * 
 * @author Jon-Anders Teigen
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

    // runScalaTest is called by the concrete mojo subclasses
    boolean runScalaTest(String[] args) {
        getLog().debug(Arrays.toString(args));
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
