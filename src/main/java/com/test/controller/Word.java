package com.test.controller;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class Word {

	public static class WordCountMap extends Mapper<LongWritable, Text, Text, IntWritable> 
	{
		private final IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException 
		{
			String line = value.toString();
			StringTokenizer token = new StringTokenizer(line);
			while (token.hasMoreTokens()) 
			{
				word.set(token.nextToken());
				context.write(word, one);
			}
		}
	}


	public static class WordCountReduce extends Reducer<Text, IntWritable, Text, IntWritable> 
	{
	
		
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException 
		{
			int sum = 0;
			for (IntWritable val : values) 
			{
				sum += val.get();
			}
			context.write(key, new IntWritable(sum));
		}
	}
	
	
	public static void init(String input, String output) throws Exception
	{
			System.out.println("Word  init ����");
			Configuration conf = new Configuration();
			Job job = new Job(conf);
			job.setJarByClass(Word.class);
			job.setJobName("Word");

			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);

			job.setMapperClass(WordCountMap.class);
			job.setReducerClass(WordCountReduce.class);

			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);

			// �ж�output�ļ����Ƿ���ڣ����������ɾ��
			Path path = new Path(output);//
			FileSystem fileSystem = path.getFileSystem(conf);// ����path�ҵ�����ļ�
			if (fileSystem.exists(path)) {
				fileSystem.delete(path, true);// true����˼�ǣ�����output�ж�����Ҳһ��ɾ��
			}
			
			
		
			FileInputFormat.addInputPath(job, new Path(input));
			FileOutputFormat.setOutputPath(job, new Path(output));

			job.waitForCompletion(true);
		}
}
