package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision extends SubsystemBase {
    PhotonCamera camera = new PhotonCamera("photonvision");
    boolean hasTargets;
    PhotonPipelineResult result;
    
    final double CAMERA_HEIGHT_METERS = 0.0;
    final double TARGET_HEIGHT_METERS = 0.0;
    final double CAMERA_PITCH_RADIANS = 0.0;

    double range = 0.0;

    @Override
    public void periodic() {
        var result = camera.getLatestResult();
        hasTargets = result.hasTargets();
        if (hasTargets) {
            this.result = result;
        }

        if (result.hasTargets()) {
            // First calculate range
            range =
                    PhotonUtils.calculateDistanceToTargetMeters(
                            CAMERA_HEIGHT_METERS,
                            TARGET_HEIGHT_METERS,
                            CAMERA_PITCH_RADIANS,
                            ((result.getBestTarget().getPitch())*(Math.PI/180)));
    
        }

        SmartDashboard.putNumber("Range", range);
        
    }


}
