package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.math.MathUtil;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

public class Arm {
    WPI_TalonFX intake = new WPI_TalonFX(Constants.INTAKE_ROLLER_MOTOR_1_ID);
    WPI_TalonFX main_arm_motor_1 = new WPI_TalonFX(Constants.MAIN_ARM_MOTOR_1_ID, "rio"); 
    WPI_TalonFX main_arm_motor_2 = new WPI_TalonFX(Constants.MAIN_ARM_MOTOR_2_ID, "rio");
    WPI_TalonSRX intake_arm_motor = new WPI_TalonSRX(Constants.INTAKE_ARM_MOTOR_ID);
    DigitalInput intake_arm_encoder_raw = new DigitalInput(Constants.INTAKE_ARM_ENCODER_PWM_CHANNEL);
    DutyCycleEncoder intake_arm_encoder = new DutyCycleEncoder(intake_arm_encoder_raw);
    PIDController intake_arm_PID = new PIDController(0.25, 0, 0);
    int actionProgress = 0;

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

    // Used to time how long actions take.
    Timer m_time = new Timer();

    // GLOBAL_ARM_STATE is used quite a bit.
    ArmStateEnum GLOBAL_ARM_STATE = ArmStateEnum.Idle;

    // Used to help time/document/run any actions.
    boolean action_finished = false;
    
    double current_main_arm_position_command, current_intake_arm_position_command;

    Arm (){
        phCompressor.enableDigital();
        //phCompressor.enableAnalog(119, 120);
        pneumaticsOpen();
        
        intake.configFactoryDefault();
        intake_arm_motor.configFactoryDefault();
        // elevator motor setups
        main_arm_motor_1.configFactoryDefault();
        main_arm_motor_2.configFactoryDefault();
        
        main_arm_motor_1.setInverted(false);
        main_arm_motor_1.setSensorPhase(true);
        main_arm_motor_1.setNeutralMode(NeutralMode.Brake);

        main_arm_motor_2.setInverted(true);        
        main_arm_motor_2.setSensorPhase(false);
        main_arm_motor_2.setNeutralMode(NeutralMode.Brake);

        main_arm_motor_1.config_kP(0, 0.05, 30);
        main_arm_motor_1.config_kI(0, 0.0, 30);
        main_arm_motor_1.config_kD(0, 0.0, 30);
        main_arm_motor_1.config_kF(0, 0.0, 30);
        main_arm_motor_1.configClosedloopRamp(0.25);

        main_arm_motor_2.config_kP(0, 0.05, 30);
        main_arm_motor_2.config_kI(0, 0.0, 30);
        main_arm_motor_2.config_kD(0, 0.0, 30);
        main_arm_motor_2.config_kF(0, 0.0, 30);
        main_arm_motor_2.configClosedloopRamp(0.25);

        main_arm_motor_2.follow(main_arm_motor_1);
        current_main_arm_position_command = main_arm_motor_1.getSelectedSensorPosition();

        intake_arm_motor.setInverted(false);
        intake_arm_motor.setNeutralMode(NeutralMode.Brake);
        intake_arm_motor.configOpenloopRamp(0.15);
    }
    
    public void setIntakeMotor(double input){
        intake.set(ControlMode.PercentOutput, input);
    }

    public void move_main_arm_to_position(double input){
        current_main_arm_position_command = input;
        main_arm_motor_1.set(ControlMode.Position, input);
    }

    public double get_main_arm_position() {
        return main_arm_motor_1.getSelectedSensorPosition();
    }

    public double get_main_arm_position_throughbore() {
        //return main_arm_encoder.getAbsolutePosition();
        return 0;
    }
    
    public void move_intake_arm_to_position(double input){
        current_intake_arm_position_command = input;
        //intake_arm_motor.set(ControlMode.PercentOutput, 3.0*(get_intake_arm_position() - input));
        intake_arm_motor.set(MathUtil.clamp(intake_arm_PID.calculate(get_intake_arm_position(), input), -0.5, 0.5));
    }

    public double get_intake_arm_position() {
        return intake_arm_encoder.getAbsolutePosition();
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
            //move_main_arm_to_position(Constants.Start_Arm_Position.main_arm_position);
            //move_intake_arm_to_position(Constants.Start_Arm_Position.intake_arm_position);
        }
    }

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
}