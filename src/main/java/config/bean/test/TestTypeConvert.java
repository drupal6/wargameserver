package config.bean.test;

import org.apache.commons.beanutils.Converter;

public class TestTypeConvert implements Converter {

	@Override
	public Object convert(Class arg0, Object object) {
		Double ordinal = Double.parseDouble(object.toString());
		return TestType.ordinalValue(ordinal.intValue());
	}
}
