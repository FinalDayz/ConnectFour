package ui;

public class ViewerConfig {
    private boolean isInteractive = false;
    private boolean isPlayingRed = false;
    private boolean canControlGame = true;

    public ViewerConfig setPlayer(boolean isRed) {
        isInteractive = true;
        isPlayingRed = isRed;
        return this;
    }

    public ViewerConfig setCanControl(boolean canControl) {
        this.canControlGame = canControl;
        return this;
    }

    public boolean isInteractive() {
        return isInteractive;
    }

    public boolean isRed() {
        return isPlayingRed;
    }

    public boolean canControlGame() {
        return canControlGame;
    }

}
