package org.scalatest.spi.location;

import java.util.ArrayList;
import java.util.List;

public class FreeSpecFinder implements Finder {

    private String getTestNameBottomUp(MethodInvocation invocation) {
        AstNode parent = invocation.getParent();
        AstNode target = invocation.getTarget();
        if (parent == null || !(parent instanceof MethodInvocation)) 
            return target.toString();
        else 
            return getTestNameBottomUp((MethodInvocation) parent) + " " + target.toString();
    }
    
    private List<String> getTestNamesUpBottom(MethodInvocation invocation) {
        String prefix = getTestNameBottomUp(invocation);
        AstNode[] children = invocation.getChildren();
        List<String> testNameList = new ArrayList<String>();
        for(AstNode childNode : children) {
            if(childNode instanceof MethodInvocation) {
                MethodInvocation child = (MethodInvocation) childNode;
                if (child.getName().equals("in") || child.getName().equals("is")) 
                    testNameList.add(prefix + " " + child.getTarget().toString());
                else if (child.getName().equals("-"))
                    testNameList.addAll(getTestNamesUpBottom(child));
            }
        }
        return testNameList;
    }
    
    public Test find(AstNode node) {
        Test test = null;
        if (node instanceof MethodInvocation) {
            MethodInvocation invocation = (MethodInvocation) node;
            if (invocation.getName().equals("in") || invocation.getName().equals("is")) {
                String testName = getTestNameBottomUp(invocation);
                test = new Test(invocation.getClassName(), testName, new String[] { testName });
            }
            else if (invocation.getName().equals("-")) {
                String displayName = getTestNameBottomUp(invocation);
                List<String> testNameSet = getTestNamesUpBottom(invocation);
                test = new Test(invocation.getClassName(), displayName, testNameSet.toArray(new String[testNameSet.size()]));
            }
        }    
        return test;
    }
    
}
