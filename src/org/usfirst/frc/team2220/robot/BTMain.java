
package org.usfirst.frc.team2220.robot;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.usfirst.frc.team2220.robot.electronics.BTCameraThreaded;
import org.usfirst.frc.team2220.robot.autonomous.BTAutoContinuous;
import org.usfirst.frc.team2220.robot.autonomous.BTIAutonomousRoutine;
import org.usfirst.frc.team2220.robot.drivetrain.BTTankMeca;
import org.usfirst.frc.team2220.robot.manipulator.BTManipulator;

//import com.ni.vision.NIVision;


import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.SampleRobot;

public class BTMain extends SampleRobot
{
	// Testing folders
	BTTankMeca meca;
	//BTOcto octo;
	BTStorage storage;
	BTIAutonomousRoutine auto;
	//BTTestClass test;
	BTManipulator manipulator;
	CameraServer server;
	BTCameraThreaded T1 = new BTCameraThreaded();

	
	
	
    public BTMain()
    {
    	
    }
	
	@Override
    public void robotInit()
    {
		Compressor comp = new Compressor();
		
		
		storage = new BTStorage();
		if(BTConstants.IS_TEST)
		{
		//	test = new BTTestClass(storage);
			meca = new BTTankMeca(storage);
		}
		else
		{
			meca = new BTTankMeca(storage);
			manipulator = new BTManipulator(storage);
			auto = new BTAutoContinuous(storage, manipulator);
		}
    	//octo = new BTOcto(storage);   	
		if (storage.robot.getGyro() != null)
		{
			storage.robot.getGyro().reset();
		}
		
		storage.robot.getBarrelHolder().retract();
		
		
		
		
		
//		T1.start();
//		T1.setPriority(Thread.MIN_PRIORITY);
//		T1.frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
//        T1.session = NIVision.IMAQdxOpenCamera("cam0", NIVision.IMAQdxCameraControlMode.CameraControlModeController);
//        NIVision.IMAQdxConfigureGrab(T1.session);
        
//	   	server = CameraServer.getInstance();
//        server.setQuality(50);
//        server.startAutomaticCapture("cam0");
		
		
	}
	
	//@Override
    public void autonomous()
    {
    	auto.resetTimer();
    	
//    	BTMacroPlay playah = null;
//    	try {
//    		 playah = new BTMacroPlay();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
    	
		while (isAutonomous())
		{
			auto.runAutonomous();
//			if (playah != null)
//			{
//			playah.play(storage);
//			}
			
		}
//		if(playah!= null)
//		{
//			playah.end(storage);
//		}
    	
    }
	
	//@Override
    public void operatorControl()
    {
//    	NIVision.IMAQdxStartAcquisition(T1.session);
        auto.resetTimer();
//    	BTMacroRecord recorder = null;
//        try {
//			recorder = new BTMacroRecord();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
    	while(isOperatorControl())
    	{
    		//Camera (threaded)
//    		T1.run();
    		meca.drive();
    		manipulator.perform();
//    		try {
//    			
//    			if(recorder != null)
//    			{
//    				recorder.record(storage);
//    			}
//    			
//			} catch (IOException e) {
//			}
//		}
//    	try {
//    		if(recorder != null)
//    		{
//    			recorder.end();
//    		}
//		} catch (IOException e) {
//			e.printStackTrace();
		}
//    	NIVision.IMAQdxStopAcquisition(T1.session);
    }
	
	//@Override
    public void disabled()
    {
    	auto.resetTimer();
    }
}
