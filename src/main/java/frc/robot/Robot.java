// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.ArmStateEnum;
import frc.robot.LED_Signaling.LED_State;
import frc.robot.Vision.gamePiecePipelineIndex;
import frc.robot.Vision.infoTypeToReturn;
import frc.robot.commands.TeleopSwerve;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import com.ctre.phoenix.sensors.Pigeon2;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {

  public static CTREConfigs ctreConfigs;
  private Command m_autonomousCommand;
  private RobotContainer m_robotContainer;
  public Pigeon2 gyro;


  XboxController driver_Controller = new XboxController(0);
  XboxController operator_controller = new XboxController(1);
// maybe put these variables in the constants class? Yes, constants being worked by Gargi, still need these though
  boolean operator_controller_A_button;
  boolean operator_controller_Y_button;
  boolean operator_controller_X_button;
  boolean operator_controller_B_button;
  boolean brake_mode_enabled = false; // Controls whether robot is in brake mode or not
  double driver_controller_L_X_Axis;
  double driver_controller_L_Y_Axis;
  double driver_controller_R_X_Axis;
  double driver_controller_R_Y_Axis;
  int driver_controller_POV_button;
  double speedScale = 0.85;
  double max_speed_limiter = 0.1;  // 10% of the maximum translation velocity and angular velocity
  int autoStage = 3;
  double initialGyroValue = 0.0;
  double initialDistanceY = 0.0;

  boolean[] operator_buttons = {false, false, false, false, false, false, false, false};
  double[] operator_triggers = new double[2];
  
  Vision m_vision = new Vision();
  LED_Signaling LEDInstance = new LED_Signaling(9);
  LED_Signaling LEDInstance2 = new LED_Signaling(8);
  long lastnano_time = 0;
  Timer m_timeToButtonPress = new Timer();
  Timer chargedStationTimer = new Timer();
  Timer moveAutoTimer = new Timer();
  Timer autoIntake = new Timer();
  Arm m_arm = new Arm();

  double[] autonomousSwerveCommands = {0,0,0};

  // Shuffleboard: Declares variables associated with Alliance Selection
  private final SendableChooser<String> m_alliance = new SendableChooser<>();
  private static final String kRed = "Red";
  private static final String kBlue = "Blue";
  private String m_allianceSelected;
  // Shuffleboard: Declares variables associated with Idle Selection
  private final SendableChooser<String> m_idle = new SendableChooser<>();
  private static final String kNotIdle = "No";
  private static final String kIsIdle = "Yes";
  private String m_idleSelected;
  // Shuffleboard: Declares variables associated with Position Selection
  private final SendableChooser<String> m_position = new SendableChooser<>();
  private static final String kCenter = "Center";
  private static final String kLeft = "Left";
  private static final String kRight = "Right";
  private String m_positionSelected;
  // Shuffleboard: Declares variables associated with Scoring Node
  private final SendableChooser<String>  m_scoringNode= new SendableChooser<>();
  private static final String kHigh = "High";
  private static final String kMedium = "Medium";
  private static final String kHybrid = "Hybrid";
  private String m_scoringNodeSelected;
  // Shuffleboard: Declares variables associated with Leaving Community
  private final SendableChooser<String>  m_leavingCommunity = new SendableChooser<>();
  private static final String kIsLeaving = "Yes";
  private static final String kNotLeaving = "No";
  private String m_leavingCommunitySelected;
  // Shuffleboard: Declares variables associated with Delay
  private final SendableChooser<String>  m_delay = new SendableChooser<>();
  private static final String kNoDelay = "No Delay";
  private static final String kOneSecond = "One Second";
  private static final String kThreeSeconds = "Three Seconds";
  private static final String kFiveSeconds = "Five Seconds";
  private String m_delaySelected;

/*
   * 
   *
   * 
   */
  enum pickupStatusEnum {
    in_progress,
    finished,
    idle,
  }
  pickupStatusEnum pickupStatus = pickupStatusEnum.idle;

  /**
   * This needs to be run whenever the button (assigned to picking up a piece) is pressed. You don't have to worry about adding a debounce to this, it's all handled internally :P
   */

  private void _RobotPickUpPiece() {
    autonomousSwerveCommands = m_vision.runAlignmentProcess();
    SmartDashboard.putNumber("Yaw", autonomousSwerveCommands[0]);
    SmartDashboard.putNumber("Pitch", autonomousSwerveCommands[1]);
    
    double rotation = (autonomousSwerveCommands[0])/28;

    double swerveYchange = autonomousSwerveCommands[1]/2; // These 5 variables are being used for calculating how far to move towards the game object.
/*
    double ca = autonomousSwerveCommands[1]; // do not remove these!!! (current area)
    double idealArea = Constants.idealConeArea_Standing;
    double top_speed = Constants.top_speed_mps;
    double increment = top_speed / idealArea;
*/
    //swerveYchange = (ca - idealArea) * increment * -1; // this one equation took up like half a whiteboard lmao !!!DO NOT REMOVE THIS!!!

    /* 
    if ((swerveYchange < 0.35) && (swerveYchange >= .11)) { // this is all for the graph line thingy that Coach Dan knows about ask him if things happen
      swerveYchange = 0.35;
    } else if (swerveYchange <= .11) {
      swerveYchange = 0.0;
    }
    */
    SmartDashboard.putNumber("rotation", rotation);
    SmartDashboard.putNumber("swerveYchange", swerveYchange);
    m_robotContainer.updateSwerveParameters(new Translation2d(0,swerveYchange), rotation, true);
  

  }
  /*
   * 
   * 
   * 
   * 
   * 
   * 
   */

  @Override
  public void robotInit() {

    //m_vision.setGamePiecePipeline(gamePiecePipelineIndex.driver);
    ctreConfigs = new CTREConfigs();
    m_robotContainer = new RobotContainer();
    robotInitShuffleboard();

    gyro = new Pigeon2(Constants.Swerve.pigeonID);
    gyro.configFactoryDefault();
    zeroGyro();

    final double xRoll = gyro.getRoll();
        
  }
 
  boolean gonebackwards = false;

  @Override
  public void robotPeriodic() {
    LEDInstance.SetLEDS(m_arm.colorValue);
    LEDInstance2.SetLEDS(m_arm.colorValue2);
    
    CommandScheduler.getInstance().run();

    SmartDashboard.putNumber("SwerveDistanceX", getSwerveDistanceX());
    SmartDashboard.putNumber("SwerveDistanceY", getSwerveDistanceY());
    SmartDashboard.putNumber("Roll", gyro.getRoll());
    SmartDashboard.putNumber("Pitch", gyro.getPitch());
    SmartDashboard.putNumber("Yaw", gyro.getYaw());

    SmartDashboard.putNumber("Main Arm Position", m_arm.get_main_arm_position());
    SmartDashboard.putNumber("Intake Arm Position", m_arm.get_intake_arm_position_selected());
    SmartDashboard.putNumber("Main Arm Position Throughbore", m_arm.get_main_arm_position_throughbore());


    getControllerStates();


    if (driver_Controller.getPOV() == 0){
      m_robotContainer.updateSwerveParameters(new Translation2d(0, 2), 0, true);
    }
    else if (driver_Controller.getPOV() == 90){
      m_robotContainer.updateSwerveParameters(new Translation2d(2, 0), 0, true);

    }
    else if (driver_Controller.getPOV() == 270){
      m_robotContainer.updateSwerveParameters(new Translation2d(-2, 0), 0, true);

    }
    else if (driver_Controller.getPOV() == 180){
      m_robotContainer.updateSwerveParameters(new Translation2d(0, -2), 0, true);

    }
    else {
      m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);

    }

    /* if (operator_controller.getRawButton(1) == true){
      m_arm.setIntakeMotor(0.9);
    }
    else if (operator_controller.getRawButton(3) == true) {
      m_arm.setIntakeMotor(-0.9);
    }
    else {
      m_arm.setIntakeMotor(0.0);
    } */


  

   }

  @Override
  public void autonomousInit() {
    autoInitShuffleboard();
    zeroGyro();
    initialGyroValue = gyro.getRoll();
    initialDistanceY = getSwerveDistanceY();
    //m_robotContainer = new RobotContainer(); //WHY IS THIS HERE... ITS ALREADY RUN ONCE UNDER INIT
        /*
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
    */
    


  }

  @Override
  public void autonomousPeriodic() {
    //autoBackup();
    //autoDefault();
    //autoDistanceTest();
    autoTest(); //balance 

  }

  @Override
  public void teleopInit() {
    
  }

  @Override
  public void teleopPeriodic() {
    m_arm.armPeriodic(operator_buttons, operator_triggers, operator_controller.getLeftY(), operator_controller.getRightY());
    /*
    if (m_arm.GLOBAL_ARM_STATE == ArmStateEnum.Picking_up || m_arm.GLOBAL_ARM_STATE == ArmStateEnum.Scoring) {
      m_robotContainer.updateSwerveParameters(new Translation2d(Constants.Swerve.maxSpeed/2 * -driver_Controller.getLeftX(), 
                                                                Constants.Swerve.maxSpeed/2 * driver_Controller.getLeftY()),
                                                                Constants.Swerve.maxSpeed/2 * -driver_Controller.getRightX(), true);
    } 
    else {
      m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
    }*/
    if (driver_Controller.getAButton() == true) {
      _RobotPickUpPiece();
    } else {
     m_robotContainer.updateSwerveParameters(new Translation2d(0,0), 0, false);
    }
  }

  @Override
  public void disabledInit() {
    LEDInstance.SetLEDS(LED_State.Off);
    LEDInstance2.SetLEDS(LED_State.Off);
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  public void getControllerStates() {
    operator_controller_A_button = operator_controller.getAButton();
    operator_controller_B_button = operator_controller.getBButton();
    operator_controller_X_button = operator_controller.getXButton();
    operator_controller_Y_button = operator_controller.getYButton();
    operator_buttons[0] = operator_controller_X_button;
    operator_buttons[1] = operator_controller_Y_button;
    operator_buttons[2] = operator_controller_A_button;
    operator_buttons[3] = operator_controller_B_button;
    operator_buttons[4] = operator_controller.getStartButton();
    operator_buttons[5] = operator_controller.getBackButton();
    operator_buttons[6] = operator_controller.getLeftBumperReleased();
    operator_buttons[7] = operator_controller.getRightBumperReleased();
    operator_triggers[0] = operator_controller.getLeftTriggerAxis();
    operator_triggers[1] = operator_controller.getRightTriggerAxis();
    // brake_mode_enabled = driver_controller.getStartButton() && driver_controller.getBackButton();
    int m_buttonPressCount = 0;
    double obtainedButtonTime = 0;
    if(operator_controller.getStartButton() && operator_controller.getBackButton()) {
      m_buttonPressCount++;
      if(m_buttonPressCount == 1) {
        m_timeToButtonPress.start();
      }
      obtainedButtonTime = m_timeToButtonPress.get();
    }
      if(obtainedButtonTime > 0.5) {
        brake_mode_enabled = !brake_mode_enabled;
        m_timeToButtonPress.stop();
        m_timeToButtonPress.reset();
    }
  }
  
  public void robotInitShuffleboard() {
      // Shuffleboard: Passes options "Red" and "Blue" for Alliance
      m_alliance.setDefaultOption("Red", kRed);
      m_alliance.addOption("Blue", kBlue);
      SmartDashboard.putData("Alliance", m_alliance);
      // Shuffleboard: Passes options "Yes" and "No" for Idle
      m_idle.setDefaultOption("No", kNotIdle);
      m_idle.addOption("Yes", kIsIdle);
      SmartDashboard.putData("Idle", m_idle);
      // Shuffleboard: Passes options "Center", "Left", and "Right" for Position
      m_position.setDefaultOption("Center", kCenter);
      m_position.addOption("Left", kLeft);
      m_position.addOption("Right", kRight);
      SmartDashboard.putData("Position", m_position);
      // Shuffleboard: Passes options "High", "Medium", and "Hybrid" for Scoring Node
      m_scoringNode.setDefaultOption("High", kHigh);
      m_scoringNode.addOption("Medium", kMedium);
      m_scoringNode.addOption("Hybrid", kHybrid);
      SmartDashboard.putData("Scoring Node", m_scoringNode);
      // Shuffleboard: Passes options "Yes" and "No" for Leaving Community
      m_leavingCommunity.setDefaultOption("Yes", kIsLeaving);
      m_leavingCommunity.addOption("No", kNotLeaving);
      SmartDashboard.putData("Leaving Community", m_leavingCommunity);
      // Shuffleboard: Passes options "No Delay", "One Second", "Three Seconds", and "Five Seconds" for Delay
      m_delay.setDefaultOption("No Delay", kNoDelay);
      m_delay.addOption("One Second", kOneSecond);
      m_delay.addOption("Three Seconds", kThreeSeconds);
      m_delay.addOption("Five Seconds", kFiveSeconds);
      SmartDashboard.putData("Delay", m_delay);
  }

  public void autoInitShuffleboard() {
    // Shuffleboard: Sets Alliance Selection to variable and prints to console
    m_allianceSelected = m_alliance.getSelected();
    System.out.println("Alliance Selected: " + m_allianceSelected);
    // Shuffleboard: Sets Idle Selection to variable and prints to console
    m_idleSelected = m_idle.getSelected();
    System.out.println("Idle Selected: " + m_idleSelected);
    // Shuffleboard: Sets Position Selection to variable
    m_positionSelected = m_position.getSelected();
    System.out.println("Position Selected: " + m_positionSelected);
    // Shuffleboard: Sets Scoring Node Selection to variable and prints to console
    m_scoringNodeSelected = m_scoringNode.getSelected();
    System.out.println("Scoring Node Selected: " + m_scoringNodeSelected);
    // Shuffleboard: Sets Leaving Community Selection to variable and prints to console
    m_leavingCommunitySelected = m_leavingCommunity.getSelected();
    System.out.println("Leaving Community Selected: " + m_leavingCommunitySelected);
    // Shuffleboard: Sets Delay Selection to variable and prints to console
    m_delaySelected = m_delay.getSelected();
    System.out.println("Delay Selected: " + m_delaySelected);
  }

  public double getSwerveDistanceX(){
    return m_robotContainer.s_Swerve.swerveOdometry.getPoseMeters().getX();
  }
  public double getSwerveDistanceY(){
    return m_robotContainer.s_Swerve.swerveOdometry.getPoseMeters().getY();
  }

  public void zeroGyro(){
    gyro.setYaw(0);
}

  public Rotation2d getYaw() {
      return (Constants.Swerve.invertGyro) ? Rotation2d.fromDegrees(360 - gyro.getYaw()) : Rotation2d.fromDegrees(gyro.getYaw());
  }

  public void autoBalance(double initialGyroValue){
    chargedStationTimer.start();
    String xy = "";
    // if (chargedStationTimer.get() < 5){
    //   m_robotContainer.updateSwerveParameters(new Translation2d(0, -0.7), 0, true);
    //   xy = "timer";

    // }
    if ((gyro.getRoll()) > initialGyroValue + 1.5){
      if (gonebackwards == false){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0.6), 0, true);
        xy = "forward fast";

      }
      else {
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0.3), 0, true);
        xy = "forward slow";
      }

    }
    else if ((gyro.getRoll()) < initialGyroValue - 1.5){
      m_robotContainer.updateSwerveParameters(new Translation2d(0, -0.55), 0, true);
      xy = "backwards";
      gonebackwards = true;
    }
    else {
      m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
      xy = "level";

    }

    SmartDashboard.putString("Status", xy);
  }

  public void autoMove(){
    moveAutoTimer.start();
    if (moveAutoTimer.get() < 7){
      m_robotContainer.updateSwerveParameters(new Translation2d(0, -1), 0, true);
    }
    else {
      m_robotContainer.updateSwerveParameters(new Translation2d(0, -0.25), 0, true);

    }
  }

  public void autoDefault(){
    if (autoStage == 0){
      m_arm.setIntakeMotor(0.1);
      m_arm.move_main_arm_to_position(Constants.Cube_Mid_Score_Position.main_arm_position);
      if (Math.abs((m_arm.get_main_arm_position() - Constants.Cube_Mid_Score_Position.main_arm_position)) < 1000){
        m_arm.move_intake_arm_to_position(Constants.Cube_Mid_Score_Position.intake_arm_position);
        if ((Math.abs(m_arm.get_intake_arm_position_selected() - Constants.Cube_Mid_Score_Position.intake_arm_position) < 1000)){
          autoStage = 1;
        }
      }
    }
    else if (autoStage == 1){
      autoIntake.start();
      if (autoIntake.get() < 1.5){
        m_arm.setIntakeMotor(-1);
      }
      else {
        m_arm.setIntakeMotor(0);
        autoStage = 2;
      }
    }
    else if (autoStage == 2){
      m_arm.move_intake_arm_to_position(Constants.Start_Arm_Position.intake_arm_position);
      if (Math.abs((m_arm.get_intake_arm_position_selected() - Constants.Start_Arm_Position.intake_arm_position)) < 750){
          if (Math.abs((m_arm.get_main_arm_position() - Constants.Start_Arm_Position.main_arm_position)) < 1500){
            m_arm.setMianArmToZero();
            autoStage = 3;
          }
          else {
            m_arm.move_main_arm_to_position(Constants.Start_Arm_Position.main_arm_position);
          }
      }
    }
    else if (autoStage == 3){
      if ((106.75 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) > 0.25){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, -1), 0, true);
      }
      else if ((106.75 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
        autoStage = 4;
      }
    }
    else if (autoStage == 4){ // note to self
      if (getSwerveDistanceY() > 96.75){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0.7), 0, true);
      }
      else {
        autoBalance(initialGyroValue);  
      }
    }

  }

  public void autoBackup(){
    if (autoStage == 0){
      m_arm.setIntakeMotor(0.1);
      m_arm.move_main_arm_to_position(Constants.Cube_Mid_Score_Position.main_arm_position);
      if (Math.abs((m_arm.get_main_arm_position() - Constants.Cube_Mid_Score_Position.main_arm_position)) < 1000){
        m_arm.move_intake_arm_to_position(Constants.Cube_Mid_Score_Position.intake_arm_position);
        if ((Math.abs(m_arm.get_intake_arm_position_selected() - Constants.Cube_Mid_Score_Position.intake_arm_position) < 1000)){
          autoStage = 1;
        }
      }
    }
    else if (autoStage == 1){
      autoIntake.start();
      if (autoIntake.get() < 1.5){
        m_arm.setIntakeMotor(-1);
      }
      else {
        m_arm.setIntakeMotor(0);
        autoStage = 2;
      }
    }
    else if (autoStage == 2){
      m_arm.move_intake_arm_to_position(Constants.Start_Arm_Position.intake_arm_position);
      if (Math.abs((m_arm.get_intake_arm_position_selected() - Constants.Start_Arm_Position.intake_arm_position)) < 750){
          if (Math.abs((m_arm.get_main_arm_position() - Constants.Start_Arm_Position.main_arm_position)) < 1500){
            m_arm.setMianArmToZero();
            autoStage = 3;
          }
          else {
            m_arm.move_main_arm_to_position(Constants.Start_Arm_Position.main_arm_position);
          }
      }
    }
    else if (autoStage == 3){
      autoBalance(initialGyroValue);  
      //autoMove();
    }
  }

  public void autoDistanceTest(){
    if (autoStage == 0){
      m_arm.setIntakeMotor(0.1);
      m_arm.move_main_arm_to_position(Constants.Cube_Mid_Score_Position.main_arm_position);
      if (Math.abs((m_arm.get_main_arm_position() - Constants.Cube_Mid_Score_Position.main_arm_position)) < 1000){
        m_arm.move_intake_arm_to_position(Constants.Cube_Mid_Score_Position.intake_arm_position);
        if ((Math.abs(m_arm.get_intake_arm_position_selected() - Constants.Cube_Mid_Score_Position.intake_arm_position) < 1000)){
          autoStage = 1;
        }
      }
    }
    else if (autoStage == 1){
      autoIntake.start();
      if (autoIntake.get() < 1.5){
        m_arm.setIntakeMotor(-1);
      }
      else {
        m_arm.setIntakeMotor(0);
        autoStage = 2;
      }
    }
    else if (autoStage == 2){
      m_arm.move_intake_arm_to_position(Constants.Start_Arm_Position.intake_arm_position);
      if (Math.abs((m_arm.get_intake_arm_position_selected() - Constants.Start_Arm_Position.intake_arm_position)) < 750){
          if (Math.abs((m_arm.get_main_arm_position() - Constants.Start_Arm_Position.main_arm_position)) < 1500){
            m_arm.setMianArmToZero();
            autoStage = 3;
          }
          else {
            m_arm.move_main_arm_to_position(Constants.Start_Arm_Position.main_arm_position);
          }
      }
    }
    else if (autoStage == 3){
      //autoBalance(initialGyroValue);  
      //autoMove();
      //229.623151
      //72.56158302 this is how much the robot needs to travel to balance in in
      //21.603 this is how much the robot needs to travel sideways to get 

      //180 for cube pickup
      //81 for balance
      if ((12) - (-1*getSwerveDistanceY()) > 0.05){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, -1.75), -gyro.getYaw()/30, true);
      }
      // else if ((229.623151 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
      //   m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
      //   autoStage = 4;
      // }
      else {
        //autoBalance(initialGyroValue);
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
        autoStage = 4;
      }
    }
    else if (autoStage == 4){
      if ((6) - (getSwerveDistanceX()) > 0.05){
        m_robotContainer.updateSwerveParameters(new Translation2d(1, 0), -gyro.getYaw()/30, true);
      }
      else {
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
        autoStage = 5;
      }
    }
    else if (autoStage == 5){
      if ((150+12) - (-1*getSwerveDistanceY()) > 0.1){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, -2.5), -gyro.getYaw()/30, true);
      }
      else {
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
        autoStage = 7;
      }
    }
    else if (autoStage == 6){
      if ((0) + (getSwerveDistanceX()) > 0.05){
        m_robotContainer.updateSwerveParameters(new Translation2d(-1, 0), -gyro.getYaw()/30, true);
      }
      else {
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
        //autoStage = 6;12
      }
    }
    else if (autoStage == 7){
      if ((12) - (-1*getSwerveDistanceY()) < 0.1){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 2.5), -gyro.getYaw()/30, true);
      }
      else {
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
        autoStage = 6;
      }
    }
    else if (autoStage == 8){
      if ((10) - (-1*getSwerveDistanceY()) < 0.05){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 2.5), -gyro.getYaw()/30, true);
      }
      // else if ((229.623151 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
      //   m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
      //   autoStage = 4;
      // }
      else {
        //autoBalance(initialGyroValue);
        //m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);

    }

  }
}

