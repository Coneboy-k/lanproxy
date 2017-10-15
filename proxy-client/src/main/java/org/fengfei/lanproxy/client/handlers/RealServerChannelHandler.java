package org.fengfei.lanproxy.client.handlers;

import org.fengfei.lanproxy.client.ClientChannelMannager;
import org.fengfei.lanproxy.protocol.ProxyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理服务端 channel.
 */
public class RealServerChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static Logger logger = LoggerFactory.getLogger(RealServerChannelHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {

        Channel realServerChannel = ctx.channel();
        Channel channel = ClientChannelMannager.getChannel();
        if (channel == null) {
            // 代理客户端连接断开
            ctx.channel().close();
        } else {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            String userId = ClientChannelMannager.getRealServerChannelUserId(realServerChannel);
            ProxyMessage proxyMessage = new ProxyMessage();
            proxyMessage.setType(ProxyMessage.TYPE_TRANSFER);
            proxyMessage.setUri(userId);
            proxyMessage.setData(bytes);
            channel.writeAndFlush(proxyMessage);
            logger.debug("write data to proxy server, {}, {}", realServerChannel, channel);

            logger.info("RC: 远程服务返回数据userId={} length={}",userId,bytes.length);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel realServerChannel = ctx.channel();

        super.channelActive(ctx);

        logger.info("RC: 目的服务器 已连接 连接：info={} 可写入状态为={} " ,realServerChannel.toString(),realServerChannel.isWritable());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel realServerChannel = ctx.channel();
        String userId = ClientChannelMannager.getRealServerChannelUserId(realServerChannel);
        ClientChannelMannager.removeRealServerChannel(userId);
        Channel channel = ClientChannelMannager.getChannel();
        if (channel != null) {
            logger.debug("channelInactive, {}", realServerChannel);
            ProxyMessage proxyMessage = new ProxyMessage();
            proxyMessage.setType(ProxyMessage.TYPE_DISCONNECT);
            proxyMessage.setUri(userId);
            channel.writeAndFlush(proxyMessage);
        }
        logger.info("RC: 目的服务器 已关闭 连接：info={} 可写入状态为={} " ,realServerChannel.toString(),realServerChannel.isWritable());

        super.channelInactive(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

        Channel realServerChannel = ctx.channel();
        String userId = ClientChannelMannager.getRealServerChannelUserId(realServerChannel);
        // 获取连接代理服务的远程连接
        Channel channel = ClientChannelMannager.getChannel();
        if (channel != null) {
            ProxyMessage proxyMessage = new ProxyMessage();
            proxyMessage.setType(ProxyMessage.TYPE_WRITE_CONTROL);
            proxyMessage.setUri(userId);
            proxyMessage.setData(realServerChannel.isWritable() ? new byte[] { 0x01 } : new byte[] { 0x00 });
            channel.writeAndFlush(proxyMessage);
            logger.info("RC：channelWritabilityChanged :客户端和远程服务器的连接断开 ={}",proxyMessage.toString());
        }

        logger.info("RC: 目的服务器状态发生变化 连接：info={} 可写入状态为={} " ,realServerChannel.toString(),realServerChannel.isWritable());
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        Channel realServerChannel = ctx.channel();
        logger.info("RC: 目的服务器状态出现异常info={} exception={} " ,realServerChannel.toString(),cause);
        super.exceptionCaught(ctx, cause);
    }
}