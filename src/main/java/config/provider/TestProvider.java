package config.provider;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.reflect.TypeToken;
import config.DataProvider;
import config.bean.test.TestConfig;

public class TestProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestProvider.class);

	private static TestProvider instance = new TestProvider();

	public static TestProvider getIns() {
		return instance;
	}

	private Map<Integer, TestConfig> testConfigs = new HashMap<Integer, TestConfig>();

	@SuppressWarnings("unchecked")
	public boolean load() {
		Map<Integer, TestConfig> newTestConfigs = new HashMap<Integer, TestConfig>();
		try {
			Type type = new TypeToken<Map<Integer, TestConfig>>() {}.getType();
			newTestConfigs = (Map<Integer, TestConfig>) DataProvider.fromJson("Test.json", type);
			testConfigs = newTestConfigs;
			return true;
		} catch(Exception e) {
			LOGGER.error("TestProvider load json exception :", e);
			return false;
		}
	}

	public Map<Integer, TestConfig> testConfigs() {
		return testConfigs;
	}

	public TestConfig testConfig(int id) {
		return testConfigs.get(id);
	}

}