/**
 * Simple Friend Recommendation - MR Job 2
 * 
 * Map:
 *   input: <user1>,<user2><TAB><common friends number>
 *   output: key -> <user1> 
 *   		 value -> <user2>,<common friends number>
 *   
 * Reduce:
 *   input: <user1><TAB><list of form <user2>,<number>>
 *   output: <user1>, <list of 10 user2 which has most common friends with user1> 
 * 
 */

package hw1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import com.google.common.collect.MinMaxPriorityQueue;

public class MRJob2 {

	public static class Map2 extends Mapper<LongWritable, Text, Text, Text> {
		static Pattern pattern = Pattern.compile("([0-9]+),([0-9]+)[^0-9]*([0-9]+)");
		
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			
			Matcher m = pattern.matcher(line);
			String user1, user2, count;
			if (m.find()) {
				user1 = m.group(1);
				user2 = m.group(2);
				count = m.group(3);
			} else {
				return;
			}
			
			context.write(new Text(user1), new Text(user2 + "," + count));
		}
	}

	public static class Reduce2 extends Reducer<Text, Text, Text, Text> {

		private static Comparator<String> cmp = new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				String s1 = arg0.split(",")[1];
				String s2 = arg1.split(",")[1];

				return Integer.parseInt(s2) - Integer.parseInt(s1);
			}
		};

		public void reduce(Text key, Iterable<Text> value, Context context)
				throws IOException, InterruptedException {
			/* use a max heap with limited size to sort friend candidates */
			MinMaxPriorityQueue<String> queue = MinMaxPriorityQueue
					.orderedBy(cmp).maximumSize(10).create();
			for (Text val : value) {
				queue.add(val.toString());
			}

			ArrayList<String> recommList = new ArrayList<String>();
			while (!queue.isEmpty()) {
				recommList.add(queue.removeFirst());
			}
			context.write(key, new Text(recommList.toString()));

		}
	}

	public static void runJob(String input, String output) throws Exception {
		Configuration conf2 = new Configuration();

		Job job2 = new Job(conf2, "Simple Friend Recommendation - MR2");
		job2.setJarByClass(MRJob2.class);
		job2.setOutputKeyClass(Text.class);
		job2.setOutputValueClass(Text.class);
		job2.setMapOutputKeyClass(Text.class);
		job2.setMapOutputValueClass(Text.class);

		job2.setMapperClass(Map2.class);
		job2.setReducerClass(Reduce2.class);

		job2.setInputFormatClass(TextInputFormat.class);
		job2.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job2, new Path(input));
		FileOutputFormat.setOutputPath(job2, new Path(output));

		job2.waitForCompletion(true);
	}

	public static void main(String[] args) throws Exception {
		runJob(args[0], args[1]);
	}
}
