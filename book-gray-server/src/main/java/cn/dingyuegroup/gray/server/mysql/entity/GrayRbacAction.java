package cn.dingyuegroup.gray.server.mysql.entity;

public class GrayRbacAction {
    private Integer id;

    private String actionId;

    private String actionName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId == null ? null : actionId.trim();
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName == null ? null : actionName.trim();
    }
}