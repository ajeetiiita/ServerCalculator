package com.ajeet.netty;

import org.apache.log4j.Logger;

import com.ajeet.response.HTTPResponse;
import com.ajeet.util.Operands;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponseStatus;


public class ComputationHandler extends SimpleChannelInboundHandler<Operands> {

	final static Logger logger = Logger.getLogger(ComputationHandler.class);
	public ComputationHandler() {
		System.out.println("Creating Computaion Handler");
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Operands opr) throws Exception {
		logger.info("Got Reuest in Computation Handler");
	    Integer result = calculate(opr);
	    logger.info("Result Computed \t"+result);
        HTTPResponse.sendTextResponse(ctx, opr.getHttpResuest(), HttpResponseStatus.OK, result.toString());
	}
	
	private int calculate(Operands opr) {
	    int a = opr.getA();
	    int b=  opr.getB(); 
	    String op = opr.getOp();
	    if(op.equals("add"))
	    {
	    	return a+b;
	    }
	    if(op.equals("sub"))
	    {
	    	return a-b;
	    }
	    if(op.equals("mul"))
	    {
	    	return a*b;
	    }
	    if(op.equals("div"))
	    {
	    	return a/b;
	    }
		return 0;
	}

	
}
