/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.Timer;

/**
 *
 * @author Axel Ramírez - A00399692
 */
public class Juego extends JFrame implements Runnable, KeyListener {

    private Base basPrincipal;         // Objeto principal
    private LinkedList<Base> lklMalos;  //Lista ligada de malos
    private LinkedList<Proyectil> lklBalas; //Lista ligada de balas
    boolean bGameover;
    private Image imaImagenJframe;   // Imagen a proyectar en Jframe	
    private Graphics graGraficaJframe;  // Objeto grafico de la Imagen
    private int iBasvidas;           //Vidas de mi jugador 
    private int iBaspuntuacion;      //puntuacion de mi jugador
    private Image imaImagenFondo;        // para dibujar la imagen de fondo
    private Image imaImagenBala;        // para dibujar la imagen de fondo
    private int iAleatorioSpawn;            //Valor aleatorio para creacion de malos
    private int iAleatorioX;                //Valor aleatorio para pos. X
    private int iAleatorioY;                //Valor aleatorio para pos. Y
    private Image imaImagenMalo;    //Imagen del malo
    private int iVelmalo;       //Velocidad (pixeles) en los que el malo se mueve
    private Image imaGameover;    //Imagen gameover
    private String sPuntuacion;  //Para imprimir la puntuacion en string
    private Image imaImagenbueno;
    private int iWIDTH;
    private int iHEIGHT;
    private Timer timer;                //Timer para mis monstruos
    private int iBasSalud;
    private SoundClip sSong;
    private String sVidas;            //String de vidas
    private String sSalud;
    private Boolean bPausa;
    private SoundClip sBala;
    private SoundClip sDolor;
    private SoundClip sMuere;
    private SoundClip sDuele;
    private Image imaImagenVida;

