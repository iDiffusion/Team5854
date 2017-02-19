package org.usfirst.frc.team5854.robot;

import org.usfirst.frc.team5854.Utils.EightDrive;
import org.usfirst.frc.team5854.Utils.SpecialFunctions;


//AutoMethods
import static org.usfirst.frc.team5854.robot.AutoMethods.move;
import static org.usfirst.frc.team5854.robot.AutoMethods.moveWithMap;
import static org.usfirst.frc.team5854.robot.AutoMethods.turnGyro;
import static org.usfirst.frc.team5854.robot.AutoMethods.visionTurn;


import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	boolean CalebsController = true;
	public static EightDrive mecanumDrive;
	Encoder backLeftEnc;
	VictorSP shootermotor;
	VictorSP agitatormotor;
	Servo leftgearservo;
	Servo rightgearservo;
	Joystick mainJoystick;
	Joystick buttonJoystick;
	static ADXRS450_Gyro gyro;
	VictorSP climbermotor;
	VictorSP harvestermotor;
	final String driveForward = "driveforward";
	final String spin = "spin";
	final String visionTrackingTest = "Vision Test";
	final String objective1 = "Objective1";
	final String objective2 = "Objective2";
	final String objective3 = "Objective3";
	final String objective4 = "Objective4";
	final String objective45 = "Objective45";
	final String objective6 = "Objective6";
	final String objective67 = "Objective67";
	String autoSelected;
	SendableChooser<String> chooser;
	final String calebsController = "caleb";
	final String mainjoy = "mainjoy";
	String stickSelected;
	SendableChooser<String> controllerChooser;
	CameraStreamer cameraserver;
	CameraStreamer cameraserver1;
	CameraStreamer cameraserver2;

	public void robotInit() {
		mecanumDrive = new EightDrive(2, 3, 1, 4, 7, 5, 8, 6);
		backLeftEnc = new Encoder(0, 1, false, CounterBase.EncodingType.k4X);
		leftgearservo = new Servo(2);
		rightgearservo = new Servo(1);

		shootermotor = new VictorSP(3);
		agitatormotor = new VictorSP(4);

		climbermotor = new VictorSP(5);

		mainJoystick = new Joystick(1);
		buttonJoystick = new Joystick(0);

		gyro = new ADXRS450_Gyro();

		harvestermotor = new VictorSP(0);

		chooser = new SendableChooser<String>();
		chooser.addDefault("Drive forward", "driveforward");
		chooser.addObject("spin", "spin");
		chooser.addObject("Vision Test", "Vision Test");
		chooser.addObject("Objective #1", "Objective1");
		chooser.addObject("Objective #2", "Objective2");
		chooser.addDefault("Objective #3", "Objective3");
		chooser.addDefault("Objective #4", "Objective4");
		chooser.addDefault("Objective #4 and #5", "Objective45");
		chooser.addDefault("Objective #6", "Objective6");
		chooser.addDefault("Objective #6 and #7", "Objective67");
		SmartDashboard.putData("Auto choices", chooser);
		controllerChooser = new SendableChooser<String>();
		controllerChooser.addDefault("Calebs Controller", "caleb");
		controllerChooser.addObject("main Joystick", "mainjoy");
		SmartDashboard.putData("Choose Controller", controllerChooser);

		cameraserver = new CameraStreamer(0, 1181);
		cameraserver.setResolution(640, 400);
		cameraserver.setBrightness(1);
	}

	double gyroOffset = 0.0D;

	public void autonomousInit() {
		autoSelected = ((String) chooser.getSelected());
		mecanumDrive.setCANTalonDriveMode(CANTalon.TalonControlMode.PercentVbus);
		gyro.reset();
		go = true;
	}

	boolean go = true;

	public void autonomousPeriodic() {
		if (buttonJoystick.getRawButton(7)) {
			go = true;
			gyro.reset();
		}
		if (go) {
			switch (autoSelected) {
			case "Vision Test":
				visionTurn();
				go = false;
				break;
			case "spin":
				turnGyro('R', 90.0);
				go = false;
				break;
			case "driveforward":
				move(30.0, 0.2);
				go = false;
				break;
			case "Objective1":
				move(94.75, 0.5);
				gearManager(true);
				break;
			case "Objective2":
				move(68.234, 0.5);
				turnGyro('R', 30.0);
				move(66.217, 0.5);
				gearManager(true);
				break;
			case "Objective3":
				move(68.234, 0.5);
				turnGyro('L', 30.0);
				move(66.217, 0.5);
				gearManager(true);
				break;
			case "Objective4":
				move(13.0, -0.5);
				turnGyro('R', 23.0);
				for (int i = 0; i < 3000; i++) {
					shooterManager(true, false);
				}
				for (int i = 0; i < 7000; i++) {
					shooterManager(true, true);
				}
				break;
			case "Objective45":
				turnGyro('R', 148.0);
				move(100.0, 0.5);
				gearManager(true);
				move(8.0, -0.5);
				break;
			case "Objective6":
				move(13.0, -0.5);
				turnGyro('L', 23.0);
				for (int i = 0; i < 3000; i++) {
					shooterManager(true, false);
				}
				for (int i = 0; i < 7000; i++) {
					shooterManager(true, true);
				}
				break;
			}
		}
	}

	public int secToTicks(double secs) {
		return (int) (secs * 500.0);
	}

	double gyroAngle = 0.0;

	public void teleopPeriodic() {
		stickSelected = ((String) controllerChooser.getSelected());
		switch (stickSelected) {
		case "caleb":
			CalebsController = true;
			break;
		case "mainjoy":
			CalebsController = false;
		}
		mecanumDrive.setCANTalonDriveMode(CANTalon.TalonControlMode.PercentVbus);

		mecanumDrive.setDeadband(0.1);

		mecanumDrive.setTwistMultiplyer(0.3);

		mecanumDrive.setSpeedMultiplyer(.5);
		if (CalebsController) {
			mecanumDrive.mecanumDrive_Cartesian(mainJoystick.getRawAxis(0), mainJoystick.getRawAxis(1), mainJoystick.getRawAxis(2), 0);
		} else {
			mecanumDrive.mecanumDrive_Cartesian(buttonJoystick.getX(), buttonJoystick.getY(), buttonJoystick.getTwist(), 0.0);
		}
		gearManager(buttonJoystick.getRawButton(3));

		// climberManager(buttonJoystick.getRawButton(11));

		shooterManager(buttonJoystick.getRawButton(2), buttonJoystick.getRawButton(1));

		harvesterManager(buttonJoystick.getRawButton(7), buttonJoystick.getRawButton(8));

		gyroAngle = gyro.getAngle();
	}

	public void gearManager(boolean go) {
		if (go == true) {
			leftgearservo.setAngle(0.0);
			rightgearservo.setAngle(90.0);
		} else {
			leftgearservo.setAngle(90.0);
			rightgearservo.setAngle(0.0);
		}
	}

	public void harvesterManager(boolean go, boolean reverse) {
		if (go) {
			harvestermotor.setSpeed(-1.0);
		} else if (reverse) {
			harvestermotor.setSpeed(1.0);
		} else {
			harvestermotor.setSpeed(0.0);
		}
	}

	public void climberManager(boolean go) {
		if (go) {
			climbermotor.setSpeed(0.7);
		} else {
			climbermotor.setSpeed(0.0);
		}
	}

	public void shooterManager(boolean go, boolean second) {
		if (go) {
			shootermotor.setSpeed(-1.0);
			if (second) {
				agitatormotor.setSpeed(SpecialFunctions.map(buttonJoystick.getThrottle(), -1, 1, 1, .25));
			} else {
				agitatormotor.setSpeed(0.0);
			}
		} else {
			shootermotor.setSpeed(0.0);
			agitatormotor.setSpeed(0.0);
		}
	}


	double r = 0.0;
	double prevR = 0.0;
	double l = 0.0;
	double prevL = 0.0;
	boolean reset = true;
	boolean once = true;
	int i = 0;

	public void testPeriodic() {
		if (buttonJoystick.getRawButton(4)) {
			once = true;
		}
		if (once) {
			moveWithMap(50);
			once = false;
		}
	}
}