public void autoTest(){
  if (autoStage == 0){
    m_arm.setIntakeMotor(0.1);
    m_arm.move_main_arm_to_position(Constants.Cube_Mid_Score_Position.main_arm_position);
    if (Math.abs((m_arm.get_main_arm_position() - Constants.Cube_Mid_Score_Position.main_arm_position)) < 1000){
      m_arm.move_intake_arm_to_position(Constants.Cube_Mid_Score_Position.intake_arm_position);
      if ((Math.abs(m_arm.get_intake_arm_position_selected() - Constants.Cube_Mid_Score_Position.intake_arm_position) < 1000)){
        autoStage = 1;
      }
    }
  }
  else if (autoStage == 1){
    autoIntake.start();
    if (autoIntake.get() < 1.5){
      m_arm.setIntakeMotor(-1);
    }
    else {
      m_arm.setIntakeMotor(0);
      autoStage = 2;
    }
  }
  else if (autoStage == 2){
    m_arm.move_intake_arm_to_position(Constants.Start_Arm_Position.intake_arm_position);
    if (Math.abs((m_arm.get_intake_arm_position_selected() - Constants.Start_Arm_Position.intake_arm_position)) < 750){
        if (Math.abs((m_arm.get_main_arm_position() - Constants.Start_Arm_Position.main_arm_position)) < 1500){
          m_arm.setMianArmToZero();
          autoStage = 3;
        }
        else {
          m_arm.move_main_arm_to_position(Constants.Start_Arm_Position.main_arm_position);
        }
    }
  }
  else if (autoStage == 3){
    //autoBalance(initialGyroValue);  
    //autoMove();
    //229.623151
    //72.56158302 this is how much the robot needs to travel to balance in in
    //21.603 this is how much the robot needs to travel sideways to get 

    //180 for cube pickup
    //81 for balance
    if ((173) - (-1*getSwerveDistanceY()) > 0.05){
      m_robotContainer.updateSwerveParameters(new Translation2d(0, -1.75), -gyro.getYaw()/30, true);
    }
    // else if ((229.623151 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
    //   m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
    //   autoStage = 4;
    // }
    else {
      //autoBalance(initialGyroValue);
      m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);
      autoStage = 4;
    }
  } else if (autoStage == 4){
      if ((100) - (-1*getSwerveDistanceY()) < 0.05){
        m_robotContainer.updateSwerveParameters(new Translation2d(0, 1.75), -gyro.getYaw()/30, true);
      }
      // else if ((229.623151 - (-1*initialDistanceY)) - (-1*getSwerveDistanceY()) <= 0.25){
      //   m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, false);
      //   autoStage = 4;
      // }
      else {
        autoBalance(initialGyroValue);
        //m_robotContainer.updateSwerveParameters(new Translation2d(0, 0), 0, true);

      }

    }
  }

  /*
   * 
   * 
   * Jon's Auton
   * 
   * 
   */



  double xVelocity = 0;
  double yVelocity = 0;
  double xDestination = 0;
  double yDestination = 0;

  //double[] path = 
  //{0,50,
  //  20,50,
  //  20,40,
  //  0,40,
  //  0,-20,
  //  0,0}; -- this defines the path using X and Y coordinates (inches). This would first make the robot go 50 inches forward, and 0 inches left and right, then ten inches back and 20 inches left/right, and so on. It IS relative to the robot's starting position.
  //runPath(path); -- this would run the path seen above, and will return true if it's finished. It does need to be run constantly.
  //movePeriodic; -- this needs to be run constantly, so that it actually updates swerve paramaters.
  // if you're not pressing a button/the robot shouldn't be moving, I would recommend setting these variables:
  //
  //xVelocity = 0;
  //Velocity = 0;
  //xDestination = 0;
  //yDestination = 0;
  //stage = 0; (this might need to be set to something either really low (like zero) or something really high (like 500))
  //
  //
  


  public void moveTo(double x, double y) { //runPath is basically just a bunch of these moveTo methods looped together.
    xVelocity = 0;
    yVelocity = 0;
    xDestination = x;
    yDestination = y;
    SmartDashboard.putNumber("zAttempted Position Y", y);
    SmartDashboard.putNumber("zAttempted Position X", x);


    if (yDestination > getSwerveDistanceY()) {
      yVelocity = 0.7;
    } else {
      yVelocity = -0.7;
    }

    if (xDestination > getSwerveDistanceX()) {
      xVelocity = 0.6;
    } else {
      xVelocity = -0.6;
    }
  }







  public boolean movePeriodic() {
    SmartDashboard.putNumber("VelocityX - Before", xVelocity);
    SmartDashboard.putNumber("VelocityY - Before", yVelocity);

    if (Math.abs(yDestination - getSwerveDistanceY()) <= 6) { // see below
      yVelocity = 0;
    }
    if (Math.abs(xDestination - getSwerveDistanceX()) <= 6) { // this number (6) can/should be changed, if the robot is within 6 inches of it's destination, it will stop. I think it should work if you were to set it to 1
      xVelocity = 0;
    }

    SmartDashboard.putNumber("DestinationX", xDestination);
    SmartDashboard.putNumber("DestinationY", yDestination);
    SmartDashboard.putNumber("VelocityX - After", xVelocity);
    SmartDashboard.putNumber("VelocityY - After", yVelocity);
    if (xVelocity == 0 && yVelocity == 0) {
      return true;
    } else {
      
      m_robotContainer.updateSwerveParameters(new Translation2d(xVelocity, yVelocity), 0, true);
      return false;
    }

  }




  int stage = 0;
  boolean hasDashboarded = false;
  boolean hasCompletedStage = false;

  public void runPath(double... values) {
    SmartDashboard.putNumber("Stage", stage);
    if (stage < values.length) {
      SmartDashboard.putString("Under length", "yessss");

     
      moveTo(values[stage], values[stage+1]);

      if (movePeriodic() == true) { // completed current moveTo

        if (stage < values.length) {
          

          hasDashboarded = true;
          stage = stage + 2;
          hasCompletedStage = true;

          //xDestination = values[2];
          //yDestination = values[3];
        } else {
          // finisheddddddddddddd
          SmartDashboard.putString("Finished", "yay we finished lets go #poppingoff #letsgooo #blessed");
        }
        
      }
    } else {
      xDestination = getSwerveDistanceX();
      yDestination = getSwerveDistanceY();
      SmartDashboard.putString("Under length", "nooooo");
    }
  }






















  public void runBackwards(double... values) {
    SmartDashboard.putNumber("Stage", stage);
    if (stage < values.length) {
      SmartDashboard.putString("Under length", "yessss");

      moveTo(values[values.length - stage - 2], values[values.length - stage - 1]);

      if (movePeriodic() == true) { // completed current moveTo

        if (stage < values.length) {
          

          hasDashboarded = true;
          stage = stage + 2;
          hasCompletedStage = true;

          //xDestination = values[2];
          //yDestination = values[3];
        } else {
          // finisheddddddddddddd
          SmartDashboard.putString("Finished", "yay we finished lets go #poppingoff #letsgooo #blessed");
        }
        
      }
    } else {
      xDestination = getSwerveDistanceX();
      yDestination = getSwerveDistanceY();
      SmartDashboard.putString("Under length", "nooooo");
    }
  }
}
