package Game;

public interface ConnectFourPlayer {

	public void init(ConnectFourPlayable game, boolean redPlayer);
	
	public void makePlay() throws InterruptedException;

	public void update();

	public void setLog(boolean log);

}
