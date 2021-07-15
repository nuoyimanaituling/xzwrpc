package io.xzw.xzwrpc.stub.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.xzw.xzwrpc.serializer.RpcSerializer;
import lombok.extern.slf4j.Slf4j;

import java.rmi.server.ExportException;


@Slf4j
public class NettyEncoder extends MessageToByteEncoder<Object> {

    private final Class<?> clazz;
    private final RpcSerializer serializer;
    public NettyEncoder(Class<?> clazz,RpcSerializer serializer){
        this.clazz =clazz;
        this.serializer =serializer;
    }
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 很简单的一个协议。自定义长度，然后就是数据
        if(clazz.isInstance(msg)){
            try{
                byte[] bytes =this.serializer.serialize(msg);
                out.writeInt(bytes.length);
                out.writeBytes(bytes);
            }
            catch (Exception e){
                log.error("server has exception",e);
                e.printStackTrace();
            }
        }
    }
}
