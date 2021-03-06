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
			System.out.println("Word  init 启动");
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

			// 判断output文件夹是否存在，如果存在则删除
			Path path = new Path(output);//
			FileSystem fileSystem = path.getFileSystem(conf);// 根据path找到这个文件
			if (fileSystem.exists(path)) {
				fileSystem.delete(path, true);// true的意思是，就算output有东西，也一带删除
			}
			
			
		
			FileInputFormat.addInputPath(job, new Path(input));
			FileOutputFormat.setOutputPath(job, new Path(output));

			job.waitForCompletion(true);
		}
}
