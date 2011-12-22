package org.scalatest.spi.location;

import java.util.ArrayList;
import java.util.List;

public class FeatureSpecTestResolver implements TestResolver {

    public Test resolveTest(AstNode node) {
        Test test = null;
        if(node instanceof MethodInvocation) {
            MethodInvocation invocation = (MethodInvocation) node;
            if(invocation.getName().equals("scenario") && invocation.getArgs().length == 1 && invocation.getArgs()[0].getClass() == String.class) {
                AstNode parent = node.getParent();
                if(parent == null || !(parent instanceof MethodInvocation))
                    test = new Test(invocation.getClassName(), invocation.getArgs()[0].toString(), new String[] { invocation.getArgs()[0].toString() });
                else {
                    MethodInvocation parentInvocation = (MethodInvocation) parent;
                    if(parentInvocation.getName().equals("feature") && parentInvocation.getArgs().length == 1 && parentInvocation.getArgs()[0].getClass() == String.class) {
                        String testName = parentInvocation.getArgs()[0] + " " + invocation.getArgs()[0];
                        test = new Test(invocation.getClassName(), testName, new String[] { testName });
                    }
                    else
                        test = new Test(invocation.getClassName(), invocation.getArgs()[0].toString(), new String[] { invocation.getArgs()[0].toString() });
                }
            }
            else if(invocation.getName().equals("feature") && invocation.getArgs().length == 1 && invocation.getArgs()[0].getClass() == String.class) {
                AstNode[] children = invocation.getChildren();
                String featureText = invocation.getArgs()[0].toString();
                List<String> testNameList = new ArrayList<String>();
                for (AstNode childNode : children) {
                    if(childNode instanceof MethodInvocation) {
                        MethodInvocation child = (MethodInvocation) childNode;
                        if(child.getName().equals("scenario") && child.getArgs().length == 1 && child.getArgs()[0].getClass() == String.class)
                            testNameList.add(featureText + " " + child.getArgs()[0]);
                    }
                }
                test = new Test(invocation.getClassName(), featureText, testNameList.toArray(new String[testNameList.size()]));
            }
        }
            
        return test;
    }
}
