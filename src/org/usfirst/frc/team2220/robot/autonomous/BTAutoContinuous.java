package org.usfirst.frc.team2220.robot.autonomous;

import org.usfirst.frc.team2220.robot.BTStorage;
import org.usfirst.frc.team2220.robot.BTConstants;
import org.usfirst.frc.team2220.robot.manipulator.BTManipulator;

public class BTAutoContinuous implements BTIAutonomousRoutine
{
	BTStorage storage;
	BTManipulator manipulator;
	
	double wheelSpeed = 0.75;
	double slowWheelSpeed= 0.5;
	double fl = 0.0;
	double bl = 0.0;
	double fr = 0.0;
	double br = 0.0;
	double degree = 0.0;
	
	double s1 = 1400; 			//turn 90 degrees to align secondary with landfill & barrels
	double s2 = s1 + 600; 		//ram forward into landfill
	double s3 = s2 + 500; 		//move robot right to 2nd barrel
	double s3_5 = s3 + 100;		//move right side even further into totes
	double s3_75 = s3_5 + 150;	//move left side into totes
	double s4 = s3_75 + 3000;		//lift barrel
	double s5 = s4 + 3000;		//stafe forward
	double s6 = s5 + 350;		//move right
//	double s7 = s6 + 250;		//rotate robot left wheels backward
//	double s8 = s7 + 250;		//moving robot right to next barrel
//	double s9 = s8 + 150;		//moving and raising secondary
//	double s10 = s9 + 200;		//move robot backward into landfill
//	double s11 = s10 + 1500; 	//raise secondary
//	double s12 = s11 + 200;		//move robot forward
//	double s13 = s12 + 1300;	//rotate 90 counterclockwise
//	double s14 = s13 + 1500;	//driving robot right to auto zone 
	
	int barrelCount = 0;
	
	boolean isSecondaryUpper = false;
	boolean correcting = false;
	boolean barrelCollectComplete = false;
	
	long elapsedTime = 0;
	long startTime = 0;
	long setUpTime = 2500;
	long extraMoveLeftTime = 0;
	long moveLeftTime = 0;
	long moveLeftStartTime = 0;
	long moveLeftElapsedTime = 0;
	
	public BTAutoContinuous(BTStorage storage, BTManipulator manipulator)
	{
		this.storage = storage;
		this.manipulator = manipulator;
	}
	
	@Override
	public void runAutonomous()
	{
		moveAutoZone();
//		moveIntoAutoZone();
	}
	
	public void runAutonomousCoop()
	{	
		if(startTime == 0)
		{
			startTime = System.currentTimeMillis();
		}
		
		elapsedTime = System.currentTimeMillis() - startTime;
		isSecondaryUpper = storage.robot.getSecondaryUpperLimit().getValue();
		
		if(!isSecondaryUpper)
		{
			manipulator.startBarrelMotors(true);
		}
		else
		{
			manipulator.stopBarrelMotors();
		}
		
		if((elapsedTime > 4500) && (elapsedTime <= 5400))
		{
			moveForward();
		}
		else if((elapsedTime > 5400) && (elapsedTime <= 6750))
		{
			rotateOnlyRightWheels(true);
		}
		else if((elapsedTime > 6750) && (elapsedTime <= 8000))
		{
			moveLeft();
		}
		else if ((elapsedTime > 8000) && (elapsedTime <= 8900))
		{
			moveBackward();
		}
		else if((elapsedTime > 8900) && (elapsedTime <= 10150))
		{
			moveLeft();
		}
		else 
		{
			stopMotors();
		}
	}
	
