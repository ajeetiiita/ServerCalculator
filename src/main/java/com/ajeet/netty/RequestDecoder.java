package com.ajeet.netty;
import java.util.Map;
import com.ajeet.util.Operands;
import com.ajeet.util.QueryStringDecoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.log4j.*;

public class RequestDecoder extends SimpleChannelInboundHandler<FullHttpRequest>{
	final static Logger logger = Logger.getLogger(RequestDecoder.class);
	public RequestDecoder() {
		System.out.println("Creating Request Decoder");
	}	
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
		logger.info("Recieved Http Request");
		logger.info("The request is \t"+httpRequest.toString());
	    String uri = httpRequest.getUri();
	    QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
		Map<String, String> queryParameters = queryStringDecoder.getParameters();
	    Operands o = new Operands();
	    o.setA(Integer.parseInt(queryParameters.get("a")));
	    o.setB(Integer.parseInt(queryParameters.get("b")));
	    o.setOp(queryParameters.get("op"));
	    o.setHttpResuest(httpRequest);
	    logger.info("a: {}"+o.getA());
	    logger.info("b: {}"+o.getB());
	    logger.info("Oper: {}"+o.getOp());
	    ctx.fireChannelRead(o);
	}
}
