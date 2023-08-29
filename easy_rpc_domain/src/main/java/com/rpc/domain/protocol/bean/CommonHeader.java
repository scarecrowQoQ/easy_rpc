package com.rpc.domain.protocol.bean;

import com.rpc.domain.protocol.enum2.RequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一请求头，用于表示行为
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonHeader implements Serializable {
    private RequestType type;
}
