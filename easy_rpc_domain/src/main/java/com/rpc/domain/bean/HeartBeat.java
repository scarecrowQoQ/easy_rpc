package com.rpc.domain.bean;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 心跳
 */
@Data
public class HeartBeat implements Serializable {

    private String initiator;

    Long updateTime;

    public HeartBeat(){
        this.updateTime = new Date().getTime();
    }

    public HeartBeat(String initiator){
        this.initiator = initiator;
    }
}
