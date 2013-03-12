/**
 * Simple Friend Recommendation - MR Job 1
 * 
 * Map:
 *   input: <user><TAB><friend list>
 *   output: key -> <user1>,<user2> 
 *   		 value -> 1 if u1 and u2 not friend, 0 else 
 * 
 * Reduce:
 *   input: <user1>,<user2><TAB><list of 1 and 0>
 *   output: key -> <user1>,<user2> 
 *     		 value -> sum of 1s
 *           no output if value list contains 0 (means already friends)
 * 
 */

package hw1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
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

public class MRJob1 {
	
	public static class Map1 extends
	Mapper<LongWritable, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private final static IntWritable zero = new IntWritable(0);
		
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			
			int index = line.indexOf('\t');
			String userId = line.substring(0, index);
			List<String> friendList = new ArrayList<String>();
			StringTokenizer tokenizer = new StringTokenizer(line.substring(index), ",");
			while (tokenizer.hasMoreTokens()) {
				friendList.add(tokenizer.nextToken());
			}
			
			int len = friendList.size();
			int i, j;
			
			/* generate friend pairs */
			for (i = 0; i < len; i++) {
				context.write(new Text(userId+","+friendList.get(i)), zero);
			}
			/* generate pairs with common friend */
			for (i = 0; i < len; i++) {
				for (j = 0; j < len; j++) {
					if (i != j)
						context.write(new Text(friendList.get(i)+","+friendList.get(j)), one);
				}
			}
		}
	}

	public static class Reduce1 extends
	Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			int prod = 1;
			for (IntWritable val : values) {
				sum += val.get();
				prod *= val.get();
			}
			if (prod != 0)	// ignore pairs that are already friends
				context.write(key, new IntWritable(sum));
		}
	}
	
	public static void runJob(String input, String output) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "Simple Friend Recommendation - MR1");
		job.setJarByClass(MRJob1.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(IntWritable.class);
	    
		job.setMapperClass(Map1.class);
		job.setReducerClass(Reduce1.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		job.waitForCompletion(true);	
	}
	
	public static void main(String[] args) throws Exception {
		runJob(args[0], args[1]);
	}
}
