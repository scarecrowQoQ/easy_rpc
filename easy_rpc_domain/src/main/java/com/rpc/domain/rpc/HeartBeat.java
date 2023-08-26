package com.rpc.domain.rpc;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class HeartBeat implements Serializable {

    Long updateTime;

    public HeartBeat(){
        this.updateTime = new Date().getTime();
    }
}
