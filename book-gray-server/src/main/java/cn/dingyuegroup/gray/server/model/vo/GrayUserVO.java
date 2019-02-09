package cn.dingyuegroup.gray.server.model.vo;

import lombok.Data;

/**
 * Created by 170147 on 2019/2/3.
 */
@Data
public class GrayUserVO {
    private String username;//用户名称
    private String department;
    private String roles;
    private boolean departmentAdmin;
    private String udid;
    private String account;//账户
}
