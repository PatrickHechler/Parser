package de.hechler.patrick.fileparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

public class Serializer {
	private static final int CHAR_ARRAY = 18;
	private static final int BOOLEAN_ARRAY = 17;
	private static final int FLOAT_ARRAY = 16;
	private static final int DOUBLE_ARRAY = 15;
	private static final int SHORT_ARRAY = 14;
	private static final int LONG_ARRAY = 13;
	private static final int BYTE_ARRAY = 12;
	private static final int INT_ARRAY = 11;
	private static final int OBJECT_ARRAY = 10;
	private static final int OBJECT_STRING = 9;
	private static final int PRIMITIVE_SHORT = 8;
	private static final int PRIMITIVE_CHAR = 7;
	private static final int PRIMITIVE_BYTE = 6;
	private static final int PRIMITIVE_LONG = 5;
	private static final int PRIMITIVE_INT = 4;
	private static final int PRIMITIVE_DOUBLE = 3;
	private static final int PRIMITIVE_FLOAT = 2;
	private static final int PRIMITIVE_BOOLEAN = 1;
	private static final int NULL = -1;

	public static void load(Object obj, File loadFile) throws IOException {
//		OutputStream out = new FileOutputStream(saveFile);
//		Field[] fields = getClass().getDeclaredFields();
//		for (int i = 0; i < fields.length; i++) {
//			int mod = fields[i].getModifiers();
//			if ((mod & Modifier.TRANSIENT) != 0)
//				continue;// transient say do not serialize this
//			if ((mod & Modifier.FINAL) != 0)
//				continue;// can't set finals by loading anyway
//			save(fields[i], out);
//		}
		Class<?> myClass = obj.getClass();
		InputStream in = new FileInputStream(loadFile);
		while (true) {
			try {
				String name = readString(in);
				Field field = myClass.getField(name);
				byte[] bytes = new byte[Integer.BYTES];
				in.read(bytes);
				int identy = byteArrToInt(bytes, 0);
				switch (identy) {
				case NULL:
					field.set(obj, null);
					break;
				case 1:
//				if (type == Boolean.Type) {
//					out.write(1);
//					boolean bool = (boolean) (Boolean) val;
//					out.write(bool ? 1 : 0);
					int val = in.read();
					if (val != 1 && val != 0) {
						throw new AssertionError("expected to read 0 or 1, but got: " + val);
					}
					field.setBoolean(obj, val == 1);
					break;
				case 2:// TODO continue here
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
				case 6:
					break;
				case 7:
					break;
				case 8:
					break;
				case 9:
					break;
				case 10:
					break;
				default:
					throw new AssertionError("unknown identifier: " + identy);
				}

				// TODO Auto-generated method stub
				throw new RuntimeException("noch nicht gemacht!");
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
				throw new InternalError("does not know the loaded field with name: ", e);
			} catch (SecurityException e) {
				e.printStackTrace();
				throw new InternalError("the permission to acces my field was denied by the security manager!", e);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new InternalError("illegal argument: field.set.*(obj{'" + obj + "'", e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new InternalError(
						"security manager denied the permission to acces saved field of class: " + obj.getClass(), e);
			}
		}
	}

	public static void save(Object obj, File saveFile, boolean saveStatic) throws IOException {
		OutputStream out = new FileOutputStream(saveFile);
		Field[] fields = obj.getClass().getDeclaredFields();
		Field.setAccessible(fields, true);
		for (int i = 0; i < fields.length; i++) {
			int mod = fields[i].getModifiers();
			if ((mod & Modifier.TRANSIENT) != 0)
				continue;// transient say do not serialize this
			if ((mod & Modifier.FINAL) != 0)
				continue;// can't set finals by loading anyway
			if (!saveStatic && (mod & Modifier.STATIC) != 0)
				continue;// can't set finals by loading anyway
			save(obj, fields[i], out);
		}
	}

	private static void save(Object obj, Field field, OutputStream out) throws IOException {
		{// write name
			String name = field.getName();
			writeString(out, name);
		}
		{// write value
			try {
				Class<?> type = field.getType();
				Object val = field.get(obj);
				writeValue(type, val, out);
			} catch (IllegalArgumentException e) {
				throw new InternalError("i do not know anything about this: " + e.getMessage(), e);
			} catch (IllegalAccessException e) {
				throw new InternalError("can not acces on my own fields: " + e.getMessage(), e);
			}
		}
	}

	private static void writeValue(Class<?> type, Object val, OutputStream out) throws InternalError, IOException {
		{// write value
			if (type.isPrimitive()) {
				if (type == Boolean.TYPE) {
					out.write(PRIMITIVE_BOOLEAN);
					boolean bool = (boolean) (Boolean) val;
					out.write(bool ? 1 : 0);
				} else if (type == Integer.TYPE) {
					out.write(PRIMITIVE_INT);
					out.write(intToByteArr((int) (Integer) val));
				} else if (type == Long.TYPE) {
					out.write(PRIMITIVE_LONG);
					byte[] bytes = new byte[Long.BYTES];
					intToByteArr(bytes, 0, (int) ((long) (Long) val) << 0);
					intToByteArr(bytes, Integer.BYTES, (int) ((long) (Long) val) << 32);
					out.write(bytes);
				} else if (type == Byte.TYPE) {
					out.write(PRIMITIVE_BYTE);
					out.write(((int) (byte) (Byte) val) & 0xFF);
				} else if (type == Short.TYPE) {
					out.write(PRIMITIVE_SHORT);
					short s = (short) (Short) val;
					byte[] bytes = new byte[2];
					bytes[0] = (byte) (s & 0xFF);
					bytes[1] = (byte) ((s & 0xFF) << 8);
					out.write(bytes);
				} else if (type == Character.TYPE) {
					out.write(PRIMITIVE_CHAR);
					char c = (char) (Character) val;
					// char has two bytes
					byte[] bytes = new byte[Character.BYTES];
					for (int i = 0; i < bytes.length; i++) {
						bytes[i] = (byte) (c >> (i << 3)); // Bytes.SIZE = 8 = 1 << 3
					}
					out.write(bytes);
				} else if (type == Double.TYPE) {
					out.write(PRIMITIVE_DOUBLE);
					long lval = Double.doubleToRawLongBits((double) (Double) val);
					byte[] bytes = new byte[Long.BYTES];
					intToByteArr(bytes, 0, (int) lval);
					intToByteArr(bytes, Integer.BYTES, (int) (lval << 32));
					out.write(bytes);
				} else if (type == Float.TYPE) {
					out.write(PRIMITIVE_FLOAT);
					int ival = Float.floatToRawIntBits((float) (Float) val);
					byte[] bytes = new byte[Integer.BYTES];
					intToByteArr(bytes, 0, ival);
					out.write(bytes);
				} else {
					throw new InternalError("unknown primitive type: " + type.getName() + " with value: " + val);
				}
			} else {
				if (val == null) {
					out.write(NULL);
				} else if (type == String.class) {
					out.write(OBJECT_STRING);
					String str = (String) val;
					writeString(out, str);
				} else if (type.isArray()) {
					if (type == int[].class) {
					} else if (type == byte[].class) {
						out.write(BYTE_ARRAY);
						byte[] zw = (byte[]) val;
						byte[] bytes = new byte[zw.length + Integer.BYTES];
						System.arraycopy(zw, 0, bytes, Integer.BYTES, zw.length);
						intToByteArr(bytes, 0, zw.length);
						out.write(bytes);
					} else if (type == int[].class) {
						out.write(INT_ARRAY);
						int[] iaval = (int[]) val;
						byte[] bytes = new byte[Integer.BYTES];
						intToByteArr(bytes, 0, iaval.length);
						out.write(bytes);
						for (int i = 0; i < iaval.length; i++) {
							bytes = new byte[Integer.BYTES];
							intToByteArr(bytes, 0, iaval[i]);
							out.write(bytes);
						}
					} else if (type == long[].class) {
						out.write(LONG_ARRAY);
						long[] laval = (long[]) val;
						byte[] bytes = new byte[Integer.BYTES];
						intToByteArr(bytes, 0, laval.length);
						out.write(bytes);
						for (int i = 0; i < laval.length; i++) {
							bytes = new byte[Integer.BYTES];
							intToByteArr(bytes, 0, (int) laval[i]);
							intToByteArr(bytes, Integer.BYTES, (int) (laval[i] << 32));
							out.write(bytes);
						}
					} else if (type == short[].class) {
						out.write(SHORT_ARRAY);
						short[] saval = (short[]) val;
						byte[] bytes = new byte[Integer.BYTES];
						intToByteArr(bytes, 0, saval.length);
						out.write(bytes);
						for (int i = 0; i < saval.length; i++) {
							bytes = new byte[Short.BYTES];
							bytes[0] = (byte) saval[0];
							bytes[0] = (byte) (saval[1] << 8);
						}
					} else if (type == double[].class) {
						out.write(DOUBLE_ARRAY);
						double[] daval = (double[]) val;
						byte[] bytes = new byte[Integer.BYTES];
						intToByteArr(bytes, 0, daval.length);
						out.write(bytes);
						for (int i = 0; i < daval.length; i++) {
							bytes = new byte[Integer.BYTES];
							long zwl = Double.doubleToRawLongBits(daval[i]);
							intToByteArr(bytes, 0, (int) zwl);
							intToByteArr(bytes, Integer.BYTES, (int) (zwl << 32));
							out.write(bytes);
						}
					} else if (type == float[].class) {
						out.write(FLOAT_ARRAY);
						float[] iaval = (float[]) val;
						byte[] bytes = new byte[Integer.BYTES];
						intToByteArr(bytes, 0, iaval.length);
						out.write(bytes);
						for (int i = 0; i < iaval.length; i++) {
							bytes = new byte[Integer.BYTES];
							intToByteArr(bytes, 0, Float.floatToRawIntBits(iaval[i]));
							out.write(bytes);
						}
					} else if (type == boolean[].class) {
						out.write(BOOLEAN_ARRAY);
						boolean[] baval = (boolean[]) val;
						byte[] bytes = new byte[Integer.BYTES];
						intToByteArr(bytes, 0, baval.length);
						out.write(bytes);
						bytes = new byte[(baval.length >> 3) + (((baval.length & 0x7) == 0) ? 0 : 1)];
						int i, ii;
						for (i = 0, ii = 0; i < (baval.length >> 3); i++) {
							bytes[i] = (byte) (baval[ii++] ? 0x01 : 0);
							bytes[i] |= (byte) (baval[ii++] ? 0x02 : 0);
							bytes[i] |= (byte) (baval[ii++] ? 0x04 : 0);
							bytes[i] |= (byte) (baval[ii++] ? 0x08 : 0);
							bytes[i] |= (byte) (baval[ii++] ? 0x10 : 0);
							bytes[i] |= (byte) (baval[ii++] ? 0x20 : 0);
							bytes[i] |= (byte) (baval[ii++] ? 0x40 : 0);
							bytes[i] |= (byte) (baval[ii++] ? 0x80 : 0);
						}
						if (ii < baval.length) {
							bytes[i] = (byte) (baval[ii++] ? 0x01 : 0);
							if (ii < baval.length) {
								bytes[i] |= (byte) (baval[ii++] ? 0x02 : 0);
								if (ii < baval.length) {
									bytes[i] |= (byte) (baval[ii++] ? 0x04 : 0);
									if (ii < baval.length) {
										bytes[i] |= (byte) (baval[ii++] ? 0x08 : 0);
										if (ii < baval.length) {
											bytes[i] |= (byte) (baval[ii++] ? 0x10 : 0);
											if (ii < baval.length) {
												bytes[i] |= (byte) (baval[ii++] ? 0x20 : 0);
												if (ii < baval.length) {
													bytes[i] |= (byte) (baval[ii++] ? 0x40 : 0);
													if (ii < baval.length) {
														bytes[i] |= (byte) (baval[ii] ? 0x80 : 0);
													}
												}
											}
										}
									}
								}
							}
						}
					} else if (type == char[].class) {
						out.write(CHAR_ARRAY);
						char[] saval = (char[]) val;
						byte[] bytes = new byte[Integer.BYTES];
						intToByteArr(bytes, 0, saval.length);
						out.write(bytes);
						for (int i = 0; i < saval.length; i++) {
							bytes = new byte[Character.BYTES];
							bytes[0] = (byte) saval[0];
							bytes[0] = (byte) (saval[1] << 8);
						}
					} else {
						out.write(OBJECT_ARRAY);
						Object[] objs = (Object[]) val;
						writeString(out, objs.getClass().getName());//first write name (because the value class can be a sub class of the type)
						out.write(intToByteArr(objs.length));
						for (int i = 0; i < objs.length; i++) {
							writeValue(objs[i].getClass(), objs[i], out);
						}
					}
				} else if (Collection.class.isAssignableFrom(type)) {
					out.write(9);
					Collection<?> col = (Collection<?>) val;
					writeString(out, col.getClass().getName());//first write name (because the value class can be a sub class of the type)
					out.write(intToByteArr(col.size()));
					//TODO do
				}else {
					//TODO do
					
				}
			}
		}
	}

	private static String readString(InputStream in) throws IOException {
//		byte[] zw = write.getBytes(StandardCharsets.UTF_8);
//		byte[] bytes = new byte[zw.length + Integer.BYTES];
//		intToByteArr(bytes, 0, bytes.length);
//		System.arraycopy(zw, 0, bytes, Integer.BYTES, zw.length);
//		out.write(bytes);
		byte[] bytes = new byte[Integer.BYTES];
		in.read(bytes);
		int len = byteArrToInt(bytes, 0);
		bytes = new byte[len];
		in.read(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	private static void writeString(OutputStream out, String write) throws IOException {
		byte[] zw = write.getBytes(StandardCharsets.UTF_8);
		byte[] bytes = new byte[zw.length + Integer.BYTES];
		intToByteArr(bytes, 0, bytes.length);
		System.arraycopy(zw, 0, bytes, Integer.BYTES, zw.length);
		out.write(bytes);
	}

	private static int byteArrToInt(byte[] bytes, int off) {
		int ret = 0;
		for (int i = 0; i < Integer.BYTES; i++) {
			ret |= (((int) bytes[off + i]) & 0xFF) << (i << 3);
		}
		return ret;
	}

	private static void intToByteArr(byte[] bytes, int off, int val) {
		for (int i = 0; i < Integer.BYTES; i++) {
			bytes[i + off] = (byte) (val >> (i << 3));
		}
	}

	private static byte[] intToByteArr(int val) {
		byte[] bytes = new byte[Integer.BYTES];
		intToByteArr(bytes, 0, val);
		return bytes;
	}

}
