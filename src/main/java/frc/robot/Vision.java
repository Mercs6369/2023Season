package frc.robot;

import java.util.List;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonTrackedTarget;
import org.photonvision.targeting.PhotonPipelineResult;
import edu.wpi.first.wpilibj.util.Color;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTableInstance;


public class Vision {
    PhotonCamera camera = new PhotonCamera("photonvision"); // April Tag camera
    PhotonCamera gamePieceCamera = new PhotonCamera("GamePieceTargeting"); // Game Piece Camera
    boolean hasTargets; // april tags
    PhotonPipelineResult result; // april tags
    double red_color = 0;
    double green_color = 0;
    double blue_color = 0;
    double mag;
    ColorMatch m_colorMatcher = new ColorMatch();
    ColorMatchResult match;
    String color_string;

    //private final Color kBlueTarget = new Color(0.143, 0.427, 0.429);
    //private final Color kYellowTarget = new Color(0.361, 0.524, 0.113);
    private final Color kCubeTarget = new Color(0.25, 0.25, 0.5);  //this was measured by us
    private final Color kConeTarget = new Color(0.46, 0.45, 0.09); //this was measured by us

    boolean gamePieceHasTargets; // Game Piece has Targets
    PhotonPipelineResult gamePieceCameraResult = new PhotonPipelineResult(); // Game Piece Detection Result
    
    final double CAMERA_HEIGHT_METERS = 0.0;
    final double TARGET_HEIGHT_METERS = 0.0;
    final double CAMERA_PITCH_RADIANS = 0.0;

    double range = 0.0;

    public void color_sensor_init() {

        m_colorMatcher.addColorMatch(kCubeTarget);
        m_colorMatcher.addColorMatch(kConeTarget);
    }
        
    public void color_sensor_measure () {
        red_color = NetworkTableInstance.getDefault().getTable("default").getEntry("colorSensorRed").getDouble(0);
        green_color = NetworkTableInstance.getDefault().getTable("default").getEntry("colorSensorGreen").getDouble(0);
        blue_color = NetworkTableInstance.getDefault().getTable("default").getEntry("colorSensorBlue").getDouble(0);
        mag = red_color + green_color + blue_color;
        red_color = red_color/mag;
        green_color = green_color/mag;
        blue_color = blue_color/mag;
        
        if ((red_color != 0) && (green_color != 0) && (blue_color != 0)) {
            match = m_colorMatcher.matchClosestColor(new Color(red_color, green_color, blue_color));

            if (match.color == kCubeTarget) {
            color_string = "Cube";
            } else if (match.color == kConeTarget) {
                color_string = "Cone";
            } else {
                color_string = "Unknown";
            }
            System.out.print(color_string);
            }
 
        SmartDashboard.putNumber("Confidence", match.confidence);
        SmartDashboard.putString("Detected Color", color_string);

        }

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
    */ 
    public double getDistanceLowerConeNode(double angleCenterVisionBounds, double limelightHeight) {
       return ((11.875 + 22.125-limelightHeight)/(Math.tan(Math.toRadians(Math.abs(angleCenterVisionBounds)-1.58164))))-8.75;
    }

    public int getGamePieceCameraPipeline() {
        return gamePieceCamera.getPipelineIndex(); // 0 = cones, 1 = cubes
    }

    public void getOrientationOfCone() {
        setGamePiecePipeline(0);
        
        gamePieceCamera.getLatestResult();

        if (gamePieceCameraResult.hasTargets()) {
            
            PhotonTrackedTarget bestTarget = gamePieceCameraResult.getBestTarget();
            double objectWidth = Math.abs(bestTarget.getDetectedCorners().get(0).x - bestTarget.getDetectedCorners().get(1).x);
            double objectHeight = Math.abs(bestTarget.getDetectedCorners().get(0).y - bestTarget.getDetectedCorners().get(3).y);
            SmartDashboard.putNumber("length", objectHeight);
            SmartDashboard.putNumber("width", objectWidth);




            if (objectHeight > (objectWidth + 10)) {
                // probably standing up
                SmartDashboard.putString("Orientation", "Hopefully standing up");
            } else {
                // probably on the side
                SmartDashboard.putString("Orientation", "On It's Side maybe, probably, eh what do I know, this code probably isn't accurate at all oops");
            }




           
        }
    }





    public void setGamePiecePipeline(int gamePiecePipelineIndex) {
        

        if (gamePiecePipelineIndex == -99) { // -99 is the driver cam thingy
            gamePieceCamera.setDriverMode(true);
        } else if (gamePiecePipelineIndex == 0 || gamePiecePipelineIndex == 1){
            gamePieceCamera.setDriverMode(false);
            gamePieceCamera.setPipelineIndex(gamePiecePipelineIndex);
            
        } else {
            // invalid command sent
        }
    }

}
