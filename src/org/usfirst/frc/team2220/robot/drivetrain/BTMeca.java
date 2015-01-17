package org.usfirst.frc.team2220.robot.drivetrain;

import org.usfirst.frc.team2220.robot.BTConstants;
import org.usfirst.frc.team2220.robot.BTStorage;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BTMeca implements BTIDrivetrain
{
	
	public BTStorage storage;
	
	public BTMeca(BTStorage storage)
	{
		this.storage = storage;
	}
		
	double strafe = 0.0;
	double forward = 0.0;
	double rotate = 0.0;
	
	/**
	 * Applies the deadzone to the input axis
	 */
	public void setDeadzone()
	{
		if(strafe < BTConstants.MECANUM_DEADZONE && strafe > -BTConstants.MECANUM_DEADZONE)
		{
			strafe = 0;
		}
		if(forward < BTConstants.MECANUM_DEADZONE && forward > -BTConstants.MECANUM_DEADZONE)
		{
			forward = 0;
		}
		if(rotate < BTConstants.MECANUM_DEADZONE && rotate > -BTConstants.MECANUM_DEADZONE)
		{
			rotate = 0;
		}
	}

	@Override
	public void drive()
	{
		// Strafe is the left/right dimension of the joystick. Moves the robot left or right without rotating.
		strafe = storage.controller.getDriveLeftRight().getValue();
		// Forward is the forward/back dimension of the joystick. Moves the robot forward and backward.
		forward = storage.controller.getDriveFrontBack().getValue();
		// Rotate is how much the robot should turn.
		rotate = storage.controller.getDriveRotate().getValue();
		
		setDeadzone();
		
		double fr = strafe - forward + rotate;
		double br = strafe + forward + rotate;
		double fl = strafe + forward - rotate;
		double bl = strafe - forward - rotate;
		
		if (rotate > 0.9) rotate = 0.9;
		if (rotate < -0.9) rotate = -0.9;

		if (strafe > 0.9) strafe = 0.9;
		if (strafe < -0.9) strafe = -0.9;

		if (forward > 0.9) forward = 0.9;
		if (forward < -0.9) forward = -0.9;
		// Reverse front and back right motors
		fr = -fr;
		br = -br;
		
		// Get the maximum motor power, before scaling. If it's over 1, that will break the code.
		// We need to scale it down then, so if one motor is 2.0 and the rest are 1.0, the 2.0 will
		// be scaled to 1.0, the rest to 0.5.
		double max = Math.max(Math.abs(fr), Math.max(Math.abs(br), Math.max(Math.abs(fl), Math.abs(bl))));
		
		// Don't upscale so we can drive slowly
		if(max < 1)
		{
			max = 1;
		}

		// Scale the motor powers down so they are all <= 1.0
		fr = fr / max;
		br = br / max;
		fl = fl / max;
		bl = bl / max;
		
		// Scale to the mecanum value, ie if we want to run at half power
		fr = fr * BTConstants.MECANUM_SCALE_VALUE;
		br = br * BTConstants.MECANUM_SCALE_VALUE;
		fl = fl * BTConstants.MECANUM_SCALE_VALUE;
		bl = bl * BTConstants.MECANUM_SCALE_VALUE;
		
		// Now set the motor powers!
		//System.out.println(storage.data.FRONT_RIGHT_MOTOR == null);
		SmartDashboard.putNumber("Y Axis Input", forward);
		SmartDashboard.putNumber("X Axis Input", strafe);
		SmartDashboard.putNumber("Z Axis Input", rotate);
		
		if (fr > 0.9) fr = 0.9;
		if (fr < -0.9) fr = -0.9;

		if (br > 0.9) br = 0.9;
		if (br < -0.9) br = -0.9;

		if (fl > 0.9) fl = 0.9;
		if (fl < -0.9) fl = -0.9;
		
		if (bl > 0.9) bl = 0.9;
		if (bl < -0.9) bl = -0.9;
		
		storage.data.FRONT_RIGHT_MOTOR.setX(fr);
		storage.data.BACK_RIGHT_MOTOR.setX(br);
		storage.data.FRONT_LEFT_MOTOR.setX(fl);
		storage.data.BACK_LEFT_MOTOR.setX(bl);
		
		// test code for something
//		storage.data.FRONT_RIGHT_MOTOR.setX(.2);
//		storage.data.BACK_RIGHT_MOTOR.setX(.2);
//		storage.data.FRONT_LEFT_MOTOR.setX(.2);
//		storage.data.BACK_LEFT_MOTOR.setX(.2);
//		
	}

}
