import java.util.List;

/**
 * Created by Chen on 2017/6/19.
 */
public class LogisticRegression {
    private static List<TrainPoint> trainPoints;
    private static List<TestPoint> testPoints;
    private static double alpha = 0.1;

    private static double h(double[] x, double[] theata) {
        double product = 0.0;
        double denominator;
        for (int i = 0; i < x.length; i++) {
            product += x[i]*theata[i];
        }
        product = -product;
        denominator = 1+ Math.pow(Math.E, product);
        return 1/denominator;
    }

    private static double cost(double h, double y) {
        System.out.println("-----------计算cost-----------");
        return  -y*Math.log(h)-(1-y)*Math.log(1-h);
    }

    private static double J(double[] theata) {
        System.out.println("-----------计算J-----------");
        double sum = 0.0;
        for (TrainPoint trainPoint : trainPoints) {
            sum += cost(h(trainPoint.getFeatures(), theata), trainPoint.getLabel());
        }
        return sum/trainPoints.size();
    }

    private static double[] getGaps(double[] theata) {
        System.out.println("-----------计算Gap-----------");
        int m = trainPoints.size();
        double[] gaps = new double[m];
        for(int i = 0; i < trainPoints.size(); i++) {
            System.out.println("------------计算第"+i+"个样本的差值-----------");
            gaps[i] = h(trainPoints.get(i).getFeatures(), theata)-trainPoints.get(i).getLabel();
        }
        return gaps;
    }

    private static double[] getNextTheata(double[] theata) {
        System.out.println("-----------计算NextTheata-----------");
        double[] nextTheata = new double[theata.length];
        for (int j = 0; j < nextTheata.length; j++) {
            double[] gaps = getGaps(theata);
            double sum = 0.0;
            for (int i = 0; i < trainPoints.size(); i++) {
                sum += gaps[i]*trainPoints.get(i).getFeatures()[j];
            }
            nextTheata[j] = theata[j]-alpha*sum;
        }
        return nextTheata;
    }

    private static double[] initTheata(int size) {
        double[] theata = new double[size];
        for (int i = 0; i < size; i++) {
            theata[i] = 0.5;
        }
        return theata;
    }

    public static void main(String[] args) {
        trainPoints = DataUtil.readTrainData();
        double[] theata = initTheata(202);
        int count = 1;
        while (true) {
            double[] nextTheata = getNextTheata(theata);
            double currentJ = J(theata);
            System.out.println("第"+count+"次迭代,当前J值为："+currentJ);
            if (currentJ - J(nextTheata) < 0.000001) {
                theata = nextTheata;
                break;
            }
        }
        trainPoints = null;
        testPoints = DataUtil.readTestData();
        for (TestPoint testPoint : testPoints) {
            double label = h(testPoint.getFeatures(), theata);
            testPoint.setLabel(label);
        }
        DataUtil.writeSubmission(testPoints);
    }
}
