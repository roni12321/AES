package PacketSender;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * File handling class
 */
public class FileSystem implements Serializable {

	/**
	 * default library
	 */
	private final static String dir = "Uploads";
	
	/**
	 * local path where file is saved
	 */
	private String localPath = "";
	/**
	 * extension of the file
	 */
	private String extenstion = "";
	/**
	 * file codes
	 */
	private int studentId = -1;
	private String examId = "";
	/**
	 * file size
	 */
	private int size = 0;
	/**
	 * file in byte array
	 */
	public byte[] mybytearray;
	/**
	 * if file is empty
	 */
	private boolean fileIsEmpty = false;
	/**
	 * if file is not exists
	 */
	private boolean fileIsNotExists = false;
	
	/**
	 * init the array
	 * @param size size of new array
	 */
	public void initArray(int size) {
		mybytearray = new byte[size];
	}
	/**
	 * Constructor default
	 */
	public FileSystem() 
	{
		this("", -1, "");
	}
	
	public FileSystem(String localPath, int studentId, String examId) 
	{
		this.studentId = studentId;
		this.examId = examId;
		loadFileFromLocal(localPath);
	}
	/**
	 * send file to the server to save
	 * @throws IOException  if failed
	 */
	public void saveFileOnServer() throws IOException
	{
		File theDir = new File(dir);
		// if the directory does not exist, create it
		if (!theDir.exists()) {
		        theDir.mkdir();
		}
		
		// there is no product id linked to the image
		if (studentId == -1 || examId.isEmpty())
			return;
		
		String serverPath = getServerPath();
		// save image to default local
		try (FileOutputStream fos = new FileOutputStream(serverPath)) {
			   fos.write(getMybytearray());
			   fos.close();
			}
	}
	/**
	 * get server path where file is saved
	 * @return path on server
	 */
	public String getServerPath()
	{
		return String.format("%s/Ex_%d_%s.%s", dir, studentId, examId , extenstion);
	}
	/**
	 * load file from local path
	 * @param localPath path from where to load
	 */
	public void loadFileFromLocal(String localPath)
	{
		File newFile = null;
		try
		{
			if (localPath.isEmpty())
			{
				fileIsEmpty = true;
				throw new FileNotFoundException();
			}
			
		    newFile = new File(localPath);
			if (!newFile.exists())
			{
				fileIsNotExists = true;
				throw new FileNotFoundException();
			}
		}
		catch (FileNotFoundException e)
		{
			newFile = null;
		}
		finally
		{
			try
			{
				// blank image is not exists
				if (newFile == null || newFile != null && !newFile.exists())
					throw new FileNotFoundException();
			
				setExtenstion(newFile.getName().split("\\.")[1]);
				byte [] mybytearray  = new byte [(int)newFile.length()];
				FileInputStream fis = new FileInputStream(newFile);
				BufferedInputStream bis = new BufferedInputStream(fis);			  
	      
				initArray(mybytearray.length);
				setSize(mybytearray.length);
	      
				bis.read(getMybytearray(),0,mybytearray.length);
				bis.close();
				
				this.localPath = localPath;
			}
			catch (IOException e) { }
		}
	}
	/**
	 * get file extension
	 * @return extension
	 */
	public String getExtenstion()
	{
		return extenstion; 
	}
	
	/**
	 * update file extension
	 * @param extenstion extension
	 */
	public void setExtenstion(String extenstion)
	{
		this.extenstion = extenstion;
	}
	/**
	 * @return local path
	 */
	public String getLocalFilePath() {
		return localPath;
	}

	public void setLocalFilePath(String localPath) {
		this.localPath = localPath;
	}
	
	public String getExamId() {
		return examId;
	}

	public void setExamId(String examId) {
		this.examId = examId;
	}
	
	public void setStudentId(int studentId)
	{
		this.studentId = studentId;
	}
	
	public int getStudentId()
	{
		return studentId;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public byte[] getMybytearray() {
		return mybytearray;
	}

	public byte getMybytearray(int i) {
		return mybytearray[i];
	}

	public void setMybytearray(byte[] mybytearray) {

		for (int i = 0; i < mybytearray.length; i++)
			this.mybytearray[i] = mybytearray[i];
	}
	
	public boolean getIfFileIsNotExists()
	{
		return fileIsNotExists;
	}
	
	public boolean getIfFileIsEmpty()
	{
		return fileIsEmpty;
	}
	
	public File getImageInstance() throws IOException
	{
		if (fileIsNotExists)
			throw new FileNotFoundException("File For this Exam has been Removed, Please update the File.");
		
		if (fileIsEmpty)
			throw new FileNotFoundException("There is no File Linked to this Exam on the Server, Please Update the File for Re-Linking");
		
	
		Path path = Paths.get(getServerPath());

		Path newPath = java.nio.file.Files.write(path, getMybytearray());
		 
		return newPath.toFile();
	}

}
