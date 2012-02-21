package org.scalatest.finders;

public class Selection {
    
    private String className;
    private String displayName;
    private String[] testNames;
  
    public Selection(String className, String displayName, String[] testNames) {
        this.className = className;
        this.displayName = displayName;
        this.testNames = testNames;
    }

    public String className() {
        return className;
    }

    public String displayName() {
        return displayName;
    }

    public String[] testNames() {
        return testNames;
    }
}
