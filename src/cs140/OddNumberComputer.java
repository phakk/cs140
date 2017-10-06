package cs140;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OddNumberComputer {

	public static void main(String[] args){
		Scanner stdin = new Scanner(System.in);

		System.out.print("What is n? > ");
		int n = stdin.nextInt();
		System.out.print("How many workers? > ");
		int w = stdin.nextInt();

		int share = (n * 2) / w; // share of work per worker
		int excess = (n * 2) % w; // remainder work if worker does not divide n
									// exactly
		Sum result = new Sum();

		ExecutorService executorService = Executors.newFixedThreadPool(w);//parallel run of threads
		int start = 1;
		int end;
		for (int s = 1; s <= w; s++) {
			end = ((start + share) - 1);
			if (excess > 0) {
				end += 1; // add additional work
				excess--; // decrease excess
			}

			Worker worker = new Worker("Thread " + s, start, end, result);
			executorService.execute(worker);

			start = end + 1;
		}

		executorService.shutdown();
		while (!executorService.isTerminated()) {
		}
		System.out.println("Thread main: SUM is " + result.getValue());
		stdin.close();
	}
}

class Worker implements Runnable {
	String name;
	int start;
	int end;
	int sum = 0;
	Sum s;

	public Worker(String name, int start, int end, Sum s) {
		this.name = name;
		this.start = start;
		this.end = end;
		this.s = s;
	}

	@Override
	public void run() {
		System.out.println(name + ": Computing for sum of odd numbers in range [" + start + "," + end + "]");
		// make sure to always start with odd
		start = start % 2 == 0 ? start + 1 : start;
		// solution that does not iterate from start to end
		for (int x = start; x <= end; x += 2) {
			sum += x;
		}
		s.add(sum);
		System.out.println(name + ": Sum is " + sum);
	}
}

class Sum {
	int value = 0;

	public Sum() {
	}

	public void add(int s) {
		synchronized (this) {//to ensure that only 1 thread modify the value at a time
			this.value += s;
		}
	}

	public int getValue() {
		return value;
	}
}
