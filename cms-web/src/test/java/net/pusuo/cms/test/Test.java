package net.pusuo.cms.test;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by sjk on 20/7/14.
 */
public class Test {
	public static void main(String[] args) throws IOException {
		File l = new File("/Users/sjk/workspace/github/cms-framework/cms-web/target/lib");
		File[] files = l.listFiles();
		for (File f : files) {
			JarFile jar = new JarFile(f);
			Enumeration<JarEntry> e = jar.entries();
			while (e.hasMoreElements()) {
				JarEntry entry = e.nextElement();
				String path = entry.getName();
				if (path.contains("org/logicalcobwebs")) {
					System.out.println(jar.getName()+"||"+path);
				}
			}
		}
	}
}
