package com.veritasware.neto.test;

import java.io.*;

public class LineCounter {
    private void run(String directory, String extension) {
        File dir = new File(directory);
        if (dir.exists()) {
            int lineSum = this.processDirectory(dir, extension);

            System.out.println(lineSum);
        }
        else {
            System.err.printf("%s 디렉토리는 존재하지 않습니다.", directory);
        }
    }

    private int processDirectory(File directory, String extension) {
        File[] files = directory.listFiles();

        int lineCount = 0;

        for (File file : files) {
            // 디렉토리인 경우. 재귀호출.
            if (file.isDirectory()) {
                lineCount += this.processDirectory(file, extension);
            }

            // 일반 파일인 경우.
            else {
                lineCount += this.countLines(file);
            }
        }

        return lineCount;
    }

    private int countLines(File file) {
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            int count = 0;
            while (bufferedReader.readLine() != null) count++;

            return count;
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return 0;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return 0;
        }
        finally {
            try {
                if (bufferedReader != null) bufferedReader.close();
            }
            catch (IOException ex2) {}
        }
    }
    public static void main(String[] args) {

        LineCounter lineCounter = new LineCounter();
//        lineCounter.run("F:\\workspace\\totorosa\\project\\ToToBrowser\\Source", "");
        lineCounter.run("F:\\workspace\\totorosa\\project\\ToToFileServer_LinuxC\\src", "");
    }
}