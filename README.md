# Backup
Simple sync and backup tool I use for encrypted cloud backups.

## Prerequisites
This tool needs:
* Java 1.7 and higher
* 7zip - version 9.20
* (optional) Cloud sync software (such as Google Drive for desktop)

## What it does?
"Zipper" is a very simple script (command line tool) written in Java that can be used for automated, compressed and encrypted (cloud) backup.

1. Zipper recursively "7zips" your source directory. It traverses the files to a given depth and creates a separate archive for each branch with all sub-directories included.
2. Resulting files are created in a selected output directory. It can be, for example, your Google Drive local folder.
3. If "sync" parameter is used, it will only update changed files next time it is run at the same set of directories.

## No cloud?
If you do not need the "cloud" backup you can just use the "Zipper" tool to keep encrypted backup of your files in a local or network filesystem.

## Use it at your own risk.
This is a very simple tool I use for my personal non-critical backups. I cannot guarantee it will work for you. Please check the backup files very carefully during the backups.

## How to build it?
Currently you are asked to build the .jar yourself:
* "cz.jkosnar.backup.zip.Zipper" is the Main class. 
* Easiest way is to Extract required libraries to the resulting .jar.
	
## How to use it?
If the program is run with no arguments, the help is printed:

```
Zipper: Recursive 7zip based archiving tool. Creates non-solid password protected archives. 
Parameters: {7zipLocation} {source} {destination} {depth} {archivePass} {synchronize} {workingDir} {compression}

{7zipLocation}              7zip executable (7z.exe)
{source} & {destination}:   source and destination folders
{depth}:                    folder tree recursing depth - folders lower than {depth} will be merged into one archive
{archivePass}:              password for the resulting archives
{synchronize}:              true - update existing destination, false - delete destination first
{workingDir}:               7zip temporary directory location
{compression}:              compression: 0-9 - 0 no compression

All parameters must by specified in the given order.
```

You can prepare a batch script for your backup, such as:

```
set /p PASS= Enter Password: 
java -cp Backup.jar cz.jkosnar.backup.zip.Zipper "C:\Program Files\7-Zip\7z.exe" "-SOURCE-" "-DESTINATION-" 4 %PASS% true "C:\Temp\DriveDuplicateTemp" 9
timeout 500
```

## Advanced Modes?
The Backup project actually contains two main classes:
* java -cp Backup.jar cz.jkosnar.backup.zip.Zipper
* java -cp Backup.jar cz.jkosnar.backup.sync.Sync

This allows you to use "non-timestmap" based backup approach. In this case you have to:
* Turn off the synchronize parameter in Zipper. (It will therefore always start from scratch.)
* Set Zzipper destination to same intermediate folder.
* Use "Sync" tool to synchronize the "intermediate folder" to your "final folder" based on file-sizes only.

Sync:
```
Sync: One purpose file-size-based synchronization tool. 
Sync ignores timestamps rather using file size as the only parameter. 
If your data can change without changing the actual file sizes this tool is not for  you.
params: {source} {destination}
```





