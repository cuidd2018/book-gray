package cn.dingyuegroup.gray.core;

import cn.dingyuegroup.bamboo.context.BambooRequest;
import cn.dingyuegroup.bamboo.context.BambooRequestContext;

public enum PolicyType {

    /**
     * @see BambooRequest#params
     */
    REQUEST_PARAMETER,
    /**
     * @see BambooRequest#headers
     */
    REQUEST_HEADER,

    /**
     * @see BambooRequest#ip
     */
    REQUEST_IP,
    /**
     * @see BambooRequestContext#params
     */
    CONTEXT_PARAMS
}
