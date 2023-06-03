package Game;

public interface ConnectFourPlayer {

	public void init(ConnectFour game, boolean redPlayer);
	
	public void makePlay() throws InterruptedException;

}
