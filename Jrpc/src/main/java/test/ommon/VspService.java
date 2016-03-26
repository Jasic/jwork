package test.ommon;

/**
 * @Author 菜鹰.
 * @Date 2014/12/17
 */
public class VspService implements IVspService {

    @Override
    public Result play_自动发人币服务() {
        Result result = new Result();
        /**
         TODO do一些你们喜欢do的东西
         */
        result.setCode("100");
        result.setSuccess(true);
        result.setMsg("表哥们发了888888888元,表弟发了1蚊鸡送块mx3手机膜");
        return result;
    }
}
