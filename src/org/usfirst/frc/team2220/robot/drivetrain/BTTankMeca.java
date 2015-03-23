package org.usfirst.frc.team2220.robot.drivetrain;

import org.usfirst.frc.team2220.robot.BTConstants;
import org.usfirst.frc.team2220.robot.BTStorage;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BTTankMeca implements BTIDrivetrain 
{
public BTStorage storage;
	
	public BTTankMeca(BTStorage storage)
	{
		this.storage = storage;
	}
		
	double left_joystick_front_back  = 0;
	double left_joystick_left_right  = 0;
	double right_joystick_front_back = 0;
	double right_joystick_left_right = 0;
	
	double front_left_motor  = 0;
	double front_right_motor = 0;
	double back_left_motor   = 0;
	double back_right_motor  = 0;
	
//	int counter = 0;
	
	@Override
	public void drive() 
	{
		
		
		left_joystick_front_back  = storage.controller.getLeftJoystickFrontBack().getValue();
		left_joystick_left_right  = storage.controller.getLeftJoystickLeftRight().getValue();
		right_joystick_front_back = storage.controller.getRightJoystickFrontBack().getValue();
		right_joystick_left_right = storage.controller.getRightJoystickLeftRight().getValue();
		
//		if (counter >= 250)
//		{
//			SmartDashboard.putNumber("Left joystick", left_joystick_front_back);
//			counter = 0;
//		}
//		
		if (Math.abs(left_joystick_front_back) > BTConstants.DEADZONE_MAX_RANGE)
		{
			front_left_motor = -BTConstants.MECANUM_SCALE_VALUE * left_joystick_front_back;
			back_left_motor =  -BTConstants.MECANUM_SCALE_VALUE * left_joystick_front_back;
		}
		else if (Math.abs(left_joystick_left_right) > BTConstants.DEADZONE_MAX_RANGE)
		{
			front_left_motor = BTConstants.MECANUM_SCALE_VALUE * left_joystick_left_right;
			back_left_motor = -BTConstants.MECANUM_SCALE_VALUE * left_joystick_left_right;
		}
		else
		{
			front_left_motor = 0;
			back_left_motor = 0;
		}	
		
		if (Math.abs(right_joystick_front_back) > BTConstants.DEADZONE_MAX_RANGE)
		{
			front_right_motor = BTConstants.MECANUM_SCALE_VALUE * right_joystick_front_back;
			back_right_motor =  BTConstants.MECANUM_SCALE_VALUE * right_joystick_front_back;
		}
		else if (Math.abs(right_joystick_left_right) > BTConstants.DEADZONE_MAX_RANGE)
		{
			front_right_motor = BTConstants.MECANUM_SCALE_VALUE * right_joystick_left_right;
			back_right_motor = -BTConstants.MECANUM_SCALE_VALUE * right_joystick_left_right;
		}
		else 
		{
			front_right_motor = 0;
			back_right_motor  = 0;
		}
		
		front_left_motor 	= front_left_motor	* BTConstants.FRONT_LEFT_SCALE;
		front_right_motor 	= front_right_motor	* BTConstants.FRONT_RIGHT_SCALE;
		back_left_motor 	= back_left_motor 	* BTConstants.BACK_LEFT_SCALE;
		back_right_motor	= back_right_motor 	* BTConstants.BACK_RIGHT_SCALE;
		
		storage.robot.getFrontLeftMotor().setX(front_left_motor);
		storage.robot.getBackLeftMotor().setX(back_left_motor);
		storage.robot.getFrontRightMotor().setX(front_right_motor);
		storage.robot.getBackRightMotor().setX(back_right_motor);
		
//		counter++;
	}

}
