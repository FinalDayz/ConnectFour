package Game;

public class State {
	private boolean redWon,
		draw, ended;
	
	
	public boolean gameDidEnd() {
		return this.ended;
	}
	
	public boolean redDidWon() {
		return this.ended && this.redWon;
	}
	
	public boolean yellowDidWon() {
		return this.ended && !this.redWon;
	}
	
	public boolean gameDidDraw() {
		return this.ended && this.draw;
	}
	
	
	public void setYellowWon() {
		this.redWon = false;
		this.ended = true;
	}
	
	public void setRedWon() {
		this.redWon = true;
		this.ended = true;
	}
	
	public void setDraw() {
		this.draw = true;
		this.ended = true;
	}
	
	
	public void reset() {
		this.redWon = false;
		this.ended = false;
		this.draw = false;
	}
}
