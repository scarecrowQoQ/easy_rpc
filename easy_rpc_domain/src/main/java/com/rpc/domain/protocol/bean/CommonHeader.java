package com.rpc.domain.protocol.bean;

import com.rpc.domain.protocol.enum2.RequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

/**
 * 统一请求头，用于表示行为
 */
@Data
@NoArgsConstructor
public class CommonHeader implements Serializable {

    private RequestType type;

    private String requestId;

    private HashMap<Object,Object> headerMap;

    public CommonHeader(RequestType type){
        this.type = type;
        this.headerMap = new HashMap<>();
        long time = System.currentTimeMillis();
        int random = (int) (Math.random() * Integer.MAX_VALUE);
        UUID uuid = new UUID(time, random);
        this.requestId = uuid.toString();
    }

    public CommonHeader(RequestType type, String requestId) {
        this.type = type;
        this.requestId = requestId;
        this.headerMap = new HashMap<>();
    }
}
