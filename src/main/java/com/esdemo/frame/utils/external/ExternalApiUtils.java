package com.esdemo.frame.utils.external;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-07-04 08:53
 */
@Component
public class ExternalApiUtils {

    public static ExternalApi externalApi;

    public static String getAccountPath(AccountApiEnum accountApiEnum) {
        return ExternalApiUtils.externalApi.getAccountHost() + accountApiEnum.getPath();
    }

    public static String getFlowmoneyPath(FlowmoneyApiEnum flowmoneyApiEnum) {
        return ExternalApiUtils.externalApi.getFlowmoneyHost() + flowmoneyApiEnum.getPath();
    }

    public static String getCorePath(CoreApiEnum coreApiEnum) {
        return ExternalApiUtils.externalApi.getCoreHost() + coreApiEnum.getPath();
    }

    @Resource
    public void setExternalApi(ExternalApi externalApi) {
        ExternalApiUtils.externalApi = externalApi;
    }

}
