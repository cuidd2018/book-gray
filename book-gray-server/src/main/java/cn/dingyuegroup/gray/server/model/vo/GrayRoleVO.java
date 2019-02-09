package cn.dingyuegroup.gray.server.model.vo;

import lombok.Data;

/**
 * Created by 170147 on 2019/2/8.
 */
@Data
public class GrayRoleVO {

    private String roleId;

    private String roleName;

    private String department;

    private boolean departmentAdmin;

    private String resourceIds;

    private String resourceNames;

    private String creator;//创建者udid

    private String creatorName;//创建者名称nickname
}
