package org.raisedeel.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class ChatClient {

  // Canal del cliente, permite escribir directamente al server
  private Channel clientChannel;
  private final String address;
  private final int port;

  public ChatClient(String address, int port) {
    this.address = address;
    this.port = port;
  }

  public Channel getClientChannel() {
    return clientChannel;
  }

  public static void main(String[] args) throws Exception {
    ChatClient client = new ChatClient("localhost", 8080);
    client.start();
    Channel channel = client.getClientChannel();
    Scanner scanner = new Scanner(System.in);

    String input;
    while (!(input = scanner.nextLine()).equals("exit")) {
      channel.writeAndFlush(Unpooled.copiedBuffer(input, StandardCharsets.UTF_8));
    }

    System.exit(0);
  }

  public void start() throws InterruptedException {
    final NioEventLoopGroup workerGroup = new NioEventLoopGroup();


    Bootstrap client = new Bootstrap()
        .group(workerGroup)
        .channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<SocketChannel>() {

          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new ChatClientHandler());
          }
        });

    clientChannel = client.connect(address, port).sync().channel();
  }

}
