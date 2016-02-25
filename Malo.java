
import java.awt.Image;


/**
 *@class Malo
 * Clase del personaje Malo
 * @author Carmelo
 */
public class Malo extends Base {
    char cTipo; //Tipo de Malo: S - Super Malo, N - Normal
    int iVel; //Velocidad del Malo
    
    public Malo(int iX, int iY, Image imaImagen, char chTipo){
        super(iX, iY, imaImagen);
        cTipo = chTipo; //Tipo de Malo: S - SuperMalo ; N - Normal
        iVel = 1; //Velocidad inicia en 1
    }
    
    public void avanza(Base basObj){
        //Si el malo es un Super Malo sigue la posicion del personaje
        if(cTipo == 'S' || cTipo == 's'){
            if(basObj.getX() > getX()){
               setX(getX() + iVel); //Modificamos la posicion segun el personaje principal
            } else {
                setX(getX() - iVel);//Modificamos la posicion segun el personaje principal
            }

            if(basObj.getY()> getY()){
               setY(getY() + iVel); //Modificamos la posicion segun el personaje principal
            } else {
                setY(getY() - iVel);//Modificamos la posicion segun el personaje principal
            }
        } else {
            setY(getY() + iVel); //Si no es Super Malo, solamente avanza hacia abajo
        }
    }
    public void aumentarVelocidad() { 
        iVel++;
    }
    
}