	public void moveAutoZone()
	{
		if (startTime == 0)
		{
			startTime = System.currentTimeMillis();
		}
			
		elapsedTime = System.currentTimeMillis() - startTime;
		
		if (elapsedTime > 0 && elapsedTime <= 400)
		{
			moveLeft();
		}
		else
		{
			stopMotors();
		}
	}
	public void barrelSteal()
	{
		if (barrelCount >= 4)	//We don't expect this to happen given the short time for autonomous
		{
			stopMotors();
			manipulator.stopBarrelMotors();
		}
		
		else if (barrelCount <= 3)
		{
			
			if (!barrelCollectComplete)
			{
				liftBarrel();
			}
			
			if(moveLeftStartTime == 0)
			{
				moveLeftStartTime = System.currentTimeMillis();
			}
			
			moveLeftElapsedTime = System.currentTimeMillis() - moveLeftStartTime;
			
			
			
			if (barrelCollectComplete && moveLeftElapsedTime < (1_200 + setUpTime))
			{
				if (!correcting)
				{
					moveLeft();
				}
				
				degree = storage.robot.getGyro().getAngle();
				
				if(degree < (-BTConstants.ANGLE_ERROR)) //If robot pointing too far left, adjust front wheel speed to turn robot 
				{
					fr = wheelSpeed * BTConstants.MOTOR_GYRO_SCALE_VALUE;
					fl = wheelSpeed * BTConstants.MOTOR_GYRO_SCALE_VALUE;
		
					invertMotors();
		
					storage.robot.getFrontLeftMotor().setX(fl);
					storage.robot.getFrontRightMotor().setX(fr);
					correcting = true;
				}
				else if(degree > (BTConstants.ANGLE_ERROR)) //If robot pointing too far right, adjust back wheel speed to turn robot 
				{
					br = wheelSpeed * BTConstants.MOTOR_GYRO_SCALE_VALUE;					
					bl = wheelSpeed * BTConstants.MOTOR_GYRO_SCALE_VALUE;
		
					invertMotors();
		
					storage.robot.getBackLeftMotor().setX(bl);
					storage.robot.getBackRightMotor().setX(br);	
					correcting = true;
				}
				else
				{
					correcting = false;
				}
				
			}
			if( moveLeftElapsedTime > 10_000 + setUpTime && barrelCount != 2)
			{
				stopMotors();
				barrelCollectComplete = false;
				moveLeftStartTime = 0;
				if (barrelCount == 3)
				{
					liftBarrel(); 
				}
			}
			
			if (moveLeftElapsedTime > 12_000 + setUpTime && barrelCount == 2)
			{
				stopMotors();
				barrelCollectComplete = false;
				moveLeftStartTime = 0;
			}
		}

	}
		
	public void liftBarrel()
	{
		if(startTime == 0)
		{
			startTime = System.currentTimeMillis();
		}
		
		elapsedTime = System.currentTimeMillis() - startTime;	//Time since the operation began running
		
		if (elapsedTime >= 0 && elapsedTime < (0 + setUpTime))
		{
			manipulator.startBarrelMotors(false);	//Barrel motors start moving down
		}
		
		else if(elapsedTime > (0 + setUpTime) && elapsedTime <= (250 + setUpTime))
		{
			manipulator.stopBarrelMotors();
			secondaryActuate();	//Currently does nothing
		}
		
		else if(elapsedTime > (250 + setUpTime) && elapsedTime < (1000 + setUpTime))
		{
			manipulator.startBarrelMotors(true);	//Barrel motors start moving up
		}

		else if (elapsedTime > (1000 + setUpTime) && elapsedTime < (3000 + setUpTime))
		{
			if(barrelCount < 3)
			{
				manipulator.stopBarrelMotors();
				rotateOnlyRightWheels(true);	//Rotates only the left wheels, moving the robot (clockwise?)
			}
			else
			{
				manipulator.stopBarrelMotors();
				rotateOnlyLeftWheels(true);	//Rotates only the right wheels, moving the robot (clockwise?)
			}
		}
		
		else if (elapsedTime > (3000 + setUpTime) && elapsedTime < (5000 + setUpTime))
		{
			stopMotors();
			manipulator.startBarrelMotors(false);	//Barrel motors start moving down
		}
		
		else if (elapsedTime > (5000 + setUpTime) && elapsedTime <= (5250 + setUpTime))
		{
			stopMotors();
			manipulator.stopBarrelMotors();
			secondaryActuate();
		}
		
		else if (elapsedTime > (5250 + setUpTime) && elapsedTime < (7250 + setUpTime))
		{
			manipulator.startBarrelMotors(true);	//Barrel motors start moving up
		}
		
		else if	(elapsedTime > (7250 + setUpTime) && elapsedTime <= (9250 + setUpTime))
		{
			if(barrelCount < 3)
			{
				manipulator.stopBarrelMotors();
				rotateOnlyRightWheels(false);		//Rotates only the left wheels, moving the robot (counterclockwise?)
			}
			else
			{
				manipulator.stopBarrelMotors();
				rotateOnlyLeftWheels(true);	//Rotates only the right wheels, moving the robot (counterclockwise?)
			}
		}
		
		else if(elapsedTime > (9250 + setUpTime))
		{
			stopMotors();
			startTime = 0;
			barrelCount++;
			barrelCollectComplete = true;
		}
	}
	
