package cn.dingyuegroup.gray.client.decision;

public enum PolicyType {

    /**
     * @see cn.dingyuegroup.bamboo.BambooRequest#params
     */
    REQUEST_PARAMETER,
    /**
     * @see cn.dingyuegroup.bamboo.BambooRequest#headers
     */
    REQUEST_HEADER,

    /**
     * @see cn.dingyuegroup.bamboo.BambooRequest#ip
     */
    REQUEST_IP,
    /**
     * @see cn.dingyuegroup.bamboo.BambooRequestContext#params
     */
    CONTEXT_PARAMS
}
