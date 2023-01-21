package frc.robot;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.Spark;


public class LED_Signaling {
    

    // Defines the Spark motor controller.
    public PWMSparkMax Spark = new PWMSparkMax(1);



    double[] BlinkinValues = {
           // The following values will be used for the Blinkin, and are assigned to the relative action. Just change it here, and it should update everywhere else in the code :) 
        0, // Decoration
        0, // Error
        0, // In_Progress
        0, // Idle
        0, // ReadyToScore
    };


    // Sets the LED pattern to Idle at startup.
    public LED_Signaling() {
        //SetLEDS(LED_State.Idle);
        Spark.set(0.5);
    }

    // I mean, it's an enum, need I say more?
    enum LED_State {
        Decoration, // Probably for AhTahn Mode
        Error, // Error
        In_Progress, // Picking up or scoring
        Idle, // When Idle/not doing anything
        ReadyToScore; // Item in claw/intake
    }

    public void changepattern(double m_value) {
        Spark.set(Spark.get()+m_value);
        System.out.println(Spark.get());
    }

    public void test_function() {
        Spark.set(0.07);
    }

    /**
     * Sets the LED pattern.
     *  @param Status Can be set to Decoration, Error, In_Progress, Idle, or ReadyToScore.
     */
    
    public void SetLEDS(LED_State Status) {
        if (Status == LED_State.Decoration) {
            Spark.set(BlinkinValues[1]);

        } else if (Status == LED_State.Error) {
            Spark.set(BlinkinValues[2]);

        } else if (Status == LED_State.In_Progress) {
            Spark.set(BlinkinValues[3]);

        } else if (Status == LED_State.Idle) {
            Spark.set(BlinkinValues[4]);

        } else if (Status == LED_State.ReadyToScore) {
            Spark.set(BlinkinValues[5]);
        }
    }
    
}

