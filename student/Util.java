
package student;

import java.math.BigDecimal;


public class Util
{
    static double euclidean(final int x1, final int y1, final int x2, final int y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
    
    static BigDecimal getPackagePrice(final int type, final BigDecimal weight, final double distance, BigDecimal percentage) {
        percentage = percentage.divide(new BigDecimal(100));
        switch (type) {
            case 0: {
                return new BigDecimal(10.0 * distance).multiply(percentage.add(new BigDecimal(1)));
            }
            case 1: {
                return new BigDecimal((25.0 + weight.doubleValue() * 100.0) * distance).multiply(percentage.add(new BigDecimal(1)));
            }
            case 2: {
                return new BigDecimal((75.0 + weight.doubleValue() * 300.0) * distance).multiply(percentage.add(new BigDecimal(1)));
            }
            default: {
                return null;
            }
        }
    }
}
