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
    
    public Arm() {}
//  ^^^ idk what to do with this



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
    /*   
    private final I2C.Port i2cPort = I2C.Port.kOnboard;
    ColorSensorV3 claw_sensor = new ColorSensorV3(i2cPort);
    ^^^ Temporary comments             
    */
  

    // Motors
    WPI_TalonFX claw_motor = new WPI_TalonFX(Constants.CLAW_MOTOR_ID, "rio"); 
    WPI_TalonFX pivot_motor = new WPI_TalonFX(Constants.PIVOT_MOTOR_ID, "rio"); 
    WPI_TalonFX first_stage_elevator = new WPI_TalonFX(Constants.FIRST_STAGE_ELEVATOR_ID, "rio"); 
    WPI_TalonFX second_stage_elevator = new WPI_TalonFX(Constants.SECOND_STAGE_ELEVATOR_ID, "rio");





    public void _Score_Game_Piece() {
        if (ARM_STATE == ArmStateEnum.Idle) {
            ARM_STATE = ArmStateEnum.Scoring;
        }
    }

    public void _Eject_Game_Piece() {
        if (ARM_STATE == ArmStateEnum.Idle) {
            ARM_STATE = ArmStateEnum.Ejecting;
        }
    }

    public void _Pickup_Game_Piece() {
        if (ARM_STATE == ArmStateEnum.Idle) {
            ARM_STATE = ArmStateEnum.Picking_up;
        }
    }

//  ^^^ These are the methods you would call when you want to eject/score/pickup. Theoretically these can be run however many times you want, you shouldn't have to implement a debounce thingy :P




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



