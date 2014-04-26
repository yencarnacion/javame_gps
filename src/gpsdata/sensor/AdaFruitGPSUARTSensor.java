/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. 
 * Java Embedded MOOC
 * 
 * February 2014
 */
package gpsdata.sensor;

//import com.oracle.deviceaccess.uart.UART;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.microedition.io.CommConnection;
import javax.microedition.io.Connector;

//import java.io.IOException;

/**
 * AdaFruit GPS sensor accessed through the UART interface of the device IO API.
 *
 * @author Simon
 */
public class AdaFruitGPSUARTSensor extends AdaFruitGPSSensor
        implements AutoCloseable {

    private static final int UART_DEVICE_ID = 40;

//    private UART uart;

    /**
     * Constructor
     *
     * @throws IOException If there is an IO error
     */
    public AdaFruitGPSUARTSensor() throws Exception {
        // YOUR CODE HERE
      CommConnection con = (CommConnection) Connector.open("comm:/dev/ttyUSB0;baudrate=9600");
      InputStream inputstream = con.openInputStream();
      Reader r = new InputStreamReader(inputstream);
      serialBufferedReader = new BufferedReader(r);
        System.out.println("AdaFruit GPS Sensor: DIO API UART opened");
    }

    /**
     * Close the connection to the GPS receiver via the UART
     *
     * @throws IOException If there is an IO error
     */
    @Override
    public void close() throws Exception {
        serialBufferedReader.close();
        //uart.close();
    }
}
