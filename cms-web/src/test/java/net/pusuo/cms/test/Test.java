package net.pusuo.cms.test;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.Map;

/**
 * sf
 * Created by sjk on 20/7/14.
 */
public class Test {
	public static void main(String[] args) throws IOException {
		Map<Integer, Integer> map = Maps.newTreeMap();
		map.put(1, 11);
		map.put(2, 22222);
		map.put(1, 11);
		map.put(33, 333);

		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}


//		File l = new File("/Users/sjk/workspace/github/cms-framework/cms-web/target/lib");
//		File[] files = l.listFiles();
//		for (File f : files) {
//			JarFile jar = new JarFile(f);
//			Enumeration<JarEntry> e = jar.entries();
//			while (e.hasMoreElements()) {
//				JarEntry entry = e.nextElement();
//				String path = entry.getName();
//				if (path.contains("org/logicalcobwebs")) {
//					System.out.println(jar.getName()+"||"+path);
//				}
//			}
//		}
	}
}
