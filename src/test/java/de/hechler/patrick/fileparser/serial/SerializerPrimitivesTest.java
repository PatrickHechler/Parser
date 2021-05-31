package de.hechler.patrick.fileparser.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;

import de.hechler.patrick.zeugs.check.Checker;
import de.hechler.patrick.zeugs.check.anotations.Check;
import de.hechler.patrick.zeugs.check.anotations.CheckClass;
import de.hechler.patrick.zeugs.check.anotations.Start;

@CheckClass
public class SerializerPrimitivesTest extends Checker {

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

	
	public static class BooleanBean {
		public boolean value1;
		public boolean value2;
		public BooleanBean() {
			this(false,false);
		}
		public BooleanBean(boolean value1, boolean value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BooleanBean other = (BooleanBean) obj;
			return value1 == other.value1 && value2 == other.value2;
		}
	}
	
	public static class ByteBean {
		public byte value1;
		public byte value2;
		public ByteBean() {
			this((byte)0,(byte)0);
		}
		public ByteBean(byte value1, byte value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ByteBean other = (ByteBean) obj;
			return value1 == other.value1 && value2 == other.value2;
		}
	}
	
	public static class CharBean {
		public char value1;
		public char value2;
		public CharBean() {
			this('\0','\0');
		}
		public CharBean(char value1, char value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CharBean other = (CharBean) obj;
			return value1 == other.value1 && value2 == other.value2;
		}
	}

	public static class ShortBean {
		public short value1;
		public short value2;
		public ShortBean() {
			this((short)0,(short)0);
		}
		public ShortBean(short value1, short value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ShortBean other = (ShortBean) obj;
			return value1 == other.value1 && value2 == other.value2;
		}
	}
	
	public static class IntBean {
		public int value1;
		public int value2;
		public IntBean() {
			this(0,0);
		}
		public IntBean(int value1, int value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IntBean other = (IntBean) obj;
			return value1 == other.value1 && value2 == other.value2;
		}
	}
	
	public static class LongBean {
		public long value1;
		public long value2;
		public LongBean() {
			this(0L,0L);
		}
		public LongBean(long value1, long value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LongBean other = (LongBean) obj;
			return value1 == other.value1 && value2 == other.value2;
		}
	}
	
