package frc.robot;



// don't mess with this or else you'll get whacked in the BEAN with a baguette!
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;


public class LED_Signaling {
    public PWMSparkMax motor = new PWMSparkMax(0);

    public void dolightstuff(double colorinput) {
        motor.set(colorinput);
    }
}
