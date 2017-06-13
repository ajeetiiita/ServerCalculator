package com.ajeet.netty;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import com.ajeet.netty.RequestDecoder;
import com.ajeet.netty.ComputationHandler;

public class DefaultServerInitializer extends ChannelInitializer<SocketChannel>{

	public DefaultServerInitializer() {
		/* Constructor for Default Server Initializer */
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		p.addLast("decoder", new HttpRequestDecoder(8192, 8192 * 2, 8192 * 2));
		p.addLast("encoder", new HttpResponseEncoder());
		p.addLast("httpObjectAggregator", new HttpObjectAggregator(1048576));
		p.addLast("Request Decoder", new RequestDecoder());
		p.addLast("Computation Handler", new ComputationHandler());
	}

}
