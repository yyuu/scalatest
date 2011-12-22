package org.scalatest.spi.location;

public class Test {
    
    private String className;
    private String displayName;
    private String[] testNames;
  
    public Test(String className, String displayName, String[] testNames) {
        this.className = className;
        this.displayName = displayName;
        this.testNames = testNames;
    }

    public String getClassName() {
        return className;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String[] getTestNames() {
        return testNames;
    }
}
