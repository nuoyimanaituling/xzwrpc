package io.xzw.xzwrpc.router;

import io.xzw.xzwrpc.router.common.Filter;
import io.xzw.xzwrpc.spi.SpiPluginLoader;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xzw
 */
public class FilterChainTest {


    @Test
    public void buildProviderChain(){
        SpiPluginLoader.load();
        List<Filter> filters = SpiPluginLoader.getFilterList();
        List<Filter> filtersTest = new ArrayList<>();
        filtersTest.add(new DefaultFilter());
        filtersTest.add(new DefaultFilter1());
        int index = 0;
        for (Filter filter: filters) {
            Assert.assertEquals(filter.getClass(),filtersTest.get(index).getClass());
            index ++ ;
        }
    }
}
