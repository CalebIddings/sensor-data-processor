package com.sensordata;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class SensorDataProcessorTest {

    @Test
    public void testExceptionHandling() {
        // Trigger catch block inside calculate() try-catch
        // making data valid so it passes the initialization, but limit invalid
        double[][][] data = { { { 0.0 } } };
        double[][] limit = new double[0][0];
        SensorDataProcessor processor = new SensorDataProcessor(data, limit);
        
        // This will throw ArrayIndexOutOfBounds inside the try block, effectively covering the catch block.
        assertDoesNotThrow(() -> {
            processor.calculate(1.0);
        });
    }

    @Test
    public void testFirstBranchBreak() {
        // Condition: average(data2[i][j]) > 10 && average(data2[i][j]) < 50
        // We need data2 = 20. Let data=20, d=1, limit=0.
        double[][][] data = { { { 20.0 } } };
        double[][] limit = { { 0.0 } };
        SensorDataProcessor processor = new SensorDataProcessor(data, limit);
        
        processor.calculate(1.0);
        // We verify execution simply returns (writes to RacingStatsData.txt safely)
        File file = new File("RacingStatsData.txt");
        assertTrue(file.exists());
    }

    @Test
    public void testFirstBranchFalseSecondTrueBreak() {
        // average > 10 but not < 50 => First condition false.
        // Second condition: Math.max(data, data2) > data => data2 > data.
        // Let data = 10, d = 0.1, limit = 0 => data2 = 100.
        double[][][] data = { { { 10.0 } } };
        double[][] limit = { { 0.0 } };
        SensorDataProcessor processor = new SensorDataProcessor(data, limit);
        
        processor.calculate(0.1);
    }

    @Test
    public void testThirdBranchTrue() {
        // To make Third branch true:
        // First false: average(data2) <= 10.
        // Second false: data2 <= data.
        // Third part 1: |data| < |data2|
        // Third part 2: average(data) < data2
        // Third part 3: (i+1)*(j+1)>0 (always true)
        // Set data = {-5, -50}, d=0.5, limit=0. => for k=0, data2 = -10. 
        // average(data2) = -5 <= 10. data2(-10) <= data(-5). |data(5)| < |data2(10)|. average(data)=-27.5 < -10.
        double[][][] data = { { { -5.0, -50.0 } } };
        double[][] limit = { { 0.0 } };
        SensorDataProcessor processor = new SensorDataProcessor(data, limit);
        
        processor.calculate(0.5);
    }

    @Test
    public void testElseContinue() {
        // All false => else continue
        // data=0, limit=0, d=1 => data2 = 0.
        // 1. average = 0 <= 10 (F)
        // 2. data2(0) > data(0) (F)
        // 3. |data|(0) < |data2|(0) (F)
        double[][][] data = { { { 0.0 } } };
        double[][] limit = { { 0.0 } };
        SensorDataProcessor processor = new SensorDataProcessor(data, limit);
        
        processor.calculate(1.0);
    }

    @Test
    public void testThirdBranchFalseOnSecondCondition() {
        // Here we want:
        // 1. First if false (average <= 10)
        // 2. Second if false (data2 <= data)
        // 3. Third if: A is true (|data| < |data2|)
        // 4. Third if: B is false (average(data) >= data2)
        // Let data = {-5, 0}, d=0.5, limit=0 => data2= -10.
        // average(data) = -2.5. data2 = -10. -2.5 < -10 is FALSE.
        double[][][] data = { { { -5.0, 0.0 } } };
        double[][] limit = { { 0.0 } };
        SensorDataProcessor processor = new SensorDataProcessor(data, limit);
        
        processor.calculate(0.5);
    }
}
