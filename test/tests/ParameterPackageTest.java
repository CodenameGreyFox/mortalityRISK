package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import model.ParameterPackage;

public class ParameterPackageTest {

    @Test
    public void testExcelHorizontalParsing() throws Exception {
        String testFilePath = "test/resources/InputFile - Spatially Explicit.xlsx";        
        ParameterPackage parameters = new ParameterPackage(testFilePath, 1, "Spatial");
        
        // Verify it extracted the values exactly as written in the test spreadsheet
        assertNotNull(parameters.speciesNames);
        assertEquals("Panthera pardus 1.00", parameters.speciesNames[0]);
        assertEquals(0.5, parameters.sexRatio[2],0.000001);
        assertEquals(0.975110208672649, 1-parameters.baseBirthMort[0][1][0],0.000001);
        assertEquals(0.00148976642950499, parameters.roadkillPerKmPerYear[1][1],0.0000000001);
        assertEquals(53.17975573, parameters.disRan[1],0.0000000001);
        assertEquals(144, parameters.lifePhases[2][parameters.lifePhases[2].length-1],0.000001);
        assertEquals(12, parameters.lifePhases[0][0],0.000001);
        assertEquals(1, parameters.lifePhases[1].length,0.000001);
        assertEquals(2.14, parameters.avgLitSize[0],0.000001);
        assertEquals(-1, parameters.maxLitSize[0],0.000001);
        assertEquals(-1, parameters.minLitSize[0],0.000001);
        assertEquals(0.123, parameters.populationDensity[1],0.000001);
        assertEquals(0.2211, parameters.maxPopulation[2],0.000001);
        assertEquals((int)11.75348355, parameters.matAge[1],0.000001);
        assertEquals((int)4.446052632, parameters.minTimeBetweenBreeding[1],0.000001);
        assertEquals(15.67, parameters.avgTimeBetweenBreeding[0],0.000001);
        assertEquals(4891.172342, parameters.mateFindingRadius[0],0.000001);
        
    }
    
    @Test
    public void testExcelVerticalParsing() throws Exception {
        String testFilePath = "test/resources/InputFile - Spatially Explicit - Vertical.xlsx";        
        ParameterPackage parameters = new ParameterPackage(testFilePath, 1, "Spatial");
        
        // Verify it extracted the values exactly as written in the test spreadsheet
        assertNotNull(parameters.speciesNames);
        assertEquals("Panthera pardus 1.00", parameters.speciesNames[0]);
        assertEquals(0.5, parameters.sexRatio[2],0.000001);
        assertEquals(0.975110208672649, 1-parameters.baseBirthMort[0][1][0],0.000001);
        assertEquals(0.00148976642950499, parameters.roadkillPerKmPerYear[1][1],0.0000000001);
        assertEquals(53.17975573, parameters.disRan[1],0.0000000001);
        assertEquals(144, parameters.lifePhases[2][parameters.lifePhases[2].length-1],0.000001);
        assertEquals(12, parameters.lifePhases[0][0],0.000001);
        assertEquals(1, parameters.lifePhases[1].length,0.000001);
        assertEquals(2.14, parameters.avgLitSize[0],0.000001);
        assertEquals(-1, parameters.maxLitSize[0],0.000001);
        assertEquals(-1, parameters.minLitSize[0],0.000001);
        assertEquals(0.123, parameters.populationDensity[1],0.000001);
        assertEquals(0.2211, parameters.maxPopulation[2],0.000001);
        assertEquals((int)11.75348355, parameters.matAge[1],0.000001);
        assertEquals((int)4.446052632, parameters.minTimeBetweenBreeding[1],0.000001);
        assertEquals(15.67, parameters.avgTimeBetweenBreeding[0],0.000001);
        assertEquals(4891.172342, parameters.mateFindingRadius[0],0.000001);
        
    }
    
    
    @TempDir
    Path tempDir;

    @Test
    public void testWriteParametersToFile() throws Exception {
        // Create the object and define a path inside the temp directory
        ParameterPackage params = new ParameterPackage("test/resources/InputFile - Spatially Explicit.xlsx",1,"Spatial");
        
        File outputFile = tempDir.resolve("test_output.xlsx").toFile();

        params.saveToFile(outputFile.getAbsolutePath(),"Spatial");

        ParameterPackage loadedParams = new ParameterPackage(outputFile.getAbsolutePath(),1,"Spatial");
        
        // Verify it wrote the values exactly as written in the test spreadsheet
        assertNotNull(loadedParams.speciesNames);
        assertEquals("Panthera pardus 1.00", loadedParams.speciesNames[0]);
        assertEquals(0.5, loadedParams.sexRatio[2],0.000001);
        assertEquals(0.975110208672649, 1-loadedParams.baseBirthMort[0][1][0],0.000001);
        assertEquals(0.00148976642950499, loadedParams.roadkillPerKmPerYear[1][1],0.0000000001);
        assertEquals(53.17975573, loadedParams.disRan[1],0.0000000001);
        assertEquals(144, loadedParams.lifePhases[2][loadedParams.lifePhases[2].length-1],0.000001);
        assertEquals(12, loadedParams.lifePhases[0][0],0.000001);
        assertEquals(1, loadedParams.lifePhases[1].length,0.000001);
        assertEquals(2.14, loadedParams.avgLitSize[0],0.000001);
        assertEquals(-1, loadedParams.maxLitSize[0],0.000001);
        assertEquals(-1, loadedParams.minLitSize[0],0.000001);
        assertEquals(0.123, loadedParams.populationDensity[1],0.000001);
        assertEquals(0.2211, loadedParams.maxPopulation[2],0.000001);
        assertEquals((int)11.75348355, loadedParams.matAge[1],0.000001);
        assertEquals((int)4.446052632, loadedParams.minTimeBetweenBreeding[1],0.000001);
        assertEquals(15.67, loadedParams.avgTimeBetweenBreeding[0],0.000001);
        assertEquals(4891.172342, loadedParams.mateFindingRadius[0],0.000001);
    }
}