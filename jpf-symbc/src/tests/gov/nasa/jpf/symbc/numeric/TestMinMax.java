package gov.nasa.jpf.symbc.numeric;

import gov.nasa.jpf.symbc.numeric.MinMax;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.test.TestJPF;

import org.junit.Test;
import org.junit.After;

public class TestMinMax extends TestJPF {
	private static final String STATIC_PATH = "src/tests/static/TestMinMax/";
	
	@After
	public void resetLoadBounds() {
		MinMax.reset();
	}
	
	@Test
	public void loadBoundsInformationFromFullCorrectConfigShouldSetCorrectDefaultBounds() {
		Config config = new Config(STATIC_PATH + "full_correct_config");
		MinMax.loadBounds(config); 
		
		assertEquals(config.getInt("symbolic.min_byte"), MinMax.getMinByte());
		assertEquals(config.getInt("symbolic.max_byte"), MinMax.getMaxByte());
		
		assertEquals(config.getInt("symbolic.min_short"), MinMax.getMinShort());
		assertEquals(config.getInt("symbolic.max_short"), MinMax.getMaxShort());
		
		assertEquals(config.getInt("symbolic.min_int"), MinMax.getMinInt());
		assertEquals(config.getInt("symbolic.max_int"), MinMax.getMaxInt());
		
		assertEquals(config.getLong("symbolic.min_long"), MinMax.getMinLong());
		assertEquals(config.getLong("symbolic.max_long"), MinMax.getMaxLong());
		
		assertEquals(config.getInt("symbolic.min_char"), MinMax.getMinChar());
		assertEquals(config.getInt("symbolic.max_char"), MinMax.getMaxChar());
		
		assertEquals(config.getDouble("symbolic.min_double"), MinMax.getMinDouble());
		assertEquals(config.getDouble("symbolic.max_double"), MinMax.getMaxDouble());
	}
	
	@Test(expected = MinMax.IllegalRangeException.class) 
	public void loadBoundsFromConfigWithIncorrectRangeShouldThrowIllegalRangeException() {
		Config config = new Config(STATIC_PATH + "illegal_range_config");
		MinMax.loadBounds(config);
	}
	
	@Test
	public void loadBoundsFromConfigWithSpecVariablesShouldSetCorrectBounds() {
		Config config = new Config(STATIC_PATH + "spec_vars_correct_config");
		String var1 = "var1";
		String var2 = "var2";
		MinMax.loadBounds(config); 
		
		assertEquals(config.getInt("symbolic.min_int_" + var1), MinMax.getVarMinInt(var1));
		assertEquals(config.getInt("symbolic.max_int_" + var1), MinMax.getVarMaxInt(var1));
		
		assertEquals(config.getDouble("symbolic.min_double_" + var2), MinMax.getVarMinDouble(var2));
		assertEquals(config.getDouble("symbolic.max_double_" + var2), MinMax.getVarMaxDouble(var2));
	}
	
	@Test(expected = MinMax.IllegalRangeException.class) 
	public void loadBoundsFromConfigWithSpecVarsAndIncorrectRangeShouldThrowIllegalRangeException() {
		Config config = new Config(STATIC_PATH + "spec_vars_illegal_range_config");
		MinMax.loadBounds(config);
	}
}
