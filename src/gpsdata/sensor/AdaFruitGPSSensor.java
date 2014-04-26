/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. 
 * Java Embedded MOOC
 * 
 * February 2014
 */
package gpsdata.sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import mooc.data.Messages;
import static mooc.data.Messages.ERROR;
import static mooc.data.Messages.INFO;
import mooc.data.gps.Position;
import mooc.data.gps.Velocity;
import mooc.sensor.GPSSensor;

/**
 * Common code for the AdaFruit Ultimate GPS sensor. This is an abstract class
 * so the implementation of the readDataLine method can be implemented in
 * subclasses that can use different methods to communicate with the GPS
 * receiver.
 *
 * @author Simon
 */
public abstract class AdaFruitGPSSensor implements GPSSensor, Messages {

    private static final String POSITION_TAG = "GPGGA";
    private static final String VELOCITY_TAG = "GPVTG";

    private final ArrayList<String> fields = new ArrayList<>();
    protected BufferedReader serialBufferedReader;
    private boolean verbose = false;
    private int messageLevel = 1;

    /**
     * Get a line of raw data from the GPS sensor
     *
     * @return The complete line of data
     * @throws IOException If there is an IO error
     */
    protected String readDataLine() throws IOException {
        String dataLine = null;

        /**
         * All data lines start with a '$' character so keep reading until we
         * find a valid line of data
         */
    
        // YOUR CODE HERE

        /* Return what we got */
        while(true){
            dataLine = serialBufferedReader.readLine();
            if(dataLine.startsWith("$")){
                break;
            }
        }
        return dataLine;
    }

    /**
     * Get a string of raw data from the GPS receiver. How this happens is
     * sub-class dependent.
     *
     * @param type The type of data to be retrieved
     * @return A line of data for that type
     * @throws IOException If there is an IO error
     */
    @Override
    public String getRawData(String type) throws IOException {
        boolean foundGGAData = false;
        String dataLine = null;

        /*
        * Read continuously from the device until type is matched
        */
        // YOUR CODE HERE
        
        int start = 0;
        String prefix = "$"+type;
        while(true){
            dataLine = serialBufferedReader.readLine();
            if(dataLine.startsWith(prefix)){
                break;
            }
        }

        //System.out.println("Prefix: "+prefix+";"+dataLine);
        //System.out.println("rawdata: "+dataLine.substring(prefix.length()+1));
        return dataLine.substring(prefix.length()+1);
    }

    /**
     * Get the current position
     *
     * @return The position data
     * @throws IOException If there is an IO error
     */
    @Override
    public Position getPosition() throws IOException {
        String rawData;
        long timeStamp = 0;
        double latitude = 0;
        double longitude = 0;
        double altitude = 0;
        char latitudeDirection = 0;
        char longitudeDirection = 0;

        /* Read data repeatedly, until we have valid data */
        while (true) {
            rawData = getRawData(POSITION_TAG);

            /* Handle situation where we didn't get data */
            if (rawData == null) {
                printMessage("NULL position data received", ERROR);
                continue;
            }

            if (rawData.contains("$GP")) {
                printMessage("Corrupt position data", ERROR);
                continue;
            }

            // YOUR CODE HERE
            int numRead = splitCSVString(rawData);
            if(numRead<10){
                printMessage("Did not read 10 fields", ERROR);
                continue;
            }
            
            System.out.println("rawdata: "+rawData);
            // (1) Time, (2) Latitude [N/S], (3) Longitude [E/W], 
            // (4) Fix Quality, (5) No of Satellites, (6) Horizontal Dilution, 
            // (7) Altitude in meters
            try {
                System.out.println(">>>>: ");
                timeStamp = Long.parseLong(fields.get(0).substring(0,fields.get(0).length()-4), 10);
                System.out.println("timestamp: "+timeStamp);


                System.out.println("latitude: "+fields.get(1).toString());
                String tmp = fields.get(1);
                latitude = Double.parseDouble(tmp.substring(0,tmp.length()-2));
                if(fields.get(2) == "S"){
                    //latitude = -latitude;
                    latitudeDirection = 'S';
                } else {
                    latitudeDirection = 'N';    
                }

                System.out.println("longitude: "+fields.get(3).toString());
                tmp = fields.get(3);
                longitude = Double.parseDouble(tmp.substring(0,tmp.length()-2));
                if(fields.get(4) == "W"){
                    //longitude = -longitude;
                    longitudeDirection = 'W';
                } else {
                    longitudeDirection = 'E';

                }

                System.out.println("altitude: "+fields.get(8).toString());
                altitude = Double.parseDouble(fields.get(8));        
                System.out.println("<<<<: ");

            } catch (Exception e){
                System.out.println("Algo Paso! "+ e.toString());
               printMessage(e.getMessage(), ERROR); 
            }
            /* Passed all the tests so we have valid data */
            break;
        }

        /* Record a time stamp for the reading */
        Date now = new Date();
        timeStamp = now.getTime() / 1000;

        /* Return the encapsulated data */
        return new Position(timeStamp, latitude, latitudeDirection,
                longitude, longitudeDirection, altitude);
    }

