package war.game.server;

import config.provider.TestProvider;

public class TestInitConfig {

	
	public static void main(String[] args) {
		TestProvider.getIns().load();
	}
}
