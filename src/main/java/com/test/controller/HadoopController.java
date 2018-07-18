package com.test.controller;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapred.JobStatus;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.test.pojo.ResultJson;
import com.test.pojo.User;
import com.test.util.HadoopUtils;



@Controller
// ������ǰ��Ϊ������
@RequestMapping("/hadoop")
// ������ǰ���·��
public class HadoopController {

	@RequestMapping("/upload")
	// ������ǰ������·��
	//�ļ��ϴ�
	public String upload(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		List<String> fileList = (List<String>) request.getSession()
				.getAttribute("fileList");//�õ��û����ϴ��ļ��б�
		if (fileList == null)
			fileList = new ArrayList<String>();//����ļ��б�Ϊ�գ����½�
		User user = (User) request.getSession().getAttribute("user");
		if (user == null)
			return "login";//����û�δ��¼������ת��¼ҳ��
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());//�õ���Spring�����ļ���ע����ļ��ϴ����
		if (multipartResolver.isMultipart(request)) {//����������ļ�����
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

			Iterator<String> iter = multiRequest.getFileNames();//�õ��ļ���������
			while (iter.hasNext()) {
				MultipartFile file = multiRequest.getFile((String) iter.next());
				if (file != null) {
					String fileName =  file.getOriginalFilename();
					File folder = new File(user.getUserName());
					if (!folder.exists()) {
						folder.mkdir();//����ļ���Ŀ¼���ڣ����ڷ��������ش���
					}
					String path = user.getUserName() + "/" + fileName;

					File localFile = new File(path);

					file.transferTo(localFile);//���ϴ��ļ�����������������Ŀ¼
					// fileList.add(path);
				}
				handleUploadFiles(user, fileList);//�����ϴ��ļ�
			}

		}
		request.getSession().setAttribute("fileList", fileList);//���ϴ��ļ��б�����Session��
		return "console";//����console.jsp�����ϴ��ļ�
	}

	@RequestMapping("/wordcount")
	//����Hadoop����mapreduce
	public ResultJson<Map<String,Integer>> wordcount(HttpServletRequest request,
			HttpServletResponse response) {
		ResultJson<Map<String,Integer>> resultJson = new ResultJson<Map<String,Integer>>();
		resultJson.setCode(2);
		resultJson.setMsg("ʧ��");
		Map<String,Integer> tempMap = new HashMap<String,Integer>();
		List<String> strlist= new ArrayList<String>();
		System.out.println("����controller wordcount ");
		User user = (User) request.getSession().getAttribute("user");
		System.out.println(user);
		// if(user == null)
		// return "login";
		Word word = new Word();//�½�����ͳ������
		String username = user.getUserName();
		String input = "hdfs://"+HadoopUtils.JOBTRACKER+"/user/" + username
				+ "/wordcountinput";//ָ��Hadoop�ļ�ϵͳ�������ļ���
		String output = "hdfs://"+HadoopUtils.JOBTRACKER+"/user/" + username
				+ "/wordcountoutput";//ָ��Hadoop�ļ�ϵͳ������ļ���
		String reslt = output + "/part-r-00000";//Ĭ������ļ�
		try {
			Thread.sleep(3*1000);
			word.init(  input,  output );//���õ���ͳ������
			Configuration conf = new Configuration();//�½�Hadoop����
			conf.addResource(new Path("/usr/lib/hadoop/hadoop-2.8.4/etc/hadoop/core-site.xml"));//���Hadoop���ã��ҵ�Hadoop������Ϣ
			conf.addResource(new Path("/usr/lib/hadoop/hadoop-2.8.4/etc/hadoop/hdfs-site.xml"));//Hadoop���ã��ҵ��ļ�ϵͳ

			FileSystem fileSystem = FileSystem.get(conf);//�ô��ļ�ϵͳ
			Path file = new Path(reslt);//�ҵ��������ļ�
			FSDataInputStream inStream = fileSystem.open(file);//��
			URI uri = file.toUri();//�õ�����ļ�·��
			System.out.println(uri);
			String data = null;
			while ((data = inStream.readLine()) != null ) {
				strlist.add(data);
				int bt = data.lastIndexOf('\t');
				tempMap.put(data.substring(0,bt), Integer.parseInt(data.substring(bt+1,data.length())));
				//System.out.println(data.substring(0,bt) + "������" + Integer.parseInt(data.substring(bt+1,data.length())) + "��");
				//response.getOutputStream().println(data);//������ļ�д���û���ҳ
			}
			//response.getOutputStream().println("success.");//������ļ�д���û���ҳ

			inStream.close();
		
			tempMap = sortByValue(tempMap);
		
			Map<String,Integer> resultMap = new HashMap<String,Integer>();
			Set<String> keys = tempMap.keySet();
			int size = 50;
			for(String key : keys)
				{
				if(size==0)
					break;
				resultMap.put(key, tempMap.get(key));
				size--;
				}
			resultJson.setCode(1);
			resultJson.setMsg("�ɹ�");
			resultJson.setData(resultMap);
			
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return resultJson;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());//�Ӵ�С
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }




	@RequestMapping("/wordcountResult")
	//�õ�����ַ���
	public void getResult(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			System.out.println("�յ���ѯ�������:" + new Date());
			String json = (String)request.getSession().getAttribute("json");
			response.getWriter().write(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	@RequestMapping("/MapReduceStates")
	//�õ�MapReduce��״̬
	public void mapreduce(HttpServletRequest request,
			HttpServletResponse response) {
		float[] progress=new float[2];
		try {
			Configuration conf1=new Configuration();
			conf1.set("mapred.job.tracker", HadoopUtils.JOBTRACKER);
			
			JobStatus jobStatus  = HadoopUtils.getJobStatus(conf1);
//			while(!jobStatus.isJobComplete()){
//				progress = Utils.getMapReduceProgess(jobStatus);
//				response.getOutputStream().println("map:" + progress[0]  + "reduce:" + progress[1]);
//				Thread.sleep(1000);
//			}
			JobConf jc = new JobConf(conf1);
			
			JobClient jobClient = new JobClient(jc);
			JobStatus[] jobsStatus = jobClient.getAllJobs();  
			//�����͵õ���һ��JobStatus���飬���ȡ��һ��Ԫ��ȡ����jobStatus  
			jobStatus = jobsStatus[0];  
			JobID jobID = jobStatus.getJobID(); //ͨ��JobStatus��ȡJobID  
			RunningJob runningJob = jobClient.getJob(jobID);  //ͨ��JobID�õ�RunningJob����  
			runningJob.getJobState();//���Ի�ȡ��ҵ״̬��״̬�����֣�ΪJobStatus.Failed ��JobStatus.KILLED��JobStatus.PREP��JobStatus.RUNNING��JobStatus.SUCCEEDED  
			jobStatus.getUsername();//���Ի�ȡ������ҵ���û�����  
			runningJob.getJobName();//���Ի�ȡ��ҵ����  
			jobStatus.getStartTime();//���Ի�ȡ��ҵ�Ŀ�ʼʱ�䣬ΪUTC��������  
			float map = runningJob.mapProgress();//���Ի�ȡMap�׶���ɵı�����0~1��  
			System.out.println("map=" + map);
			float reduce = runningJob.reduceProgress();//���Ի�ȡReduce�׶���ɵı�����
			System.out.println("reduce="+reduce);
			runningJob.getFailureInfo();//���Ի�ȡʧ����Ϣ��  
			runningJob.getCounters();//���Ի�ȡ��ҵ��صļ������������������ݺ���ҵ���ҳ���Ͽ����ļ�������ֵһ���� 
			
			
		} catch (IOException e) {
			progress[0] = 0;
			progress[1] = 0;
		}
	
		request.getSession().setAttribute("map", progress[0]);
		request.getSession().setAttribute("reduce", progress[1]);
	}
	
	//�����ļ��ϴ�
	public void handleUploadFiles(User user, List<String> fileList) {
		File folder = new File("/home/linux/test/"
				+ user.getUserName());
		if (!folder.exists())
			return;
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File file : files) {
				System.out.println(file.getName());
				try {
					putFileToHadoopFSFolder(user, file, fileList);//�������ļ��ϴ���Hadoop�ļ�ϵͳ
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

	//�������ļ��ϴ���Hadoop�ļ�ϵͳ
	private void putFileToHadoopFSFolder(User user, File file,
			List<String> fileList) throws IOException {
		Configuration conf = new Configuration();
		conf.addResource(new Path("/usr/lib/hadoop/hadoop-2.8.4/etc/hadoop/core-site.xml"));
		conf.addResource(new Path("/usr/lib/hadoop/hadoop-2.8.4/etc/hadoop/hdfs-site.xml"));

		FileSystem fileSystem = FileSystem.get(conf);
		System.out.println(fileSystem.getUri());

		Path localFile = new Path(file.getAbsolutePath());
		Path foler = new Path("/user/" + user.getUserName()
				+ "/wordcountinput");
		if (!fileSystem.exists(foler)) {
			fileSystem.mkdirs(foler);
		}
		
		Path hadoopFile = new Path("/user/" + user.getUserName()
				+ "/wordcountinput/" + file.getName());
//		if (fileSystem.exists(hadoopFile)) {
//			System.out.println("File exists.");
//		} else {
//			fileSystem.mkdirs(hadoopFile);
//		}
		fileSystem.copyFromLocalFile(true, true, localFile, hadoopFile);
		fileList.add(hadoopFile.toUri().toString());

	}

	
		
}