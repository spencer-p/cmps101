import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.Before;

public class TestMatrix {
	Matrix m;

	@Before
	public void setup() {
		this.m = new Matrix(3);
	}

	@Test
	public void initialSize() {
		assertEquals(3, m.getSize());
	}

	@Test
	public void initialNNZ() {
        assertEquals(0, m.getNNZ());
	}
}
