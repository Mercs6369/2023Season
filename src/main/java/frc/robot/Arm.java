package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;

// test ignore this
public class Arm {
    


    enum ArmStateEnum {
        Idle,
        Scoring,
        Ejecting,
        Picking_up,
        error
      }
    
    ArmStateEnum ARM_STATE = ArmStateEnum.Idle;

    // Variables
    double arm_length;    
    private final I2C.Port i2cPort = I2C.Port.kOnboard;


    // Motors
    WPI_TalonFX claw_motor = new WPI_TalonFX(0, "rio");
    WPI_TalonFX pivot_motor = new WPI_TalonFX(1, "rio");
    WPI_TalonFX first_stage_elevator = new WPI_TalonFX(2, "rio");
    WPI_TalonFX second_stage_elevator = new WPI_TalonFX(3, "rio");

    ColorSensorV3 claw_sensor = new ColorSensorV3(i2cPort);



    public Arm() {}





    public void _Score_Game_Piece() {
        if (ARM_STATE == ArmStateEnum.Idle) {
            ARM_STATE = ArmStateEnum.Scoring;
        }
    }

    public void _Eject_Game_Piece() {
        if (ARM_STATE == ArmStateEnum.Idle) {
            ARM_STATE = ArmStateEnum.Scoring;
        }
    }

    public void _Pickup_Game_Piece() {
        if (ARM_STATE == ArmStateEnum.Idle) {
            ARM_STATE = ArmStateEnum.Scoring;
        }
    }

//  ^^^ These are the methods you would call when you want to eject/score/pickup.




    private void ejectPeriodic() {

    }

    private void scorePeriodic() {

    }

    private void pickupPeriodic() {

    }

//  ^^^ These shouldn't need to be messed with (besides fine-tuning) after they're laid out.

    public void armPeriodic() {
        if (ARM_STATE == ArmStateEnum.Scoring) {
            scorePeriodic();
        } else if (ARM_STATE == ArmStateEnum.Ejecting) {
            ejectPeriodic();
        } else if (ARM_STATE == ArmStateEnum.Picking_up) {
            pickupPeriodic();
        }
    }

//  ^^^ This needs to be run constantly whenever you want the arm to work and do magik shtuff

}
