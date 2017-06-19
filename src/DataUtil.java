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

    public static List<TrainPoint> readTrainData() {
        List<TrainPoint> trainPoints = null;
        try {
//            inputStream = new FileInputStream(TRAIN_DATA_FILE);
//            scanner = new Scanner(inputStream);
            fileReader = new FileReader(new File(TRAIN_DATA_FILE));
            reader = new BufferedReader(fileReader);
            trainPoints = new LinkedList<>();
            String line = null;
            String[] lineData;
            String[] indexAndFeature;
            int i;
            int index;
            double feature;
            System.out.println("-----------开始读取训练数据-----------");
            while ((line = reader.readLine()) != null) {
//                line = scanner.nextLine();
                System.out.println(trainPoints.size());
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
                    indexAndFeature = null;
                    trainPoint.getFeatures()[index] = feature;
                }

                trainPoints.add(trainPoint);
            }
            System.out.println("-----------读取训练数据结束-----------");
        } catch (Exception e) {
            System.out.println("找不到文件");
            e.printStackTrace();
        } finally {
            handlerInputStream();
        }
        return trainPoints;
    }



    public static List<TestPoint> readTestData() {
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
                System.out.println("当前数据id："+id);
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
