package resolve;

import io.xzw.xzwrpc.anntataion.RpcService;
import io.xzw.xzwrpc.api.RpcResolve;
import org.junit.platform.commons.support.ReflectionSupport;
import org.reflections.Reflections;

import java.util.*;

public class AnnotationResolve implements RpcResolve {


    private  String scanpackage;
    private Map<String,Object> serviceMap;


    public AnnotationResolve(String scanpackage) {
        this.scanpackage = scanpackage;
        serviceMap =new HashMap<>();
    }



    @Override
    public void register(String name, Object service) {


    }

    @Override
    public void register() {

        Objects.requireNonNull(scanpackage);
        Reflections reflections =new Reflections(this.scanpackage);
        Set<Class<?>> classSet =reflections.getTypesAnnotatedWith(RpcService.class);
        for (Class<?> clazz:classSet ){

            try {
                Object o = clazz.newInstance();
                Arrays.stream(clazz.getInterfaces()).forEach(aclazz ->
                    serviceMap.put(aclazz.getName(), o));
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Object resolve(String name) {
        return serviceMap.get(name);
    }
}
