package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import model.Individual;

public class IndividualLifePhaseTest {

    @Test
    public void testGetLifePhaseBoundaries() {
        // Create an individual with a specific timeline
        int dateOfBirth = 0;
        int[] lifePhases = {12, 24, 36}; // Milestones for phase transitions
        double sexRatio = 0.5;
        double[] initialLocation = {0.0, 0.0};
        
        Individual individual = new Individual(dateOfBirth, lifePhases, sexRatio, initialLocation);

        //Verify behavior at the boundary edges
        
        // Case A: Clearly before the milestone (Age 11)
        assertEquals(0, individual.getLifePhase(11), "At age 11, individual should be in phase 0");

        // Case B: Exactly on the milestone value (Age 12)
        // 12 < 12 is false, so it should technically still return phase 0
        assertEquals(1, individual.getLifePhase(24), "At age 24, strict less-than logic keeps individual in phase 0");

        // Case C: One step past the milestone value (Age 13)
        // 12 < 13 is true, so it should advance to phase 1
        assertEquals(1, individual.getLifePhase(13), "At age 13, individual should transition to phase 1");
    }
}