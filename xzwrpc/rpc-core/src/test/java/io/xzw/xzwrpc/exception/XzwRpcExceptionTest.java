package io.xzw.xzwrpc.exception;

import io.xzw.xzwrpc.exception.XzwRpcException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author xzw
 */
public class XzwRpcExceptionTest {

    @Test
    public void testAll() throws Exception {

        XzwRpcException xzwRpcException = new XzwRpcException("xzw");

        Assert.assertEquals(xzwRpcException.getMessage(), "xzw");

    }


}
