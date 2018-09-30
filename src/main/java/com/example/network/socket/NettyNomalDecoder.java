package com.example.network.socket;

import cn.joylab.game.GameContext;
import cn.joylab.game.MoonServer;
import cn.joylab.game.channel.GameCmdStatistics;
import cn.joylab.game.channel.GameProcesserRunnable;
import cn.joylab.game.net.GameIoSession;
import cn.joylab.game.net.cache.ThreadLocalCache;
import cn.joylab.game.net.netty.NettyConstants;
import cn.joylab.game.net.netty.NettySession;
import com.google.protobuf.TextFormat;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.zip.CRC32;


public class NettyNomalDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

	    //前四个字节是长度
		if (in.readableBytes() < 4) {
			// 不足4字节直接等待继续传数据
			return;
		}
		in.markReaderIndex();
		int packetDataLen = in.readInt();
		if (packetDataLen == 0) {
			//  如果长度为0则直接断开连接
			ctx.close();
			return;
		}
		if (in.readableBytes() < packetDataLen) {
			in.resetReaderIndex();
			return;
		}

		short cmdId = in.readShort();
		// 需要减去2字节指令
		int protoLen = packetDataLen - 2;



		// 改为直接解析为runnable对象，避免中间对象的产生，减少gc
		Attribute<GameIoSession> attribute = ctx.channel().attr(NettyConstants.SESSION_KEY);
		if (attribute == null) {
			log.error("attribute is null with :" + ctx.toString());
		}
		NettySession session = (NettySession) attribute.get();
		GameProcesserRunnable gameRunnable = MoonServer.getGameProcesserRunnable(cmdId);
		gameRunnable.cmdId = cmdId;
		gameRunnable.param = MoonServer.getParamParser(cmdId).parseProtobuf(protoData, 0, protoLen);
		gameRunnable.ioSession = session;
		out.add(gameRunnable);

		if (session != null) {
			if (session.player != null) {
				if (loop != 0) {
					if (session.player.loopId + 1 != loop) {
						if (log.isInfoEnabled()) {
							log.info("decode loop error:" + (session == null ? "" : session.getLogStr()) + " cmdId:"
									+ cmdId + " player.loopId:" + session.player.loopId + " loop:" + loop);
						}
					}
					session.player.loopId = loop;
				}
			}
		}
		if (minaLog.isDebugEnabled()) {
			minaLog.debug("decode:" + (session == null ? "" : session.getLogStr()) + " cmdId:" + cmdId + "\t"
					+ TextFormat.printToUnicodeString(gameRunnable.param));
		}
		// -------------------- 每条指令的流量统计 -------------------------------
		// --- start
		GameCmdStatistics statistics = MoonServer.cmdIdStatistics.get(cmdId);
		if (statistics == null) {
			in.resetReaderIndex();
			minaLog.error("error cmdId:" + cmdId + "\tlen:" + packetDataLen + "\tprotoLen:" + protoLen + "\tbytes:"
					+ Hex.encodeHexString(protoData));
			return;
		}
		statistics.readBytes.addAndGet(protoLen);
		// 不再多次尝试，失败就失败
		long max = statistics.readMaxBytes.get();
		if (protoLen > max) {
			statistics.readMaxBytes.compareAndSet(max, protoLen);
		} else {
			if (protoLen > max * 7 / 10) {
				statistics.readBytesCount2.incrementAndGet();
			}
		}
		// --- end
		// -------------------------------------------------------------------
	}

	private static final Log minaLog = LogFactory.getLog("mina.data");
	private static final Log log = LogFactory.getLog(NettyDecoder.class);
}
