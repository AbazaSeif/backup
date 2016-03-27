package cz.jkosnar.backup.zip;

import java.io.File;

import org.apache.commons.io.FileUtils;

/**
 * Zipper: Recursive 7zip based archiving tool. Creates non-solid password
 * protected archives.
 */
public class Zipper {

	private static String sevenZipLocation = "c:\\Program Files\\7-Zip\\7z.exe";
	private static File source = new File("");
	private static File destination = new File("");
	private static int depth = 4;
	private static String archivePass = "_defaultPassword";

	private static boolean reuse = false;
	private static String workingDir = "";
	private static int compression = 0;

	public static void main(String[] args) throws Exception {

		if (!(args.length == 5 || args.length == 8)) {
			System.out.println("Zipper: Recursive 7zip based archiving tool. Creates non-solid password protected archives. ");
			System.out.println("Mandatory parameters: {7zipLocation} {source} {destination} {depth} {archivePass} ");
			System.out.println();
			System.out.println("{7zipLocation}              7zip executable (7z.exe)");
			System.out.println("{source} & {destination}:   source and destination folders");
			System.out.println("{depth}:                    folder tree recursing depth - folders lower than {depth} will be merged into one archive");
			System.out.println("{archivePass}:              password for the resulting archives");
			System.out.println();
			System.out.println("Additional parameters: {reuse} {workingDir} {compression} ");
			System.out.println("{reuse}:                    update the destination folder - archives will be synchronized (true/false)");
			System.out.println("{workingDir}:               temporary directory location");
			System.out.println("{compression}:              compression: 0-9 - 0 no compression");
			System.out.println();
			System.out.println("Specify all mandatory parameters plus optionally all additional ones.");
			return;
		}

		sevenZipLocation = args[0];
		source = new File(args[1]);
		destination = new File(args[2]);
		depth = new Integer(args[3]).intValue();
		archivePass = args[4];

		// additional parameters
		if (args.length == 8) {
			reuse = new Boolean(args[5]).booleanValue();
			workingDir = args[6];
			compression = new Integer(args[7]).intValue();
		}

		// check parameters
		if (!source.exists()) {
			System.out.println("Source file does not exist...");
			return;
		}

		if (!destination.exists()) {
			FileUtils.forceMkdir(destination);
		}

		File workingDirFile = new File(workingDir);
		if (!"".equals(workingDir) && !workingDirFile.exists()) {
			FileUtils.forceMkdir(workingDirFile);
		}

		ZipCreator z = new ZipCreator();
		z.createZip(sevenZipLocation, source, destination, depth, archivePass, reuse, workingDir, compression);
	}

}
