package com.rpc.easy_rpc_protocol.serialization;

import java.io.IOException;

public interface RpcSerialization {
   <T> byte[] serialize(T t) throws IOException;

   <T> T deserialize(byte[] bytes,Class<T> clz) throws IOException;
}
