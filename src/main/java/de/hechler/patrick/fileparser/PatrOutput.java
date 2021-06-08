package de.hechler.patrick.fileparser;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class PatrOutput extends PrintStream {

	private final Charset cs;
	private final byte[] lineSep;
	private final PrintStream[] zusatz;

	public PatrOutput(OutputStream out, Charset charset, String lineSeparator, PrintStream... zusatz) {
		super(out);
		this.out = out;
		this.cs = charset;
		this.lineSep = lineSeparator.getBytes(cs);
		this.zusatz = zusatz;
	}

	@Override
	public void print(String str) {
		byte[] bytes = str.getBytes(cs);
		super.write(bytes, 0, bytes.length);
		for (PrintStream p : zusatz) {
			p.write(bytes, 0, bytes.length);
		}
	}
	
	@Override
	public void println() {
		super.write(lineSep, 0, lineSep.length);
		for (PrintStream p : zusatz) {
			p.println();
		}
	}
	
	@Override
	public void println(String str) {
		byte[] bytes = str.getBytes(cs);
		int len = bytes.length;
		bytes = Arrays.copyOf(bytes, len + lineSep.length);
		System.arraycopy(lineSep, 0, bytes, len, lineSep.length);
		super.write(bytes, 0, bytes.length);
		for (PrintStream p : zusatz) {
			p.write(bytes, 0, bytes.length);
		}
	}
	
	
	
	@Override
	public void print(boolean b) {
		String str = Boolean.toString(b);
		byte[] bytes = str.getBytes(cs);
		super.write(bytes, 0, bytes.length);
		for (PrintStream p : zusatz) {
			p.write(bytes, 0, bytes.length);
		}
	}

}
