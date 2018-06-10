package com.tiny.game.common.net.netty;

import java.nio.ByteOrder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class NetDecoder extends MessageToMessageDecoder<ByteBuf> {
	
	private static final Logger logger = LoggerFactory.getLogger(NetDecoder.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg,
			List<Object> out) throws Exception {
		int msgNameLength = msg.order(ByteOrder.LITTLE_ENDIAN).readInt();
		
		logger.info("NetDecoder msg name length =>" + msgNameLength);
		
		byte[] array;
		int offset;
		int length = msg.readableBytes();
		
		if (msg.hasArray()) {
			array = msg.array();
			offset = msg.arrayOffset() + msg.readerIndex();
		} else {
			array = new byte[length];
			msg.getBytes(msg.readerIndex(), array, 0, length);
			offset = 0;
			
			// decrypt logic
//			if(msgCode == 0x01)
//			{
//				logger.debug("msgCode{} installLogin", msgCode);
//				array = ProtocolAES.getInstanceLogin().decrypt(array);
//			}
//			else
//			{
//				array = ProtocolAES.getInstance().decrypt(array);
//			}
//
//			length = array.length;
		}
		
		byte[] msgNameByteArray = new byte[msgNameLength];
		System.arraycopy(array, offset, msgNameByteArray, 0, msgNameLength);
		String msgName = new String(msgNameByteArray);
		logger.info("NetDecoder msg name =>" + msgName);
		
		byte[] pbMessage = new byte[length-msgNameLength];
		System.arraycopy(array, msgNameLength, pbMessage, 0, length-msgNameLength);
		
		out.add(new NetMessage(new String(msgNameByteArray), pbMessage));
	}

}
