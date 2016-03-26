package org.jasic.jrpc.rpublish.serial;

import java.io.Serializable;

/**
 * @Author 菜鹰.
 * @Date 2014/12/17
 */
public class SerialRequest implements Serializable {

    private static final long serialVersionUID = 2730660654971283691L;
    private String methodName;
    private String interfaceName;

    private Class<?>[] paraTypes;
    private Object[] arguments;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParaTypes() {
        return paraTypes;
    }

    public void setParaTypes(Class<?>[] paraTypes) {
        this.paraTypes = paraTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
}
