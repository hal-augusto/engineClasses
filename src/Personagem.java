import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class Personagem extends Sprite {
	float velX = 0;
	float velY = 0;
	BufferedImage charset;
	
	int charw = 32;
	int charh = 48;
	
	int frame = 0;
	int timer = 0;
	
	int animsp = 100;
	
	int anim = 0;
	
	int skw = 0;
	int skh = 0;
	int charsetw = 96;
	int charseth = 192;
	
	public Personagem(int X,int Y,BufferedImage charset, int skw,int skh) {
		x = X;
		y = Y;
		this.charset = charset;
		this.skw = skw;
		this.skh = skh;
	}
	
	@Override
	public void simulaSe(long diffTime) {
		float oldx = x;
		float oldy = y;
		
		x = x + velX*diffTime/1000.0f;
		y = y + velY*diffTime/1000.0f;
		
		timer+=diffTime;
		frame = (timer/animsp)%3;
		
		if(x<0){
			x = 0;
			velX = -velX;
		}
		if(x>(GamePanel.mapCurrent.Largura*16)-charw){
			x = (GamePanel.mapCurrent.Largura*16)-charw;
			velX = -velX;
		}
		
		if(y<0){
			y = 0;
			velY = -velY;
		}
		if(y>(GamePanel.mapCurrent.Altura*16)-charh){
			y = (GamePanel.mapCurrent.Altura*16)-charh;
			velY = -velY;
			GamePanel.mapCotroller = 2;
		}	
		
		int bx = (int)((x+16)/16);
		int by = (int)((y+40)/16);
		
		if(GamePanel.mapCurrent.mapa2[by][bx]>0){
			x = oldx;
			y = oldy;
			velY = -velY;
			velX = -velX;
		}
	}

	@Override
	public void desenhaSe(Graphics2D dbg,int telaX,int telaY) {
//		dbg.setColor(cor);
//		dbg.fillRect((int)x,(int)y, 10, 10);
		dbg.drawImage(charset, (int)x-telaX,(int)y-telaY, (int)(x+charw)-telaX,(int)(y+charh)-telaY, frame*charw + skw*charsetw,anim*charh + skh*charseth, frame*charw+charw+skw*charsetw,anim*charh+charh+skh*charseth, null);
	}

}
