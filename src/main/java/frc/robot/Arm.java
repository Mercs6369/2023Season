package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.LED_Signaling.LED_State;

public class Arm {
    WPI_TalonFX intake = new WPI_TalonFX(Constants.INTAKE_ROLLER_MOTOR_1_ID);
    WPI_TalonFX main_arm_motor_1 = new WPI_TalonFX(Constants.MAIN_ARM_MOTOR_1_ID, "rio"); 
    WPI_TalonFX main_arm_motor_2 = new WPI_TalonFX(Constants.MAIN_ARM_MOTOR_2_ID, "rio");
    //WPI_TalonSRX intake_arm_motor = new WPI_TalonSRX(Constants.INTAKE_ARM_MOTOR_ID);
    WPI_TalonFX intake_arm_motor = new WPI_TalonFX(Constants.INTAKE_ARM_MOTOR_ID, "rio");

    DigitalInput intake_arm_encoder_raw = new DigitalInput(Constants.INTAKE_ARM_ENCODER_PWM_CHANNEL);
    DutyCycleEncoder intake_arm_encoder = new DutyCycleEncoder(intake_arm_encoder_raw);
    //PIDController intake_arm_PID = new PIDController(0.25, 0, 0);

    DigitalInput main_arm_encoder_raw = new DigitalInput(Constants.MAIN_ARM_ENCODER_PWM_CHANNEL);
    DutyCycleEncoder main_arm_encoder = new DutyCycleEncoder(main_arm_encoder_raw);

    int actionProgress = 0;

    public enum ArmStateEnum {
        Idle,
        Scoring,
        Picking_up,
        error
    }

    enum GamePieces {
        Cube,
        Cone,
        Neither
    }

    enum ActiveScorePosition {
        Top,
        Middle,
        Community,
        Neither
    }

    enum ActivePickPosition {
        CubeStation,
        CubeGround,
        ConeStation,
        ConeGroundUp,
        ConeGroundSide,
        Neither
    }

    // Used to time how long actions take.
    Timer m_time = new Timer();

    double last_button_time = 1;
    double main_arm_encoder_corrected;

    // GLOBAL_ARM_STATE is used quite a bit.
    ArmStateEnum GLOBAL_ARM_STATE = ArmStateEnum.Idle;
    GamePieces GLOBAL_OBJECT_STATE = GamePieces.Neither;
    ActiveScorePosition GLOBAL_SCORE_POSITION = ActiveScorePosition.Neither;
    ActivePickPosition GLOBAL_PICK_POSITION = ActivePickPosition.Neither;

    // Used to help time/document/run any actions.
    boolean action_finished = false;
    double current_main_arm_position_command, current_intake_arm_position_command;

        Arm (){       
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

        main_arm_motor_1.config_kP(0, 0.025, 30);
        main_arm_motor_1.config_kI(0, 0.0, 30);
        main_arm_motor_1.config_kD(0, 0.0, 30);
        main_arm_motor_1.config_kF(0, 0.0, 30);
        main_arm_motor_1.configClosedloopRamp(0.3);
        main_arm_motor_1.configNeutralDeadband(0.001);
        main_arm_motor_1.configClosedLoopPeakOutput(0, 0.375);
        // main_arm_motor_1.configPeakOutputForward(0.85);
        // main_arm_motor_1.configPeakOutputReverse(0.85);
        
        main_arm_motor_2.config_kP(0, 0.025, 30);
        main_arm_motor_2.config_kI(0, 0.0, 30);
        main_arm_motor_2.config_kD(0, 0.0, 30);
        main_arm_motor_2.config_kF(0, 0.0, 30);
        main_arm_motor_2.configClosedloopRamp(0.3);
        main_arm_motor_2.configNeutralDeadband(0.001);
        main_arm_motor_2.configClosedLoopPeakOutput(0, 0.375);

        // main_arm_motor_2.configPeakOutputForward(0.85);
        // main_arm_motor_2.configPeakOutputReverse(0.85);


        main_arm_motor_2.follow(main_arm_motor_1);
        current_main_arm_position_command = main_arm_motor_1.getSelectedSensorPosition();

        intake_arm_motor.setInverted(true);
        intake_arm_motor.setSensorPhase(false);
        intake_arm_motor.setNeutralMode(NeutralMode.Brake);
        intake_arm_motor.config_kP(0, 0.06, 30);
        intake_arm_motor.config_kI(0, 0.0, 30);
        intake_arm_motor.config_kD(0, 0.0, 30);
        intake_arm_motor.config_kF(0, 0.0, 30);
        intake_arm_motor.configClosedloopRamp(0.15);
        intake_arm_motor.configNeutralDeadband(0.001);
        intake_arm_motor.configClosedLoopPeakOutput(0, 0.25); //0.65
        //intake_arm_motor.configClearPositionOnLimitR(true, 30);


        // intake_arm_motor.configPeakOutputForward(0.9, 30);
        // intake_arm_motor.configPeakOutputReverse(0.9, 30);

        recalibrate_intake_arm_encoder();
        recalibrate_main_arm_encoder();
    }
    
