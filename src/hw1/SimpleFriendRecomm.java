/**
 * Simple MapReduce algorithm for friend recommendation
 * Based on the number of common friends between 2 non-friend users
 * 
 * Dataset:
 *   http://snap.stanford.edu/class/cs246-data/hw1q1.zip
 * 
 */

package hw1;

public class SimpleFriendRecomm {
	public static void main(String[] args) throws Exception {
		MRJob1.runJob(args[0], args[1]);
		MRJob2.runJob(args[2], args[3]);
	}
}
