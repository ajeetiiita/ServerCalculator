package com.ajeet.util;

import io.netty.handler.codec.http.FullHttpRequest;

public class Operands {

	private int a;
	private int b;
	private String op;
	private FullHttpRequest httpResuest;
	public int getA() {
		return a;
	}
	public void setA(int a) {
		this.a = a;
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public FullHttpRequest getHttpResuest() {
		return httpResuest;
	}
	public void setHttpResuest(FullHttpRequest httpResuest) {
		this.httpResuest = httpResuest;
	}
}
