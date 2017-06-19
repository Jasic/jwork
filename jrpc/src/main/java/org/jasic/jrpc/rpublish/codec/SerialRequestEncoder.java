package org.jasic.jrpc.rpublish.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * @Author 菜鹰.
 * @Date 2014/12/16
 */
public class SerialRequestEncoder implements ProtocolEncoder {
    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {

    }

    @Override
    public void dispose(IoSession session) throws Exception {

    }
}
