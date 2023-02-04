package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.Timer;

import java.util.TimerTask;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm {

    Compressor phCompressor = new Compressor(1, PneumaticsModuleType.REVPH);
    //Compressor phCompressor = new Compressor(PneumaticsModuleType.CTREPCM);
    DoubleSolenoid LeftClawSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, 2, 3);
    DoubleSolenoid RightClawSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, 0, 1);

    public Arm() { // constructor
        phCompressor.enableDigital();
        //phCompressor.enableAnalog(119, 120);
    } 

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

    // Motors
    WPI_TalonFX claw_motor = new WPI_TalonFX(Constants.CLAW_MOTOR_ID, "rio"); 
    WPI_TalonFX pivot_motor = new WPI_TalonFX(Constants.PIVOT_MOTOR_ID, "rio"); 
    WPI_TalonFX first_stage_elevator = new WPI_TalonFX(Constants.FIRST_STAGE_ELEVATOR_ID, "rio"); 
    WPI_TalonFX second_stage_elevator = new WPI_TalonFX(Constants.SECOND_STAGE_ELEVATOR_ID, "rio");

    // newmatics shtuff

    public void update() {
        SmartDashboard.putNumber("High Side Pressure", phCompressor.getPressure());
    }

    public void setForward(){
        LeftClawSolenoid.set(Value.kForward);
        RightClawSolenoid.set(Value.kForward);
    }

    public void setReverse(){
        LeftClawSolenoid.set(Value.kReverse);
        RightClawSolenoid.set(Value.kReverse);
    }

    public void close(){
        RightClawSolenoid.close();
        LeftClawSolenoid.close();

    }

    /**
     * Call this whenever you need to score a game piece. This can be run whenever, you don't need a debounce thingy.
     */
    public void _Score_Game_Piece() {
        if (GLOBAL_ARM_STATE == ArmStateEnum.Idle) {
            GLOBAL_ARM_STATE = ArmStateEnum.Scoring;            
            m_time.start();
        }
    }

    /**
     * Call this whenever you need to score a eject a game piece piece. This can be run whenever, you don't need a debounce thingy.
     */
    public void _Eject_Game_Piece() {
        if (GLOBAL_ARM_STATE == ArmStateEnum.Idle) {
            GLOBAL_ARM_STATE = ArmStateEnum.Ejecting;
            m_time.start();
        }
    }

    /**
     * Call this whenever you need to score a pickup a game piece piece. This can be run whenever, you don't need a debounce thingy.
     */
    public void _Pickup_Game_Piece() {
        if (GLOBAL_ARM_STATE == ArmStateEnum.Idle) {
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