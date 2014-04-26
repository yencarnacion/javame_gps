/* Copyright © 2014, Oracle and/or its affiliates. All rights reserved. 
 * Java Embedded MOOC
 * 
 * January 2014
 */
package gpsdata.sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.microedition.io.CommConnection;
import javax.microedition.io.Connector;
import java.io.Reader;

/**
 * Adafruit Ultimate GPS sensor connected to the Raspberry Pi via a serial port
 *
 * @author Simon
 */
public class AdaFruitGPSCommSensor extends AdaFruitGPSSensor
    implements AutoCloseable {
  /**
   * Constructor
   *
   * @param serialPort The serial port to use
   * @throws IOException If there is an IO error
   */
  public AdaFruitGPSCommSensor(String serialPort) throws IOException {
      // YOUR CODE HERE
      CommConnection con = (CommConnection) Connector.open("comm:"+serialPort+";baudrate=9600");
      InputStream inputstream = con.openInputStream();
      Reader r = new InputStreamReader(inputstream);
      serialBufferedReader = new BufferedReader(r);
    System.out.println("AdaFruit GPS Sensor: READY");
  }

  /**
   * Close the serial port
   *
   * @throws IOException If there is an IO error
   */
  @Override
  public void close() throws IOException {
    serialBufferedReader.close();
  }
}