import org.apache.commons.math3.distribution.NormalDistribution;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Chen on 2017/6/19.
 */
public class DataUtil {

    private static final String TEST_DATA_FILE = "data/test_data.txt";
    private static final String TRAIN_DATA_FILE = "data/train_data.txt";
    private static final String SUBMISSION_FILE = "data/submission.txt";
    private static FileReader fileReader = null;
    private static FileInputStream inputStream = null;
    private static Scanner scanner = null;
    private static BufferedReader reader = null;
    private static FileWriter writer = null;
    private static double[] mean = null;
    private static double[] deviation = null;

    private static List<TrainPoint> readTrainData() {
        List<TrainPoint> trainPoints = null;
        try {
            fileReader = new FileReader(new File(TRAIN_DATA_FILE));
            reader = new BufferedReader(fileReader);
            trainPoints = new ArrayList<>();
            String line = null;
            String[] lineData;
            String[] indexAndFeature;
            int i;
            int index;
            double feature;
            System.out.println("-----------开始读取训练数据-----------");
            int count = 0;
            while ((line = reader.readLine()) != null) {
                count++;
                lineData = line.split(" ");
                line = null;
                TrainPoint trainPoint = new TrainPoint();
                double label = Double.parseDouble(lineData[0]);
                trainPoint.setLabel(label);
                for (i = 1; i < lineData.length; i++) {
                    indexAndFeature = lineData[i].split(":");
                    lineData[i] = null;
                    index = Integer.parseInt(indexAndFeature[0]);
                    feature = Double.parseDouble(indexAndFeature[1]);
                    trainPoint.getFeatures()[index] = feature;
                }

                trainPoints.add(trainPoint);
                if (count >= 10000) {
                    break;
                }
            }
            System.out.println("-----------读取训练数据结束,size:"+trainPoints.size()+"---------------------");
        } catch (Exception e) {
            System.out.println("找不到文件");
            e.printStackTrace();
        } finally {
            handlerInputStream();
        }
        return trainPoints;
    }



    private static List<TestPoint> readTestData() {
        List<TestPoint> testPoints = null;
        try {
            inputStream = new FileInputStream(TEST_DATA_FILE);
            scanner = new Scanner(inputStream);
            testPoints = new ArrayList<>();
            System.out.println("-----------开始读取测试数据-----------");
            for(int i = 0; i <= 282795; i++) {
                testPoints.add(new TestPoint());
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] lineData = line.split(" ");
                int id = Integer.parseInt(lineData[0]);
                testPoints.get(id).setId(id);
                for (int i = 1; i < lineData.length; i++) {
                    String[] indexAndFeature = lineData[i].split(":");
                    int index = Integer.parseInt(indexAndFeature[0]);
                    double feature = Double.parseDouble(indexAndFeature[1]);
                    testPoints.get(id).getFeatures()[index] = feature;
                }
            }
            System.out.println("-----------读取测试数据结束-----------");
        } catch (Exception e) {
            System.out.println("文件读取出错");
            e.printStackTrace();
        } finally {
            handlerInputStream();
        }
        return testPoints;
    }

    public static List<TestPoint> getTestPoint() {
        List<TestPoint> testPoints = readTestData();
        return processTestPoint(testPoints);
    }

    private static List<TestPoint> processTestPoint(List<TestPoint> testPoints) {
        for(int i = 0; i < 133; i++) {
            if (deviation[i] == 0.0)
                continue;
            NormalDistribution normalDistribution = new NormalDistribution(mean[i], deviation[i]);
            for (TestPoint testPoint : testPoints) {
                testPoint.getFeatures()[i] = normalDistribution.cumulativeProbability(testPoint.getFeatures()[i]);
            }
        }
        return testPoints;
    }

    public static void writeSubmission(List<TestPoint> testPoints) {
        try {
            writer = new FileWriter(SUBMISSION_FILE, false);
            writer.write("id,label");
            for (int i = 0; i < testPoints.size(); i++) {
                writer.write("\n");
                writer.write(testPoints.get(i).getId()+","+testPoints.get(i).getLabel());
            }
        } catch (IOException e) {
            System.out.println("写入数据失败");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.out.println("关闭文件出错");
                    e.printStackTrace();
                }
            }
        }
    }

    private static void calculateMeanAndDeviation(List<TrainPoint> trainPoints) {
        mean = new double[202];
        deviation = new double[202];
        for (int i = 0; i < 202; i++) {
            double sum = 0.0;
            for (TrainPoint trainPoint : trainPoints) {
                sum += trainPoint.getFeatures()[i];
            }
            mean[i] = sum/trainPoints.size();
            double deviationSum = 0.0;
            for (TrainPoint trainPoint : trainPoints) {
                deviationSum += Math.pow(trainPoint.getFeatures()[i]-mean[i], 2);
            }
            deviation[i] = Math.sqrt(deviationSum/trainPoints.size());
        }
    }

    public static List<TrainPoint> getTrainPoints() {
        List<TrainPoint> trainPoints = readTrainData();
        calculateMeanAndDeviation(trainPoints);
        return processTrainPoints(trainPoints);
    }


    public static List<TrainPoint> processTrainPoints(List<TrainPoint> trainPoints) {
        for (int i = 0; i < 202; i++) {
            if (deviation[i] == 0.0)
                continue;
            NormalDistribution normalDistribution = new NormalDistribution(mean[i], deviation[i]);
            for (TrainPoint trainPoint : trainPoints) {
                trainPoint.getFeatures()[i] = normalDistribution.cumulativeProbability(trainPoint.getFeatures()[i]);
            }
        }
        return trainPoints;
    }

    private static void handlerInputStream() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                System.out.println("关闭文件失败");
                e.printStackTrace();
            }
        }
    }
}