	public void secondaryActuate()
	{
		
	}
	
	public void barrelSteal2()
	{
		
		if (startTime == 0)
		{
			startTime = System.currentTimeMillis();
		}
			
		elapsedTime = System.currentTimeMillis() - startTime;
			
		//first barrel - setting up next to totes
		if (elapsedTime > 0 && elapsedTime <= s1)
		{
			rotateOnlyRightWheels(true);
		}
		else if (elapsedTime > s1 && elapsedTime <= s2)
		{
			moveBackward();
		}
		else if (elapsedTime > s2 && elapsedTime <= s3)
		{
			slowMoveRight();
		}
		else if (elapsedTime > s3 && elapsedTime <= 3_5)
		{
			rotateOnlyRightWheels(true);
		}
		else if (elapsedTime > s3_5 && elapsedTime <= 3_75)
		{
			rotateOnlyLeftWheels(true);
		}	
		else if ((elapsedTime > s3_75 && elapsedTime <= s4) && !isSecondaryUpper)
		{
			stopMotors();
			isSecondaryUpper = storage.robot.getSecondaryUpperLimit().getValue();
			if (isSecondaryUpper)
			{
				manipulator.stopBarrelMotors();	
			}
			barrelMotorsAuto(false);
		}
		else if ((elapsedTime > s4 && elapsedTime <= s5))
		{
			manipulator.stopBarrelMotors();
			moveForward();
		}
		else if ((elapsedTime > s5 && elapsedTime <= s6))
		{
			moveRight();
		}
		else
		{
			stopMotors();
			manipulator.stopBarrelMotors();
		}
//		else if (elapsedTime > s4 && elapsedTime <= s5)
//		{
//			manipulator.stopBarrelMotors();
//			rotateOnlyLeftWheels(false);
//		}
//		else if (elapsedTime > s5 && elapsedTime <= s6)
//		{
//			stopMotors();
//			barrelMotorsAuto(true);
//		}
//		else if (elapsedTime > s6 && elapsedTime <= s7)
//		{
//			manipulator.stopBarrelMotors();
//			rotateOnlyLeftWheels(true);
//		}
//		else if (elapsedTime > s7 && elapsedTime <= s8)
//		{
//			slowMoveRight();
//		}
//		else if (elapsedTime > s8 && elapsedTime <= s9)
//		{
//			barrelMotorsAuto(true);
//		}
//		else if (elapsedTime > s9 && elapsedTime <= s10)
//		{
//			moveBackward();
//		}
//		else if ((elapsedTime > s10 && elapsedTime <= s11) && !isSecondaryUpper)
//		{
//			isSecondaryUpper = storage.robot.getSecondaryUpperLimit().getValue();
//			stopMotors();
//			if (isSecondaryUpper)
//			{
//				manipulator.stopBarrelMotors();	
//			}
//			barrelMotorsAuto(false);
//		}
//		else if (elapsedTime > s11 && elapsedTime <= s12)
//		{
//			manipulator.stopBarrelMotors();
//			moveForward();
//		}
//		else if (elapsedTime > s12 && elapsedTime <= s13)
//		{
//			rotateOnlyRightWheels(false);
//		}
//		else if (elapsedTime > s13 && elapsedTime <= s14)
//		{
//			moveRight();
//		}
//		else
//		{
//			stopMotors();
//			manipulator.stopBarrelMotors();
//		}
		
	}
	
