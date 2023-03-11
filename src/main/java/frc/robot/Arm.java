package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;


public class Arm {
    WPI_TalonFX intake = new WPI_TalonFX(Constants.INTAKE_MOTOR_1_ID);

    Arm (){
        intake.configFactoryDefault();
    }
    
    public void setIntakeMotor(double input){
        intake.set(ControlMode.PercentOutput, input);
    }

    
}
