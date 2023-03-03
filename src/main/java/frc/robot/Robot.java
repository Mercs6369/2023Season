// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.LED_Signaling.LED_State;
import frc.robot.Vision.gamePiecePipelineIndex;
import frc.robot.Vision.infoTypeToReturn;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {

  public static CTREConfigs ctreConfigs;
  private Command m_autonomousCommand;
  private RobotContainer m_robotContainer;

  //XboxController driver_controller = new XboxController(0);
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
  
  Vision m_vision = new Vision();
  Arm m_arm = new Arm();
  LED_Signaling LEDInstance = new LED_Signaling();
  long lastnano_time = 0;
  Timer m_timeToButtonPress = new Timer();


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
    pickupStatus = pickupStatusEnum.in_progress;
  }

  /**
   * This needs to be run constantly if you want to pickup a game piece. Does not need any parameters.
   */

   private void _RobotEjectPiece() {
    m_arm._Eject_Game_Piece();
  }
  private void _RobotScorePiece() {
    m_arm._Score_Game_Piece();
  }
  private void pickUpPiecePeriodic() {
    autonomousSwerveCommands = m_vision.runAlignmentProcess();
    SmartDashboard.putNumber("Yaw", autonomousSwerveCommands[0]);
    SmartDashboard.putNumber("Area", autonomousSwerveCommands[1]);
    
    double rotation = -1*((0-autonomousSwerveCommands[0])/28);
    rotation = rotation * 1.2;


    if (rotation >= -0.07142857142 && rotation <= 0.07142857142){
      rotation = 0.0;
    } else if (rotation >= 0.07142857142 && rotation <= 0.6) {
      rotation = 0.6;
    } else if (rotation <= -0.07142857142 && rotation >= -0.6) {
      rotation = -0.6;
    } else if (rotation >= 1.2) {
      rotation = 1.2;
    } else if (rotation <= -1.2) {
      rotation = -1.2;
    }

    double swerveYchange; // These 5 variables are being used for calculating how far to move towards the game object.
    double ca = autonomousSwerveCommands[1]; // do not remove these!!! (current area)
    double idealArea = Constants.idealConeArea_Standing;
    double top_speed = Constants.top_speed_mps;
    double increment = top_speed / idealArea;



    SmartDashboard.putNumber("Unofficial status", (ca - idealArea) * increment * -1);


    if ((Constants.idealConeArea_Standing > (ca - 0.5)) && (Constants.idealConeArea_Standing < (ca + 0.5))) {
      swerveYchange = 0.0;
      SmartDashboard.putString("On axis", "true");

    } else {
      swerveYchange = (ca - idealArea) * increment * -1; // this one equation took up like half a whiteboard lmao !!!DO NOT REMOVE THIS!!!
      SmartDashboard.putString("On axis", "false");

    }


    SmartDashboard.putNumber("First", swerveYchange);


    if ((swerveYchange < 0.35) && (swerveYchange >= .11)) { // this is all for the graph line thingy that Coach Dan knows about ask him if things happen
      swerveYchange = 0.35;
    } else if (swerveYchange <= .11) {
      swerveYchange = 0.0;
    }

    swerveYchange = -1 * swerveYchange; // overall invert


    SmartDashboard.putNumber("Rotation For Swerve", rotation);
    SmartDashboard.putNumber("Move (meters per second) For Swerve", swerveYchange);


    if (pickupStatus == pickupStatusEnum.in_progress) {
                   
      SmartDashboard.putString("Pickup Status", "In progress");
      // updateSwerveParameters()
      m_robotContainer.updateSwerveParameters(new Translation2d(0,swerveYchange), rotation, true);
     
    } else {
      SmartDashboard.putString("Pickup Status", "Idle");
    }

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

    ctreConfigs = new CTREConfigs();
    m_robotContainer = new RobotContainer();
    
    robotInitShuffleboard();   // performs robot initialization of Shuffleboard usuage
    m_vision.setGamePiecePipeline(gamePiecePipelineIndex.driver);
        
  }
 

  @Override
  public void robotPeriodic() {
    pickUpPiecePeriodic();
  
    m_arm.armPeriodic();
    m_vision.targeting();

    /* Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
     * commands, running already-scheduled commands, removing finished or interrupted commands,
     * and running subsystem periodic() methods.  This must be called from the robot's periodic
     * block in order for anything in the Command-based framework to work.
     */
    CommandScheduler.getInstance().run();
    SmartDashboard.putNumber("Vertical elevator_position", m_arm.getVerticalElevatorPosition());
    SmartDashboard.putNumber("Horizontal elevator_position", m_arm.getHorizontalElevatorPosition());

    /*
    SmartDashboard.putNumber("Estimated Cone Node Distance", m_vision.getDistanceLowerConeNode(NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0),32.1875));
    SmartDashboard.putNumber("Game Piece Area", m_vision.getConeInfo(infoTypeToReturn.Area));
    SmartDashboard.putNumber("Game Piece Yaw", m_vision.getConeInfo(infoTypeToReturn.Yaw));
    SmartDashboard.putNumber("Game Piece Orientation", m_vision.getConeInfo(infoTypeToReturn.Orientation));
    SmartDashboard.putNumber("position (y)", m_vision.getY());
    SmartDashboard.putNumber("position (x)", m_vision.getX());
    SmartDashboard.putNumber("yaw angle", m_vision.getYaw());
    SmartDashboard.putNumber("AT photonvision yaw", m_vision.target.getBestCameraToTarget().getRotation().getAngle());

    SmartDashboard.putNumber("Fid ID 7 Pos X", m_vision.getTag(7).getX());
    SmartDashboard.putNumber("Fid ID 7 Pos Y", m_vision.getTag(7).getY());
    SmartDashboard.putNumber("Fid ID 7 Yaw", m_vision.getTag(7).getRotation().getAngle());
    */
    getControllerStates();    // reads all controller inputs


      //m_vision.CS_RGB_measure(); // tests rev color sensor
      //m_vision.CS_Prox_measure(); // tests rev color sensor
      //SmartDashboard.putString("Object Detection Output", m_vision.m_color_sensor.color_string);

    double scale = 1.0;
    
    /*
    {
      // There should be a method that scores the object
      double targetDistanceX = 1.0;
      double targetDistanceY = 0;
      double tol = 0.05;

      double xValue = 0.0;
      double yVaule = 0.0;
      double rotationValue = 0.0;

      if (m_vision.getXm() - targetDistanceX < 0.25){
        scale = 0.75;
      }
      else if (m_vision.getX() - targetDistanceX < 0.5){
        scale = 0.50;
      }
      else {
        scale = 1;
      }

      //scale = m_vision.getXm() - targetDistanceX;

      if ((Math.abs(m_vision.getZ()) - targetDistanceY) < 1.5){
        if ((Math.abs(m_vision.getXm() - targetDistanceX)) < 0.15){
          yVaule = 0.0;
          if (m_vision.getYm() < targetDistanceY + 0.1){
            xValue = -0.75;
          }
          else if (m_vision.getYm() > targetDistanceY - 0.1){
            xValue = 0.75;
          }
        }
        else {
  
          if (m_vision.getXm() > targetDistanceX + 0.1){
      
            yVaule = -0.75;
    
          }
          else if (m_vision.getXm() < targetDistanceX + 0.1){
            yVaule = 0.75;
          }

        }
      }   
      else {
        if ((m_vision.getZ()) > targetDistanceY + 1.5){
          rotationValue = -0.50;
        }
        else if (m_vision.getZ() < targetDistanceY - 1.5){
          rotationValue = 0.50;
        }
        else{
          rotationValue = 0.0;
        }
      }

      /*
      if ((Math.abs(m_vision.getXm() - targetDistanceX)) < 0.15){
        yVaule = 0.0;
        if (Math.abs(m_vision.getYm()) < 0.1){
          xValue = 0.0;
          if ((m_vision.getZ()) > targetDistanceY + 1.5){
            rotationValue = -0.50;
          }
          else if (m_vision.getZ() < targetDistanceY - 1.5){
            rotationValue = 0.50;
          }
          else{
            rotationValue = 0.0;
          }
        }
        else {
          if (m_vision.getYm() < targetDistanceY + 0.1){
            xValue = -0.75;
          }
          else if (m_vision.getYm() > targetDistanceY - 0.1){
            xValue = 0.75;
          }
        }

      }
      
      else {
        if (m_vision.getXm() > targetDistanceX + 0.1){
      
          yVaule = -0.75;
  
        }
        else if (m_vision.getXm() < targetDistanceX + 0.1){
          yVaule = 0.75;
        }
      }     
      
      m_robotContainer.updateSwerveParameters(new Translation2d(xValue*scale, yVaule*scale), rotationValue, true);

    }
    
    else {
      m_robotContainer.updateSwerveParameters(new Translation2d(0.0, 0.0), 0.0, false);

    }

    SmartDashboard.putNumber("auto X", m_vision.getXm());
    SmartDashboard.putNumber("auto Y", m_vision.getYm());
    SmartDashboard.putNumber("scale", scale);
    SmartDashboard.putNumber("z", m_vision.getZ());


    /*Maybe we should add the next two methods in the far future
    if(canAutoPickObj() == true)
    {
      driver_controller.setRumble(null, .5);
    }
    this method should make the contoller rumble if a game object can be autonomous intaked
    if(canAutoScoreObj() == true)
    { 
      driver_controller.setRumble(null, .5);
    }
    this method should make the contoller rumble if a game object can be autonomous scored
    */

   }

  @Override
  public void autonomousInit() {

    autoInitShuffleboard();  // performs autonomous initialization of Shuffleboard usuage

  }

  @Override
  public void autonomousPeriodic() {

  }

  @Override
  public void teleopInit() {

  }

  @Override
  public void teleopPeriodic() {
    if (Math.abs(driver_controller_L_X_Axis) <= 0.1){
      driver_controller_L_X_Axis = 0;
    }
    if (Math.abs(driver_controller_L_Y_Axis) <= 0.1){
      driver_controller_L_Y_Axis = 0;
    }
    if (Math.abs(driver_controller_R_X_Axis) <= 0.1){
      driver_controller_R_X_Axis = 0;
    }

    if (brake_mode_enabled == true){
      //m_drive.brake();
    }
    else {
      //m_drive.drive(new Translation2d(5*driver_controller_L_X_Axis, 5*driver_controller_L_Y_Axis), 1.6*driver_controller_R_X_Axis, false);
    }

    if(operator_controller_A_button == true)
    {
      m_arm.setIntakeMotor1(0.5);
      SmartDashboard.putString("Button State","A pressed");
      
    }
    else if(operator_controller_X_button == true)
    { 
      m_arm.setIntakeMotor1(-0.5);
    }
    else
    {
      m_arm.setIntakeMotor1(0.0);
      SmartDashboard.putString("Button State","A not pressed");
    }

    if(operator_controller_B_button == true)
    {
      m_arm.setIntakeMotor2(0.5);
    }
    else if(operator_controller_Y_button == true) {
      m_arm.setIntakeMotor2(-.5);
    }
    else {
      m_arm.setIntakeMotor2(0.0);
      pickupStatus = pickupStatusEnum.idle;
    }

    m_arm.move_vertical_elevator(-1*operator_controller.getRightY());
    m_arm.move_horizontal_elevator(-1*operator_controller.getRightX());
  }

  @Override
  public void disabledInit() {}

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
      m_alliance.setDefaultOption("Blue", kBlue);
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
}
