package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Hardware {
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backRight;
    public DcMotor backLeft;
    public DcMotor slidesPushMotor;
    public DcMotor slidesPivotMotor;
    public CRServo intakeServo;  // CRServo = continuous servo
    private IMU gyro;
    private final OpMode opMode;
    private static Hardware myInstance;

    private Hardware(OpMode opMode) {
        this.opMode = opMode;
    }

    public static Hardware getInstance(OpMode opMode){
        if(myInstance == null){
            myInstance = new Hardware(opMode);
        }
        return myInstance;
    }

    public void init(HardwareMap hardwareMap) {
        frontLeft = hardwareMap.dcMotor.get("leftFront");
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//            frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//            frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        opMode.telemetry.addData("FrontLeftMotor: ", "Initialized");

        frontRight = hardwareMap.dcMotor.get("rightFront");
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//            frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        opMode.telemetry.addData("FrontRightMotor: ", "Initialized.");

        backRight = hardwareMap.dcMotor.get("rightRear");
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        opMode.telemetry.addData("BackRightMotor: ", "Initialized.");

        backLeft = hardwareMap.dcMotor.get("leftRear");
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        opMode.telemetry.addData("BackLeftMotor: ", "Initialized.");

        slidesPushMotor = hardwareMap.dcMotor.get("slides");
        slidesPushMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slidesPushMotor.setTargetPosition(0);
        slidesPushMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        slidesMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        slidesPivotMotor = hardwareMap.dcMotor.get("pivot");
        slidesPivotMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slidesPivotMotor.setTargetPosition(0);
        slidesPivotMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        slidesPivotMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeServo = hardwareMap.crservo.get("intake");
        intakeServo.setPower(0);

        try {
            gyro = hardwareMap.get(IMU.class, "imu");
            // if vertically positioned
            gyro.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                    RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
                    RevHubOrientationOnRobot.UsbFacingDirection.RIGHT)));
        } catch(Exception e) {
            opMode.telemetry.addData("Gyro ", "Error init");
            opMode.telemetry.update();
        }
    }

    /**
     * @return angle - left is ___, right is ___
     */
    public double getGyroAngle() {
        return gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    public void setMotorsToPower(double power) {
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }

    public void setTargets(int targetPos) {
        frontLeft.setTargetPosition(targetPos);
        frontRight.setTargetPosition(targetPos);
        backLeft.setTargetPosition(targetPos);
        backRight.setTargetPosition(targetPos);
    }

    public boolean notInRange(int targetPos) {
        return (notInRange(frontLeft, targetPos, 10) && notInRange(frontRight, targetPos, 10)
                && notInRange(backLeft, targetPos, 10) && notInRange(backRight, targetPos, 10));
    }

    public static boolean notInRange(DcMotor motor, int targetPos, int threshold) {
        return !(((Math.abs(motor.getCurrentPosition()) - threshold) <= Math.abs(targetPos)) &&
                ((Math.abs(motor.getCurrentPosition() + threshold)) >= Math.abs(targetPos)));
    }

    public void telemetryHardware() {
        opMode.telemetry.addData("Motor Powers", "");
        opMode.telemetry.addData("FrontLeftPower: ", frontLeft.getPower());
        opMode.telemetry.addData("FrontRightPower: ", frontRight.getPower());
        opMode.telemetry.addData("backRightPower: ", backRight.getPower());
        opMode.telemetry.addData("backLeftPower: ", backLeft.getPower());
        opMode.telemetry.addData("", "");
        opMode.telemetry.addData("Slides Motor: ", slidesPushMotor.getPower());
        opMode.telemetry.addData("Pivot Motor: ", slidesPivotMotor.getPower());
        opMode.telemetry.addData("Intake Servo: ", intakeServo.getPower());
        opMode.telemetry.addData("IMU Angle: ", getGyroAngle());


        opMode.telemetry.update();
    }

    public void setMotorsToRunToPosition() {
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
}
