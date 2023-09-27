package com.rpc.domain.bean;

import com.sun.xml.internal.ws.developer.Serialization;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一请求完整内容，包含头消息和数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Serialization
public class RpcRequestHolder implements Serializable {

    private RequestHeader requestHeader;

    private Object data;
}
