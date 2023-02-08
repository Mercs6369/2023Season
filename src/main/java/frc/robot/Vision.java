package frc.robot;

import java.util.List;

import javax.xml.crypto.Data;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonTrackedTarget;
import org.photonvision.targeting.PhotonPipelineResult;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision {
    PhotonCamera camera = new PhotonCamera("photonvision"); // April Tag camera
    PhotonCamera gamePieceCamera = new PhotonCamera("Microsoft_LifeCam_HD-3000"); // Game Piece Camera
    boolean hasTargets; // april tags
    PhotonPipelineResult result; // april tags
    Color_Sensor m_color_sensor = new Color_Sensor();

    private boolean gamePieceHasTargets = false; // Game Piece has Targets
    private PhotonPipelineResult gamePieceCameraResult = new PhotonPipelineResult(); // Game Piece Detection Result
    
    private final double CAMERA_HEIGHT_METERS = 0.0;
    private final double TARGET_HEIGHT_METERS = 0.0;
    private final double CAMERA_PITCH_RADIANS = 0.0;
    private double range = 0.0;

   /**
    * Performs a sample of Rev Color Sensor V3 RGB, normalizes the data, and attempts to match to expected colors.
    * Member variables "color_string" and "match" are updated with results and info is output to SmartDashboard.
    *
    */
    public void CS_RGB_measure() {
        m_color_sensor.color_sensor_RGB_measure();
    }
 
   /**
    * Performs a sample of Rev Color Sensor V3 proximity data and updates member variable "proximity" with result.
    * Info is output to SmartDashboard also.
    *
    */
    public void CS_Prox_measure() {
        m_color_sensor.color_sensor_prox_measure();
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




    /*
     *
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * Everything above this is not Jonathan's code
     * 
     * 
     * 
     * 
     * Everything under this is Jonathan's code.
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */


    /**
     * Returns the current game piece targeting pipeline index. 0 = cone, 1 = cube, other is probably driver mode
     * 
     */
    public int getGamePieceCameraPipeline() {
        return gamePieceCamera.getPipelineIndex(); // 0 = cones, 1 = cubes
    }


    enum infoTypeToReturn { // this is used when using GetCubeInfo/GetConeInfo
        Area,
        Pitch,
        Skew,
        Yaw,
        Orientation,
    }


    enum gamePiecePipelineIndex { // this is used for the setGamePiecePipeline method
        driver,
        cube,
        cone,
    }


    double coneAreaAt1Foot;
    double cubeAreaAt1Foot;


    /**
     * 
     * Returns the orientation of the cone, this should only be run after a cone game piece has been identified. (1.0 - Standing Up || 0.0 - On It's Side)
     */
    private double getOrientationOfCone() {
        PhotonTrackedTarget bestTarget = gamePieceCameraResult.getBestTarget();
        double objectWidth = Math.abs(bestTarget.getMinAreaRectCorners().get(3).x - bestTarget.getMinAreaRectCorners().get(2).x);
        double objectHeight = Math.abs(bestTarget.getMinAreaRectCorners().get(0).y - bestTarget.getMinAreaRectCorners().get(3).y);

        SmartDashboard.putNumber("Object Hieght",objectHeight);
        SmartDashboard.putNumber("Object Width",objectWidth);

        SmartDashboard.putNumber("Upper Left X",bestTarget.getMinAreaRectCorners().get(3).x);
        SmartDashboard.putNumber("Upper Left Y",bestTarget.getMinAreaRectCorners().get(3).y);

        SmartDashboard.putNumber("Upper Right X",bestTarget.getMinAreaRectCorners().get(2).x);
        SmartDashboard.putNumber("Upper Right Y",bestTarget.getMinAreaRectCorners().get(2).y);

        SmartDashboard.putNumber("Lower Left X",bestTarget.getMinAreaRectCorners().get(0).x);
        SmartDashboard.putNumber("Lower Left Y",bestTarget.getMinAreaRectCorners().get(0).y);

        SmartDashboard.putNumber("Lower Right X",bestTarget.getMinAreaRectCorners().get(1).x);
        SmartDashboard.putNumber("Lower Right Y",bestTarget.getMinAreaRectCorners().get(1).y);
     
        if (objectHeight > (objectWidth - 10)) {
            // Probably standing up
            SmartDashboard.putString("Orientation", "Standing up");
            return 1.0;
        } else {
            // Probably on the side
            SmartDashboard.putString("Orientation", "On it's side, probably, ehh, probably not, who knows");
            return 0.0;
        }
        
    }  
    



    /**
    * 
    *   Returns the best target, can return 3 strings: Cone - The best/closest target is a cone || Cube - The best/closest target is a cone || Error - There is not a target to evaluate.
    *
    */
    public String getBestTargetGlobal() {
        
        double CubeArea;
        double ConeArea;

        setGamePiecePipeline(gamePiecePipelineIndex.cone);     
        gamePieceCameraResult = gamePieceCamera.getLatestResult();

        // cone pipeline
        if (gamePieceCameraResult.hasTargets()) {
            PhotonTrackedTarget bestTarget = gamePieceCameraResult.getBestTarget();
            ConeArea = bestTarget.getArea();
        }


        // cube pipeline
        setGamePiecePipeline(gamePiecePipelineIndex.cube);
        gamePieceCameraResult = gamePieceCamera.getLatestResult();
        
        if (gamePieceCameraResult.hasTargets()) {
            PhotonTrackedTarget bestTarget = gamePieceCameraResult.getBestTarget();
            CubeArea = bestTarget.getArea();
        }


        return "in progress"; // don't mess with any code here


    }
    

    
    public double getCubeInfo(infoTypeToReturn valueToGet) {
        setGamePiecePipeline(gamePiecePipelineIndex.cube);
        gamePieceCameraResult = gamePieceCamera.getLatestResult();
        
        
        if (gamePieceCameraResult.hasTargets()) { // if it does has a target then do this :D
            PhotonTrackedTarget bestTarget = gamePieceCameraResult.getBestTarget();
            switch (valueToGet) {
                case Area:
                    return bestTarget.getArea();
                case Pitch:
                    return bestTarget.getPitch();
                case Skew:
                    return bestTarget.getSkew();
                case Yaw:
                    return bestTarget.getYaw();
                default:
                    break;
            }
        }
        return -1; // if camera doesn't have targets
    }


    public double getConeInfo(infoTypeToReturn valueToGet) {
        setGamePiecePipeline(gamePiecePipelineIndex.cone);
        gamePieceCameraResult = gamePieceCamera.getLatestResult();

        if (gamePieceCameraResult.hasTargets()) { // if it does has a target then do this :D
            PhotonTrackedTarget bestTarget = gamePieceCameraResult.getBestTarget();
            switch (valueToGet) {
                case Area:
                    return bestTarget.getArea();
                case Pitch:
                    return bestTarget.getPitch();
                case Skew:
                    return bestTarget.getSkew();
                case Yaw:
                    return bestTarget.getYaw();
                case Orientation:
                    return getOrientationOfCone();
                default:
                    break;
            }
        }
        return -1; // if camera doesn't have targets
    }




    public void setGamePiecePipeline(gamePiecePipelineIndex newPipelineName) {
       if (newPipelineName == gamePiecePipelineIndex.driver) {
        gamePieceCamera.setPipelineIndex(2); // idx 2 is driver
       } else if (newPipelineName == gamePiecePipelineIndex.cone) {
        gamePieceCamera.setPipelineIndex(0); // idx 0 is cone
       } else if (newPipelineName == gamePiecePipelineIndex.cube) {
        gamePieceCamera.setPipelineIndex(1); // idx 1 is cube
       } else {
        System.out.println("Error in Vision.java - newPipelineName wasn't of the proper enum. Not sure how this is possible :P");
       }


    }
}