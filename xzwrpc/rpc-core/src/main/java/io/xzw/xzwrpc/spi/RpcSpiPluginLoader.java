package io.xzw.xzwrpc.spi;


import io.xzw.xzwrpc.router.common.Filter;
import io.xzw.xzwrpc.router.common.Router;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.util.*;
@Slf4j
public class RpcSpiPluginLoader {
    private static final List<Filter> FILTER_LIST =new ArrayList<>();
    private static final Map<Class<?>, Router> ROUTER_MAP =new HashMap<>();
    public static void load(){
        ServiceLoader<Filter> filters =ServiceLoader.load(Filter.class);
        Iterator<Filter> iterator =filters.iterator();

        if (!iterator.hasNext()){
            log.debug("no rpc filter plugin found");
        }
        while (iterator.hasNext()){
            FILTER_LIST.add(iterator.next());
        }
        ServiceLoader<Router> routers=ServiceLoader.load(Router.class);
        Iterator<Router> routerIterator =routers.iterator();
        if(!iterator.hasNext()){
            log.debug("no rpc router plugin found");
        }
        while (routerIterator.hasNext()){
            Router router =routerIterator.next();
            ROUTER_MAP.put(router.getClass(),router);
        }
    }
    public static List<Filter> getFilterList(){
        return FILTER_LIST;
    }
    public static Router getRouterByClass(Class<?> router){
        return ROUTER_MAP.get(router);
    }
}
