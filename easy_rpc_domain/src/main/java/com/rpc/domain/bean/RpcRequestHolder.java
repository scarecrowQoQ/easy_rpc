package com.rpc.domain.bean;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 统一请求完整内容，包含头消息和数据
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RpcRequestHolder implements Serializable {

    private RequestHeader requestHeader;

    private Object data;

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(RequestHeader requestHeader) {
        this.requestHeader = requestHeader;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
