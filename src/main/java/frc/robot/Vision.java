package frc.robot;

import java.util.List;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonTrackedTarget;
import org.photonvision.targeting.PhotonPipelineResult;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision {
    PhotonCamera camera = new PhotonCamera("photonvision");
    boolean hasTargets;
    PhotonPipelineResult result;
    
    final double CAMERA_HEIGHT_METERS = 0.0;
    final double TARGET_HEIGHT_METERS = 0.0;
    final double CAMERA_PITCH_RADIANS = 0.0;

    double range = 0.0;

    public void targeting() {
        var result = camera.getLatestResult();
        hasTargets = result.hasTargets();
        if (hasTargets) {
            this.result = result;
        }

        if (result.hasTargets()) {
            // First calculate range
            range = PhotonUtils.calculateDistanceToTargetMeters(
                            CAMERA_HEIGHT_METERS,
                            TARGET_HEIGHT_METERS,
                            CAMERA_PITCH_RADIANS,
                            ((result.getBestTarget().getPitch())*(Math.PI/180)));
            this.result = result;
            

            

    
        }

        SmartDashboard.putNumber("Range", range);
        SmartDashboard.putNumber("ID", getBestTarget());
        SmartDashboard.putNumber("Yaw", getTargetWithID(getBestTarget()).getYaw());
        SmartDashboard.putNumber("Skew", getTargetWithID(getBestTarget()).getSkew());
        
    }

    public PhotonTrackedTarget getTargetWithID(int id) { 
        List<PhotonTrackedTarget> targets = result.getTargets(); 
        for (PhotonTrackedTarget i : targets) {
            if (i.getFiducialId() == id) { 
                return i; 
            }
        }
        return null; 
    }

    public int getBestTarget() {
        if (hasTargets) {
        return result.getBestTarget().getFiducialId(); 
        }
        else {
            return -1; 
        }
    }

    /*
    getDistanceLowerConeNode() takes angleCenterVisionBounds, or the angle in degrees from the limelight to the 
    center of the vision bounds, and limelightHeight, or the height of the limelight from the ground as parameters.

    The limelightHeight does not need to be lower than 22.125 for the method to function, as that signals that the 
    limelightHeight will simply be taller than the level of the lower reflective tape. 

    ** angleCenterVisionBounds and limelightHeight may not need to be declared as parameters once the method is 
    fully implemented. **
    */ 
    public double getDistanceLowerConeNode(double angleCenterVisionBounds, double limelightHeight) {
       return 1/(Math.tan(angleCenterVisionBounds)) * (10.875 + (22.125-limelightHeight));
    }

}
