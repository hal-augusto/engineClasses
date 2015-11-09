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

public static int mapCotroller = 1;
public static TileMapJSON mapCurrent = null;
public static TileMapJSON map1 = null;
public static TileMapJSON map2 = null;
public static TileMapJSON map3 = null;
public static TileMapJSON map4 = null;

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
				if(keyCode == KeyEvent.VK_1){
					mapCotroller = 1;
				}
				if(keyCode == KeyEvent.VK_2){
					mapCotroller = 2;
				}
				if(keyCode == KeyEvent.VK_3){
					mapCotroller = 3;
				}
				if(keyCode == KeyEvent.VK_4){
					mapCotroller = 4;
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
	
	heroi = new Personagem(10, 500,charset,0,0);
	
	map1 = new TileMapJSON(tileset, 40, 30);
	map1.AbreMapa("mapa1.json");
	map2 = new TileMapJSON(tileset, 40, 30);
	map2.AbreMapa("mapa2.json");
	map3 = new TileMapJSON(tileset, 40, 30);
	map3.AbreMapa("mapa3.json");
	map4 = new TileMapJSON(tileset, 40, 30);
	map4.AbreMapa("mapa4.json");
	

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
	
	switch (mapCotroller) {
	case 1:
		mapCurrent = map1;
		break;
	case 2:
		mapCurrent = map2;
		break;
	case 3:
		mapCurrent = map3;
		break;
	case 4:
		mapCurrent = map4;
		break;
	default:
		break;
	}
	
	heroi.simulaSe(diffTime);
	
	for(int i = 0; i < listaDePersonagens.size();i++){
		Personagem pers = (Personagem)listaDePersonagens.get(i);
		pers.simulaSe(diffTime);
	}
	
	mapCurrent.Posiciona((int)(heroi.x-PWIDTH/2),(int)(heroi.y-PHEIGHT/2));
}

private void gameRender()
// draw the current frame to an image buffer
{
	mapCurrent.DesenhaSe(dbg);

	for(int i = 0; i < listaDePersonagens.size();i++){
		Personagem pers = (Personagem)listaDePersonagens.get(i);
		pers.desenhaSe(dbg,mapCurrent.MapX,mapCurrent.MapY);
	}
	
	heroi.desenhaSe(dbg,mapCurrent.MapX,mapCurrent.MapY);
	
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

