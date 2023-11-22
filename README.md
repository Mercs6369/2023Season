This branch contains test code for Team 1: Vision off-season projects.
Team Roster:
	Gaurav Salandri (lead)
	Gargi Garg
	Kevin Ahr
	Joshua Lee
	Brady Hargraves
Major Task Status:
1. Define Requirements
	We have two major objectives/requirements.
	i. Develop an autonomous capability, using the existing 2023 Swerve Robot, that is capable of picking up and scoring multiple game pieces using camera video for feedback. Performance will be judged on consistency and number of points.
	ii. Develop an autonomous capability which can be commanded during teleop to quickly pickup and score game pieces using camera video for feedback. Performance will be judged on speed, accuracy, and ease of use.
2. Identify/brainstorm multiple alternative designs that can meet requirements
	Team has identified multiple issues to work in support of these objectives, each having multiple potential solutions. These major issues and identified solutions are as follows:
	i. Need the ability to accurately detect and geolocate AprilTags, reflective tape, purple cubes, and yellow cones including its orientation. The resulting data can come from NetworkTables and the post processing needed after that will depend on the type of target and the method used. Potential SW solutions include:
		a. PhotonVision server
		b. Limelight
		c. Pixby
		d. YOLO v8 with custom training
	ii. Need the ability to convert vision system data results (target X, Y, angle coordinates, etc.) into robot pathing commands for the Swerve system. Potential SW solutions include:
		a. Use existing Swerve waypoint generator function and command scheduled with modifications, hosted on RoboRIO
		b. Develop new waypoint generator which includes Arm control functions
	iii. Need the ability to execute generated Swerve and Arm commands on command and during autonomous. Potential SW solutions include:
		a. Use the existing autonomous-bypass function within swerve system, but drive it with new velocity command generator
        b. Adapt velocity command and waypoint generator to work directly with Swerve command scheduler
3. Evaluate alternatives and DOCUMENT why you chose the design you did
	TBD	
4. Complete detailed design (with CAD model where appropriate)
	TBD	
5. Build and test design
    TBD



Proposed SW Structure (assumes the current HW design we are prototyping) with basic psuedo code ideas
    RoboRIO
        Vision Process running at 50 Hz (20 millisec between updates)
            if (one or more AprilTags are visible)
                reset the odometry position to 0, 0, 0
                update the current robot's predicted position based on AprilTag data
            else
                update the current robot's predicted position based on the last known AprilTag data AND the current odometry data
            ...
            Change Vision Processing Mode method (new method which reconfigures the PhotonVision server to change targetting modes)
            ...

        Teleop Periodic (this is an existing process)
            ...
            Add ability to command autonomous modes using controller commands (ex. hold B to automatically hunt and pickup cubes)
            ...

        Auto Periodic (this is an existing process)
            ...
            Add automated sequence to move robot to specific position, hunt for game pieces, score, etc.
            ...

        Auto Move Robot method (this is a new method which moves the robot automatically to the commanded position using this new Vision Proces)

        Auto Position to Nearest Visible Cube Target method (this is a new method which moves the robot automatically in a pickup position for the cube)

        Auto Position to Nearest Visible Cone Target method (this is a new method which moves the robot automatically in a pickup position for the cone)

        Auto Cube Pickup method (this is a new method which commands the robot to automatically pickup a cube in front of it)

        Auto Cone Pickup method (this is a new method which commands the robot to automatically pickup a cone in front of it)

        Auto Cube Score method (this is a new method which commands the robot to automatically score a cube onto the designated position)

        Auto Cone Score method (this is a new method which commands the robot to automatically score a cone onto the designated position)

    Beelink SER5
        PhotonVision server outputting targetting data over Networktables at 100 Hz (10 millisec between updates)
