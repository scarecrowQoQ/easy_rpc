package com.rpc.domain.bean;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.rpc.domain.enumeration.RequestType;
/**
 * 统一请求头，用于表示行为
 */
@Data
@NoArgsConstructor
public class RequestHeader implements Serializable {

    private RequestType type;
    /**
     * 同步标记位，如果返回数据需要同步处理则置为true
     */
    private Boolean sync;

    private String requestId;

    private Map<String,Object>  attrs;

    public RequestHeader(RequestType type){
        this.type = type;
        this.sync = false;
        this.attrs = null;
        long time = System.currentTimeMillis();
        int random = (int) (Math.random() * Integer.MAX_VALUE);
        UUID uuid = new UUID(time, random);
        this.requestId = uuid.toString();
    }

    public RequestHeader(RequestType type,Boolean sync){
        this.type = type;
        this.sync = sync;
        this.attrs = null;
        long time = System.currentTimeMillis();
        int random = (int) (Math.random() * Integer.MAX_VALUE);
        UUID uuid = new UUID(time, random);
        this.requestId = uuid.toString();
    }

    public RequestHeader(RequestType type, Map<String,Object> attrs){
        this.type = type;
        this.attrs = attrs;
        long time = System.currentTimeMillis();
        int random = (int) (Math.random() * Integer.MAX_VALUE);
        UUID uuid = new UUID(time, random);
        this.requestId = uuid.toString();
    }

    public RequestHeader(RequestType type, String requestId) {
        this.type = type;
        this.requestId = requestId;
        this.attrs = new HashMap<>();
    }



}
