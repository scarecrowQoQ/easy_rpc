package com.rpc.easy_rpc_govern.filter;

import com.rpc.domain.bean.ConsumeRequest;
import com.rpc.domain.bean.RequestHeader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
public class RequestFilterDefault implements RequestFilterHandler{
    @Override
    public void filterHandler() {
        return ;
    }
}
