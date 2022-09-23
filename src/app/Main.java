package app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import entities.Merger;
import entities.Worker;

public class Main {

	public static void main(String[] args) {
		
		String rootDataPath = "./data";
		
		File aux = new File(rootDataPath);
		if(!aux.exists()) {
			aux.mkdir();
		}
		
		List<Semaphore> semaphoreList = new ArrayList<>();
		Semaphore fileListSemaphore = new Semaphore(1);
		semaphoreList.add(new Semaphore(0));
		semaphoreList.add(new Semaphore(0));
		semaphoreList.add(new Semaphore(0));
		semaphoreList.add(new Semaphore(0));
		
		List<File> fileList = new ArrayList<>();

		Worker worker1 = new Worker(0, fileList, fileListSemaphore, semaphoreList, rootDataPath);
		Worker worker2 = new Worker(1, fileList, fileListSemaphore, semaphoreList, rootDataPath);
		Worker worker3 = new Worker(2, fileList, fileListSemaphore, semaphoreList, rootDataPath);
		Worker worker4 = new Worker(3, fileList, fileListSemaphore, semaphoreList, rootDataPath);
		Merger merger = new Merger(fileList, fileListSemaphore, semaphoreList, rootDataPath);
		
		new Thread(worker1).start();
		new Thread(worker2).start();
		new Thread(worker3).start();
		new Thread(worker4).start();
		new Thread(merger).start();
		
	}

}
