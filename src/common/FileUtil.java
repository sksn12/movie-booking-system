package common;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {

	private FileUtil() {
	}

	public static List<String> readLines(String filePath) {
		List<String> lines = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {

			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				lines.add(line);
			}

		} catch (FileNotFoundException e) {
			System.out.println("[FileUtil] 파일 없음 (빈 리스트 반환): " + filePath);
		} catch (IOException e) {
			throw new RuntimeException("[FileUtil] 파일 읽기 오류: " + filePath, e);
		}

		return lines;
	}

	public static void writeLines(String filePath, List<String> lines) {
		File file = new File(filePath);
		if (file.getParentFile() != null) {
			file.getParentFile().mkdirs();
		}

		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(filePath, false), "UTF-8"))) {

			for (String line : lines) {
				writer.write(line);
				writer.newLine();
			}

		} catch (IOException e) {
			throw new RuntimeException("[FileUtil] 파일 쓰기 오류: " + filePath, e);
		}
	}

	public static void appendLine(String filePath, String line) {
		File file = new File(filePath);
		if (file.getParentFile() != null) {
			file.getParentFile().mkdirs();
		}

		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(filePath, true), "UTF-8"))) {

			writer.write(line);
			writer.newLine();

		} catch (IOException e) {
			throw new RuntimeException("[FileUtil] 파일 append 오류: " + filePath, e);
		}
	}

	public static Map<String, List<String>> readLinesFromDirectory(String directoryPath) {
		Map<String, List<String>> movieMap = new HashMap<>();
		File directory = new File(directoryPath);
		if (!directory.exists() || !directory.isDirectory()) {
			return movieMap;
		}
		File[] files = directory.listFiles();
		if (files == null) {
			return movieMap;
		}
		for (File file : files) {
			if (file.isFile()) {
				String date = file.getName().replace(FilePath.MOVIE_FILE_PREFIX, "").replace(".txt", "");
				movieMap.put(date, readLines(file.getAbsolutePath()));
			}
		}
		return movieMap;
	}
}
