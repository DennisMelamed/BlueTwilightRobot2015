package org.usfirst.frc.team2220.robot;
import java.io.FileWriter;
import java.io.IOException;


public class BTMacroRecord {
	FileWriter writer;
	long startTime;
	
	public BTMacroRecord() throws IOException
	{
			startTime = System.currentTimeMillis();
			
			writer = new FileWriter("/home/lvuser/recordedAuto.csv");
			
//			writer.append("Time");
//			
//			writer.append(",FrontLeftDrive");
//			writer.append(",FrontRightDrive");
//			writer.append(",BackRightDrive");
//			writer.append(",BackLeftDrive");
//			
//			writer.append(",BarrelMotorLeft");
//			writer.append(",BarrelMotorRight");
//			
//			writer.append(",LeftForkLeft");
//			writer.append(",LeftForkRight");
//			writer.append(",RightForkLeft");
//			writer.append(",RightForkRight\n");
	}
	

	public void record(BTStorage storage) throws IOException
	{
		//time
		writer.append("" + (System.currentTimeMillis()-startTime));
		//drive motors
		writer.append("," + storage.robot.getFrontLeftMotor().get());
		writer.append("," + storage.robot.getFrontRightMotor().get());
		writer.append("," + storage.robot.getBackRightMotor().get());		
		writer.append("," + storage.robot.getBackLeftMotor().get());
		//barrel motors
		writer.append("," + storage.robot.getBarrelMotorLeft().get());
		writer.append("," + storage.robot.getBarrelMotorRight().get());
		//fork motors
		writer.append("," + storage.robot.getLeftForkLeft().get());
		writer.append("," + storage.robot.getLeftForkRight().get());
		writer.append("," + storage.robot.getRightForkLeft().get());
		writer.append("," + storage.robot.getRightForkRight().get() + ",");
	}
	
	public void end() throws IOException
	{
		writer.flush();
		writer.close();
	}
}
