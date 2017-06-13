package com.ajeet.response;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HTTPResponse {
	
	public static void sendTextResponse(ChannelHandlerContext ctx, FullHttpRequest request, HttpResponseStatus status,
			String responseBody) {
		sendTextResponse(null, ctx, request, status, responseBody, false);
	}
	
	public static void sendTextResponse(String callID, ChannelHandlerContext ctx, FullHttpRequest request,
			HttpResponseStatus status, String responseBody, boolean removeFromMaps) {
		sendHttpStringResponse(callID, ctx, request, status, responseBody, "text/plain; charset=UTF-8", removeFromMaps,
				false);
	}
	
	
	private static void sendHttpStringResponse(String callID, ChannelHandlerContext ctx, FullHttpRequest request,
			HttpResponseStatus status, String responseBody, String contentType, boolean removeFromMaps, boolean isSI) {
		sendHttpResponse(callID, ctx, request, status, responseBody, contentType, removeFromMaps, false, isSI);
	}
	
	private static void sendHttpResponse(String callID, ChannelHandlerContext ctx, FullHttpRequest request,
			HttpResponseStatus status, Object responseBody, String contentType, boolean removeFromMaps,
			boolean isByteBuffer, boolean isSI) {

		HttpResponse response = null;
		HttpHeaders respHeaders = null;
		if (responseBody != null) {
			ByteBuf buf = null;
			byte[] byteArr;
			if (true == isByteBuffer) {
				byteArr = (byte[]) responseBody;
				buf = Unpooled.copiedBuffer(byteArr);
			} else {
				// String resp = (String) responseBody;
				// byteArr = resp.getBytes(CharsetUtil.UTF_8);
				buf = Unpooled.copiedBuffer((String) responseBody, CharsetUtil.UTF_8);
			}
			// buf = ctx.alloc().buffer(byteArr.length).writeBytes(byteArr);
		//	FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
			FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);                       
			respHeaders = fullHttpResponse.headers();
			response = fullHttpResponse;
		} else {
			response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
			respHeaders = response.headers();
		}
		boolean isKeepAlive = false;
		sendResponse(ctx, response, isKeepAlive);
	}
	
	
	private static void sendResponse(final ChannelHandlerContext ctx, HttpResponse response, boolean isKeepAlive) {
		if (isKeepAlive) {
			System.out.println("I am keep Alive");
			ctx.writeAndFlush(response);
		} else {
			System.out.println("I am not keep alive");
			ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture arg0) throws Exception {
					System.out.println("done");
					ctx.close();					
				}
			}); 
		}
	}
}
