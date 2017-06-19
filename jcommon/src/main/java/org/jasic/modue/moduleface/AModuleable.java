package org.jasic.modue.moduleface;

import org.jasic.modue.annotation.Module;
import org.jasic.util.Asserter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jasic
 * Date: 12-9-21
 */

public abstract class AModuleable {

    private Module module;
    private List<String> methodNames;

    public AModuleable() {
        module = null;
        methodNames = null;
        init();
    }

    private void init() {
        module = getClass().getAnnotation(Module.class);
        Asserter.notNull(module, (new StringBuilder()).append("Module can't be null,please check the class[").append(getClass().getName()).append("]").toString());
        methodNames = new ArrayList<String>();
        Method arr$[] = module.getClass().getMethods();
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$++) {
            Method m = arr$[i$];
            methodNames.add(m.getName());
        }

    }

    private Object _getModuleInfor(String methodName) {
        Asserter.notNull(methodNames, (new StringBuilder()).append("The list of method's name is null, perhaps that the 'Module' is not used at begin of[").append(getClass().getName()).append("]").toString());
        Asserter.isTrue(methodNames.contains(methodName), (new StringBuilder()).append("This moduleface[").append(Module.class.getName()).append("] has no method named[").append(methodName).append("] please check later!").toString());
        Object val = null;
        if (methodName.equalsIgnoreCase("name"))
            val = module.name();
        else if (methodName.equalsIgnoreCase("priority"))
            val = Integer.valueOf(module.priority());
        else if (methodName.equalsIgnoreCase("status"))
            val = Boolean.valueOf(module.status());
        return val;
    }

    public String getModuleName() {
        return (String) _getModuleInfor("name");
    }

    public int getModulePrority() {
        return ((Integer) _getModuleInfor("priority")).intValue();
    }

    public boolean getModuleStatus() {
        return ((Boolean) _getModuleInfor("status")).booleanValue();
    }
}