    public void setIntakeMotor(double input){
        intake.set(ControlMode.PercentOutput, input);
    }

    public void move_main_arm_to_position(double input){
        current_main_arm_position_command = input;
        main_arm_motor_1.set(ControlMode.Position, input);
    }

    public void move_main_arm_to_position(double input, double getRightY){
        if (Math.abs(getRightY) > 0.1){
            current_main_arm_position_command = current_main_arm_position_command + 500*getRightY;
        }
        else {
            current_main_arm_position_command = input;

        }
        main_arm_motor_1.set(ControlMode.Position, current_main_arm_position_command);
    }

    public double get_main_arm_position() {
        return main_arm_motor_1.getSelectedSensorPosition();
    }

    public double get_main_arm_position_throughbore() {
        return main_arm_encoder.getAbsolutePosition();
    }
    
    public void move_intake_arm_to_position(double input){
        current_intake_arm_position_command = input;
        intake_arm_motor.set(ControlMode.Position, current_intake_arm_position_command);
        //intake_arm_motor.set(MathUtil.clamp(intake_arm_PID.calculate(get_intake_arm_position(), input), -0.5, 0.5));
    }

    public void move_intake_arm_to_position(double input, double getLeftY){
        if (Math.abs(getLeftY) > 0.1){
            current_intake_arm_position_command = current_intake_arm_position_command + 500*getLeftY;
        }
        else {
            current_intake_arm_position_command = input;

        }
        intake_arm_motor.set(ControlMode.Position, current_intake_arm_position_command);
        //SmartDashboard.putNumber("current_intake_arm_position_command", current_intake_arm_position_command);
        //intake_arm_motor.set(MathUtil.clamp(intake_arm_PID.calculate(get_intake_arm_position(), input), -0.5, 0.5));
    }

    public double get_intake_arm_position() {
        return intake_arm_encoder.getAbsolutePosition();
    }

    public double get_intake_arm_position_selected(){
        return intake_arm_motor.getSelectedSensorPosition();
    }
    public void recalibrate_intake_arm_encoder() {
        //intake_arm_encoder.setPositionOffset(0.9);
        intake_arm_motor.setSelectedSensorPosition(0.0, 0, 30);
    }

    public void recalibrate_main_arm_encoder() {
        if(main_arm_encoder.getAbsolutePosition() < 0.8) {
            main_arm_encoder_corrected = main_arm_encoder.getAbsolutePosition() + 1.0;
        }
        else {
            main_arm_encoder_corrected = main_arm_encoder.getAbsolutePosition();
        }
        main_arm_motor_1.setSelectedSensorPosition((-152130.3358 * main_arm_encoder_corrected) + 152293.97, 0, 30);
    }

    public void processArmCommands0(double intake_setpoint, double main_setpoint){
        move_intake_arm_to_position(intake_setpoint);
        if (Math.abs((get_intake_arm_position_selected() - intake_setpoint)) < Constants.ARM_COMMAND_CHECK_LIMIT){
            move_main_arm_to_position(main_setpoint);
        }
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
        // GLOBAL_ARM_STATE = ArmStateEnum.Idle;
        actionProgress = 0;
    }

    //  These three methods shouldn't need to be run except inside armPeriodic()

    private void pickupPeriodic() {
        
        if (actionProgress == 0) {

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
         } else {
             end_action(); // possibly move to scoring position?
         }
    }

    public void setMianArmToZero(){
        main_arm_motor_1.set(ControlMode.PercentOutput, 0.0);
    }

