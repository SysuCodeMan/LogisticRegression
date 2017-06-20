import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Chen on 2017/6/19.
 */
public class LogisticRegression {
    private static List<TrainPoint> trainPoints;
    private static List<TestPoint> testPoints;
    private static double alpha = 0.00001;

    private static ExecutorService ThreadPool;

    private static double h(double[] x, double[] theata) {
        double product = 0.0;
        for (int i = 0; i < x.length; i++) {
            product += x[i]*theata[i];
        }
        double result = 1/(1+Math.exp(-product));
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            System.out.println("h result"+result);
        }
        return result;
    }

    private static double cost(double h, double y) {
        double result = -y*Math.log(h)-(1-y)*Math.log(1-h);
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            System.out.println("cost result"+result);
        }
        return  result;
    }

    private static double J(double[] theata) {
        double sum = 0.0;
        for (int i = 0; i < trainPoints.size(); i++) {
            TrainPoint trainPoint = trainPoints.get(i);
            double h = h(trainPoint.getFeatures(), theata);
            sum += cost(h, trainPoint.getLabel());
        }
        return sum/trainPoints.size();
    }

    private static double[] getGaps(double[] theata) {
        int size = trainPoints.size();
        double[] gaps = new double[size];
        for(int i = 0; i < size; i++) {
            TrainPoint trainPoint = trainPoints.get(i);
            gaps[i] = h(trainPoint.getFeatures(), theata)-trainPoint.getLabel();
        }
        return gaps;
    }

    private static double[] getNextTheata(double[] theata) {
        double[] nextTheata = new double[theata.length];
        double[] gaps = getGaps(theata);
        for (int j = 0; j < nextTheata.length; j++) {
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
            theata[i] = 0.0;
        }
        return theata;
    }

    private static double[] serialLR() {
        double[] theata = initTheata(202);
        int count = 1;
        long startTime = new Date().getTime();
        long endtTime;
        while (true) {
            double[] nextTheata = getNextTheata(theata);
            double currentJ = J(theata);
            System.out.println("第"+count+"次迭代,当前J值为："+currentJ);
            count++;
            theata = nextTheata;
            if (count >= 1000) {
                endtTime = new Date().getTime();
                break;
            }
        }
        System.out.println("SerialLR cost:"+(endtTime-startTime)/1000+"s");
        return theata;
    }

    private static double[] parallelLR() {
        double[] theata = initTheata(202);
        long startTime = new Date().getTime();
        long endtTime;
        int count = 1;
        while (true) {
            double[] nextTheata = getNextParallelTheata(theata);
            double currentJ = J(theata);
            System.out.println("第"+count+"次迭代,当前J值为："+currentJ);
            count++;
            theata = nextTheata;
            if (count >= 1000) {
                endtTime = new Date().getTime();
                break;
            }
        }
        System.out.println("ParallelLR cost:"+(endtTime-startTime)/1000+"s");
        return theata;
    }

    private static double[] getNextParallelTheata(double[] theata) {
        double[] nextParallelTheata = new double[202];
        double[] gaps = getParallelGaps(theata);
        ThreadPool = Executors.newCachedThreadPool();
        try {
            ThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 50; j++) {
                        double sum = 0.0;
                        for (int i = 0; i < trainPoints.size(); i++) {
                            sum += gaps[i] * trainPoints.get(i).getFeatures()[j];
                        }
                        nextParallelTheata[j] = theata[j] - alpha * sum;
                    }
                }
            });
            ThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    for (int j = 51; j < 100; j++) {
                        double sum = 0.0;
                        for (int i = 0; i < trainPoints.size(); i++) {
                            sum += gaps[i] * trainPoints.get(i).getFeatures()[j];
                        }
                        nextParallelTheata[j] = theata[j] - alpha * sum;
                    }
                }
            });
            ThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    for (int j = 101; j < 150; j++) {
                        double sum = 0.0;
                        for (int i = 0; i < trainPoints.size(); i++) {
                            sum += gaps[i] * trainPoints.get(i).getFeatures()[j];
                        }
                        nextParallelTheata[j] = theata[j] - alpha * sum;
                    }
                }
            });
            ThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    for (int j = 150; j < 202; j++) {
                        double sum = 0.0;
                        for (int i = 0; i < trainPoints.size(); i++) {
                            sum += gaps[i] * trainPoints.get(i).getFeatures()[j];
                        }
                        nextParallelTheata[j] = theata[j] - alpha * sum;
                    }
                }
            });
            ThreadPool.shutdown();
            while (true) {
                if (ThreadPool.isTerminated())
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nextParallelTheata;
    }

    private static double[] getParallelGaps(double[] theata) {
        int size = trainPoints.size();
        double[] parallelGaps = new double[size];
        ThreadPool = Executors.newCachedThreadPool();
        try {
            ThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    for(int i = 0; i < size/4; i++) {
                        TrainPoint trainPoint = trainPoints.get(i);
                        parallelGaps[i] = h(trainPoint.getFeatures(), theata)-trainPoint.getLabel();
                    }
                }
            });
            ThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    for(int i = size/4; i < size/2; i++) {
                        TrainPoint trainPoint = trainPoints.get(i);
                        parallelGaps[i] = h(trainPoint.getFeatures(), theata)-trainPoint.getLabel();
                    }
                }
            });
            ThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    for(int i = size/2; i < size/4*3; i++) {
                        TrainPoint trainPoint = trainPoints.get(i);
                        parallelGaps[i] = h(trainPoint.getFeatures(), theata)-trainPoint.getLabel();
                    }
                }
            });
            ThreadPool.execute(new Runnable() {
                @Override
                public void run() {

                    for(int i = size/4*3; i < size; i++) {
                        TrainPoint trainPoint = trainPoints.get(i);
                        parallelGaps[i] = h(trainPoint.getFeatures(), theata)-trainPoint.getLabel();
                    }
                }
            });
            ThreadPool.shutdown();
            while (true) {
                if (ThreadPool.isTerminated())
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parallelGaps;
    }

    public static void main(String[] args) {
        trainPoints = DataUtil.getTrainPoints();
        double[] serialTheata = serialLR();
        double[] parallelTheata = parallelLR();
        testPoints = DataUtil.getTestPoint();
        for (TestPoint testPoint : testPoints) {
            double label = h(testPoint.getFeatures(), serialTheata);
            testPoint.setLabel(label);
        }
        DataUtil.writeSubmission(testPoints);
    }
}
