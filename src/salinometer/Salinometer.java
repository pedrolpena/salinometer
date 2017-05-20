/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package salinometer;


/**
 *
 * @author pena
 */
public class Salinometer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // TODO code application logic here


        try {

            mainView mv = new mainView();
            mv.init();
            mv.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();


        }

    }
}
