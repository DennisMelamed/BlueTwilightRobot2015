
package org.usfirst.frc.team2220.robot;


import org.usfirst.frc.team2220.robot.compressor.BTCompressor;
import org.usfirst.frc.team2220.robot.drivetrain.BTOcto;

import edu.wpi.first.wpilibj.SampleRobot;

public class BTMain extends SampleRobot
{
	// Testing folders
	BTCompressor comp;
	BTOcto octo;
	BTStorage storage;
	
    public BTMain()
    {
    	
    }
	
	@Override
    public void robotInit()
    {
		storage = new BTStorage();
    	octo = new BTOcto(storage);	
    	octo.init();
    	comp = new BTCompressor();
    }
	
	//@Override
    public void autonomous()
    {
		
    }
	
	//@Override
    public void operatorControl()
    {
    	while(isOperatorControl())
    	{
    		octo.drive();
    		
    	}
    }
	
	//@Override
    public void disabled()
    {
    	
    }
}
