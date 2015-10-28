import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import javax.swing.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.*;

import javax.imageio.ImageIO;


public class GamePanel extends JPanel implements Runnable
{
public static final int PWIDTH = 640;
public static final int PHEIGHT = 480;
private Thread animator;
private boolean running = false;
private boolean gameOver = false; 

private BufferedImage dbImage;
private Graphics2D dbg;


int FPS,SFPS;
int fpscount;

Random rnd = new Random();

BufferedImage fundo;
byte dadofundo[];
BufferedImage fundo2;
byte dadofundo2[];

BufferedImage charset;
BufferedImage tileset;


boolean LEFT, RIGHT,UP,DOWN;

int MouseX,MouseY;

Personagem heroi;
ArrayList<Sprite> listaDePersonagens = new ArrayList<Sprite>();

public static TileMapJSON map = null;

public GamePanel()
{

	setBackground(Color.white);
	setPreferredSize( new Dimension(PWIDTH, PHEIGHT));

	// create game components
	setFocusable(true);

	requestFocus(); // JPanel now receives key events
	
	if (dbImage == null){
		dbImage = new BufferedImage(PWIDTH, PHEIGHT,BufferedImage.TYPE_4BYTE_ABGR);
		if (dbImage == null) {
			System.out.println("dbImage is null");
			return;
		}else{
			dbg = (Graphics2D)dbImage.getGraphics();
		}
	}	
	
	
	// Adiciona um Key Listner
	addKeyListener( new KeyAdapter() {
		public void keyPressed(KeyEvent e)
			{ 
				int keyCode = e.getKeyCode();
				
				if(keyCode == KeyEvent.VK_LEFT){
					LEFT = true;
				}
				if(keyCode == KeyEvent.VK_RIGHT){
					RIGHT = true;
				}
				if(keyCode == KeyEvent.VK_UP){
					UP = true;
				}
				if(keyCode == KeyEvent.VK_DOWN){
					DOWN = true;
				}	
			}
		
		@Override
		public void keyReleased(KeyEvent e ) {
				int keyCode = e.getKeyCode();
				
				if(keyCode == KeyEvent.VK_LEFT){
					LEFT = false;
				}
				if(keyCode == KeyEvent.VK_RIGHT){
					RIGHT = false;
				}
				if(keyCode == KeyEvent.VK_UP){
					UP = false;
				}
				if(keyCode == KeyEvent.VK_DOWN){
					DOWN = false;
				}
			}
	});
	
	addMouseMotionListener(new MouseMotionListener() {
		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			MouseX = e.getX();
			MouseY = e.getY();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			MouseX = e.getX();
			MouseY = e.getY();
		}
	});
	
	addMouseListener(new MouseListener() {
		
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("Mouse pressionado");
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	});
	
	
	charset = abreImagem("rmxp004tw4.png");
	tileset = abreImagem("tilemap3.png");
	
	MouseX = MouseY = 0;
	
	for(int i = 0; i < 10;i++){
		Personagem pers = new Personagem(rnd.nextInt(630), rnd.nextInt(470), charset,rnd.nextInt(4),rnd.nextInt(2));
		pers.velX = rnd.nextInt(400)-200;
		pers.velY = rnd.nextInt(400)-200;
		listaDePersonagens.add(pers);
	}
	
	heroi = new Personagem(10, 100,charset,0,0);
	
	map = new TileMapJSON(tileset, 40, 30);
	map.AbreMapa("mapa1.json");
	

} // end of GamePanel()

public BufferedImage abreImagem(String imagename){
	BufferedImage saida = null;
	try {
		BufferedImage tmp = ImageIO.read( getClass().getResource(imagename) );
		saida = new BufferedImage(tmp.getTileWidth(), tmp.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		saida.getGraphics().drawImage(tmp, 0, 0, null);
		return saida;
	}
	catch(IOException e) {
		System.out.println("Load Image error:");
	}
	return saida;
}

public void addNotify()
{
	super.addNotify(); // creates the peer
	startGame(); // start the thread
}

private void startGame()
// initialise and start the thread
{
	if (animator == null || !running) {
		animator = new Thread(this);
		animator.start();
	}
} // end of startGame()

public void stopGame()
// called by the user to stop execution
{ running = false; }


public void run()
/* Repeatedly update, render, sleep */
{
	running = true;
	
	long DifTime,TempoAnterior;
	
	int segundo = 0;
	DifTime = 0;
	TempoAnterior = System.currentTimeMillis();
	
	while(running) {
	
		gameUpdate(DifTime); // game state is updated
		gameRender(); // render to a buffer
		paintImmediately(0, 0, 640, 480); // paint with the buffer
	
		try {
			Thread.sleep(0); // sleep a bit
		}	
		catch(InterruptedException ex){}
		
		DifTime = System.currentTimeMillis() - TempoAnterior;
		TempoAnterior = System.currentTimeMillis();
		
		if(segundo!=((int)(TempoAnterior/1000))){
			FPS = SFPS;
			SFPS = 1;
			segundo = ((int)(TempoAnterior/1000));
		}else{
			SFPS++;
		}
	
	}
System.exit(0); // so enclosing JFrame/JApplet exits
} // end of run()

int timerfps = 0;

private void gameUpdate(long diffTime)
{ 
	
	int vel = 200;
	
	if(DOWN){
		heroi.velY = vel;
		heroi.anim = 0;
	}else if(UP){
		heroi.velY = -vel;
		heroi.anim = 3;
	}else{
		heroi.velY = 0;
	}
	
	if(RIGHT){
		heroi.velX = vel;
		heroi.anim = 2;
	}else if(LEFT){
		heroi.velX = -vel;
		heroi.anim = 1;
	}else{
		heroi.velX = 0;
	}
	
	heroi.simulaSe(diffTime);
	
	for(int i = 0; i < listaDePersonagens.size();i++){
		Personagem pers = (Personagem)listaDePersonagens.get(i);
		pers.simulaSe(diffTime);
	}
	
	map.Posiciona((int)(heroi.x-PWIDTH/2),(int)(heroi.y-PHEIGHT/2));
}

private void gameRender()
// draw the current frame to an image buffer
{

	map.DesenhaSe(dbg);

	for(int i = 0; i < listaDePersonagens.size();i++){
		Personagem pers = (Personagem)listaDePersonagens.get(i);
		pers.desenhaSe(dbg,map.MapX,map.MapY);
	}
	
	heroi.desenhaSe(dbg,map.MapX,map.MapY);
	
	dbg.setColor(Color.BLUE);
	dbg.drawString("FPS: "+FPS+" "+MouseX+" "+MouseY, 10, 10);
}


public void paintComponent(Graphics g)
{
	super.paintComponent(g);
	if (dbImage != null)
		g.drawImage(dbImage, 0, 0, null);
}


public static void main(String args[])
{
	GamePanel ttPanel = new GamePanel();

  // create a JFrame to hold the timer test JPanel
  JFrame app = new JFrame("Swing Timer Test");
  app.getContentPane().add(ttPanel, BorderLayout.CENTER);
  app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  app.setResizable(false);  
  app.setVisible(true);
  app.pack();
} // end of main()

} // end of GamePanel class

