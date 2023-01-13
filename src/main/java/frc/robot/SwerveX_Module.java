package frc.robot;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.geometry.Rotation2d;
import com.ctre.phoenix.motorcontrol.ControlMode;

import com.ctre.phoenix.sensors.WPI_CANCoder;

public class SwerveX_Module {
    
    private static final double kWheelRadius = 0.0508; // this is in meters; need to update for us
    private static final double wheelCircumference = kWheelRadius*Math.PI*2.0; 
    private static final double driveGearRatio = (7.63 / 1.0); //need to update for us
    private static final double angleGearRatio = (15.43 / 1.0); //need to update for us

    private final WPI_TalonFX m_driveMotor;
    private final WPI_TalonFX m_steerMotor;

    private final WPI_CANCoder m_angleEncoder;

    private final double angleOffset;

    /**
   * Constructs a SwerveModule with a drive motor, turning motor, angle encoder.
   *
   * @param driveMotorChannel PWM output for the drive motor.
   * @param steerMotorChannel PWM output for the turning motor.
   * @param angleEncoderChannel DIO input for the angle encoder.
   * @param offset the angle encoder offset for swerve module
   */
    public SwerveX_Module(
        int driveMotorChannel,
        int steerMotorChannel,
        int angleEncoderChannel, 
        double offset) {

    this.m_driveMotor = new WPI_TalonFX(driveMotorChannel);
    this.m_steerMotor = new WPI_TalonFX(steerMotorChannel);
    this.m_angleEncoder = new WPI_CANCoder(angleEncoderChannel); //need to update for us //m_angleEncoder = new WPI_CANCoder(0, "rio"); // Rename "rio" to match the CANivore device name if using a CANivore

    this.angleOffset = offset;
    }

   /**
   * Returns the current state of the module.
   *
   * @return The current state of the module.
   */
    public SwerveModuleState getState() {
        //return new SwerveModuleState(m_driveMotor.getSelectedSensorVelocity(), new Rotation2d(m_angleEncoder.get()));
        return new SwerveModuleState(
            Conversions.falconToMPS(m_driveMotor.getSelectedSensorVelocity(), wheelCircumference, driveGearRatio), 
            Rotation2d.fromDegrees(Conversions.falconToDegrees(m_steerMotor.getSelectedSensorPosition(), angleGearRatio)) //depends on gear ratio
        );
        
    }

    /*
   * Sets the desired state for the module.
   *
   * @param desiredState Desired state with speed and angle.
   */
    public void setDesiredState(SwerveModuleState desiredState) {
        /*
        // Optimize the reference state to avoid spinning further than 90 degrees
        SwerveModuleState state =
            SwerveModuleState.optimize(desiredState, new Rotation2d(m_angleEncoder.getAbsolutePosition()*(Math.PI/180)));
        // Calculate the drive output from the drive PID controller.
        final double driveOutput =
            m_drivePIDController.calculate(m_driveEncoder.getRate(), state.speedMetersPerSecond);
        final double driveFeedforward = m_driveFeedforward.calculate(state.speedMetersPerSecond);
        // Calculate the turning motor output from the turning PID controller.
        final double turnOutput =
            m_turningPIDController.calculate(m_turningEncoder.getDistance(), state.angle.getRadians());
        final double turnFeedforward =
            m_turnFeedforward.calculate(m_turningPIDController.getSetpoint().velocity);
        m_driveMotor.setVoltage(driveOutput + driveFeedforward);
        m_turningMotor.setVoltage(turnOutput + turnFeedforward);
        */

        SwerveModuleState state = SwerveModuleState.optimize(desiredState, new Rotation2d(m_angleEncoder.getAbsolutePosition()*(Math.PI/180)));

        double velocity = Conversions.MPSToFalcon(state.speedMetersPerSecond, wheelCircumference, driveGearRatio);
        m_driveMotor.set(ControlMode.Velocity, velocity);

        double angle = Conversions.degreesToFalcon((state.angle.getRadians()*(180/Math.PI)), angleGearRatio);
        m_steerMotor.set(ControlMode.Position, (angle + this.angleOffset));
    }
}