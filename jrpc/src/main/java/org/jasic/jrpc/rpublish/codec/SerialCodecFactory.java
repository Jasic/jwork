package org.jasic.jrpc.rpublish.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * @Author 菜鹰.
 * @Date 2014/12/16
 */
public class SerialCodecFactory implements ProtocolCodecFactory {
    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return null;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return null;
    }
}
