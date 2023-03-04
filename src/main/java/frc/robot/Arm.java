package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.Timer;

import java.util.TimerTask;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FollowerType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;

import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class Arm {

    Compressor phCompressor = new Compressor(1, PneumaticsModuleType.REVPH);    
//Compressor phCompressor = new Compressor(PneumaticsModuleType.CTREPCM);
    DoubleSolenoid LeftClawSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, 2, 3);
    DoubleSolenoid RightClawSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, 0, 1);

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
    double current_vertical_elevator_position_command, current_horizontal_elevator_position_command;
    
    // Used to time how long actions take.
    Timer m_time = new Timer();

    // Used to help time/document/run any actions.
    boolean action_finished = false;

    // Motors
    CANSparkMax intake_motor_1 = new CANSparkMax(Constants.INTAKE_MOTOR_1_ID, MotorType.kBrushless);
    CANSparkMax intake_motor_2 = new CANSparkMax(Constants.INTAKE_MOTOR_2_ID, MotorType.kBrushless);
    WPI_TalonFX vertical_elevator_motor_1 = new WPI_TalonFX(Constants.VERTICAL_ELEVATOR_MOTOR_1_ID, "rio"); 
    WPI_TalonFX vertical_elevator_motor_2 = new WPI_TalonFX(Constants.VERTICAL_ELEVATOR_MOTOR_2_ID, "rio");
    WPI_TalonFX horizontal_elevator_motor = new WPI_TalonFX(Constants.HORIZONTAL_ELEVATOR_MOTOR_ID, "rio");

    public Arm() { // constructor
        phCompressor.enableDigital();
        //phCompressor.enableAnalog(119, 120);
        pneumaticsOpen();

        // elevator motor setups
        vertical_elevator_motor_1.configFactoryDefault();
        vertical_elevator_motor_1.configFactoryDefault();
        
        vertical_elevator_motor_1.setInverted(false);
        vertical_elevator_motor_1.setSensorPhase(false);
        vertical_elevator_motor_1.setNeutralMode(NeutralMode.Coast);

        vertical_elevator_motor_2.setInverted(false);        
        vertical_elevator_motor_2.setSensorPhase(false);
        vertical_elevator_motor_2.setNeutralMode(NeutralMode.Coast);

        vertical_elevator_motor_1.config_kP(0, 0.05, 30);
        vertical_elevator_motor_1.config_kI(0, 0.0, 30);
        vertical_elevator_motor_1.config_kD(0, 0.0, 30);
        vertical_elevator_motor_1.config_kF(0, 0.0, 30);
        vertical_elevator_motor_1.configClosedloopRamp(0.25);


        vertical_elevator_motor_2.config_kP(0, 0.05, 30);
        vertical_elevator_motor_2.config_kI(0, 0.0, 30);
        vertical_elevator_motor_2.config_kD(0, 0.0, 30);
        vertical_elevator_motor_2.config_kF(0, 0.0, 30);
        vertical_elevator_motor_2.configClosedloopRamp(0.25);


        //vertical_elevator_motor_1.configAllowableClosedloopError(0, 20, 30);
        //vertical_elevator_motor_2.configAllowableClosedloopError(0, 20, 30);

        vertical_elevator_motor_2.follow(vertical_elevator_motor_1);
        current_vertical_elevator_position_command = vertical_elevator_motor_1.getSelectedSensorPosition();

        horizontal_elevator_motor.setInverted(false);        
        horizontal_elevator_motor.setSensorPhase(false);
        horizontal_elevator_motor.setNeutralMode(NeutralMode.Coast);

        horizontal_elevator_motor.config_kP(0, 0.2, 30);
        horizontal_elevator_motor.config_kI(0, 0.0, 30);
        horizontal_elevator_motor.config_kD(0, 0.0, 30);
        horizontal_elevator_motor.config_kF(0, 0.0, 30);

        // INTAKE SECTION
        intake_motor_1.restoreFactoryDefaults();
        intake_motor_2.restoreFactoryDefaults();

        intake_motor_1.setInverted(false);
        //intake_motor_1.setSensorPhase(false);
        //intake_motor_1.setNeutralMode(NeutralMode.Coast);

        intake_motor_2.setInverted(false);
        //intake_motor_2.setSensorPhase(false);
        //intake_motor_2.setNeutralMode(NeutralMode.Coast);
    }

    public void setIntakeMotor1(double joyStickValue) {
        intake_motor_1.set(joyStickValue);
    }
    public void setIntakeMotor2(double joyStickValue) {
        intake_motor_2.set(joyStickValue);
    }

    // Pneumatics
    public void pneumaticsClose(){
        LeftClawSolenoid.set(Value.kForward);
        RightClawSolenoid.set(Value.kForward);
    }

    public void pneumaticsOpen(){
        LeftClawSolenoid.set(Value.kReverse);
        RightClawSolenoid.set(Value.kReverse);
    }

    // Coach Dan testing I think.
    public void move_vertical_elevator(double controller_input){
        current_vertical_elevator_position_command = current_vertical_elevator_position_command + 100*controller_input;
        vertical_elevator_motor_1.set(ControlMode.Position, current_vertical_elevator_position_command);
        //elevator_motor_1.set(ControlMode.PercentOutput, controller_input);
    }

    public void move_vertical_elevator_to_pos(double input){
        current_vertical_elevator_position_command = input;
        vertical_elevator_motor_1.set(ControlMode.Position, input);

    }

    public double getVerticalElevatorPosition() {
        return vertical_elevator_motor_1.getSelectedSensorPosition();
        
    }