    /**
     * Get the current velocity
     *
     * @return The velocity data
     * @throws IOException If there is an IO error
     */
    @Override
    public Velocity getVelocity() throws IOException {
        String rawData = getRawData(VELOCITY_TAG);
        double track = 0;
        double speed = 0;

        while (true) {
            /* Handle the situation where we didn't get valid data */
            if (rawData == null) {
                printMessage("NULL velocity data received", ERROR);
                continue;
            }

            // YOUR CODE HERE
            int numRead = splitCSVString(rawData);
            if(numRead<7){
                printMessage("Did not read 7 fields", ERROR);
                continue;
            }
            
            System.out.println("rawdata: "+rawData);            
            //$GPVTG | Velocity Made Good Packet
            // (1) True Track Made Good (degrees) followed T, 
            // (2) Magnetic Track Made Good followed by M, 
            // (3) Ground Speed (knots) followed by N, 
            // (4) Ground Speed (km/hr) followed by K
            try {
                String tmp = fields.get(0);
                track = Double.parseDouble(tmp);
                System.out.println("track: "+track);

                tmp = fields.get(4);
                speed = Double.parseDouble(tmp);
                System.out.println("speed: "+speed);


            } catch (Exception e){
               System.out.println("Algo Paso! "+ e.toString());
               printMessage(e.getMessage(), ERROR); 
            }
            break;
        }

        printMessage("velocity data = " + rawData, INFO);

        /* Record a time stanp for the reading */
        Date now = new Date();
        long timeStamp = now.getTime() / 1000;

        printMessage("Bearing = " + fields.get(0), DATA);
        printMessage("speed = " + fields.get(6), DATA);

        /* Return the Velocity object */
        return new Velocity(timeStamp, track, speed);
    }

    /**
     * Turn on or off verbose messaging
     *
     * @param verbose Whether to enable verbose messages
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Set the level of messages to display, 1 = ERROR, 2 = INFO
     *
     * @param level The level for messages
     */
    public void setMessageLevel(int level) {
        messageLevel = level;
    }

    /**
     * Break a comma separated value string into its individual fields. We need
     * to have this as explicit code because Java ME does not support
     * String.split or java.util.regex and StringTokenizer has a bug that
     * affects empty fields.
     *
     * @param input The CSV input string
     * @return The number of fields extracted
     */
    private int splitCSVString(String input) {
        /* Clear the list of data fields */
        fields.clear();
        int start = 0;
        int end;

        while ((end = input.indexOf(",", start)) != -1) {
            fields.add(input.substring(start, end));
            start = end + 1;
        }

        return fields.size();
    }

    /**
     * Print a message if verbose messaging is turned on
     *
     * @param message The message to print
     * @param level Message level
     */
    protected void printMessage(String message, int level) {
        if (verbose && level <= messageLevel) {
            System.out.println("AdaFruit GPS Sensor: " + message);
        }
    }
}
