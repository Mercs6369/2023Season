package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.LED_Signaling.LED_State;

import java.util.TimerTask;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;

// test ignore this
public class Arm {
    
    public Arm() {}


    // Use dis for LED things.
    LED_Signaling LED_Instance = new LED_Signaling();
    
    // Uhh yea, enum.
    enum ArmStateEnum {
        Idle,
        Scoring,
        Ejecting,
        Picking_up,
        error
    }
    
    // GLOBAL_ARM_STATE is used quite a bit.
    ArmStateEnum GLOBAL_ARM_STATE = ArmStateEnum.Idle;

    // I don't think this has a use.
    double arm_length;   
    
    // Used to time how long actions take.
    Timer m_time = new Timer();

    // Used to help time/document/run any actions.
    boolean action_finished = false;    





   
   
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



    /**
     * Call this whenever you need to score a game piece. This can be run whenever, you don't need a debounce thingy.
     */
    public void _Score_Game_Piece() {
        if (GLOBAL_ARM_STATE == ArmStateEnum.Idle) {
            LED_Instance.SetLEDS(LED_State.In_Progress);
            GLOBAL_ARM_STATE = ArmStateEnum.Scoring;            
            m_time.start();
        }
    }

    /**
     * Call this whenever you need to score a eject a game piece piece. This can be run whenever, you don't need a debounce thingy.
     */
    public void _Eject_Game_Piece() {
        if (GLOBAL_ARM_STATE == ArmStateEnum.Idle) {
            LED_Instance.SetLEDS(LED_State.In_Progress);
            GLOBAL_ARM_STATE = ArmStateEnum.Ejecting;
            m_time.start();
        }
    }

    /**
     * Call this whenever you need to score a pickup a game piece piece. This can be run whenever, you don't need a debounce thingy.
     */
    public void _Pickup_Game_Piece() {
        if (GLOBAL_ARM_STATE == ArmStateEnum.Idle) {
            LED_Instance.SetLEDS(LED_State.In_Progress);
            GLOBAL_ARM_STATE = ArmStateEnum.Picking_up;
            m_time.start();
        }
    }

//  ^^^ These three methods you would call when you want to eject/score/pickup. 
//      Theoretically these can be run however many times you want, you shouldn't have to implement a debounce thingy :P



    /**
     * You shouldn't have to run this method. It should all be handled internally, which is why it's private, and not public. But it just ends the current action.
     */
    private void end_action() {
        System.out.println("Action duration for the action "+GLOBAL_ARM_STATE+" was (in secs) "+m_time.get()+".");
        m_time.reset();
        m_time.stop();
        GLOBAL_ARM_STATE = ArmStateEnum.Idle;
        LED_Instance.SetLEDS(LED_State.Idle);
    }


//  These three methods shouldn't need to be run except inside armPeriodic()

    private void ejectPeriodic() {

    }

    private void scorePeriodic() {
    }

    private void pickupPeriodic() {

    }



//  This needs to be run constantly whenever you want the arm to work and do magik shtuff
    public void armPeriodic() {
        if (GLOBAL_ARM_STATE == ArmStateEnum.Scoring) {
            scorePeriodic();
        } else if (GLOBAL_ARM_STATE == ArmStateEnum.Ejecting) {
            ejectPeriodic();
        } else if (GLOBAL_ARM_STATE == ArmStateEnum.Picking_up) {
            pickupPeriodic();
        }
    }


};