/*     public void recalibrateElevatorPositions() {
        vertical_elevator_motor_1.setSelectedSensorPosition(?, 0, 30);
        vertical_elevator_motor_2.setSelectedSensorPosition(?, 0, 30);
        horizontal_elevator_motor.setSelectedSensorPosition(?, 0, 30);
    } */

    public void updateFparameter(double kF) {
        vertical_elevator_motor_2.config_kF(0, kF, 30);
    }


    public void move_horizontal_elevator(double controller_input){
        current_horizontal_elevator_position_command = current_horizontal_elevator_position_command - 100*controller_input;
        //horizontal_elevator_motor.set(ControlMode.Position, current_horizontal_elevator_position_command);
        horizontal_elevator_motor.set(ControlMode.PercentOutput, controller_input);
    }

    public double getHorizontalElevatorPosition() {
        return horizontal_elevator_motor.getSelectedSensorPosition();
    }


    /**
     * Call this whenever you need to score a game piece. You do not have to worry about running this repeatedly, you can run it as many times as you need. You don't need to worry about adding a debounce.
     */
    public void _Score_Game_Piece() {
        if (GLOBAL_ARM_STATE == ArmStateEnum.Idle) {
            GLOBAL_ARM_STATE = ArmStateEnum.Scoring;            
            m_time.start();
        }
    }

    /**
     * Call this whenever you need to eject a game piece. You do not have to worry about running this repeatedly, you can run it as many times as you need. You don't need to worry about adding a debounce.
     */
    public void _Eject_Game_Piece() {
        if (GLOBAL_ARM_STATE == ArmStateEnum.Idle) {
            GLOBAL_ARM_STATE = ArmStateEnum.Ejecting;
            m_time.start();
        }
    }

    /**
     * Call this whenever you need to pickup a game piece. You do not have to worry about running this repeatedly, you can run it as many times as you need. You don't need to worry about adding a debounce.
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
        actionProgress = 0;
    }

//  These three methods shouldn't need to be run except inside armPeriodic()

    int actionProgress = 0;

    private void ejectPeriodic() {
       
        if (actionProgress == 0){
            pneumaticsOpen();

            if (true) { // this if statment needs to be true if we're ready to go onto the next state
                actionProgress ++;
            }
        } else if (actionProgress == 1) {

            // make sure the arm is out of the robot perimeter, so that when we drop the object, we don't drop it on the bot. 

            if (true) { // this if statment needs to be true if we're ready to go onto the next state
                actionProgress ++;
            }
        } else {
            end_action();
        }
    }

    private void scorePeriodic() {

        if (actionProgress == 0) {
           // make sure the robot is in the correct position

           if (true) { // this if statment needs to be true if we're ready to go onto the next state
                actionProgress ++;
            }
        } else if (actionProgress == 1) {
            // angle the arm to scoring position

            if (true) { // this if statment needs to be true if we're ready to go onto the next state
                actionProgress ++;
            }
        } else if (actionProgress == 2) {
            pneumaticsOpen();

            if (true) { // this if statment needs to be true if we're ready to go onto the next state
                actionProgress ++;
            }
        } else {
            end_action();
        }
    }

    private void pickupPeriodic() {
        
        if (actionProgress == 0) {
            pneumaticsOpen();

            if (true) { // this if statment needs to be true if we're ready to go onto the next state
                actionProgress ++;
            }
         } else if (actionProgress == 1) {
            // angle the arm to pickup position

            if (true) { // this if statment needs to be true if we're ready to go onto the next state
               actionProgress ++;
           }
        } else if (actionProgress == 1) {


            // lock on

            // go forward/backward


             if (true) { // this if statment needs to be true if we're ready to go onto the next state
                actionProgress ++;
            }
         } else if (actionProgress == 2) {
             pneumaticsClose();
         } else {
             end_action(); // possibly move to scoring position?
         }
    }


    /**
     * This needs to be run constantly to do anything. So teleopPeriodic, autoPeriodic, etc.
     */
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
