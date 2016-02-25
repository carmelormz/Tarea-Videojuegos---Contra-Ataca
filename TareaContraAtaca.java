/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Carmelo Ramriez A01175987 y Axel Ramirez A00399692
 * @Fecha 22/02/2016
 * @Tarea Contra-Ataca
 */

import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
    private Image imaVida; //Imagen de las vidas
    private LinkedList <Malo> lklMalos; //Personajes Malos
    private LinkedList <Proyectil> lklBalas; //Linked List de Balas
    private int iVida; //Vida del Personaje
    private int iScore; //Puntaje del juego
    private int iVelocidad; //Velocidad de los Malos
    private int iDireccion; //Direccion del Personaje
    private int iCont; //Contador de colision 
    private SoundClip scColision; //Sound Clip de la colision de la bala con el malo
    private SoundClip scVida; //Sound Clip de eliminacion de la vida
    private SoundClip scBala; //Sound Clip colision bala con Malo
    private SoundClip scDisparo; //Sound Clip al disparar
    private Base basPersonaje; //Personaje principal
    private boolean bTecla; //Booleano del teclado
    private boolean bPausa; //Booleano de pausa del juego
    private boolean bGameOver; //Booleano de fin de juego
    
    /** Constructor de la clase Tarea Contra Ataca
     * 
     */
    public TareaContraAtaca() {
        //Llamo al init y start
        init();
        start();
    }
    
    public void init() { 
        iVida = 5; //Inicializamos la vida en 5
        iVelocidad = 1; //Inicializamos la velocidad de los malos en 1
        iScore = 0; //Inicializamos el puntaje en 0
        iCont = 10; //Inicializamos el contador en 10
        bPausa = false; //Inicializamos el booleano en false (JUEGO ESTA CORRIENDO)
        bGameOver = false; //Inicializamos el booleano en false;
        
        //LLamo metodos de inicializacion y creacion de los personaje
        inicializaImagenes();
        inicializaLkl();
        inicializaSonidos();
        crearMalos();
        
         //Defino la base del personaje principal
        basPersonaje = new Base(0, 0, imaPersonaje);
        //Posiciono Personaje a la mitad
        basPersonaje.setX(iWIDTH/2 - basPersonaje.getAncho());
        basPersonaje.setY(iHEIGHT - basPersonaje.getAlto());
        
        //Posiciono Malos
        for(Malo malMalos: lklMalos){
           posicionaMalos(malMalos);
        }
        
        //Agregamos el KeyListener
        addKeyListener(this);
    }
    
    public void start() { 
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
        //Defino la imagen de vida
        imaVida = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("item_life.png"));
    }
    
    public void inicializaLkl() {
        //Creo una Linked List de Base
        lklMalos = new LinkedList <Malo> ();
        //Creo una Linked List de Proyectil
        lklBalas = new LinkedList <Proyectil> ();
    }
    
    public void crearMalos() {
        //Creo un numero al azahr entre 10 y 15
        int iRandom = (int)(Math.random() * 5 + 10);
        //Creo un numero al azahar entre 1 y 2
        int iRanSupMalos = (int)(Math.random() * 1 + 1); 
        
        for(int iC=0; iC  < iRanSupMalos; iC++){
            //Creo la instancia del Super Malo
            Malo maSuperMalo = new Malo(0 , 0 , imaMalos, 'S');
            lklMalos.add(maSuperMalo); //Agrego el Malo a la lista
        }
        
        //Creo los Malos al azahr
        for(int iC=0; iC < (iRandom - iRanSupMalos); iC++){
            //creo una instancia de Malos
            Malo maMalos = new Malo(0, 0, imaMalos, 'N');
            //añado el Malo creado a la lista
            lklMalos.add(maMalos);
        }
    }
    
    public void posicionaMalos(Malo malObj) {
        //reposiciona el malo
        malObj.setY(-100); //Posiciono a los malos afuera del frame
        malObj.setX((int)(Math.random() * (iWIDTH- malObj.getAncho()))); //Posiciono en un lugar random a los malos en el eje X
    }
    
    public void inicializaSonidos() {
        //Defino el sonido de colision Malo con Personaje
        scColision =new SoundClip("click1.wav");
        
        //Defino el sonido cuando se elimina una vida 
        scVida= new SoundClip("breaking.wav");
        
        //Defino el sonido cuando colisiona Bala con Malo
        scBala = new SoundClip("close1.wav");
        
        //Defino el sonido cuando se hace un disparo
        scDisparo = new SoundClip("gun6.wav");
    }
    public void actualiza() {
            //DIRECCION: 
            // 1 - DERECHA
            // 2 - IZQUIERDA
            if(bTecla) {
                switch(iDireccion){
                    //Derecha
                    case 1: { 
                        basPersonaje.setX(basPersonaje.getX() + 5);
                        break;
                    }
                    //Izquierda
                    case 2: {
                        basPersonaje.setX(basPersonaje.getX() - 5);
                    }
                }
            }

            //Movimiento personajes malos
            for(Malo malMalo: lklMalos){
                malMalo.avanza(basPersonaje); //llamamos la funcion del malo
            }
            //Movimiento de balas
            for(Proyectil ptlBala: lklBalas){
                ptlBala.avanza(); //llamamos la funcion del malo
            }
    }
    
    public void run() {
        do {
            //Si las vidas son mayores a 0 seguimos jugando
            while(iVida > 0 ){
                if(!bPausa){
                    actualiza();
                    checaColisionPersonaje();
                    checaColisionMalos();
                    checaColisionBalas();
                }
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
            //Juego termino, cambio el booleano bGameOver a true
            bGameOver = true; 
        } while(bGameOver != false); //Ciclo para que siga checando si hay algun cambio en el reinicio
    }
    
    public void checaColisionPersonaje(){
        //Checa si el planeta choca con la parte derecha del applet
         if(basPersonaje.getX() + basPersonaje.getAncho() >= getWidth()){
             basPersonaje.setX(getWidth()- basPersonaje.getAncho());
         }
         //Checa si el planeta choca con la parte izquiera del applet
         if(basPersonaje.getX() < 0){
             
             basPersonaje.setX(0);
         }
        
    }
    
    public void checaColisionMalos(){
        //Recorro toda la linked list de Malos
        for(Malo malMalo: lklMalos){
                //si el malo choca con el personaje
                if(basPersonaje.colisiona(malMalo)){
                    iScore -= 1; //Resto 1  a la puntuacion
                    if(iCont > 1){
                        iCont--;
                        scColision.play(); //sonido de Colision
                    } else {
                        iVida--; //Resto una vida
                        scVida.play(); //Sonido de eliminacion de vida
                        iCont = 10; //Regreso el contador a su valor inicial
                        iVelocidad ++; //Aumento la velocidad de los malos
                        //Aumento la velocidad de todos los malos
                        for(Malo malMalo2 : lklMalos){
                            malMalo2.aumentarVelocidad(); //Aumento al velocidad del malo
                        }
                    }
                    posicionaMalos(malMalo); //reposiciono el malo 
                }else if(malMalo.getY() + malMalo.getAlto() > getHeight()){ //Si se sale del frame
                      iScore -= 1; //resto un punto al score
                      scColision.play(); //sonido de Colision
                      posicionaMalos(malMalo); //reposiciono el malo  
                }
                        
        }
        
    }
    
    public void checaColisionBalas() { 
        //Si el malo choca con la bala, se reposicionan el malo y se elimina la bala
        for(int iC=0; iC < lklBalas.size(); iC++){
            Proyectil ptlBala = lklBalas.get(iC);
            for(int iK = 0 ; iK < lklMalos.size(); iK++){
                Malo malMalo = lklMalos.get(iK); //Copiamos el malo a otra variable
                if(malMalo.colisiona(ptlBala)){
                    scBala.play();
                    iScore += 10; //Aumentamos en 10 el score
                    lklBalas.remove(ptlBala); //Eliminamos la bala
                    posicionaMalos(malMalo);//Reposicionamos
                }
            }
        }
        //Si la bala choca con el frame, se elimina 
        for(int iC = 0; iC < lklBalas.size(); iC++){
            Proyectil ptlBala = lklBalas.get(iC); //Copiamos la bala a otra variable
            if(ptlBala.getY() < 0){
                lklBalas.remove(ptlBala); //Eliminamos la bala
            }
        } 
    }

    
    
    /**
     * @function paint
     * @param graGrafico 
     */
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
    /**
     * @function paint1
     * @param graDibujo 
     */
    public void paint1(Graphics graDibujo) {
        //Mientras el numero de vidas sea mayor a 0
        if(iVida > 0) {
            // si la imagen ya se cargo
            if (basPersonaje != null && lklMalos != null) {
                    //Dibuja la imagen de principal en el Applet
                    basPersonaje.paint(graDibujo, this);
                    synchronized(this) {
                     //pinto cada malo
                    for(Malo malMalo: lklMalos){
                        malMalo.paint(graDibujo, this);
                    }
                    //Pinto cada bala
                    for(Proyectil ptlBala : lklBalas){
                        ptlBala.paint(graDibujo, this);
                    }
                    }
                      // Dibujo las vidas y puntaje
                    graDibujo.drawString("Score: " + iScore, 700, 45);
                    graDibujo.drawString("Salud: " + iCont, 700, 60);
            } else {
                //Da un mensaje mientras se carga el dibujo	
                graDibujo.drawString("No se cargo la imagen..", 20, 20);
              }
            //DIBUJO LAS VIDAS
            if(iVida >= 1){
                graDibujo.drawImage(imaVida, 30, 30, 32, 32 ,this);
            }
            if(iVida >= 2) { 
                graDibujo.drawImage(imaVida, 62, 30, 32, 32 ,this);
            }
            if(iVida >= 3){
                graDibujo.drawImage(imaVida, 94, 30, 32, 32 ,this);
            }
            if(iVida >=4) { 
                graDibujo.drawImage(imaVida, 126, 30, 32, 32 ,this);
            }
            if(iVida == 5){
                graDibujo.drawImage(imaVida, 158, 30, 32, 32 ,this);
            }
            
          } else {
            //Si las vidas son igual a 0, se muestra la imagen de Game Over
            graDibujo.drawImage(imaGameOver, 0, 0, 800, 500, this);
            graDibujo.setColor(Color.green);
            JOptionPane.showMessageDialog(null, "Para reiniciar el juego presione ENTER", "REINICIO", JOptionPane.PLAIN_MESSAGE);
        }
        
        if(bPausa) { 
            graDibujo.setColor(Color.red);
            graDibujo.drawString("PAUSA", 400 , 250);
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
        
        //Si el usuario teclea la letra P se pausa el juego
        if(!bPausa && keEvent.getKeyCode() == KeyEvent.VK_P) {
            bPausa = true;//Si se estaba jugando, el juego se pausa
        } else if(bPausa && keEvent.getKeyCode() == KeyEvent.VK_P){
            bPausa = false;  //Si ya estaba en pausa, cambiamos a false para que se continue jugando
        }
        //Si el juego acabo y el usuario presiona Enter se llama al init()
        if(bGameOver && keEvent.getKeyCode() == KeyEvent.VK_ENTER){
            init(); //llamo al init para reiniciar todas las variables
        }
    }

    @Override
    public void keyReleased(KeyEvent keEvent) {
        bTecla = false; //Apago la bandera
        
        //Al presionar el Espacio
        if (!bPausa && keEvent.getKeyCode() == KeyEvent.VK_SPACE) {
            // emito sonido
            scDisparo.play();
            // creo bala
            Proyectil ptlBala = new Proyectil(basPersonaje.getX() + 64,
                    basPersonaje.getY() - 64, imaBalas, 'v', 6, 'a');
            // añado la bala a la lista
            lklBalas.add(ptlBala);

        }
        //Al presionar la letra A
        if (!bPausa && keEvent.getKeyCode() == KeyEvent.VK_A) {
            // emito sonido
            scDisparo.play();
            // creo bala
            Proyectil ptlBala = new Proyectil(basPersonaje.getX() + 64,
                    basPersonaje.getY() - 64, imaBalas, 'd', 6, 'i');
            // añado la bala a la lista
            lklBalas.add(ptlBala);

        }
        //Al presionar la letra S
        if (!bPausa && keEvent.getKeyCode() == KeyEvent.VK_S) {
            // emito sonido
            scDisparo.play();
            // creo bala
            Proyectil ptlBala = new Proyectil(basPersonaje.getX() + 64,
                    basPersonaje.getY() - 64, imaBalas, 'd', 6, 'd');
            // añado la bala a la lista
            lklBalas.add(ptlBala);
        }
    }
    /**
     * @function getHeight()
     * @return iHEIGHT
     */
    public int getHeight(){
        return iHEIGHT; //regresa la altura del frame
    }
    
    /**
     * @function getWidth()
     * @return iWIDTH
     */
    public int getWidth(){
        return iWIDTH; //regresa el ancho del frame
    }
}
