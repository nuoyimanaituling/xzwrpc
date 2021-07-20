package io.xzw.xzwrpc.router;

import io.xzw.xzwrpc.router.common.Router;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;


/**
 * @author xzw
 */
public class DefaultRouter implements Router {

    @Override
    public List<String> route(List<String> urls, Method method, Object[] args) {

        System.out.println("我是路由逻辑");
        System.out.println(Arrays.toString(urls.toArray()) + "#" + method.getName() + "#" + Arrays.toString(args));
        return urls;
    }
}
