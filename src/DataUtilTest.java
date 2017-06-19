import java.util.List;

/**
 * Created by Chen on 2017/6/19.
 */
public class DataUtilTest {
    public static void main(String[] args) {
        List<TestPoint> testPointList = DataUtil.readTestData();
        DataUtil.writeSubmission(testPointList);
    }
}
