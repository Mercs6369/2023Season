package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;


public class Arm {

    // Variables
    double arm_length;

    // Motors
    WPI_TalonFX claw_motor = new WPI_TalonFX(0, "rio");
    WPI_TalonFX pivot_motor = new WPI_TalonFX(1, "rio");
    WPI_TalonFX first_stage_elevator = new WPI_TalonFX(2, "rio");
    WPI_TalonFX second_stage_elevator = new WPI_TalonFX(3, "rio");



    public Arm() {}
    

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
