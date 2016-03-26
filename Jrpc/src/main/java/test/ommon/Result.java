package test.ommon;

import java.io.Serializable;

/**
 * @Author 菜鹰.
 * @Date 2014/12/17
 */
public class Result implements Serializable {
    private static final long serialVersionUID = -4259253422238582199L;

    private boolean isSuccess;

    private String msg;

    private String code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Result{" +
                "isSuccess=" + isSuccess +
                ", msg='" + msg + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
