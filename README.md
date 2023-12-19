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
  -  System Design

![image](https://github.com/Mercs6369/2023Season/assets/72580050/43dbb785-1fd3-4f6b-a6c8-7b8ff1c1c268)

Vision Processor (VP), the Photon Vision server runs on the Beelink mini pc. The server can connect to multiple cameras at once, and sends the data from each camera process over local host to a custom processing software running on the Beelink. This performs the final vision processing. Critical robot information is sent through network tables (ex. x, y, theta) to the Robo Rio. Then the Robo Rio uses this data to determine the robots precise location.


  -  Vision Processor
	Major Parts:
     	Arducam 120 fps Mono Global Shutter USB Camera (Amazon Link: https://www.amazon.com/Arducam-Distortion-Microphones-Computer-Raspberry/dp/B096M5DKY6/ref=sr_1_2?crid=KGS8EVJN0SDT&keywords=arducam%2B120fps%2Bmono%2Bglobal%2Bshutter%2Busb%2Bcamera&qid=1702947529&sprefix=arducam%2B120%2Bfps%2Bmono%2Bg%2Caps%2C103&sr=8-2&th=1)

![image](https://github.com/Mercs6369/2023Season/assets/72580050/6af8153e-b32c-413b-924d-8e8c662704ac)
		M12 Wide Angle Lens (Amazon Link: https://www.amazon.com/Xenocam-Degrees-Distortion-Without-Infrared/dp/B07CZ5G2TY/ref=sr_1_5?crid=23CC74EGVVPIR&keywords=M12%2Bwide%2Bangle%2Blens&qid=1702947639&sprefix=m12%2Bwide%2Bangle%2Blens%2Caps%2C115&sr=8-5&th=1)

![image](https://github.com/Mercs6369/2023Season/assets/72580050/26630844-6574-40f6-8c7a-827b4e28de01)
		Beelink Mini PC (Amazon Link: https://www.amazon.com/Beelink-SER5-Graphics-Computer-Support/dp/B0C2P486GQ/ref=sr_1_4?crid=2JRXMK9ED7XUR&keywords=beelink%2Bmini%2Bpc&qid=1702947874&sprefix=Beelin%2Caps%2C118&sr=8-4&th=1)

![image](https://github.com/Mercs6369/2023Season/assets/72580050/fabffcfb-aa9f-40b9-b995-036484863f8d)

Design Description:
	System consists of the following major software components per processor:
        Beelink -
            Custom version of Photon Vision Server that maps to cameras based on USB port they are connected to.
            Custom Vision Processor service that receives Photon Vision April tag targeting information and outputs consolidated estimated robot position.This is acheived by using the relative location of the cameras to the center of the robot, the robot distance from April tags, and the known locations of each April tag on the field to estimate the precise position of the robot on the field.
        
        RoboRIO -

 	
       
  
  -  RoboRIO
        Vision Process running at 50 Hz (20 millisec between updates)
            if (one or more AprilTags are visible)
                \\reset the odometry position to 0, 0, 0
                update the current robot's predicted position based on AprilTag data
            else
                \\update the current robot's predicted position based on the last known AprilTag data AND the current odometry data
                rotate robot until AprilTag is visible
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
	
