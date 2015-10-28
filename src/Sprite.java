import java.awt.Graphics2D;


public abstract class Sprite {
	float x,y;
	public abstract void simulaSe(long diffTime);
	public abstract void desenhaSe(Graphics2D dbg,int telaX,int telaY);
}
