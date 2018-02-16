/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import com.sun.j3d.utils.geometry.Sphere;
import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;

/**
 *
 * @author Kuba
 */
public class CollisionDetector extends Behavior {
    public static boolean inCollision = false;
    private WakeupOnCollisionEntry wEnter;
    private WakeupOnCollisionExit wExit;
    Sphere element;
   
  /** Konstruktor klasy CollisonDetector.
   * @param obiekt Obiekt typu Sphere. To na nim będzie sprawdzane czy zachodzi kolizja.
   * @param sphere  Obiekty typu BoundingSphere. Wewnątrz tego obszaru będą sprawdzane kolizje.
   */
  public CollisionDetector(Sphere obiekt, BoundingSphere sphere) {
    inCollision = false;
    element = obiekt;
    element.setCollisionBounds(sphere);
  }

  /** Metoda inicjalizująca. */
  public void initialize() {
    wEnter = new WakeupOnCollisionEntry(element);
    wExit = new WakeupOnCollisionExit(element);
    wakeupOn(wEnter);
  }
  /** Reaguje na pojawienie się lub zniknięcie kolizji.*/
  public void processStimulus(Enumeration criteria) {
    
    inCollision = !inCollision;

    if (inCollision) {
        System.out.println("Weszlo");
        wakeupOn(wExit);  
  }
    else {
        System.out.println("Wyszlo");
        wakeupOn(wEnter); 
    }
    
}
}
