package org.raisedeel.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;

import java.nio.charset.StandardCharsets;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {

  private final ChannelGroup serverChildren;

  public ChatServerHandler(ChannelGroup clients) {
    this.serverChildren = clients;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("Connection established with client.");
    System.out.println("Start writing messages below... \n");
    serverChildren.add(ctx.channel());
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf byteBuf = (ByteBuf) msg;
    System.out.println("Client: " + byteBuf.toString(StandardCharsets.UTF_8));
    byteBuf.release();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
  }
}
