/**
 * Created by Chen on 2017/6/19.
 */
public class TrainPoint {
    private double label;
    private double[] features;


    public TrainPoint() {
        features = new double[202];
        features[0] = 1.0;
//        for (int i = 1; i < features.length; i++) {
//            features[i] = 0.0;
//        }
    }

    public double getLabel() {
        return label;
    }

    public void setLabel(double label) {
        this.label = label;
    }

    public double[] getFeatures() {
        return features;
    }

    public void setFeatures(double[] features) {
        this.features = features;
    }
}
