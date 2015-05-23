package br.com.rhonan.StartUp;

import java.io.IOException;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class Main {
	private static SerialPort serialPort;

	public static void main(final String[] args) throws IOException {
		serialPort = new SerialPort("/dev/cu.usbmodem1421");
		try {
			serialPort.openPort();
			serialPort.setParams(9600, 8, 1, 0);
			serialPort.readBytes();
			serialPort.addEventListener(new PortReader());
		} catch (final SerialPortException ex) {
			System.out.println(ex);
		}
	}

	private static class PortReader implements SerialPortEventListener {

		final StringBuilder message = new StringBuilder();
		Boolean receivingMessage = false;

		public void serialEvent(final SerialPortEvent serialPortEvent) {
			if (serialPortEvent.isRXCHAR() && (serialPortEvent.getEventValue() > 0)) {
				try {
					final byte buffer[] = serialPort.readBytes();
					for (final byte b : buffer) {
						if (b == '>') {
							receivingMessage = true;
							message.setLength(0);
						} else if (receivingMessage == true) {
							if (b == '\n') {
								receivingMessage = false;
								final String toProcess = message.toString();
								System.out.println(toProcess);
							} else {
								message.append((char) b);
							}
						}
					}
				} catch (final SerialPortException ex) {
					System.out.println(ex);
				}
			}
		}
	}
}
