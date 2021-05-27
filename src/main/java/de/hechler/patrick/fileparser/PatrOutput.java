package de.hechler.patrick.fileparser;

import java.io.*;
import java.nio.charset.Charset;

public class PatrOutput extends PrintStream {
	
	private final Charset cs;
	
	public PatrOutput(OutputStream out, Charset charset) {
		super(out);
		this.out = out;
		this.cs = charset;
	}
	
	@Override
	public void print(String str) {
		byte[] bytes = str.getBytes(cs);
		/*int i = */write(bytes, 0 , bytes.length);
		/*for (; i < bytes.length; i++) {
			write(((int)bytes[i]) & 0xFF);
		}*/
	}
	
}
