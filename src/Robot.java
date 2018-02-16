
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.*;
import java.awt.color.ColorSpace;
import java.sql.Time;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Robot  extends javax.swing.JFrame implements KeyListener, ActionListener, MouseListener   {

    private Timer zegar = new Timer();
    private ViewingPlatform vPlatform;
    private BranchGroup bg_prymityw;

    //TransformGroup
    private TransformGroup trans_podloga;
    private TransformGroup trans_podstawa;
    private TransformGroup trans_cylinder;
    private TransformGroup trans_cylinder2;
    private TransformGroup trans_przegub1;
    private TransformGroup trans_ramie1;
    private TransformGroup trans_przegub2;
    private TransformGroup trans_ramie2;
    private TransformGroup trans_prymityw;
    private TransformGroup trans_chwytak;
    private TransformGroup trans_glowny;
    private TransformGroup trans_kulka;

    //Transform3d
    private Transform3D trans3d_podloga;
    private Transform3D trans3d_podstawa;
    private Transform3D trans3d_cylinder;
    private Transform3D trans3d_cylinder2, trans3d_cylinder2_rot;
    private Transform3D trans3d_przegub1;
    private Transform3D trans3d_ramie1, trans3d_ramie1_rot;
    private Transform3D trans3d_przegub2;
    private Transform3D trans3d_ramie2, trans3d_ramie2_rot;
    private Transform3D trans3d_prymityw, trans3d_prymityw_rot;
    private Transform3D trans3d_chwytaka;

   // private Transform3D ustaw_prymityw
    public Vector3f prymityw_pozycja = new Vector3f(1.2f,0.0f,0.15f);
    private final Vector3f prymityw_chwyt = new Vector3f(0.75f,0.0f,0.0f);

    // różne - ory, material
    Color3f eColor    = new Color3f(0.9f, 0.0f, 0.1f);
    Color3f sColor    = new Color3f(1.0f, 2.0f, 1.0f);
    Color3f objColor  = new Color3f(0.3f, 0.9f, 0.6f);
    Color3f zColor  = new Color3f(0.8f, 0.6f, 0.3f);
    Material m = new Material(objColor, eColor, objColor, sColor, 100.0f);
    Material m2 = new Material(objColor, objColor, objColor, sColor, 100.0f);
    Material m3 = new Material(zColor, zColor, sColor, sColor, 80.0f);
     Material m4 = new Material(sColor, objColor, eColor, sColor, 80.0f);

    // wygląd obiektów
    Appearance wyglad_podloga;
    Appearance wyglad_ramienia;
    Appearance wyglad_cylindra;
    Appearance wyglad_podstawy;
    Appearance wyglad_prymitywu;
    Appearance wyglad_chwytaka;
    Appearance wyglad_nieba;

    // obiekty na scenie
    Cylinder cylinder, cylinder1, przegub1, przegub2;
    Box podloga, podstawa, ramie1, ramie2;

    // interpolatory - odpowiedzialne za obrot robota
    private RotationInterpolator Rot_cylinder, Rot_przegub1, Rot_przegub2, Rot_kulka;

    // kąty przesunięć robota
    private float k_cylinder = 0f;
    private float k_przegub1 = 0f;
    private float k_przegub2 = 0f;
    private float ograniczenie1 = 3.5f;
    private float ograniczenie2 = 4f;
    private float wartosc;


    double ruch = 0.04;

    public Boolean w_nauka = false;
    public Boolean czy_chwyt = false;
    private int zliczanie[];
    public int i;
    public int ostatni;



    private BranchGroup createSceneGraph() {

        // Tekstura podlogi
        Appearance wyglad_podloga = new Appearance();
        wyglad_podloga.setTexture(createTexture("img/ground4.jpg"));
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.BLEND);
    //    wyglad_podloga.setTextureAttributes(texAttr);
        wyglad_podloga.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

        // Tekstura robot
        Appearance wyglad_robot = new Appearance();
        wyglad_robot.setTexture(createTexture("img/ground3.jpg"));
        wyglad_robot.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

        // wygląd podstawy
        wyglad_podstawy = new Appearance();
        wyglad_podstawy.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        wyglad_podstawy.setMaterial(m2);


        // wygląd cylindra
        wyglad_cylindra = new Appearance();
        wyglad_cylindra.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        wyglad_cylindra.setMaterial(m);

        // wygląd ramienia
        wyglad_ramienia = new Appearance();
        wyglad_ramienia.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        wyglad_ramienia.setMaterial(m3);

        // wyglad prymitywu

        wyglad_prymitywu = new Appearance();
        wyglad_prymitywu.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        wyglad_prymitywu.setMaterial(m);

        // wyglad sfey

        wyglad_nieba = new Appearance();
        wyglad_nieba.setTexture(createTexture("img/gwiazdy.jpg"));
        wyglad_nieba.setCapability(Appearance.ALLOW_MATERIAL_WRITE);




        Alpha alpha1 = new Alpha(-1, 5000);

        BranchGroup scena = new BranchGroup();
        bg_prymityw = new BranchGroup();
        bg_prymityw.setCapability(bg_prymityw.ALLOW_DETACH);
        bg_prymityw.setCapability(bg_prymityw.ALLOW_CHILDREN_WRITE);
        bg_prymityw.setCapability(bg_prymityw.ALLOW_CHILDREN_READ);
        bg_prymityw.setCapability(bg_prymityw.ALLOW_CHILDREN_EXTEND);

        trans_podloga = new TransformGroup();
        trans_podstawa = new TransformGroup();
        trans_cylinder = new TransformGroup();
        trans_cylinder2 = new TransformGroup();
        trans_przegub1 = new TransformGroup();
        trans_ramie1 = new TransformGroup();
        trans_przegub2 = new TransformGroup();
        trans_ramie2 = new TransformGroup();
        trans_prymityw = new TransformGroup();
        trans_chwytak = new TransformGroup();
        trans_glowny = new TransformGroup();
        trans_kulka = new TransformGroup();

        trans_podloga.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_podloga.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        trans_podloga.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        trans_podloga.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

        trans_podstawa.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_podstawa.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        trans_podstawa.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        trans_podstawa.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

        trans_cylinder.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_cylinder2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_przegub1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_ramie1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_przegub2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_ramie2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_prymityw.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_chwytak.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        trans_chwytak.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        trans_chwytak.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        trans_chwytak.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

        trans_glowny.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        trans_glowny.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        trans_glowny.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

        trans_kulka.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_kulka.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        trans_kulka.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        trans_kulka.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

         BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0); // ograniczenie

        // 0.Podloga
        trans3d_podloga = new Transform3D();
        trans3d_podloga.set(new Vector3f(0.0f,-0.1f,0.0f));

        // TransforGroup pomocniczy
        TransformGroup podloga_p = new TransformGroup();
        podloga_p.setTransform(trans3d_podloga);
        podloga = new Box(10.0f, 0.0f, 10.0f,Box.GENERATE_TEXTURE_COORDS ,wyglad_podloga);
        podloga_p.addChild(podloga);
        trans_podloga.addChild(podloga_p);
        trans_podloga.addChild(trans_podstawa);

        // 1.Podstawa
        trans3d_podstawa = new Transform3D();
        trans3d_podstawa.set(new Vector3f(0.0f,0.0f,0.0f));
        podstawa = new Box(0.3f, 0.1f, 0.3f,Box.GENERATE_TEXTURE_COORDS, wyglad_robot);

        trans_podstawa.addChild(podstawa);


        // 2.Cylinder - obrot
        trans3d_cylinder = new Transform3D();
        trans3d_cylinder.set(new Vector3f(0.0f,0.4f,0.0f));

        Rot_cylinder = new RotationInterpolator(alpha1,trans_cylinder,trans3d_cylinder,0,0);
        Rot_cylinder.setSchedulingBounds(bounds);
        trans_cylinder.addChild(Rot_cylinder);
            // Pomocniczy transform group
            TransformGroup p_cylinder = new TransformGroup();
            cylinder = new Cylinder(0.2f, 0.6f, Cylinder.GENERATE_TEXTURE_COORDS ,wyglad_robot);
            p_cylinder.setTransform(trans3d_cylinder);
            p_cylinder.addChild(cylinder);

         trans_cylinder.addChild(p_cylinder);
         trans_podstawa.addChild(trans_cylinder);

        // 3.Cylinder poziomy
        trans3d_cylinder2_rot = new Transform3D();
        trans3d_cylinder2_rot.rotX(Math.PI/2);
        trans3d_cylinder2 = new Transform3D();
        trans3d_cylinder2.set(new Vector3f(0.0f,0.7f,0.0f));
        trans3d_cylinder2.mul(trans3d_cylinder2_rot);
        trans_cylinder2.setTransform(trans3d_cylinder2);
        cylinder1 = new Cylinder(0.2f, 0.6f, Cylinder.GENERATE_TEXTURE_COORDS ,wyglad_robot);
        trans_cylinder2.addChild(cylinder1);

         trans_cylinder.addChild(trans_cylinder2);

        //4.Przegub1
        trans3d_przegub1 = new Transform3D();
        trans3d_przegub1.set(new Vector3f(0.0f,0.3f,0.0f));

        Rot_przegub1 = new RotationInterpolator(alpha1, trans_przegub1, trans3d_przegub1, 0, 0);
        Rot_przegub1.setSchedulingBounds(bounds);
        trans_przegub1.addChild(Rot_przegub1);
            //Pomocniczy transform group
            TransformGroup p_przegub1 = new TransformGroup();
            przegub1 = new Cylinder(0.1f, 0.2f, Cylinder.GENERATE_NORMALS | Cylinder.GENERATE_TEXTURE_COORDS,wyglad_robot);
            p_przegub1.setTransform(trans3d_przegub1);
            p_przegub1.addChild(przegub1);


        trans_przegub1.addChild(p_przegub1);
         trans_cylinder2.addChild(trans_przegub1);

         //5.Ramie1
        trans3d_ramie1_rot = new Transform3D();
        trans3d_ramie1_rot.rotY(Math.PI/4);
        trans3d_ramie1 = new Transform3D();
        trans3d_ramie1.set(new Vector3f(0.3f,.3f,-0.3f));
        trans3d_ramie1.mul(trans3d_ramie1_rot);
        trans_ramie1.setTransform(trans3d_ramie1);
        ramie1 = new Box(0.4f,0.1f,0.1f,Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS ,wyglad_robot);
        trans_ramie1.addChild(ramie1);

         trans_przegub1.addChild(trans_ramie1);

        // 6.Przegub2
        trans3d_przegub2 = new Transform3D();
        trans3d_przegub2.set(new Vector3f(0.4f,-0.05f,0.0f));

        Rot_przegub2 = new RotationInterpolator(alpha1, trans_przegub2, trans3d_przegub2, 0, 0);
        Rot_przegub2.setSchedulingBounds(bounds);
        trans_przegub2.addChild(Rot_przegub2);
            //Pomocniczy Transform group
            TransformGroup p_przegub2 = new TransformGroup();
            przegub2 = new Cylinder(0.1f, 0.3f, Cylinder.GENERATE_NORMALS | Cylinder.GENERATE_TEXTURE_COORDS ,wyglad_robot);
            p_przegub2.setTransform(trans3d_przegub2);
            p_przegub2.addChild(przegub2);


        trans_przegub2.addChild(p_przegub2);
         trans_ramie1.addChild(trans_przegub2);

        //7.Ramie2
        trans3d_ramie2_rot = new Transform3D();
        trans3d_ramie2_rot.rotY(-Math.PI/3);
        trans3d_ramie2 = new Transform3D();
        trans3d_ramie2.set(new Vector3f(.4f,-0.15f,0.0f));
        trans3d_ramie2.mul(trans3d_ramie2_rot);
        trans_ramie2.setTransform(trans3d_ramie2);
        ramie2 = new Box(0.45f,0.1f,0.1f,Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS ,wyglad_robot);
        trans_ramie2.addChild(ramie2);

         trans_przegub2.addChild(trans_ramie2);

         //8.Element chwytajacy

          trans3d_chwytaka = new Transform3D();
          Transform3D rot = new Transform3D();
          rot.rotZ(Math.PI/2);
          trans3d_chwytaka.set(new Vector3f(0.45f,0.0f,0.0f));
          trans3d_chwytaka.mul(rot);


           //Pomocniczy Transform group
            TransformGroup p_chwytak = new TransformGroup();
            Cone chwytak = new Cone(0.1f,0.4f, wyglad_chwytaka);
            p_chwytak.setTransform(trans3d_chwytaka);
            p_chwytak.addChild(chwytak);


           trans_chwytak.addChild(p_chwytak);
           trans_ramie2.addChild(trans_chwytak);


         //9.prymityw


         trans3d_prymityw = new Transform3D();
         trans3d_prymityw.set(prymityw_pozycja);
         trans3d_prymityw_rot = new Transform3D();
         trans3d_prymityw_rot.set(new Vector3f(0.0f, 0.0f, 0.0f));
         Rot_kulka = new RotationInterpolator(alpha1, trans_prymityw, trans3d_prymityw_rot, 0, 0);
         Rot_kulka.setSchedulingBounds(bounds);
         trans_prymityw.addChild(Rot_kulka);

            //Pomocniczy Transform group
            Sphere prymityw = new Sphere(0.1f, wyglad_prymitywu);

            trans_kulka.setTransform(trans3d_prymityw);
            trans_kulka.addChild(prymityw);

         trans_prymityw.addChild(trans_kulka);
         bg_prymityw.addChild(trans_prymityw);
         trans_glowny.addChild(bg_prymityw);


         // xddd

      // Box niebo = new Box(11.0f,11.0f,11.0f, Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS_INWARD, wyglad_nieba);
       //trans_glowny.addChild(niebo);



        // światło kierunkowe
        Color3f light1Color = new Color3f(0.5f, 0.3f, 0.4f);
        Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
        DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        scena.addChild(light1);

        Color3f light2Color = new Color3f(0.5f, 0.3f, 0.4f);
        Vector3f light2Direction = new Vector3f(-4.0f, 7.0f, 12.0f);
        DirectionalLight light2 = new DirectionalLight(light2Color, light2Direction);
        light2.setInfluencingBounds(bounds);
        scena.addChild(light2);

        AmbientLight swiatlo_tla = new AmbientLight(new Color3f(1.0f, 1.0f, 1.0f));
       swiatlo_tla.setInfluencingBounds(bounds);
      // scena.addChild(swiatlo_tla);

       scena.addChild(trans_podloga);
       scena.addChild(trans_glowny);


        CollisionDetector detect = new CollisionDetector(prymityw, new BoundingSphere(new Point3d(), 0.065d));
        detect.setSchedulingBounds(bounds);
        scena.addChild(detect);

       scena.compile();
       return scena;
    }

     public void chwytanie(){


                if( czy_chwyt == false ){
                trans3d_prymityw.set(new Vector3f(prymityw_chwyt));
                Rot_kulka.setMaximumAngle(0);
                Rot_kulka.setMinimumAngle(0);
                trans_kulka.setTransform(trans3d_prymityw);

                trans_glowny.removeChild(bg_prymityw);
                trans_chwytak.addChild(bg_prymityw);

                }
                else{

                trans3d_prymityw.set(prymityw_pozycja);        // <---- tutaj jest pozycja do której wraca prymityw po upuszczeniu
                Rot_kulka.setMinimumAngle(k_cylinder);
                Rot_kulka.setMaximumAngle(k_cylinder);
                trans_kulka.setTransform(trans3d_prymityw);

                trans_chwytak.removeChild(bg_prymityw);
                trans_glowny.addChild(bg_prymityw);

                }
                czy_chwyt = !czy_chwyt;

    }
    private void dolaczPanel(JPanel panel) {

        int x_size, y_size;
        x_size = Okno.getWidth();
        y_size = Okno.getHeight();
        panel.setSize(x_size, y_size);
        Okno.add(panel);
    }

    public Robot()    {

       zliczanie = new int[1000];


        initComponents();

        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

         Canvas3D canvas = new Canvas3D((new GraphicsConfigTemplate3D()).getBestConfiguration(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getConfigurations()));
         SimpleUniverse univ = new SimpleUniverse(canvas);
         univ.getViewingPlatform().setNominalViewingTransform();
         canvas.addKeyListener(this);

         BranchGroup scene = createSceneGraph();
         scene.compile();

         OrbitBehavior obserwator = new OrbitBehavior(canvas);
         BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
         obserwator.setSchedulingBounds(bounds);
         vPlatform = univ.getViewingPlatform();
         Transform3D temp = new Transform3D();
         temp.set(new Vector3f(0f,0.6f,5.0f));
         vPlatform.getViewPlatformTransform().setTransform(temp);
         vPlatform.setViewPlatformBehavior(obserwator);

         scene.compile();
         univ.addBranchGraph(scene);
         panel.add("Center", canvas);
         dolaczPanel(panel);
         setResizable(false);
         createBufferStrategy(2);


         zegar.scheduleAtFixedRate(new Ruch(), 1, 1);

    }



    public void powtarzanie() throws InterruptedException {

      if ( w_nauka == true)
        {
     k_cylinder = 0f;
     k_przegub1 = 0f;
     k_przegub2 = 0f;

     if(czy_chwyt == false)
     {
     Rot_kulka.setMaximumAngle(0);
     Rot_kulka.setMinimumAngle(0);
     }

     if (czy_chwyt == true)
     chwytanie();


        int zmienna;
        for( int k = 0 ; k  <  i; k++)
        {
           zmienna = zliczanie[k];

           if (zmienna == 1){
              k_cylinder -= ruch;
              Thread.sleep(40);

              zmienna = 0;
           }
           if (zmienna == 2){
              k_cylinder += ruch;
              Thread.sleep(40);
              zmienna = 0;
           }
           if (zmienna == 3){

            k_przegub1 -= ruch;
            Thread.sleep(40);
              zmienna = 0;
           }
           if (zmienna == 4){

            k_przegub1 += ruch;
            Thread.sleep(40);
              zmienna = 0;
           }
           if (zmienna == 5){

                k_przegub2 -= ruch;
                Thread.sleep(40);
               zmienna = 0;

           }
           if (zmienna == 6){

                k_przegub2 += ruch;
                Thread.sleep(40);
                zmienna = 0;
           }
           if (zmienna == 7){

               chwytanie();

           }

        }

        w_nauka = false;
        i = 0;


        }

    };



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Okno = new javax.swing.JPanel();
        lewo = new javax.swing.JButton();
        prawo = new javax.swing.JButton();
        gora = new javax.swing.JButton();
        dol = new javax.swing.JButton();
        elbow1 = new javax.swing.JButton();
        elbow2 = new javax.swing.JButton();
        nauka = new javax.swing.JButton();
        powtarzanie = new javax.swing.JButton();
        chwyt = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Okno.setPreferredSize(new java.awt.Dimension(600, 1000));

        lewo.setText("waist -");
        lewo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lewoActionPerformed(evt);
            }
        });

        prawo.setText("waist +");
        prawo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prawoActionPerformed(evt);
            }
        });

        gora.setText("shoulder +");
        gora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goraActionPerformed(evt);
            }
        });

        dol.setText("shoulder -");
        dol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dolActionPerformed(evt);
            }
        });

        elbow1.setText("elbow -");
        elbow1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                elbow1ActionPerformed(evt);
            }
        });

        elbow2.setText("elbow +");
        elbow2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                elbow2ActionPerformed(evt);
            }
        });

        nauka.setText("start nauki");
        nauka.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                naukaActionPerformed(evt);
            }
        });

        powtarzanie.setText("Powtarzanie");
        powtarzanie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                powtarzanieActionPerformed(evt);
            }
        });

        chwyt.setText("chwyt");
        chwyt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chwytActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout OknoLayout = new javax.swing.GroupLayout(Okno);
        Okno.setLayout(OknoLayout);
        OknoLayout.setHorizontalGroup(
            OknoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(OknoLayout.createSequentialGroup()
                .addGroup(OknoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(OknoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lewo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(prawo))
                    .addGroup(OknoLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(gora)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(OknoLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(dol)
                .addGap(64, 64, 64)
                .addComponent(elbow1)
                .addGap(18, 18, 18)
                .addComponent(elbow2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 283, Short.MAX_VALUE)
                .addComponent(chwyt)
                .addGap(31, 31, 31)
                .addComponent(nauka)
                .addGap(33, 33, 33)
                .addComponent(powtarzanie)
                .addGap(49, 49, 49))
        );
        OknoLayout.setVerticalGroup(
            OknoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, OknoLayout.createSequentialGroup()
                .addGap(0, 597, Short.MAX_VALUE)
                .addComponent(gora)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(OknoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prawo)
                    .addComponent(lewo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(OknoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dol)
                    .addComponent(elbow1)
                    .addComponent(elbow2)
                    .addComponent(nauka)
                    .addComponent(powtarzanie)
                    .addComponent(chwyt))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Okno, javax.swing.GroupLayout.PREFERRED_SIZE, 1015, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Okno, javax.swing.GroupLayout.PREFERRED_SIZE, 699, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lewoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lewoActionPerformed

         if((CollisionDetector.inCollision == false || czy_chwyt == true) || (CollisionDetector.inCollision == true && ostatni == 2))
         {
           k_cylinder -= ruch;
              if(w_nauka == true)
            {
                zliczanie[i] = 1;
                i++;
            }
              ostatni =  1;
         }
    }//GEN-LAST:event_lewoActionPerformed

    private void prawoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prawoActionPerformed
             if((CollisionDetector.inCollision == false || czy_chwyt == true)||(CollisionDetector.inCollision == true && ostatni == 1))
             {
             k_cylinder += ruch;
              if(w_nauka == true)
            {
                zliczanie[i] = 2;
                i++;
            }
              ostatni = 2;
             }
    }//GEN-LAST:event_prawoActionPerformed

    private void goraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goraActionPerformed

        if(((k_przegub1 < Math.PI/6 && CollisionDetector.inCollision == false)||(k_przegub1 < Math.PI/6 && czy_chwyt == true))||(CollisionDetector.inCollision == true && ostatni == 3))
        {
                k_przegub1 += ruch;
              if(w_nauka == true)
            {
                zliczanie[i] = 4;
                i++;
            }
              ostatni = 4;
        }
    }//GEN-LAST:event_goraActionPerformed

    private void dolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dolActionPerformed
        if(czy_chwyt == false)
            wartosc = ograniczenie1;
        else wartosc = ograniczenie2;


         if(((k_przegub1 > -Math.PI/wartosc && CollisionDetector.inCollision == false)||(k_przegub1 > -Math.PI/wartosc && czy_chwyt == true))||(CollisionDetector.inCollision == true && ostatni == 4))
        {
            k_przegub1 -= ruch;
             if(w_nauka == true)
            {
                zliczanie[i] = 3;
                i++;
            }
             ostatni = 3;
        }
    }//GEN-LAST:event_dolActionPerformed

    private void elbow1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elbow1ActionPerformed
        if(((k_przegub2 > -Math.PI/4 && CollisionDetector.inCollision == false)||(k_przegub2 > -Math.PI/4 && czy_chwyt == true))||(CollisionDetector.inCollision == true && ostatni == 6))
        {
                k_przegub2 -= ruch;
        if(w_nauka == true)
            {
                zliczanie[i] = 5;
                i++;
            }
        ostatni = 5;
        }
    }//GEN-LAST:event_elbow1ActionPerformed

    private void elbow2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elbow2ActionPerformed
       if(((k_przegub2 < Math.PI/4 && CollisionDetector.inCollision == false)||(k_przegub2 < Math.PI/4 && czy_chwyt == true))||(CollisionDetector.inCollision == true && ostatni == 5))
       {
            k_przegub2 += ruch;
       if(w_nauka == true)
            {
                zliczanie[i] = 6;
                i++;
            }
       ostatni = 6;
       }
    }//GEN-LAST:event_elbow2ActionPerformed

    private void naukaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_naukaActionPerformed
            w_nauka = true;

    }//GEN-LAST:event_naukaActionPerformed

    private void powtarzanieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_powtarzanieActionPerformed
        try {
            powtarzanie();
        } catch (InterruptedException ex) {
            Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_powtarzanieActionPerformed

    private void chwytActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chwytActionPerformed
       if(CollisionDetector.inCollision == true || czy_chwyt == true)
       {
        chwytanie();
        zliczanie[i] = 7;
        i++;
       }
    }//GEN-LAST:event_chwytActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])  {


        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Robot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Robot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Robot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Robot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Robot().setVisible(true);

            }
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {
   //     throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == KeyEvent.VK_LEFT){
             if((CollisionDetector.inCollision == false || czy_chwyt == true) || (CollisionDetector.inCollision == true && ostatni == 2))
             {
            if(w_nauka == true)
            {
                zliczanie[i] = 1;
                i++;
            }

            k_cylinder -= ruch;
            ostatni = 1;
             }
        }

        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
             if((CollisionDetector.inCollision == false || czy_chwyt == true)||(CollisionDetector.inCollision == true && ostatni == 1)){
            if(w_nauka == true)
            {
                zliczanie[i] = 2;
                i++;
            }
            k_cylinder += ruch;
            ostatni = 2;
             }
        }


        if(e.getKeyCode() == KeyEvent.VK_DOWN){
            if(((k_przegub1 > -Math.PI/wartosc && CollisionDetector.inCollision == false)||(k_przegub1 > -Math.PI/wartosc && czy_chwyt == true))||(CollisionDetector.inCollision == true && ostatni == 4)){
            if(w_nauka == true)
            {
                zliczanie[i] = 3;
                i++;
            }
            if(czy_chwyt == false)
            wartosc = ograniczenie1;
            else wartosc = ograniczenie2;

            if(k_przegub1 > -Math.PI/wartosc)
                k_przegub1 -= ruch;
            ostatni = 3;
            }
        }

        if(e.getKeyCode() == KeyEvent.VK_UP){

            if(((k_przegub1 < Math.PI/6 && CollisionDetector.inCollision == false)||(k_przegub1 < Math.PI/6 && czy_chwyt == true))||(CollisionDetector.inCollision == true && ostatni == 3)){

            k_przegub1 += ruch;
            ostatni = 4;

            if(w_nauka == true)
            {
                zliczanie[i] = 4;
                i++;
            }
            }


        }

        if(e.getKeyCode() == KeyEvent.VK_S){
            if(((k_przegub2 > -Math.PI/4 && CollisionDetector.inCollision == false)||(k_przegub2 > -Math.PI/4 && czy_chwyt == true))||(CollisionDetector.inCollision == true && ostatni == 6)){
            if(w_nauka == true)
            {
                zliczanie[i] = 5;
                i++;
            }

            if(k_przegub2 > -Math.PI/4)
                k_przegub2 -= ruch;
            ostatni = 5;
            }
            }

        if(e.getKeyCode() == KeyEvent.VK_W){
            if(((k_przegub2 < Math.PI/4 && CollisionDetector.inCollision == false)||(k_przegub2 < Math.PI/4 && czy_chwyt == true))||(CollisionDetector.inCollision == true && ostatni == 5)){
            if(w_nauka == true)
            {
                zliczanie[i] = 6;
                i++;
            }
            if(k_przegub2 < Math.PI/4)
            k_przegub2 += ruch;
            ostatni = 6;
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE)
           if(CollisionDetector.inCollision == true || czy_chwyt == true)
        {
        chwytanie();
        zliczanie[i] = 7;
        i++;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
     //   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent evt) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Texture createTexture(String sciezka) {
         // Załadowanie tekstury
        TextureLoader loader = new TextureLoader(sciezka, null);
        ImageComponent2D image = loader.getImage();

        if (image == null) {
          System.out.println("Nie udało się załadować tekstury: " + sciezka);
        }

        // can't use parameterless constuctor
        Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
        texture.setMagFilter(Texture.NICEST);
        texture.setMinFilter(Texture.NICEST);
        texture.setImage(0, image);

        return texture;

    }

    private class Ruch extends TimerTask{

        public void run(){
            Rot_cylinder.setMinimumAngle(k_cylinder);
            Rot_cylinder.setMaximumAngle(k_cylinder);
            Rot_przegub1.setMinimumAngle(k_przegub1);
            Rot_przegub1.setMaximumAngle(k_przegub1);
            Rot_przegub2.setMinimumAngle(k_przegub2);
            Rot_przegub2.setMaximumAngle(k_przegub2);

        }
    }






    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Okno;
    private javax.swing.JButton chwyt;
    private javax.swing.JButton dol;
    private javax.swing.JButton elbow1;
    private javax.swing.JButton elbow2;
    private javax.swing.JButton gora;
    private javax.swing.JButton lewo;
    private javax.swing.JButton nauka;
    private javax.swing.JButton powtarzanie;
    private javax.swing.JButton prawo;
    // End of variables declaration//GEN-END:variables
}
