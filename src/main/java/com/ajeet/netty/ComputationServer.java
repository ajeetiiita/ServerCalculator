package com.ajeet.netty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ComputationServer {

	public void run() throws Exception {
		final EventLoopGroup bossGroup = new NioEventLoopGroup();
		final EventLoopGroup workerGroup = new NioEventLoopGroup(256);
		try {

			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new DefaultServerInitializer());
					
			Channel ch = b.bind(9090).sync().channel();			
			//logger.info("NettyRPC server listening on port "+ port + " and ready for connections...");
			System.out.println("Computation Server is Listening to Port");
	         Runtime.getRuntime().addShutdownHook(new Thread(){
	                @Override
	                public void run(){
	                    bossGroup.shutdownGracefully();
	                    workerGroup.shutdownGracefully();   
	                }
	            });
			ch.closeFuture().sync();

		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		new ComputationServer().run();
	}
}
