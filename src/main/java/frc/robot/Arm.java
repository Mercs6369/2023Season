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

    //private final I2C.Port i2cPort = I2C.Port.kOnboard; //has known bug

    // Motors
    WPI_TalonFX claw_motor = new WPI_TalonFX(0, "rio");
    WPI_TalonFX pivot_motor = new WPI_TalonFX(1, "rio");
    WPI_TalonFX first_stage_elevator = new WPI_TalonFX(2, "rio");
    WPI_TalonFX second_stage_elevator = new WPI_TalonFX(3, "rio");

    //ColorSensorV3 claw_sensor = new ColorSensorV3(i2cPort);

    public Arm() {

    }
    

    public void score_game_piece() {
        // Scoring game pieces
    }

    public void eject_game_piece() {
        // put eject code here
    }

    public void pickup_game_piece() {
        // put pickup code here  
    }

}
