package test;

public abstract class Test {

    abstract void executeTests();

    public void equals(boolean x, boolean y) {
		if(x != y) {
			throw new IllegalArgumentException("x not equals y, x:"+x+", y:"+y);
		}
	}
	
	public void equals(double x, double y) {
		if(x != y) {
			throw new IllegalArgumentException("x not equals y, x:"+x+", y:"+y);
		}
	}
	
	public void equals(int x, int y) {
		if(x != y) {
			throw new IllegalArgumentException("x not equals y, x:"+x+", y:"+y);
		}
	}
}
