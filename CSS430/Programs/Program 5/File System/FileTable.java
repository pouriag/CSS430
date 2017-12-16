/**
 *
 * Created by lisakim and pouriaG on 12/1/17.
 *
 */

import java.util.Vector;

public class FileTable {
	private final static short UNUSED = 0;
	private final static short USED = 1;
	private final static short READ = 2;
	private final static short WRITE = 3;

	// table is a lookup to see if a thread has opened a file and thus exists in the table
	Vector<FileTableEntry> table;         // the actual entity of this file table
	Directory dir;        				// the root directory

	/**
	 * Constructor, instantiate a file structure table
	 * receive reference to a Directory from the File system
	 * @param directory
	 */
	public FileTable( Directory directory ) {
		table = new Vector<>();
		dir = directory;
	}

	/**
	 * This method creates a new FileTableEntry object in memory
	 * for files that have not been opened yet and needs to be opened
	 *
	 * @param filename to create
	 * @param mode file is to be read or written to
	 * @return FileTableEntry object, or null if file is marked for deletion, or it doesn't exist for read
	 */
	public synchronized FileTableEntry falloc( String filename, String mode ) {
		// allocate a new file (structure) table entry for this file name
		// allocate/retrieve and register the corresponding inode using dir
		// increment this inode's count
		// immediately write back this inode to the disk
		// return a reference to this file (structure) table entry object
		short iNumber = -1;
		Inode inode = null;

		while (true) {
			// if filename is root, assign 0, else grab the inumber corresponding to filename
			iNumber = (filename.equals("/") ? 0 : dir.namei(filename));

			if (iNumber >= 0) {  // if iNumber exists in disk, grab it and set it to an iNode
				inode = new Inode(iNumber);

				if (mode.equals("r")) {
					if (inode.flag == READ) {
						inode.flag = READ;
						break; // no need to wait, we can have multiple threads reading same file
					} else if (inode.flag == WRITE) {
						try {
							wait();  // the calling thread will ahve to wait until other thread finishes writing
						} catch (InterruptedException e) {
						}
					}
					else {
						inode.flag = READ;
						break;  // no threads are using this inode in any mode atm
					}
				} else if (mode.equals("w") || mode.equals("w+") || mode.equals("a")) {
					if (inode.flag == READ || inode.flag == WRITE) {  // can't write to files that are being read or written to
						try {
							wait();
						} catch (InterruptedException e) {
						}
					}
					else {
						inode.flag = WRITE;
						break;
					}
				}
			} // end of iNumber >= 0
			// iNumber was negative, means blank inode, mode can't be read, b/c why read an empty file
			// so we set the blank inode flag to "write" b/c "write, write+, append" are essentially the same
			else if (mode.equals("w") || mode.equals("w+") || mode.equals("a")) {
				inode = new Inode();
				iNumber = dir.ialloc(filename);  // adding this to directory
				//SysLib.cout("AFTER CREATING = " + filename +" : " + dir.namei("css430") + "\n");
				inode.flag = WRITE;
				break;
			}
			else {
				break;
			}
		} // end of while loop

		if(inode != null) {
			inode.count++;
			inode.toDisk(iNumber);
			// create a table entry and register it
			FileTableEntry entry = new FileTableEntry(inode, iNumber, mode);
			this.table.addElement(entry);
			return entry;
		}

		return null;
	}

	/**
	 * This method syncs the inode stored in the FileTableEntry to disk for any changes
	 * then removes the FileTableEntry object from the table (but not the actual inode on disk)
	 *
	 * (Only remove when the last thread is finished reading and writing from/to a file)
	 *
	 * @param entry filetableentry object
	 * @return true if file table entry found, thus deleted, else false if not found
	 */
	public synchronized boolean ffree( FileTableEntry entry ) {
		// receive a file table entry reference
		// save the corresponding inode to the disk (sync to disk)
		// free this file table entry.
		// return true if this file table entry found in my table
		if(entry.count != 0) {
			notifyAll();
		}

		entry.inode.toDisk(entry.iNumber);
		return this.table.remove(entry);
	}

	/**
	 * Should be called before starting a format
	 *
	 * @return true if our fileTableEntry vector table is empty, false otherwise
	 */
	public synchronized boolean fempty( ) {
		return table.isEmpty( );
	}
}
