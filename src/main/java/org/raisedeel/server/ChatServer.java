package org.raisedeel.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatServer {

  // Estructura que permite guardar canales, en este caso se guardan
  // las conexiones que va recibiendo el servidor
  private final ChannelGroup serverChildren;
  private final int port;

  public ChatServer(int port) {
    this.port = port;
    serverChildren = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
  }

  public ChannelGroup getServerChildren() {
    return serverChildren;
  }

  public static void main(String[] args) throws Exception {
    ChatServer server = new ChatServer(8080);
    server.start();
    ChannelGroup channels = server.getServerChildren();
    Scanner scanner = new Scanner(System.in);

    String input;
    while (!(input = scanner.nextLine()).equals("exit")) {
      channels.writeAndFlush(Unpooled.copiedBuffer(input, StandardCharsets.UTF_8));
    }

    System.exit(0);
  }

  public void start() throws InterruptedException {
    final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    ServerBootstrap server = new ServerBootstrap()
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(
            new ChannelInitializer<SocketChannel>() {

              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ChatServerHandler(serverChildren));
              }
            }
        );

    ChannelFuture f = server.bind(port).sync();
  }
}

