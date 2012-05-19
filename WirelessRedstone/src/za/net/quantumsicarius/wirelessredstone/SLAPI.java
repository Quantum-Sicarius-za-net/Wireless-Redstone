package za.net.quantumsicarius.wirelessredstone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/** SLAPI = Saving/Loading API
 * API for Saving and Loading Objects.
 */
public class SLAPI
{
	public static void save(Object obj,File string) throws Exception
	{
		if (!string.exists()) {
			if (!string.getParentFile().exists()) {
				string.getParentFile().mkdirs();
			}
			string.createNewFile();
		}
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(string));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}
	public static Object load(String path) throws Exception
	{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
		Object result = ois.readObject();
		ois.close();
		return result;
	}
}


