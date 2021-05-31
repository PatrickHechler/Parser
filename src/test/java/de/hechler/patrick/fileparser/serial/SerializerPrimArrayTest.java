package de.hechler.patrick.fileparser.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

import de.hechler.patrick.zeugs.check.Checker;
import de.hechler.patrick.zeugs.check.anotations.Check;
import de.hechler.patrick.zeugs.check.anotations.CheckClass;
import de.hechler.patrick.zeugs.check.anotations.Start;

@CheckClass
public class SerializerPrimArrayTest extends Checker {

	Serializer serializer;
	Deserializer deserializer;
	
	@Start
	public void setup() {
		serializer = new Serializer(false, true, false, true, true);
		deserializer = new Deserializer(Collections.emptyMap());
	}
	
	
	public <T> T readObject(Class<T> tClazz, InputStream in) throws IOException {
		return readObject(deserializer, tClazz, in);
	}
	public static <T> T readObject(Deserializer deser, Class<T> tClazz, InputStream in) throws IOException {
		Object result = deser.readObject(in);
		if (result == null) {
			return null;
		}
		if (!tClazz.isInstance(result)) {
			fail("Wrong instance read: "+ result.getClass().getName()+" instead of "+tClazz.getName());
		}
		return tClazz.cast(result);
	}

	
	public static class BooleanArrBean {
		public boolean[] value = new boolean[2];
		public BooleanArrBean() {
			this(false,false);
		}
		public BooleanArrBean(boolean value1, boolean value2) {
			this.value[0] = value1;
			this.value[1] = value2;
		}
		@Override
		public String toString() {
			return "[" + value[0] + "," + value[1] + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value[0], value[1]);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BooleanArrBean other = (BooleanArrBean) obj;
			return value[0] == other.value[0] && value[1] == other.value[1];
		}
	}
	
	public static class ByteArrBean {
		public byte[] value = new byte[2];
		public ByteArrBean() {
			this((byte)0,(byte)0);
		}
		public ByteArrBean(byte value1, byte value2) {
			this.value[0] = value1;
			this.value[1] = value2;
		}
		@Override
		public String toString() {
			return "[" + value[0] + "," + value[1] + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value[0], value[1]);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ByteArrBean other = (ByteArrBean) obj;
			return value[0] == other.value[0] && value[1] == other.value[1];
		}
	}
	
	public static class CharArrBean {
		public char[] value = new char[2];
		public CharArrBean() {
			this('\0','\0');
		}
		public CharArrBean(char value1, char value2) {
			this.value[0] = value1;
			this.value[1] = value2;
		}
		@Override
		public String toString() {
			return "[" + value[0] + "," + value[1] + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value[0], value[1]);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CharArrBean other = (CharArrBean) obj;
			return value[0] == other.value[0] && value[1] == other.value[1];
		}
	}

	public static class ShortArrBean {
		public short[] value = new short[2];
		public ShortArrBean() {
			this((short)0,(short)0);
		}
		public ShortArrBean(short value1, short value2) {
			this.value[0] = value1;
			this.value[1] = value2;
		}
		@Override
		public String toString() {
			return "[" + value[0] + "," + value[1] + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value[0], value[1]);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ShortArrBean other = (ShortArrBean) obj;
			return value[0] == other.value[0] && value[1] == other.value[1];
		}
	}
	
	public static class IntArrBean {
		public int[] value = new int[2];
		public IntArrBean() {
			this(0,0);
		}
		public IntArrBean(int value1, int value2) {
			this.value[0] = value1;
			this.value[1] = value2;
		}
		@Override
		public String toString() {
			return "[" + value[0] + "," + value[1] + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value[0], value[1]);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IntArrBean other = (IntArrBean) obj;
			return value[0] == other.value[0] && value[1] == other.value[1];
		}
	}
	
	public static class LongArrBean {
		public long[] value = new long[2];
		public LongArrBean() {
			this(0L,0L);
		}
		public LongArrBean(long value1, long value2) {
			this.value[0] = value1;
			this.value[1] = value2;
		}
		@Override
		public String toString() {
			return "[" + value[0] + "," + value[1] + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value[0], value[1]);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LongArrBean other = (LongArrBean) obj;
			return value[0] == other.value[0] && value[1] == other.value[1];
		}
	}
	
	public static class FloatArrBean {
		public float[] value = new float[2];
		public FloatArrBean() {
			this(0.0f,0.0f);
		}
		public FloatArrBean(float value1, float value2) { 
			this.value[0] = value1;
			this.value[1] = value2;
		}
		@Override
		public String toString() {
			return "[" + value[0] + "," + value[1] + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value[0], value[1]);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FloatArrBean other = (FloatArrBean) obj;
			return value[0] == other.value[0] && value[1] == other.value[1];
		}
	}
	
	public static class DoubleArrBean {
		public double[] value = new double[2];
		public DoubleArrBean() {
			this(0.0,0.0);
		}
		public DoubleArrBean(double value1, double value2) { 
			this.value[0] = value1;
			this.value[1] = value2;
		}
		@Override
		public String toString() {
			return "[" + value[0] + "," + value[1] + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value[0], value[1]);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DoubleArrBean other = (DoubleArrBean) obj;
			return value[0] == other.value[0] && value[1] == other.value[1];
		}
	}
	

	private static Random rand = new Random();
	
	public static class BigArraysBean {
		public boolean[] booleanValues;
		public byte[] byteValues;
		public short[] shortValues;
		public char[] charValues;
		public int[] intValues;
		public long[] longValues;
		public float[] floatValues;
		public double[] doubleValues;
		public BigArraysBean() {
		}
		public void generateBooleanValues(int size) {
			booleanValues = new boolean[size];
			for (int i=0; i<size; i++) {
				booleanValues[i] = rand.nextBoolean();
			}
		}
		public void generateByteValues(int size) {
			byteValues = new byte[size];
			rand.nextBytes(byteValues);
		}
		public void generateShortValues(int size) {
			shortValues = new short[size];
			for (int i=0; i<size; i++) {
				shortValues[i] = (short) (rand.nextInt(((int)Short.MAX_VALUE)-((int)Short.MIN_VALUE))+((int)Short.MIN_VALUE));
			}
		}
		public void generateCharValues(int size) {
			charValues = new char[size];
			for (int i=0; i<size; i++) {
				charValues[i] = (char) (rand.nextInt(((int)Character.MAX_VALUE)-((int)Character.MIN_VALUE))+((int)Character.MIN_VALUE));
			}
		}
		public void generateIntValues(int size) {
			intValues = new int[size];
			for (int i=0; i<size; i++) {
				intValues[i] = rand.nextInt();
			}
		}
		public void generateLongValues(int size) {
			longValues = new long[size];
			for (int i=0; i<size; i++) {
				longValues[i] = rand.nextLong();
			}
		}
		public void generateFloatValues(int size) {
			floatValues = new float[size];
			for (int i=0; i<size; i++) {
				floatValues[i] = rand.nextFloat();
			}
		}
		public void generateDoubleValues(int size) {
			doubleValues = new double[size];
			for (int i=0; i<size; i++) {
				doubleValues[i] = rand.nextDouble();
			}
		}
		public void resetAll() {
			booleanValues = null;
			byteValues = null;
			shortValues = null;
			charValues = null;
			intValues = null;
			longValues = null;
			floatValues = null;
			doubleValues = null;
		}
		public void generateAll(int booleanSize, int byteSize, int shortSize, int charSize, int intSize, int longSize, int floatSize, int doubleSize) {
			if (booleanSize>=0) {
				generateBooleanValues(booleanSize);
			}
			if (byteSize>=0) {
				generateByteValues(byteSize);
			}
			if (shortSize>=0) {
				generateShortValues(shortSize);
			}
			if (charSize>=0) {
				generateCharValues(charSize);
			}
			if (intSize>=0) {
				generateIntValues(intSize);
			}
			if (longSize>=0) {
				generateLongValues(longSize);
			}
			if (floatSize>=0) {
				generateFloatValues(floatSize);
			}
			if (doubleSize>=0) {
				generateDoubleValues(doubleSize);
			}
		}
		@Override
		public String toString() {
			return "["
					+ "booleans:" + (booleanValues==null? "null" : ""+booleanValues.length) + ","
					+ "bytes:" + 	(byteValues==null	? "null" : ""+byteValues.length) 	+ ","
					+ "shorts:" + 	(shortValues==null	? "null" : ""+shortValues.length) 	+ ","
					+ "chars:" + 	(charValues==null	? "null" : ""+charValues.length) 	+ ","
					+ "ints:" + 	(intValues==null	? "null" : ""+intValues.length) 	+ ","
					+ "longs:" + 	(longValues==null	? "null" : ""+longValues.length) 	+ ","
					+ "floats:" + 	(floatValues==null	? "null" : ""+floatValues.length) 	+ ","
					+ "doubles:" + 	(doubleValues==null	? "null" : ""+doubleValues.length)
				+ "]";
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(booleanValues);
			result = prime * result + Arrays.hashCode(byteValues);
			result = prime * result + Arrays.hashCode(charValues);
			result = prime * result + Arrays.hashCode(doubleValues);
			result = prime * result + Arrays.hashCode(floatValues);
			result = prime * result + Arrays.hashCode(intValues);
			result = prime * result + Arrays.hashCode(longValues);
			result = prime * result + Arrays.hashCode(shortValues);
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof BigArraysBean))
				return false;
			BigArraysBean other = (BigArraysBean) obj;
			return Arrays.equals(booleanValues, other.booleanValues) && Arrays.equals(byteValues, other.byteValues)
					&& Arrays.equals(charValues, other.charValues) && Arrays.equals(doubleValues, other.doubleValues)
					&& Arrays.equals(floatValues, other.floatValues) && Arrays.equals(intValues, other.intValues)
					&& Arrays.equals(longValues, other.longValues) && Arrays.equals(shortValues, other.shortValues);
		}

	}
	

	
	@Check
	public void testSerializeBoolean() throws IOException {
		BooleanArrBean zero = new BooleanArrBean(false,false);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		BooleanArrBean readZero = readObject(BooleanArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		BooleanArrBean max = new BooleanArrBean(true, true);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		BooleanArrBean readMax = readObject(BooleanArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		BooleanArrBean mix = new BooleanArrBean(true, false);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, mix);
		BooleanArrBean readMix = readObject(BooleanArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(mix, readMix);
		
		BooleanArrBean mix2 = new BooleanArrBean(false, true);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, mix2);
		BooleanArrBean readMix2 = readObject(BooleanArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(mix2, readMix2);
	}

	@Check
	public void testSerializeByte() throws IOException {
		ByteArrBean zero = new ByteArrBean((byte)0,(byte)0);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		ByteArrBean readZero = readObject(ByteArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		ByteArrBean max = new ByteArrBean(Byte.MAX_VALUE, Byte.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		ByteArrBean readMax = readObject(ByteArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		ByteArrBean min = new ByteArrBean(Byte.MIN_VALUE, Byte.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		ByteArrBean readMin = readObject(ByteArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		ByteArrBean seq = new ByteArrBean((byte)123, (byte)-98);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		ByteArrBean readSeq = readObject(ByteArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	
	@Check
	public void testSerializeShort() throws IOException {
		ShortArrBean zero = new ShortArrBean((short)0,(short)0);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		ShortArrBean readZero = readObject(ShortArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		ShortArrBean max = new ShortArrBean(Short.MAX_VALUE, Short.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		ShortArrBean readMax = readObject(ShortArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		ShortArrBean min = new ShortArrBean(Short.MIN_VALUE, Short.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		ShortArrBean readMin = readObject(ShortArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		ShortArrBean seq = new ShortArrBean((short)12356, (short)21098);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		ShortArrBean readSeq = readObject(ShortArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	
	@Check
	public void testSerializeChar() throws IOException {
		CharArrBean zero = new CharArrBean((char)0,(char)0);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		CharArrBean readZero = readObject(CharArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		CharArrBean max = new CharArrBean(Character.MAX_VALUE, Character.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		CharArrBean readMax = readObject(CharArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		CharArrBean min = new CharArrBean(Character.MIN_VALUE, Character.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		CharArrBean readMin = readObject(CharArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		CharArrBean seq = new CharArrBean((char)12356, (char)21098);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		CharArrBean readSeq = readObject(CharArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	
	@Check
	public void testSerializeInt() throws IOException {
		IntArrBean zero = new IntArrBean(0,0);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		IntArrBean readZero = readObject(IntArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		IntArrBean max = new IntArrBean(Integer.MAX_VALUE, Integer.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		IntArrBean readMax = readObject(IntArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		IntArrBean min = new IntArrBean(Integer.MIN_VALUE, Integer.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		IntArrBean readMin = readObject(IntArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		IntArrBean seq = new IntArrBean(1234567890, 987654321);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		IntArrBean readSeq = readObject(IntArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	
	@Check
	public void testSerializeLong() throws IOException {
		LongArrBean zero = new LongArrBean(0L,0L);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		LongArrBean readZero = readObject(LongArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		LongArrBean max = new LongArrBean(Long.MAX_VALUE, Long.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		LongArrBean readMax = readObject(LongArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		LongArrBean min = new LongArrBean(Long.MIN_VALUE, Long.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		LongArrBean readMin = readObject(LongArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		LongArrBean seq = new LongArrBean(1234567890123456789L, 987654321098765432L);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		LongArrBean readSeq = readObject(LongArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}

	@Check
	public void testSerializeFloat() throws IOException {
		FloatArrBean zero = new FloatArrBean(0.0f,0.0f);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		FloatArrBean readZero = readObject(FloatArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		FloatArrBean max = new FloatArrBean(Float.MAX_VALUE, Float.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		FloatArrBean readMax = readObject(FloatArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		FloatArrBean min = new FloatArrBean(Float.MIN_VALUE, Float.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		FloatArrBean readMin = readObject(FloatArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		FloatArrBean min_norm = new FloatArrBean(Float.MIN_NORMAL, Float.MIN_NORMAL);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min_norm);
		FloatArrBean readMin_norm = readObject(FloatArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min_norm, readMin_norm);
		
		FloatArrBean seq = new FloatArrBean(123567890.1f, 098765432.1f);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		FloatArrBean readSeq = readObject(FloatArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	

	@Check
	public void testSerializeDouble() throws IOException {
		DoubleArrBean zero = new DoubleArrBean(0.0,0.0);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		DoubleArrBean readZero = readObject(DoubleArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		DoubleArrBean max = new DoubleArrBean(Double.MAX_VALUE, Double.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		DoubleArrBean readMax = readObject(DoubleArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		DoubleArrBean min = new DoubleArrBean(Double.MIN_VALUE, Double.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		DoubleArrBean readMin = readObject(DoubleArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		DoubleArrBean min_norm = new DoubleArrBean(Double.MIN_NORMAL, Double.MIN_NORMAL);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min_norm);
		DoubleArrBean readMin_norm = readObject(DoubleArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min_norm, readMin_norm);
		
		DoubleArrBean seq = new DoubleArrBean(12356789012345678.9, 98765432109876543.2);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		DoubleArrBean readSeq = readObject(DoubleArrBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}

	
	@Check
	public void testSerializeBigArrays() throws IOException {
		ByteArrayOutputStream baos;
		
		BigArraysBean babNul = new BigArraysBean();
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, babNul);
		BigArraysBean readBabNul = readObject(BigArraysBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(babNul, readBabNul);
		
		BigArraysBean babZero = new BigArraysBean();
		babZero.generateAll(0, 0, 0, 0, 0, 0, 0, 0);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, babZero);
		BigArraysBean readBabZero = readObject(BigArraysBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(babZero, readBabZero);
		
		BigArraysBean babOne = new BigArraysBean();
		babOne.generateAll(1, 1, 1, 1, 1, 1, 1, 1);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, babOne);
		BigArraysBean readBabOne = readObject(BigArraysBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(babOne, readBabOne);
		
		BigArraysBean babTen = new BigArraysBean();
		babTen.generateAll(10, 10, 10, 10, 10, 10, 10, 10);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, babTen);
		BigArraysBean readBabTen = readObject(BigArraysBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(babTen, readBabTen);
		
		BigArraysBean babPartial1 = new BigArraysBean();
		babPartial1.generateAll(-1, -1, -1, -1, 10, 10, 10, 10);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, babPartial1);
		BigArraysBean readBabPartial1 = readObject(BigArraysBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(babPartial1, readBabPartial1);

		BigArraysBean babPartial2 = new BigArraysBean();
		babPartial2.generateAll(10, 10, 10, 10, -1, -1, -1, -1);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, babPartial2);
		BigArraysBean readBabPartial2 = readObject(BigArraysBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(babPartial2, readBabPartial2);

		BigArraysBean babPartial3 = new BigArraysBean();
		babPartial3.generateAll(10, -1, 10, -1, 10, -1, 10, -1);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, babPartial3);
		BigArraysBean readBabPartial3 = readObject(BigArraysBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(babPartial3, readBabPartial3);

		BigArraysBean babHuge = new BigArraysBean();
		babHuge.generateAll(100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000);
		assertEquals(babHuge.toString(), "[booleans:100000,bytes:100000,shorts:100000,chars:100000,ints:100000,longs:100000,floats:100000,doubles:100000]");
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, babHuge);
		BigArraysBean readBabHuge = readObject(BigArraysBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(babHuge, readBabHuge);
	}

	
	public static void main(String[] args) {
		SerializerPrimArrayTest test = new SerializerPrimArrayTest();
		CheckResult result = test.result();
		result.print();
		result.forAllUnexpected((m, t) -> {
			System.err.println("method: " + m);
			System.err.println("\terror: " + t);
			t.printStackTrace();
		});
		if (!result.wentExpected()) {
			fail("There were test failures: "+result.allUnexpected());
		}
	}

}
