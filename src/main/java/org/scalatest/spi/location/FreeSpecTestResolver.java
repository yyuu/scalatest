package org.scalatest.spi.location;

import java.util.HashSet;
import java.util.Set;

public class FreeSpecTestResolver implements TestResolver {

    private String getTestNameBottomUp(MethodInvocation invocation) {
        AstNode parent = invocation.getParent();
        AstNode owner = invocation.getOwner();
        if (parent == null || !(parent instanceof MethodInvocation)) 
            return owner.toString();
        else 
            return getTestNameBottomUp((MethodInvocation) parent) + owner.toString();
    }
    
    private Set<String> getTestNamesUpBottom(MethodInvocation invocation) {
        String prefix = getTestNameBottomUp(invocation);
        AstNode[] children = invocation.getChildren();
        Set<String> testNameList = new HashSet<String>();
        for(AstNode childNode : children) {
            if(childNode instanceof MethodInvocation) {
                MethodInvocation child = (MethodInvocation) childNode;
                if (child.getName().equals("in") || child.getName().equals("is")) 
                    testNameList.add(prefix + " " + child.getOwner().toString());
                else if (child.getName().equals("-"))
                    testNameList.addAll(getTestNamesUpBottom(child));
            }
        }
        return testNameList;
    }
    
    public Test resolveTest(AstNode node) {
        Test test = null;
        if (node instanceof MethodInvocation) {
            MethodInvocation invocation = (MethodInvocation) node;
            if (invocation.getName().equals("in") || invocation.getName().equals("is")) {
                String testName = getTestNameBottomUp(invocation);
                test = new Test(invocation.getClassName(), testName, new String[] { testName });
            }
            else if (invocation.getName().equals("-")) {
                String displayName = getTestNameBottomUp(invocation);
                Set<String> testNameSet = getTestNamesUpBottom(invocation);
                test = new Test(invocation.getClassName(), displayName, testNameSet.toArray(new String[testNameSet.size()]));
            }
        }    
        return test;
    }
    
}