    public Juego() {
        /*Funcion del init applet*/
        //Agrego el KeyListener.
        addKeyListener(this);
        // defino la imagen principal y del malo
        imaImagenbueno = Toolkit.getDefaultToolkit().getImage(
                this.getClass().getResource("gun.png"));
        imaImagenMalo = Toolkit.getDefaultToolkit().getImage(
                this.getClass().getResource("malo.png"));
        // Creo la imagen de fondo.
        URL urlImagenFondo = this.getClass().getResource("fondo.jpg");
        imaImagenFondo = Toolkit.getDefaultToolkit().getImage(urlImagenFondo);

        URL urlImagenBala = this.getClass().getResource("shot.png");
        URL urlImagenVida = this.getClass().getResource("life.png");
        imaImagenBala = Toolkit.getDefaultToolkit().getImage(urlImagenBala);
        imaImagenVida = Toolkit.getDefaultToolkit().getImage(urlImagenVida);

        // Creo el objeto para principal 
        basPrincipal = new Base(0, 0,
                imaImagenbueno);

        //Inicio mis variables
        sSong = new SoundClip("doom.wav");
        sBala = new SoundClip("disparo.wav");
        sDolor = new SoundClip("dolor.wav");
        sMuere = new SoundClip("explota.wav");
        sDuele = new SoundClip("muere.wav");
        //sSong.setLooping(true);
        //sSong.play();

        bPausa = false;
        iVelmalo = 3;
        iWIDTH = 1024;
        iHEIGHT = 768;
        iBasSalud = 5;
        iVelmalo = 3;
        iBasvidas = 5;
        iBaspuntuacion = 0;

        //Creo la lista ligada de los malos y disparos
        lklMalos = new LinkedList<>();
        lklBalas = new LinkedList<>();

        // se posiciona a principal  en el cuarto cuadrante 
        basPrincipal.setX(1280 / 2 - basPrincipal.getAncho());
        basPrincipal.setY(768 / 2 - basPrincipal.getAlto());

        //valor de cuantos malos van a aparecer
        iAleatorioSpawn = (int) (Math.random() * 3 + 8); //numero rand entre 8 y 10
        System.out.print("Malos: " + iAleatorioSpawn + "    ");

        //Cada segundo se creará un malo
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!bPausa) {
                    // Aquí el código que queramos ejecutar.
                    for (int iI = 0; iI < 1; iI++) {
                        //Numeros aleatorios para las posiciones de las manzanas
                        iAleatorioX = (int) (Math.random() * 1168);
                        iAleatorioY = (int) (Math.random() * 500 - 768);

                        /*Creo a un malo arriba de la pantalla 
            y dentro de sus limites  de X            */
                        Base basMalo = new Base(iAleatorioX, iAleatorioY, imaImagenMalo);
                        //añado al malo a la lista
                        lklMalos.add(basMalo);
                    }
                }
            }
        });
        //inicio el timer de creacion de malos
        timer.start();

        // se posiciona a principal  en el cuarto cuadrante 
        basPrincipal.setX(1114 / 2);
        basPrincipal.setY(646);


        /*Funcion del start applet*/
        // Declaras un hilo
        Thread th = new Thread(this);
        // Empieza el hilo
        th.start();
    }

    public void run() {
        while (!bGameover) {
            actualiza();
            checaColision();
            repaint();
            try {
                // El hilo del juego se duerme.
                Thread.sleep(20);
            } catch (InterruptedException iexError) {
                System.out.println("Hubo un error en el juego "
                        + iexError.toString());
            }
        }
    }

    /**
     * actualiza
     *
     * Metodo que actualiza la posicion de los objetos
     *
     */
    public void actualiza() {
        if (!bPausa) {
            sPuntuacion = Integer.toString(iBaspuntuacion);
            sSalud = Integer.toString(iBasSalud);
            sVidas = Integer.toString(iBasvidas);

            //Caen los malos
            for (Base basMalo : lklMalos) {
                if (basMalo.activo) {
                    basMalo.setY(basMalo.getY() + iVelmalo);
                }
          
                if (basMalo.destruido == true) {
                    //lo mandmos lejos
                    basMalo.setX(0);
                    basMalo.setY(-800);
                }
            }
            //que se mueva mi bala
            for (Proyectil ptlBala : lklBalas) {
                ptlBala.Avanza();

            }
        }

        /*
        for (Base basShot : lklShot) {
            basShot.setY(basShot.getX() - 5);
            System.out.println(basShot.getX());
        }
         */
    }

    /**
     * checaColision
     *
     * Metodo usado para checar la colision entre objetos
     *
     */
    public void checaColision() {
        //Detecto colision del malo con el suelo
        for (Base basMalo : lklMalos) {
            if (basMalo.activo && basMalo.getY() + basMalo.getAlto() > 768) {
                iAleatorioX = (int) (Math.random() * 1168);
                iAleatorioY = (int) (Math.random() * 500 - 768);
                basMalo.setX(iAleatorioX);
                basMalo.setY(iAleatorioY);
                basMalo.activo = false;
                sMuere.play();
            }
        }

        //El personaje no se puede salir de la ventana.
        if (basPrincipal.getX() + basPrincipal.getAncho() >= getWidth()) {
            basPrincipal.setX(getWidth() - basPrincipal.getAncho());
        }
        if (basPrincipal.getX() < 0) {
            basPrincipal.setX(0);
        }

        //Colision del malo con mi personaje
        for (Base basMalo : lklMalos) {
            if (basMalo.activo && basPrincipal.getX() < basMalo.getX() + basMalo.getAncho()
                    && basPrincipal.getX() + basPrincipal.getAncho() > basMalo.getX()
                    && basPrincipal.getY() < basMalo.getY() + basMalo.getAlto()
                    && basPrincipal.getAlto() + basPrincipal.getY() > basMalo.getY()) {
                if (iBasSalud > 1) {
                    iBasSalud--;
                    iBaspuntuacion--;
                } else {
                    iBasvidas--;
                    iBasSalud = 5;
                    iVelmalo++;
                }
                basMalo.activo = false;
                basMalo.destruido = true;
                sDolor.play();
            }
            //Colision de una bala con un malo
            for (Proyectil ptlBala : lklBalas) {
                if (ptlBala.activo && basMalo.activo && ptlBala.getX() < basMalo.getX() + basMalo.getAncho()
                        && ptlBala.getX() + ptlBala.getAncho() > basMalo.getX()
                        && ptlBala.getY() < basMalo.getY() + basMalo.getAlto()
                        && ptlBala.getAlto() + ptlBala.getY() > basMalo.getY()) {
                    //destruyo al malo
                    basMalo.activo = false;
                    basMalo.destruido = true;
                    sDuele.play();
                    iBaspuntuacion = iBaspuntuacion + 10;
                    ptlBala.activo = false;
                    ptlBala.destruido = true;
                    ptlBala.setX(-100);
                    ptlBala.setY(-100);

                }
                if (ptlBala.getY() < 0) {
                    ptlBala.setX(-100);
                    ptlBala.setY(-100);
                }
            }
        }

    }

    public void paint(Graphics graGrafico) {
        // Inicializan el DoubleBuffer
        if (imaImagenJframe == null) {
            imaImagenJframe = createImage(this.getSize().width,
                    this.getSize().height);
            graGraficaJframe = imaImagenJframe.getGraphics();
        }

        // Actualiza la imagen de fondo.
        URL urlImagenFondo = this.getClass().getResource("fondo.jpg");
        Image imaImagenFondo = Toolkit.getDefaultToolkit().getImage(urlImagenFondo);
        graGraficaJframe.drawImage(imaImagenFondo, 0, 0, 1280, 768, this);

        // Actualiza el Foreground.
        graGraficaJframe.setColor(getForeground());
        paint1(graGraficaJframe);

        // Dibuja la imagen actualizada
        graGrafico.drawImage(imaImagenJframe, 0, 0, this);
        if (iBasvidas < 1) {
            graGrafico.drawImage(imaGameover, 0, 0, this);
            bGameover = false;

        }

    }

    public void paint1(Graphics graDibujo) {
        if (iBasvidas > 0) {
            // si la imagen ya se cargo
            if (basPrincipal != null && imaImagenFondo != null) {
                // Dibuja la imagen de fondo
                graDibujo.drawImage(imaImagenFondo, 0, 0, getWidth(), getHeight(), this);
                //Dibuja la imagen de principal en el Jframe
                basPrincipal.paint(graDibujo, this);
                for (Base basMalo : lklMalos) {
                    basMalo.paint(graDibujo, this);
                }

                for (Proyectil ptlBala : lklBalas) {
                    ptlBala.paint(graDibujo, this);

                }

            } // sino se ha cargado se dibuja un mensaje 
            else {
                //Da un mensaje mientras se carga el dibujo	
                graDibujo.drawString("No se cargo la imagen..", 20, 20);
            }
        } else {

            graDibujo.drawImage(imaGameover, 0, 0, 1280, 768, this);
        }

        Dimension d = this.getPreferredSize();
        int fontSize = 30;

        graDibujo.setFont(new Font("Arial", Font.BOLD, fontSize));

        graDibujo.setColor(Color.red);

        graDibujo.drawString("Puntuacion: " + sPuntuacion, 10, 60);
        graDibujo.drawString("Vidas: " + sVidas, 10, 90);
        graDibujo.drawString("Salud: " + sSalud, 10, 120);

        if (bPausa) {
            graDibujo.setColor(Color.white);
            graDibujo.drawString("PAUSA", 595, 384);
        }

    }

    public void keyTyped(KeyEvent ke) {

    }

    public void keyPressed(KeyEvent keyEvent) {
        if (!bPausa && keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
            basPrincipal.setX(basPrincipal.getX() - 30);

        }

        if (!bPausa && keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
            basPrincipal.setX(basPrincipal.getX() + 30);

        }

    }

    public void keyReleased(KeyEvent keyEvent) {

        if (!bPausa && keyEvent.getKeyCode() == KeyEvent.VK_P) {
            bPausa = true;
        } else if (bPausa && keyEvent.getKeyCode() == KeyEvent.VK_P) {
            bPausa = false;
        }

        if (!bPausa && keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
            // emito sonido
            sBala.play();
            // creo bala
            Proyectil ptlBala = new Proyectil(basPrincipal.getX() + 54,
                    basPrincipal.getY() - 80, imaImagenBala, 'v', 6, 'a');
            // añado la bala a la lista
            lklBalas.add(ptlBala);

        }

        if (!bPausa && keyEvent.getKeyCode() == KeyEvent.VK_A) {
            // emito sonido
            sBala.play();
            // creo bala
            Proyectil ptlBala = new Proyectil(basPrincipal.getX() + 54,
                    basPrincipal.getY() - 80, imaImagenBala, 'd', 6, 'i');
            // añado la bala a la lista
            lklBalas.add(ptlBala);

        }

        if (!bPausa && keyEvent.getKeyCode() == KeyEvent.VK_S) {
            // emito sonido
            sBala.play();
            // creo bala
            Proyectil ptlBala = new Proyectil(basPrincipal.getX() + 54,
                    basPrincipal.getY() - 80, imaImagenBala, 'd', 6, 'd');
            // añado la bala a la lista
            lklBalas.add(ptlBala);

        }

    }

    public void dispara() {

    }

    public static void main(String[] args) {
        // TODO code application logic here
        Juego game = new Juego();
        game.setSize(1280, 768);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setVisible(true);
    }

}
