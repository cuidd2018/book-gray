package cn.dingyuegroup.gray.server.model.resp;

import lombok.Data;

@Data
public class RespMsg {

    // 成功状态
    public static final String SUC_20000 = "20000";
    // 失败状态
    public static final String ERR_50000 = "50000";
    private String respCode = SUC_20000;
    private String message = "请求成功";
    private Object data;

    public RespMsg() {

    }

    public RespMsg(Object data) {
        this.data = data;
    }

    public RespMsg(String respCode, String message) {
        this.respCode = respCode;
        this.message = message;
    }

    public RespMsg(String respCode, String message, Object data) {
        this.respCode = respCode;
        this.message = message;
        this.data = data;
    }

    public static RespMsg success(Object data) {
        return new RespMsg(data);
    }

    public static RespMsg error(String message) {
        return new RespMsg(ERR_50000, message);
    }

    public static RespMsg error(ErrorCode errorCode) {
        return new RespMsg(errorCode.getValue(), null);
    }

    public static RespMsg error(ErrorCode errorCode, String message) {
        return new RespMsg(errorCode.getValue(), message);
    }

    public static RespMsg error(String respCode, String message) {
        return new RespMsg(respCode, message);
    }
}
