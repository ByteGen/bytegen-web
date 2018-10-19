package com.bytegen.common.web.filter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
public class BufferedRequestWrapper extends HttpServletRequestWrapper {
    private ByteArrayInputStream bais;
    private ByteArrayOutputStream baos;
    private BufferedServletInputStream bsis;
    private byte[] buffer;

    /**
     * Default constructor.
     *
     * @param request {@link HttpServletRequest}
     * @throws IOException error
     */
    public BufferedRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        InputStream is = request.getInputStream();
        baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int length;
        while ((length = is.read(buf)) > 0) {
            baos.write(buf, 0, length);
        }
        buffer = baos.toByteArray();
    }

    @Override
    public ServletInputStream getInputStream() {
        bais = new ByteArrayInputStream(buffer);
        bsis = new BufferedServletInputStream(bais);
        return bsis;
    }

    public ByteBuffer getBuffer() {
        return ByteBuffer.wrap(Arrays.copyOf(buffer, buffer.length));
    }
}
