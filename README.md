# Tarea-Videojuegos---Contra-Ataca
Tarea de la materia de Videojuegos
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Carmelo y Axel
 * @Fecha 22/02/2016
 * @Tarea Contra-Ataca
 */

import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.JFrame;

public class TareaContraAtaca extends JFrame implements Runnable, KeyListener {
    private Graphics graGraficaApplet; //Grafico de el Applet
    private static final int iWIDTH = 800; //Ancho del Frame = 800
    private static final int iHEIGHT = 500;//Alto del Frame = 500
    private Image imaImagenApplet; // Imagen a proyectar en Applet	
    private Image imaPersonaje; //Personaje Principal
    private Image imaMalos; //Personajes Malo
    private Image imaBalas; //Imagen de las balas
    private Image imaImagenFondo; //Imagen de Fondo del juego
    private Image imaGameOver; //Imagen Fin del juego
    private Image imaPausa; //Imagen en la pausa del juego
    private LinkedList <Base> lklMalos; //Personajes Malos
    private int iVida; //Vida del Personaje
    private int iScore; //Puntaje del juego
    private int iVelocidad; //Velocidad de los Malos
    private int iDireccion; //Direccion del Personaje
    private int iCont; //Contador de colision 
    private SoundClip scColision; //Sound Clip de la colision de la bala con el malo
    private SoundClip scVida; //Sound Clip de eliminacion de la vida
    private Base basPersonaje; //Personaje principal
    private boolean bTecla; //Booleano del teclado
    
    /** Constructor de la clase Tarea Contra Ataca
     * 
     */
    public void TareaContraAtaca() {
        //INIT
        iVida = 5; //Inicializamos la vida en 5
        iVelocidad = 1; //Inicializamos la velocidad de los malos en 1
        iScore = 0; //Inicializamos el puntaje en 0
        iCont = 10; //Inicializamos el contador en 10
        
        //LLamo metodos de inicializacion y creacion de los personaje
        inicializaImagenes();
        inicializaLkl();
        inicializaSonidos();
        crearMalos();
        
         //Defino la base del personaje principal
        basPersonaje = new Base(0, 0, imaPersonaje);
        //Posiciono Personaje a la mitad
        basPersonaje.setX(getWidth()/2 - basPersonaje.getAncho());
        basPersonaje.setY(getHeight() - basPersonaje.getAlto());
        
        //Posiciono Malos
        for(Base basMalos: lklMalos){
           posiciona(basMalos);
        }
        
        //Agregamos el KeyListener
        addKeyListener(this);
        
        //START
        // Declaras un hilo
        Thread th = new Thread (this);
        // Empieza el hilo
        th.start ();
        
    }
    
