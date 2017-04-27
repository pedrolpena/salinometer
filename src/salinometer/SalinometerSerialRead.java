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

    Thread thisThread;
    SerialPort port;
    String line = "";
    String temp = "";
    int lineLength = 0;
    int rawChar;
    char rxChar;
    boolean serialPortFound = true;
    double conductivity = 0.0;
    InputStreamReader ist = null;

    public SalinometerSerialRead(SerialPort cp) {
        port = cp;
        try {
            ist = new InputStreamReader(port.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        thisThread = new Thread(this, "SalinometerSerialRead");
        thisThread.start();

    } //end constructor

    @Override
    public void run() {

        Pattern standbyReading = Pattern.compile("[FT][0-9][0-9][0-9][0-9][ ][0-9][0-9][+][0-9][0-9][0-9][0-9]");
        Matcher standbyReadingMatcher;

        Pattern readReading = Pattern.compile("[FT][0-9][0-9][0-9][0-9][ ][0-2]" + Pattern.quote(".") + "[0-9][+][0-9][0-9][0-9][0-9]");
        Matcher readReadingMatcher;

        Pattern readMinusReading = Pattern.compile("[FT][0-9][0-9][0-9][0-9][ ][0-2]" + Pattern.quote(".") + "[0-9][-][0-9][0-9][0-9][0-9]");
        Matcher readMinusReadingMatcher;

        Pattern autosalNoConnection = Pattern.compile("AUTOSAL NC");
        Matcher autosalNoConnectionMatcher;

        while (true && serialPortFound) {
            try {
                Thread.sleep(5);

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

                            temp = line.substring(6);
                            temp = temp.replace("+", "");
                            conductivity = Double.parseDouble(temp);

                            if (conductivity * 100000 > 0 && conductivity * 100000 <= 90) {
                                System.out.println("ZERO reading");

                            } else {

                                System.out.println("Conductivity = " + conductivity / 2);
                            }
                        }

                        if (readMinusReadingMatcher.matches()) {

                            System.out.println("NEGATIVE VALUE, CHECK SUPRESSION = " + line);
                        }

                        if (standbyReadingMatcher.matches()) {
                            System.out.println("STANDBY Reading = " + line);
                        }
                        if (autosalNoConnectionMatcher.matches()) {
                            System.out.println("Check the connection to the autosal.");
                        }

                        //System.out.println(line + " " + line.length());
                    }
                    line = "";

                }

            } catch (Exception e) {

                System.out.println("Check the connection to the autosal.");
            }

        }//end while

    }

}
