/**
 * Created by Chen on 2017/6/19.
 */
public class TestPoint {
    private int id;
    private double label;
    private double[] features;

    public TestPoint() {
        features = new double[133];
        features[0] = 1.0;
        label = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
