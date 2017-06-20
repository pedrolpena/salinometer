/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package salinometer;

import gnu.io.*;
//import gnu.io.CommPortIdentifier;
//import gnu.io.SerialPort;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Enumeration;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingWorker;

/**
 *
 * @author pena
 */
public class SalinometerSerialRead implements Runnable {

    private Thread thisThread;
    private Preferences prefs;
    private SerialPort port;
    private String line = "";
    private String temp = "";
    private int lineLength = 0;
    private int rawChar;
    private char rxChar;
    private boolean serialPortFound = true;
    private boolean isAlive = true;
    private double conductivity = 0.0;
    private int noDataTimeout = 0;
    private InputStreamReader ist = null;
    private dataSyncingSingleton syncData = dataSyncingSingleton.getInstance();
    private int counter = 10;
    private ProgressBarJDialog pbj;
    private long currentTime = System.currentTimeMillis();
    private long previousTime = System.currentTimeMillis();
    private long standByReadingTime = System.currentTimeMillis();
    private long standByReadingPreviousTime = System.currentTimeMillis();
    private int readingTime;

    public SalinometerSerialRead(SerialPort cp) {
        port = cp;
        prefs = Preferences.userNodeForPackage(getClass());
        readingTime = prefs.getInt("readingTime", 10);
        syncData.setStartReading(false);
        thisThread = new Thread(this, "SalinometerSerialRead");
        thisThread.start();

    } //end constructor

    public void close() {

        isAlive = false;
        try {
            if (ist != null) {

                ist.close();
                ist = null;
            }
            if (port != null) {

                port.close();
                port = null;
            }

        }//end try
        catch (Exception e) {

            e.printStackTrace();

        }//end catch

    }//end colse

