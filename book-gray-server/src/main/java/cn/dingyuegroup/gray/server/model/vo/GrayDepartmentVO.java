package cn.dingyuegroup.gray.server.model.vo;

import lombok.Data;

/**
 * Created by 170147 on 2019/2/11.
 */
@Data
public class GrayDepartmentVO {

    private String departmentId;

    private String departmentName;

    private String creator;

    private String creatorName;//创建者名称nickname
}
