/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package salinometer;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.InputStreamReader;
import java.util.Enumeration;

/**
 *
 * @author pena
 */
public class Salinometer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String wantedPort = "/dev/ttyUSB0";
        //String wantedPort = "COM5";

        Enumeration portIdentifiers;
        // TODO code application logic here

        CommPortIdentifier portId = null;
        CommPortIdentifier pid = null;
        SerialPort port = null;
        try {
//            portId = CommPortIdentifier.getPortIdentifier("/dev/ttyUSB0");
//
//            portIdentifiers = CommPortIdentifier.getPortIdentifiers();
//            //try {
//            while (portIdentifiers.hasMoreElements()) {
//                //Thread.sleep(100);
//                pid = (CommPortIdentifier) portIdentifiers.nextElement();
//                System.out.println("Port Name = " + pid.getName());
//                if (pid.getName().contains(wantedPort)) {
//                    break;
//                }
//            }// end while
//
//            if (portId.getName().equals(wantedPort)) {
//                System.out.println("Selected " + portId.getName());
//                //portId = pid;
//                port = (SerialPort) portId.open("Salinometer", 10000);
//                port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//
//                //br = new BufferedReader(ist);
//            }// end if  
//            SalinometerSerialRead srr = new SalinometerSerialRead(port);
            mainView mv = new mainView();
            mv.init();
            mv.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();

            //SalinometerSerialRead srr = new SalinometerSerialRead(port);
            mainView mv = new mainView();
            mv.init();
            mv.setVisible(true);
        }

    }
}
