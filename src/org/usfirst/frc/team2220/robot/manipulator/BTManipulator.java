package org.usfirst.frc.team2220.robot.manipulator;

import org.usfirst.frc.team2220.robot.BTConstants;
import org.usfirst.frc.team2220.robot.BTStorage;

public class BTManipulator implements BTIManipulator
{
	public BTStorage storage;
	
	public BTManipulator(BTStorage storage)
	{
		this.storage = storage;
		stopSecondary();
	}
	
	boolean isToteIn;
	boolean isLeftToteLower;
	boolean isRightToteLower;
	boolean isToteMiddle;
	boolean isLeftToteUpper;
	boolean isRightToteUpper;
	boolean isSecondaryUpper = false;
	
	double toteCollectUp = 0.0;
	double toteCollectDown = 0.0;
	boolean isReleasing = false;
	boolean isReleasingDown = false;
	boolean isClaspRelease = false;
	
	boolean isSecondaryCollectButtonUp;
	boolean isSecondaryCollectButtonDown;
	boolean isBarrelRelease = false;
	boolean isExtended = false;
	boolean keepExtended = false;
	boolean isGoingUp;
	
	boolean waitingForUpper = false;
	
	@Override
	public void perform()
	{
		isToteIn = storage.robot.getToteLimit().getValue();
		isLeftToteLower = storage.robot.getLeftToteLowerLimit().getValue();
		isRightToteLower = storage.robot.getRightToteLowerLimit().getValue();
		isToteMiddle = storage.robot.getToteMiddleLimit().getValue();
		isLeftToteUpper = storage.robot.getLeftToteUpperLimit().getValue();
		isRightToteUpper = storage.robot.getRightToteUpperLimit().getValue();
		isSecondaryUpper = storage.robot.getSecondaryUpperLimit().getValue();
		
		toteCollectUp = storage.controller.getToteCollect().getValue();
		toteCollectDown = storage.controller.getToteCollectDown().getValue();
		isReleasing = storage.controller.getToteRelease().getButtonValue();
		isBarrelRelease = storage.controller.getBarrelRelease().getButtonValue();
		isClaspRelease = storage.controller.getClaspRelease().getLeadingEdge();
		
		isSecondaryCollectButtonUp = storage.controller.getBarrelCollect().getButtonValue();
		isSecondaryCollectButtonDown = storage.controller.getBarrelCollectDown().getButtonValue();
		
		if((toteCollectUp > 0)||(toteCollectDown > 0))
		{
			collectTote();
		}
		
		if(isClaspRelease)
		{
			claspRelease();
		}
		
		if(isReleasing)
		{
			releaseTotes();
		}
		
		if ((toteCollectUp == 0) && (toteCollectDown == 0) && !isReleasing)
		{
			moveRightForkMotors(0);
			moveLeftForkMotors(0);
		}
		
//		if(toteCollectUp > 0)
//		{
//			if ((isToteMiddle && (!isLeftToteUpper && !isRightToteUpper)) && !isToteIn)
//			{
//				keepExtended = true; 
//				storage.robot.getToteClamp().retract();
//			}
//			
//			else if (keepExtended && isLeftToteUpper && isRightToteUpper)
//			{
//				keepExtended = false;
//				storage.robot.getToteClamp().extend();
//			}
//			
//			else if (!keepExtended)
//			{
//				storage.robot.getToteClamp().extend();
//			}
//		}
		
		if (isBarrelRelease)
		{
			storage.robot.getBarrelHolder().extend();
		}
		else 
		{
			storage.robot.getBarrelHolder().retract();
		}
		
		if (isSecondaryCollectButtonDown)
		{
			lowerSecondary();
		}
		else if ((isSecondaryCollectButtonUp) && !isSecondaryUpper)
		{
			liftSecondary();
		}
		
		else
		{
			stopSecondary();
		}
		
	}
	
	public void liftSecondary()
	{
		startBarrelMotors(false); //based on the ways the barrel motors are wired
	}
	
	public void lowerSecondary()
	{
		startBarrelMotors(true); //based on the ways the barrel motors are wired
	}
	
	public void stopSecondary()
	{
		stopBarrelMotors();
	}
	
	public void collectTote()
	{
			
		if((toteCollectUp > 0) && !isLeftToteUpper)
		{
			moveLeftForkMotors(-BTConstants.TOTE_MOTOR_POWER);
		}
		else if((toteCollectDown > 0) && !isLeftToteLower)
		{
			moveLeftForkMotors(BTConstants.TOTE_MOTOR_POWER);
		}
		else
		{
			moveLeftForkMotors(0);
		}
		
		if((toteCollectUp > 0) && !isRightToteUpper)
		{
			moveRightForkMotors(BTConstants.TOTE_MOTOR_POWER);
		}
		else if((toteCollectDown > 0) && !isRightToteLower)
		{
			moveRightForkMotors(-BTConstants.TOTE_MOTOR_POWER);
		}
		else
		{
			moveRightForkMotors(0);
		}
			
			//set robot color to blue
	}
	
	public void claspRelease()
	{
		if (storage.robot.getToteClamp().isExtended())
		{
			storage.robot.getToteClamp().retract();
		}
		else
		{
			storage.robot.getToteClamp().extend();
		}
	}
			
	
	public void releaseTotes()
	{
		storage.robot.getToteClamp().retract();
		storage.robot.getBarrelHolder().retract();
		isExtended = true;
	}
	
	
	public void startBarrelMotors(boolean goUp)
	{
		if (!goUp)
		{
			storage.robot.getBarrelMotorLeft().setX(BTConstants.BARREL_MOTOR_POWER_DOWN);
			storage.robot.getBarrelMotorRight().setX(BTConstants.BARREL_MOTOR_POWER_DOWN);
		}
		else
		{
			storage.robot.getBarrelMotorLeft().setX(-BTConstants.BARREL_MOTOR_POWER_UP);
			storage.robot.getBarrelMotorRight().setX(-BTConstants.BARREL_MOTOR_POWER_UP);
		}
	}
	
	public void stopBarrelMotors()
	{
		storage.robot.getBarrelMotorLeft().setX(0);
		storage.robot.getBarrelMotorRight().setX(0);
	}
	
	public void moveLeftForkMotors(double x)
	{
		storage.robot.getLeftForkLeft().setX(x);
		storage.robot.getLeftForkRight().setX(x);
//		System.out.println("Fork Left:\t" + x);
	}
	
	public void moveRightForkMotors(double x)
	{
		storage.robot.getRightForkLeft().setX(x);
		storage.robot.getRightForkRight().setX(x);
//		System.out.println("Fork Right:\t" + x);
	}
	
}