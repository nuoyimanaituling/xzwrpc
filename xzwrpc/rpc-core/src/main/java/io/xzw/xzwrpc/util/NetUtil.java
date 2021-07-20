package io.xzw.xzwrpc.util;
import io.xzw.xzwrpc.exception.XzwRpcException;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

/**
 * @author xzw
 */
@Slf4j
public class NetUtil {

    private NetUtil(){}

    /**
     * 根据传入的地址信息，拆分成ip和端口号
     * @param addr 服务端地址
     * @return 返回主机地址和端口
     */
    public static String[] getHostAndPort(String addr){
        if (addr.contains(":")){
            String[] split = addr.split(":");
            return new String[]{split[0].trim(),split[1].trim()};
        }
        else {
            throw new XzwRpcException("addr[ "+addr+" is illegal! ");
        }
    }

    public static String getIpAddress(){
        // 获取网卡的一个地址即可
        try {
            Enumeration<NetworkInterface> allNetInterfaces =NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while(allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface)allNetInterfaces.nextElement();
                if(!netInterface.isLoopback() && !netInterface.isVirtual() &&netInterface.isUp()){
                    Enumeration<InetAddress> addresses =netInterface.getInetAddresses();
                    while(addresses.hasMoreElements()){
                        ip =addresses.nextElement();
                        if(ip instanceof Inet4Address){
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("ip 地址获取失败",e);
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 检查传入的端口号是否可用
     * @param port 端口号
     * @return 返回端口是否可用判断
     */
    public static boolean isAvailablePort(int port){
        boolean available = false;
        ServerSocket server = null;
        try{
            server = new ServerSocket(port);
            available = true;
            server.close();
        }catch (IOException e){
            available = false;
            log.debug("[xzw-rpc] port[{}] is in use", port);
        }
        return available;
    }

    /**
     * 根据默认端口号为分界点，向左右两端找可用的端口号
     * @param defaultPort 默认端口号
     * @return 返回可用端口号
     */
    public static int findAvailablePort(int defaultPort){
        int port = defaultPort;
        while(port < 65535){
            if (isAvailablePort(port)){
                return port;
            }
            else {
                port++;
            }
        }
        port = port - defaultPort;
        while(port > 0){
            if(isAvailablePort(port)){
                return port;
            }
            else {
                port--;
            }
        }
        throw new XzwRpcException("not find available port");
    }




}
