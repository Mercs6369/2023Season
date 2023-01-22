package frc.robot;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.geometry.Rotation2d;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.sensors.WPI_CANCoder;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveX_Module {
    private static final double kWheelRadius = Units.inchesToMeters(2.0);
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
   * @param driveMotorChannel CAN bus ID for the drive motor.
   * @param steerMotorChannel CAN bus ID for the turning motor.
   * @param angleEncoderChannel CAN bus ID for the angle encoder.
   * @param offset the angle encoder offset for swerve module
   */
    public SwerveX_Module(
        int driveMotorChannel,
        int steerMotorChannel,
        int angleEncoderChannel, 
        double offset) {

    Timer.delay(1.0);
    this.m_driveMotor = new WPI_TalonFX(driveMotorChannel);
    this.m_driveMotor.configFactoryDefault();
    this.m_driveMotor.config_kP(0, 0.05);
    this.m_driveMotor.setInverted(false);

    this.m_steerMotor = new WPI_TalonFX(steerMotorChannel);
    this.m_steerMotor.configFactoryDefault();
    this.m_steerMotor.config_kP(0, 0.3);
    this.m_steerMotor.setInverted(true);

    this.m_angleEncoder = new WPI_CANCoder(angleEncoderChannel); //need to update for us //m_angleEncoder = new WPI_CANCoder(0, "rio"); // Rename "rio" to match the CANivore device name if using a CANivore
    this.m_angleEncoder.configFactoryDefault();
    this.m_angleEncoder.configSensorDirection(false);
    this.m_angleEncoder.configAbsoluteSensorRange(AbsoluteSensorRange.Unsigned_0_to_360);
    this.angleOffset = offset;
    this.m_steerMotor.setSelectedSensorPosition(offset); // one alternative idea
    }

   /**
   * Returns the current state of the module.
   *
   * @return The current state of the module.
   */
    public SwerveModuleState getState() {
        return new SwerveModuleState(
            Conversions.falconToMPS(m_driveMotor.getSelectedSensorVelocity(), wheelCircumference, driveGearRatio), 
            Rotation2d.fromDegrees(Conversions.falconToDegrees(m_steerMotor.getSelectedSensorPosition(), angleGearRatio)) //depends on gear ratio
        );
    }

    public void setDesiredState(SwerveModuleState desiredState) {
        //SwerveModuleState state = SwerveModuleState.optimize(desiredState, new Rotation2d(this.m_angleEncoder.getAbsolutePosition()*(Math.PI/180)));
        SwerveModuleState state = desiredState;

        m_driveMotor.set(ControlMode.Velocity,
                         Conversions.MPSToFalcon(state.speedMetersPerSecond, wheelCircumference, driveGearRatio));
        m_steerMotor.set(ControlMode.Position,
                         Conversions.degreesToFalcon((state.angle.getDegrees()), angleGearRatio) + angleOffset);
    }

    public void update(){
        SmartDashboard.putNumber("Angle Encoder" + Integer.toString(m_driveMotor.getDeviceID()), m_angleEncoder.getAbsolutePosition());
        SmartDashboard.putNumber("Steer Motor" + Integer.toString(m_driveMotor.getDeviceID()), m_steerMotor.getSelectedSensorPosition());
    }
}