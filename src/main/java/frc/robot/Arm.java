package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;


public class Arm {

    double arm_length;

    WPI_TalonFX claw_motor = new WPI_TalonFX(0, "rio");
//  Jonathan to add rotating_arm, 2nd_stage_elevator, 1st_stage_elevator

    public Arm() {
        
    }
    

    public void score_game_piece() {
        // put score code here
    }

    public void eject_game_piece() {
        // put eject code here
    }

    public void pickup_game_piece() {
        // put pickup code here
    }

}
