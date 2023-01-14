package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;


public class Arm {

    // Variables
    double arm_length;
    
    String eject_stage = "neutral";
    String score_stage = "neutral";
    String pickup_stage = "neutral";
    
    boolean movement_finished = false;
    // If we've detected we've finished a stage, set movement_finished to true.
    
    private final I2C.Port i2cPort = I2C.Port.kOnboard;


    // Motors
    WPI_TalonFX claw_motor = new WPI_TalonFX(Constants.CLAW_MOTOR_ID, "rio");
    WPI_TalonFX pivot_motor = new WPI_TalonFX(Constants.PIVOT_MOTOR_ID, "rio");
    WPI_TalonFX first_stage_elevator = new WPI_TalonFX(Constants.FIRST_STAGE_ELEVATOR_ID, "rio");
    WPI_TalonFX second_stage_elevator = new WPI_TalonFX(Constants.SECOND_STAGE_ELEVATOR_ID, "rio");

    ColorSensorV3 claw_sensor = new ColorSensorV3(i2cPort);



    public Arm() {

    }
    
    private void update_stage(String current_action, String current_stage) {

    }



    // Arm functions
    
    public void score_game_piece() {}


    public void eject_game_piece() {
        // movement_finished needs to be set to "true" at any point when robot is ready to go to the next stage :)
        movement_finished = false;

        if (eject_stage == "neutral" || eject_stage == "stage_one") {
            pickup_stage = "stage_one";
 
        } else if (eject_stage == "stage_two") {

        } else if (eject_stage == "stage_three") {
            
        } else if (eject_stage == "stage_four") {
            
        } else if (eject_stage == "stage_five") {
            
        } else if (eject_stage == "stage_six") {
            
        }

        
        
        
        // This is independent from any if loops
        if (movement_finished) {
            update_stage("eject", eject_stage);
        }
    }


    public void pickup_game_piece() {}

}
