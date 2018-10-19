package com.bytegen.common.web.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
public class BufferedServletInputStream extends ServletInputStream {
    private ReadListener readListener;
    private ByteArrayInputStream bais;

    BufferedServletInputStream(ByteArrayInputStream bais) {
        this.bais = bais;
    }

    @Override
    public int available() {
        return bais.available();
    }

    @Override
    public int read() throws IOException {
        int i = bais.read();
        if (isFinished() && (readListener != null)) {
            try {
                readListener.onAllDataRead();
            } catch (IOException ex) {
                readListener.onError(ex);
                throw ex;
            }
        }
        return i;
    }

    @Override
    public int read(byte[] buf, int off, int len) {
        return bais.read(buf, off, len);
    }

    @Override
    public boolean isFinished() {
        return bais.available() <= 0;
    }

    @Override
    public boolean isReady() {
        return !isFinished();
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        this.readListener = readListener;
        if (!isFinished()) {
            try {
                readListener.onDataAvailable();
            } catch (IOException e) {
                readListener.onError(e);
            }
        } else {
            try {
                readListener.onAllDataRead();
            } catch (IOException e) {
                readListener.onError(e);
            }
        }
    }

    @Override
    public boolean markSupported() {
        return bais.markSupported();
    }

    @Override
    public synchronized void reset() throws IOException {
        bais.reset();
    }

    @Override
    public synchronized void mark(int readlimit) {
        bais.mark(readlimit);
    }
}
