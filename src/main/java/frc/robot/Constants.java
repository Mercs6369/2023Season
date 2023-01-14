package frc.robot;

public class Constants {

    /* IDS */

    public static final int PIGEON_ID = 0;
    public static final int INTAKE_MOTOR_1_ID = 1;
    public static final int CLAW_MOTOR_ID = 16;
    public static final int INTAKE_MOTOR_2_ID = 2;
    public static final int LIFT_MOTOR_1_ID = 3;
    public static final int LIFT_MOTOR_2_ID = 4;
    public static final int LIFT_MOTOR_3_ID = 14;
    public static final int LIFT_PIVOT_MOTOR_ID = 5;
    public static final int DRIVE_FL_MOTOR_1_ID = 6;
    public static final int angleMotor1ID = 15;
    public static final int DRIVE_FL_MOTOR_2_ID = 7;
    public static final int DRIVE_FR_MOTOR_1_ID = 8;
    public static final int angleMotor2ID = 16;
    public static final int DRIVE_FR_MOTOR_2_ID = 9;
    public static final int DRIVE_BL_MOTOR_1_ID = 10;
    public static final int angleMotor3ID = 17;
    public static final int DRIVE_BL_MOTOR_2_ID = 11;
    public static final int DRIVE_BR_MOTOR_1_ID = 12;
    public static final int angleMotor4ID = 18;
    public static final int DRIVE_BR_MOTOR_2_ID = 13;

    /* Drivetrain Constants */

    /* Swerve Kinematics */

    /* Module Gear Ratios */

    /* Motor Inverts */

    /* Angle Encoder Invert */

    /* Swerve Current Limiting */

    public static final int angleContinuousCurrentLimit = 0;
    public static final int anglePeakCurrentLimit = 0;
    public static final double anglePeakCurrentDuration = 0;
    public static final boolean angleEnableCurrentLimit = true;


    public static final int driveContinuousCurrentLimit = 0;
    public static final int drivePeakCurrentLimit = 0;
    public static final double drivePeakCurrentDuration = 0;
    public static final boolean driveEnableCurrentLimit = true;

    /* These values are used by the drive falcon to ramp in open loop and closed loop driving.
     * We found a small open loop ramp (0.25) helps with tread wear, tipping, etc TO BE CHANGED, NOT OURS*/
    
    public static final double openLoopRamp = 0;
    public static final double closedLoopRamp = 0;


    /* Angle Motor PID Values */

    /* Drive Motor PID Values */

    /* Drive Motor Characterization Values */

    /* Swerve Profiling Values */
    /* Meters per Second */

    /* Neutral Modes */

    /* Module Specific Constants */
    /* Front Left Module - Module 0 */

    /* Front Right Module - Module 1 */

    /* Back Left Module - Module 2 */

    /* Back Right Module - Module 3 */

}
