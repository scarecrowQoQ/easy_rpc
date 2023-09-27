package com.rpc.domain.bean;

import com.sun.xml.internal.ws.developer.Serialization;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;
import com.rpc.domain.enumeration.RequestType;
/**
 * 统一请求头，用于表示行为
 */
@Data
@NoArgsConstructor
@Serialization
public class RequestHeader implements Serializable {

    private RequestType type;

    private String requestId;

    private HashMap<Object,Object> headerMap;

    public RequestHeader(RequestType type){
        this.type = type;
        this.headerMap = new HashMap<>();
        long time = System.currentTimeMillis();
        int random = (int) (Math.random() * Integer.MAX_VALUE);
        UUID uuid = new UUID(time, random);
        this.requestId = uuid.toString();
    }

    public RequestHeader(RequestType type, String requestId) {
        this.type = type;
        this.requestId = requestId;
        this.headerMap = new HashMap<>();
    }
}
