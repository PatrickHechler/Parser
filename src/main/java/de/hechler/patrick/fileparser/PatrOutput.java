package de.hechler.patrick.fileparser;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class PatrOutput extends PrintStream {

	private final Charset cs;
	private final byte[] lineSep;

	public PatrOutput(OutputStream out, Charset charset, String lineSeparator) {
		super(out);
		this.out = out;
		this.cs = charset;
		this.lineSep = lineSeparator.getBytes(cs);
	}

	@Override
	public void print(String str) {
		byte[] bytes = str.getBytes(cs);
		super.write(bytes, 0, bytes.length);
	}
	
	@Override
	public void println() {
		super.write(lineSep, 0, lineSep.length);
	}
	
	@Override
	public void println(String str) {
		byte[] bytes = str.getBytes(cs);
		int len = bytes.length;
		bytes = Arrays.copyOf(bytes, len + lineSep.length);
		System.arraycopy(lineSep, 0, bytes, len, lineSep.length);
	}
	
	
	
	@Override
	public void print(boolean b) {
		String str = Boolean.toString(b);
		byte[] bytes = str.getBytes(cs);
		super.write(bytes, 0, bytes.length);
	}

}
