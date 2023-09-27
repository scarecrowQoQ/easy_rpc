package com.rpc.domain.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProviderResponse implements Serializable {

    /**
     * 是否成功
     */
    private Boolean isSuccess;
    /**
     * 状态码
     */
    private int code;
    /**
     * 错误信息
     */
    private String error;
    /**
     * 返回的结果
     */
    private Object result;
}
