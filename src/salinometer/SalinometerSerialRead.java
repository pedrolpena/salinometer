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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author pena
 */
public class SalinometerSerialRead implements Runnable {

    private Thread thisThread;
    private SerialPort port;
    private String line = "";
    private String temp = "";
    private int lineLength = 0;
    private int rawChar;
    private char rxChar;
    private boolean serialPortFound = true;
    private boolean isAlive = true;
    private double conductivity = 0.0;
    int noDataTimeout = 0;
    private InputStreamReader ist = null;
    private dataSyncingSingleton syncData = dataSyncingSingleton.getInstance();

    public SalinometerSerialRead(SerialPort cp) {
        port = cp;

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
        long currentTime = System.currentTimeMillis();
        long previousTime = System.currentTimeMillis();

        while (serialPortFound && isAlive) {
            currentTime = System.currentTimeMillis();
            try {
                Thread.sleep(5);
                
                if (ist!=null && ist.ready()) { // need to check or it will block
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
                else{
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

}
