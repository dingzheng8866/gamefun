package com.tiny.game.common.net.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.CodedOutputStream;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.net.NetMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NetEncoder extends MessageToByteEncoder<NetMessage> {
	
	private static final Logger logger = LoggerFactory.getLogger(NetEncoder.class);

//	private boolean enableEncrypt = false;
	
	@Override
	protected void encode(ChannelHandlerContext ctx, NetMessage msg,
			ByteBuf out) throws Exception {
		if (msg == null) {
			throw new InternalBugException("Empty net message to encode.");
		}
		String msgName = msg.getName();
		
		int msgBytesLength =msg.getParameters().length;
		
		int headLen = 4/*协议长度 int*/ + 4/*msgName len*/ + msgName.length()/*msgName*/;
		int totalLen = headLen + msgBytesLength;
		
		out.ensureWritable(totalLen);

		CodedOutputStream outputStream = CodedOutputStream.newInstance(new ByteBufOutputStream(out));
		outputStream.writeRawLittleEndian32(totalLen);
		outputStream.writeRawLittleEndian32(msgName.length());
		outputStream.writeRawBytes(msgName.getBytes());
		outputStream.writeRawBytes(msg.getParameters());
		outputStream.flush();
		logger.info("NetEncoder => msg: " + msgName + ", total length:" + totalLen);
	}

}

