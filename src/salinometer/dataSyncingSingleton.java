/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package salinometer;

/**
 *
 * @author user
 */
public class dataSyncingSingleton {

    private boolean serialPortOpen = false;
    private boolean isSalinometerConnected = false;
    private boolean isRunCreated = false;
    private String functionStatus = "No Connection";
    private String runID;
    private String fileName;
    private String shipName;
    private String cruiseNumber;
    private String analyst;
    private double labTemp;

    private dataSyncingSingleton() {
    }

    public static dataSyncingSingleton getInstance() {
        return dataSyncingSingletonHolder.INSTANCE;
    }

    private static class dataSyncingSingletonHolder {

        private static final dataSyncingSingleton INSTANCE = new dataSyncingSingleton();
    }

    void setSalinometerConnected(boolean b) {
        isSalinometerConnected = b;

    }//end method

    boolean isSalinometerConnected() {
        return isSalinometerConnected;

    }//end method

    boolean isRunCreated() {
        return isRunCreated;
    }

    void setRunCreated(boolean b) {
        isRunCreated = b;
    }

    void setFunctionStatus(String status) {
        functionStatus = status;

    }

    String getFunctionStatus() {
        return functionStatus;
    }

    boolean isSerialPortOpen() {
        return serialPortOpen;
    }//end method

    void setSerialPortOpen(boolean spo) {
        serialPortOpen = spo;
    }

    String getRunID() {

        return runID;
    }//end mehtod

    String getFileName() {

        return fileName;
    }

    String getShipName() {

        return shipName;
    }

    String getCruiseNumber() {

        return cruiseNumber;
    }

    String getAnalyst() {

        return analyst;
    }

    double getLabTemp() {

        return labTemp;
    }

    void setRunID(String s) {

        runID = s;
    }//end mehtod

    void setFileName(String s) {

        fileName = s;
    }

    void setShipName(String s) {

        shipName = s;
    }

    void setCruiseNumber(String s) {

        cruiseNumber = s;
    }

    void setAnalyst(String s) {

        analyst = s;
    }

    void setLabTemp(double d) {

        labTemp = d;
    }

}