	public static class FloatBean {
		public float value1;
		public float value2;
		public FloatBean() {
			this(0.0f,0.0f);
		}
		public FloatBean(float value1, float value2) { 
			this.value1 = value1;
			this.value2 = value2;
		}
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FloatBean other = (FloatBean) obj;
			return value1 == other.value1 && value2 == other.value2;
		}
	}
	
	public static class DoubleBean {
		public double value1;
		public double value2;
		public DoubleBean() {
			this(0.0,0.0);
		}
		public DoubleBean(double value1, double value2) { 
			this.value1 = value1;
			this.value2 = value2;
		}
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DoubleBean other = (DoubleBean) obj;
			return value1 == other.value1 && value2 == other.value2;
		}
	}
	

	@Check
	public void testSerializeBoolean() throws IOException {
		BooleanBean zero = new BooleanBean(false,false);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		BooleanBean readZero = readObject(BooleanBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		BooleanBean max = new BooleanBean(true, true);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		BooleanBean readMax = readObject(BooleanBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		BooleanBean mix = new BooleanBean(true, false);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, mix);
		BooleanBean readMix = readObject(BooleanBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(mix, readMix);
		
		BooleanBean mix2 = new BooleanBean(false, true);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, mix2);
		BooleanBean readMix2 = readObject(BooleanBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(mix2, readMix2);
	}

	@Check
	public void testSerializeByte() throws IOException {
		ByteBean zero = new ByteBean((byte)0,(byte)0);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		ByteBean readZero = readObject(ByteBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		ByteBean max = new ByteBean(Byte.MAX_VALUE, Byte.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		ByteBean readMax = readObject(ByteBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		ByteBean min = new ByteBean(Byte.MIN_VALUE, Byte.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		ByteBean readMin = readObject(ByteBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		ByteBean seq = new ByteBean((byte)123, (byte)-98);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		ByteBean readSeq = readObject(ByteBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	
	@Check
	public void testSerializeShort() throws IOException {
		ShortBean zero = new ShortBean((short)0,(short)0);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		ShortBean readZero = readObject(ShortBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		ShortBean max = new ShortBean(Short.MAX_VALUE, Short.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		ShortBean readMax = readObject(ShortBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		ShortBean min = new ShortBean(Short.MIN_VALUE, Short.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		ShortBean readMin = readObject(ShortBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		ShortBean seq = new ShortBean((short)12356, (short)21098);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		ShortBean readSeq = readObject(ShortBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	
	@Check
	public void testSerializeChar() throws IOException {
		CharBean zero = new CharBean((char)0,(char)0);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		CharBean readZero = readObject(CharBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		CharBean max = new CharBean(Character.MAX_VALUE, Character.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		CharBean readMax = readObject(CharBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		CharBean min = new CharBean(Character.MIN_VALUE, Character.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		CharBean readMin = readObject(CharBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		CharBean seq = new CharBean((char)12356, (char)21098);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		CharBean readSeq = readObject(CharBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	
	@Check
	public void testSerializeInt() throws IOException {
		IntBean zero = new IntBean(0,0);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		IntBean readZero = readObject(IntBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		IntBean max = new IntBean(Integer.MAX_VALUE, Integer.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		IntBean readMax = readObject(IntBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		IntBean min = new IntBean(Integer.MIN_VALUE, Integer.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		IntBean readMin = readObject(IntBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		IntBean seq = new IntBean(1234567890, 987654321);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		IntBean readSeq = readObject(IntBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	
	@Check
	public void testSerializeLong() throws IOException {
		LongBean zero = new LongBean(0L,0L);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		LongBean readZero = readObject(LongBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		LongBean max = new LongBean(Long.MAX_VALUE, Long.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		LongBean readMax = readObject(LongBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		LongBean min = new LongBean(Long.MIN_VALUE, Long.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		LongBean readMin = readObject(LongBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		LongBean seq = new LongBean(1234567890123456789L, 987654321098765432L);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		LongBean readSeq = readObject(LongBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}

	@Check
	public void testSerializeFloat() throws IOException {
		FloatBean zero = new FloatBean(0.0f,0.0f);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		FloatBean readZero = readObject(FloatBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		FloatBean max = new FloatBean(Float.MAX_VALUE, Float.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		FloatBean readMax = readObject(FloatBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		FloatBean min = new FloatBean(Float.MIN_VALUE, Float.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		FloatBean readMin = readObject(FloatBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		FloatBean min_norm = new FloatBean(Float.MIN_NORMAL, Float.MIN_NORMAL);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min_norm);
		FloatBean readMin_norm = readObject(FloatBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min_norm, readMin_norm);
		
		FloatBean seq = new FloatBean(123567890.1f, 098765432.1f);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		FloatBean readSeq = readObject(FloatBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	

	@Check
	public void testSerializeDouble() throws IOException {
		DoubleBean zero = new DoubleBean(0.0,0.0);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		DoubleBean readZero = readObject(DoubleBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		DoubleBean max = new DoubleBean(Double.MAX_VALUE, Double.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		DoubleBean readMax = readObject(DoubleBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		DoubleBean min = new DoubleBean(Double.MIN_VALUE, Double.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		DoubleBean readMin = readObject(DoubleBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		DoubleBean min_norm = new DoubleBean(Double.MIN_NORMAL, Double.MIN_NORMAL);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min_norm);
		DoubleBean readMin_norm = readObject(DoubleBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min_norm, readMin_norm);
		
		DoubleBean seq = new DoubleBean(12356789012345678.9, 98765432109876543.2);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		DoubleBean readSeq = readObject(DoubleBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}

	
	public static void main(String[] args) {
		SerializerPrimitivesTest test = new SerializerPrimitivesTest();
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
