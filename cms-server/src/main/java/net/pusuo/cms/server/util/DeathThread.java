package net.pusuo.cms.server.util;

import java.io.File;
import java.io.IOException;

// listenning signal to quit jvm
public class DeathThread implements Runnable {

	private static DeathThread dt = null;

	private File isAlive = null;

	private DeathThread () {
		try {
			isAlive = File.createTempFile("cms4_rmiserver","isAlive");
		} catch ( IOException ioe ) {
		}
	}

	public static DeathThread getInstance() {
		if ( dt==null ) {
			synchronized ( DeathThread.class ) {
				if ( dt==null ) {
					dt = new DeathThread();
					Thread thread = new Thread(dt);
					thread.start();
				}
			}
		}
		return dt;
	}

	public void run() {
		while ( true ) {
			if ( isAlive==null || !isAlive.exists() ) {
				Runtime.getRuntime().exit(0);
			}
			try {
				Thread.sleep(5000);
			} catch ( InterruptedException ie ) {
				continue;
			}
		}
	}
}
