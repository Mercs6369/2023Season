package frc.robot;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;


public class LED_Signaling {
    
    // Defines the Spark motor controller.
    public PWMSparkMax Spark = new PWMSparkMax(0);

    // Sets the LED pattern to Idle at startup.
    public LED_Signaling() {
        SetLEDS(LED_State.Idle);
    }

    // I mean, it's an enum, need I say more?
    enum LED_State {
        Decoration, // Probably for AhTahn Mode
        Error, // Error
        In_Progress, // Picking up or scoring
        Idle, // When Idle/not doing anything
        ReadyToScore; // Item in claw/intake
    }


    
    /**
     * Sets the LED pattern.
     *  @param Status Can be set to Decoration, Error, In_Progress, Idle, or ReadyToScore.
     */
    public void SetLEDS(LED_State Status) {
        if (Status == LED_State.Decoration) {
            Spark.set(0);

        } else if (Status == LED_State.Error) {
            Spark.set(0);

        } else if (Status == LED_State.In_Progress) {
            Spark.set(0);

        } else if (Status == LED_State.Idle) {
            Spark.set(0);

        } else if (Status == LED_State.ReadyToScore) {
            Spark.set(0);
        }
    }

}

