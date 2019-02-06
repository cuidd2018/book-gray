package cn.dingyuegroup.gray.server.model.resp;

public enum ErrorCode {

    /**
     * 参数错误
     */
    PARAM_ERROR("10002"),

    VERIFY_FAIL("10003"),

    OK("20000"),

    SYSTEM_ERROR("50000");

    private String value;

    ErrorCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
