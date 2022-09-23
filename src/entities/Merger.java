package entities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class Merger implements Runnable {

	private List<File> fileList;
	private String rootDataPath;
	private Semaphore fileListSemaphore;
	private List<Semaphore> semaphoreList;

	public Merger(List<File> fileList, Semaphore fileListSemaphore, List<Semaphore> semaphoreList, String rootDataPath) {
		this.fileList = fileList;
		this.rootDataPath = rootDataPath;
		this.semaphoreList = semaphoreList;
		this.fileListSemaphore = fileListSemaphore;
	}

	public void rendezvous() {
		for (Semaphore s : semaphoreList) {
			try {
				s.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		int fileCount = 0;
		while (true) {
			rendezvous();
			long startTime = System.currentTimeMillis();
			System.out.printf("Merging File %d!\n", fileCount);
			try {
				fileListSemaphore.acquire();
				List<File> aux = new ArrayList<>();
				for (int i = 0; i < 4; i++) {
					aux.add(fileList.get(0));
					fileList.remove(0);
				}
				fileListSemaphore.release();
				Set<Long> numberSet = new HashSet<>();

				for (File file : aux) {
					try (BufferedReader br = new BufferedReader(new FileReader(file))) {
						String line = br.readLine();
						while (br.readLine() != null) {
							numberSet.add(Long.parseLong(line));
							line = br.readLine();
						}
						file.delete();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				List<Long> numberList = new ArrayList<>(numberSet);
				Collections.sort(numberList);

				try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(rootDataPath+"/M"+fileCount)))) {
					for (Long value : numberList) {
						bw.write(value + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				long endTime = System.currentTimeMillis();
				int elapsedMillis = (int) (endTime - startTime);
				int elapsedSeconds = elapsedMillis / 1000;
				elapsedMillis = elapsedMillis % 1000;
				System.out.printf("Merged File %d! (%d.%ds elapsed)\n", fileCount, elapsedSeconds, elapsedMillis);
				fileCount++;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
