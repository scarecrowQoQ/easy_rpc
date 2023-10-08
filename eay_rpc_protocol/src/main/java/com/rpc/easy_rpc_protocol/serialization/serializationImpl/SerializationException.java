package com.rpc.easy_rpc_protocol.serialization.serializationImpl;

public class SerializationException extends RuntimeException{

    private static final long serialVersionUID = -1;

    public SerializationException() {
        super();
    }

    public SerializationException(String msg) {
        super(msg);
    }

    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
}

