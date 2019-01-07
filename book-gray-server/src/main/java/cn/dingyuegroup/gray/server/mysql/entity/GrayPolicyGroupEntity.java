package cn.dingyuegroup.gray.server.mysql.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GrayPolicyGroupEntity {
    private Integer id;

    private String policyGroupId;

    private String alias;

    private Short enable;

    private Short isDelete;

    private List<GrayPolicyEntity> grayPolicyEntities = new ArrayList<>();
}