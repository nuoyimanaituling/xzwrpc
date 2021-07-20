package io.xzw.xzwrpc.stub.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.xzw.xzwrpc.serializer.RpcSerializer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author xzw
 */
@Slf4j
public class NettyDecoder extends ByteToMessageDecoder {
    private final Class<?> clazz;
    private final RpcSerializer serializer;
    public NettyDecoder(Class<?> clazz,RpcSerializer serializer){
        this.clazz = clazz;
        this.serializer  = serializer;
    }
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 说明写的int长度出现错误
        if(in.readableBytes() <4){
            return;
        }
        // 保存一下当前的读指针
        in.markReaderIndex();
        //获取字节数组的长度
        int len = in.readInt();
        if(len < 0){
            ctx.close();
        }
        // 如果可读的字节数<长度，则直接返回
        if(in.readableBytes() < len){
            in.resetReaderIndex();
            return;
        }
        // 数据已经足够了
        byte[] bytes = new byte[len];
        // 进行序列化
        in.readBytes(bytes);
        try{
            Object msg = serializer.deserialize(bytes,clazz);
            out.add(msg);
        }catch (Exception e){
            log.error("server catch exception",e);
            e.printStackTrace();

        }




    }
}
