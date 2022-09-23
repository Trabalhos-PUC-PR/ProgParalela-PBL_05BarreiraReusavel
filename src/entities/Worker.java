package entities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Worker implements Runnable {

	private int id;
	private List<File> fileList;
	private Semaphore fileListSemaphore;
	private List<Semaphore> semaphoreList;
	private String rootDataPath;

	public Worker(int id, List<File> fileList, Semaphore fileListSemaphore, List<Semaphore> semaphoreList, String rootDataPath) {
		this.id = id;
		this.fileList = fileList;
		this.rootDataPath = rootDataPath;
		this.semaphoreList = semaphoreList;
		this.fileListSemaphore = fileListSemaphore;
	}

	public static File fileGenerator(String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			} else {
				if (file.isDirectory()) {
					throw new RuntimeException("File already exists as dir!");
				}
			}

			long iterator = 0;
			Random rng = new Random();
			List<Long> numberList = new ArrayList<>();
			while (iterator < 1000000) {
				numberList.add(rng.nextLong(10000001));
				iterator++;
			}       
                 
			Collections.sort(numberList);
                 
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
				for (Long value : numberList) {
					bw.write(value + "\n");
				}
			}    
			return file;
                 
		} catch (IOException e) {
			e.printStackTrace();
		}        
		return null;
	}

	@Override
	public void run() {
		int cont = 0;
			while (true) {
				try {
					fileListSemaphore.acquire();
					fileList.add(fileGenerator(rootDataPath+"/"+"W"+id+"-"+cont));
					fileListSemaphore.release();
					semaphoreList.get(id).release();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				cont++;
			}
	}

}
