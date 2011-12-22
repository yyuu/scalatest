package org.scalatest.spi.location;

import java.util.ArrayList;
import java.util.List;

import org.scalatest.Style;

public class LocationUtils {

    public static TestResolver getTestResolver(Class clazz) {
        // Look for interface first since style traits are compiled as Java interfaces
        TestResolver testResolver = lookInInterfaces(clazz);
        if(testResolver == null)
            testResolver = lookInSuperClasses(clazz);  // Look in super classes, in case custom test style is a class instead of trait.
        return testResolver;
    }
    
    private static TestResolver lookInSuperClasses(Class clazz) {
        TestResolver testResolver = null;
        Class superClass = null;
        while (testResolver == null) {
            superClass = clazz.getSuperclass();
            if (superClass == null)
                break;
            Style style = (Style) superClass.getAnnotation(Style.class);
            if (style != null && style.testResolver() != null) {
                try {
                    testResolver = (TestResolver) Class.forName(style.testResolver()).newInstance();
                    break;
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            clazz = superClass;
        }
        return testResolver;
    }
    
    private static TestResolver lookInInterfaces(Class clazz) {
        TestResolver testResolver = null;
        List<Class> baseList = new ArrayList<Class>();
        baseList.add(clazz);
        List<Class> superInterfaceList = null;
        while (testResolver == null) {
            superInterfaceList = new ArrayList<Class>();
            int length = baseList.size();
            int i = 0;
            while(i < length && testResolver == null) {
                Class base = baseList.get(i);
                Class[] interfaces = base.getInterfaces();
                for(Class intf : interfaces) {
                    Style style = (Style) intf.getAnnotation(Style.class);
                    if (style != null && style.testResolver() != null) {
                        try {
                            testResolver = (TestResolver) Class.forName(style.testResolver()).newInstance();
                            break;
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                i++;
            }
            if(superInterfaceList.size() == 0)
                break;
            baseList = superInterfaceList;
        }
        return testResolver;
    }
}