    /**
     * This needs to be run constantly to do anything. So teleopPeriodic, autoPeriodic, etc.
     * button array is 
     * 0 = X
     * 1 = Y
     * 2 = A
     * 3 = B
     * 4 = Start
     * 5 = Back
     * 6 = LB
     * 7 = RB
     * 
     */
    public LED_State colorValue = LED_State.Idle;
    public LED_State colorValue2 = LED_State.Idle;
    public void armPeriodic(boolean operator_buttons[], double operator_triggers[], double getLeftY, double getRightY) {

        if (GLOBAL_ARM_STATE == ArmStateEnum.Idle && operator_buttons[6]) {
            GLOBAL_ARM_STATE = ArmStateEnum.Picking_up;
            GLOBAL_OBJECT_STATE = GamePieces.Neither;
            colorValue = LED_State.Idle;
        } else if (GLOBAL_ARM_STATE == ArmStateEnum.Idle && operator_buttons[7]) {
            GLOBAL_ARM_STATE = ArmStateEnum.Scoring;
        } else if (GLOBAL_ARM_STATE == ArmStateEnum.Picking_up && operator_buttons[7]) {
            GLOBAL_ARM_STATE = ArmStateEnum.Idle;
        } else if (GLOBAL_ARM_STATE == ArmStateEnum.Scoring && operator_buttons[6]) {
            GLOBAL_ARM_STATE = ArmStateEnum.Idle;
        }

        if (operator_buttons[4]) {
            GLOBAL_OBJECT_STATE = GamePieces.Cone;
            colorValue = LED_State.Cone;
            GLOBAL_PICK_POSITION = ActivePickPosition.Neither;
        } else if (operator_buttons[5]) {
            GLOBAL_OBJECT_STATE = GamePieces.Cube;
            colorValue = LED_State.Cube;
            GLOBAL_PICK_POSITION = ActivePickPosition.Neither;
        }

        if (GLOBAL_ARM_STATE == ArmStateEnum.Scoring) {
            if (operator_buttons[0] || operator_buttons[3]) {
                GLOBAL_SCORE_POSITION = ActiveScorePosition.Middle;
            } else if (operator_buttons[1]) {
                GLOBAL_SCORE_POSITION = ActiveScorePosition.Top;
            } else if (operator_buttons[2]) {
                GLOBAL_SCORE_POSITION = ActiveScorePosition.Community;
            }
        } else if (GLOBAL_ARM_STATE == ArmStateEnum.Picking_up) {
            if ((operator_buttons[0] || operator_buttons[1] || operator_buttons[3]) && GLOBAL_OBJECT_STATE == GamePieces.Cube) {
                GLOBAL_PICK_POSITION = ActivePickPosition.CubeStation;
            }  else if ((operator_buttons[2]) && GLOBAL_OBJECT_STATE == GamePieces.Cube) {
                GLOBAL_PICK_POSITION = ActivePickPosition.CubeGround;
            }  else if ((operator_buttons[2]) && GLOBAL_OBJECT_STATE == GamePieces.Cone) {
                GLOBAL_PICK_POSITION = ActivePickPosition.ConeGroundSide;
            }  else if ((operator_buttons[0] || operator_buttons[3]) && GLOBAL_OBJECT_STATE == GamePieces.Cone) {
                GLOBAL_PICK_POSITION = ActivePickPosition.ConeGroundUp;
            }  else if ((operator_buttons[1]) && GLOBAL_OBJECT_STATE == GamePieces.Cone) {
                GLOBAL_PICK_POSITION = ActivePickPosition.ConeStation;
            }  
        }

        if (GLOBAL_ARM_STATE == ArmStateEnum.Idle) {
            GLOBAL_SCORE_POSITION = ActiveScorePosition.Neither;
            GLOBAL_PICK_POSITION = ActivePickPosition.Neither;
        } else {
            setIntakeMotor(operator_triggers[1] - operator_triggers[0]);
        }

        if (GLOBAL_ARM_STATE == ArmStateEnum.Idle) {
            colorValue2 = LED_State.Idle;
        } else if (GLOBAL_ARM_STATE == ArmStateEnum.Scoring) {
            colorValue2 = LED_State.ScoreMode;
        } else if (GLOBAL_ARM_STATE == ArmStateEnum.Picking_up) {
            colorValue2 = LED_State.PickMode;
        } else if (GLOBAL_ARM_STATE == ArmStateEnum.error) {
            colorValue2 = LED_State.Error;
        }

        SmartDashboard.putString("Mode", GLOBAL_ARM_STATE.toString());
        SmartDashboard.putString("Object", GLOBAL_OBJECT_STATE.toString());
        SmartDashboard.putString("ScorePos", GLOBAL_SCORE_POSITION.toString());
        SmartDashboard.putString("PickPos", GLOBAL_PICK_POSITION.toString());

        if (GLOBAL_ARM_STATE == ArmStateEnum.Idle){
            move_intake_arm_to_position(Constants.Start_Arm_Position.intake_arm_position, getLeftY);
            if (Math.abs((get_intake_arm_position_selected() - Constants.Start_Arm_Position.intake_arm_position)) < 750){
                if (Math.abs((get_main_arm_position() - Constants.Cone_Ground_Upright_Pickup_Position.main_arm_position)) < 1000){
                    main_arm_motor_1.set(ControlMode.PercentOutput, 0.0);
                }
                else {
                    move_main_arm_to_position(Constants.Start_Arm_Position.main_arm_position);
                }
            }
        }
        else if (GLOBAL_ARM_STATE == ArmStateEnum.Picking_up){
            if (GLOBAL_OBJECT_STATE == GamePieces.Cone){
                if (GLOBAL_PICK_POSITION == ActivePickPosition.ConeGroundUp){
                    move_main_arm_to_position(Constants.Cone_Ground_Upright_Pickup_Position.main_arm_position);
                    //move_main_arm_to_position(Constants.Cone_Ground_Upright_Pickup_Position.main_arm_position, getRightY);
                    if (Math.abs((get_main_arm_position() - Constants.Cone_Ground_Upright_Pickup_Position.main_arm_position)) < 1500){
                        move_intake_arm_to_position(Constants.Cone_Ground_Upright_Pickup_Position.intake_arm_position, getLeftY);

                    }
                }
                else if (GLOBAL_PICK_POSITION == ActivePickPosition.ConeGroundSide){
                    move_main_arm_to_position(Constants.Cone_Ground_Side_Pickup_Position.main_arm_position);
                    //move_main_arm_to_position(Constants.Cone_Ground_Side_Pickup_Position.main_arm_position, getRightY);
                    if (Math.abs((get_main_arm_position() - Constants.Cone_Ground_Side_Pickup_Position.main_arm_position)) < 1500){
                        move_intake_arm_to_position(Constants.Cone_Ground_Side_Pickup_Position.intake_arm_position, getLeftY);

                    }
                }
                else if (GLOBAL_PICK_POSITION == ActivePickPosition.ConeStation){
                    move_intake_arm_to_position(Constants.Cone_Station_Pickup_Position.intake_arm_position, getLeftY);
                    if (Math.abs((get_intake_arm_position_selected() - Constants.Cone_Station_Pickup_Position.intake_arm_position)) < 500){
                        move_main_arm_to_position(Constants.Cone_Station_Pickup_Position.main_arm_position);
                        //move_main_arm_to_position(Constants.Cone_Station_Pickup_Position.main_arm_position, getRightY);
                    }
                }
            }
            else if (GLOBAL_OBJECT_STATE == GamePieces.Cube){
                if (GLOBAL_PICK_POSITION == ActivePickPosition.CubeGround){
                    move_intake_arm_to_position(Constants.Cube_Ground_Pickup_Position.intake_arm_position, getLeftY);
                    if (Math.abs((get_intake_arm_position_selected() - Constants.Cube_Ground_Pickup_Position.intake_arm_position)) < 1000){
                        move_main_arm_to_position(Constants.Cube_Ground_Pickup_Position.main_arm_position);
                        //move_main_arm_to_position(Constants.Cube_Ground_Pickup_Position.main_arm_position, getRightY);
                    }
                }
                else if (GLOBAL_PICK_POSITION == ActivePickPosition.CubeStation){
                    move_intake_arm_to_position(Constants.Cube_Station_Pickup_Position.intake_arm_position, getLeftY);
                    if (Math.abs((get_intake_arm_position_selected() - Constants.Cube_Station_Pickup_Position.intake_arm_position)) < 500){
                        move_main_arm_to_position(Constants.Cube_Station_Pickup_Position.main_arm_position);
                        //move_main_arm_to_position(Constants.Cube_Station_Pickup_Position.main_arm_position, getRightY);
                    }
                }
            }
        }
        else if (GLOBAL_ARM_STATE == ArmStateEnum.Scoring){
            if (GLOBAL_SCORE_POSITION == ActiveScorePosition.Community){
                if (GLOBAL_OBJECT_STATE == GamePieces.Cone){
                    move_main_arm_to_position(Constants.Cone_Community_Score_Position.main_arm_position);
                    //move_main_arm_to_position(Constants.Cone_Community_Score_Position.main_arm_position, getRightY);
                    if (Math.abs((get_main_arm_position() - Constants.Cone_Community_Score_Position.main_arm_position)) < 1500){
                        move_intake_arm_to_position(Constants.Cone_Community_Score_Position.intake_arm_position, getLeftY);

                    }
                }
                else if (GLOBAL_OBJECT_STATE == GamePieces.Cube){
                    move_intake_arm_to_position(Constants.Cube_Community_Score_Position.intake_arm_position, getLeftY);
                    if (Math.abs((get_intake_arm_position_selected() - Constants.Cube_Community_Score_Position.intake_arm_position)) < 500){
                        move_main_arm_to_position(Constants.Cube_Community_Score_Position.main_arm_position);
                        //move_main_arm_to_position(Constants.Cube_Community_Score_Position.main_arm_position, getRightY);
                    }
                }
            }
            else if (GLOBAL_SCORE_POSITION == ActiveScorePosition.Middle){
                if (GLOBAL_OBJECT_STATE == GamePieces.Cone){
                    move_main_arm_to_position(Constants.Cone_Mid_Score_Position.main_arm_position);
                    //move_main_arm_to_position(Constants.Cone_Mid_Score_Position.main_arm_position, getRightY);
                    if (Math.abs((get_main_arm_position() - Constants.Cone_Mid_Score_Position.main_arm_position)) < 1000){
                        SmartDashboard.putString("testing temp cone mid", "works");
                        move_intake_arm_to_position(Constants.Cone_Mid_Score_Position.intake_arm_position, getLeftY);

                    }
                }
                else if (GLOBAL_OBJECT_STATE == GamePieces.Cube){
                    move_main_arm_to_position(Constants.Cube_Mid_Score_Position.main_arm_position);
                    //move_main_arm_to_position(Constants.Cube_Mid_Score_Position.main_arm_position, getRightY);
                    if (Math.abs((get_main_arm_position() - Constants.Cube_Mid_Score_Position.main_arm_position)) < 1000){
                        move_intake_arm_to_position(Constants.Cube_Mid_Score_Position.intake_arm_position, getLeftY);

                    }
                }
            }
            else if (GLOBAL_SCORE_POSITION == ActiveScorePosition.Top){
                if (GLOBAL_OBJECT_STATE == GamePieces.Cone){
                    move_main_arm_to_position(Constants.Cone_Top_Score_Position.main_arm_position);
                    //move_main_arm_to_position(Constants.Cone_Top_Score_Position.main_arm_position, getRightY);
                    if (Math.abs((get_main_arm_position() - Constants.Cone_Top_Score_Position.main_arm_position)) < 1000){
                        move_intake_arm_to_position(Constants.Cone_Top_Score_Position.intake_arm_position, getLeftY);

                    }
                }
                else if (GLOBAL_OBJECT_STATE == GamePieces.Cube){
                    move_main_arm_to_position(Constants.Cube_Top_Score_Position.main_arm_position);
                    //move_main_arm_to_position(Constants.Cube_Top_Score_Position.main_arm_position, getRightY);
                    if (Math.abs((get_main_arm_position() - Constants.Cube_Top_Score_Position.main_arm_position)) < 1000){
                        move_intake_arm_to_position(Constants.Cube_Top_Score_Position.intake_arm_position, getLeftY);

                    }
                }
            }
        }
        else if (GLOBAL_ARM_STATE == ArmStateEnum.error){

        }
        else {

        }

        /*
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

            if (true) { // this if statment needs to be true if we're ready to go onto the next state
                actionProgress ++;
            }
        } else {
            end_action();
        }
        */
    }
}
