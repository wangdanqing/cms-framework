package net.pusuo.cms.client.file;

import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;

import java.io.*;

import java.rmi.*;

import com.hexun.cms.file.FileInterface;

public class ClientFile {

	private static final Log LOG = LogFactory.getLog(ClientFile.class);

	private static ClientFile sf;

	private FileInterface serverfile;

	private String fileinterface = "ServerFile";

	private static Integer lock = new Integer(0);

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	public static ClientFile getInstance() throws Exception {
		if (sf == null) {
			synchronized (lock) {
				if (sf == null)
					sf = new ClientFile();
			}
		}
		return sf;
	}

	private ClientFile() throws Exception {
		serverfile = (FileInterface) com.hexun.cms.client.util.ClientUtil
				.renewRMI(fileinterface);
	}

	public String read(String file) {
		try {
			return serverfile.read(file);
		} catch (RemoteException re) {
			serverfile = (FileInterface) com.hexun.cms.client.util.ClientUtil
					.renewRMI(fileinterface);
			LOG.error(re);
			return null;
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
	}

	public boolean write(String content, String file) {
		return write(content, file, true);
	}

	public boolean write(String content, String file, boolean sync) {
		try {
			if (sync) {
				return serverfile.write(content, file);
			} else {
				return serverfile.write(content, file, false);
			}
		} catch (RemoteException re) {
			serverfile = (FileInterface) com.hexun.cms.client.util.ClientUtil
					.renewRMI(fileinterface);
			LOG.error(re);
			return false;
		} catch (Exception e) {
			LOG.error(e);
			return false;
		}
	}

	public boolean write(byte[] content, String file) {
		return write(content, file, false);
	}

	public boolean write(byte[] content, String file, boolean sync) {
		try {
			if (sync) {
				return serverfile.write(content, file);
			} else {
				return serverfile.write(content, file, false);
			}
		} catch (RemoteException re) {
			serverfile = (FileInterface) com.hexun.cms.client.util.ClientUtil
					.renewRMI(fileinterface);
			LOG.error(re);
			return false;
		} catch (Exception e) {
			LOG.error(e);
			return false;
		}
	}

	public boolean delete(String file) {
		try {
			return serverfile.delete(file);
		} catch (RemoteException re) {
			serverfile = (FileInterface) com.hexun.cms.client.util.ClientUtil
					.renewRMI(fileinterface);
			LOG.error(re);
			return false;
		} catch (Exception e) {
			LOG.error(e);
			return false;
		}
	}

	public String[] getFileList(String dir) {
		try {
			return serverfile.getFileList(dir);
		} catch (RemoteException re) {
			serverfile = (FileInterface) com.hexun.cms.client.util.ClientUtil
					.renewRMI(fileinterface);
			LOG.error(re);
			return null;
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
	}

	public static int copy(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public void write(File src, File dest, boolean rename) throws Exception {
		if (!rename || (rename && !src.renameTo(dest))) {
			BufferedInputStream in = null;
			BufferedOutputStream out = null;
			try {
				in = new BufferedInputStream(new FileInputStream(src));
				out = new BufferedOutputStream(new FileOutputStream(dest));
				copy(in, out);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// ignore
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}
	}

	/**
	 * 
	 * 关闭流,异常会被隐藏起来
	 * 
	 * @param in
	 * 
	 */

	public static void closeStream(InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				// ingore
			}
		}
	}

	/**
	 * 
	 * 关闭流,异常会被隐藏起来
	 * 
	 * @param out
	 * 
	 */

	public static void closeStream(OutputStream out) {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				// ingore
			}
		}
	}

	public void writesync(String File) {
		try {
			serverfile.writesync(File);
		} catch (RemoteException re) {
			serverfile = (FileInterface) com.hexun.cms.client.util.ClientUtil
					.renewRMI(fileinterface);
			LOG.error(re);
		} catch (Exception e) {
			LOG.error(e);
		}
	}

}
