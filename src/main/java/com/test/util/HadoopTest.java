package com.test.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class HadoopTest {
public static String HADOOP_HOST="192.168.0.25";
	
	public static int HADOOP_PORT=9000;
	
	public static String JOBTRACKER=HADOOP_HOST+":"+HADOOP_PORT;
	public static String HDFS_BASE_PATH = "hdfs://"+JOBTRACKER+"/";
	
	public static void uploadInputFile(String localFile,String hdfsPath) throws IOException{
	    Configuration conf = new Configuration();
	    String hdfsInput = HDFS_BASE_PATH+hdfsPath+"/";
	   
	    FileSystem fs = FileSystem.get(URI.create(HDFS_BASE_PATH), conf);
	    fs.copyFromLocalFile(new Path(localFile), new Path(hdfsInput+localFile.substring(localFile.lastIndexOf(File.separator)+1,localFile.length())));
	    fs.close();
	    System.out.println("�Ѿ��ϴ��ļ���input�ļ�����");
	  }
	
	public static void getOutput(String outputfile,String hdfsPath,String fileName) throws IOException{
	    String remoteFile =HDFS_BASE_PATH+hdfsPath+"/"+fileName;
	    Path path = new Path(remoteFile);
	    Configuration conf = new Configuration();
	    FileSystem fs = FileSystem.get(URI.create(HDFS_BASE_PATH),conf);
	    fs.copyToLocalFile(path, new Path(outputfile));
	    System.out.println("�Ѿ�������ļ������������ļ�");
	    fs.close();
	  }
	
	public static void deleteOutput(String hdfsPath,String fileName) throws IOException{
		   Configuration conf = new Configuration();
		   String hdfsOutput =HDFS_BASE_PATH+hdfsPath+"/"+fileName;
		   Path path = new Path(hdfsOutput);
		   FileSystem fs = FileSystem.get(URI.create(HDFS_BASE_PATH), conf);
		   fs.deleteOnExit(path);
		   fs.close();
		   System.out.println("output�ļ��Ѿ�ɾ��");
		 }
	
	public static void uploadFile(String filePath,String hdfsPath)  throws IOException{
		Configuration conf = new Configuration();
        //conf.addResource(new Path("conf/hadoop-default.xml"));
        //conf.addResource(new Path("conf/hadoop-site.xml"));
        //ͨ��conf��ָ��Ҫ������HDFS
        FileSystem hdfs = FileSystem.get(URI.create(HDFS_BASE_PATH),conf);
        //Ҫ�ϴ���Դ�ļ�����·��
        Path src = new Path(filePath);
        //hadoop�ļ�ϵͳ�ĸ�Ŀ¼
        Path dst = new Path(hdfsPath+"/"+filePath.substring(filePath.lastIndexOf(File.separator)+1,filePath.length()));
        //��Դ�ļ�copy��hadoop�ļ�ϵͳ
        hdfs.copyFromLocalFile(src, dst);
        System.out.println("�ϴ��ɹ�");
        FileStatus files[] = hdfs.listStatus(dst);
        for(int i=0;i<files.length;i++)
        {
            System.out.println(files[i].getPath());
        }
	}
	
	
	public static void downloadFile(String localPath,String hdfsPath)  throws IOException{
			String hdfsFilePath=HDFS_BASE_PATH+hdfsPath;
			//�����ļ�
		        //String dest = "hdfs://192.168.0.25:9000/Login4AFilter.java";
		        Configuration conf2 = new Configuration();
		        FileSystem fs = FileSystem.get(URI.create(hdfsFilePath),conf2);
		        FSDataInputStream fsdi = fs.open(new Path(hdfsFilePath));
		        OutputStream output = new FileOutputStream(localPath);
		        IOUtils.copyBytes(fsdi,output,4096,true);
		        System.out.println("�ļ����سɹ�");
			
		        
	}
	
	
	public static void main(String[] args) {
		try {
			String localFile="G:\\Login4AFilter.java";
			String outputfile="G:\\a.txt";
			String hdfsPath="/user/file";
			String hdfsPath2="/user/file3";
			uploadFile(localFile, hdfsPath2);
			downloadFile(outputfile, hdfsPath2+"/Login4AFilter.java");
	        /*************************************************************/
			
	        
		
			uploadInputFile(localFile, hdfsPath);
			//getOutput(outputfile, hdfsPath, "Login4AFilter.java");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