    public void inicializaImagenes() {
        //Defino la imagen del personaje principal
        imaPersonaje = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("personaje.png"));
        //Defino la imagen de los personajes malos
        imaMalos = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("item_skull.png"));
        //Defino la imagen de game over
        imaGameOver = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("gameover.jpg"));
        //Defino la imagen de la bala
        imaBalas = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("bullet_up.png"));
        //Defino la imagen de pausa
        imaPausa = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("pausa.jpg"));
    }
    
    public void inicializaLkl() {
        lklMalos = new LinkedList <Base> ();
    }
    
    public void crearMalos() {
        //Creo un numero al azahr entre 10 y 15
        int iRandom = (int)(Math.random() * 5 + 10);
        
        //Creo los Malos al azahr
        for(int iC=0; iC < iRandom; iC++){
            //creo una instancia de Malos
            Base basMalos = new Base(0, 0, imaMalos);
            //añado el Malo creado a la lista
            lklMalos.add(basMalos);
        }
    }
    
    public void posicionaMalos(Base basObj) {
        //reposiciona el malo
        basObj.setY(-100); //Posiciono a los malos afuera del frame
        basObj.setX((int)(Math.random() * getWidth())); //Posiciono en un lugar random a los malos en el eje X
    }
    
    public void inicializaSonidos() {
        //Defino el sonido de colision Bala con Malo
        scColision =new SoundClip("click1.wav");
        
        //Defino el sonido cuando se elimina una vida 
        scVida= new SoundClip("breaking.wav");
    }
    public void actualiza() {
        //DIRECCION: 
        // 1 - DERECHA
        // 2 - IZQUIERDA
        if(bTecla) {
            switch(iDireccion){
                //Derecha
                case 1: { 
                    basPersonaje.setX(basPersonaje.getX() + 2);
                    break;
                }
                //Izquierda
                case 2: {
                    basPersonaje.setX(basPersonaje.getX() - 2);
                }
            }
        }
        
        //Movimiento personajes malos
        for(Base basMalo: lklMalos){
            basMalo.setY(basMalo.getY() + iVelocidad); //Avanza segun la velocidad establecida
        }
    }
    
    public void run() {
        //Si las vidas son mayores a 0 seguimos jugando
        while(iVida > 0 ){
            actualiza();
            checaColisionPersonaje();
            checaColisionMalos();
            repaint();
            try	{
                // El hilo del juego se duerme.
                Thread.sleep (20);
            }
            catch (InterruptedException iexError) {
                System.out.println("Hubo un error en el juego " + 
                        iexError.toString());
            }
        } 
    }
    
    public void checaColisionPersonaje(){
        //Checa si el planeta choca con la parte derecha del applet
         if(basPersonaje.getX() > getWidth()){
             
             basPersonaje.setX(getWidth()- basPersonaje.getAncho());
         }
         //Checa si el planeta choca con la parte izquiera del applet
         if(basPersonaje.getX() < 0){
             
             basPersonaje.setX(0);
         }
         //Checa si el planeta choca con la parte superior del applet
         if(basPersonaje.getY() > getHeight()){
             
             basPersonaje.setY(0);
         }
         //Checa si el planeta choca con la parte inferior del planeta
         if(basPersonaje.getY() < 0){
             
             basPersonaje.setX(getHeight()- basPersonaje.getAlto());
         }
        
    }
    
    public void checaColisionMalos(){
        for(Base basMalo: lklMalos){
                //si el malo choca con el planeta
                if(basPersonaje.colisiona(basMalo)){
                    iScore -= 1; //Resto 1  a la puntuacion
                    scColision.play(); //sonido de Colision
                    posiciona(basMalo); //reposiciono el malo
                }
        }
        
    }
    
    /*public void checaColisionBala() {
        //COLISION DE LA BALA CON MALO
    }*/
    
    public void posiciona(Base basObj){
        //reposiciona el asteroide
        basObj.setX((int)(Math.random() * getWidth()));//Posicion al azahr en el eje X
        basObj.setY(-100); //Posicion -100 en eje Y
    }
    
    public void paint(Graphics graGrafico) {
        // Inicializan el DoubleBuffer
        if (imaImagenApplet == null){
                imaImagenApplet = createImage (this.getSize().width, 
                        this.getSize().height);
                graGraficaApplet = imaImagenApplet.getGraphics ();
        }

        // Actualiza la imagen de fondo.
        URL urlImagenFondo = this.getClass().getResource("FondoJuego.jpg");
        Image imaImagenFondo = Toolkit.getDefaultToolkit().getImage(urlImagenFondo);
         graGraficaApplet.drawImage(imaImagenFondo, 0, 0, getWidth(), getHeight(), this);

        // Actualiza el Foreground.
        graGraficaApplet.setColor (getForeground());
        paint1(graGraficaApplet);

        // Dibuja la imagen actualizada
        graGrafico.drawImage (imaImagenApplet, 0, 0, this);
    }
    
    public void paint1(Graphics graDibujo) {
        //Mientras el numero de vidas sea mayor a 0
        if(iVida > 0) {
            // si la imagen ya se cargo
            if (basPersonaje != null && imaImagenFondo != null && lklMalos != null) {
                    // Dibuja la imagen de fondo
                    graDibujo.drawImage(imaImagenFondo, 0, 0, getWidth(), getHeight(), this);
                    //Dibuja la imagen de principal en el Applet
                    basPersonaje.paint(graDibujo, this);
                     //pinto cada malo
                     for(Base basMalo: lklMalos){
                        basMalo.paint(graDibujo, this);
                     }
                      // Dibujo las vidas y puntaje
                        graDibujo.drawString("Score: " + iScore, 700, 30);
                        graDibujo.drawString("Lives: " + iVida, 700, 45);
            } else {
                //Da un mensaje mientras se carga el dibujo	
                graDibujo.drawString("No se cargo la imagen..", 20, 20);
              }
          } else {
            //Si las vidas son igual a 0, se muestra la imagen de Game Over
            graDibujo.drawImage(imaGameOver, 0, 0, 800, 500, this);
        }
        
    }
    
    public static void main(String [] args){
        // TODO code application logic here
    	TareaContraAtaca jfTarea = new TareaContraAtaca();
    	jfTarea.setSize(iWIDTH, iHEIGHT);
    	jfTarea.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	jfTarea.setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent keEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keEvent) {
        if(keEvent.getKeyCode() == KeyEvent.VK_RIGHT){
            iDireccion = 1; //DEfino la direccion a la derecha
            bTecla = true; //Prendo la bandera del teclado
        } else if(keEvent.getKeyCode() == KeyEvent.VK_LEFT){
            iDireccion = 2; //Defino la direccion a al izquierda
            bTecla = true; //Prendo la bandera del teclado
        }
    }

    @Override
    public void keyReleased(KeyEvent keEvent) {
        bTecla = false; //Apago la bandera
    }
    
    public int getHEIGTH(){
        return iHEIGHT;
    }
    
    public int getWIDTH(){
        return iWIDTH;
    }
}
