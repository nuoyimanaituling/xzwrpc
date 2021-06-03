package resolve;

import io.xzw.xzwrpc.api.RpcResolve;
import org.springframework.context.ApplicationStartupAware;

import java.util.HashMap;
import java.util.Map;


// 现在的问题就是MapResolve与远程skeleton进行了耦合，想要通过注解的方式，在程序启动的时候能够自动发现并注册

public class MapResolve implements RpcResolve {

    private  final Map<String,Object> mapservice;

    public MapResolve() {
        this.mapservice =new HashMap<>();
    }

    @Override
    public void register() {

    }

    @Override
    public void register(String name, Object service) {

        mapservice.put(name,service);

    }

    @Override
    public Object resolve(String name) {
        return mapservice.get(name);
    }
}
