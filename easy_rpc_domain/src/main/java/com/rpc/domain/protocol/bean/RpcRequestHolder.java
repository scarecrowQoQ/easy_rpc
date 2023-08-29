package com.rpc.domain.protocol.bean;

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
public class RpcRequestHolder implements Serializable {

    private CommonHeader commonHeader;

    private Object data;
}
