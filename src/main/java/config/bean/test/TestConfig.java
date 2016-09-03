package config.bean.test;

public class TestConfig {

	protected int id;

	protected int cash;

	protected TestType type;

	protected String desc;

	protected float weight;

	public int getId(){
		 return id;
	}

	public void setId(int id){
		 this.id=id;
	}

	public int getCash(){
		 return cash;
	}

	public void setCash(int cash){
		 this.cash=cash;
	}

	public TestType getType(){
		 return type;
	}

	public void setType(TestType type){
		 this.type=type;
	}

	public String getDesc(){
		 return desc;
	}

	public void setDesc(String desc){
		 this.desc=desc;
	}

	public float getWeight(){
		 return weight;
	}

	public void setWeight(float weight){
		 this.weight=weight;
	}

}