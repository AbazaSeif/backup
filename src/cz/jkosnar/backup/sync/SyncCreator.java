package cz.jkosnar.backup.sync;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cz.jkosnar.backup.common.BackupTools;

public class SyncCreator {

	/**
	 * Processes the synchronization and prints information about changes.
	 * 
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public void processSync(File src, File dst) throws IOException {
		System.out.println("Sync Started...\nSource: " + src.getAbsolutePath() + "\nDestination: " + dst.getAbsolutePath() + "\n");

		List<String> deletedStuff = BackupTools.propagateDeletions(src, dst);
		List<String> newStuff = BackupTools.copyNew(src, dst);
		List<String> overwrittenStuff = BackupTools.overwriteFilesWithDifferentSize(src, dst);

		System.out.println("Deleted:");
		for (String string : deletedStuff) {
			System.out.println(" " + string);
		}

		System.out.println("New:");
		for (String string : newStuff) {
			System.out.println(" " + string);
		}

		System.out.println("Overwritten:");
		for (String string : overwrittenStuff) {
			System.out.println(" " + string);
		}

		System.out.println("\nAll OK.");
	}

}
