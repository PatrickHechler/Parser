package de.hechler.patrick.fileparser.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;

import de.hechler.patrick.zeugs.check.Checker;
import de.hechler.patrick.zeugs.check.anotations.Check;
import de.hechler.patrick.zeugs.check.anotations.Start;

public class SerializerTest extends Checker {

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
	
	public static void main(String[] args) {
		SerializerTest test = new SerializerTest();
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