	public void moveIntoAutoZone()
	{
		if (startTime == 0)
		{
			startTime = System.currentTimeMillis();
		}
			
		elapsedTime = System.currentTimeMillis() - startTime;
		
		if (elapsedTime > 0 && elapsedTime <= 500)
		{
			moveRight();
		}
		else 
		{
			stopMotors();
			manipulator.stopBarrelMotors();
		}
	}

	public void invertMotors()
	{
		if (BTConstants.FRONT_LEFT_REVERSED)
		{
			fl = -fl;
		}
		if (BTConstants.BACK_LEFT_REVERSED)
		{
			bl = -bl;
		}
		if (BTConstants.FRONT_RIGHT_REVERSED)
		{
			fr = -fr;
		}
		if (BTConstants.BACK_RIGHT_REVERSED)
		{
			br = -br;
		}
	}

	public void stopMotors()
	{
		storage.robot.getFrontLeftMotor().setX(0);
		storage.robot.getBackLeftMotor().setX(0);
		storage.robot.getFrontRightMotor().setX(0);
		storage.robot.getBackRightMotor().setX(0);
	}
	
	public void moveForward()
	{
		fl = wheelSpeed;
		fr = -wheelSpeed;
		bl = -wheelSpeed;
		br = wheelSpeed;
		
		invertMotors();
		
		storage.robot.getFrontLeftMotor().setX(fl);
		storage.robot.getBackLeftMotor().setX(bl);
		storage.robot.getFrontRightMotor().setX(fr);
		storage.robot.getBackRightMotor().setX(br);
	}
	
	public void moveBackward()
	{
		fl = -wheelSpeed;
		fr = wheelSpeed;
		bl = wheelSpeed;
		br = -wheelSpeed;
		
		invertMotors();
		
		storage.robot.getFrontLeftMotor().setX(fl);
		storage.robot.getBackLeftMotor().setX(bl);
		storage.robot.getFrontRightMotor().setX(fr);
		storage.robot.getBackRightMotor().setX(br);
	}
	
	public void moveLeft()
	{
		fl = -wheelSpeed;
		fr = -wheelSpeed;
		bl = -wheelSpeed;
		br = -wheelSpeed;
		
		invertMotors();
		
		storage.robot.getFrontLeftMotor().setX(fl);
		storage.robot.getBackLeftMotor().setX(bl);
		storage.robot.getFrontRightMotor().setX(fr);
		storage.robot.getBackRightMotor().setX(br);
	}
	
	public void slowMoveLeft()
	{
		fl = -slowWheelSpeed;
		fr = -slowWheelSpeed;
		bl = -slowWheelSpeed;
		br = -slowWheelSpeed;
		
		invertMotors();
		
		storage.robot.getFrontLeftMotor().setX(fl);
		storage.robot.getBackLeftMotor().setX(bl);
		storage.robot.getFrontRightMotor().setX(fr);
		storage.robot.getBackRightMotor().setX(br);
	}
	
	public void slowMoveRight()
	{
		fl = slowWheelSpeed;
		fr = slowWheelSpeed;
		bl = slowWheelSpeed;
		br = slowWheelSpeed;
		
		invertMotors();
		
		storage.robot.getFrontLeftMotor().setX(fl);
		storage.robot.getBackLeftMotor().setX(bl);
		storage.robot.getFrontRightMotor().setX(fr);
		storage.robot.getBackRightMotor().setX(br);
	}
	
	public void moveRight()
	{
		fl = wheelSpeed;
		fr = wheelSpeed;
		bl = wheelSpeed;
		br = wheelSpeed;
		
		invertMotors();
		
		storage.robot.getFrontLeftMotor().setX(fl);
		storage.robot.getBackLeftMotor().setX(bl);
		storage.robot.getFrontRightMotor().setX(fr);
		storage.robot.getBackRightMotor().setX(br);
	}
	