    @Override
    public void run() {

        try {
            ist = new InputStreamReader(port.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Pattern standbyReading = Pattern.compile("[FT][0-9][0-9][0-9][0-9][ ][0-9][0-9][+][0-9][0-9][0-9][0-9]");
        Matcher standbyReadingMatcher;

        Pattern readReading = Pattern.compile("[FT][0-9][0-9][0-9][0-9][ ][0-2]" + Pattern.quote(".") + "[0-9][+][0-9][0-9][0-9][0-9]");
        Matcher readReadingMatcher;

        Pattern readMinusReading = Pattern.compile("[FT][0-9][0-9][0-9][0-9][ ][0-2]" + Pattern.quote(".") + "[0-9][-][0-9][0-9][0-9][0-9]");
        Matcher readMinusReadingMatcher;

        Pattern autosalNoConnection = Pattern.compile("AUTOSAL NC");
        Matcher autosalNoConnectionMatcher;

//        ProgressBarJDialog pbj = new ProgressBarJDialog(null, true);
//        pbj.setMinimumValue(1);
//        pbj.setMaximumValue(20);
//        //pbj.setMessage("We got this freaking robot here, there he is.");
//        //pbj.setDialogTitle("Test Title");
//
//        pbj.startProgressBar();
//        pbj.setVisible(true);
//        try{
//        for (int i =1; i<=20 ;i++ ){
//            
//            syncData.tester = i;
//            //Thread.sleep(500);
//        }//end for
//        }
//        catch(Exception e){
        //       e.printStackTrace();}
        while (serialPortFound && isAlive) {
            currentTime = System.currentTimeMillis();
            try {
                Thread.sleep(5);

                if (ist != null && ist.ready()) { // need to check or it will block
                    rawChar = ist.read();

                    rxChar = (char) rawChar;

                    if (rawChar != 9 && rxChar != '\r' && rxChar != '\n') {
                        line += rxChar;
                    } else {
                        if (!line.equals("")) {
                            lineLength = line.length();
                            standbyReadingMatcher = standbyReading.matcher(line);
                            autosalNoConnectionMatcher = autosalNoConnection.matcher(line);
                            readReadingMatcher = readReading.matcher(line);
                            readMinusReadingMatcher = readMinusReading.matcher(line);

                            if (readReadingMatcher.matches()) {
                                syncData.setSalinometerConnected(true);
                                syncData.setFunctionStatus("Reading");

                                temp = line.substring(6);
                                temp = temp.replace("+", "");
                                conductivity = Double.parseDouble(temp);

                                if (conductivity * 100000 > 0 && conductivity * 100000 <= 90) {
                                    System.out.println("ZERO");
                                    syncData.setSalinometerConnected(true);
                                    syncData.setFunctionStatus("Zero");
                                    //previousTime = System.currentTimeMillis();

                                } else {

                                    System.out.println("Conductivity = " + conductivity / 2);
                                }
                                previousTime = System.currentTimeMillis();
                            }

                            if (readMinusReadingMatcher.matches()) {
                                syncData.setSalinometerConnected(true);
                                syncData.setFunctionStatus("NEGATIVE VALUE, CHECK SUPRESSION");
                                previousTime = System.currentTimeMillis();

                                System.out.println("NEGATIVE VALUE, CHECK SUPRESSION = " + line);

                            }

                            if (standbyReadingMatcher.matches()) {

                                standByReadingTime = currentTime - standByReadingPreviousTime;
                                getReading(standByReadingTime);
                                syncData.setSalinometerConnected(true);
                                syncData.setFunctionStatus("Standby");
                                previousTime = System.currentTimeMillis();
                                System.out.println("STANDBY Reading = " + line);

                            }
                            if (autosalNoConnectionMatcher.matches()) {
                                syncData.setSalinometerConnected(false);
                                syncData.setFunctionStatus("No Connection");
                                System.out.println("Check the connection to the autosal.");
                            }

                            //System.out.println(line + " " + line.length());
                        }
                        line = "";

                    }//end else
                }//end if
                else {
                    //System.out.println("Not Ready To Read");
                }

            } catch (Exception e) {
                e.printStackTrace();

                System.out.println("Check the connection to the autosal.");
                syncData.setSalinometerConnected(false);
                syncData.setFunctionStatus("No Connection");
            }

            if (currentTime - previousTime >= 2000) {
//                if (noDataTimeout == 3) {
//                    this.close();
//                    
//                    noDataTimeout = 0;
//
//                }
                syncData.setSalinometerConnected(false);
                syncData.setFunctionStatus("No Connection");
                System.out.println("Check the connection to the autosal.");
                previousTime = System.currentTimeMillis();
                //noDataTimeout++;

            }//end if = 
        }//end while

    }

    void getReading(long elapsedTime) {

       
        if (this.syncData.isRunCreated() && !syncData.getTookReading()) {

            getStandbyReading(1, readingTime);
            syncData.setStartReading(true);

            currentTime = System.currentTimeMillis();
            standByReadingPreviousTime = System.currentTimeMillis();
            standByReadingTime = currentTime - standByReadingPreviousTime;
        }

        if (pbj != null && standByReadingTime < readingTime*1000) {
            //System.out.println((int) standByReadingTime / 1000 + 1);
            pbj.setCurrentValue((int) standByReadingTime / 1000 + 1);

        } else {
            if (pbj != null) {
                pbj.dispose();
            }
            pbj = null;

        }

    }//end endGetReading

    void getStandbyReading(int min, int max) {

        // SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
//       ProgressBarJDialog pbj;
        // @Override
        //protected Void doInBackground() {
        pbj = new ProgressBarJDialog(null, false);
        pbj.setMinimumValue(min);
        pbj.setMaximumValue(max);
        pbj.setMessage("Getting Standby Reading.......");
        //pbj.setDialogTitle("Test Title");

        pbj.startProgressBar();
        pbj.setVisible(true);
        try {

//                    for (int i = 1; i <= 20; i++) {
//
//                        pbj.setCurrentValue(i);
//                        Thread.sleep(250);
//                    }//end for
            //pbj.setVisible(true); 
        }//end try
        catch (Exception e) {
            e.printStackTrace();
        }
        //return null;
    }

    //// @Override
    //protected void done() {
    //    pbj.dispose();//close the modal dialog
    // }
    // };
    //sw.execute(); // this will start the processing on a separate thread        
}//end getStandByReading     

//}
