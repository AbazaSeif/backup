package cz.jkosnar.backup.sync;

import java.io.File;

import org.apache.commons.io.FileUtils;

/**
 * Sync: One purpose file-size-based synchronization tool. <br/>
 * Sync ignores timestamps rather using file size as the only parameter. If your
 * data can change without changing the actual file sizes this tool is not for
 * you. <br/>
 * <br/>
 * It does following:
 * <ul>
 * <li>Propagate deletions (delete files no longer present)</li>
 * <li>Copies new stuff (copies new files)</li>
 * <li>Overwrites existing files that has different file size.</li>
 * </ul>
 */
public class Sync {

	private static File source = new File("");
	private static File destination = new File("");

	public static void main(String[] args) throws Exception {

		if (args.length != 2) {
			System.out.println(
					"Sync: One purpose file-size-based synchronization tool. Sync ignores timestamps rather using file size as the only parameter. If your data can change without changing the actual file sizes this tool is not for  you.");
			System.out.println("params: {source} {destination}");
			return;
		}

		source = new File(args[0]);
		destination = new File(args[1]);

		if (!destination.exists()) {
			FileUtils.forceMkdir(destination);
		}

		SyncCreator s = new SyncCreator();
		s.processSync(source, destination);

	}

}
