
import java.awt.Image;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author laptops
 */
public class Proyectil extends Base {
    
    char cTipo; //Tipo de bala - Vertical o diagonal.
    int iVel;  //Velocidad de mi proyectil.
    char cOrientacion; //Si mi bala va a la izq (i) o der (d) en horizontal
    
    public Proyectil(int iX, int iY, Image imaImagen, char cTipo1, int iVelocidad, char cOrient) {
        super(iX, iY, imaImagen);
        cTipo = cTipo1;
        iVel = iVelocidad;
        cOrientacion = cOrient;
    }
    
    public void Avanza(){
        
        if(activo){
        //Si es vertical
        if(cTipo == 'v' || cTipo == 'V'){
            //va para arriba 
           setY(getY() - iVel);
       
        }
        //Si es diagonal
        if(cTipo == 'd' || cTipo == 'D'){
            //Y va a la izquierda
            if(cOrientacion == 'i' || cOrientacion == 'I'){
                //diagonal a la izquierda
                setX(getX() - iVel);
                setY(getY() - iVel);
                
            }
            //Y va a la derecha
            if(cOrientacion == 'd' || cOrientacion == 'D'){
                //diagonal a la derecha
                setX(getX() + iVel);
                setY(getY() - iVel);
                
            }
            
        }
        }
        
    }  
    
}