	public void rotateOnlyRightWheels(boolean direction)
	{
		if(!direction)
		{
			fr = -wheelSpeed;
			br = wheelSpeed;
			fl = 0;
			bl = 0;
		
			invertMotors();
		
			storage.robot.getFrontLeftMotor().setX(fl);
			storage.robot.getBackLeftMotor().setX(bl);
			storage.robot.getFrontRightMotor().setX(fr);
			storage.robot.getBackRightMotor().setX(br);
		}
		
		if(direction)
		{
			fr = wheelSpeed;
			br = -wheelSpeed;
			fl = 0;
			bl = 0;
		
			invertMotors();
		
			storage.robot.getFrontLeftMotor().setX(fl);
			storage.robot.getBackLeftMotor().setX(bl);
			storage.robot.getFrontRightMotor().setX(fr);
			storage.robot.getBackRightMotor().setX(br);	
		}
	}
	
	
	public void rotateOnlyRightWheelsLeaveOthers(boolean direction)
	{
		if(!direction)
		{
			fr = -wheelSpeed;
			br = wheelSpeed;
		
			invertMotors();
		
			storage.robot.getFrontRightMotor().setX(fr);
			storage.robot.getBackRightMotor().setX(br);
		}
		
		if(direction)
		{
			fr = wheelSpeed;
			br = -wheelSpeed;
		
			invertMotors();
		
			storage.robot.getFrontRightMotor().setX(fr);
			storage.robot.getBackRightMotor().setX(br);	
		}
	}
	
	public void rotateOnlyLeftWheels(boolean direction)
	{
		if(!direction)
		{
			fr = 0;
			br = 0;
			fl = wheelSpeed;
			bl = -wheelSpeed;
		
			invertMotors();
		
			storage.robot.getFrontLeftMotor().setX(fl);
			storage.robot.getBackLeftMotor().setX(bl);
			storage.robot.getFrontRightMotor().setX(fr);
			storage.robot.getBackRightMotor().setX(br);
		}
		
		if(direction)
		{
			fr = 0;
			br = 0;
			fl = -wheelSpeed;
			bl = wheelSpeed;
		
			invertMotors();
		
			storage.robot.getFrontLeftMotor().setX(fl);
			storage.robot.getBackLeftMotor().setX(bl);
			storage.robot.getFrontRightMotor().setX(fr);
			storage.robot.getBackRightMotor().setX(br);	
		}
	}
	
	public void rotateOnlyLeftWheelsLeaveOthers(boolean direction)
	{
		if(!direction)
		{
			fl = wheelSpeed;
			bl = -wheelSpeed;
		
			invertMotors();
		
			storage.robot.getFrontLeftMotor().setX(fl);
			storage.robot.getBackLeftMotor().setX(bl);
		}
		
		if(direction)
		{
			fl = -wheelSpeed;
			bl = wheelSpeed;
		
			invertMotors();
		
			storage.robot.getFrontLeftMotor().setX(fl);
			storage.robot.getBackLeftMotor().setX(bl);
		}
	}
	
	
	public void resetTimer()
	{
		startTime = 0; 
	}
	
	public void barrelMotorsAuto(boolean goUp)
	{
		if (!goUp)
		{
			storage.robot.getBarrelMotorLeft().setX(BTConstants.BARREL_MOTOR_POWER_UP_AUTO);
			storage.robot.getBarrelMotorRight().setX(BTConstants.BARREL_MOTOR_POWER_UP_AUTO);
		}
		else
		{
			storage.robot.getBarrelMotorLeft().setX(-BTConstants.BARREL_MOTOR_POWER_DOWN_AUTO);
			storage.robot.getBarrelMotorRight().setX(-BTConstants.BARREL_MOTOR_POWER_DOWN_AUTO);
		}
	}
}
